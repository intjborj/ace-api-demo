package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.hisd3.hismk2.domain.doh.SummaryOfPatient
import com.hisd3.hismk2.domain.doh.DOH_REPORT_TYPE
import com.hisd3.hismk2.graphqlservices.doh.dto.OpdConsultationDto
import com.hisd3.hismk2.domain.doh.DohLogs
import com.hisd3.hismk2.graphqlservices.doh.dto.SummaryPatientV2Dto
import com.hisd3.hismk2.graphqlservices.doh.dto.SummaryPatientDto
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.doh.SummaryOfPatientRepository
import com.hisd3.hismk2.utils.SOAPConnector
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptSummaryOfPatients
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptSummaryOfPatientsResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.SubmittedReportsResponse
import com.hisd3.hismk2.graphqlservices.doh.dto.SummaryOfPatientDTO

import javax.persistence.criteria.CriteriaBuilder.In
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@Component
@GraphQLApi
class SummaryOfPatientServices {

    @Autowired
    SOAPConnector soapConnector

    @Autowired
    SummaryOfPatientRepository summaryOfPatientRepository

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    HospitalConfigService hospitalConfigService

    @Autowired
    BedCapacityServices bedCapacityServices

    @Autowired
    DohAPIService dohAPIService

    @Autowired
    DohLogsServices dohLogsServices


    @Autowired
    private JdbcTemplate jdbcTemplate

    @GraphQLQuery(name = "findAllSummaryOfPatient", description = "Find all Summary Of Patient ")
    List<SummaryOfPatient> findAllSummaryOfPatient() {
        return summaryOfPatientRepository.findAllSummaryOfPatient()
    }

    //==================================Mutation ============
    @GraphQLMutation
    def postSummaryOfPatient(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    ) {
        if (id) {
            def summaryOfPatient = summaryOfPatientRepository.findById(id).get()
            objectMapper.updateValue(summaryOfPatient, fields)
            summaryOfPatient.submittedDateTime = Instant.now()

            return summaryOfPatientRepository.save(summaryOfPatient)
        } else {

            def summaryOfPatient = objectMapper.convertValue(fields, SummaryOfPatient)

            return summaryOfPatientRepository.save(summaryOfPatient)
        }
    }
    @GraphQLMutation(name = "sendSummaryPatient")
    GraphQLRetVal<String> sendSummaryPatient(@GraphQLArgument(name = 'fields') Map<String, Object> fields) {

        try {
            HospOptSummaryOfPatients request = new HospOptSummaryOfPatients()

            request.hfhudcode = hospitalConfigService.hospitalInfo.hfhudcode?:""
            request.totalinpatients = fields.get('totalInpatients') as Integer
            request.totalnewborn = fields.get('totalNewborn') as Integer
            request.totaldischarges = fields.get('totalDischarges') as Integer
            request.totalpad = fields.get('totalPad') as Integer
            request.totalibd = fields.get("totalIbd") as Integer
            request.totalinpatienttransto = fields.get('totalInpatientTransto') as Integer
            request.totalinpatienttransfrom = fields.get('totalInpatientTransFrom') as Integer
            request.totalpatientsremaining = fields.get('totalPatientRemaining') as Integer
            request.reportingyear = fields.get('reportingYear') as Integer

            HospOptSummaryOfPatientsResponse response =
                    (HospOptSummaryOfPatientsResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospOptSummaryOfPatients", request)

            UUID id = UUID.fromString(fields.get('id') as String)
            SummaryOfPatient dto = summaryOfPatientRepository.getOne(id)
            dto.dohResponse = response.return

            summaryOfPatientRepository.save(dto)

            return new GraphQLRetVal<String>(response.return, true)

        } catch (Exception e) {
            return new GraphQLRetVal<String>(e.message, false)
        }
    }

    @GraphQLQuery(name="getTotalAdmission",description = "All cases that has service type = inpatient from (January 1, 12:01 am to December 31, 11:59:59 midnight/ for the whole reporting year)")
    Long getTotalAdmission(@GraphQLArgument(name="year") Integer year){
        return jdbcTemplate.queryForObject("""
                    select count(c.id)
                    from pms.cases c 
          --          ,
		--				 LATERAL json_array_elements(
          --      CASE 
          --          WHEN pms.is_json(c.doh_icd_diagnosis ::character varying) AND c.doh_icd_diagnosis IS NOT NULL THEN c.doh_icd_diagnosis::json
          --      ELSE NULL::json
         --           END) d(value)
                    where 
                    c.registry_type = 'IPD'
                    and
                    c.service_type is not null
                    and
                    c.admission_datetime is not null
                    and date_part('year'::text, c.admission_datetime + interval '8 hour') = ?::DOUBLE PRECISION""",
                    Long,
                    year)
    }

    @GraphQLQuery(name="getTotalNewborn" , description = "Total Newborn (In facility Deliveries) - not in the Annual Statistical Report but asked in the DOH Web Service.")
    Long getTotalNewborn(@GraphQLArgument(name="year") Integer year){
        return jdbcTemplate.queryForObject("""
                                        select 
              count(c.id)
                    
                    from pms.cases c 
                    where 
                    upper(c.service_type) in ('PATHOLOGIC','NON-PATHOLOGIC / WELL-BABY')
                    and c.registry_type = 'IPD'
                    and discharge_disposition in ('DAMA/HAMA','TRANSFERRED','ABSCONDED','DISCHARGED')
                    and date_part('year'::text, c.admission_datetime + interval '8 hour') =  ? ::DOUBLE PRECISION
                    ;
                    """,
                    Long,
                    year)
    }

    @GraphQLQuery(name="getTotalDischargedAlive", description = "Total Discharges (Alive) Include HAMA and Absconded")
    Long getTotalDischargedAlive(
            @GraphQLArgument(name= "year" ) Integer year,
            @GraphQLArgument(name = "registryType") String registryType = 'IPD'
    ){
        return jdbcTemplate.queryForObject("""
                 select 
                    count(c.id)
                    from pms.cases c
          --           ,
         --       LATERAL json_array_elements(
          --      CASE 
         --           WHEN pms.is_json(c.doh_icd_diagnosis ::character varying) AND c.doh_icd_diagnosis IS NOT NULL THEN c.doh_icd_diagnosis::json
        --        ELSE NULL::json
          --          END) d(value)
                    where 
                    c.discharge_disposition is not null
                   and (c.discharge_condition != 'EXPIRED' )
                    and date_part('year'::text, c.discharged_datetime + interval '8 hour') =  ? ::DOUBLE PRECISION 
                    and c.registry_type = 'IPD'
                    and may_go_home_datetime is not NULL
                    ;
                    """,
                    Long,
                    year
                    )
    }

    @GraphQLQuery(name="getTotalInPatientTransferToThisFacility",description = "Total number of inpatients transferred TO THIS FACILITY from another facility for inpatient care)")
    Long getTotalInPatientTransferToThisFacility(@GraphQLArgument(name="year") Integer year){
        return jdbcTemplate.queryForObject("""
                    select
                    count(c.id)
                    from
                    pms.cases c
                    where
                    c.registry_type = 'IPD'
                    and c.admission_datetime  is not null
                    and date_part('year'::text, c.admission_datetime + interval '8 hour') = ?::DOUBLE PRECISION
                    and c.previous_admission = 'OTHER/TRANSFEREE' """,
                    Long,
                    year
        )
    }


    @GraphQLQuery(name="getTotalInPatientTransferFromThisFacility", description = "Total number of inpatients transferred FROM THIS FACILITY to another facility for inpatients care.")
    Long getTotalInPatientTransferFromThisFacility(
            @GraphQLArgument(name="year") Integer year,
            @GraphQLArgument(name="registryType") String registryType = 'IPD'
    ){
        return jdbcTemplate.queryForObject("""
                    select 
                    count(c.id)
                    from pms.cases c 
                    where 
                    c.discharge_disposition = 'TRANSFERRED'
                    and date_part('year'::text, c.admission_datetime + interval '8 hour') = ?::DOUBLE PRECISION 
                    and c.registry_type = ?
                    """,
                Long,
                year,
                registryType)
    }

    @GraphQLQuery(name = "getInpatientRemainingAtMidnightPrev", description = "Inpatient remaining at midnight (number of patient that are admitted as of dec 31 11:59:59 currently or  Admission Date time is before dec 31 11:59:59 of reporting year and discharge date/time is after dec 31 11:59:59)")
    Long getInpatientRemainingAtMidnightPrev(@GraphQLArgument(name = "year") Integer year){
        Integer prevYear = year - 1
        LocalDate december = LocalDate.of(prevYear,12,1)
        LocalDate localDate = LocalDate.of(prevYear,12,december.lengthOfMonth())
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
                prevYear.toString())
        return result
    }

    @GraphQLQuery(name = 'getSummaryPatientHospital', description = 'get inpatient more than 24 hrs')
    List<SummaryPatientDto> getSummaryPatientHospital(@GraphQLArgument(name="year") Integer year){
        List<SummaryPatientDto> summaryPatient = []
        summaryPatient.push(new SummaryPatientDto(1,'Total Inpatients',getTotalAdmission(year)))
        summaryPatient.push(new SummaryPatientDto(2,'Total Newborn (In facility deliveries)',getTotalNewborn(year)))
        summaryPatient.push(new SummaryPatientDto(3,'Total Discharges (Alive) Include HAMA and Absconded',getTotalDischargedAlive(year)))
        summaryPatient.push(new SummaryPatientDto(4,'Total patients admitted and discharged on the same day',bedCapacityServices.getNumberOfSameDayAdmissionAndDischarge(year)))
        summaryPatient.push(new SummaryPatientDto(5,'Total number of inpatient bed days (service days)',bedCapacityServices.getTotalInpatientServiceDays(year)))
        summaryPatient.push(new SummaryPatientDto(6,'Total number of inpatients transferred TO THIS FACILITY from another facility for inpatient care',getTotalInPatientTransferToThisFacility(year)))
        summaryPatient.push(new SummaryPatientDto(7,'Total number of inpatients transferred FROM THIS FACILITY to another facility for inpatient care',getTotalInPatientTransferFromThisFacility(year)))
        summaryPatient.push(new SummaryPatientDto(8,'Total number of patients remaining in the hospital as of midnight last day of previous year',getInpatientRemainingAtMidnightPrev(year)))

        return summaryPatient.sort{it.key}
    }


    @GraphQLMutation
    GraphQLRetVal<List<SummaryOfPatientDTO>>postDohSummaryPatient(@GraphQLArgument(name = "year") Integer year){

       List <SummaryOfPatientDTO> summaryPatientV2Dto = []

        String totalAdmission = getTotalAdmission(year)
        String totalNewBorn =   getTotalNewborn(year)
        String totalDischargeAlive = getTotalDischargedAlive(year)
        String totalAdmittedPatientDischargeSameDay = bedCapacityServices.getNumberOfSameDayAdmissionAndDischarge(year)
        String totalNumberInPatientBedDays =  bedCapacityServices.getTotalInpatientServiceDays(year)
        String totalNumInPatientTransTo =  getTotalInPatientTransferToThisFacility(year)
        String totalNumInPatientTransFrom =  getTotalInPatientTransferFromThisFacility(year)
        String totalPatientRemaining = getInpatientRemainingAtMidnightPrev(year)


        GraphQLRetVal <SummaryOfPatientDTO> retVal = dohAPIService.hospOptSummaryOfPatients(
                totalAdmission,
                totalNewBorn,
                totalDischargeAlive,
                totalAdmittedPatientDischargeSameDay,
                totalNumberInPatientBedDays,
                totalNumInPatientTransTo,
                totalNumInPatientTransFrom,
                totalPatientRemaining,
                year.toString()
        )

        summaryPatientV2Dto.push(retVal.payload)
            Map<String, Object> fields = [:]

            fields['Total Inpatients'] = totalAdmission
            fields['Total Newborn (In facility deliveries)'] = totalNewBorn
            fields['Total Discharges (Alive)'] = totalDischargeAlive
            fields['Total patients admitted and discharged on the same day'] = totalAdmittedPatientDischargeSameDay
            fields['Total number of inpatient bed days (service days)'] = totalNumberInPatientBedDays
            fields['Total number of inpatients transferred TO THIS FACILITY from another facility for inpatient care'] = totalNumInPatientTransTo
            fields['Total number of inpatients transferred FROM THIS FACILITY to another facility for inpatient care'] = totalNumInPatientTransFrom
            fields['Total number of patients remaining in the hospital as of midnight last day of previous year'] = totalPatientRemaining

            if(retVal){
                DohLogs dohLogs = new DohLogs()
                dohLogs.type = DOH_REPORT_TYPE.SUMMARY_PATIENT.name()
                dohLogs.submittedReport = new JSONObject(fields)
                dohLogs.reportResponse = new Gson().toJson(retVal.payload)
                dohLogs.reportingYear = year
                dohLogs.status = retVal.payload.response_desc
                dohLogsServices.save(dohLogs)
            }

        return new GraphQLRetVal<List<SummaryOfPatientDTO>>(summaryPatientV2Dto, true, '')
    }

}
