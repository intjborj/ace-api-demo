package com.hisd3.hismk2.graphqlservices

import com.hisd3.hismk2.domain.Authority
import com.hisd3.hismk2.repository.AuthorityRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class AuthorityService {
	
	@Autowired
	private AuthorityRepository authorityRepository
	
	//============== All Queries ====================
	
	@GraphQLQuery(name = "authorities", description = "Get all Authorities")
	List<Authority> findAll() {
		return authorityRepository.findAll()
	}
}
