package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.AccountsPayable
import com.hisd3.hismk2.domain.accounting.ApLedger
import com.hisd3.hismk2.domain.accounting.ApPfCompany
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.repository.inventory.SupplierRepository
import com.hisd3.hismk2.services.GeneratorService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.Duration
import java.time.Instant

@Service
@GraphQLApi
class ApPfCompanyServices extends AbstractDaoService<ApPfCompany> {

	@Autowired
	GeneratorService generatorService

	@Autowired
	SupplierRepository supplierRepository

    ApPfCompanyServices() {
		super(ApPfCompany.class)
	}
	
	@GraphQLQuery(name = "pfCompanyById")
	ApPfCompany pfCompanyById(
			@GraphQLArgument(name = "id") UUID id
	) {
		findOne(id)
	}

	@GraphQLQuery(name = "pfCompanyPage", description = "List of AP Pageable")
	Page<ApPfCompany> pfCompanyPage(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "supplier") UUID supplier,
			@GraphQLArgument(name = "start") String start,
			@GraphQLArgument(name = "end") String end,
			@GraphQLArgument(name = "status") Boolean status,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		String query = '''Select pf from ApPfCompany pf where
						( lower(pf.accountReceivable.arNo) like lower(concat('%',:filter,'%')) OR
						lower(pf.patient) like lower(concat('%',:filter,'%')) OR
						lower(pf.accountReceivable.companyName) like lower(concat('%',:filter,'%')) ) and 
						to_date(to_char(pf.accountReceivable.transactionDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
             			between to_date(:start,'YYYY-MM-DD') and  to_date(:end,'YYYY-MM-DD')'''

		String countQuery = '''Select count(pf) from ApPfCompany pf where
							( lower(pf.accountReceivable.arNo) like lower(concat('%',:filter,'%')) OR
						lower(pf.patient) like lower(concat('%',:filter,'%')) OR
						lower(pf.accountReceivable.companyName) like lower(concat('%',:filter,'%')) ) and 
						to_date(to_char(pf.accountReceivable.transactionDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
             			between to_date(:start,'YYYY-MM-DD') and  to_date(:end,'YYYY-MM-DD')'''

		Map<String, Object> params = new HashMap<>()
		params.put('filter', filter)
		params.put('start', start)
		params.put('end', end)

		if (supplier) {
			query += ''' and (pf.supplier.id = :supplier) '''
			countQuery += ''' and (pf.supplier.id = :supplier) '''
			params.put("supplier", supplier)
		}

		if (status) {
			query += ''' and (pf.apProcess = :status or pf.apProcess is null) '''
			countQuery += ''' and (pf.apProcess = :status or pf.apProcess is null) '''
			params.put("status", !status)
		}

		query += ''' ORDER BY pf.accountReceivable.arNo DESC'''

		getPageable(query, countQuery, page, size, params)
	}
}
