package com.hisd3.hismk2.graphqlservices.inventoryv2

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.inventory.PoItemMonitoring
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.services.GeneratorService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.data.domain.Page


import com.hisd3.hismk2.repository.inventory.InventoryLedgerRepository
import com.hisd3.hismk2.repository.inventory.ReceivingReportRepository

@Service
@GraphQLApi
class ServicePoItemMonitoring extends AbstractDaoService<PoItemMonitoring> {

	ServicePoItemMonitoring() {
		super(PoItemMonitoring.class)
	}
	
	@Autowired
	ObjectMapper objectMapper
	
	@Autowired
	GeneratorService generatorService
	
	@Autowired
	UserRepository userRepository

	@Autowired
	InventoryLedgerRepository inventoryLedgerRepository

	@Autowired
	ReceivingReportRepository receivingReportRepository


	@GraphQLQuery(name = "getPOItemMonitoring")
	List<PoItemMonitoring> getPOItemMonitoring(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "poId") UUID poId
	) {

		String query = '''Select poim from PoItemMonitoring poim where 
		poim.purchaseOrder.id = :id and lower(poim.item.descLong) like lower(concat('%',:filter,'%'))'''
		Map<String, Object> params = new HashMap<>()
		params.put('id', poId)
		params.put('filter', filter)

		createQuery(query, params).resultList.sort{it.item.descLong}
	}

	@GraphQLQuery(name = "getPOItemMonitoringPage")
	Page<PoItemMonitoring> getPOItemMonitoringPage(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "poId") UUID poId,
			@GraphQLArgument(name = "supplier") UUID supplier,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		String query = '''Select poim from PoItemMonitoring poim where 
		 	(lower(poim.descLong) like lower(concat('%',:filter,'%')) or 
		 	lower(poim.supplierFullName) like lower(concat('%',:filter,'%'))) and poim.purchaseOrder.status != :status '''

		String countQuery = '''Select count(poim) from PoItemMonitoring poim where 
		 	(lower(poim.descLong) like lower(concat('%',:filter,'%')) or 
		 	lower(poim.supplierFullName) like lower(concat('%',:filter,'%'))) and poim.purchaseOrder.status != :status '''

		Map<String, Object> params = new HashMap<>()
		params.put('filter', filter)
		params.put('status', 'VOIDED')

		if (poId) {
			query += ''' and poim.purchaseOrder.id = :id '''
			countQuery += ''' and poim.purchaseOrder.id = :id '''
			params.put('id', poId)
		}

		if(supplier){
			query += ''' and poim.supplier = :supplier '''
			countQuery += ''' and poim.supplier = :supplier '''
			params.put('supplier', supplier)
		}

		query += ''' ORDER BY poim.poNumber DESC'''

		getPageable(query, countQuery, page, size, params)
	}

	@GraphQLQuery(name = "poItemNotReceiveMonitoring")
	List<PoItemMonitoring> poItemNotReceiveMonitoring(
			@GraphQLArgument(name = "id") UUID id
	) {
		String query = '''Select e from PoItemMonitoring e where e.purchaseOrder.id = :id and (e.receivingReport is null or e.delBalance > 0)'''
		Map<String, Object> params = new HashMap<>()
		params.put('id', id)
		createQuery(query, params).resultList.sort { it.item.descLong }
	}

	@GraphQLQuery(name = "checkBalancesByPO")
	List<PoItemMonitoring> checkBalancesByPO(
			@GraphQLArgument(name = "id") UUID id
	) {
		String query = '''Select e from PoItemMonitoring e where e.purchaseOrder.id = :id'''
		Map<String, Object> params = new HashMap<>()
		params.put('id', id)
		createQuery(query, params).resultList.sort { it.delBalance }
	}

	@GraphQLQuery(name = "getPOByDel")
	PoItemMonitoring getPOByDel(
			@GraphQLArgument(name = "poItemId") UUID poItemId
	) {
		findOne(poItemId)
	}


}
