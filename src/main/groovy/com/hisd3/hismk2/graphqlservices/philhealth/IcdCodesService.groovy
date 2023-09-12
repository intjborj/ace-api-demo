package com.hisd3.hismk2.graphqlservices.philhealth

import com.hisd3.hismk2.domain.bms.ICDCode
import com.hisd3.hismk2.repository.philhealth.ICDCodesRepository
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
class IcdCodesService {
	
	@Autowired
	private ICDCodesRepository icdCodesRepository
	
	@GraphQLQuery(name = "searchICDCodes", description = "search ICD Codes")
	Set<ICDCode> searchICDCodes(@GraphQLArgument(name = "filter") String filter) {
		return icdCodesRepository.searchICDCodes(filter)
	}
	
	@GraphQLQuery(name = "searchICDCodesPageable", description = "search ICD Codes")
	Page<ICDCode> searchICDCodesPageable(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "pageSize") Integer pageSize
	) {
		return icdCodesRepository.searchICDCodesPageable(filter, PageRequest.of(page, pageSize, Sort.Direction.DESC, 'primaryAmount1'))
	}
}
