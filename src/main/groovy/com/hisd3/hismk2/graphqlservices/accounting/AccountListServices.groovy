package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.AccountList
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
class AccountListServices extends AbstractDaoService<AccountList> {
	
	@Autowired
	GeneratorService generatorService
	
	AccountListServices() {
		super(AccountList.class)
	}
	
	@GraphQLQuery(name = "accountListById")
	AccountList accountListById(
			@GraphQLArgument(name = "id") UUID id
	) {
		findOne(id)
	}
	
	@GraphQLQuery(name = "accountList", description = "Account List")
	List<AccountList> accountList() {
		findAll().sort { it.createdDate }
	}
	
	@GraphQLQuery(name = "accountConfigParent", description = "Account List by id")
	List<AccountList> accountConfigParent(@GraphQLArgument(name = "parent") UUID parent) {
		createQuery("Select f from AccountList f where f.parent.id = :parent",
				[parent: parent]).resultList.sort { it.createdDate }
	}
	
	//mutation
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertAccountList", description = "insert TransType")
	AccountList upsertAccountList(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "id") UUID id
	) {
		
		upsertFromMap(id, fields, { AccountList entity, boolean forInsert ->
		
		})
	}
	
}
