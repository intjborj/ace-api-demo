package com.hisd3.hismk2.graphqlservices.accounting

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.accounting.Wtx2307
import com.hisd3.hismk2.domain.accounting.Wtx2307Consolidated
import com.hisd3.hismk2.domain.inventory.PhysicalCount
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.repository.inventory.SupplierRepository
import com.hisd3.hismk2.rest.dto.DisbursementApDto
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLContext
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.Duration

@Service
@GraphQLApi
class Wtx2307ConsolidatedService extends AbstractDaoService<Wtx2307Consolidated> {

	@Autowired
	GeneratorService generatorService

	@Autowired
	SupplierRepository supplierRepository

	@Autowired
	Wtx2307Service wtx2307Service

	@Autowired
	ObjectMapper objectMapper

    Wtx2307ConsolidatedService() {
		super(Wtx2307Consolidated.class)
	}
	
	@GraphQLQuery(name = "wtxConById")
	Wtx2307Consolidated wtxConById(
			@GraphQLArgument(name = "id") UUID id
	) {
		findOne(id)
	}


	@GraphQLQuery(name = "wtxConList", description = "Transaction List")
	List<Wtx2307Consolidated> wtxConList() {
		findAll().sort { it.refNo }
	}

	@GraphQLQuery(name = "wtxConListPage")
	Page<Wtx2307Consolidated> wtxConListPage(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "supplier") UUID supplier,
			@GraphQLArgument(name = "start") String start,
			@GraphQLArgument(name = "end") String end,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		String query = '''Select d from Wtx2307Consolidated d where
						(lower(d.refNo) like lower(concat('%',:filter,'%')) )
						and to_date(to_char(d.dateFrom, 'YYYY-MM-DD'),'YYYY-MM-DD')
             			between to_date(:start,'YYYY-MM-DD') and  to_date(:end,'YYYY-MM-DD') '''

		String countQuery = '''Select count(d) from Wtx2307Consolidated d where
						(lower(d.refNo) like lower(concat('%',:filter,'%')) )
						and to_date(to_char(d.dateFrom, 'YYYY-MM-DD'),'YYYY-MM-DD')
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

		query += ''' ORDER BY d.dateFrom DESC'''

		getPageable(query, countQuery, page, size, params)
	}

	@GraphQLQuery(name = "ewtAmount")
	BigDecimal getPhysicalCountById(@GraphQLContext Wtx2307Consolidated wtx) {
		getSum("Select sum(d.ewtAmount) from Wtx2307 d where d.wtxConsolidated = :id",
				[id: wtx.id])
	}
	
	//mutation
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertConsolidated")
	Wtx2307Consolidated upsertConsolidated(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "items") ArrayList<Map<String,Object>> items,
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "supplier") UUID supplier
	) {
		def wtx = upsertFromMap(id, fields, { Wtx2307Consolidated entity, boolean forInsert ->
			if(forInsert){
				entity.dateFrom = entity.dateFrom.plus(Duration.ofHours(8))
				entity.dateTo = entity.dateTo.plus(Duration.ofHours(8))
				entity.refNo = generatorService.getNextValue(GeneratorType.WTXNO, {
					return "WTX-" + StringUtils.leftPad(it.toString(), 6, "0")
				})
				entity.supplier = supplierRepository.findById(supplier).get()
			}
		})

		def item = items as ArrayList<Wtx2307>
		item.each {
			def e = objectMapper.convertValue(it, Wtx2307.class)
			wtx2307Service.update2307(e.id, true, wtx.id)
		}

		return wtx
	}


	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "add2307")
	Wtx2307Consolidated add2307(
			@GraphQLArgument(name = "items") ArrayList<Map<String,Object>> items,
			@GraphQLArgument(name = "id") UUID id
	) {
		def wtx = findOne(id)

		def item = items as ArrayList<Wtx2307>
		item.each {
			def e = objectMapper.convertValue(it, Wtx2307.class)
			wtx2307Service.update2307(e.id, true, wtx.id)
		}

		return wtx
	}
	
}
