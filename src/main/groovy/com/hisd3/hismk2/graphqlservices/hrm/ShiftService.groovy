package com.hisd3.hismk2.graphqlservices.hrm

import com.hisd3.hismk2.domain.hrm.Shift
import com.hisd3.hismk2.repository.hrm.ShiftRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class ShiftService {
	
	@Autowired
	private ShiftRepository shiftRepository
	
	//============== All Queries ====================
	
	@GraphQLQuery(name = "shifts", description = "Get all shifts")
	List<Shift> findAll() {
		return shiftRepository.findAll().sort { it.createdDate }
	}
	
	@GraphQLQuery(name = "shift", description = "Get Shift By Id")
	Shift findById(@GraphQLArgument(name = "id") UUID id) {
		return shiftRepository.findById(id).get()
	}
}
