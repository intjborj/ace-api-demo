package com.hisd3.hismk2.graphqlservices.referential

import com.hisd3.hismk2.domain.referential.QualityManagementType
import com.hisd3.hismk2.repository.referential.QualityManagementTypeRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class QualityManagementTypeService {
	
	@Autowired
	private QualityManagementTypeRepository qualityManagementType
	
	@GraphQLQuery(name = "getQualityManagementType", description = "get quality management type")
	List<QualityManagementType> getQualityManagementType(
			@GraphQLArgument(name = "qualityManagementType") String qmtype
	) {
		return qualityManagementType.getQualityManagementType(qmtype)
	}
	
	@GraphQLQuery(name = "getQualityManagement", description = "get quality management title")
	List<QualityManagementType> getQualityManagement() {
		return qualityManagementType.getQualityManagement()
	}
	
	@GraphQLQuery(name = "getQualityManagementDesc", description = "get quality management description")
	List<QualityManagementType> getQualityManagementDesc(
			@GraphQLArgument(name = "qualityManagementDesc") String desc
	) {
		return qualityManagementType.getQualityManagementDesc(desc)
	}
	
	@GraphQLQuery(name = "getQualityManagementTypes", description = "get quality management type")
	List<QualityManagementType> getQualityManagementTypes(
			@GraphQLArgument(name = "filter") String qmtype
	) {
		return qualityManagementType.getQualityManagementTypes(qmtype)
	}
	
}
