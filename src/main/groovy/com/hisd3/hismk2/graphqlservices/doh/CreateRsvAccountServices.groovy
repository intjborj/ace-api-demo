package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.doh.CreateRsvAccount
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.doh.CreateRsvAccountRepository
import com.hisd3.hismk2.utils.SOAPConnector
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ph.gov.doh.uhmistrn.ahsr.webservice.index.CreateNEHEHRSVaccount
import ph.gov.doh.uhmistrn.ahsr.webservice.index.CreateNEHEHRSVaccountResponse

import java.time.Instant

@Component
@GraphQLApi
class CreateRsvAccountServices {

	@Autowired
	SOAPConnector soapConnector

	@Autowired
	CreateRsvAccountRepository createRsvAccountRepository

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	HospitalConfigService hospitalConfigService

	@GraphQLQuery(name = "findAllCreateRsvAccount", description = "Find all classification")
	List<CreateRsvAccount> findAllCreateRsvAccount() {
		return createRsvAccountRepository.findAllCreateRsvAccount()
	}

	//==================================Mutation ============
	@GraphQLMutation
	def postCreateRsvAccount(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		if (id) {
			def createRvsAccount = createRsvAccountRepository.findById(id).get()
			objectMapper.updateValue(createRvsAccount, fields)
			createRvsAccount.submittedDateTime = Instant.now()

			return createRsvAccountRepository.save(createRvsAccount)
		} else {

			def createRvsAccount = objectMapper.convertValue(fields, CreateRsvAccount)

			return createRsvAccountRepository.save(createRvsAccount)
		}
	}

	@GraphQLMutation(name = "createNERvsAccount")
	GraphQLRetVal<String> createNERvsAccount(@GraphQLArgument(name = "fields") Map<String, Object> fields) {

		try {
			CreateNEHEHRSVaccount request = new CreateNEHEHRSVaccount()
			request.hfhudcode = hospitalConfigService.hospitalInfo.hfhudcode?:""
			request.hfhudname = fields.get("hfhUdName") as String
			request.fhudaddress = fields.get("fhUdAddress") as String
			request.regcode = fields.get("regCode") as String
			request.provcode = fields.get("provCode") as String
			request.ctymuncode = fields.get("ctyMunCode") as String
			request.bgycode = fields.get("bgyCode") as String
			request.fhudtelno1 = fields.get("fhUdTelNo1") as String
			request.fhudtelno2 = fields.get("fhUdTelNo2") as String
			request.fhudfaxno = fields.get("fhUdFaxNo") as String
			request.fhudemail = fields.get("fhUdEmail") as String
			request.headlname = fields.get("headlName") as String
			request.headfname = fields.get("headfName") as String
			request.headmname = fields.get("headmName") as String
			request.accessKey = fields.get("accessKey") as String

			CreateNEHEHRSVaccountResponse response =
					(CreateNEHEHRSVaccountResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/createNEHEHRSVaccount", request)
			return new GraphQLRetVal<String>(response.return, true)
		}
		catch (Exception e) {
			return new GraphQLRetVal<String>(e.message, false)
		}
	}
}
