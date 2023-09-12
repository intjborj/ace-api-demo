package com.hisd3.hismk2.graphqlservices.inventory

import com.hisd3.hismk2.domain.inventory.DepartmentStockIssueItems
import com.hisd3.hismk2.domain.inventory.DepartmentStockRequestItem
import com.hisd3.hismk2.repository.inventory.DepartmentStockIssueItemRepository
import com.hisd3.hismk2.rest.dto.QueryErrorException
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import java.time.Instant
import java.time.ZoneId

@Component
@GraphQLApi
@TypeChecked
class DepartmentStockIssueItemService {
	
	@Autowired
	DepartmentStockIssueItemRepository departmentStockIssueItemRepository
	
	//
	@GraphQLQuery(name = "departmentStockIssueItems", description = "List of Departmental Request Items")
	List<DepartmentStockIssueItems> getDepartmentStockRequestItems() {
		return departmentStockIssueItemRepository.findAll().sort { it.createdDate }.reverse(true)
	}
	
	@GraphQLQuery(name = "StockIssueItemsById", description = "List of Departmental Request items By Issue")
	List<DepartmentStockIssueItems> getStockRequestItemsByIssue(@GraphQLArgument(name = "id") UUID id) {
		return departmentStockIssueItemRepository.findItemsByIssue(id).sort{it.item.descLong}
	}
	
	@GraphQLQuery(name = "ItemExpense", description = "List of item expense")
	List<DepartmentStockIssueItems> getItemExpensesPerDateRange(@GraphQLArgument(name = "start") Instant start,
	                                                            @GraphQLArgument(name = "end") Instant end,
	                                                            @GraphQLArgument(name = "expenseFrom") UUID expenseFrom,
	                                                            @GraphQLArgument(name = "filter") String filter) {
		
		Instant fromDate = start.atZone(ZoneId.systemDefault()).toInstant()
		Instant toDate = end.atZone(ZoneId.systemDefault()).toInstant()
		
		return departmentStockIssueItemRepository.getItemExpense(fromDate, toDate, 'Expense', filter, expenseFrom)
	}

	@GraphQLQuery(name = "ItemIssue", description = "List of item issue")
	List<DepartmentStockIssueItems> getItemIssuePerDateRange(@GraphQLArgument(name = "start") Instant start,
																@GraphQLArgument(name = "end") Instant end,
																@GraphQLArgument(name = "expenseFrom") UUID expenseFrom,
																@GraphQLArgument(name = "filter") String filter) {

		Instant fromDate = start.atZone(ZoneId.systemDefault()).toInstant()
		Instant toDate = end.atZone(ZoneId.systemDefault()).toInstant()

		return departmentStockIssueItemRepository.getItemExpense(fromDate, toDate, 'Stock', filter, expenseFrom)
	}

	//code ni wilson
	@GraphQLQuery(name = "getIssuanceItemsForPosting", description = "List of Departmental Request items By Issue")
	List<DepartmentStockIssueItems> getIssuanceItemsForPosting(@GraphQLArgument(name = "id") UUID id) {
		return departmentStockIssueItemRepository.getIssuanceItemsForPosting(id).sort{it.item.descLong}
	}

	//code ni wilson
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "rmDepIssueItem")
	DepartmentStockIssueItems rmDepIssueItem(
			@GraphQLArgument(name = "id") UUID id
	) {
		def upsert = departmentStockIssueItemRepository.findById(id).get()
		departmentStockIssueItemRepository.delete(upsert)
		return upsert
	}
}
