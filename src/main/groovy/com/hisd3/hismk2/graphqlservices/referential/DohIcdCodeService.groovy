package com.hisd3.hismk2.graphqlservices.referential

import com.hisd3.hismk2.domain.referential.DohIcdCategory
import com.hisd3.hismk2.domain.referential.DohIcdCode
import com.hisd3.hismk2.repository.referential.DohIcdCategoryRepository
import com.hisd3.hismk2.repository.referential.DohIcdCodeRepository
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
@GraphQLApi
@Component
class DohIcdCodeService {
	
	@Autowired
	private DohIcdCodeRepository dohICDCodeRepository
	
	@Autowired
	private DohIcdCategoryRepository dohIcdCategoryRepository
	
	@GraphQLQuery(name = "searchDOHICDCodes", description = "Get All DOH ICD Codes")
	List<DohIcdCode> searchDOHICDCodes(
			@GraphQLArgument(name = "filter") String filter
	) {
		return dohICDCodeRepository.searchDOHICDCodes(filter)
	}
	
	@GraphQLQuery(name = "searchDOHICDCodesPageable", description = "Get All DOH ICD Codes")
	Page<DohIcdCode> searchDOHICDCodesPageable(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "pageSize") Integer pageSize
	) {
		return dohICDCodeRepository.searchDOHICDCodesPageable(filter, PageRequest.of(page, pageSize, Sort.Direction.ASC, 'icdDesc'))
	}
	
	@GraphQLQuery(name = "searchCategories", description = "Get All DOH ICD Categories")
	List<DohIcdCategory> getAllDOHCategories(
			@GraphQLArgument(name = "filter") String filter
	) {
		return dohIcdCategoryRepository.allDohIcdCodeCategory(filter)
	}
	
	@GraphQLQuery(name = "searchCategoriesPageable", description = "Get All DOH ICD Categories")
	Page<DohIcdCategory> getAllDOHCategoriesPageable(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "pageSize") Integer pageSize
	) {
		return dohIcdCategoryRepository.allDohIcdCodeCategoryPageable(filter, PageRequest.of(page, pageSize, Sort.Direction.ASC, 'icdCategoryDesc'))
	}
	
}
