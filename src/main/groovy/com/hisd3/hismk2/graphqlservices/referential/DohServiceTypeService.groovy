package com.hisd3.hismk2.graphqlservices.referential

import com.hisd3.hismk2.domain.referential.DohServiceType
import com.hisd3.hismk2.repository.referential.DohServiceTypeRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@TypeChecked
@GraphQLApi
@Component
class DohServiceTypeService {
	@Autowired
	private DohServiceTypeRepository dohServiceTypeRepository
	
	@GraphQLQuery(name = "getDOHServiceTypes", description = "Get all DOH Service Types")
	List<DohServiceType> getDOHPositions() {
		return dohServiceTypeRepository.getDOHServiceTypes().sort { it.tscode }
	}
}
