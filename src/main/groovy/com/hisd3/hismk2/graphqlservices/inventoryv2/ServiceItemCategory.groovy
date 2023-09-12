package com.hisd3.hismk2.graphqlservices.inventoryv2

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.inventory.ItemCategory
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.inventory.ItemCategoryService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.services.GeneratorService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@GraphQLApi
class ServiceItemCategory extends AbstractDaoService<ItemCategory> {

	ServiceItemCategory() {
		super(ItemCategory.class)
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
	ItemCategoryService itemCategoryService

	@GraphQLQuery(name = "itemCategoryPage")
	Page<ItemCategory> itemCategoryPage(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "group") UUID group,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		String query = '''Select ic from ItemCategory ic where
						(lower(ic.categoryDescription) like lower(concat('%',:filter,'%')) or
						lower(ic.categoryCode) like lower(concat('%',:filter,'%'))) '''

		String countQuery = '''Select count(ic) from ItemCategory ic where
						(lower(ic.categoryDescription) like lower(concat('%',:filter,'%')) or
						lower(ic.categoryCode) like lower(concat('%',:filter,'%'))) '''

		Map<String, Object> params = new HashMap<>()
		params.put('filter', filter)

		if (group) {
			query += ''' and (ic.itemGroup.id = :group)'''
			countQuery += ''' and (ic.itemGroup.id = :group)'''
			params.put("group", group)
		}

		query += ''' ORDER BY ic.categoryCode ASC'''

		Page<ItemCategory> result = getPageable(query, countQuery, page, size, params)
		return result
	}

	//Mutation
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertItemCategory", description = "insert Item")
	GraphQLRetVal<Boolean> upsertItemCategory(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "id") UUID id
	) {
		def obj = objectMapper.convertValue(fields, ItemCategory.class)
		def checkCode = itemCategoryService.findOneByItemCategoryCode(obj.categoryCode)
		def checkDesc = itemCategoryService.findOneByItemCategoryName(obj.categoryDescription)

		def result = new GraphQLRetVal<Boolean>(true,true,"Item Category Added")
		if(id){
			upsertFromMap(id, fields, { ItemCategory entity, boolean forInsert ->

			})
			result = new GraphQLRetVal<Boolean>(true,true,"Item Category Updated")
		}else{
			if(checkCode || checkDesc){
				result = new GraphQLRetVal<Boolean>(false,false,"Item Category Code or Description is already exist")
			}else{
				upsertFromMap(id, fields, { ItemCategory entity, boolean forInsert ->

				})
			}
		}
		return result
	}
}
