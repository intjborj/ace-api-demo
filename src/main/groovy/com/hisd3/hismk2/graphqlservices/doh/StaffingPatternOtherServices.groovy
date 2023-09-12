package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.doh.StaffingPatternOthers
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.doh.StaffingPatternOtherRepository
import com.hisd3.hismk2.utils.SOAPConnector
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import ph.gov.doh.uhmistrn.ahsr.webservice.index.HospitalOperationsHAI
import ph.gov.doh.uhmistrn.ahsr.webservice.index.RevenuesResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.StaffingPattern
import ph.gov.doh.uhmistrn.ahsr.webservice.index.StaffingPatternOthersResponse

import java.time.Instant

@Component
@GraphQLApi
class StaffingPatternOtherServices {

	@Autowired
	StaffingPatternOtherRepository staffingPatternOtherRepository

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	SOAPConnector soapConnector

	@Autowired
	HospitalConfigService hospitalConfigService

	@GraphQLQuery(name = "findAllStaffingPatternOthers", description = "Find all classification")
	List<StaffingPatternOthers> findAllStaffingPatternOthers() {
		return staffingPatternOtherRepository.findAllStaffingPatternOthers()
	}

	//==================================Mutation ============
	@GraphQLMutation
	def postStaffingOthers(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		if (id) {
			def staffingOthers = staffingPatternOtherRepository.findById(id).get()
			objectMapper.updateValue(staffingOthers, fields)
			staffingOthers.submittedDateTime = Instant.now()

			return staffingPatternOtherRepository.save(staffingOthers)
		} else {

			def staffingOthers = objectMapper.convertValue(fields, StaffingPatternOthers)

			return staffingPatternOtherRepository.save(staffingOthers)
		}
	}

	@GraphQLMutation(name = "postStaffingPatternOthersDoh")
	GraphQLRetVal<String> postStaffingPatternOthersDoh(@GraphQLArgument(name = 'fields') Map<String, Object> fields){

		try {
			ph.gov.doh.uhmistrn.ahsr.webservice.index.StaffingPatternOthers request = new ph.gov.doh.uhmistrn.ahsr.webservice.index.StaffingPatternOthers()

			request.hfhudcode = hospitalConfigService.hospitalInfo.hfhudcode?:""
			request.parent = fields.get('operationCode') as String
			request.professiondesignation = fields.get('operationCode') as String
			request.specialtyboardcertified = fields.get('surgicalOperation') as String
			request.fulltime40Permanent = fields.get('number') as String
			request.fulltime40Contractual = fields.get('number') as String
			request.parttimepermanent = fields.get('number') as String
			request.parttimecontractual = fields.get('number') as String
			request.activerotatingaffiliate = fields.get('number') as String
			request.outsourced = fields.get('number') as String
			request.reportingyear =  fields.get("reportingYear") as Integer


			StaffingPatternOthersResponse response =
					(StaffingPatternOthersResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/staffingPatternOthers", request)

			UUID id = UUID.fromString(fields.get('id') as String)
			StaffingPatternOthers dto = staffingPatternOtherRepository.getOne(id)
			dto.dohResponse = response.return

			staffingPatternOtherRepository.save(dto)

			return new GraphQLRetVal<String>(response.return, true)

		}catch (Exception e){
			return new GraphQLRetVal<String>(e.message, false)
		}
	}
}
