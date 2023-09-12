package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.doh.OperationMinorOpt
import com.hisd3.hismk2.domain.doh.OperationMortalityDeaths
import com.hisd3.hismk2.graphqlservices.doh.dto.OpdMinorOpDto
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.doh.OperationMinorOptRepository
import com.hisd3.hismk2.utils.SOAPConnector
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsHAI
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsMajorOpt
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsMajorOptResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsMinorOpt
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsMinorOptResponse

import java.time.Instant



@Component
@GraphQLApi
class OperationMinorOptServices {

	@Autowired
	OperationMinorOptRepository operationMinorOptRepository

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	SOAPConnector soapConnector

	@Autowired
	HospitalConfigService hospitalConfigService

	@Autowired
	JdbcTemplate jdbcTemplate

	@GraphQLQuery(name = "findAllOperationMinor", description = "Find all classification")
	List<OperationMinorOpt> findAllOperationMinor() {
		return operationMinorOptRepository.findAllOperationMinor()
	}

	@GraphQLQuery(name = "findTop10MinorOperations", description = "Find all top 10 minor operations")
	GraphQLRetVal<List<OpdMinorOpDto>> findTop10MinorOperations(
			@GraphQLArgument(name = 'year') Integer year
	) {
		def result = jdbcTemplate.query('''
				SELECT (d.value ->> 'proccode'::text) AS proccode,
				(d.value ->> 'procdesc'::text) AS longName,
				count((d.value ->> 'proccode'::text)) AS total
				FROM pms.cases c,
				LATERAL json_array_elements(
				CASE
				WHEN (pms.is_json((c.doh_surgical_diagnosis)::character varying) AND (c.doh_surgical_diagnosis IS NOT NULL)) THEN (c.doh_surgical_diagnosis)::json
				ELSE NULL::json
				END) d(value)
				
				WHERE 
				date_part('year'::text, c.entry_datetime) = ''' + year + ''' 
				AND  (d.value ->> 'procedureType'::text) = 'MINOR' 
				GROUP BY 
				(d.value ->> 'proccode'::text), 
				(d.value ->> 'procdesc'::text), 
				(date_part('year'::text, c.entry_datetime)), 
				(d.value ->> 'proccode'::text)
				ORDER BY (count((d.value ->> 'proccode'::text))) DESC
				-- limit 10
			''',new BeanPropertyRowMapper(OpdMinorOpDto.class))
		if(result){
			return new GraphQLRetVal<List<OpdMinorOpDto>>(result as List<OpdMinorOpDto>,true,'Success!')
		}
		return new GraphQLRetVal<List<OpdMinorOpDto>>(new ArrayList<OpdMinorOpDto>(),false,'Error')
	}

	//==================================Mutation ============
	@GraphQLMutation
	def postOperationMinor(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		if (id) {
			def operationMinor = operationMinorOptRepository.findById(id).get()
			objectMapper.updateValue(operationMinor, fields)
			operationMinor.submittedDateTime = Instant.now()

			return operationMinorOptRepository.save(operationMinor)
		} else {

			def operationMinor = objectMapper.convertValue(fields, OperationMinorOpt)

			return operationMinorOptRepository.save(operationMinor)
		}
	}

	@GraphQLMutation(name = "postOperationMinorOptDoh")
	GraphQLRetVal<String> postOperationMinorOptDoh(@GraphQLArgument(name = 'fields') Map<String, Object> fields){

		try {
			HospitalOperationsMinorOpt request = new HospitalOperationsMinorOpt()

			request.hfhudcode = hospitalConfigService.hospitalInfo.hfhudcode?:""
			request.operationcode = fields.get('operationCode') as String
			request.surgicaloperation = fields.get('surgicalOperation') as String
			request.number = fields.get('number') as String
			request.reportingyear =  fields.get("reportingYear") as Integer


			HospitalOperationsMinorOptResponse response =
					(HospitalOperationsMinorOptResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospitalOperationsMinorOpt", request)

			UUID id = UUID.fromString(fields.get('id') as String)
			OperationMinorOpt dto = operationMinorOptRepository.getOne(id)
			dto.dohResponse = response.return

			operationMinorOptRepository.save(dto)

			return new GraphQLRetVal<String>(response.return, true)
		} catch (Exception e) {
			return new GraphQLRetVal<String>(e.message, false)
		}
	}
}
