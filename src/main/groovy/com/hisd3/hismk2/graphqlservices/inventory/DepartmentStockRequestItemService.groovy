package com.hisd3.hismk2.graphqlservices.inventory

import com.hisd3.hismk2.domain.inventory.DepartmentStockRequest
import com.hisd3.hismk2.domain.inventory.DepartmentStockRequestItem
import com.hisd3.hismk2.domain.inventory.PurchaseRequestItem
import com.hisd3.hismk2.repository.inventory.DepartmentStockRequestItemRepository
import com.hisd3.hismk2.rest.dto.QueryErrorException
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
class DepartmentStockRequestItemService {
	
	@Autowired
	DepartmentStockRequestItemRepository departmentStockRequestItemRepository
	
	//
	@GraphQLQuery(name = "departmentStockRequestItems", description = "List of Departmental Request Items")
	List<DepartmentStockRequestItem> getDepartmentStockRequestItems() {
		return departmentStockRequestItemRepository.findAll().sort { it.createdDate }.reverse(true)
	}
	
	@GraphQLQuery(name = "StockRequestItemsById", description = "List of Departmental Request items By Request")
	List<DepartmentStockRequestItem> getStockRequestItemsByDep(@GraphQLArgument(name = "id") UUID id) {
		return departmentStockRequestItemRepository.findItemsByRequest(id)
	}

	//code ni donss
	@GraphQLQuery(name = "StockRequestItemsByStockIssueId", description = "List of Departmental Request items By Stock Issue")
	List<DepartmentStockRequestItem> getStockRequestItemsByStockIssueId(@GraphQLArgument(name = "stock_issue_id") UUID stock_issue_id) {
		return departmentStockRequestItemRepository.findItemsByStockIssueId(stock_issue_id)
	}

	@GraphQLQuery(name = 'getDepRequestItems', description = "get req items")
	List<DepartmentStockRequestItem> getDepRequestItems(@GraphQLArgument(name = "id") UUID id) {
		departmentStockRequestItemRepository.getDepRequestItems(id).sort{it.item.descLong}
	}


	//code ni wilson
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "rmDepStockItem")
	DepartmentStockRequestItem rmDepStockItem(
			@GraphQLArgument(name = "id") UUID id
	) {
		def upsert = departmentStockRequestItemRepository.findById(id).get()
		departmentStockRequestItemRepository.delete(upsert)
		return upsert
	}

}
