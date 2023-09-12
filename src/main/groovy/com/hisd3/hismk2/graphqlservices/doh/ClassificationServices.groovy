package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.doh.Classification
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.doh.ClassificationRepository
import com.hisd3.hismk2.utils.SOAPConnector
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ph.gov.doh.uhmistrn.ahsr.webservice.index.GenInfoClassification
import ph.gov.doh.uhmistrn.ahsr.webservice.index.GenInfoClassificationResponse

import java.time.Instant

@Component
@GraphQLApi
class ClassificationServices {

	@Autowired
	SOAPConnector soapConnector

	@Autowired
	ClassificationRepository classificationRepository
	
	@Autowired
	ObjectMapper objectMapper

	@Autowired
	HospitalConfigService hospitalConfigService

	@GraphQLQuery(name = "findAllClassification", description = "Find all classification")
	List<Classification> findAllClassification() {
		return classificationRepository.findAllClassification()
	}
	
	//==================================Mutation ============
	@GraphQLMutation
	def postGeneralInfoClass(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		if (id) {
			def classification = classificationRepository.findById(id).get()
			objectMapper.updateValue(classification, fields)
			classification.submittedDateTime = Instant.now()

			return classificationRepository.save(classification)
		} else {

			def classification = objectMapper.convertValue(fields, Classification)

			return classificationRepository.save(classification)
		}
	}
	@GraphQLMutation(name = "sendInfoClassification")
	GraphQLRetVal<String> sendInfoClassification(@GraphQLArgument(name = "fields") Map<String, Object> fields) {
		try {
			GenInfoClassification request = new GenInfoClassification()
			request.hfhudcode = hospitalConfigService.hospitalInfo.hfhudcode?:""
			request.servicecapability = fields.get("serviceCapability") as Integer
			request.general = fields.get("general") as Integer
			request.specialty = fields.get("specialty") as Integer
			request.specialtyspecify = fields.get("specialtySpecify") as String
			request.traumacapability = fields.get("traumaCapability") as Integer
			request.natureofownership = fields.get("natureOfOwnership") as Integer
			request.government = fields.get("government") as Integer
			request.national = fields.get("nationals") as Integer
			request.local = fields.get("locals") as Integer
			request.private = fields.get("privates") as Integer
			request.reportingyear = fields.get("reportingYear") as Integer
			request.ownershipothers = fields.get("ownershipOthers") as String

			GenInfoClassificationResponse response =
					(GenInfoClassificationResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/genInfoClassification", request)
			return new GraphQLRetVal<String>(response.return, true)
		}
		catch (Exception e) {
			return new GraphQLRetVal<String>(e.message, false)
		}
	}
}
