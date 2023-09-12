package com.hisd3.hismk2.graphqlservices.inventoryv2

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.inventory.BeginningItem
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.inventory.ItemCategory
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.inventory.DepartmentItemService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.services.GeneratorService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@GraphQLApi
class ServiceItem extends AbstractDaoService<Item> {

	ServiceItem() {
		super(Item.class)
	}
	
	@Autowired
	ObjectMapper objectMapper
	
	@Autowired
	GeneratorService generatorService
	
	@Autowired
	UserRepository userRepository
	
	@Autowired
	EmployeeRepository employeeRepository

	@Autowired
	DepartmentItemService departmentItemService

	@GraphQLQuery(name = "findDuplicateItem")
	Long findDuplicateItem(
			@GraphQLArgument(name = "desc") String desc,
			@GraphQLArgument(name = "sku") String sku,
			@GraphQLArgument(name = "itemCode") String itemCode,
			@GraphQLArgument(name = "id") UUID id
	) {

		String query = '''Select count(item) from Item item where 
						(lower(item.descLong) = lower(:desc) or 
						lower(item.sku) = lower(:sku) or 
						lower(item.itemCode) = lower(:itemCode)) '''

		Map<String, Object> params = new HashMap<>()
		params.put('desc', desc)
		params.put('sku', sku)
		params.put('itemCode', itemCode)

		if(id){
			query += ''' and item.id not in (:id)'''
			params.put('id', id)
		}

		getCount(query, params)
	}

	//Mutation
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertItem", description = "insert Item")
	GraphQLRetVal<Boolean> upsertItem(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "id") UUID id
	) {
		def result = new GraphQLRetVal<Boolean>(true,  true,"Item information added")

		String descLong = fields['descLong'] as String
		String sku = fields['sku'] as String
		String itemCode = fields['itemCode'] as String

		def checkpoint = null
		if(descLong && sku && itemCode){
			checkpoint = this.findDuplicateItem(descLong, sku, itemCode, id)
		}
		//upsert
		if(checkpoint){
			result = new GraphQLRetVal<Boolean>(false,  false,"Duplicate Item. Item is already exists, Please check description, barcode and item code for duplicate.")
		}else{
			upsertFromMap(id, fields, { Item entity, boolean forInsert -> })
			if(id){
				result = new GraphQLRetVal<Boolean>(true,  true,"Item information updated")
			}
		}

		return result
	}
}
