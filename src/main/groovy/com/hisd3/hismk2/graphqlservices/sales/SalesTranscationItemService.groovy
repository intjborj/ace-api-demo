package com.hisd3.hismk2.graphqlservices.sales

import com.hisd3.hismk2.domain.sales.SalesTransactionItem
import com.hisd3.hismk2.repository.sales.SalesTransactionItemRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@GraphQLApi
@TypeChecked
class SalesTranscationItemService {
	
	@Autowired
	SalesTransactionItemRepository salesTransactionItemRepository
	
	@GraphQLQuery(name = "sales_transaction_items", description = "List of Sale Transaction Items")
	List<SalesTransactionItem> allSalesTransactionItems(
			@GraphQLArgument(name = "filter") String filter
	) {
		return salesTransactionItemRepository.findAllByFilter(filter)
	}
	
	@GraphQLQuery(name = "sales_transaction_item", description = "Sales Transaction")
	SalesTransactionItem getTransactionItem(
			@GraphQLArgument(name = "id") String id
	) {
		return salesTransactionItemRepository.findById(UUID.fromString(id)).get()
	}
	
}
