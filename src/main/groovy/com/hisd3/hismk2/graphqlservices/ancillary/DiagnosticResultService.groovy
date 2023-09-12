package com.hisd3.hismk2.graphqlservices.ancillary

import com.hisd3.hismk2.domain.ancillary.DiagnosticResult
import com.hisd3.hismk2.repository.ancillary.DiagnosticsResultRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class DiagnosticResultService {
	
	@Autowired
	private DiagnosticsResultRepository diagnosticsResultRepository
	
	//============== All Queries ====================
	
	@GraphQLQuery(name = "diagnosticResults", description = "Get All Diagnostics Results")
	List<DiagnosticResult> findAll() {
		diagnosticsResultRepository.findAll()
	}
	
	@GraphQLQuery(name = "resultsByOrderSlip", description = "Get Results by OrderSilp")
	List<DiagnosticResult> findByOrderSlipItem(@GraphQLArgument(name = "id") UUID id) {
		diagnosticsResultRepository.findByOrderSlipItem(id)
	}

	@GraphQLQuery(name = "resultsByPatientId", description = "Get Results by OrderSilp")
	List<DiagnosticResult> resultsByPatientId(@GraphQLArgument(name = "id") UUID id) {
		diagnosticsResultRepository.resultsByPatienId(id)
	}

	@GraphQLQuery(name = "resultsByCaseId", description = "Get Results by OrderSilp")
	List<DiagnosticResult> resultsByCaseId(@GraphQLArgument(name = "caseId") UUID caseId) {
		def results = diagnosticsResultRepository.resultsByCaseId(caseId)
		return results;
	}

	@GraphQLQuery(name = "resultsDataByService", description = "Get Results by OrderSilp")
	List<DiagnosticResult> resultsDataByService(
			@GraphQLArgument(name = "caseId") UUID caseId,
			@GraphQLArgument(name = "serviceId") UUID serviceId) {
		return diagnosticsResultRepository.resultsDataByService(caseId, serviceId);
	}
}
