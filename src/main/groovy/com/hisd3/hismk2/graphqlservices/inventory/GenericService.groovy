package com.hisd3.hismk2.graphqlservices.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.inventory.Generic
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.inventory.UnitMeasurement
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.inventory.GenericRepository
import com.hisd3.hismk2.repository.inventory.ItemRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLContext
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@GraphQLApi
@TypeChecked
class GenericService {
	
	@Autowired
	GenericRepository genericRepository
	
	@Autowired
	ItemRepository itemRepository

	@Autowired
	ObjectMapper objectMapper
	
	@GraphQLQuery(name = "genericList", description = "List of Generics")
	List<Generic> getGenerics() {
		return genericRepository.findAll().sort { it.genericDescription }
	}
	
	@GraphQLQuery(name = "genericFilter", description = "List of Generics")
	List<Generic> genericFilter(@GraphQLArgument(name = "filter") String filter) {
		return genericRepository.genericFilter(filter).sort { it.genericDescription }
	}
	
	@GraphQLQuery(name = "genericActive", description = "List of Active Generics")
	List<Generic> genericActive() {
		return genericRepository.genericActive().sort { it.genericDescription }
	}
	
	@GraphQLQuery(name = "genericActiveFilter", description = "List of Active Generics")
	Page<Generic> genericActiveFilter(@GraphQLArgument(name = "page") Integer page, // zero based
	                                  @GraphQLArgument(name = "size") Integer pageSize,
	                                  @GraphQLArgument(name = "filter") String filter) {
		
		return genericRepository.genericActiveFilter(filter, new PageRequest(page, pageSize, Sort.Direction.ASC, "genericDescription"))
	}

	@GraphQLQuery(name = "genericPageFilter", description = "List of Page Generics")
	Page<Generic> genericPageFilter(@GraphQLArgument(name = "page") Integer page, // zero based
									  @GraphQLArgument(name = "size") Integer pageSize,
									  @GraphQLArgument(name = "filter") String filter) {

		return genericRepository.genericPageFilter(filter, new PageRequest(page, pageSize, Sort.Direction.ASC, "genericCode"))
	}
	
	//validation
	@GraphQLQuery(name = "isGenericCodeUnique", description = "Check if Generic Code exists")
	Boolean findOneByGenericCode(@GraphQLArgument(name = "genericCode") String genericCode) {
		return !genericRepository.findOneByGenericCode(genericCode)
	}
	
	@GraphQLQuery(name = "isGenericNameUnique", description = "Check if Generic Name exists")
	Boolean findOneByGenericName(@GraphQLArgument(name = "genericDescription") String genericDescription) {
		return !genericRepository.findOneByGenericName(genericDescription)
	}
	
	@GraphQLQuery(name = "items", description = "List of Item Generic items")
	Set<Item> findByItemGeneric(@GraphQLContext Generic itemGeneric) {
		return itemRepository.findByItemGeneric(itemGeneric.id).sort({ it.descLong }) as Set
	}

	//mutation
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertGenerics", description = "upsert Unit")
	GraphQLRetVal<Boolean> upsertGenerics(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "id") UUID id
	) {
		Generic gen = new Generic()
		def obj = objectMapper.convertValue(fields, Generic.class)
		def result = new GraphQLRetVal<Boolean>(true,true,"Item Generic Added")
		def checkCode = genericRepository.findOneByGenericCode(obj.genericCode)
		def checkDesc = genericRepository.findOneByGenericName(obj.genericDescription)
		if(id){
			gen = genericRepository.findById(id).get()
			gen.genericCode = obj.genericCode
			gen.genericDescription = obj.genericDescription
			gen.isActive = obj.isActive
			genericRepository.save(gen)
			result = new GraphQLRetVal<Boolean>(true,true,"Item Generic Updated")
		}else{
			if(checkCode || checkDesc){
				result =  new GraphQLRetVal<Boolean>(false,false,"Item Generic Code or Description already exist")
			}else{
				gen.genericCode = obj.genericCode
				gen.genericDescription = obj.genericDescription
				gen.isActive = obj.isActive
				genericRepository.save(gen)
			}
		}
		return result
	}
}
