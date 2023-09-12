package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.doh.OperationHai
import com.hisd3.hismk2.domain.doh.OperationMajorOpt
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.doh.OperationHaiRepository
import com.hisd3.hismk2.utils.SOAPConnector
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.xmlsoap.schemas.soap.encoding.Decimal
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsDeaths
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsDeathsResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsHAI
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsHAIResponse

import java.time.Instant

@Component
@GraphQLApi
class OperationHaiServices {

	@Autowired
	OperationHaiRepository operationHaiRepository

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	SOAPConnector soapConnector

	@Autowired
	HospitalConfigService hospitalConfigService

	@Autowired
	JdbcTemplate jdbcTemplate

	@GraphQLQuery(name = "findAllOperationHai", description = "Find all classification")
	List<OperationHai> findAllOperationHai() {
		return operationHaiRepository.findAllOperationHai()
	}

	@GraphQLQuery(name = "getTotalPatientsWithVap", description =  "Total number of patients with VAP")
	Long getTotalPatientsWithVap(@GraphQLArgument(name="year") Integer year){
		def result = jdbcTemplate.queryForObject("""
		Select count(*) from pms.cases where vap_infection IS TRUE AND date_part('year'::text, entry_datetime + interval '8 hour') = ?::DOUBLE PRECISION
		""", Long, year)
		return result
	}

	@GraphQLQuery(name = "getTotalPatientsWithBsi", description =  "Total number of patients with BSI")
	Long getTotalPatientsWithBsi(@GraphQLArgument(name="year") Integer year){
		def result = jdbcTemplate.queryForObject("""
		Select count(*) from pms.cases where bsi_infection IS TRUE AND date_part('year'::text, entry_datetime + interval '8 hour') = ?::DOUBLE PRECISION
		""", Long, year)
		return result
	}

	@GraphQLQuery(name = "getTotalPatientsWithUti", description =  "Total number of patients with UTI")
	Long getTotalPatientsWithUti(@GraphQLArgument(name="year") Integer year){
		def result = jdbcTemplate.queryForObject("""
		Select count(*) from pms.cases where uti_infection IS TRUE AND date_part('year'::text, entry_datetime + interval '8 hour') = ?::DOUBLE PRECISION
		""", Long, year)
		return result
	}


	@GraphQLQuery(name = "getTotalSurgicalSiteInfections", description =  "Total number of surgical site infections")
	Long getTotalSurgicalSiteInfections(@GraphQLArgument(name="year") Integer year){
		def result = jdbcTemplate.queryForObject("""
		select 
		count(infections) as "numberOfInfections"
		from  pms.surgical_site_records s 
		left join pms.cases c on c.id = s."case"::uuid
		where date_part('year'::text, c.entry_datetime + interval '8 hour') = ?::DOUBLE PRECISION;
		""", Long, year)
		return result
	}




	@GraphQLQuery(name = "getTotalVentilatorDays", description =  "Total number of ventilator days")
	Long getTotalVentilatorDays(@GraphQLArgument(name="year") Integer year){
		def result = jdbcTemplate.queryForObject("""
		SELECT COALESCE(sum(c.end_date::date - c.start_date::date),0) from pms.ventilator_records c Left Join pms.cases d on d.id = c.case 
		where date_part('year'::text, d.entry_datetime + interval '8 hour') = ?::DOUBLE PRECISION
		""", Long, year)
		return result
	}

	@GraphQLQuery(name = "getTotalCentralLineDays", description =  "Total number of central line days")
	Long getTotalCentralLineDays(@GraphQLArgument(name="year") Integer year){
		def result = jdbcTemplate.queryForObject("""
		SELECT COALESCE(sum(c.end_date::date - c.start_date::date),0) from pms.central_line_records c Left Join pms.cases d on d.id = c.case 
		where date_part('year'::text, d.entry_datetime + interval '8 hour') = ?::DOUBLE PRECISION
		""", Long, year)
		return result
	}

	@GraphQLQuery(name = "getTotalCatheterDays", description =  "Total number of catheter days")
	Long getTotalCatheterDays(@GraphQLArgument(name="year") Integer year){
		def result = jdbcTemplate.queryForObject("""
		SELECT COALESCE(sum(c.end_date::date - c.start_date::date),0) from pms.catheter_records c Left Join pms.cases d on d.id = c.case 
		where date_part('year'::text, d.entry_datetime + interval '8 hour') = ?::DOUBLE PRECISION
		""", Long, year)
		return result
	}


	@GraphQLQuery(name = "getTotalProceduresDone", description =  "Total number of procedures done")
	Long getTotalProceduresDone(@GraphQLArgument(name="year") Integer year){
		def result = jdbcTemplate.queryForObject("""
		select 
		coalesce (sum("procedures"),0) as "totalOfProcedures" 
		from pms.surgical_site_records s 
		left join pms.cases c on c.id = s."case"::uuid
		where date_part('year'::text, c.entry_datetime + interval '8 hour') = ?::DOUBLE PRECISION;
		""", Long, year)
		return result
	}

	@GraphQLQuery(name = "getVAP", description =  "Ventilator Acquired Pneumonia (VAP)")
	BigDecimal getVAP(@GraphQLArgument(name="year") Integer year){
		BigDecimal vap = 0.00
		Long totalPatientsWithVap = getTotalPatientsWithVap(year)
		Long totalVentilatorDays = getTotalVentilatorDays(year)

		if(totalPatientsWithVap > 0 && totalVentilatorDays > 0) {
			 vap = (totalPatientsWithVap / totalVentilatorDays) * 1000
		}
		return vap
	}

	@GraphQLQuery(name = "getBSI", description =  "Blood Stream Infection (BSI)")
	BigDecimal getBSI(@GraphQLArgument(name="year") Integer year){
		BigDecimal bsi = 0.00
		Long totalPatientsWithBsi = getTotalPatientsWithBsi(year)
		Long totalCentralLineDays = getTotalCentralLineDays(year)

		if(totalPatientsWithBsi > 0 && totalCentralLineDays > 0) {
			bsi = (totalPatientsWithBsi / totalCentralLineDays) * 1000
		}
		return bsi
	}

	@GraphQLQuery(name = "getUTI", description =  "Urinary Tract Infection (UTI)")
	BigDecimal getUTI(@GraphQLArgument(name="year") Integer year){
		BigDecimal uti = 0.00
		Long totalPatientsWithUti = getTotalPatientsWithUti(year)
		Long totalCatheterDays = getTotalCatheterDays(year)

		if(totalPatientsWithUti > 0 && totalCatheterDays > 0) {
			uti = (totalPatientsWithUti / totalCatheterDays) * 1000
		}
		return uti
	}


	@GraphQLQuery(name = "getSSI", description =  "Surgical Site Infection (SSI)")
	BigDecimal getSSI(@GraphQLArgument(name="year") Integer year){
		BigDecimal ssi = 0.00
		Long totalPatientsWithSsi = getTotalSurgicalSiteInfections(year)
		Long totalProcDone = getTotalProceduresDone(year)

		if(totalPatientsWithSsi > 0 && totalProcDone > 0) {
			ssi = (totalPatientsWithSsi / totalProcDone) * 100
		}
		return ssi
	}

	//==================================Mutation ============
	@GraphQLMutation
	def postOperationHai(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		if (id) {
			def operationHai = operationHaiRepository.findById(id).get()
			objectMapper.updateValue(operationHai, fields)
			operationHai.submittedDateTime = Instant.now()

			return operationHaiRepository.save(operationHai)
		} else {

			def operationHai = objectMapper.convertValue(fields, OperationHai)

			return operationHaiRepository.save(operationHai)
		}
	}

	@GraphQLMutation(name = "postOperationHaiDoh")
	GraphQLRetVal<String> postOperationDeathsDoh(@GraphQLArgument(name = 'fields') Map<String, Object> fields){

		try {
			HospitalOperationsHAI request = new HospitalOperationsHAI()

			request.hfhudcode = hospitalConfigService.hospitalInfo.hfhudcode?:""
			request.numhai = new BigDecimal(fields.get('numHai', '0.00') as String)
			request.numdischarges = new BigDecimal(fields.get('numDischarges', '0.00') as String)
			request.infectionrate = new BigDecimal(fields.get('infectionRate', '0.00') as String)
			request.patientnumvap =new BigDecimal(fields.get('patientNumVap', '0.00') as String)
			request.totalventilatordays =new BigDecimal(fields.get('totalVentilatorDays', '0.00') as String)
			request.resultvap = new BigDecimal(fields.get('resultVap', '0.00') as String)
			request.patientnumbsi = new BigDecimal(fields.get('patientNumbsi', '0.00') as String)
			request.totalnumcentralline =new BigDecimal(fields.get('totalNumcentralline', '0.00') as String)
			request.resultbsi = new BigDecimal(fields.get('resultBsi', '0.00') as String)
			request.patientnumuti = new BigDecimal(fields.get('patientNumuti', '0.00') as String)
			request.totalcatheterdays = new BigDecimal(fields.get('totalcatheterDays', '0.00') as String)
			request.resultuti = new BigDecimal(fields.get('resultUti', '0.00') as String)
			request.numssi = new BigDecimal(fields.get('numSsi', '0.00') as String)
			request.totalproceduresdone = new BigDecimal(fields.get('totalProceduresDone', '0.00') as String)
			request.resultssi = new BigDecimal(fields.get('resultSsi', '0.00') as String)
			request.reportingyear =  fields.get("reportingYear") as Integer


			HospitalOperationsHAIResponse response =
					(HospitalOperationsHAIResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospitalOperationsHAI", request)


			UUID id = UUID.fromString(fields.get('id') as String)
			OperationHai dto = operationHaiRepository.getOne(id)
			dto.dohResponse = response.return

			operationHaiRepository.save(dto)

			return new GraphQLRetVal<String>(response.return, true)
		} catch (Exception e) {

			return new GraphQLRetVal<String>(e.message, false)

		}

	}
}
