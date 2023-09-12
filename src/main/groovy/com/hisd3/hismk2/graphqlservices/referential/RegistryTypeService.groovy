package com.hisd3.hismk2.graphqlservices.referential

import com.hisd3.hismk2.domain.referential.RegistryType
import com.hisd3.hismk2.repository.referential.RegistryTypeRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@TypeChecked
@GraphQLApi
@Component
class RegistryTypeService {
	@Autowired
	private RegistryTypeRepository registryTypeRepository
	
	@GraphQLQuery(name = "getRegistryTypes", description = "Get all Registry Types")
	List<RegistryType> getRegistryTypes() {
		return registryTypeRepository.findAll()
	}
}
