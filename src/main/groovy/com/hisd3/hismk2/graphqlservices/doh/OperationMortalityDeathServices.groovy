package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.doh.OperationMortalityDeaths
import com.hisd3.hismk2.domain.doh.OptDischargeOpv
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.doh.OperationMortalityDeathRepository
import com.hisd3.hismk2.utils.SOAPConnector
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesMorbidity
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospOptDischargesMorbidityResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsMortalityDeaths
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsMortalityDeathsResponse

import java.time.Instant

@Component
@GraphQLApi
class OperationMortalityDeathServices {

	@Autowired
	OperationMortalityDeathRepository operationMortalityDeathRepository

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	SOAPConnector soapConnector

	@Autowired
	HospitalConfigService hospitalConfigService

	@GraphQLQuery(name = "findAllMortalityDeathsRepository", description = "Find all classification")
	List<OperationMortalityDeaths> findAllMortalityDeathsRepository() {
		return operationMortalityDeathRepository.findAllMortalityDeathsRepository()
	}

	//==================================Mutation ============
	@GraphQLMutation
	def postMortalityDeaths(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		if (id) {
			def mortalityDeaths = operationMortalityDeathRepository.findById(id).get()
			objectMapper.updateValue(mortalityDeaths, fields)
			mortalityDeaths.submittedDateTime = Instant.now()

			return operationMortalityDeathRepository.save(mortalityDeaths)
		} else {

			def mortalityDeaths = objectMapper.convertValue(fields, OperationMortalityDeaths)

			return operationMortalityDeathRepository.save(mortalityDeaths)
		}
	}

	@GraphQLMutation(name = "postHospitalDichargeMortality")
	GraphQLRetVal<String> postHospitalDichargeMortality(@GraphQLArgument(name='fields') Map<String, Object> fields){

		try {
			HospitalOperationsMortalityDeaths request = new HospitalOperationsMortalityDeaths()

			request.hfhudcode = hospitalConfigService.hospitalInfo.hfhudcode?:""
			request.icd10Desc = fields.get('icd10Desc') as String
			request.munder1 = fields.get('maleUnder1') as Integer
			request.funder1 = fields.get('femaleUnder1') as Integer
			request.m1To4 = fields.get('male14') as Integer
			request.f1To4 = fields.get('female14') as Integer
			request.m5To9 = fields.get('male59') as Integer
			request.f5To9 = fields.get('female59') as Integer
			request.m10To14 = fields.get('male1014') as Integer
			request.f10To14 = fields.get('female1014') as Integer
			request.m15To19 = fields.get('male1519') as Integer
			request.f15To19 = fields.get('female1519') as Integer
			request.m20To24 = fields.get('male2024') as Integer
			request.f20To24 = fields.get('female2024') as Integer
			request.m25To29 = fields.get('male2529') as Integer
			request.f25To29 = fields.get('female2529') as Integer
			request.m30To34 = fields.get('male3034') as Integer
			request.f30To34 = fields.get('female3034') as Integer
			request.m35To39 = fields.get('male3539') as Integer
			request.f35To39 = fields.get('female3539') as Integer
			request.m40To44 = fields.get('male4044') as Integer
			request.f40To44 = fields.get('female4044') as Integer
			request.m45To49 = fields.get('male4549') as Integer
			request.f45To49 = fields.get('female4549') as Integer
			request.m50To54 = fields.get('male5054') as Integer
			request.f50To54 = fields.get('female5054') as Integer
			request.m55To59 = fields.get('male5559') as Integer
			request.f55To59 = fields.get('female5559') as Integer
			request.m60To64 = fields.get('male6064') as Integer
			request.f60To64 = fields.get('female6064') as Integer
			request.m65To69 = fields.get('male5669') as Integer
			request.f65To69 = fields.get('female6569') as Integer
			request.m70Over = fields.get('male70Over') as Integer
			request.f70Over = fields.get('female70Over') as Integer
			request.msubtotal = fields.get('maleSubtotal') as Integer
			request.fsubtotal = fields.get('femaleSubtotal') as Integer
			request.grandtotal = fields.get('grandTotal') as Integer
			request.icd10Code = fields.get('icd10Code') as String
			request.icd10Category = fields.get('diagnosisCategory') as String
			request.reportingyear = fields.get('reportingYear') as Integer

			HospitalOperationsMortalityDeathsResponse response =
					(HospitalOperationsMortalityDeathsResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/hospitalOperationsMortalityDeaths", request)


			UUID id = UUID.fromString(fields.get('id') as String)
			OperationMortalityDeaths dto = operationMortalityDeathRepository.getOne(id)
			dto.dohResponse = response.return

			operationMortalityDeathRepository.save(dto)

			return new GraphQLRetVal<String>(response.return, true)
		} catch (Exception e) {
			return new GraphQLRetVal<String>(e.message, false)

		}
	}
}
