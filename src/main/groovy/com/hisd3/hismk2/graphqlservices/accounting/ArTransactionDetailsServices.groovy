package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.AccountReceivable
import com.hisd3.hismk2.domain.accounting.ArTransactionDetails
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.cashiering.PaymentTrackerServices
import com.hisd3.hismk2.repository.accounting.*
import com.hisd3.hismk2.rest.dto.QueryErrorException
import com.hisd3.hismk2.services.EntityObjectMapperService
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@GraphQLApi
class ArTransactionDetailsServices extends AbstractDaoService<ArTransactionDetails> {

	ArTransactionDetailsServices() {
		super(ArTransactionDetails.class)
	}

	@Autowired
	EntityObjectMapperService entityObjectMapperService

	@Autowired
	AccountReceivableRepository accountReceivableRepository

	@Autowired
	AccountReceivableItemsRepository accountReceivableItemsRepository

	@Autowired
	BillingScheduleRepository billingScheduleRepository

	@Autowired
	PaymentTrackerServices paymentTrackerServices

	@Autowired
	ArTransactionRepository arTransactionRepository

	@Autowired
	ArTransactionDetailsRepository arTransactionDetailsRepository

	@Autowired
	GeneratorService generatorService

	@GraphQLQuery(name = "findTransactionDetailsByParentId")
	List<ArTransactionDetails> findTransactionDetailsByParentId(@GraphQLArgument(name = "id") UUID id

	) {
		createQuery("""
                    select b from ArTransactionDetails b where  b.arTransaction.id = :id 
            """,
				[
						id: id,
				] as Map<String, Object>).resultList
	}

	@GraphQLQuery(name = "checkIfARItemIdExist")
	Boolean checkIfARItemIdExist(@GraphQLArgument(name = "arRec") AccountReceivable arRec

	) {
		List<UUID> ids = new ArrayList<UUID>()
		arRec.accountReceivableItems.each {
			it->
				ids.push(it.id)
		}
		def result =  findArDetailItemPaymentByID(ids)
		if(result){
			return  true
		}
		return  false

	}

	@GraphQLQuery(name = "findArDetailItemPaymentByID")
	List<ArTransactionDetails> findArDetailItemPaymentByID(@GraphQLArgument(name = "id") List<UUID> id) {
		createQuery("""
                    select b from ArTransactionDetails b where  b.accountReceivableItems.id in (:id) 
            """,
				[
						id: id,
				] as Map<String, Object>).resultList

	}


}
