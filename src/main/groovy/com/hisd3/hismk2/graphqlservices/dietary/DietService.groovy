package com.hisd3.hismk2.graphqlservices.dietary

import com.hisd3.hismk2.domain.dietary.Diet
import com.hisd3.hismk2.repository.dietary.DietRepository
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
@GraphQLApi
class DietService {
	@Autowired
	DietRepository dietRepository
	
	@GraphQLQuery(name = "diets", description = "Get All Diets")
	List<Diet> findAll() {
		return dietRepository.findAll().sort { it.dietName }
	}
	
	@GraphQLQuery(name = "filterDiets")
	List<Diet> filterAllDiets(@GraphQLArgument(name = "filter") String filter) {
		return dietRepository.filterAllDiets(filter).sort { it.dietName }
	}
	
	@GraphQLQuery(name = "get_all_diets")
	Page<Diet> getAllDiets(@GraphQLArgument(name = "filter") String filter, @GraphQLArgument(name = "page") Integer page, @GraphQLArgument(name = "pageSize") Integer pageSize) {
		return dietRepository.findAllByFilter(filter, new PageRequest(page, pageSize, Sort.Direction.ASC, "dietName"))
	}
	
	@GraphQLMutation(name = "post_diet")
	Diet postDiet(@GraphQLArgument(name = "diet") Diet diet) {
		return dietRepository.save(diet)
	}
	
}
