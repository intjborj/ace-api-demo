package com.hisd3.hismk2.graphqlservices.accounting


import com.hisd3.hismk2.domain.accounting.ApReadersFee
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
class ApReadersFeeServices extends AbstractDaoService<ApReadersFee> {

	@Autowired
	GeneratorService generatorService

	@Autowired
	SupplierRepository supplierRepository

    ApReadersFeeServices() {
		super(ApReadersFee.class)
	}
	
	@GraphQLQuery(name = "apReadersFeeById")
	ApReadersFee apReadersFeeById(
			@GraphQLArgument(name = "id") UUID id
	) {
		findOne(id)
	}

	@GraphQLQuery(name = "apReadersFeeList", description = "List of AP Readers Fee")
	Page<ApReadersFee> apReadersFeeList(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "supplier") UUID supplier,
			@GraphQLArgument(name = "dep") UUID dep,
			@GraphQLArgument(name = "start") String start,
			@GraphQLArgument(name = "end") String end,
			@GraphQLArgument(name = "status") Boolean status,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		String query = '''Select rf from ApReadersFee rf where
						( lower(rf.description) like lower(concat('%',:filter,'%')) OR
						lower(rf.recordNo) like lower(concat('%',:filter,'%')) OR
						lower(rf.billing.billingNo) like lower(concat('%',:filter,'%')) OR
						lower(rf.billing.patient.fullName) like lower(concat('%',:filter,'%')) ) and 
						to_date(to_char(rf.transactionDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
             			between to_date(:start,'YYYY-MM-DD') and  to_date(:end,'YYYY-MM-DD')'''

		String countQuery = '''Select count(rf) from ApReadersFee rf where
							( lower(rf.description) like lower(concat('%',:filter,'%')) OR
						lower(rf.recordNo) like lower(concat('%',:filter,'%')) OR
						lower(rf.billing.patient.fullName) like lower(concat('%',:filter,'%')) ) and 
						to_date(to_char(rf.transactionDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
             			between to_date(:start,'YYYY-MM-DD') and  to_date(:end,'YYYY-MM-DD')'''

		Map<String, Object> params = new HashMap<>()
		params.put('filter', filter)
		params.put('start', start)
		params.put('end', end)

		if (supplier) {
			query += ''' and (rf.supplier.id = :supplier) '''
			countQuery += ''' and (rf.supplier.id = :supplier) '''
			params.put("supplier", supplier)
		}

		if (dep) {
			query += ''' and (rf.department.id = :dep or rf.department.parentDepartment.id = :dep) '''
			countQuery += ''' and (rf.department.id = :dep or rf.department.parentDepartment.id = :dep) '''
			params.put("dep", dep)
		}

		if (status) {
			query += ''' and (rf.apProcess = :status or rf.apProcess is null) '''
			countQuery += ''' and (rf.apProcess = :status or rf.apProcess is null) '''
			params.put("status", !status)
		}

		getPageable(query, countQuery, page, size, params)
	}

	@GraphQLQuery(name = "apReadersList", description = "List of AP Readers Fee")
	List<ApReadersFee> apReadersList(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "supplier") UUID supplier,
			@GraphQLArgument(name = "dep") UUID dep,
			@GraphQLArgument(name = "start") String start,
			@GraphQLArgument(name = "end") String end,
			@GraphQLArgument(name = "status") Boolean status
	) {

		String query = '''Select rf from ApReadersFee rf where
						( lower(rf.description) like lower(concat('%',:filter,'%')) OR
						lower(rf.recordNo) like lower(concat('%',:filter,'%')) OR
						lower(rf.billing.billingNo) like lower(concat('%',:filter,'%')) OR
						lower(rf.billing.patient.fullName) like lower(concat('%',:filter,'%')) ) and 
						to_date(to_char(rf.transactionDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
             			between to_date(:start,'YYYY-MM-DD') and  to_date(:end,'YYYY-MM-DD')'''

		Map<String, Object> params = new HashMap<>()
		params.put('filter', filter)
		params.put('start', start)
		params.put('end', end)

		if (supplier) {
			query += ''' and (rf.supplier.id = :supplier) '''
			params.put("supplier", supplier)
		}

		if (dep) {
			query += ''' and (rf.department.id = :dep or rf.department.parentDepartment.id = :dep) '''
			params.put("dep", dep)
		}

		if (status) {
			query += ''' and (rf.apProcess = :status or rf.apProcess is null) '''
			params.put("status", !status)
		}

		createQuery(query, params).resultList
	}
}
