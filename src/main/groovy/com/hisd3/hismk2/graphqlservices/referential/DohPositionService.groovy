package com.hisd3.hismk2.graphqlservices.referential

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.referential.DohPosition
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.referential.DohPositionRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.transaction.Transactional

@TypeChecked
@GraphQLApi
@Component
class DohPositionService {
	@Autowired
	private DohPositionRepository dohPositionRepository

	@Autowired
	ObjectMapper objectMapper

	
	@GraphQLQuery(name = "getDOHPositions", description = "Get all DOH Positions")
	List<DohPosition> getDOHPositions() {
		return dohPositionRepository.getDOHPositions()
	}

	@GraphQLQuery(name = "getOtherPositions", description = "Get all doh positions")
	List<DohPosition> getOtherPositions() {
		return dohPositionRepository.getOtherPositions().sort({it.postdesc})
	}



	@GraphQLMutation
	@Transactional
	GraphQLRetVal<DohPosition> upsertOtherPosition(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "id") UUID id

	) {
		List <DohPosition> positions = dohPositionRepository.findAll()
		List <DohPosition> positionOthersList = dohPositionRepository.findAll()

		Boolean postDescExists = false
		positions.each {
			if(it.postdesc == fields['postdesc']){
				postDescExists = true
			}
		}
		positionOthersList.each {
			if(it.postdesc == fields['postdesc']){
				postDescExists = true
			}
		}

		if(postDescExists)
			return new GraphQLRetVal<DohPosition>(null, false, "Position description already exists..")

		if (id) {
			DohPosition positionOthers = dohPositionRepository.findById(id).get()
			objectMapper.updateValue(positionOthers, fields)
			positionOthers.isOthers = true
			return new GraphQLRetVal<DohPosition>(positionOthers, true, "Updated position successfully.")
		} else {
			DohPosition positionOthers = objectMapper.convertValue(fields, DohPosition)
			positionOthers.isOthers = true
			dohPositionRepository.save(positionOthers)
			return new GraphQLRetVal<DohPosition>(positionOthers, true, "Added position successfully.")
		}

	}


	@GraphQLMutation
	@Transactional
	GraphQLRetVal<DohPosition> updatePositionStatus(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "status") Boolean status

	) {
		DohPosition position = dohPositionRepository.findById(id).get()
		position.isActive = status
		return new GraphQLRetVal<DohPosition>(position, true, "Active status changed successfully.")

	}
}
