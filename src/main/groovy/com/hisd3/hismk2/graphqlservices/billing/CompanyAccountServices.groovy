package com.hisd3.hismk2.graphqlservices.billing

import com.hisd3.hismk2.domain.billing.BillingItem
import com.hisd3.hismk2.domain.billing.BillingItemStatus
import com.hisd3.hismk2.domain.billing.CompanyAccount
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
import org.springframework.stereotype.Component

@Component
@GraphQLApi
class CompanyAccountServices extends AbstractDaoService<CompanyAccount> {
	
	@Autowired
	GeneratorService generatorService
	
	@Autowired
	BillingItemServices billingItemServices
	
	CompanyAccountServices() {
		super(CompanyAccount.class)
	}
	
	@GraphQLQuery(name = "companyAccountById")
	CompanyAccount companyAccountById(
			@GraphQLArgument(name = "id") UUID id
	) {
		findOne(id)
	}
	
	@GraphQLQuery(name = "companyAccounts")
	Page<CompanyAccount> companyAccounts(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {
		
		getPageable("Select c from CompanyAccount c  where lower(c.companyname) like lower(concat('%',:filter,'%')) order by c.companyaccountId",
				"Select count(c) from CompanyAccount c  where lower(c.companyname) like lower(concat('%',:filter,'%'))",
				page,
				size,
				[
						filter: filter
				]
		)
	}
	
	@GraphQLMutation
	CompanyAccount upsertCompanyAccounts(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		
		upsertFromMap(id, fields, { CompanyAccount entity, boolean forInsert ->
			
			if (forInsert)
				entity.companyaccountId = generatorService.getNextValue(GeneratorType.COMPANYACCOUNTID, {
					return "COM-" + StringUtils.leftPad(it.toString(), 6, "0")
				})
			
		})
		
	}
	
	//=================== Account Receivable Routines =============
	
	//Todo: Consider Posting o Ledger?
	
	@GraphQLQuery(name = "getAllOutstandingClaims")
	Page<BillingItem> getAllOutstandingClaims(
			@GraphQLArgument(name = "companyAccountId") String companyAccountId,
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {
		
		//language=HQL
		billingItemServices.getPageable(
				"""
 select bi from BillingItem bi where bi.details['COMPANY_ACCOUNT_ID'] = :companyAccountId
 and 'COMPANY_ACCOUNT_CLAIM_REFERENCE' not in indices(bi.details)
 and (
    lower(bi.billing.patient.fullName) like concat('%', :filter ,'%')
    or lower(bi.recordNo)  like concat('%',:filter,'%')
  )
  and  bi.status = :status
 order by bi.transactionDate
""", """
select count(bi) from BillingItem bi where bi.details['COMPANY_ACCOUNT_ID'] = :companyAccountId
and 'COMPANY_ACCOUNT_CLAIM_REFERENCE' not in indices(bi.details)
and (
    lower(bi.billing.patient.fullName) like concat('%', :filter ,'%')
    or lower(bi.recordNo)  like concat('%',:filter,'%')
  )
  and  bi.status = :status
""",
				page,
				size,
				[companyAccountId: companyAccountId,
				 filter          : filter,
				 status          : BillingItemStatus.ACTIVE
				]
		)
		
	}
	
	List<BillingItem> getAllOutstandingClaimsList(
			String companyAccountId
	) {
		
		//language=HQL
		billingItemServices.createQuery(
				"""
			 select bi from BillingItem bi where bi.details['COMPANY_ACCOUNT_ID'] = :companyAccountId
			 and 'COMPANY_ACCOUNT_CLAIM_REFERENCE' not in indices(bi.details)
			  and  bi.status = :status
			 order by bi.transactionDate
			""",
				[companyAccountId: companyAccountId,
				 status          : BillingItemStatus.ACTIVE
				]
		).resultList
		
	}
	
	List<BillingItem> getAllOutstandingClaimsListFromRecno(
			String companyAccountId,
			List<String> recNos
	) {
		
		//language=HQL
		billingItemServices.createQuery(
				"""
			 select bi from BillingItem bi where bi.details['COMPANY_ACCOUNT_ID'] = :companyAccountId
			 and 'COMPANY_ACCOUNT_CLAIM_REFERENCE' not in indices(bi.details)
			  and  bi.status = :status and bi.recordNo in (:recNos)
			 order by bi.transactionDate
			""",
				[companyAccountId: companyAccountId,
				 status          : BillingItemStatus.ACTIVE,
				 recNos          : recNos
				]
		).resultList
		
	}
	
}
