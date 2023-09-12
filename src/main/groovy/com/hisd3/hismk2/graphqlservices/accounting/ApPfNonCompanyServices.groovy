package com.hisd3.hismk2.graphqlservices.accounting


import com.hisd3.hismk2.domain.accounting.ApPfCompany
import com.hisd3.hismk2.domain.accounting.ApPfNonCompany
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.repository.inventory.SupplierRepository
import com.hisd3.hismk2.services.GeneratorService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

@Service
@GraphQLApi
class ApPfNonCompanyServices extends AbstractDaoService<ApPfNonCompany> {

	@Autowired
	GeneratorService generatorService

	@Autowired
	SupplierRepository supplierRepository

    ApPfNonCompanyServices() {
		super(ApPfNonCompany.class)
	}
	
	@GraphQLQuery(name = "pfNonCompanyById")
	ApPfNonCompany pfNonCompanyById(
			@GraphQLArgument(name = "id") UUID id
	) {
		findOne(id)
	}

	@GraphQLQuery(name = "pfNonCompanyPage", description = "List of AP Pageable for Non Company PF")
	Page<ApPfNonCompany> pfNonCompanyPage(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "supplier") UUID supplier,
			@GraphQLArgument(name = "start") String start,
			@GraphQLArgument(name = "end") String end,
			@GraphQLArgument(name = "status") Boolean status,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		String query = '''Select pf from ApPfNonCompany pf where
						( lower(pf.billing.patient.fullName) like lower(concat('%',:filter,'%')) OR
						lower(pf.billing.billingNo) like lower(concat('%',:filter,'%')) OR
						lower(pf.recordNo) like lower(concat('%',:filter,'%')) OR
						lower(pf.billing.patientCase.caseNo) like lower(concat('%',:filter,'%')) OR
						lower(pf.orNumber) like lower(concat('%',:filter,'%')) ) and 
						to_date(to_char(pf.transactionDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
             			between to_date(:start,'YYYY-MM-DD') and  to_date(:end,'YYYY-MM-DD')'''

		String countQuery = '''Select count(pf) from ApPfNonCompany pf where
							( lower(pf.billing.patient.fullName) like lower(concat('%',:filter,'%')) OR
							lower(pf.billing.billingNo) like lower(concat('%',:filter,'%')) OR
						lower(pf.recordNo) like lower(concat('%',:filter,'%')) OR
						lower(pf.billing.patientCase.caseNo) like lower(concat('%',:filter,'%')) OR
						lower(pf.orNumber) like lower(concat('%',:filter,'%')) ) and 
						to_date(to_char(pf.transactionDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
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

		query += ''' ORDER BY pf.transactionDate DESC'''

		getPageable(query, countQuery, page, size, params)
	}
}
