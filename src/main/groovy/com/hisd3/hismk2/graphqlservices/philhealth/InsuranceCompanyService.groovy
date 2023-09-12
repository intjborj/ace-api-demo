package com.hisd3.hismk2.graphqlservices.philhealth

import com.hisd3.hismk2.domain.philhealth.InsuranceCompany
import com.hisd3.hismk2.repository.philhealth.InsuranceCompanyRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class InsuranceCompanyService {
	
	@Autowired
	private InsuranceCompanyRepository insuranceCompanyRepository
	
	//============== All Queries ====================
	
	@GraphQLQuery(name = "insuranceCompanies", description = "Get all insurance companies")
	List<InsuranceCompany> getInsuranceCompanies() {
		return insuranceCompanyRepository.findAll()
	}
	
	@GraphQLQuery(name = "getInsurancesByFilter", description = "Get Insurances by filter")
	List<InsuranceCompany> getInsurancesByFilter(@GraphQLArgument(name = "filter") String filter) {
		insuranceCompanyRepository.getInsurancesByFilter(filter).sort { it.companyName }
	}
}
