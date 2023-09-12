package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.doh.SubmittedReports
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.doh.SubmittedReportRepository
import com.hisd3.hismk2.utils.SOAPConnector
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ph.gov.doh.uhmistrn.ahsr.webservice.index.RevenuesResponse
import ph.gov.doh.uhmistrn.ahsr.webservice.index.SubmittedReportsResponse

import java.time.Instant

@Component
@GraphQLApi
class SubmittedReportsServices {

	@Autowired
	SubmittedReportRepository submittedReportRepository

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	SOAPConnector soapConnector

	@Autowired
	HospitalConfigService hospitalConfigService

	@GraphQLQuery(name = "findAllSubmittedReports", description = "Find all classification")
	List<SubmittedReports> findAllSubmittedReports() {
		return submittedReportRepository.findAllSubmittedReports()
	}

	//==================================Mutation ============
	@GraphQLMutation
	def postSubmittedReports(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		if (id) {
			def submittedReports = submittedReportRepository.findById(id).get()
			objectMapper.updateValue(submittedReports, fields)
//			submittedReports.submittedDateTime = Instant.now()

			return submittedReportRepository.save(submittedReports)
		} else {

			def submittedReports = objectMapper.convertValue(fields, SubmittedReports)

			return submittedReportRepository.save(submittedReports)
		}
	}

	@GraphQLMutation(name = "postSubmittedReportsDoh")
	GraphQLRetVal<String> postSubmittedReportsDoh(@GraphQLArgument(name = 'fields') Map<String, Object> fields){

		try {
			ph.gov.doh.uhmistrn.ahsr.webservice.index.SubmittedReports request = new ph.gov.doh.uhmistrn.ahsr.webservice.index.SubmittedReports()

			request.hfhudcode = hospitalConfigService.hospitalInfo.hfhudcode?:""
			request.reportingstatus = fields.get('reportingStatus') as String
			request.reportedby = fields.get('reportedBy') as String
			request.designation = fields.get('designation') as String
			request.section = fields.get('sections') as String
			request.reportingyear =  fields.get("reportingYear") as Integer
			request.department = fields.get('department') as String

			SubmittedReportsResponse response =
					(SubmittedReportsResponse) soapConnector.callWebService("http://uhmistrn.doh.gov.ph/ahsr/webservice/index.php/submittedReports", request)

			UUID id = UUID.fromString(fields.get('id') as String)
			SubmittedReports dto = submittedReportRepository.getOne(id)
			dto.dohResponse = response.return

			submittedReportRepository.save(dto)

			return new GraphQLRetVal<String>(response.return, true)

		}catch (Exception e){
			return new GraphQLRetVal<String>(e.message, false)
		}
	}
}
