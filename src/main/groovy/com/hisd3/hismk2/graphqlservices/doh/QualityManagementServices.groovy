package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.doh.GenInfoQualityManagements
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.doh.GenInfoQualityManagementRepository
import com.hisd3.hismk2.utils.SOAPConnector
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ph.gov.doh.uhmistrn.ahsr.webservice.index.GenInfoQualityManagement
import ph.gov.doh.uhmistrn.ahsr.webservice.index.GenInfoQualityManagementResponse

import java.time.Instant

@Component
@GraphQLApi
class QualityManagementServices {

	@Autowired
	SOAPConnector soapConnector

	@Autowired
	GenInfoQualityManagementRepository genInfoQualityManagementRepository

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	HospitalConfigService hospitalConfigService

	@GraphQLQuery(name = "findAllQualityManagement", description = "Find all General Info Quality Management")
	List<GenInfoQualityManagements> findAllQualityManagement() {
		return genInfoQualityManagementRepository.findAllQualityManagement()
	}

	//==================================Mutation ============
	@GraphQLMutation
	def postQualityManagement(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		if (id) {
			def qualityManagement = genInfoQualityManagementRepository.findById(id).get()
			objectMapper.updateValue(qualityManagement, fields)
			qualityManagement.submittedDateTime = Instant.now()

			return genInfoQualityManagementRepository.save(qualityManagement)
		} else {

			def qualityManagement = objectMapper.convertValue(fields, GenInfoQualityManagements)

			return genInfoQualityManagementRepository.save(qualityManagement)
		}
	}
	@GraphQLMutation
	GraphQLRetVal<String> sendQualityManagement(
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	){
		try {



			GenInfoQualityManagement request = new GenInfoQualityManagement()
			request.hfhudcode = hospitalConfigService.hospitalInfo.hfhudcode?:""
			request.qualitymgmttype = fields.get("qualityMgmttype") as Integer
			request.description = fields.get("description") as String
			request.certifyingbody = fields.get("certifyingBody") as String
			request.philhealthaccreditation = fields.get("philHealthAccredition") as Integer
			request.validityfrom = fields.get("validityFrom") as String
			request.validityto = fields.get("validityTo") as String
			request.reportingyear = fields.get("reportingYear") as Integer

			GenInfoQualityManagementResponse response =
					(GenInfoQualityManagementResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/genInfoQualityManagement", request)

			UUID id = UUID.fromString(fields.get('id') as String)
			GenInfoQualityManagements dto = genInfoQualityManagementRepository.getOne(id)
			dto.dohResponse = response.return

			genInfoQualityManagementRepository.save(dto)

			return new GraphQLRetVal<String>(response.return, true)
		} catch (Exception e) {
			return new GraphQLRetVal<String>(e.message, false)

		}

	}
}
