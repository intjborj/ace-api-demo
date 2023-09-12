package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.ApTransaction
import com.hisd3.hismk2.domain.accounting.ChartOfAccount
import com.hisd3.hismk2.domain.accounting.Fiscal
import com.hisd3.hismk2.domain.accounting.TransactionType
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@GraphQLApi
class ApTransactionServices extends AbstractDaoService<ApTransaction> {

	@Autowired
	GeneratorService generatorService

    ApTransactionServices() {
		super(ApTransaction.class)
	}
	
	@GraphQLQuery(name = "apTransactionById")
	ApTransaction apTransactionById(
			@GraphQLArgument(name = "id") UUID id
	) {
		findOne(id)
	}
	
	@GraphQLQuery(name = "apTransactionActive", description = "Find Ap Transaction Active")
	List<ApTransaction> apTransactionActive() {
		createQuery("Select ap from ApTransaction ap where ap.status = true").resultList
	}

	@GraphQLQuery(name = "apTransactionByType", description = "Find Ap Transaction Active")
	List<ApTransaction> apTransactionActive(@GraphQLArgument(name = "type") UUID type,
											@GraphQLArgument(name = "category") String category) {
		createQuery("Select ap from ApTransaction ap where ap.supplierType.id = :type and ap.category = :category and ap.status = true",
				[type: type, category: category]).resultList
	}

	@GraphQLQuery(name = "apTransactionOthers", description = "Find Ap Transaction Active")
	List<ApTransaction> apTransactionOthers(@GraphQLArgument(name = "category") String category) {
		createQuery("Select ap from ApTransaction ap where ap.supplierType is null and ap.category = :category and ap.status = true",
				[category: category]).resultList
	}

	@GraphQLQuery(name = "apTransactionList", description = "Transaction List")
	List<ApTransaction> apTransactionList(@GraphQLArgument(name = "desc") String desc,
										  @GraphQLArgument(name = "type") UUID type,
										  @GraphQLArgument(name = "category") String category) {

		def query = "Select f from ApTransaction f where lower(f.description) like lower(concat('%',:desc,'%'))"
		Map<String, Object> params = new HashMap<>()
		params.put('desc', desc)

		if(type){
			query+= " and f.supplierType.id = :type"
			params.put('type', type)
		}

		if(category){
			query+= " and f.category = :category"
			params.put('category', category)
		}

		createQuery(query,
				params)
				.resultList.sort { it.description }

	}

	//mutations
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertApTransaction")
	ApTransaction upsertApTransaction(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "id") UUID id
	) {

		upsertFromMap(id, fields, { ApTransaction entity, boolean forInsert ->
		})
		
	}
}
