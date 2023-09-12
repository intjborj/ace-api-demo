package com.hisd3.hismk2.graphqlservices.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.inventory.ItemGroup
import com.hisd3.hismk2.domain.inventory.SupplierItem
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.inventory.ItemGroupRepository
import com.hisd3.hismk2.repository.inventory.ItemRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLContext
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@GraphQLApi
@TypeChecked
class ItemGroupService {
	
	@Autowired
	ItemGroupRepository itemGroupRepository
	
	@Autowired
	ItemRepository itemRepository

	@Autowired
	ObjectMapper objectMapper
	
	@GraphQLQuery(name = "itemGroupList", description = "List of Item Groups")
	List<ItemGroup> getItemGroups() {
		return itemGroupRepository.findAll().sort { it.itemDescription }
	}
	
	@GraphQLQuery(name = "itemGroupFilter", description = "List of Item Groups")
	List<ItemGroup> itemGroupFilter(@GraphQLArgument(name = "filter") String filter) {
		return itemGroupRepository.itemGroupFilter(filter).sort { it.itemCode }
	}
	
	@GraphQLQuery(name = "itemGroupActive", description = "List of Active Item Groups")
	List<ItemGroup> itemGroupActive() {
		return itemGroupRepository.itemGroupActive().sort { it.itemCode }
	}
	
	//validation
	@GraphQLQuery(name = "isItemGroupCodeUnique", description = "Check if Item Group exists")
	Boolean findOneByItemGroupCode(@GraphQLArgument(name = "itemCode") String itemCode) {
		return itemGroupRepository.findOneByItemGroupCode(itemCode)
	}
	
	@GraphQLQuery(name = "isItemGroupNameUnique", description = "Check if itemGroupName exists")
	Boolean findOneByItemGroupName(@GraphQLArgument(name = "itemDescription") String itemDescription) {
		return itemGroupRepository.findOneByItemGroupName(itemDescription)

	}
	
	@GraphQLQuery(name = "items", description = "List of Item Category items")
	Set<Item> findByItemGroup(@GraphQLContext ItemGroup itemGroup) {
		return itemRepository.findByItemGroup(itemGroup.id).sort({ it.descLong }) as Set
	}

	//mutation
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertItemGroup", description = "upsert Department Item")
	GraphQLRetVal<Boolean> upsertItemGroup(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "id") UUID id
	) {
		ItemGroup group = new ItemGroup()
		def obj = objectMapper.convertValue(fields, ItemGroup.class)
		def result = new GraphQLRetVal<Boolean>(true,true,"Item Group Added")
		def checkCode = this.findOneByItemGroupCode(obj.itemCode)
		def checkDesc = this.findOneByItemGroupName(obj.itemDescription)
		if(id){
			group = itemGroupRepository.findById(id).get()
			group.itemCode = obj.itemCode
			group.itemDescription = obj.itemDescription
			group.isActive = obj.isActive
			itemGroupRepository.save(group)
			result = new GraphQLRetVal<Boolean>(true,true,"Item Group Updated")
		}else{
			if(checkCode || checkDesc){
				result =  new GraphQLRetVal<Boolean>(false,false,"Item Code or Description already exist")
			}else{
				group.itemCode = obj.itemCode
				group.itemDescription = obj.itemDescription
				group.isActive = obj.isActive
				itemGroupRepository.save(group)
			}
		}
		return result
	}

}
