package com.hisd3.hismk2.graphqlservices.pms

import com.hisd3.hismk2.domain.pms.CaseInsurance
import com.hisd3.hismk2.repository.pms.CaseInsuranceRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class CaseInsuranceService {
	
	@Autowired
	private CaseInsuranceRepository caseInsuranceRepository
	
	//============== All Queries ====================
	
	@GraphQLQuery(name = "getCaseInsurancesByCase", description = "Get all insurance by case")
	List<CaseInsurance> getCaseInsurancesByCase(@GraphQLArgument(name = "caseId") UUID caseId) {
		return caseInsuranceRepository.getCaseInsurancesByCase(caseId)
	}
	
}
