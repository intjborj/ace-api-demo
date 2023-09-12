package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.doh.Revenues
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.doh.RevenuesRepository
import com.hisd3.hismk2.utils.SOAPConnector
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesOPV
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesOPVResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsHAI
import ph.gov.doh.uhmistrn.ahsr.webservice.index.RevenuesResponse

import java.time.Instant

@Component
@GraphQLApi
class RevenuesServices {

	@Autowired
	RevenuesRepository revenuesRepository

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	SOAPConnector soapConnector

	@Autowired
	HospitalConfigService hospitalConfigService

	@GraphQLQuery(name = "findAllRevenues", description = "Find all classification")
	List<Revenues> findAllRevenues() {
		return revenuesRepository.findAllRevenues()
	}

	//==================================Mutation ============
	@GraphQLMutation
	def postRevenues(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		if (id) {
			def revenuesServices = revenuesRepository.findById(id).get()
			objectMapper.updateValue(revenuesServices, fields)
			revenuesServices.submittedDateTime = Instant.now()

			return revenuesRepository.save(revenuesServices)
		} else {

			def revenuesServices = objectMapper.convertValue(fields, Revenues)

			return revenuesRepository.save(revenuesServices)
		}
	}

	@GraphQLMutation(name = "postHospitalRevenueDoh")
	GraphQLRetVal<String> postHospitalRevenueDoh(@GraphQLArgument(name = 'fields') Map<String, Object> fields){

		try {
			ph.gov.doh.uhmistrn.ahsr.webservice.index.Revenues request = new ph.gov.doh.uhmistrn.ahsr.webservice.index.Revenues()

			request.hfhudcode = hospitalConfigService.hospitalInfo.hfhudcode?:""
			request.amountfromdoh = fields.get('operationCode') as String
			request.amountfromlgu = fields.get('surgicalOperation') as String
			request.amountfromdonor = fields.get('number') as String
			request.amountfromprivateorg = fields.get('number') as String
			request.amountfromphilhealth = fields.get('number') as String 
			request.amountfrompatient = fields.get('number') as String
			request.amountfromreimbursement = fields.get('number') as String
			request.amountfromothersources = fields.get('number') as String
			request.grandtotal = fields.get('number') as String
			request.reportingyear =  fields.get("reportingYear") as Integer

			RevenuesResponse response =
					(RevenuesResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/revenues", request)

			UUID id = UUID.fromString(fields.get('id') as String)
			Revenues dto = revenuesRepository.getOne(id)
			dto.dohResponse = response.return

			revenuesRepository.save(dto)

			return new GraphQLRetVal<String>(response.return, true)

		}catch (Exception e){
			return new GraphQLRetVal<String>(e.message, false)
		}
	}
}
