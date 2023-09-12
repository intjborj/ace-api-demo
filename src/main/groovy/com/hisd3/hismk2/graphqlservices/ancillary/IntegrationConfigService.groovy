package com.hisd3.hismk2.graphqlservices.ancillary

import com.hisd3.hismk2.dao.ancillary.IntegrationConfigDao
import com.hisd3.hismk2.domain.ancillary.IntegrationConfig
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class IntegrationConfigService {
	
	@Autowired
	IntegrationConfigDao integrationConfigDao
	
	@GraphQLQuery(name = "IntegrationConfig", description = "Get Active Config")
	List<IntegrationConfig> getActiveIntegration() {
		
		return integrationConfigDao.getActiveIntegration()
	}
}
