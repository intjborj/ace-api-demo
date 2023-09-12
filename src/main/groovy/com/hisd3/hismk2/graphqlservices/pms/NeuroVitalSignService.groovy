package com.hisd3.hismk2.graphqlservices.pms

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.NeuroVitalSign
import com.hisd3.hismk2.repository.pms.NeuroVitalSignRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.sql.Timestamp

@TypeChecked
@Component
@GraphQLApi
class NeuroVitalSignService {
	
	@Autowired
	private NeuroVitalSignRepository neuroVitalSignRepository
	
	@Autowired
	ObjectMapper objectMapper
	
	//============== All Queries ====================
	
	@GraphQLQuery(name = "neuroVitalSigns", description = "Get all NeuroVitalSigns")
	List<NeuroVitalSign> findAll() {
		return neuroVitalSignRepository.findAll().sort { it.entryDateTime }
	}
	
	@GraphQLQuery(name = "neuroVitalSign", description = "Get NeuroVitalSign By Id")
	NeuroVitalSign findById(@GraphQLArgument(name = "id") UUID id) {
		return neuroVitalSignRepository.findById(id).get()
	}
	
	@GraphQLQuery(name = "neuroVitalSignsByCase", description = "Get all NeuroVitalSigns by Case Id")
	List<NeuroVitalSign> getNeuroVitalSignsByCase(@GraphQLArgument(name = "caseId") UUID caseId) {
		return neuroVitalSignRepository.getNeuroVitalSignsByCase(caseId)
	}
	
	@GraphQLQuery(name = "latestNeuroVitalSign", description = "Get latest NeuroVitalSign")
	NeuroVitalSign getLatest(@GraphQLArgument(name = "caseId") UUID caseId) {
		return neuroVitalSignRepository.getLatest(caseId)
	}
	
	@GraphQLMutation
	NeuroVitalSign addNeuroVitalSignsForFlutter(
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		return neuroVitalSignRepository.save(
				new NeuroVitalSign().tap {
					entryDateTime = Timestamp.valueOf(fields.get("entryDateTime") as String).toInstant()
					employee = objectMapper.convertValue(fields.get("employee"), Employee)
					parentCase = objectMapper.convertValue(fields.get("parentCase"), Case)
				}
		)
	}
}
