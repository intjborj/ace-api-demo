package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.hisd3.hismk2.domain.doh.DOH_REPORT_TYPE
import com.hisd3.hismk2.domain.doh.DohLogs
import com.hisd3.hismk2.domain.doh.OptDischargeOpv
import com.hisd3.hismk2.graphqlservices.doh.dto.HospOptDischargesOPDDTO
import com.hisd3.hismk2.graphqlservices.doh.dto.OpdConsultation2Dto
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.doh.OptDischargeOpvRepository
import com.hisd3.hismk2.utils.SOAPConnector
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesOPV
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesOPVResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsHAI
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsMinorOpt
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsMinorOptResponse

import java.time.Instant

@Component
@GraphQLApi
class OptDischargeOpvServices {

	@Autowired
	OptDischargeOpvRepository optDischargeOpvRepository

	@Autowired
	DohLogsServices dohLogsServices

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	SOAPConnector soapConnector

	@Autowired
	HospitalConfigService hospitalConfigService

	@Autowired
	OpdConsultationService opdConsultationService

	@Autowired
	DohAPIService dohAPIService

	@GraphQLQuery(name = "findAllDischargeOpv", description = "Find all classification")
	List<OptDischargeOpv> findAllDischargeOpv() {
		return optDischargeOpvRepository.findAllDischargeOpv()
	}

	//==================================Mutation ============
	@GraphQLMutation
	def postDischargeOpv(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		if (id) {
			def dischargeOpv = optDischargeOpvRepository.findById(id).get()
			objectMapper.updateValue(dischargeOpv, fields)
			dischargeOpv.submittedDateTime = Instant.now()

			return optDischargeOpvRepository.save(dischargeOpv)
		} else {

			def dischargeOpv = objectMapper.convertValue(fields, OptDischargeOpv)

			return optDischargeOpvRepository.save(dischargeOpv)
		}
	}


	@GraphQLMutation(name = "postOptDischargeOpvDoh")
	GraphQLRetVal<String> postOptDischargeOpvDoh(@GraphQLArgument(name = 'fields') Map<String, Object> fields){

		try {
			HospOptDischargesOPV request = new HospOptDischargesOPV()

			request.hfhudcode = hospitalConfigService.hospitalInfo.hfhudcode?:""
			request.newpatient = fields.get('operationCode') as String
			request.revisit = fields.get('surgicalOperation') as String
			request.adult = fields.get('number') as String
			request.pediatric = fields.get('number') as String
			request.adultgeneralmedicine = fields.get('number') as String
			request.specialtynonsurgical = fields.get('number') as String
			request.surgical = fields.get('number') as String
			request.antenatal = fields.get('number') as String
			request.postnatal = fields.get('number') as String
			request.reportingyear =  fields.get("reportingYear") as Integer


			HospOptDischargesOPVResponse response =
					(HospOptDischargesOPVResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospOptDischargesOPV", request)

			UUID id = UUID.fromString(fields.get('id') as String)
			OptDischargeOpv dto = optDischargeOpvRepository.getOne(id)
			dto.dohResponse = response.return

			optDischargeOpvRepository.save(dto)

			return new GraphQLRetVal<String>(response.return, true)
		} catch (Exception e) {
			return new GraphQLRetVal<String>(e.message, false)

		}
	}


	@GraphQLMutation(name="postHospOptDischargesOPD")
	GraphQLRetVal<List<HospOptDischargesOPDDTO>> postHospOptDischargesOPD (@GraphQLArgument(name ='year') Integer year) {
		List<HospOptDischargesOPDDTO> hospitalOptDischargeOpdList = []
		List<OpdConsultation2Dto> opdConsultationV2 = opdConsultationService.OpdConsultationV2(year)

		opdConsultationV2.each {
			GraphQLRetVal <HospOptDischargesOPDDTO> retVal = dohAPIService.hospOptDischargesOPD(
					it.description,
					it.number.toString(),
					it.icdCode,
					it.icdCategory,
					year.toString()
			)
			hospitalOptDischargeOpdList.push(retVal.payload)

			Map<String,Object> fields = [:]
			fields['description'] = it.description
			fields['number'] = it.number.toString()
			fields['icdCode'] = it.icdCode
			fields['icdCategory'] = it.icdCategory
			fields['year'] = year.toString()

			if(retVal){
				DohLogs dohLogs = new DohLogs()
				dohLogs.type = DOH_REPORT_TYPE.DISCHARGES_OPD.name()
				dohLogs.submittedReport = new JSONObject(fields)
				dohLogs.reportResponse = new Gson().toJson(retVal.payload)
				dohLogs.reportingYear = year
				dohLogs.status = retVal.payload.response_desc
				dohLogsServices.save(dohLogs)
			}

		}
		return new GraphQLRetVal<List<HospOptDischargesOPDDTO>>(hospitalOptDischargeOpdList, true, '')
	}

}
