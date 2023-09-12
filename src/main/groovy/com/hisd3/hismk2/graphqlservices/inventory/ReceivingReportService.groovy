package com.hisd3.hismk2.graphqlservices.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.dao.inventory.ReceivingDao
import com.hisd3.hismk2.domain.accounting.AccountReceivable
import com.hisd3.hismk2.domain.accounting.HeaderLedger
import com.hisd3.hismk2.domain.accounting.Integration
import com.hisd3.hismk2.domain.accounting.IntegrationItem
import com.hisd3.hismk2.domain.accounting.JournalType
import com.hisd3.hismk2.domain.accounting.Ledger
import com.hisd3.hismk2.domain.accounting.LedgerDocType
import com.hisd3.hismk2.domain.accounting.TransactionType
import com.hisd3.hismk2.domain.fixed_assets.FixedAssetCategory
import com.hisd3.hismk2.domain.inventory.*
import com.hisd3.hismk2.graphqlservices.accounting.IntegrationServices
import com.hisd3.hismk2.graphqlservices.accounting.LedgerServices
import com.hisd3.hismk2.graphqlservices.accounting.TransactionTypeService
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.inventory.*
import com.hisd3.hismk2.rest.dto.ItemsReceiving
import com.hisd3.hismk2.rest.dto.JournalEntryViewDto
import com.hisd3.hismk2.rest.dto.QueryErrorException
import com.hisd3.hismk2.rest.dto.ReceivingAmountDto
import com.hisd3.hismk2.rest.dto.ReceivingDto
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLContext
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.BooleanUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.xmlsoap.schemas.soap.encoding.Duration

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.Duration

@TypeChecked
@Component
@GraphQLApi
class ReceivingReportService {
	
	@Autowired
	GeneratorService generatorService
	
	@Autowired
	ReceivingReportRepository receivingReportRepository
	
	@Autowired
	ReceivingReportItemRepository receivingReportItemRepository
	
	@Autowired
	DepartmentRepository departmentRepository
	
	@Autowired
	SupplierRepository supplierRepository
	
	@Autowired
	ItemRepository itemRepository
	
	@Autowired
	PaymentTermRepository paymentTermRepository
	
	@Autowired
	PurchaseOrderItemRepository purchaseOrderItemRepository
	
	@Autowired
	ReceivingDao receivingDao
	
	@Autowired
	PurchaseOrderRepository purchaseOrderRepository
	
	@Autowired
	ObjectMapper objectMapper

	@Autowired
	IntegrationServices integrationServices

	@Autowired
	LedgerServices ledgerServices

	@Autowired
	TransactionTypeService transactionTypeService

	@Autowired
	InventoryLedgerService inventoryLedgerService

	@Autowired
	PODeliveryMonitoringService poDeliveryMonitoringService
	
	@GraphQLQuery(name = "receivingReport", description = "list of receiving report")
	List<ReceivingReport> receivingReportList() {
		return receivingDao.findAll().sort { it.createdDate }.reverse(true)
	}
	
	@GraphQLQuery(name = "receivingItems", description = "get list of items")
	Set<ReceivingReportItem> getItems(@GraphQLContext ReceivingReport receivingReport) {
		return receivingDao.getReceivingItems(receivingReport)
	}
	
	@GraphQLQuery(name = "getReceivingById", description = "get receiving report")
	ReceivingReport getReceivingById(@GraphQLArgument(name = "id") UUID id) {
		if(id){
			return receivingDao.getReceivingReport(id)
		}else {
			return null
		}
	}

	@GraphQLQuery(name = "getReceivingNo")
	List<ReceivingReport> getReceivingNo(
			@GraphQLArgument(name = "filter") String filter
	){
		return receivingReportRepository.getReceivingNoReport(filter).sort{it.createdDate}
	}
	
	@GraphQLQuery(name = "receivingReportByDep", description = "get receiving report list by dep")
	List<ReceivingReport> getReceivingByDep(@GraphQLArgument(name = "id") UUID id, @GraphQLArgument(name = "filter") String filter) {
		return receivingReportRepository.findReceivingByDep(id, filter).sort { it.createdDate }.reverse(true)
	}

	@GraphQLQuery(name = "getReceivingByDepRange", description = "get receiving report list by dep")
	List<ReceivingReport> getReceivingByDepRange(@GraphQLArgument(name = "id") UUID id,
												 @GraphQLArgument(name = "filter") String filter,
												 @GraphQLArgument(name = "startDate") String startDate,
												 @GraphQLArgument(name = "endDate") String endDate) {
		return receivingReportRepository.getReceivingByDepRange(id, filter,startDate, endDate ).sort { it.createdDate }.reverse(true)
	}

	@GraphQLQuery(name = "getReceivingByDepRangePage", description = "get receiving report page by dep")
	Page<ReceivingReport> getReceivingByDepRangePage(@GraphQLArgument(name = "id") UUID id,
									@GraphQLArgument(name = "filter") String filter,
									@GraphQLArgument(name = "startDate") String startDate,
									@GraphQLArgument(name = "endDate") String endDate,
									@GraphQLArgument(name = "page") Integer page,
									@GraphQLArgument(name = "pageSize") Integer pageSize,
									@GraphQLArgument(name = "consignment") Boolean consignment,
									@GraphQLArgument(name = "asset") Boolean asset) {
		return receivingReportRepository.getReceivingByDepRangePage(id, filter,startDate, endDate, consignment, asset, new PageRequest(page, pageSize, Sort.Direction.DESC, "createdDate"))
	}
	
	@GraphQLQuery(name = "receivingReportPosted", description = "get receiving report posted list")
	List<ReceivingReport> findReceivingPosted() {
		return receivingReportRepository.findReceivingPosted('SRR').sort { it.createdDate }.reverse(true)
	}

	@GraphQLQuery(name = "sampleVatIin")
	BigDecimal sampleVatIin(@GraphQLArgument(name = "id") UUID id) {
		return receivingReportItemRepository.getNetVatEx(id, true)
	}
	
	// mutation //
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "upsertReceiving", description = "insert BEG")
	ReceivingReport upsertReceiving(
			@GraphQLArgument(name = "receiving") Map<String, Object> receiving,
			@GraphQLArgument(name = "receivingItems") ArrayList<Map<String, Object>> receivingItems,
			@GraphQLArgument(name = "receivingId") String receivingId
	) {
		def upsert = new ReceivingReport()
		def rec = objectMapper.convertValue(receiving, ReceivingDto)
		def items = receivingItems as ArrayList<ItemsReceiving>
		Boolean status = true
		try {
			String type
			def ref_no
			if (rec.donation) {
				type = 'DR'
			} else {
				type = rec.emergency_purchase ? 'EP' : 'SRR'
			}
			if (receivingId) { //update
				upsert = receivingReportRepository.findById(UUID.fromString(receivingId)).get()
				ref_no = upsert.rrNo.split("-")
				upsert.receivedType = type
				upsert.rrNo = type + '-' + ref_no[1]
				upsert.receiveDate = rec.date_delivered
				if (rec.poNumber) {
					upsert.purchaseOrder = purchaseOrderRepository.findById(UUID.fromString(rec.poNumber)).get()
				}
				upsert.receivedRefNo = rec.refSINo
				upsert.receivedRefDate = rec.refSI_date
				upsert.receiveDepartment = departmentRepository.findById(UUID.fromString(rec.receiving_dept)).get()
				upsert.supplier = supplierRepository.findById(UUID.fromString(rec.supplier)).get()
				upsert.paymentTerms = paymentTermRepository.findById(UUID.fromString(rec.payment_terms)).get()
				upsert.receivedRemarks = rec.remarks
				
				upsert.grossAmount = rec.grossAmount
				upsert.totalDiscount = rec.totalDiscount
				upsert.netDiscount = rec.netDiscount
				upsert.amount = rec.amount
				upsert.vatRate = rec.vatRate
				upsert.vatInclusive = rec.vatInclusive
				upsert.inputTax = rec.inputTax
				upsert.netAmount = rec.netAmount
				upsert.account = rec.account
				
				//loop items
				items.each {
					it ->
						def item = objectMapper.convertValue(it.item, Item)
						def POItem = objectMapper.convertValue(it.refPoItem, PurchaseOrderItems)
						if (it.isNew) {
							def receiveItem = new ReceivingReportItem()
							receiveItem.receivingReport = upsert
							receiveItem.item = item
							if (it.refPoItem != null) {
								receiveItem.refPoItem = POItem
							}
							receiveItem.receiveQty = it.receiveQty
							receiveItem.receiveUnitCost = it.receiveUnitCost
							receiveItem.receiveDiscountCost = it.receiveDiscountCost
							receiveItem.isFg = it.isFg
							receiveItem.isDiscount = it.isDiscount
							receiveItem.isPartial = it.isPartial
							receiveItem.isCompleted = it.isCompleted
							receiveItem.isTax = it.isTax
							if (it.expirationDate != null) {
								receiveItem.expirationDate = Instant.parse(it.expirationDate)
							}
							receiveItem.inputTax = it.inputTax
							receiveItem.totalAmount = it.totalAmount
							receiveItem.netAmount = it.netAmount
							receivingReportItemRepository.save(receiveItem)
							
							//update po
							if (it.refPoItem != null) {
								PurchaseOrderItems itemPO = purchaseOrderItemRepository.findById(POItem.id).get()
								itemPO.deliveryStatus = it.isCompleted ? 2 : it.isPartial ? 1 : 0
								itemPO.receivingReport = upsert
								itemPO.deliveryBalance = itemPO.qtyInSmall
								itemPO.deliveredQty = itemPO.deliveredQty
								purchaseOrderItemRepository.save(itemPO)
							}
							if (it.isPartial) {
								status = false
							}
						} else {
							def rItems = receivingReportItemRepository.findById(UUID.fromString(it.id)).get()
							rItems.receiveQty = it.receiveQty
							rItems.receiveUnitCost = it.receiveUnitCost
							rItems.receiveDiscountCost = it.receiveDiscountCost
							rItems.isFg = it.isFg
							rItems.isDiscount = it.isDiscount
							rItems.isPartial = it.isPartial
							rItems.isCompleted = it.isCompleted
							rItems.isTax = it.isTax
							if (it.expirationDate != null) {
								rItems.expirationDate = Instant.parse(it.expirationDate)
							}
							rItems.inputTax = it.inputTax
							rItems.totalAmount = it.totalAmount
							rItems.netAmount = it.netAmount
							receivingReportItemRepository.save(rItems)
							//update po item
							if (it.refPoItem != null) {
								PurchaseOrderItems itemPO = purchaseOrderItemRepository.findById(POItem.id).get()
								itemPO.deliveryStatus = it.isCompleted ? 2 : it.isPartial ? 1 : 0
								itemPO.receivingReport = upsert
								itemPO.deliveryBalance = itemPO.qtyInSmall
								itemPO.deliveredQty = itemPO.deliveredQty
								purchaseOrderItemRepository.save(itemPO)
							}
							if (it.isPartial) {
								status = false
							}
						}
				}
				//update parent PO
				if (rec.poNumber) {
					def itemsPO = purchaseOrderItemRepository.findPOItemWithOutRec(UUID.fromString(rec.poNumber))
					status = itemsPO ? false : true

					PurchaseOrder PO = purchaseOrderRepository.findById(UUID.fromString(rec.poNumber)).get()
					PO.isCompleted = status
					purchaseOrderRepository.save(PO)
				}
			} else { //insert
				upsert.receivedType = type
				upsert.rrNo = generatorService.getNextValue(GeneratorType.SRR_NO) { Long no ->
					type + '-' + StringUtils.leftPad(no.toString(), 6, "0")
				}
				upsert.receiveDate = rec.date_delivered
				if (rec.poNumber) {
					upsert.purchaseOrder = purchaseOrderRepository.findById(UUID.fromString(rec.poNumber)).get()
				}
				upsert.userId = UUID.fromString(rec.userId)
				upsert.userFullname = rec.userFullname
				upsert.receivedRefNo = rec.refSINo
				upsert.receivedRefDate = rec.refSI_date
				upsert.receiveDepartment = departmentRepository.findById(UUID.fromString(rec.receiving_dept)).get()
				upsert.supplier = supplierRepository.findById(UUID.fromString(rec.supplier)).get()
				upsert.paymentTerms = paymentTermRepository.findById(UUID.fromString(rec.payment_terms)).get()
				upsert.receivedRemarks = rec.remarks
				
				upsert.grossAmount = rec.grossAmount
				upsert.totalDiscount = rec.totalDiscount
				upsert.netDiscount = rec.netDiscount
				upsert.amount = rec.amount
				upsert.vatRate = rec.vatRate
				upsert.vatInclusive = rec.vatInclusive
				upsert.inputTax = rec.inputTax
				upsert.netAmount = rec.netAmount
				upsert.account = rec.account
				upsert.consignment = rec.consignment

				def afterSave = receivingReportRepository.save(upsert)
				
				//loop items
				items.each {
					def receiveItem = new ReceivingReportItem()
					def item = objectMapper.convertValue(it.item, Item)
					def POItem = objectMapper.convertValue(it.refPoItem, PurchaseOrderItems)
					receiveItem.receivingReport = afterSave
					receiveItem.item = item
					if (it.refPoItem != null) {
						receiveItem.refPoItem = POItem
					}
					receiveItem.receiveQty = it.receiveQty
					receiveItem.receiveUnitCost = it.receiveUnitCost
					receiveItem.receiveDiscountCost = it.receiveDiscountCost
					receiveItem.isFg = it.isFg
					receiveItem.isDiscount = it.isDiscount
					receiveItem.isPartial = it.isPartial
					receiveItem.isCompleted = it.isCompleted
					receiveItem.isTax = it.isTax
					if (it.expirationDate != null) {
						receiveItem.expirationDate = Instant.parse(it.expirationDate)
					}
					receiveItem.inputTax = it.inputTax
					receiveItem.totalAmount = it.totalAmount
					receiveItem.netAmount = it.netAmount
					receivingReportItemRepository.save(receiveItem)
					
					//update po item
					if (it.refPoItem != null) {
						PurchaseOrderItems itemPO = purchaseOrderItemRepository.findById(POItem.id).get()
						itemPO.deliveryStatus = it.isCompleted ? 2 : it.isPartial ? 1 : 0
						itemPO.receivingReport = afterSave
						itemPO.deliveryBalance = itemPO.qtyInSmall
						itemPO.deliveredQty = itemPO.deliveredQty
						purchaseOrderItemRepository.save(itemPO)
					}
					if (it.isPartial) {
						status = false
					}
				}
				//update parent PO
				if (rec.poNumber) {
					def itemsPO = purchaseOrderItemRepository.findPOItemWithOutRec(UUID.fromString(rec.poNumber))
					status = itemsPO ? false : true
					PurchaseOrder PO = purchaseOrderRepository.findById(UUID.fromString(rec.poNumber)).get()
					PO.isCompleted = status
					purchaseOrderRepository.save(PO)
				}

			}
			
		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		return upsert
	}

	//update receiving new
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "upsertNewReceiving", description = "insert BEG")
	ReceivingReport upsertNewReceiving(
			@GraphQLArgument(name = "receiving") Map<String, Object> receiving,
			@GraphQLArgument(name = "receivingItems") ArrayList<Map<String, Object>> receivingItems,
			@GraphQLArgument(name = "deleted") ArrayList<Map<String, Object>> deleted,
			@GraphQLArgument(name = "receivingId") String receivingId
	) {
		def upsert = new ReceivingReport()
		def rec = objectMapper.convertValue(receiving, ReceivingDto.class)
		def items = receivingItems as ArrayList<ItemsReceiving>
		def deleteItems = deleted as ArrayList<ReceivingReportItem>
		try {
			String type = 'SRR'
			if(rec.consignment){
				type = 'CS-SRR'
			}
			if(rec.asset){
				type = 'FA-SRR'
			}
			if (receivingId) { //update
				upsert = receivingReportRepository.findById(UUID.fromString(receivingId)).get()
				upsert.receivedType = type
				upsert.receiveDate = rec.date_delivered
				if (rec.poNumber) {
					upsert.purchaseOrder = purchaseOrderRepository.findById(UUID.fromString(rec.poNumber)).get()
				}
				upsert.receivedRefNo = rec.refSINo
				upsert.receivedRefDate = rec.refSI_date
				upsert.receiveDepartment = departmentRepository.findById(UUID.fromString(rec.receiving_dept)).get()
				upsert.supplier = supplierRepository.findById(UUID.fromString(rec.supplier)).get()
				upsert.paymentTerms = paymentTermRepository.findById(UUID.fromString(rec.payment_terms)).get()
				upsert.receivedRemarks = rec.remarks

				upsert.grossAmount = rec.grossAmount
				upsert.totalDiscount = rec.totalDiscount
				upsert.netDiscount = rec.netDiscount
				upsert.amount = rec.amount
				upsert.inputTax = rec.inputTax
				upsert.netAmount = rec.netAmount
				upsert.vatRate = rec.vatRate
				upsert.vatInclusive = rec.vatInclusive
				upsert.account = rec.account
				upsert.consignment = rec.consignment
				upsert.asset = rec.asset

				//loop items
				items.each {
					it ->
						def item = objectMapper.convertValue(it.item, Item)
						def POItem = objectMapper.convertValue(it.refPoItem, PurchaseOrderItems)
						if (it.isNew) {
							def receiveItem = new ReceivingReportItem()
							receiveItem.receivingReport = upsert
							receiveItem.item = item
							if (it.refPoItem != null) {
								receiveItem.refPoItem = POItem
							}
							receiveItem.receiveQty = it.receiveQty
							receiveItem.receiveUnitCost = it.receiveUnitCost
							receiveItem.recInventoryCost = it.recInventoryCost
							receiveItem.receiveDiscountCost = it.receiveDiscountCost
							receiveItem.isFg = it.isFg
							receiveItem.isDiscount = it.isDiscount
							receiveItem.isPartial = it.isPartial
							receiveItem.isCompleted = it.isCompleted
							receiveItem.isTax = it.isTax
							if (it.expirationDate != null) {
								def exDate = Instant.parse(it.expirationDate)
								receiveItem.expirationDate = exDate + Duration.ofHours(8)
							}else{
								receiveItem.expirationDate = null
							}
							receiveItem.lotNo = it.lotNo
							receiveItem.inputTax = it.inputTax
							receiveItem.totalAmount = it.totalAmount
							receiveItem.netAmount = it.netAmount
							receivingReportItemRepository.save(receiveItem)
						} else {
							def rItems = receivingReportItemRepository.findById(UUID.fromString(it.id)).get()
							rItems.receiveQty = it.receiveQty
							rItems.receiveUnitCost = it.receiveUnitCost
							rItems.recInventoryCost = it.recInventoryCost
							rItems.receiveDiscountCost = it.receiveDiscountCost
							rItems.isFg = it.isFg
							rItems.isDiscount = it.isDiscount
							rItems.isPartial = it.isPartial
							rItems.isCompleted = it.isCompleted
							rItems.isTax = it.isTax
							if (it.expirationDate != null) {
								def exDate = Instant.parse(it.expirationDate)
								rItems.expirationDate = exDate + Duration.ofHours(8)
							}else{
								rItems.expirationDate = null
							}
							rItems.lotNo = it.lotNo
							rItems.inputTax = it.inputTax
							rItems.totalAmount = it.totalAmount
							rItems.netAmount = it.netAmount
							receivingReportItemRepository.save(rItems)
						}
				}

			} else { //insert
				upsert.receivedType = type
				if(rec.consignment){
					upsert.rrNo = generatorService.getNextValue(GeneratorType.SRR_NO_CS) { Long no ->
						type + '-' + StringUtils.leftPad(no.toString(), 6, "0")
					}
				}else if(rec.asset){
					upsert.rrNo = generatorService.getNextValue(GeneratorType.SRR_NO_FA) { Long no ->
						type + '-' + StringUtils.leftPad(no.toString(), 6, "0")
					}
				}else{
					upsert.rrNo = generatorService.getNextValue(GeneratorType.SRR_NO) { Long no ->
						type + '-' + StringUtils.leftPad(no.toString(), 6, "0")
					}
				}

				upsert.receiveDate = rec.date_delivered
				if (rec.poNumber) {
					upsert.purchaseOrder = purchaseOrderRepository.findById(UUID.fromString(rec.poNumber)).get()
				}
				upsert.userId = UUID.fromString(rec.userId)
				upsert.userFullname = rec.userFullname
				upsert.receivedRefNo = rec.refSINo
				upsert.receivedRefDate = rec.refSI_date
				upsert.receiveDepartment = departmentRepository.findById(UUID.fromString(rec.receiving_dept)).get()
				upsert.supplier = supplierRepository.findById(UUID.fromString(rec.supplier)).get()
				upsert.paymentTerms = paymentTermRepository.findById(UUID.fromString(rec.payment_terms)).get()
				upsert.receivedRemarks = rec.remarks

				upsert.grossAmount = rec.grossAmount
				upsert.totalDiscount = rec.totalDiscount
				upsert.netDiscount = rec.netDiscount
				upsert.amount = rec.amount
				upsert.inputTax = rec.inputTax
				upsert.netAmount = rec.netAmount
				upsert.vatRate = rec.vatRate
				upsert.vatInclusive = rec.vatInclusive
				upsert.account = rec.account
				upsert.consignment = rec.consignment
				upsert.asset = rec.asset

				def afterSave = receivingReportRepository.save(upsert)

				//loop items
				items.each {
					def receiveItem = new ReceivingReportItem()
					def item = objectMapper.convertValue(it.item, Item)
					def POItem = objectMapper.convertValue(it.refPoItem, PurchaseOrderItems)
					receiveItem.receivingReport = afterSave
					receiveItem.item = item
					if (it.refPoItem != null) {
						receiveItem.refPoItem = POItem
					}
					receiveItem.receiveQty = it.receiveQty
					receiveItem.receiveUnitCost = it.receiveUnitCost
					receiveItem.recInventoryCost = it.recInventoryCost
					receiveItem.receiveDiscountCost = it.receiveDiscountCost
					receiveItem.isFg = it.isFg
					receiveItem.isDiscount = it.isDiscount
					receiveItem.isPartial = it.isPartial
					receiveItem.isCompleted = it.isCompleted
					receiveItem.isTax = it.isTax
					if (it.expirationDate != null) {
						def exDate = Instant.parse(it.expirationDate)
						receiveItem.expirationDate =  exDate + Duration.ofHours(8)
					}
					receiveItem.lotNo = it.lotNo
					receiveItem.inputTax = it.inputTax
					receiveItem.totalAmount = it.totalAmount
					receiveItem.netAmount = it.netAmount
					receivingReportItemRepository.save(receiveItem)
				}
			}
			//delete Items if naa
			if(deleteItems){
				deleteItems.each {
					def recItem = objectMapper.convertValue(it, ReceivingReportItem.class)
					receivingReportItemRepository.delete(recItem)
				}
			}


		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		return upsert
	}

	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "overrideRecItems")
	ReceivingReport overrideRecItems(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "po") UUID po,
			@GraphQLArgument(name = "toDelete") ArrayList<Map<String, Object>> toDelete,
			@GraphQLArgument(name = "toInsert") ArrayList<Map<String, Object>> toInsert,
			@GraphQLArgument(name = "amount") Map<String, Object> amount

	) {
		def amountObj = objectMapper.convertValue(amount, ReceivingAmountDto.class)
		def upsert = receivingReportRepository.findById(id).get()
		upsert.purchaseOrder = po ? purchaseOrderRepository.findById(po).get() : null
		upsert.grossAmount = amountObj.grossAmount
		upsert.totalDiscount = amountObj.totalDiscount
		upsert.netDiscount = amountObj.netDiscount
		upsert.inputTax = amountObj.inputTax
		upsert.netAmount = amountObj.netAmount
		upsert.amount = amountObj.amount
		try {
			if(toDelete){
				def d = toDelete as ArrayList<ReceivingReportItem>
				d.each {
					def deleted = objectMapper.convertValue(it, ReceivingReportItem.class)
					receivingReportItemRepository.delete(deleted)
				}
			}

			if(toInsert){
				def items = toInsert as ArrayList<ItemsReceiving>
				//loop items
				items.each {
					def receiveItem = new ReceivingReportItem()
					def item = objectMapper.convertValue(it.item, Item)
					def POItem = objectMapper.convertValue(it.refPoItem, PurchaseOrderItems)
					receiveItem.receivingReport = upsert
					receiveItem.item = item
					if (it.refPoItem != null) {
						receiveItem.refPoItem = POItem
					}
					receiveItem.receiveQty = it.receiveQty
					receiveItem.receiveUnitCost = it.receiveUnitCost
					receiveItem.receiveDiscountCost = it.receiveDiscountCost
					receiveItem.isFg = it.isFg
					receiveItem.isDiscount = it.isDiscount
					receiveItem.isPartial = it.isPartial
					receiveItem.isCompleted = it.isCompleted
					receiveItem.isTax = it.isTax
					if (it.expirationDate != null) {
						receiveItem.expirationDate = Instant.parse(it.expirationDate)
					}
					receiveItem.inputTax = it.inputTax
					receiveItem.totalAmount = it.totalAmount
					receiveItem.netAmount = it.netAmount
					receivingReportItemRepository.save(receiveItem)
				}
			}
			receivingReportRepository.save(upsert)
		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		return upsert
	}


	//code ni donss
	@GraphQLQuery(name = "getSrrByDateRange", description = "List of receiving report list per date range")
	List<ReceivingReport> getSrrPerDateRange(@GraphQLArgument(name = "start") Instant start,
											 @GraphQLArgument(name = "end") Instant end,
											 @GraphQLArgument(name = "filter") String filter,
											 @GraphQLArgument(name = "supplier") UUID supplier) {

		Instant fromDate = start.atZone(ZoneId.systemDefault()).toInstant()
		Instant toDate = end.atZone(ZoneId.systemDefault()).toInstant()

		if(supplier){
			return receivingReportRepository.getSrrByDateRangeSupplier(fromDate, toDate,filter, supplier)
		}else{
			return receivingReportRepository.getSrrByDateRange(fromDate, toDate,filter)
		}

	}

	HeaderLedger getDeliveryReceivingReport(ReceivingReport receivingReport, TransactionType type){
		try{
			BigDecimal med = BigDecimal.ZERO;
			BigDecimal sup = BigDecimal.ZERO;
			BigDecimal grandTotal = BigDecimal.ZERO;
			if(type.flagValue.equalsIgnoreCase('FA_DELIVERY_RECEIVING'))
				return integrationServices.generateAutoEntries(receivingReport){
						it , multiple ->
					it.flagValue = type.flagValue

					List<ReceivingReport> perCategories = []
					List<ReceivingReportItem> items = new ArrayList<>(receivingReport.receivingItems)
					items.each {
						recItem ->
							perCategories << new ReceivingReport().tap {
								if(recItem.item.fixedAssetCategory) {
									fixedAssetCategory = recItem.item.fixedAssetCategory
									fixedAsset = recItem.netAmount
									grandTotal += recItem.netAmount
								}
							}
					}
					multiple << perCategories
					it.clearingAmount = grandTotal
				}

			return	integrationServices.generateAutoEntries(receivingReport){
				it, mul ->
					//calculate
					if(it.vatInclusive){
						med = receivingReportItemRepository.getNetVatIn(receivingReport.id, true)
						sup = receivingReportItemRepository.getNetVatIn(receivingReport.id, false)
					}else{
						med = receivingReportItemRepository.getNetVatEx(receivingReport.id, true)
						sup = receivingReportItemRepository.getNetVatEx(receivingReport.id, false)
					}
					//end calculation
					it.flagValue = type.flagValue

					// Revenue for that department
					it.receiveDepartment = receivingReport.receiveDepartment

					it.inputTax = receivingReport.inputTax.round(2)
					it.clearingAmount = receivingReport.vatInclusive ? receivingReport.grossAmount.round(2) : (receivingReport.amount.round(2) + receivingReport.totalDiscount.round(2))
					it.supplies = sup.round(2)
					it.medicine = med.round(2)
			}
		}catch(ignored) {
			return null
		}

	}

	//accounting integration
	@Transactional(rollbackFor = QueryErrorException.class)
	ReceivingReport saveToAccounting(ReceivingReport receivingReport){
		def yearFormat = DateTimeFormatter.ofPattern("yyyy")
		def rec = receivingReportRepository.save(receivingReport) as ReceivingReport
		BigDecimal med = BigDecimal.ZERO; BigDecimal sup = BigDecimal.ZERO;
		def transType = transactionTypeService.transTypeById(rec.account)
		try{
//			def headerLedger =	integrationServices.generateAutoEntries(receivingReport){
//				it, mul ->
//					//calculate
//					if(it.vatInclusive){
//						med = receivingReportItemRepository.getNetVatIn(rec.id, true)
//						sup = receivingReportItemRepository.getNetVatIn(rec.id, false)
//					}else{
//						med = receivingReportItemRepository.getNetVatEx(rec.id, true)
//						sup = receivingReportItemRepository.getNetVatEx(rec.id, false)
//					}
//					//end calculation
//					it.flagValue = transType.flagValue
//
//					// Revenue for that department
//					it.receiveDepartment = rec.receiveDepartment
//
//					it.inputTax = rec.inputTax.round(2)
//					it.clearingAmount = rec.vatInclusive ? rec.grossAmount.round(2) : (rec.amount.round(2) + rec.totalDiscount.round(2))
//					it.supplies = sup.round(2)
//					it.medicine = med.round(2)
//			}

			def headerLedger = getDeliveryReceivingReport(receivingReport,transType)
			Map<String,String> details = [:]

			rec.details.each { k,v ->
				details[k] = v
			}

			details["SRR_ID"] = rec.id.toString()
			details["SUPPLIER_ID"] = rec.supplier.id.toString()

			def pHeader =	ledgerServices.persistHeaderLedger(headerLedger,
					"${rec.receiveDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${rec.rrNo}",
					"${rec.rrNo}-${rec.supplier?.supplierFullname}",
					"${rec.rrNo}-${rec.supplier?.supplierFullname}",
					LedgerDocType.RR,
					JournalType.PURCHASES_PAYABLES,
					rec.createdDate,
					details)
			rec.postedLedger = pHeader.id

//			if(rec.amount < 0.0)
//			{
//				pHeader.reversal = true
//				ledgerServices.save(pHeader)
//			}
			receivingReportRepository.save(rec)

		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}

		return rec
	}

	//receiving journal entries view
	@GraphQLQuery(name = "recAccountView")
	List<JournalEntryViewDto> recAccountView(
			@GraphQLArgument(name = "id") UUID id
	){
		def result = new ArrayList<JournalEntryViewDto>()
		def rec = receivingReportRepository.findById(id).get()
		BigDecimal med = BigDecimal.ZERO; BigDecimal sup = BigDecimal.ZERO;
		def transType = transactionTypeService.transTypeById(rec.account)
		if(transType){
			def headerLedger =	integrationServices.generateAutoEntries(rec){
				it, mul ->
					//calculate
					if(it.vatInclusive){
						med = receivingReportItemRepository.getNetVatIn(rec.id, true)
						sup = receivingReportItemRepository.getNetVatIn(rec.id, false)
					}else{
						med = receivingReportItemRepository.getNetVatEx(rec.id, true)
						sup = receivingReportItemRepository.getNetVatEx(rec.id, false)
					}
					//end calculation
					it.flagValue = transType.flagValue

					// Revenue for that department
					it.receiveDepartment = rec.receiveDepartment

					it.inputTax = rec.inputTax.round(2)
					it.clearingAmount = rec.vatInclusive ? rec.grossAmount.round(2) : (rec.amount.round(2) + rec.totalDiscount.round(2))
					it.supplies = sup.round(2)
					it.medicine = med.round(2)
			}

			Set<Ledger> ledger = new HashSet<Ledger>(headerLedger.ledger);
			ledger.each {
				def list = new JournalEntryViewDto(
						code: it.journalAccount.code,
						desc: it.journalAccount.description,
						debit: it.debit,
						credit: it.credit
				)
				result.add(list)
			}
		}
		return result
	}

	//new receiving journal entries view
	@GraphQLQuery(name = "recJournalView")
	List<JournalEntryViewDto> recJournalView(
			@GraphQLArgument(name = "id") UUID id
	) {
		def result = new ArrayList<JournalEntryViewDto>()
		def rec = receivingReportRepository.findById(id).get()
		def recItems = receivingReportItemRepository.findItemsByReceivingReportId(id)
		def transType = transactionTypeService.transTypeById(rec.account)

		if(!rec.postedLedger && transType && !rec.consignment){
			Integration match = integrationServices.getIntegrationByDomainAndTagValue(rec.domain, transType.flagValue)

			def headerLedger =	integrationServices.generateAutoEntries(rec){
				it, mul ->
					it.flagValue = transType.flagValue

					Map<AccountingCategory, BigDecimal> accounts  = [:]

					//initialize
					Map<String, List<ReceivingReport>> finalAcc  = [:]
					match.integrationItems.findAll { BooleanUtils.isTrue(it.multiple) }.eachWithIndex { IntegrationItem entry, int i ->
						if(!finalAcc.containsKey(entry.sourceColumn)){
							finalAcc[entry.sourceColumn] = []
						}
					}
					//end init

					//loop items
					recItems.each { a ->
						if(!accounts.containsKey(a.item.accountingCategory))
							accounts[a.item.accountingCategory] = 0.0
						if(rec.vatInclusive){
							def totalAmount =  a.totalAmount ?: BigDecimal.ZERO
							def inputTax = a.inputTax ?: BigDecimal.ZERO
							def sum = totalAmount - inputTax
							accounts[a.item.accountingCategory] =  accounts[a.item.accountingCategory] + sum
						}else{
							def totalAmount =  a.totalAmount ?: BigDecimal.ZERO
							accounts[a.item.accountingCategory] =  accounts[a.item.accountingCategory] + totalAmount
						}
					}

					//loop accounts
					accounts.each {k, v ->
						if(v > 0){
							finalAcc[k.sourceColumn] << new ReceivingReport().tap {
								it.cost = BigDecimal.ZERO;
								it.supplies = BigDecimal.ZERO;
								it.medicine = BigDecimal.ZERO;
								it.clearingAmount = BigDecimal.ZERO;
								//init asset
								it.asset_a = BigDecimal.ZERO;it.asset_b = BigDecimal.ZERO;it.asset_c = BigDecimal.ZERO;
								it.asset_d = BigDecimal.ZERO;it.asset_e = BigDecimal.ZERO;it.asset_f = BigDecimal.ZERO;
								it.asset_g = BigDecimal.ZERO;it.asset_h = BigDecimal.ZERO;it.asset_i = BigDecimal.ZERO;
								it.asset_j = BigDecimal.ZERO;it.asset_k = BigDecimal.ZERO;it.asset_l = BigDecimal.ZERO;
								it.asset_m = BigDecimal.ZERO;it.asset_n = BigDecimal.ZERO;it.asset_o = BigDecimal.ZERO;
								it.asset_p = BigDecimal.ZERO;it.asset_q = BigDecimal.ZERO;it.asset_r = BigDecimal.ZERO;
								it.asset_s = BigDecimal.ZERO;it.asset_t = BigDecimal.ZERO;it.asset_u = BigDecimal.ZERO;
								it.asset_v = BigDecimal.ZERO;it.asset_w = BigDecimal.ZERO;it.asset_x = BigDecimal.ZERO;
								it.asset_y = BigDecimal.ZERO;it.asset_z = BigDecimal.ZERO;
								//init expense
								it.expense_a = BigDecimal.ZERO;it.expense_b = BigDecimal.ZERO;it.expense_c = BigDecimal.ZERO;
								it.expense_d = BigDecimal.ZERO;it.expense_e = BigDecimal.ZERO;it.expense_f = BigDecimal.ZERO;
								it.expense_g = BigDecimal.ZERO;it.expense_h = BigDecimal.ZERO;it.expense_i = BigDecimal.ZERO;
								it.expense_j = BigDecimal.ZERO;it.expense_k = BigDecimal.ZERO;it.expense_l = BigDecimal.ZERO;
								it.expense_m = BigDecimal.ZERO;it.expense_n = BigDecimal.ZERO;it.expense_o = BigDecimal.ZERO;
								it.expense_p = BigDecimal.ZERO;it.expense_q = BigDecimal.ZERO;it.expense_r = BigDecimal.ZERO;
								it.expense_s = BigDecimal.ZERO;it.expense_t = BigDecimal.ZERO;it.expense_u = BigDecimal.ZERO;
								it.expense_v = BigDecimal.ZERO;it.expense_w = BigDecimal.ZERO;it.expense_x = BigDecimal.ZERO;
								it.expense_y = BigDecimal.ZERO;it.expense_z = BigDecimal.ZERO;

								it.receiveDepartment = rec.receiveDepartment
								it.category = k
								it[k.sourceColumn] = v;
								//println("k.sourceColumn => " + k.sourceColumn + " - " + k.categoryDescription)
							}
 						}
					}

					//loop multiples
					finalAcc.each { key, items ->
						mul << items
					}

					// Revenue for that department
					it.receiveDepartment = rec.receiveDepartment

					it.inputTax = rec.inputTax
					it.clearingAmount = rec.vatInclusive ? rec.grossAmount : (rec.amount + rec.totalDiscount)


			}

			Set<Ledger> ledger = new HashSet<Ledger>(headerLedger.ledger);
			ledger.each {
				def list = new JournalEntryViewDto(
						code: it.journalAccount.code,
						desc: it.journalAccount.description,
						debit: it.debit,
						credit: it.credit
				)
				result.add(list)
			}
		}else{
			if(rec.postedLedger) {
				def header = ledgerServices.findOne(rec.postedLedger)
				Set<Ledger> ledger = new HashSet<Ledger>(header.ledger);
				ledger.each {
					def list = new JournalEntryViewDto(
							code: it.journalAccount.code,
							desc: it.journalAccount.description,
							debit: it.debit,
							credit: it.credit
					)
					result.add(list)
				}
			}
		}
		return result.sort{it.credit}
	}

	//new accounting integration save
	@Transactional(rollbackFor = QueryErrorException.class)
	ReceivingReport saveToJournalEntry(ReceivingReport receivingReport){
		def id = receivingReport.id
		def rec = receivingReport
		def recItems = receivingReportItemRepository.findItemsByReceivingReportId(id)
		def transType = transactionTypeService.transTypeById(rec.account)
		def yearFormat = DateTimeFormatter.ofPattern("yyyy")

		try{
			if(transType && !rec.consignment){
				Integration match = integrationServices.getIntegrationByDomainAndTagValue(rec.domain, transType.flagValue)

				def headerLedger =	integrationServices.generateAutoEntries(rec){
					it, mul ->
						it.flagValue = transType.flagValue

						Map<AccountingCategory, BigDecimal> accounts  = [:]

						//initialize
						Map<String, List<ReceivingReport>> finalAcc  = [:]
						match.integrationItems.findAll { BooleanUtils.isTrue(it.multiple) }.eachWithIndex { IntegrationItem entry, int i ->
							if(!finalAcc.containsKey(entry.sourceColumn)){
								finalAcc[entry.sourceColumn] = []
							}
						}
						//end init

						//loop items
						recItems.each { a ->
							if(!accounts.containsKey(a.item.accountingCategory))
								accounts[a.item.accountingCategory] = 0.0
							if(rec.vatInclusive){
								def totalAmount =  a.totalAmount ?: BigDecimal.ZERO
								def inputTax = a.inputTax ?: BigDecimal.ZERO
								def sum = totalAmount - inputTax
								accounts[a.item.accountingCategory] =  accounts[a.item.accountingCategory] + sum
							}else{
								def totalAmount =  a.totalAmount ?: BigDecimal.ZERO
								accounts[a.item.accountingCategory] =  accounts[a.item.accountingCategory] + totalAmount
							}
						}

						//loop accounts
						accounts.each {k, v ->
							if(v > 0){
								finalAcc[k.sourceColumn] << new ReceivingReport().tap {
									it.cost = BigDecimal.ZERO;
									it.supplies = BigDecimal.ZERO;
									it.medicine = BigDecimal.ZERO;
									it.clearingAmount = BigDecimal.ZERO;
									//init asset
									it.asset_a = BigDecimal.ZERO;it.asset_b = BigDecimal.ZERO;it.asset_c = BigDecimal.ZERO;
									it.asset_d = BigDecimal.ZERO;it.asset_e = BigDecimal.ZERO;it.asset_f = BigDecimal.ZERO;
									it.asset_g = BigDecimal.ZERO;it.asset_h = BigDecimal.ZERO;it.asset_i = BigDecimal.ZERO;
									it.asset_j = BigDecimal.ZERO;it.asset_k = BigDecimal.ZERO;it.asset_l = BigDecimal.ZERO;
									it.asset_m = BigDecimal.ZERO;it.asset_n = BigDecimal.ZERO;it.asset_o = BigDecimal.ZERO;
									it.asset_p = BigDecimal.ZERO;it.asset_q = BigDecimal.ZERO;it.asset_r = BigDecimal.ZERO;
									it.asset_s = BigDecimal.ZERO;it.asset_t = BigDecimal.ZERO;it.asset_u = BigDecimal.ZERO;
									it.asset_v = BigDecimal.ZERO;it.asset_w = BigDecimal.ZERO;it.asset_x = BigDecimal.ZERO;
									it.asset_y = BigDecimal.ZERO;it.asset_z = BigDecimal.ZERO;
									//init expense
									it.expense_a = BigDecimal.ZERO;it.expense_b = BigDecimal.ZERO;it.expense_c = BigDecimal.ZERO;
									it.expense_d = BigDecimal.ZERO;it.expense_e = BigDecimal.ZERO;it.expense_f = BigDecimal.ZERO;
									it.expense_g = BigDecimal.ZERO;it.expense_h = BigDecimal.ZERO;it.expense_i = BigDecimal.ZERO;
									it.expense_j = BigDecimal.ZERO;it.expense_k = BigDecimal.ZERO;it.expense_l = BigDecimal.ZERO;
									it.expense_m = BigDecimal.ZERO;it.expense_n = BigDecimal.ZERO;it.expense_o = BigDecimal.ZERO;
									it.expense_p = BigDecimal.ZERO;it.expense_q = BigDecimal.ZERO;it.expense_r = BigDecimal.ZERO;
									it.expense_s = BigDecimal.ZERO;it.expense_t = BigDecimal.ZERO;it.expense_u = BigDecimal.ZERO;
									it.expense_v = BigDecimal.ZERO;it.expense_w = BigDecimal.ZERO;it.expense_x = BigDecimal.ZERO;
									it.expense_y = BigDecimal.ZERO;it.expense_z = BigDecimal.ZERO;

									it.receiveDepartment = rec.receiveDepartment
									it.category = k
									it[k.sourceColumn] = v;
									//println("k.sourceColumn => " + k.sourceColumn + " - " + k.categoryDescription)
								}
							}
						}

						//loop multiples
						finalAcc.each { key, items ->
							mul << items
						}

						// Revenue for that department
						it.receiveDepartment = rec.receiveDepartment

						it.inputTax = rec.inputTax
						it.clearingAmount = rec.vatInclusive ? rec.grossAmount : (rec.amount + rec.totalDiscount)
				}
				//details to save
				Map<String,String> details = [:]

				rec.details.each { k,v ->
					details[k] = v
				}

				details["SRR_ID"] = rec.id.toString()
				details["SUPPLIER_ID"] = rec.supplier.id.toString()

				def pHeader =	ledgerServices.persistHeaderLedger(headerLedger,
						"${rec.receiveDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${rec.rrNo}",
						"${rec.rrNo}-${rec.supplier?.supplierFullname}",
						"${rec.rrNo}-${rec.supplier?.supplierFullname}",
						LedgerDocType.RR,
						JournalType.PURCHASES_PAYABLES,
						rec.createdDate,
						details)
				rec.postedLedger = pHeader.id

				receivingReportRepository.save(rec)
			}

		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}

		return rec
	}

	//update rec status undo and void
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "updateRECStatus")
	ReceivingReport updateRECStatus(
			@GraphQLArgument(name = "status") String status,
			@GraphQLArgument(name = "id") UUID id
	) {
		def upsert = receivingReportRepository.findById(id).get()

		//do some magic here ...
		//update ledger
		if(status.equalsIgnoreCase("void")){

			//reverse accounting entry
			if(upsert.postedLedger){
				def header = ledgerServices.findOne(upsert.postedLedger)
				ledgerServices.reverseEntriesCustom(header, upsert.receiveDate)
			}

			upsert.isPosted = false
			upsert.isVoid = true
			upsert.postedLedger = null //remove id for accounting if void
			inventoryLedgerService.voidInventoryGlobal(upsert.rrNo)
			//rec items
			def recItems = receivingReportItemRepository.findItemsByReceivingReportId(id)

			recItems.each {
				def recItem = it
				recItem.isPosted = false
				receivingReportItemRepository.save(recItem)

				//unlink po item
				if(it.refPoItem){
					PurchaseOrderItems itemPO = it.refPoItem
					def countPOItem = poDeliveryMonitoringService.getCountMonitoring(it.refPoItem.id)
					itemPO.receivingReport = null
					//-- 0: for delivery 1: partial delivery 2: completed
					itemPO.deliveryStatus = countPOItem > 1 ? 1 : 0
					purchaseOrderItemRepository.save(itemPO)
				}
			}
			//po monitoring delete
			def poMon = poDeliveryMonitoringService.getPOMonitoringByRec(id)
			poMon.each {
				poDeliveryMonitoringService.delPOMonitoring(it.id)
			}
			//update parent po
			if(upsert.purchaseOrder){
				PurchaseOrder parentPo = upsert.purchaseOrder
				parentPo.isCompleted = false
				parentPo.status = "APPROVED"
				purchaseOrderRepository.save(parentPo)
			}

		}else if(status.equalsIgnoreCase("redo")){
			upsert.isPosted = false
			upsert.isVoid = false
		}

		receivingReportRepository.save(upsert)
	}

}
