package com.hisd3.hismk2.graphqlservices.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.User
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.inventory.Inventory
import com.hisd3.hismk2.domain.inventory.InventoryLedger
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.inventory.PODeliveryMonitoring
import com.hisd3.hismk2.domain.inventory.PurchaseOrderItems
import com.hisd3.hismk2.domain.inventory.ReceivingReport
import com.hisd3.hismk2.domain.inventory.ReceivingReportItem
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.inventory.InventoryLedgerRepository
import com.hisd3.hismk2.repository.inventory.PoItemMonitoringRepository
import com.hisd3.hismk2.repository.inventory.PurchaseOrderItemRepository
import com.hisd3.hismk2.repository.inventory.ReceivingReportItemRepository
import com.hisd3.hismk2.repository.inventory.ReceivingReportRepository
import com.hisd3.hismk2.rest.dto.POMonitoringDto
import com.hisd3.hismk2.rest.dto.QueryErrorException
import com.hisd3.hismk2.rest.dto.ReceivingDto
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.GeneratorService
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import com.hisd3.hismk2.repository.inventory.InventoryLedgerRepository

import java.time.Instant

@Component
@GraphQLApi
@TypeChecked
class PODeliveryMonitoringService extends AbstractDaoService<PODeliveryMonitoring> {

	PODeliveryMonitoringService() {
		super(PODeliveryMonitoring.class)
	}

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	GeneratorService generatorService

	@Autowired
	UserRepository userRepository

	@Autowired
	EmployeeRepository employeeRepository

	@Autowired
	PurchaseOrderItemRepository purchaseOrderItemRepository

	@Autowired
	ReceivingReportRepository receivingReportRepository

	@Autowired
	ReceivingReportItemRepository receivingReportItemRepository

	@Autowired
	InventoryLedgerRepository inventoryLedgerRepository

	@Autowired
	PoItemMonitoringRepository poItemMonitoringRepository

	@GraphQLQuery(name = "getPOMonitoringByRec")
	List<PODeliveryMonitoring> getPOMonitoringByRec(
			@GraphQLArgument(name = "id") UUID id
	) {
		String query = '''Select e from PODeliveryMonitoring e where e.receivingReport.id = :id'''
		Map<String, Object> params = new HashMap<>()
		params.put('id', id)
		createQuery(query, params).resultList
	}

	@GraphQLQuery(name = "getCountMonitoring")
	Long getCountMonitoring(
			@GraphQLArgument(name = "id") UUID id
	) {
		String query = '''Select count(e) from PODeliveryMonitoring e where e.purchaseOrderItem.id = :id'''
		Map<String, Object> params = new HashMap<>()
		params.put('id', id)
		getCount(query, params)
	}

	@GraphQLQuery(name = "getPOMonitoringByPoItemFilter")
	List<PODeliveryMonitoring> getPOMonitoringByPoItemFilter(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "filter") String filter
	) {
		String query = '''Select e from PODeliveryMonitoring e where e.purchaseOrderItem.id = :id and 
		lower(concat(e.receivingReport.rrNo, e.receivingReport.receivedRefNo)) like lower(concat('%',:filter,'%'))'''
		Map<String, Object> params = new HashMap<>()
		params.put('id', id)
		params.put('filter', filter)
		createQuery(query, params).resultList
	}


	// ========= Mutation =====/
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertPOMonitoring")
	PODeliveryMonitoring upsertPOMonitoring(
			@GraphQLArgument(name = "fields") POMonitoringDto fields,
			@GraphQLArgument(name = "id") UUID id
	) {
		PODeliveryMonitoring upsert = new PODeliveryMonitoring();
		if(id){
			upsert = findOne(id)
		}else{
			upsert.purchaseOrderItem = purchaseOrderItemRepository.findById(fields.purchaseOrderItem).get()
			upsert.receivingReport = receivingReportRepository.findById(fields.receivingReport).get()
			upsert.receivingReportItem = receivingReportItemRepository.findById(fields.receivingReportItem).get()
			upsert.quantity = fields.quantity
			upsert.status = fields.status
			save(upsert)
		}
		return upsert
	}

	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "delPOMonitoring")
	PODeliveryMonitoring delPOMonitoring(
			@GraphQLArgument(name = "id") UUID id
	) {
		def del = findOne(id)
		delete(del)
		return del
	}


}
