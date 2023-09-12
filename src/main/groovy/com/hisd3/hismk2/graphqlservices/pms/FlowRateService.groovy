package com.hisd3.hismk2.graphqlservices.pms

import com.hisd3.hismk2.domain.pms.FlowRate
import com.hisd3.hismk2.repository.pms.FlowRateRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class FlowRateService {
	
	@Autowired
	private FlowRateRepository flowRateRepository
	
	//============== All Queries ====================
	
	@GraphQLQuery(name = "o2FlowRates", description = "Get all O2 flow rates")
	List<FlowRate> findAll() {
		return flowRateRepository.findAll().sort { it.literPerMinute }
	}
	
	@GraphQLQuery(name = "filterAllO2FlowRates", description = "List of O2 flow rates")
	List<FlowRate> filterAllO2FlowRates(@GraphQLArgument(name = "filter") String filter) {
		return flowRateRepository.filterAllO2FlowRates(filter).sort { it.literPerMinute }
	}
}
