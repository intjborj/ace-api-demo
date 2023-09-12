package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.AccountConfig
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
class AccountConfigServices extends AbstractDaoService<AccountConfig> {
	
	@Autowired
	GeneratorService generatorService

	
	AccountConfigServices() {
		super(AccountConfig.class)
	}
	
	@GraphQLQuery(name = "accountConfigById")
	AccountConfig accountConfigById(
			@GraphQLArgument(name = "id") UUID id
	) {
		findOne(id)
	}
	
	@GraphQLQuery(name = "accountConfigList", description = "Transaction List")
	List<AccountConfig> accountConfigList() {
		findAll().sort { it.transactionType.description }
	}
	
	@GraphQLQuery(name = "accountConfigByTag", description = "transaction type by tag")
	List<AccountConfig> accountConfigByTag(@GraphQLArgument(name = "tag") String tag) {
		createQuery("Select f from AccountConfig f where f.transactionType.tag = :tag",
				[tag: tag]).resultList.sort { it.transactionType.description }
	}
	
	@GraphQLQuery(name = "accountConfigByTypeId", description = "transaction type by tag")
	List<AccountConfig> accountConfigByTypeId(@GraphQLArgument(name = "id") UUID id) {
		createQuery("Select f from AccountConfig f where f.transactionType.id = :id",
				[id: id]).resultList.sort { it.createdDate }
	}

//	@GraphQLQuery(name = "ledgerRecordList", description = "transaction type by tag")
//	List<LedgerRecord> ledgerRecordList(@GraphQLArgument(name = "id") UUID id) {
//		accountingService.getAccountList(id)
//	}
	
	//mutation
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertAccountConfig", description = "insert TransType")
	AccountConfig upsertAccountConfig(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "id") UUID id
	) {
		
		upsertFromMap(id, fields, { AccountConfig entity, boolean forInsert ->
		
		})
	}
	
}
