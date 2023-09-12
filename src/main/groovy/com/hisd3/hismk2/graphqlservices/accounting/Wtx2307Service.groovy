package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.PettyCash
import com.hisd3.hismk2.domain.accounting.TransactionType
import com.hisd3.hismk2.domain.accounting.Wtx2307
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

@Service
@GraphQLApi
class Wtx2307Service extends AbstractDaoService<Wtx2307> {
	//transaction Type for Receiving Report (SRR)

	@Autowired
	GeneratorService generatorService

	@Autowired
	SupplierRepository supplierRepository

    Wtx2307Service() {
		super(Wtx2307.class)
	}
	
	@GraphQLQuery(name = "wtxById")
	Wtx2307 wtxById(
			@GraphQLArgument(name = "id") UUID id
	) {
		findOne(id)
	}

	@GraphQLQuery(name = "findOneByRefId")
	Wtx2307 findOneByRefId(
			@GraphQLArgument(name = "id") UUID id
	) {
		createQuery("Select d from Wtx2307 d where d.refId = :id",
				[id: id]).resultList.find()
	}

	@GraphQLQuery(name = "wtxListByRef")
	List<Wtx2307> wtxListByRef(
			@GraphQLArgument(name = "id") UUID id
	) {
		createQuery("Select d from Wtx2307 d where d.wtxConsolidated = :id",
				[id: id]).resultList
	}
	
	@GraphQLQuery(name = "wtxList", description = "Transaction List")
	List<Wtx2307> wtxList() {
		findAll().sort { it.refNo }
	}

	@GraphQLQuery(name = "wtxListPage")
	Page<Wtx2307> wtxListPage(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "supplier") UUID supplier,
			@GraphQLArgument(name = "start") String start,
			@GraphQLArgument(name = "end") String end,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		String query = '''Select d from Wtx2307 d where
						(lower(d.refNo) like lower(concat('%',:filter,'%')) )
						and to_date(to_char(d.wtxDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
             			between to_date(:start,'YYYY-MM-DD') and  to_date(:end,'YYYY-MM-DD') '''

		String countQuery = '''Select count(d) from Wtx2307 d where
						(lower(d.refNo) like lower(concat('%',:filter,'%')) )
						and to_date(to_char(d.wtxDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
             			between to_date(:start,'YYYY-MM-DD') and  to_date(:end,'YYYY-MM-DD') '''

		Map<String, Object> params = new HashMap<>()
		params.put('filter', filter)
		params.put('start', start)
		params.put('end', end)

		if (supplier) {
			query += ''' and (d.supplier.id = :supplier) '''
			countQuery += ''' and (d.supplier.id = :supplier) '''
			params.put("supplier", supplier)
		}


		query += ''' ORDER BY d.wtxDate DESC'''

		getPageable(query, countQuery, page, size, params)
	}
	
	//mutation
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsert2307")
	Wtx2307 upsert2307(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "supplier") UUID supplier
	) {
		upsertFromMap(id, fields, { Wtx2307 entity, boolean forInsert ->
			if(forInsert){
				entity.wtxDate = entity.wtxDate
				entity.supplier = supplierRepository.findById(supplier).get()
				entity.process = false
			}
		})
	}

	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "remove2307")
	Wtx2307 remove2307(
			@GraphQLArgument(name = "id") UUID id
	) {
		def wtx = findOneByRefId(id)
		if(wtx){
			delete(wtx)
		}
		return wtx
	}

	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "update2307")
	Wtx2307 update2307(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "status") Boolean status,
			@GraphQLArgument(name = "ref") UUID ref
	) {
		def wtx = findOne(id)
		wtx.process = status
		wtx.wtxConsolidated = ref
		save(wtx)
	}

	
}
