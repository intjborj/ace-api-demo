package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.hisd3.hismk2.domain.doh.DOH_REPORT_TYPE
import com.hisd3.hismk2.domain.doh.DischargesEr
import com.hisd3.hismk2.domain.doh.DohLogs
import com.hisd3.hismk2.graphqlservices.doh.dto.ErdConsultation2Dto
import com.hisd3.hismk2.graphqlservices.doh.dto.HospOptDischargesERDTO
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.doh.DischargeErRepository
import com.hisd3.hismk2.utils.SOAPConnector
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesER
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesERResponse

import java.time.Instant

@Component
@GraphQLApi
class DischargeErServices {

	@Autowired
	SOAPConnector soapConnector

	@Autowired
	DischargeErRepository dischargeErRepository

	@Autowired
	DohLogsServices dohLogsServices

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	HospitalConfigService hospitalConfigService

	@Autowired
	DohAPIService dohAPIService

	@Autowired
	ErConsultationService erConsultationService

	@GraphQLQuery(name = "findAllEr", description = "Find all Er")
	List<DischargesEr> findAllEr() {
		return dischargeErRepository.findAllEr()
	}

	//==================================Mutation ============



	@GraphQLMutation
	def postDischargesEr(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		if (id) {
			def dischargeEr = dischargeErRepository.findById(id).get()
			objectMapper.updateValue(dischargeEr, fields)
			dischargeEr.submittedDateTime = Instant.now()

			return dischargeErRepository.save(dischargeEr)
		} else {

			def dischargeEr = objectMapper.convertValue(fields, DischargesEr)

			return dischargeErRepository.save(dischargeEr)
		}
	}
	@GraphQLMutation(name = "sendDischargeEr")
	GraphQLRetVal<String> sendDischargeEr(@GraphQLArgument(name = "fields") Map<String, Object> fields){

		try {
			HospOptDischargesER request = new HospOptDischargesER()
			request.hfhudcode = hospitalConfigService.hospitalInfo.hfhudcode?:""
			request.erconsultations = fields.get("erConsultations") as String
			request.number = fields.get("number") as Integer
			request.icd10Code = fields.get("icd10Code") as String
			request.icd10Category = fields.get("icd10Category") as String
			request.reportingyear = fields.get("reportingYear") as Integer

			HospOptDischargesERResponse response =
					(HospOptDischargesERResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospOptDischargesER", request)
			return new GraphQLRetVal<String>(response.return, true)
		} catch (Exception e) {
			return new GraphQLRetVal<String>(e.message, false)
		}
	}

	@GraphQLMutation(name = "hospitalOptDischargeER")
	GraphQLRetVal<List<HospOptDischargesERDTO>> hospitalOptDischargeER(@GraphQLArgument(name = "year") Integer year){
		List<HospOptDischargesERDTO> hospOptDischargesERDTOList = []
		List<ErdConsultation2Dto> erdConsultationV2 = erConsultationService.ErdConsultationV2(year)

		erdConsultationV2.each {
			GraphQLRetVal<HospOptDischargesERDTO> retVal = dohAPIService.hospOptDischargesER(
					it.description,
					it.number.toString(),
					it.icdCode,
					it.icdCategory,
					year.toString()

			)
			Map<String,Object> fields = [:]
			fields['description'] = it.description
			fields['number'] = it.number.toString()
			fields['icdCode'] = it.icdCode
			fields['icdCategory'] = it.icdCategory
			fields['year'] = year.toString()

			hospOptDischargesERDTOList.push(retVal.payload)
			if(retVal){
				DohLogs dohLogs = new DohLogs()
				dohLogs.type = DOH_REPORT_TYPE.DISCHARGES_ER.name()
				dohLogs.submittedReport = new JSONObject(fields)
				dohLogs.reportResponse = new Gson().toJson(retVal.payload)
				dohLogs.reportingYear = year
				dohLogs.status = retVal.payload.response_desc
				dohLogsServices.save(dohLogs)
			}

		}




		return new GraphQLRetVal<List<HospOptDischargesERDTO>>(hospOptDischargesERDTOList,true, '')

	}

}
