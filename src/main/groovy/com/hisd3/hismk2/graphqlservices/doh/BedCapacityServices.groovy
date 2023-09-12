package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.doh.BedCapacity
import com.hisd3.hismk2.domain.hospital_config.HospitalInfo
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.doh.BedCapacityRepository
import com.hisd3.hismk2.utils.SOAPConnector
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementCreator
import org.springframework.stereotype.Component
import ph.gov.doh.uhmistrn.ahsr.webservice.index.GenInfoBedCapacity
import ph.gov.doh.uhmistrn.ahsr.webservice.index.GenInfoBedCapacityResponse

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.chrono.Chronology
import java.time.format.DateTimeFormatter

@Canonical
class BedCapacitySubTable {
	Integer key
	String name
	Long value
}

@Canonical
class BedCapacityTable {
	Integer key
	String name
	Long value
	List<BedCapacitySubTable> bedCapacitySubTables
}

@Canonical
class BedCapacityOccupancy {
	BigDecimal bedOccupancyRate
	List<BedCapacityTable> bedCapacityTables
}

@Component
@GraphQLApi
class BedCapacityServices {

	@Autowired
	SOAPConnector soapConnector

	@Autowired
	BedCapacityRepository bedCapacityRepository

	@Autowired
	SummaryOfPatientServices summaryOfPatientServices

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	JdbcTemplate jdbcTemplate

	@Autowired
	HospitalConfigService hospitalConfigService


	@GraphQLQuery(name = "findAllBedCapacity", description = "Find all classification")
	List<BedCapacity> findAllBedCapacity() {
		return bedCapacityRepository.findAllBedCapacity()
	}

	//==================================Mutation ============
	@GraphQLMutation
	def postGeneralInfo(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields

	) {
		if (id) {
			def bedCapacity = bedCapacityRepository.findById(id).get()
			objectMapper.updateValue(bedCapacity, fields)
			bedCapacity.submittedDateTime = Instant.now()

			return bedCapacityRepository.save(bedCapacity)
		} else {
			def bedCapacity = objectMapper.convertValue(fields, BedCapacity)

			return bedCapacityRepository.save(bedCapacity)
		}
	}

	@GraphQLMutation(name = "sendBedCapacity")
	GraphQLRetVal<String>  sendBedCapacity(@GraphQLArgument(name = "fields") Map<String, Object> fields) {
		try {
			GenInfoBedCapacity request = new GenInfoBedCapacity()
			request.hfhudcode = hospitalConfigService.hospitalInfo.hfhudcode?:""
			request.abc = fields.get("abc") as Integer
			request.implementingbeds = fields.get("implementingBeds") as Integer
			request.bor = fields.get("bor") as Integer
			request.reportingyear = fields.get("reportingYear") as Integer

			GenInfoBedCapacityResponse response =
					(GenInfoBedCapacityResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/genInfoBedCapacity", request)
			return new GraphQLRetVal<String>(response.return, true)
		} catch (Exception e) {
			return new GraphQLRetVal<String>(e.message, false)
		}
	}

	@GraphQLQuery(name = "getInpatientRemainingAtMidnight", description = "Inpatient remaining at midnight (number of patient that are admitted as of dec 31 11:59:59 currently or  Admission Date time is before dec 31 11:59:59 of reporting year and discharge date/time is after dec 31 11:59:59)")
 	Long getInpatientRemainingAtMidnight(@GraphQLArgument(name = "year") Integer year){
		LocalDate december = LocalDate.of(year,12,1)
		LocalDate localDate = LocalDate.of(year,12,december.lengthOfMonth())
		LocalTime localTime = LocalTime.of(23,59,59)
		def localDateTime = LocalDateTime.of(localDate,localTime)
		String required = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(localDateTime)
		def result = jdbcTemplate.queryForObject("""
						select 
							count(c.id)
						from pms.cases c 
						where 
							c.registry_type  = 'IPD'
							and
							(
								to_char(c.admission_datetime + interval '8 hour','YYYY-MM-DD HH:MI:SS') <= ?
							and
								(
									to_char(c.discharged_datetime + interval '8 hour','YYYY-MM-DD HH:MI:SS') > ?
									or 
									c.discharged_datetime is null
								)
							)
							and date_part('year'::text, c.admission_datetime + interval '8 hour') = ?::DOUBLE PRECISION """,
						Long,
						required,
						required,
						year.toString())
		return result
	}

	@GraphQLQuery(name="getTotalInpatientAdmission" , description = "Total admission (status is IN-PATIENT or registry type is inpatient for the whole reporting year)")
	Long getTotalInpatientAdmission(@GraphQLArgument(name="year") Integer year){
		def result = jdbcTemplate.queryForObject("""
						select count(c.id) 
						from pms.cases c 
						where
						date_part('year'::text, c.admission_datetime + interval '8 hour') = ?::DOUBLE PRECISION
						and c.registry_type = 'IPD' """,
				Long,
				year)
		return result
	}


	@GraphQLQuery(name="getTotalDischarged" , description = "Total discharges (including death and admitted) =  outcome of treatment/disposition is discharged or expired for the whole reporting year")
	Long getTotalDischarged(@GraphQLArgument(name="year") Integer year){
		def result = jdbcTemplate.queryForObject("""
						select count(c.id)
						from pms.cases c 
						where
						date_part('year'::text, c.discharged_datetime + interval '8 hour') = ?::DOUBLE PRECISION
						and c.registry_type = 'IPD' 
						and (discharge_disposition in ('DAMA/HAMA','TRANSFERRED','ABSCONDED','DISCHARGED'))  
""",
				Long,
				year)
		return result
	}


	@GraphQLQuery(name="getNumberOfSameDayAdmissionAndDischarge" , description = "Number of admissions and discharges on the same day = admission date/time (information) and discharge date/time (discharge) is on the same day per case for the whole reporting year.")
	Long getNumberOfSameDayAdmissionAndDischarge(@GraphQLArgument(name="year") Integer year){
		def result = jdbcTemplate.queryForObject("""
						select count(c.id)
				from pms.cases c
			where
			date_part('year'::text, c.admission_datetime + interval '8 hour') = ?::DOUBLE PRECISION
			and
			to_char(c.admission_datetime + interval '8 hour','YYYY-MM-DD') = to_char(c.discharged_datetime + interval '8 hour','YYYY-MM-DD')
			and c.registry_type = 'IPD'; """,
				Long,
				year)
		return result
	}


	@GraphQLQuery(name="getAuthorizedBedCapacity" , description = "Authorized Bed Capacity (This will be from the settings/configuration)")
	Long getAuthorizedBedCapacity(){
		HospitalInfo hospitalInfo = hospitalConfigService.getHospitalInfo()
		if(hospitalInfo)
			return hospitalInfo.bedCapacity
		return 0
	}

	@GraphQLQuery(name="getImplementedBeds" , description = "Implementing Beds (This will be from the settings/configurations)")
	Long getImplementedBeds(){
		HospitalInfo hospitalInfo = hospitalConfigService.getHospitalInfo()
		if(hospitalInfo)
			return hospitalInfo.implementedBed
		return 0
	}

	@GraphQLQuery(name="getTotalDaysInPeriod" , description = "Total days in the period")
 	Integer getTotalDaysInPeriod(Integer year){
		LocalDate thisPeriod = LocalDate.of(year,1,1)
		return thisPeriod.lengthOfYear()
	}

	@GraphQLQuery(name="getTotalInpatientServiceDays" , description = "Total Inpatient service days")
	Long  getTotalInpatientServiceDays(@GraphQLArgument(name="year") Integer year){
		Long totalInpatientServiceDays = 0
		Long inpatientRemainingAtMidnight = summaryOfPatientServices.getInpatientRemainingAtMidnightPrev(year)
		Long totalAdmission = getTotalInpatientAdmission(year)
		Long totalDischarged = getTotalDischarged(year)
		Long theSameDayAdmissionAndDischarged = getNumberOfSameDayAdmissionAndDischarge(year)

		Long admissionAndRemainingAtMidnight = inpatientRemainingAtMidnight + totalAdmission

		if(admissionAndRemainingAtMidnight >= 0 && totalDischarged >= 0)
			totalInpatientServiceDays = (admissionAndRemainingAtMidnight - totalDischarged) + theSameDayAdmissionAndDischarged
		return  totalInpatientServiceDays
	}

	@GraphQLQuery(name="getBasedAuthorizedBeds")
	BigDecimal getBasedAuthorizedBeds(@GraphQLArgument(name="year") Integer year){
		BigDecimal basedAuthorizedBeds = 0.00
		BigDecimal divide = 0.00
		Long inpatientServiceDays = getTotalInpatientServiceDays(year)
		Long authorizedBeds = getAuthorizedBedCapacity()
		Long totalPeriod = getTotalDaysInPeriod(year)

		Long authorizedBedsAndDaysInPeriod = authorizedBeds * totalPeriod
		if(inpatientServiceDays > 0 && authorizedBedsAndDaysInPeriod > 0)
	 		divide = inpatientServiceDays / authorizedBedsAndDaysInPeriod

		if(divide > 0)
			basedAuthorizedBeds = divide * 100

		return basedAuthorizedBeds
	}

	@GraphQLQuery(name="getBedCapacityOccupancy")
	BedCapacityOccupancy getBedCapacityOccupancy(@GraphQLArgument(name="year") Integer year){
		BedCapacityOccupancy bedCapacityOccupancy = new BedCapacityOccupancy()
		bedCapacityOccupancy.bedOccupancyRate = getBasedAuthorizedBeds(year)
		bedCapacityOccupancy.bedCapacityTables = []
		List<BedCapacitySubTable> bedCapacitySubTables = []
		bedCapacitySubTables.push(new BedCapacitySubTable(1,'Inpatient Remain at midnight',summaryOfPatientServices.getInpatientRemainingAtMidnightPrev(year)))
		bedCapacitySubTables.push(new BedCapacitySubTable(2,'Total Admission',getTotalInpatientAdmission(year)))
		bedCapacitySubTables.push(new BedCapacitySubTable(3,'Total Discharged',getTotalDischarged(year)))
		bedCapacitySubTables.push(new BedCapacitySubTable(4,'The same day admission and discharged',getNumberOfSameDayAdmissionAndDischarge(year)))
		bedCapacitySubTables.sort{it.key}

		bedCapacityOccupancy.bedCapacityTables.push(new BedCapacityTable(1,'Total Inpatient Service days for the period',getTotalInpatientServiceDays(year),bedCapacitySubTables))
		bedCapacityOccupancy.bedCapacityTables.push(new BedCapacityTable(2,'Total number of Authorized beds',getAuthorizedBedCapacity()))
		bedCapacityOccupancy.bedCapacityTables.push(new BedCapacityTable(3,'Total days in the period',getTotalDaysInPeriod(year)))
		bedCapacityOccupancy.bedCapacityTables.sort{it.key}
		return bedCapacityOccupancy
	}



}