package com.hisd3.hismk2.graphqlservices.dietary

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.dietary.DietCategory
import com.hisd3.hismk2.domain.doh.SubmittedReports
import com.hisd3.hismk2.repository.dietary.DietCategoryRepository
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@GraphQLApi
class DietCategoryServices {
	@Autowired
	DietCategoryRepository dietCategoryRepository

	@Autowired
	ObjectMapper objectMapper

	@GraphQLQuery(name = "findAllDietCategory", description = "Find all Diet Category")
	List<DietCategory> findAllDietCategory() {
		return dietCategoryRepository.findAllDietCategory()
	}

	@GraphQLQuery(name = "findAllLikeCategory", description = "Find all Diet Category")
	List<DietCategory> findAllLikeCategory(@GraphQLArgument(name = "filter") String filter) {
		return dietCategoryRepository.findAllLikeCategory(filter).sort { it.createdDate}
	}

	@GraphQLMutation
	def postDietCategory(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		if (id) {
			def dietCategory = dietCategoryRepository.findById(id).get()
			objectMapper.updateValue(dietCategory, fields)
//			submittedReports.submittedDateTime = Instant.now()

			return dietCategoryRepository.save(dietCategory)
		} else {

			def dietCategory = objectMapper.convertValue(fields, DietCategory)

			return dietCategoryRepository.save(dietCategory)
		}
	}
}
