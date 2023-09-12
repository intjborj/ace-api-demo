package com.hisd3.hismk2.graphqlservices.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.User
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.inventory.QuantityAdjustmentType
import com.hisd3.hismk2.domain.inventory.Signature
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.rest.dto.QueryErrorException
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.GeneratorService
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@GraphQLApi
@TypeChecked
class QuantityAdjustmentTypeService extends AbstractDaoService<QuantityAdjustmentType> {

	QuantityAdjustmentTypeService() {
		super(QuantityAdjustmentType.class)
	}

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	GeneratorService generatorService

	//===========mutation====================//
	@Transactional(rollbackFor = QueryErrorException.class) //
	@GraphQLMutation(name = "upsertQuantityAdjustmentType", description = "Insert/Update QuantityAdjustmentType")
	GraphQLRetVal<Boolean> upsertQuantityAdjustmentType(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "id") UUID id
	) {

		def forInsert = objectMapper.convertValue(fields, QuantityAdjustmentType.class)
		QuantityAdjustmentType upsert = new QuantityAdjustmentType()
		def checkpoint = this.findBySourceValue(id, forInsert.sourceValue)
		def result = new GraphQLRetVal<Boolean>(true,  true,"Adjustment Type Information Added")
		try {
			if(id) {
				upsert = findOne(id)
				result = new GraphQLRetVal<Boolean>(true,  true,"Adjustment Type Information Updated")
			}
			if(checkpoint){
				result = new GraphQLRetVal<Boolean>(false,  false,"Adjustment Type source value is already taken")
			}else{
				upsert.code = forInsert.code
				upsert.description = forInsert.description
				upsert.is_active = forInsert.is_active
				upsert.flagValue = forInsert.flagValue
				upsert.sourceValue = forInsert.sourceValue
				upsert.reverse = forInsert.reverse
				save(upsert)
			}

			return result

		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}

	}

	@GraphQLQuery(name = "findBySourceValue")
	Long findBySourceValue(@GraphQLArgument(name = "id") UUID id,
						   @GraphQLArgument(name = "source") String source) {
		Map<String, Object> params = new HashMap<>()
		String query = '''Select count(q) from QuantityAdjustmentType q where lower(q.sourceValue) = lower(:source) '''
		if(id){
			query = '''Select count(q) from QuantityAdjustmentType q where q.id NOT IN (:id) and lower(q.sourceValue) = lower(:source) '''
			params.put('id', id)
		}
		params.put('source', source)
		getCount(query, params)
	}

	@GraphQLQuery(name = "quantityAdjustmentTypeList", description = "List of quantity adjustment type")
	List<QuantityAdjustmentType> quantityAdjustmentTypeList() {
		createQuery("Select q from QuantityAdjustmentType q").resultList.sort { it.createdDate }
	}

	@GraphQLQuery(name = "findOneAdjustmentType", description = "find Adjustment Type")
	QuantityAdjustmentType findOneAdjustmentType(@GraphQLArgument(name = "id") UUID id) {
		return this.findOne(id)
	}

	@GraphQLQuery(name = "quantityAdjustmentTypeFilter", description = "List of quantity adjustment type")
	List<QuantityAdjustmentType> quantityAdjustmentTypeList(@GraphQLArgument(name = "filter")String filter) {
		createQuery("Select q from QuantityAdjustmentType q where" +
				"(lower(q.code) like lower(concat('%',:filter,'%')) or lower(q.description) like lower(concat('%',:filter,'%')))"
				,
				[
						filter:filter
				] as Map<String, Object>).resultList.sort { it.createdDate}
	}

	@GraphQLQuery(name = "filterAdjustmentType", description = "List of filtered quantity adjustment type")
	List<QuantityAdjustmentType> filterAdjustmentType(@GraphQLArgument(name = "is_active")Boolean is_active,
													  @GraphQLArgument(name = "filter")String filter) {
		createQuery("Select q from QuantityAdjustmentType q where q.is_active = :is_active and " +
				"(lower(q.code) like lower(concat('%',:filter,'%')) or lower(q.description) like lower(concat('%',:filter,'%')))"
				,
				[
						is_active:is_active,
						filter:filter
				] as Map<String, Object>).resultList.sort { it.createdDate}
	}

}
