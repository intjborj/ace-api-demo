package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.ApLedger
import com.hisd3.hismk2.domain.accounting.ApTransaction
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.repository.inventory.SupplierRepository
import com.hisd3.hismk2.services.GeneratorService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.Duration
import java.time.Instant

@Service
@GraphQLApi
class ApLedgerServices extends AbstractDaoService<ApLedger> {

	@Autowired
	GeneratorService generatorService

	@Autowired
	SupplierRepository supplierRepository

    ApLedgerServices() {
		super(ApLedger.class)
	}
	
	@GraphQLQuery(name = "apLedgerById")
	ApLedger apLedgerById(
			@GraphQLArgument(name = "id") UUID id
	) {
		findOne(id)
	}
	
	@GraphQLQuery(name = "apLedgerInclude", description = "Find Ap Ledger Include")
	List<ApLedger> apLedgerInclude() {
		createQuery("Select ap from ApLedger ap where ap.isInclude = true").resultList
	}

	@GraphQLQuery(name = "apLedgerByRef", description = "Find Ap Ledger Include")
	ApLedger apLedgerByRef(@GraphQLArgument(name = "refNo") String refNo) {
		createQuery("Select ap from ApLedger ap where ap.refNo = :refNo",[refNo: refNo]).resultList.find()
	}

	@GraphQLQuery(name = "apLedgerBySupplier", description = "Find Ap Transaction Active")
	List<ApLedger> apLedgerBySupplier(@GraphQLArgument(name = "supplier") UUID supplier) {
		createQuery("Select ap from ApLedger ap where ap.supplier.id = :supplier and ap.isInclude = true",
				[supplier: supplier]).resultList
	}

	@GraphQLQuery(name = "apLedgerFilter", description = "Transaction List")
	List<ApLedger> apLedgerFilter(@GraphQLArgument(name = "filter") String filter,
										  @GraphQLArgument(name = "supplier") UUID supplier) {

		def query = "Select f from ApLedger f where lower(f.ref_no) like lower(concat('%',:desc,'%'))"
		Map<String, Object> params = new HashMap<>()
		params.put('filter', filter)

		if(supplier){
			query+= " and f.supplier.id = :type"
			params.put('supplier', supplier)
		}

		createQuery(query,
				params)
				.resultList.sort { it.ledgerDate }

	}

	//mutations
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertApLedger")
	ApLedger upsertApLedger(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "supplier") UUID supplier,
			@GraphQLArgument(name = "id") UUID id
	) {

		upsertFromMap(id, fields, { ApLedger entity, boolean forInsert ->
			if(forInsert){
				if(supplier){
					entity.supplier = supplierRepository.findById(supplier).get()
				}
				entity.isInclude = true
				entity.ledgerDate = Instant.now().plus(Duration.ofHours(8))
			}
		})
	}

	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "removeApLedger")
	ApLedger removeApLedger(
			@GraphQLArgument(name = "ref") String ref
	) {
		def ledger = apLedgerByRef(ref)
		if(ledger){
			delete(ledger)
		}
		return ledger
	}
}
