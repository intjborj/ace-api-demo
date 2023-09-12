package com.hisd3.hismk2.graphqlservices.inventory

import com.fasterxml.jackson.databind.ObjectMapper

import com.hisd3.hismk2.domain.inventory.Markup
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.services.GeneratorService
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

import java.time.Instant

@Component
@GraphQLApi
@TypeChecked
class MarkupPageableService extends AbstractDaoService<Markup> {

	MarkupPageableService() {
		super(Markup.class)
	}

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	GeneratorService generatorService

	@Autowired
	UserRepository userRepository

	@Autowired
	EmployeeRepository employeeRepository

	@GraphQLQuery(name = "markupPageable")
	Page<Markup> markupPageable(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "group") UUID group,
			@GraphQLArgument(name = "category") List<UUID> category,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		Instant start = Instant.now();

		String query = '''Select mk from Markup mk where
						lower(concat(mk.descLong)) like lower(concat('%',:filter,'%'))
						and mk.active = true'''

		String countQuery = '''Select count(mk) from Markup mk where
							lower(concat(mk.descLong)) like lower(concat('%',:filter,'%'))
							and mk.active = true'''

		Map<String, Object> params = new HashMap<>()
		params.put('filter', filter)

		if (group) {
			query += ''' and mk.item_group.id = :group'''
			countQuery += ''' and mk.item_group.id = :group'''
			params.put("group", group)
		}

		if (category) {
			query += ''' and mk.item_category.id IN (:category)'''
			countQuery += ''' and mk.item_category.id IN (:category)'''
			params.put("category", category)
		}

		query += ''' ORDER BY mk.descLong ASC'''

		Page<Markup> result = getPageable(query, countQuery, page, size, params)
//		Instant end = Instant.now();
//		Duration timeElapsed = Duration.between(start, end);
//		println('time => '+timeElapsed)
		return result
	}

	@GraphQLQuery(name = "markupItemList")
	List<Markup> markupItemList(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "group") UUID group,
			@GraphQLArgument(name = "category") List<UUID> category
	) {


		String query = '''Select mk from Markup mk where
						lower(concat(mk.descLong)) like lower(concat('%',:filter,'%'))
						and mk.active = true'''

		Map<String, Object> params = new HashMap<>()
		params.put('filter', filter)

		if (group) {
			query += ''' and mk.item_group.id = :group'''
			params.put("group", group)
		}

		if (category) {
			query += ''' and mk.item_category.id IN (:category)'''
			params.put("category", category)
		}


		List<Markup> result = createQuery(query,
				params).resultList.sort{it.descLong}


		return result
	}

}
