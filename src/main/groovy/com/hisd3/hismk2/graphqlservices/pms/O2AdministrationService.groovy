package com.hisd3.hismk2.graphqlservices.pms

import com.hisd3.hismk2.domain.pms.O2Administration
import com.hisd3.hismk2.repository.pms.O2AdministrationRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class O2AdministrationService {
	
	@Autowired
	private O2AdministrationRepository o2AdministrationRepository
	
	//============== All Queries ====================
	
	@GraphQLQuery(name = "o2_administrations", description = "Get all o2_administrations")
	List<O2Administration> o2_administrations() {
		return o2AdministrationRepository.findAll()
	}
	//============== All Queries ====================
	
	@GraphQLQuery(name = "case_o2_administrations", description = "Get all o2_administrations by case")
	List<O2Administration> case_o2_administrations(@GraphQLArgument(name = "id") UUID id) {
		return o2AdministrationRepository.findByCase(id)
	}
	
}
