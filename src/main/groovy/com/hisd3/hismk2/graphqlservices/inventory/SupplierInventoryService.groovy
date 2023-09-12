package com.hisd3.hismk2.graphqlservices.inventory

import com.hisd3.hismk2.domain.User
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.inventory.SupplierInventory
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.GeneratorService
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
@GraphQLApi
@TypeChecked
class SupplierInventoryService extends AbstractDaoService<SupplierInventory> {

	SupplierInventoryService() {
		super(SupplierInventory.class)
	}

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	GeneratorService generatorService

	@Autowired
	UserRepository userRepository

	@Autowired
	EmployeeRepository employeeRepository


	@GraphQLQuery(name = "inventorySupplierListPageable")
	Page<SupplierInventory> inventorySupplierListPageable(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "supplier") UUID supplier,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {
		User user = userRepository.findOneByLogin(SecurityUtils.currentLogin())
		Employee employee = employeeRepository.findOneByUser(user)

		String query = '''Select inv from SupplierInventory inv where
						lower(concat(inv.descLong,inv.sku)) like lower(concat('%',:filter,'%'))
						and inv.supplier.id = :supplier and (inv.department.id = :dep or inv.department is null)'''

		String countQuery = '''Select count(inv) from SupplierInventory inv where
							lower(concat(inv.descLong,inv.sku)) like lower(concat('%',:filter,'%'))
							and inv.supplier.id = :supplier and (inv.department.id = :dep or inv.department is null)'''

		Map<String, Object> params = new HashMap<>()
		params.put('filter', filter)
		params.put('supplier', supplier)
		params.put('dep', employee.departmentOfDuty.id)

		query += ''' ORDER BY inv.descLong ASC'''

		Page<SupplierInventory> result = getPageable(query, countQuery, page, size, params)

		return result
	}

}
