package com.hisd3.hismk2.graphqlservices.inventoryv2

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.User
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.inventory.BeginningItem
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.GeneratorService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

@Service
@GraphQLApi
class ServiceBeginningItem extends AbstractDaoService<BeginningItem> {

	ServiceBeginningItem() {
		super(BeginningItem.class)
	}
	
	@Autowired
	ObjectMapper objectMapper
	
	@Autowired
	GeneratorService generatorService
	
	@Autowired
	UserRepository userRepository
	
	@Autowired
	EmployeeRepository employeeRepository

	@GraphQLQuery(name = "beginningItemsPageable")
	Page<BeginningItem> findByFilters(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		String query = '''Select inv from BeginningItem inv where
						(lower(inv.item.descLong) like lower(concat('%',:filter,'%')) or
						lower(inv.item.sku) like lower(concat('%',:filter,'%')))
						and inv.department.id=:departmentid'''

		String countQuery = '''Select count(inv) from BeginningItem inv where
						(lower(inv.item.descLong) like lower(concat('%',:filter,'%')) or
						lower(inv.item.sku) like lower(concat('%',:filter,'%')))
						and inv.department.id=:departmentid'''

		Map<String, Object> params = new HashMap<>()
		params.put('departmentid', id)
		params.put('filter', filter)


		query += ''' ORDER BY inv.item.descLong ASC'''

		Page<BeginningItem> result = getPageable(query, countQuery, page, size, params)
		return result
	}


	@GraphQLQuery(name = "beginningItemsList")
	List<BeginningItem> findByFilters(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "id") UUID id
	) {

		String query = '''Select inv from BeginningItem inv where
						(lower(inv.item.descLong) like lower(concat('%',:filter,'%')) or
						lower(inv.item.sku) like lower(concat('%',:filter,'%')))
						and inv.department.id=:departmentid'''

		Map<String, Object> params = new HashMap<>()
		params.put('departmentid', id)
		params.put('filter', filter)


		List<BeginningItem> result = createQuery(query, params).resultList.sort{it.item.descLong}
		return result
	}

}
