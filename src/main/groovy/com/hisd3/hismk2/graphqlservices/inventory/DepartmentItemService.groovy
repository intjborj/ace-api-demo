package com.hisd3.hismk2.graphqlservices.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.inventory.DepartmentItem
import com.hisd3.hismk2.domain.inventory.InventoryLedger
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.inventory.DepartmentItemRepository
import com.hisd3.hismk2.repository.inventory.ItemRepository
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
class DepartmentItemService {
	
	@Autowired
	DepartmentItemService departmentItemService
	
	@Autowired
	DepartmentItemRepository departmentItemRepository
	
	@Autowired
	ItemRepository itemRepository
	
	@Autowired
	DepartmentRepository departmentRepository
	
	@Autowired
	ObjectMapper objectMapper
	
	// Department Item //
	@GraphQLMutation
	DepartmentItem insertDepartmentItem(
			@GraphQLArgument(name = "fields") ArrayList<Map<String, Object>> fields
	) {
		
		for (value in fields) {
			DepartmentItem depItemObj = departmentItemRepository.findByItemDep(UUID.fromString(value['item'] as String), UUID.fromString(value['department'] as String))
			if (depItemObj) {
				depItemObj.allow_trade = value['allow_trade']
				departmentItemRepository.save(depItemObj)
			} else {
				DepartmentItem deptItem = new DepartmentItem()
				deptItem.item = itemRepository.findById(UUID.fromString(value['item'] as String)).get()
				deptItem.department = departmentRepository.findById(UUID.fromString(value['department'] as String)).get()
				deptItem.allow_trade = value['allow_trade']
				deptItem.is_assign = true
				deptItem.reorder_quantity = BigDecimal.ZERO
				departmentItemRepository.save(deptItem)
			}
		}
	}

	@GraphQLQuery(name = "depListByItem", description = "List of Department By Item")
	List<DepartmentItem> depListByItem(@GraphQLArgument(name = "id") UUID id) {
		return departmentItemRepository.findListByItem(id).sort{it.department.departmentName}
	}

	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertDepItem", description = "upsert Department Item")
	DepartmentItem upsertDepItem(
			@GraphQLArgument(name = "depId") UUID depId,
			@GraphQLArgument(name = "itemId") UUID itemId,
			@GraphQLArgument(name = "trade") Boolean trade,
			@GraphQLArgument(name = "assign") Boolean assign,
			@GraphQLArgument(name = "id") UUID id
	) {
		DepartmentItem deptItem = new DepartmentItem()
		DepartmentItem depItemObj = departmentItemRepository.findByItemDep(itemId, depId)

			if(id){ //update
				deptItem = departmentItemRepository.findById(id).get()
				deptItem.allow_trade = trade
				deptItem.is_assign = assign
				departmentItemRepository.save(deptItem)
			}else{ //insert
				 if(!depItemObj){
					 deptItem.item = itemRepository.findById(itemId).get()
					 deptItem.department = departmentRepository.findById(depId).get()
					 deptItem.allow_trade = trade
					 deptItem.is_assign = assign
					 departmentItemRepository.save(deptItem)
				 }
			}
	}

	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "updateReorder")
	DepartmentItem updateReorder(
			@GraphQLArgument(name = "qty") BigDecimal qty,
			@GraphQLArgument(name = "id") UUID id
	) {
		DepartmentItem deptItem = departmentItemRepository.findById(id).get()
		deptItem.reorder_quantity = qty
		departmentItemRepository.save(deptItem)
	}
	
}
