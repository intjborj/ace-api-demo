package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.doh.DischargesTesting
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.doh.DischargeTestingRepository
import com.hisd3.hismk2.utils.SOAPConnector
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesTesting
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesTestingResponse

import java.time.Instant

@Component
@GraphQLApi
class DischargeTestingServices {

	@Autowired
	SOAPConnector soapConnector

	@Autowired
	DischargeTestingRepository dischargeTestingRepository

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	HospitalConfigService hospitalConfigService

	@GraphQLQuery(name = "findAllTesting", description = "Find all classification")
	List<DischargesTesting> findAllTesting() {
		return dischargeTestingRepository.findAllTesting()
	}

	//==================================Mutation ============
	@GraphQLMutation
	def postDischargeTesting(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		if (id) {
			def dischargeTesting = dischargeTestingRepository.findById(id).get()
			objectMapper.updateValue(dischargeTesting, fields)
			dischargeTesting.submittedDateTime = Instant.now()

			return dischargeTestingRepository.save(dischargeTesting)
		} else {

			def dischargeTesting = objectMapper.convertValue(fields, DischargesTesting)

			return dischargeTestingRepository.save(dischargeTesting)
		}
	}

	@GraphQLMutation(name = "sendDischargeTesting")
	GraphQLRetVal<String> sendDischargeTesting(@GraphQLArgument(name = "fields") Map<String, Object> fields){
		try {
			HospOptDischargesTesting request = new HospOptDischargesTesting()
			request.hfhudcode = hospitalConfigService.hospitalInfo.hfhudcode?:""
			request.testinggroup = fields.get("testingGroup") as String
			request.testing = fields.get("testing") as String
			request.number = fields.get("number") as Integer
			request.reportingyear = fields.get("reportingYear") as Integer

			HospOptDischargesTestingResponse response =
					(HospOptDischargesTestingResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospOptDischargesTesting", request)
			return new GraphQLRetVal<String>(response.return, true)
		} catch (Exception e) {
			return new GraphQLRetVal<String>(e.message, false)
		}
	}
}
