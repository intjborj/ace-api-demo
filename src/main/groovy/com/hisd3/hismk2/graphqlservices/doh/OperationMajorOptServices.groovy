package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.doh.OperationMajorOpt
import com.hisd3.hismk2.domain.doh.OperationMinorOpt
import com.hisd3.hismk2.graphqlservices.doh.dto.MajorOpDto
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.doh.OperationMajorOptRepository
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
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsHAIResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsMajorOpt
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsMajorOptResponse

import java.time.Instant

@Component
@GraphQLApi
class OperationMajorOptServices {

	@Autowired
	OperationMajorOptRepository operationMajorOptRepository

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	SOAPConnector soapConnector

	@Autowired
	HospitalConfigService hospitalConfigService

	@Autowired
	JdbcTemplate jdbcTemplate

	@GraphQLQuery(name = "findAllOperationMajor", description = "Find all Operation Major")
	List<OperationMajorOpt> findAllOperationMajor() {
		return operationMajorOptRepository.findAllOperationMajor()
	}

	@GraphQLQuery(name = "findTop10MajorOperations", description = "Find all top 10 major operations")
	GraphQLRetVal<List<MajorOpDto>> findTop10MajorOperations(
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
		date_part('year'::text, c.entry_datetime) = '''+ year +''' 
		AND  (d.value ->> 'procedureType'::text) = 'MAJOR' 
		AND NOT COALESCE((d.value ->> 'isCaesarian'::text)::boolean, FALSE)
		GROUP BY 
		(d.value ->> 'proccode'::text), 
		(d.value ->> 'procdesc'::text), 
		(date_part('year'::text, c.entry_datetime)), 
		(d.value ->> 'proccode'::text)
		ORDER BY (count((d.value ->> 'proccode'::text))) DESC
		--limit 10
			''',new BeanPropertyRowMapper(MajorOpDto.class))
		if(result){
			return new GraphQLRetVal<List<MajorOpDto>>(result as List<MajorOpDto>,true,'Success!')
		}
		return new GraphQLRetVal<List<MajorOpDto>>(new ArrayList<MajorOpDto>(),false,'Error')
	}
	//==================================Mutation ============
	@GraphQLMutation
	def postmajoropt(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		if (id) {
			def majorOpt = operationMajorOptRepository.findById(id).get()
			objectMapper.updateValue(majorOpt, fields)
			majorOpt.submittedDateTime = Instant.now()

			return operationMajorOptRepository.save(majorOpt)
		} else {

			def majorOpt = objectMapper.convertValue(fields, OperationMajorOpt)

			return operationMajorOptRepository.save(majorOpt)
		}
	}

	@GraphQLMutation(name = "postOperationMajorOptDoh")
	GraphQLRetVal<String> postOperationMajorOptDoh(@GraphQLArgument(name = 'fields') Map<String, Object> fields){

		try {
			HospitalOperationsMajorOpt request = new HospitalOperationsMajorOpt()

			request.hfhudcode = hospitalConfigService.hospitalInfo.hfhudcode?:""
			request.operationcode = fields.get('operationCode') as String
			request.surgicaloperation = fields.get('surgicalOperation') as String
			request.number = fields.get('number') as String
			request.reportingyear =  fields.get("reportingYear") as Integer


			HospitalOperationsMajorOptResponse response =
					(HospitalOperationsMajorOptResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospitalOperationsMajorOpt", request)

			UUID id = UUID.fromString(fields.get('id') as String)
			OperationMajorOpt dto = operationMajorOptRepository.getOne(id)
			dto.dohResponse = response.return

			operationMajorOptRepository.save(dto)

			return new GraphQLRetVal<String>(response.return, true)
		}catch(Exception e){
			return new GraphQLRetVal<String>(e.message, false)
		}
	}
}
