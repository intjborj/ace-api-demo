package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.AccountReceivableItems
import com.hisd3.hismk2.domain.accounting.ArTransaction
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.billing.BillingItemServices
import com.hisd3.hismk2.repository.accounting.AccountReceivableItemsRepository
import com.hisd3.hismk2.repository.accounting.AccountReceivableRepository
import com.hisd3.hismk2.repository.accounting.BillingScheduleItemsRepository
import com.hisd3.hismk2.repository.accounting.BillingScheduleRepository
import com.hisd3.hismk2.services.EntityObjectMapperService
import com.hisd3.hismk2.services.GeneratorService
import com.sun.org.apache.xpath.internal.operations.Bool
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@GraphQLApi
class AccountReceivableItemsServices extends AbstractDaoService<AccountReceivableItems> {

	AccountReceivableItemsServices() {
		super(AccountReceivableItems.class)
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
	BillingScheduleItemsRepository billingScheduleItemRepository

	@Autowired
	BillingItemServices billingItemServices

	@Autowired
	GeneratorService generatorService



//	@GraphQLQuery(name = "getARTotalPerType")
//	BigDecimal getARTotalPerType(
//			@GraphQLArgument(name = "companyId") UUID companyId,
//			@GraphQLArgument(name = "type") String type
//	) {
//		def total = accountReceivableItemsRepository.getARSumByType(companyId,type)
//		return total
//	}

	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "updateApProcess")
	AccountReceivableItems updateApProcess(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "status") Boolean status
	) {
		def item = findOne(id)
		item.apProcess = status
		save(item)

		//update ap delete
		return item
	}

//	Use 2021
	@GraphQLQuery(name = "receivableItemsByARId")
	List<AccountReceivableItems> receivableItemsByARId(@GraphQLArgument(name = "id") UUID id
	) {
		createQuery("""select b from AccountReceivableItems b where  b.accountReceivable.id = :id""",
				[
						id: id,
				] as Map<String, Object>).resultList
	}

	@GraphQLQuery(name = "receivableItemsPersonalList")
	Page<AccountReceivableItems> receivableItemsPersonalList(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "account") UUID account,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {
		getPageable(
				"""
					Select c from AccountReceivableItems c LEFT JOIN AccountReceivable a on a.id = c.accountReceivable.id where a.groups['PERSONAL_ACCOUNT_ID'] = :account and (lower(c.description) like lower(concat('%',:filter,'%'))) order by c.description
					""",
				"""
					Select count(c) from AccountReceivableItems c LEFT JOIN AccountReceivable a on a.id = c.accountReceivable.id where a.groups['PERSONAL_ACCOUNT_ID'] = :account and (lower(c.description) like lower(concat('%',:filter,'%')))
					""",
				page,
				size,
				[
						filter  : filter,
						account: account
				]
		)
	}

	@GraphQLQuery(name = "getExistBillItemsInReceivables")
	List<AccountReceivableItems> getExistBillItemsInReceivables() {
		createQuery("""
                    select a from AccountReceivableItems a LEFT JOIN BillingItem b on a.details['BILLING_ITEM_ID'] = CAST(b.id AS string) where b.billing.id is not null group by b.billing.id,a
            """).resultList
	}
}
