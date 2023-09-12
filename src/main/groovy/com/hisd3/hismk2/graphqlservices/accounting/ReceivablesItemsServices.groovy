package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.AccountReceivableItems
import com.hisd3.hismk2.domain.accounting.ReceivablesItems
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.billing.BillingItemServices
import com.hisd3.hismk2.repository.accounting.AccountReceivableItemsRepository
import com.hisd3.hismk2.repository.accounting.AccountReceivableRepository
import com.hisd3.hismk2.repository.accounting.BillingScheduleItemsRepository
import com.hisd3.hismk2.repository.accounting.BillingScheduleRepository
import com.hisd3.hismk2.services.EntityObjectMapperService
import com.hisd3.hismk2.services.GeneratorService
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
class ReceivablesItemsServices extends AbstractDaoService<ReceivablesItems> {

	ReceivablesItemsServices() {
		super(ReceivablesItems.class)
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


	@GraphQLQuery(name = "AccReceivableItemPerGuarantorList")
	Page<ReceivablesItems> AccReceivableItemPerGuarantorList(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "account") UUID account,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {
		getPageable(
				"""
			  Select c from ReceivablesItems c  where c.companyId = :account and (lower(c.billingDescription) like lower(concat('%',:filter,'%')) or lower(c.arDescription) like lower(concat('%',:filter,'%')) or lower(c.arNo) like lower(concat('%',:filter,'%'))) order by c.arNo,c.billingDescription,c.createdDate desc
			""",
				"""
			 Select count(c) from ReceivablesItems c where c.companyId = :account and (lower(c.billingDescription) like lower(concat('%',:filter,'%')) or lower(c.arDescription) like lower(concat('%',:filter,'%')) or lower(c.arNo) like lower(concat('%',:filter,'%')))
			""",
				page,
				size,
				[
						filter: filter,
						account: account,
				]
		)

	}

	@GraphQLQuery(name = "AccReceivableItemForPhic")
	Page<ReceivablesItems> AccReceivableItemForPhic(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "account") UUID account,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {
		getPageable(
				"""
			  Select c from ReceivablesItems c  where c.companyId = :account and ((lower(c.billingDescription) like lower(concat('%',:filter,'%'))) or (lower(c.arDescription) like lower(concat('%',:filter,'%'))) or (c.bsPhilClaims.claimSeriesLhio like concat('%',:filter,'%')) or lower(c.arNo) like lower(concat('%',:filter,'%'))) order by c.arNo,c.billingDescription,c.createdDate desc
			""",
				"""
			 Select count(c) from ReceivablesItems c where c.companyId = :account and ((lower(c.billingDescription) like lower(concat('%',:filter,'%'))) or (lower(c.arDescription) like lower(concat('%',:filter,'%'))) or (c.bsPhilClaims.claimSeriesLhio like concat('%',:filter,'%')) or lower(c.arNo) like lower(concat('%',:filter,'%')))
			""",
				page,
				size,
				[
						filter: filter,
						account: account,
				]
		)
	}
}
