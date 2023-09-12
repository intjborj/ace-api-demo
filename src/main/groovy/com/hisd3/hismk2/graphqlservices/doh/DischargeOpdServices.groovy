package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.doh.DischargeOpd
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.doh.DischargeOpdRepository
import com.hisd3.hismk2.utils.SOAPConnector
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesOPD
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesOPDResponse

import java.time.Instant

@Component
@GraphQLApi
class DischargeOpdServices {

	@Autowired
	SOAPConnector soapConnector

	@Autowired
	DischargeOpdRepository dischargeOpdRepository

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	HospitalConfigService hospitalConfigService


	@GraphQLQuery(name = "findAllDischareOpd", description = "Find all Discharge OPD")
	List<DischargeOpd> findAllDischargeOpd() {
		return dischargeOpdRepository.findAllDischargeOpd()
	}

	//==================================Mutation ============
	@GraphQLMutation
	def postDischargesOpd(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		if (id) {
			def dischargeOpd = dischargeOpdRepository.findById(id).get()
			objectMapper.updateValue(dischargeOpd, fields)
			dischargeOpd.submittedDateTime = Instant.now()

			return dischargeOpdRepository.save(dischargeOpd)
		} else {

			def dischargeOpd = objectMapper.convertValue(fields, DischargeOpd)

			return dischargeOpdRepository.save(dischargeOpd)
		}
	}
	@GraphQLMutation(name = "postDischargeOpd")
	GraphQLRetVal<String> postDischargeOpd(@GraphQLArgument(name = "fields") Map<String, Object> fields) {

		try {
			HospOptDischargesOPD request = new HospOptDischargesOPD()
			request.hfhudcode = hospitalConfigService.hospitalInfo.hfhudcode?:""
			request.opdconsultations = fields.get("opdConsultation") as String
			request.number = fields.get("number") as Integer
			request.icd10Code = fields.get("icd10Code") as String
			request.icd10Category = fields.get("icd10Category") as String
			request.reportingyear = fields.get("reportingYear") as Integer

			HospOptDischargesOPDResponse response =
					(HospOptDischargesOPDResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospOptDischargesOPD", request)
			return new GraphQLRetVal<String>(response.return, true)
		}
		catch (Exception e) {
			return new GraphQLRetVal<String>(e.message, false)
		}
	}
}
