package com.hisd3.hismk2.graphqlservices.referential

import com.google.common.graph.Graph
import com.hisd3.hismk2.domain.referential.DohSurgeryCode
import com.hisd3.hismk2.repository.referential.DohSurgeryCodeRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.lucene.search.Sort
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@TypeChecked
@GraphQLApi
@Component
class DohSurgeryCodeService {
	
	@Autowired
	private DohSurgeryCodeRepository dohSurgeryCodeRepository
	
	@GraphQLQuery(name = "searchDOHSurgeryCodes", description = "Get All DOH Surgery Codes")
	List<DohSurgeryCode> searchDOHSurgeryCodes(
			@GraphQLArgument(name = "filter") String filter
	) {
		return dohSurgeryCodeRepository.searchDOHSurgeryCodes(filter)
	}
	@GraphQLQuery(name = "searchDOHSurgeryCodesPageable", description = "Get All DOH Surgery Codes")
	Page<DohSurgeryCode> searchDOHSurgeryCodes(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = 'page') Integer page,
			@GraphQLArgument(name = 'pageSize') Integer pageSize,
			@GraphQLArgument(name = 'sort') String sort,
			@GraphQLArgument(name = 'sortBy') String sortBy
	) {
		def sortDirection = Sort.Direction.ASC

		if(sort == 'desc'){
			sortDirection = Sort.Direction.DESC
		}

		Pageable pageable = PageRequest.of(page, pageSize, sortDirection, sortBy)

		return dohSurgeryCodeRepository.searchDOHSurgeryCodesPageable(filter, pageable)
	}
}