package com.hisd3.hismk2.graphqlservices.philhealth

import com.hisd3.hismk2.domain.philhealth.PhicGroup
import com.hisd3.hismk2.repository.philhealth.PhicGroupRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class PhicGroupService {
	
	@Autowired
	private PhicGroupRepository phicGroupRepository
	
	//============== All Queries ====================
	
	@GraphQLQuery(name = "getPHICGroup", description = "Get All PHIC Group")
	List<PhicGroup> getPHICGroup() {
		return phicGroupRepository.findAll()
	}
	
}
