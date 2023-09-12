package com.hisd3.hismk2.graphqlservices.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.accounting.HeaderLedger
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.inventory.DepartmentStockIssue
import com.hisd3.hismk2.domain.inventory.Inventory
import com.hisd3.hismk2.domain.inventory.InventoryLedger
import com.hisd3.hismk2.domain.inventory.PODeliveryMonitoring
import com.hisd3.hismk2.domain.inventory.PurchaseOrderItems
import com.hisd3.hismk2.domain.inventory.ReceivingReport
import com.hisd3.hismk2.domain.inventory.ReturnSupplier
import com.hisd3.hismk2.graphqlservices.accounting.IntegrationServices
import com.hisd3.hismk2.graphqlservices.accounting.LedgerServices
import com.hisd3.hismk2.graphqlservices.accounting.TransactionTypeService
import com.hisd3.hismk2.graphqlservices.billing.BillingItemServices
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.inventory.*
import com.hisd3.hismk2.rest.InventoryResource
import com.hisd3.hismk2.rest.ap.ApvItemsDto
import com.hisd3.hismk2.rest.dto.InventoryLedgerDto
import com.hisd3.hismk2.rest.dto.LedgerDto
import com.hisd3.hismk2.rest.dto.POMonitoringDto
import com.hisd3.hismk2.rest.dto.POReportDto
import com.hisd3.hismk2.rest.dto.PostLedgerDto
import com.hisd3.hismk2.rest.dto.QueryErrorException
import com.hisd3.hismk2.rest.dto.StockCard
import com.hisd3.hismk2.rest.dto.StockCardTransaction
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.InventoryLedgService
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Component
@GraphQLApi
@TypeChecked
class InventoryLedgerService {
	
	@Autowired
	InventoryLedgerRepository inventoryLedgerRepository
	
	@Autowired
	InventoryResource inventoryResource
	
	@Autowired
	EmployeeRepository employeeRepository
	
	@Autowired
	InventoryLedgService inventoryLedgService
	
	@Autowired
	DocumentTypeRepository documentTypeRepository
	
	@Autowired
	ReceivingReportItemRepository receivingReportItemRepository
	
	@Autowired
	PurchaseOrderItemRepository purchaseOrderItemRepository
	
	@Autowired
	ReceivingReportRepository receivingReportRepository
	
	@Autowired
	DepartmentStockIssueRepository departmentStockIssueRepository
	
	@Autowired
	DepartmentStockIssueItemRepository departmentStockIssueItemRepository
	
	@Autowired
	DepartmentStockRequestRepository departmentStockRequestRepository
	
	@Autowired
	DepartmentStockRequestItemRepository departmentStockRequestItemRepository

	@Autowired
	PODeliveryMonitoringService poDeliveryMonitoringService

	@Autowired
	ReturnSupplierRepository returnSupplierRepository
	
	@Autowired
	ReturnSupplierItemRepository returnSupplierItemRepository
	
	@Autowired
	ItemRepository itemRepository

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	MaterialProductionRepository materialProductionRepository

	@Autowired
	ReceivingReportService receivingReportService

	@Autowired
	IntegrationServices integrationServices

	@Autowired
	TransactionTypeService transactionTypeService

	@Autowired
	BeginningTransactionRepository beginningTransactionRepository

	@Autowired
	InventoryAccountingIntegrationService inventoryAccountingIntegrationService

	@Autowired
	LedgerServices ledgerServices

	@Autowired
	DepartmentRepository departmentRepository

	@Autowired
	StockRequestRepository stockRequestRepository

	@Autowired
	BillingItemServices billingItemServices

	@GraphQLQuery(name = "inventoryLedgerList", description = "List of Inventory Ledger")
	List<InventoryLedger> getInventoryLedger() {
		return inventoryLedgerRepository.findAll().sort { it.createdDate }
	}
	
	@GraphQLQuery(name = "stockCard", description = "List of Stock Card")
	List<StockCard> getStockCard(@GraphQLArgument(name = "itemId") String item, @GraphQLArgument(name = "depId") String dep) {
		if (item && dep) {
			return inventoryResource.getLedger(item, dep)
		}else {
			return null
		}
	}

	@GraphQLQuery(name = "stockCardFilter", description = "List of Stock Card with filter")
	List<StockCard> stockCardFilter(@GraphQLArgument(name = "itemId") String item, @GraphQLArgument(name = "depId") String dep) {
		if (item && dep) {
			return inventoryResource.getLedger(item, dep)
		}else if(item && !dep) {
			return inventoryResource.getLedgerAll(item)
		} else {
			return null
		}
	}

	@GraphQLQuery(name = "getPatientInfoByStockCard")
	StockCardTransaction getPatientInfoByStockCard(@GraphQLArgument(name = "refNo") String refNo) {
		def stock = stockRequestRepository.stockRequestBySRNo(refNo)
		def billItem = billingItemServices.getBillingItemByRecordNo(refNo)

		if(stock){
			Employee created = employeeRepository.findByUsername(stock.createdBy).first()
			Employee last = employeeRepository.findByUsername(stock.lastModifiedBy).first()
			def dto = new StockCardTransaction(
					patient: stock.patient.fullName,
					caseNo: stock.patientCase.caseNo,
					type: "MEDICATION REQUEST",
					billNo: "--",
					createdBy: created.fullName,
					lastModifiedBy: last.fullName,
			)
			return dto
		}else if(billItem) {
			def bill = billItem.billing
			Employee created = employeeRepository.findByUsername(billItem.createdBy).first()
			Employee last = employeeRepository.findByUsername(billItem.lastModifiedBy).first()
			def dto = new StockCardTransaction(
					patient: bill.patient ? bill.patient.fullName : bill.otcname,
					caseNo: bill.patientCase ? bill.patientCase.caseNo : "--",
					type: bill.patient ? "DIRECT CHARGED BILLING" : "OTC TRANSACTION",
					billNo: bill.billingNo,
					createdBy: created.fullName,
					lastModifiedBy: last.fullName,
			)
			return dto
		} else{
			return null
		}
	}


	@GraphQLQuery(name = "stockCardAll", description = "List of Stock Card")
	List<StockCard> getStockCardAll(@GraphQLArgument(name = "itemId") String item) {
		if (item) {
			return inventoryResource.getLedgerAll(item)
		} else {
			return null
		}
	}
	
	@GraphQLQuery(name = "stockCardList", description = "List of Stock Card List")
	List<StockCard> getStockCardList(@GraphQLArgument(name = "itemId") String item) {
		Employee e = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()
		return inventoryResource.getLedger(item, e.departmentOfDuty.id as String)
	}
	
	@GraphQLQuery(name = "getOnHandByItem", description = "get on hand by Item")
	Inventory getOnHandByItem(@GraphQLArgument(name = "id") UUID id) {
		Employee e = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()
		return inventoryLedgerRepository.getOnHandByItem(e.departmentOfDuty.id, id)
	}

	@GraphQLQuery(name = "getItemBarcode", description = "getItemBarcode")
	List<Inventory> getItemBarcode(@GraphQLArgument(name = "barcode") String barcode) {
		Employee e = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()
		return inventoryLedgerRepository.getItemBarcode(e.departmentOfDuty.id, barcode)
	}



//    @GraphQLQuery(name = "details", description = "expense details")
//    List<ExpenseDetailsDto> getStockCardList(@GraphQLArgument(name = "itemId") String item) {
//        Employee e = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()
//        return inventoryResource.getLedger(item, e.departmentOfDuty.id as String)
//    }
	
	//MUTATION
	@GraphQLMutation
	@Transactional(rollbackFor = QueryErrorException.class)
	InventoryLedger insertToInventoryLedger(
			@GraphQLArgument(name = "fields") ArrayList<Map<String, Object>> fields,
			@GraphQLArgument(name = "type") String type
	) {
		
		try {
			for (value in fields) {
				inventoryLedgService.InventoryCharge(
						UUID.fromString(value['department'] as String),
						UUID.fromString(value['itemId'] as String),
						value['reference_no'] as String,
						(type),
						value['qty'] as Integer, null, null)
			}
		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		/*
		* [
		* 	{
		* 		department: "",
		* 		itemId: "",
		* 		reference_no: "",
		* 		qty: "",
		* 	}
		* ]
		*
		* */
		
	}
	
	@GraphQLMutation
	@Transactional(rollbackFor = QueryErrorException.class)
	InventoryLedger insertLedgerMaterialProd(
			@GraphQLArgument(name = "fields") ArrayList<Map<String, Object>> fields
	) {
		try {
			for (value in fields) {
				inventoryLedgService.InventoryProduction(value['department'] as String, value['itemId'] as String, value['reference_no'] as String, value['type'] as String, value['qty'] as Integer, value['cost'] as BigDecimal)
			}
		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		/*
		* [
		* 	{
		* 		type: "", // source or output
		* 		department: "",
		* 		itemId: "",
		* 		reference_no: "",
		* 		qty: "",
		* 	}
		* ]
		* //sample data
		* [
		* 	{
		* 		type: "source",
		* 		department: "MMD", // this is depId
		* 		itemId: "SPOON", // tis is itemId
		* 		reference_no: "MP-0001",
		* 		qty: 2,
		* 		cost: 10.00
		* 	},
		* 	{
		* 		type: "source",
		* 		department: "MMD", // this is depId
		* 		itemId: "PORK", // tis is itemId
		* 		reference_no: "MP-0001",
		* 		qty: 2
		* 		cost: 5.00
		* 	},
		* 	{
		* 		type: "output",
		* 		department: "MMD", // this is depId
		* 		itemId: "ADMISSION KIT", // tis is itemId
		* 		reference_no: "MP-0001",
		* 		qty: 2
		* 		cost: 20.00
		* 	},
		* ]
		*
		* */
	}
	
	// mutation //
	
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "saveInventoryLedger", description = "insert Ledger")
	InventoryLedger saveInventoryLedger(
			@GraphQLArgument(name = "items") ArrayList<Map<String, Object>> items,
			@GraphQLArgument(name = "postFrom") String postFrom,
			@GraphQLArgument(name = "parentId") String parentId
	) {
		def upsert = new InventoryLedger()
		def postItems = items as ArrayList<InventoryLedgerDto>
		try {
			if (postFrom.equalsIgnoreCase('receiving')) {
				postItems.each {
					it ->
						upsert = new InventoryLedger()
						LocalTime localTime = LocalTime.now()
						String date = it.ledgerDate + " " + localTime
						def source = objectMapper.convertValue(it.sourceDep, Department)
						def dest = objectMapper.convertValue(it.destDep, Department)
						
						upsert.sourceDep = source
						upsert.destinationDep = dest
						upsert.documentTypes = documentTypeRepository.findById(UUID.fromString(it.typeId)).get()
						upsert.item = itemRepository.findById(UUID.fromString(it.itemId)).get()
						upsert.referenceNo = it.ledgerNo
						upsert.ledgerDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")).atZone(ZoneId.of("Asia/Manila")).toInstant().plus(Duration.ofHours(8))
						upsert.ledgerQtyIn = it.qty
						upsert.ledgerQtyOut = 0
						upsert.ledgerPhysical = 0
						upsert.ledgerUnitCost = it.unitCost
						upsert.isInclude = true
						inventoryLedgerRepository.save(upsert)
						
						//update receiving Item
						def rItems = receivingReportItemRepository.findById(UUID.fromString(it.id)).get()
						rItems.isPosted = true
						receivingReportItemRepository.save(rItems)
						
						//update po item if naa
						if (it.poItem != null) {
							PurchaseOrderItems itemPO = purchaseOrderItemRepository.findById(UUID.fromString(it.poItem)).get()
							def count = itemPO.qtyInSmall - (itemPO.deliveredQty + it.qty)
							itemPO.deliveryBalance = itemPO.qtyInSmall - (itemPO.deliveredQty + it.qty)
							itemPO.deliveredQty = itemPO.deliveredQty + it.qty
							itemPO.deliveryStatus = count == 0 ? 2 : 1
							purchaseOrderItemRepository.save(itemPO)
						}
						
						//add this to viewable into inventory
						inventoryResource.insertIntoDepItem(it.itemId, dest.id.toString())
				}
				//update parent
				def update = receivingReportRepository.findById(UUID.fromString(parentId)).get()
				update.isPosted = true
				def afterSave = receivingReportRepository.save(update)

				//save to Accounting
				def transType = transactionTypeService.transTypeById(afterSave.account)
				if(integrationServices.getIntegrationByDomainAndTagValue(ReceivingReport.class.name, transType.flagValue)){
					receivingReportService.saveToAccounting(afterSave)
				}
				//end save accounting

			} else if (postFrom.equalsIgnoreCase('receiving-new')) {
				postItems = items as ArrayList<PostLedgerDto>
				postItems.each {
					it ->
						upsert = new InventoryLedger()
						def source = objectMapper.convertValue(it.source, Department)
						def dest = objectMapper.convertValue(it.destination, Department)

						upsert.sourceDep = source
						upsert.destinationDep = dest
						upsert.documentTypes = documentTypeRepository.findById(UUID.fromString(it.typeId)).get()
						upsert.item = itemRepository.findById(UUID.fromString(it.itemId)).get()
						upsert.referenceNo = it.ledgerNo
						upsert.ledgerDate = Instant.parse(it.date)
						upsert.ledgerQtyIn = it.qty
						upsert.ledgerQtyOut = 0
						upsert.ledgerPhysical = 0
						upsert.ledgerUnitCost = it.unitcost
						upsert.isInclude = true
						inventoryLedgerRepository.save(upsert)

						//update receiving Item
						def rItems = receivingReportItemRepository.findById(UUID.fromString(it.id)).get()
						rItems.isPosted = true
						receivingReportItemRepository.save(rItems)

						//insert sa PO Monitoring
						if (it.poItem != null) {
							PurchaseOrderItems itemPO = purchaseOrderItemRepository.findById(UUID.fromString(it.poItem)).get()
							def parentRec = receivingReportRepository.findById(UUID.fromString(parentId)).get()
							def mon = new POMonitoringDto(
									purchaseOrderItem: UUID.fromString(it.poItem),
									receivingReport: UUID.fromString(parentId),
									receivingReportItem: UUID.fromString(it.id),
									quantity: it.qty,
									status: it.isPartial ? "PARTIAL DELIVERED" : "DELIVERED",
							)
							poDeliveryMonitoringService.upsertPOMonitoring(mon, null)

							//link po item into receiving id
							itemPO.receivingReport = parentRec
							//-- 0: for delivery 1: partial delivery 2: completed
							itemPO.deliveryStatus = it.isPartial ? 1 : 2
							purchaseOrderItemRepository.save(itemPO)
						}
				}
				//update parent
				def update = receivingReportRepository.findById(UUID.fromString(parentId)).get()
				update.isPosted = true
				def afterSave = receivingReportRepository.save(update)

				//save to Accounting
				if(!update.consignment){
					def transType = transactionTypeService.transTypeById(afterSave.account)
					if(integrationServices.getIntegrationByDomainAndTagValue(ReceivingReport.class.name, transType.flagValue)){
						receivingReportService.saveToJournalEntry(afterSave)
					}
				}
				//end save accounting

			} else if (postFrom.equalsIgnoreCase('request') || postFrom.equalsIgnoreCase('issuance')) {
				postItems.each {
					it ->
						//date time
						upsert = new InventoryLedger()
						LocalTime localTime = LocalTime.now()
						String date = it.ledgerDate + " " + localTime
						//date time
						def source = objectMapper.convertValue(it.sourceDep, Department)
						def dest = objectMapper.convertValue(it.destDep, Department)
						upsert.sourceDep = source
						upsert.destinationDep = dest
						upsert.documentTypes = documentTypeRepository.findById(UUID.fromString(it.typeId)).get()
						upsert.item = itemRepository.findById(UUID.fromString(it.itemId)).get()
						upsert.referenceNo = it.ledgerNo
						upsert.ledgerDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")).atZone(ZoneId.of("Asia/Manila")).toInstant().plus(Duration.ofHours(8))
						upsert.ledgerQtyIn = it.typeDesc.equalsIgnoreCase("STI") ? it.qty : 0
						upsert.ledgerQtyOut = it.typeDesc.equalsIgnoreCase("STO") || it.typeDesc.equalsIgnoreCase("EX") ? it.qty : 0
						upsert.ledgerPhysical = 0
						upsert.ledgerUnitCost = it.unitCost
						upsert.isInclude = true
						inventoryLedgerRepository.save(upsert)
						
						//update request/issue Item
						if (it.typeDesc.equalsIgnoreCase("STO")) {
							if (postFrom.equalsIgnoreCase('request')) {
								def rItems = departmentStockRequestItemRepository.findById(UUID.fromString(it.id)).get()
								rItems.isPosted = true
								departmentStockRequestItemRepository.save(rItems)
							} else if (postFrom.equalsIgnoreCase('issuance')) {
								def issueItems = departmentStockIssueItemRepository.findById(UUID.fromString(it.id)).get()
								issueItems.isPosted = true
								departmentStockIssueItemRepository.save(issueItems)
							}
						}
						//add this to viewable into inventory
						inventoryResource.insertIntoDepItem(it.itemId, dest.id.toString())
				}
				//update parent
				if (postFrom.equalsIgnoreCase('request')) {
					def update = departmentStockRequestRepository.findById(UUID.fromString(parentId)).get()
					update.isPosted = true
					update.isCanceled = false
					departmentStockRequestRepository.save(update)
				} else if (postFrom.equalsIgnoreCase('issuance')) {
					def up = departmentStockIssueRepository.findById(UUID.fromString(parentId)).get()
					up.isPosted = true
					up.isCancel = false
					def newSave = departmentStockIssueRepository.save(up)
					//--uncomment this
					//def headerLedger = inventoryAccountingIntegrationService.addJournalEntryForStockIssuance(newSave)
					//if(headerLedger){
					//	newSave.postedLedger = UUID.fromString(headerLedger)
					//	departmentStockIssueRepository.save(newSave)
					//}
				}
			} else if (postFrom.equalsIgnoreCase('return_supplier')) {
				postItems.each {
					it ->
						//date time
						upsert = new InventoryLedger()
						LocalTime localTime = LocalTime.now()
						String date = it.ledgerDate + " " + localTime
						//date time
						def source = objectMapper.convertValue(it.sourceDep, Department)
						def dest = objectMapper.convertValue(it.destDep, Department)
						upsert.sourceDep = source
						upsert.destinationDep = dest
						upsert.documentTypes = documentTypeRepository.findById(UUID.fromString(it.typeId)).get()
						upsert.item = itemRepository.findById(UUID.fromString(it.itemId)).get()
						upsert.referenceNo = it.ledgerNo
						upsert.ledgerDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")).atZone(ZoneId.of("Asia/Manila")).toInstant().plus(Duration.ofHours(8))
						upsert.ledgerQtyIn = 0
						upsert.ledgerQtyOut = it.qty
						upsert.ledgerPhysical = 0
						upsert.ledgerUnitCost = it.unitCost
						upsert.isInclude = true
						inventoryLedgerRepository.save(upsert)
						
						//update receiving Item
						def rItems = returnSupplierItemRepository.findById(UUID.fromString(it.id)).get()
						rItems.isPosted = true
						returnSupplierItemRepository.save(rItems)
				}
				//update parent
				def update = returnSupplierRepository.findById(UUID.fromString(parentId)).get()
				update.isPosted = true
				returnSupplierRepository.save(update)
			} else if (postFrom.equalsIgnoreCase('beginning_balance')) {

				postItems.each {
					it ->
						upsert = new InventoryLedger()
						LocalTime localTime = LocalTime.now()
						String date = it.ledgerDate + " " + localTime

						def source = objectMapper.convertValue(it.sourceDep, Department)
						def dest = objectMapper.convertValue(it.destDep, Department)

						upsert.sourceDep = source
						upsert.destinationDep = dest
						upsert.documentTypes = documentTypeRepository.findById(UUID.fromString(it.typeId)).get()
						upsert.item = itemRepository.findById(UUID.fromString(it.itemId)).get()
						upsert.referenceNo = it.ledgerNo
						upsert.ledgerDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")).atZone(ZoneId.of("Asia/Manila")).toInstant().plus(Duration.ofHours(8))
						upsert.ledgerQtyIn = it.qty
						upsert.ledgerQtyOut = 0
						upsert.ledgerPhysical = 0
						upsert.ledgerUnitCost = it.unitCost
						upsert.isInclude = true
						inventoryLedgerRepository.save(upsert)

						def begTrans = beginningTransactionRepository.findById(UUID.fromString(parentId)).get()
						begTrans.postedBy = upsert.createdBy
						begTrans.posted = true
						begTrans.status = 'POSTED'
						begTrans.postedLedger = upsert.id

				}
			}
		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		return upsert
	}

	//@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "recalculateMoving", description = "Recalculate Moving Average")
	InventoryLedger updateInventoryLedgerCost(
			@GraphQLArgument(name = "itemId") String itemId
	) {
		def stockCard = inventoryResource.getLedgerAll(itemId)
		def upsert = new InventoryLedger()
		def counter = 0;
		ArrayList<String> list = new ArrayList<>(Arrays.asList("254a07d3-e33a-491c-943e-b3fe6792c5fc", "7b94c82f-081a-4578-82c2-f7343852fcf3", "27d236bb-c023-44dc-beac-18ddfe1daf79","0caab388-e53b-4e94-b2ea-f8cc47df6431","af7dc429-8352-4f09-b58c-26a0a490881c","37683c86-3038-4207-baf0-b51456fd7037"));
		try {
			stockCard.each {
				it ->
					if(counter > 0){
						def prev = inventoryResource.getLedgerWcostAll(itemId, stockCard[counter-1].id)
						if(list.indexOf(it.document_types) < 0 && prev > 0){
							upsert = inventoryLedgerRepository.findById(UUID.fromString(it.id)).get()
							if(upsert.ledgerUnitCost != prev){
								upsert.ledgerUnitCost = prev
								inventoryLedgerRepository.save(upsert)
							}
						}
					}
					counter++
			}

		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		return upsert
	}

//	code ni Dons
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "voidInventoryLedger", description = "void Inventory Ledger")
	InventoryLedger voidInventoryLedger(
			@GraphQLArgument(name = "parentId") UUID parentId,
			@GraphQLArgument(name = "type") String type
	) {
		def upsert = new InventoryLedger()

		if(type.equalsIgnoreCase('return_supplier')){
			def parent = returnSupplierRepository.findById(parentId).get()

			def items = returnSupplierItemRepository.findItemsByReturnSupplierId(parent.id)
			try {
				items.each {
					it->
						it.isPosted = false
						returnSupplierItemRepository.save(it)
				}

			} catch (Exception e) {
				throw new QueryErrorException("Something was Wrong : " + e)
			}

			def listLedger = inventoryLedgerRepository.getLedgerByRefNo(parent.rtsNo)
			try {
				listLedger.each {
					it->
						upsert = it
						upsert.isInclude = false
						inventoryLedgerRepository.save(upsert)
				}

			} catch (Exception e) {
				throw new QueryErrorException("Something was Wrong : " + e)
			}

			parent.isVoid = true
			parent.isPosted = false
			returnSupplierRepository.save(parent)
		}
		else if(type.equalsIgnoreCase('material_production')){
			def parent = materialProductionRepository.findById(parentId).get()

			def listLedger = inventoryLedgerRepository.getLedgerByRefNo(parent.mpNo)
			try {
				listLedger.each {
					it->
						upsert = it
						upsert.isInclude = false
						inventoryLedgerRepository.save(upsert)
				}

			} catch (Exception e) {
				throw new QueryErrorException("Something was Wrong : " + e)
			}

			//update parent
			parent.isPosted = false
			parent.status = "Voided"
			materialProductionRepository.save(parent)
		}
		else if(type.equalsIgnoreCase('stock_issue')){
			def parent = departmentStockIssueRepository.findById(parentId).get()

			def items = departmentStockIssueItemRepository.findItemsByIssue(parent.id)
			try {
				items.each {
					it->
						it.isPosted = false
						departmentStockIssueItemRepository.save(it)
				}

			} catch (Exception e) {
				throw new QueryErrorException("Something was Wrong : " + e)
			}

			def listLedger = inventoryLedgerRepository.getLedgerByRefNo(parent.issueNo)
			try {
				listLedger.each {
					it->
						upsert = it
						upsert.isInclude = false
						inventoryLedgerRepository.save(upsert)
				}

			} catch (Exception e) {
				throw new QueryErrorException("Something was Wrong : " + e)
			}

			//update parent
			parent.isPosted = false
			parent.isCancel = true
			def newSave = departmentStockIssueRepository.save(parent)
//			inventoryAccountingIntegrationService.voidStockIssueJournalEntry(newSave.id)
		}

		return upsert
	}

	//global post inventory -- wilson update final na jud ni
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "postInventoryGlobal")
	InventoryLedger postInventoryGlobal(
			@GraphQLArgument(name = "items") LedgerDto it
	) {
		def upsert = new InventoryLedger()
		upsert.sourceDep = it.sourceDep
		upsert.destinationDep = it.destinationDep
		upsert.documentTypes = documentTypeRepository.findById(it.documentTypes).get()
		upsert.item = itemRepository.findById(it.item).get()
		upsert.referenceNo = it.referenceNo
		upsert.ledgerDate = it.ledgerDate
		upsert.ledgerQtyIn = it.ledgerQtyIn
		upsert.ledgerQtyOut = it.ledgerQtyOut
		upsert.ledgerPhysical = it.ledgerPhysical
		upsert.ledgerUnitCost = it.ledgerUnitCost
		upsert.isInclude = true
		inventoryLedgerRepository.save(upsert)
		return upsert
	}

	//global void
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "voidInventoryGlobal")
	InventoryLedger voidInventoryGlobal(
			@GraphQLArgument(name = "refNo") String refNo
	) {
		def upsert = new InventoryLedger()
		def listLedger = inventoryLedgerRepository.getLedgerByRefNo(refNo)
		try {
			listLedger.each {
				it->
					upsert = it
					upsert.isInclude = false
					inventoryLedgerRepository.save(upsert)
			}

		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		return upsert
	}

	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "deleteInventoryGlobal")
	InventoryLedger deleteInventoryGlobal(
			@GraphQLArgument(name = "refNo") String refNo
	) {
		def upsert = new InventoryLedger()
		def listLedger = inventoryLedgerRepository.getLedgerByRefNo(refNo)
		try {
			listLedger.each {
				it->
					upsert = it
					inventoryLedgerRepository.delete(upsert)
			}

		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		return upsert
	}

	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "voidInventoryGlobalById")
	InventoryLedger voidInventoryGlobalById(
			@GraphQLArgument(name = "id") UUID id
	) {
		def upsert = inventoryLedgerRepository.findById(id).get()
		try {
			upsert.isInclude = false
			inventoryLedgerRepository.save(upsert)
		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		return upsert
	}

	@GraphQLMutation(name = "updateStockCardUnitCost", description = "Update stock card unit cost")
	GraphQLRetVal<Boolean> updateStockCardUnitCost(@GraphQLArgument(name = "itemId") UUID itemId,
												   @GraphQLArgument(name = "unitCost") BigDecimal unitCost)
	{
		try{
			if (itemId) {
				List<InventoryLedger> list = inventoryLedgerRepository.getInvLedgerItemsPerId(itemId)
				if(list){
					list.each {
						it->
							it.ledgerUnitCost = unitCost
							inventoryLedgerRepository.save(it)
					}
					return new GraphQLRetVal<Boolean>(true,true,"Successfully updated.")
				}
				return new GraphQLRetVal<Boolean>(true,true,"No transaction found.")

			} else {
				return new GraphQLRetVal<Boolean>(false,false,"Invalid parameters.")
			}
		}
		catch (e){
			return new GraphQLRetVal<Boolean>(false,false,e.message)
		}
	}
}
