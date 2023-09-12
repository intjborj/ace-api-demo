package com.hisd3.hismk2.graphqlservices.billing

import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.stereotype.Service

enum ValidationSource {
	SENIOR_NO,
	PWD_NO,
	EMPLOYEE_NO,
	NO_VALIDATION
}

@Service
@GraphQLApi
class ValidationUtilServices {

}
