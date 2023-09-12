package com.hisd3.hismk2.graphqlservices.accounting

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
class TransactionTypeService extends AbstractDaoService<TransactionType> {
	//transaction Type for Receiving Report (SRR)
	
	@Autowired
	GeneratorService generatorService
	
	TransactionTypeService() {
		super(TransactionType.class)
	}
	
	@GraphQLQuery(name = "transTypeById")
	TransactionType transTypeById(
			@GraphQLArgument(name = "id") UUID id
	) {
		findOne(id)
	}
	
	@GraphQLQuery(name = "transactionList", description = "Transaction List")
	List<TransactionType> transactionList() {
		findAll().sort { it.description }
	}
	
	@GraphQLQuery(name = "transTypeByTag", description = "transaction type by tag")
	List<TransactionType> transTypeByTag(@GraphQLArgument(name = "tag") String tag) {
		createQuery("Select f from TransactionType f where f.tag = :tag and f.status = true",
				[tag: tag]).resultList.sort { it.description }
	}

	@GraphQLQuery(name = "transTypeReceiving", description = "transaction type by tag")
	List<TransactionType> transTypeReceiving(
			@GraphQLArgument(name = "consignment") Boolean consignment,
			@GraphQLArgument(name = "asset") Boolean asset
	) {
		String query = "Select f from TransactionType f where f.tag = 'RECEIVING' and f.status = true"

		Map<String, Object> params = new HashMap<>()

		if (consignment) {
			query += ''' and f.consignment = :consignment'''
			params.put("consignment", consignment)
		}

		if (asset) {
			query += ''' and f.asset = :asset'''
			params.put("asset", asset)
		}

		createQuery(query, params).resultList.sort { it.description }
	}

	@GraphQLQuery(name = "transTypeByTagFilter", description = "transaction type by tag")
	List<TransactionType> transTypeByTagFilter(@GraphQLArgument(name = "tag") String tag,
										 @GraphQLArgument(name = "filter") String filter) {
		createQuery("Select f from TransactionType f where f.tag = :tag and (lower(f.description) like lower(concat('%',:filter,'%')))",
				[tag: tag, filter: filter]).resultList.sort { it.description }
	}
	
	//mutation
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertTransType", description = "insert TransType")
	TransactionType upsertTransType(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "id") UUID id
	) {
		
		upsertFromMap(id, fields, { TransactionType entity, boolean forInsert ->
		
		})
	}
	
}
