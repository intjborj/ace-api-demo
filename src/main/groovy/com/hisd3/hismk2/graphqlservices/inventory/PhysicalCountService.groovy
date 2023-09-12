package com.hisd3.hismk2.graphqlservices.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.inventory.InventoryLedger
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.inventory.PhysicalCount
import com.hisd3.hismk2.domain.inventory.PhysicalTransaction
import com.hisd3.hismk2.graphqlservices.accounting.LedgerServices
import com.hisd3.hismk2.graphqlservices.inventoryv2.ServicePhysicalTransaction
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.inventory.DocumentTypeRepository
import com.hisd3.hismk2.repository.inventory.InventoryLedgerRepository
import com.hisd3.hismk2.repository.inventory.InventoryRepository
import com.hisd3.hismk2.repository.inventory.ItemRepository
import com.hisd3.hismk2.repository.inventory.PhysicalCountRepository
import com.hisd3.hismk2.repository.inventory.PurchaseOrderItemRepository
import com.hisd3.hismk2.repository.inventory.PurchaseOrderRepository
import com.hisd3.hismk2.repository.inventory.ReceivingReportRepository
import com.hisd3.hismk2.rest.InventoryResource
import com.hisd3.hismk2.rest.dto.QueryErrorException
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLContext
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import javax.persistence.EntityManager
import java.time.*
import java.time.format.DateTimeFormatter

@Component
@GraphQLApi
class PhysicalCountService {
	
	@Autowired
	PhysicalCountRepository physicalCountRepository
	
	@Autowired
	InventoryRepository inventoryRepository
	
	@Autowired
	InventoryResource inventoryResource
	
	@Autowired
	InventoryLedgerRepository inventoryLedgerRepository
	
	@Autowired
	ObjectMapper objectMapper
	
	@Autowired
	DocumentTypeRepository documentTypeRepository
	
	@Autowired
	DepartmentRepository departmentRepository
	
	@Autowired
	GeneratorService generatorService

	@Autowired
	ReceivingReportRepository receivingReportRepository

	@Autowired
	PurchaseOrderItemRepository purchaseOrderItemRepository

	@Autowired
	PurchaseOrderRepository purchaseOrderRepository

	@Autowired
	ItemRepository itemRepository

	@Autowired
	LedgerServices ledgerServices

	@Autowired
	ServicePhysicalTransaction servicePhysicalTransaction

	@Autowired
	EntityManager entityManager

	
	@GraphQLQuery(name = "getMonthlyCountByDate", description = "List of Physical Count By Date")
	List<PhysicalCount> getMonthlyCountByDate(@GraphQLArgument(name = "date") Instant date, @GraphQLArgument(name = "dep") UUID dep, @GraphQLArgument(name = "filter") String filter) {
		if (dep) {
			return physicalCountRepository.getMonthlyCountByDateAndDep(date.plus(Duration.ofHours(8)), dep, filter)
		} else {
			return physicalCountRepository.getMonthlyCountByDate(date.plus(Duration.ofHours(8)), filter)
		}
		
	}
	
	@GraphQLQuery(name = "getPhysicalCountById", description = "List of Physical Count By Id")
	PhysicalCount getPhysicalCountById(@GraphQLArgument(name = "id") UUID id) {
		return physicalCountRepository.findById(id).get()
	}
	
	@GraphQLQuery(name = "monthlyCount", description = "List of Logs Count")
	Integer getPhysicalCountById(@GraphQLContext PhysicalCount physicalCount) {
		return physicalCountRepository.getMonthlyCount(physicalCount.id)
	}
	
	@GraphQLQuery(name = "getMonthlyCountByDatePaged", description = "List of Physical Count By Date")
	Page<PhysicalCount> getMonthlyCountByDatePaged(
			@GraphQLArgument(name = "date") Instant date,
			@GraphQLArgument(name = "dep") UUID dep,
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size) {
		if (dep) {
			return physicalCountRepository.getMonthlyCountByDateAndDepPaged(date.plus(Duration.ofHours(8)), dep, filter, new PageRequest(page, size, Sort.Direction.ASC, "item.descLong"))
		} else {
			return physicalCountRepository.getMonthlyCountByDatePaged(date.plus(Duration.ofHours(8)), filter, new PageRequest(page, size, Sort.Direction.ASC, "item.descLong"))
		}
		
	}
	
	@GraphQLQuery(name = "getMonthlyCountList", description = "List of Physical Count By Date")
	List<PhysicalCount> getMonthlyCountByDate() {
		return physicalCountRepository.findAll().sort { it.item.descLong }
	}
	
	//
	//MUTATION
	
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "physicalCountInsert", description = "insert physical Count")
	PhysicalCount physicalCountInsert(
			@GraphQLArgument(name = "departmentId") UUID departmentId,
			@GraphQLArgument(name = "countDate") String countDate,
			@GraphQLArgument(name = "dateCounted") Instant dateCounted,
			@GraphQLArgument(name = "type") String type

	) {
		def data = new PhysicalCount()

		def depInv = inventoryRepository.inventoryByDepartmentAndFilter(departmentId, "")
		def existData = physicalCountRepository.getMonthlyCountByDateAndDep(dateCounted.plus(Duration.ofHours(8)), departmentId, "")
		try {
			if(type == "UPDATE"){
				existData.each {
					it ->
						//getOnHandLastWcost(item, dep, date)
						def rc = inventoryResource.getOnHandLastWcostPhy(it.item.id, it.department.id, countDate, it.id)
						def update = it
						if(!it.isPosted){
							update.onHand = rc.onhand
							update.unitCost =  rc.last_wcost
							update.variance = rc.monthly_count - rc.onhand
							update.wcost = rc.last_wcost
							data = physicalCountRepository.save(update)
						}
				}
			}else{
				depInv.each {
					it ->
						//getOnHandLastWcost(item, dep, date)
						if (!existData.stream().filter({ filter -> filter.item.id.toString().equalsIgnoreCase(it.item.id.toString()) }).findAny().orElse(null)) {
							PhysicalCount insert = new PhysicalCount()
							def rc = inventoryResource.getOnHandLastWcost(it.item.id, it.department.id, countDate)
							insert.refNo = generatorService.getNextValue(GeneratorType.PHY_COUNT) { Long no ->
								'PHY-' + StringUtils.leftPad(no.toString(), 6, "0")
							}
							insert.dateTrans = dateCounted.plus(Duration.ofHours(8))
							insert.item = it.item
							insert.expiration_date =  it.expiration_date
							insert.department = it.department
							insert.onHand = rc.onhand
							insert.quantity = 0
							insert.variance = 0 - rc.onhand
							insert.unitCost = rc.last_wcost
							insert.wcost = rc.last_wcost
							insert.isPosted = false
							insert.isCancel = false
							data = physicalCountRepository.save(insert)
						}
				}
			}

		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		return data
	}
	
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "physicalCountLedger", description = "insert physical Count Ledger")
	InventoryLedger physicalCountLedger(
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		InventoryLedger insert = new InventoryLedger()
		def ledg = objectMapper.convertValue(fields, InventoryLedger)
		def data
		try {
			insert.sourceDep = ledg.sourceDep
			insert.destinationDep = ledg.destinationDep
			insert.documentTypes = ledg.documentTypes
			insert.item = ledg.item
			insert.referenceNo = ledg.referenceNo
			insert.ledgerDate = ledg.ledgerDate.plus(Duration.ofHours(8))
			insert.ledgerQtyIn = ledg.ledgerQtyIn
			insert.ledgerQtyOut = ledg.ledgerQtyOut
			insert.ledgerPhysical = ledg.ledgerPhysical
			insert.ledgerUnitCost = ledg.ledgerUnitCost
			insert.isInclude = true
			data = inventoryLedgerRepository.save(insert)
		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		return data
	}
	
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "massPostInventoryCount", description = "Mass Post to Ledger")
	InventoryLedger massPostInventoryCount(
			@GraphQLArgument(name = "departmentId") UUID departmentId,
			@GraphQLArgument(name = "condition") Integer condition,
			@GraphQLArgument(name = "dateCounted") Instant dateCounted,
			@GraphQLArgument(name = "countDate") String countDate
	) {
		InventoryLedger insert = new InventoryLedger()
		def items = physicalCountRepository.getMonthlyCountByDateAndDepWhereNotPosted(dateCounted.plus(Duration.ofHours(8)), departmentId)
		String docId = '37683c86-3038-4207-baf0-b51456fd7037'
		if (condition == 0) { // all
			items = physicalCountRepository.getMonthlyCountByDateAndDepWhereNotPosted(dateCounted.plus(Duration.ofHours(8)), departmentId)
		} else if (condition == 1) { // Post Items with Monthly Count
			items = physicalCountRepository.getMonthlyCountByDateAndDepWhereCounted(dateCounted.plus(Duration.ofHours(8)), departmentId)
		} else if (condition == 2) { // Post Items with Zero Variance
			items = physicalCountRepository.getMonthlyCountByDateAndDepWhereVarianceZero(dateCounted.plus(Duration.ofHours(8)), departmentId)
		}
		try {
			items.each {
				it ->
					insert = new InventoryLedger()
					LocalTime localTime = LocalTime.now()
					String date = countDate + " " + localTime
					insert.sourceDep = departmentRepository.findById(departmentId).get()
					insert.destinationDep = departmentRepository.findById(departmentId).get()
					insert.documentTypes = documentTypeRepository.findById(UUID.fromString(docId)).get()
					insert.item = it.item
					insert.referenceNo = it.refNo
					insert.ledgerDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")).atZone(ZoneId.of("Asia/Manila")).toInstant().plus(Duration.ofHours(8))
					insert.ledgerQtyIn = it.variance
					insert.ledgerQtyOut = 0
					insert.ledgerPhysical = physicalCountRepository.getMonthlyCount(it.id)
					insert.ledgerUnitCost = it.unitCost
					insert.isInclude = true
					inventoryLedgerRepository.save(insert)
					
					def count = physicalCountRepository.findById(it.id).get()
					count.isPosted = true
					physicalCountRepository.save(count)
			}
			
		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		return insert
	}
	
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "upsertPhysicalCount", description = "insert physical Count Ledger")
	PhysicalCount upsertPhysicalCount(
			@GraphQLArgument(name = "qty") Integer qty,
			@GraphQLArgument(name = "id") UUID id
	) {
		PhysicalCount upsert = physicalCountRepository.findById(id).get()
		try {
			upsert.quantity = qty
			upsert.variance = qty - upsert.onHand
			physicalCountRepository.save(upsert)
		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		return upsert
	}
	
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "voidLedger", description = "void Inventory Ledger")
	InventoryLedger updateInventoryLedgerCost(
			@GraphQLArgument(name = "referenceNo") String referenceNo,
			@GraphQLArgument(name = "type") String type
	) {
		def listLedger = inventoryLedgerRepository.getLedgerByRefNo(referenceNo)
		def upsert = new InventoryLedger()
		try {
			listLedger.each {
				it->
					upsert = it
					upsert.isInclude = false
					inventoryLedgerRepository.save(upsert)
			}
			//update necessary table

			if(type.equalsIgnoreCase('srr')){//update po if naa
				def srr = receivingReportRepository.findReceivingBySRR(referenceNo)
				if(srr.purchaseOrder != null){
					def poItems = purchaseOrderItemRepository.findByPurchaseOrderId(srr.purchaseOrder?.id)
					poItems.each {
						it ->
							def update = it
							update.deliveryStatus = 0
							update.deliveredQty = 0
							update.deliveryBalance = update.qtyInSmall
							purchaseOrderItemRepository.save(update)
					}
					//update parent po
					def po = purchaseOrderRepository.findById(srr.purchaseOrder?.id).get()
					po.isCompleted = false

					//reverse accounting entry
					if(srr.postedLedger){
						def header = ledgerServices.findOne(srr.postedLedger)
						ledgerServices.reverseEntriesCustom(header, srr.receiveDate)
					}
				}
			}

		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		return upsert
	}

	//update ni wilson
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "loadPhysicalCountItems", description = "update insert physical Count")
	GraphQLRetVal<Boolean> loadPhysicalCountItems(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "type") String type

	) {
		def data = new PhysicalCount()
		def parent = servicePhysicalTransaction.physicalTransactionById(id)
		DateTimeFormatter formatter =
				DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault())
		def result = new GraphQLRetVal<Boolean>(true,  true,"Items are now loaded.")

		def depInv = inventoryResource.getOnhandReportByDate(parent.department.id, formatter.format(parent.transDate), "") // not included consignment
		def existData = physicalCountRepository.getPhysicalItemsByTransaction(id)
		try {
			if(type.equalsIgnoreCase("UPDATE")){
				println("UPDATE")
				print('dateformated => ' + formatter.format(parent.transDate))
				existData.each {
					it ->
						def rc = depInv.find{filter -> filter.item == it.item.id}
						def variance = (it.monthlyCount - (rc?.onhand ?: 0))
						def update = it
						update.dateTrans = parent.transDate
						update.onHand = rc?.onhand ?: 0
						update.unitCost =  rc?.last_wcost ?: BigDecimal.ZERO
						update.variance = variance
						update.wcost = rc?.last_wcost ?: BigDecimal.ZERO
						data = physicalCountRepository.save(update)

				}
				result = new GraphQLRetVal<Boolean>(true,  true,"Items are now updated.")
			}else{
				depInv.each {
					it ->
						if (!existData.stream().filter({ filter -> filter.item.id.toString().equalsIgnoreCase(it.item.toString()) }).findAny().orElse(null)) {
							PhysicalCount insert = new PhysicalCount()
							insert.physicalTransaction = parent
							insert.refNo = parent.transNo
							insert.dateTrans = parent.transDate
							insert.item = itemRepository.findById(it.item).get()
							insert.expiration_date =  it.expiration_date ? Instant.parse(it.expiration_date+"T00:00:00Z") : null
							insert.department = parent.department
							insert.onHand = it.onhand
							insert.quantity = 0
							insert.variance = 0 - it.onhand
							insert.unitCost = it.last_wcost ?: BigDecimal.ZERO
							insert.wcost = it.last_wcost ?: BigDecimal.ZERO
							insert.isPosted = false
							insert.isCancel = false
							data = physicalCountRepository.save(insert)
						}
				}
				if(!data){
					result =  new GraphQLRetVal<Boolean>(false,  true,"All items are already loaded.")
				}
			}

		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e.message)
		}
		return result
	}

	//update posted physical count
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "updateStatusPhysicalCount")
	PhysicalCount updateStatusPhysicalCount(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "status") Boolean status,
			@GraphQLArgument(name = "refId") UUID refId

	) {
		def update = physicalCountRepository.findById(id).get()
		try {
			update.isPosted = status
			update.refLedgerId = status ? refId : null
			physicalCountRepository.save(update)

		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e.message)
		}
		return update
	}

	//update unit cost
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "updatePhyUnitCost")
	PhysicalCount updatePhyUnitCost(
			@GraphQLArgument(name = "cost") BigDecimal cost,
			@GraphQLArgument(name = "id") UUID id
	) {
		PhysicalCount upsert = physicalCountRepository.findById(id).get()
		try {
			entityManager.createNativeQuery("""update inventory.inventory_ledger set ledger_unit_cost = :cost where item = :item""")
					.setParameter("cost", cost)
					.setParameter("item", upsert.item.id)
					.executeUpdate()
			upsert.unitCost = cost
			physicalCountRepository.save(upsert)
		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		return upsert
	}
}
