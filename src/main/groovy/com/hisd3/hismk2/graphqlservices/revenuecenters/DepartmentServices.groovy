package com.hisd3.hismk2.graphqlservices.revenuecenters

import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.ancillary.Service
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.ancillary.ServiceRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class DepartmentServices {
	
	@Autowired
	DepartmentRepository departmentRepository
	
	@Autowired
	ServiceRepository serviceRepository
	
	List<Department> getLeafDepartment() {
		
		def allDepartments = departmentRepository.findAll()
		
	}
	
	@GraphQLQuery(name = "revenueCenters", description = "Get All Department with Revenue Servers flag on")
	List<Department> revenueCenters() {
		return departmentRepository.getRevenueCenters()
	}
	
	@GraphQLQuery(name = "servicesByDepartment")
	Page<Service> servicesByDepartment(@GraphQLArgument(name = "departmentId") UUID departmentId,
	                                   @GraphQLArgument(name = "filter") String filter,
	                                   @GraphQLArgument(name = "page") Integer page,
	                                   @GraphQLArgument(name = "size") Integer size) {
		
		return serviceRepository.searchlistByDepartmentPageable(departmentId, filter, new PageRequest(page, size))
		
	}
	
}
