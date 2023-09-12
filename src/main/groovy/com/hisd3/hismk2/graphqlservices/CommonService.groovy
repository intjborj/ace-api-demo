package com.hisd3.hismk2.graphqlservices

import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class CommonService {
	
	@GraphQLQuery
	static String version() {
		return "1.0.0"
	}
}
