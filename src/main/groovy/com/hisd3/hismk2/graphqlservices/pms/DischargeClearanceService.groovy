package com.hisd3.hismk2.graphqlservices.pms

import com.hisd3.hismk2.domain.pms.DischargeClearance
import com.hisd3.hismk2.repository.pms.DischargeClearanceRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import io.lettuce.core.GeoArgs
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class DischargeClearanceService {
	
	@Autowired
	private DischargeClearanceRepository dischargeClearanceRepository
	
	//============== All Queries ====================
	
	@GraphQLQuery(name = "dischargeClearances", description = "Get all DischargeClearance")
	List<DischargeClearance> findAll() {
		return dischargeClearanceRepository.findAll().sort { it.createdDate }
	}
	
	@GraphQLQuery(name = "dischargeClearance", description = "Get DischargeClearance By Id")
	DischargeClearance findById(@GraphQLArgument(name = "id") UUID id) {
		return dischargeClearanceRepository.findById(id).get()
	}
	
	@GraphQLQuery(name = "dischargeClearancesByCase", description = "Get all DischargeClearance by Case Id")
	List<DischargeClearance> dischargeClearancesByCase(@GraphQLArgument(name = "caseId") UUID caseId) {
		return dischargeClearanceRepository.getDischargeClearanceByCase(caseId).sort { it.createdDate }.reverse()
	}
}
