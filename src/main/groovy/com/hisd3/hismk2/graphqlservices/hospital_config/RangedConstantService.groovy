package com.hisd3.hismk2.graphqlservices.hospital_config

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hospital_config.RangedConstant
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.hospital_config.RangedConstantsRepository

import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class RangedConstantService {

	@Autowired
	RangedConstantsRepository rangedConstantsRepository

	@Autowired
	ObjectMapper objectMapper

	@GraphQLQuery(name = "rangedConstants", description = "Get all ranged constants")
	List<RangedConstant> rangedConstants() {
		return rangedConstantsRepository.findAll()
	}

	@GraphQLMutation
	GraphQLRetVal<String> upsertRangedConstant(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {

		def rangedConstantObj = null

		if(id) {
			//on edit, it checks if the new fieldname already exist
			rangedConstantObj = rangedConstantsRepository.findByOtherFieldName(fields.get("fieldName").toString(), id);
			if(rangedConstantObj) {
				return new GraphQLRetVal<String>("Field name already exist", false)
			} else {
				rangedConstantObj = rangedConstantsRepository.findById(id).get()
			}
		} else {
			//if new, still checking if it exists
			rangedConstantObj = rangedConstantsRepository.findByFieldName(fields.get("fieldName").toString());

			if(!rangedConstantObj)
				rangedConstantObj = new RangedConstant()
			else
				return new GraphQLRetVal<String>("Field name already exist", false)
		}

		objectMapper.updateValue(rangedConstantObj, fields)
		rangedConstantsRepository.save(rangedConstantObj as RangedConstant)

		return new GraphQLRetVal<String>("Range successfully saved", true)
	}
}
