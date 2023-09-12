package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.ExpenseTransaction
import com.hisd3.hismk2.domain.accounting.TransactionType
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
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
class ExpenseTransactionService extends AbstractDaoService<ExpenseTransaction> {
	//transaction Type for Petty Cash and Disbursement (This are the for the dynamic accounts targeting)

	@Autowired
	GeneratorService generatorService

    ExpenseTransactionService() {
		super(ExpenseTransaction.class)
	}
	
	@GraphQLQuery(name = "expenseTypeById")
	ExpenseTransaction expenseTypeById(
			@GraphQLArgument(name = "id") UUID id
	) {
		findOne(id)
	}
	
	@GraphQLQuery(name = "exTransList", description = "Transaction List")
	List<ExpenseTransaction> exTransList() {
		findAll().sort { it.description }
	}
	
	@GraphQLQuery(name = "transTypeByType", description = "transaction type by type")
	List<ExpenseTransaction> transTypeByType(@GraphQLArgument(name = "type") String type,
											 @GraphQLArgument(name = "filter") String filter) {
		createQuery("Select f from ExpenseTransaction f where f.type = :type and lower(f.description) like lower(concat('%',:filter,'%'))",
				[type: type, filter: filter]).resultList.sort { it.source }
	}
	
	//mutation
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertExTransType", description = "insert TransType")
	ExpenseTransaction upsertExTransType(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "id") UUID id
	) {
		
		upsertFromMap(id, fields, { ExpenseTransaction entity, boolean forInsert ->
		})
	}
	
}
