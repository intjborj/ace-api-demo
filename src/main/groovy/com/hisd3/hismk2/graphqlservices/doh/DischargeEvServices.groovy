package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.doh.DischargeEv
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.doh.DischargeEvRepository
import com.hisd3.hismk2.utils.SOAPConnector
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesEV
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesEVResponse

import java.time.Instant

@Component
@GraphQLApi
class DischargeEvServices {

	@Autowired
	SOAPConnector soapConnector

	@Autowired
	DischargeEvRepository dischargeEvRepository

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	HospitalConfigService hospitalConfigService

	@GraphQLQuery(name = "findAllDischargeEv", description = "Find all Discharge EV")
	List<DischargeEv> findAllDischargeEv() {
		return dischargeEvRepository.findAllDischargeEv()
	}

	//==================================Mutation ============
	@GraphQLMutation
	def postDischargeEv(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		if (id) {
			def dischargeEv = dischargeEvRepository.findById(id).get()
			objectMapper.updateValue(dischargeEv, fields)
			dischargeEv.submittedDateTime = Instant.now()

			return dischargeEvRepository.save(dischargeEv)
		} else {

			def dischargeEv = objectMapper.convertValue(fields, DischargeEv)

			return dischargeEvRepository.save(dischargeEv)
		}
	}
	@GraphQLMutation(name= "sendDischargeEv")
	GraphQLRetVal<String> sendDischargeEv(@GraphQLArgument(name = "fields") Map<String, Object> fields){

		try {
			HospOptDischargesEV request = new HospOptDischargesEV()
			request.hfhudcode = hospitalConfigService.hospitalInfo.hfhudcode?:""
			request.emergencyvisits = fields.get("emergencyVisits") as Integer
			request.emergencyvisitsadult = fields.get("emergencyVisitsAdult") as Integer
			request.emergencyvisitspediatric = fields.get("emergencyVisitsPediatric") as Integer
			request.evfromfacilitytoanother = fields.get("evFromFacilityToAnother") as Integer
			request.reportingyear = fields.get("reportingYear") as Integer

			HospOptDischargesEVResponse response =
					(HospOptDischargesEVResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospOptDischargesEV", request)
			return new GraphQLRetVal<String>(response.return, true)
		} catch (Exception e) {
			return new GraphQLRetVal<String>(e.message, false)
		}
	}
}
