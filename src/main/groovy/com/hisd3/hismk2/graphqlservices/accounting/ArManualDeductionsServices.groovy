package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.*
import com.hisd3.hismk2.domain.billing.CompanyAccount
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.repository.accounting.AccountReceivableItemsRepository
import com.hisd3.hismk2.repository.accounting.ArTransactionDetailsRepository
import com.hisd3.hismk2.repository.inventory.SupplierRepository
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
class ArManualDeductionsServices extends AbstractDaoService<ArManualDeductions> {

	ArManualDeductionsServices() {
		super(ArManualDeductions.class)
	}

	@Autowired
	EntityObjectMapperService entityObjectMapperService

	@Autowired
	GeneratorService generatorService

	@GraphQLQuery(name = "getAllArManualDeductions")
	Page<ArManualDeductions> getAllArManualDeductions(
			@GraphQLArgument(name = "companyId") UUID companyId,
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {
		//language=HQL
		getPageable(
				"""
			 	select ar from ArManualDeductions ar LEFT JOIN BillingScheduleItems bs on ar.id = bs.arManualDeductions.id and (bs.isVoided = FALSE OR bs.isVoided IS NULL)
			 	where bs.id is null and ar.companyAccount.id = :companyId
			 	and  ar.status = 'active' and 
				(
					lower(ar.billing.patient.fullName) like concat('%', :filter ,'%')
			  	)
				order by ar.createdDate desc
				""", """
				select count(ar) from ArManualDeductions ar LEFT JOIN BillingScheduleItems bs on ar.id = bs.arManualDeductions.id and (bs.isVoided = FALSE OR bs.isVoided IS NULL)
			 	where bs.id is null and ar.companyAccount.id = :companyId 
			 	and ar.status = 'active' and 
				(
					lower(ar.billing.patient.fullName) like concat('%', :filter ,'%')
			  	)
				""",
				page,
				size,
				[
						companyId: companyId,
						filter   : filter
				]

		)

	}

	@GraphQLMutation
	ArManualDeductions upsertArManualDeductions(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {

		upsertFromMap(id, fields, { ArManualDeductions entity, boolean forInsert ->
			if (forInsert)
				entity.recordNo = generatorService.getNextValue(GeneratorType.AR_MANUAL_DEDUCTION, {
					return StringUtils.leftPad(it.toString(), 6, "0")
				})

		})

	}
}
