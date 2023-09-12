package com.hisd3.hismk2.graphqlservices.inventory

import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.inventory.ItemCategory
import com.hisd3.hismk2.repository.inventory.ItemCategoryRepository
import com.hisd3.hismk2.repository.inventory.ItemRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLContext
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@GraphQLApi
@TypeChecked
class ItemCategoryService {
	
	@Autowired
	ItemCategoryRepository itemCategoryRepository
	
	@Autowired
	ItemRepository itemRepository
	
	@GraphQLQuery(name = "itemCategoryList", description = "List of Item Category")
	List<ItemCategory> itemCategoryActive() {
		return itemCategoryRepository.itemCategoryActive().sort { it.categoryDescription }
	}
	
	@GraphQLQuery(name = "itemCategoryByGroup", description = "List of Category by Group") //active
	List<ItemCategory> findAllByIdGroup(
			@GraphQLArgument(name = "id") UUID id
	) {
		return itemCategoryRepository.findAllByIdGroup(id).sort { it.categoryDescription }
	}
	
	@GraphQLQuery(name = "itemCategoryList", description = "List of Item Category")
	List<ItemCategory> getItemGroups() {
		return itemCategoryRepository.findAll().sort { it.categoryDescription }
	}
	
	@GraphQLQuery(name = "itemCategoryFilter", description = "List of Item Category")
	List<ItemCategory> itemCategoryFilter(@GraphQLArgument(name = "filter") String filter) {
		return itemCategoryRepository.itemCategoryFilter(filter).sort { it.categoryDescription }
	}
	
	//validation
	@GraphQLQuery(name = "isItemCategoryCodeUnique", description = "Check if Item Category Code exists")
	Boolean findOneByItemCategoryCode(@GraphQLArgument(name = "categoryCode") String categoryCode) {
		return itemCategoryRepository.findOneByItemCategoryCode(categoryCode)
	}
	
	@GraphQLQuery(name = "isItemCategoryNameUnique", description = "Check if itemCategoryName exists")
	Boolean findOneByItemCategoryName(@GraphQLArgument(name = "categoryDescription") String categoryDescription) {
		return itemCategoryRepository.findOneByItemCategoryName(categoryDescription)
	}
	
	@GraphQLQuery(name = "items", description = "List of Item Group items")
	Set<Item> findByItemCategory(@GraphQLContext ItemCategory itemCategory) {
		return itemRepository.findByItemCategory(itemCategory.id).sort({ it.descLong }) as Set
	}
}
