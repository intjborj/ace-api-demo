package com.hisd3.hismk2.graphqlservices.philhealth

import com.hisd3.hismk2.domain.philhealth.RVSCode
import com.hisd3.hismk2.repository.philhealth.RVSCodesRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class RvsCodesService {
	
	@Autowired
	private RVSCodesRepository rvsCodesRepository
	
	//============== All Queries ====================
	
	@GraphQLQuery(name = "rvs_codes", description = "Get All RVS Codes")
	Set<RVSCode> getRVSCodes() {
		return rvsCodesRepository.getRVSCodes()
	}
	
	@GraphQLQuery(name = "searchRVSCodes", description = "search RVS Codes")
	Set<RVSCode> searchRVSCodes(@GraphQLArgument(name = "filter") String filter) {
		return rvsCodesRepository.searchRVSCodes(filter)
	}
	
	@GraphQLQuery(name = "searchRVSCodesPageable", description = "search RVS Codes")
	Page<RVSCode> searchRVSCodesPageable(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "pageSize") Integer pageSize) {
		return rvsCodesRepository.searchRVSCodesPageable(filter, PageRequest.of(page, pageSize, Sort.Direction.ASC, 'longName'))
	}
}
