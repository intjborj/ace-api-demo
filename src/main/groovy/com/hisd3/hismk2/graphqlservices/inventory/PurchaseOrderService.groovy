package com.hisd3.hismk2.graphqlservices.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.inventory.*
import com.hisd3.hismk2.graphqlservices.inventoryv2.ServicePoItemMonitoring
import com.hisd3.hismk2.graphqlservices.types.GraphQLResVal
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.inventory.*
import com.hisd3.hismk2.rest.dto.PurchaseOrderItemsDto
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
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

import javax.transaction.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
@GraphQLApi
@TypeChecked
class PurchaseOrderService {
	
	@Autowired
	PurchaseOrderRepository purchaseOrderRepository
	
	@Autowired
	PurchaseOrderItemRepository purchaseOrderItemRepository
	
	@Autowired
	PurchaseRequestItemRepository purchaseRequestItemRepository
	
	@Autowired
	PurchaseRequestRepository purchaseRequestRepository
	
	@Autowired
	SupplierRepository supplierRepository
	
	@Autowired
	DepartmentRepository departmentRepository
	
	@Autowired
	GeneratorService generatorService
	
	@Autowired
	ItemRepository itemRepository
	
	@Autowired
	PaymentTermRepository paymentTermRepository
	
	@Autowired
	EmployeeRepository employeeRepository
	
	@Autowired
	UserRepository userRepository

	@Autowired
	ServicePoItemMonitoring servicePoItemMonitoring

	@Autowired
	private ObjectMapper objectMapper


	@GraphQLQuery(name = "poNotYetCompleted")
	List<PurchaseOrder> poNotYetCompleted() {
		return purchaseOrderRepository.poNotYetCompleted().sort{it.poNumber}.reverse(true)
	}

	@GraphQLQuery(name = "poNotYetCompletedCS")
	List<PurchaseOrder> poNotYetCompletedCS(
			@GraphQLArgument(name = "consignment") Boolean consignment,
			@GraphQLArgument(name = "asset") Boolean asset
	) {
		return purchaseOrderRepository.poNotYetCompleted(consignment, asset).sort{it.poNumber}.reverse(true)
	}

	@GraphQLQuery(name = "poListAll")
	List<PurchaseOrder> poListAll() {
		return purchaseOrderRepository.findAll().sort {it.poNumber}.reverse(true)
	}

	@GraphQLQuery(name = "poList", description = "list of all purchase order")
	Page<PurchaseOrder> poList(@GraphQLArgument(name = "filter") String filter, @GraphQLArgument(name = "page") Integer page, @GraphQLArgument(name = "pageSize") Integer pageSize) {
		return purchaseOrderRepository.findPurchaseOrderByPoNoOrPrNos(filter, new PageRequest(page, pageSize, Sort.Direction.DESC, "preparedDate"))
	}

	@GraphQLQuery(name = "getPoList", description = "list of all purchase order")
	Page<PurchaseOrder> getPoList(@GraphQLArgument(name = "filter") String filter, @GraphQLArgument(name = "page") Integer page, @GraphQLArgument(name = "pageSize") Integer pageSize) {
		return purchaseOrderRepository.getPoList(filter, new PageRequest(page, pageSize, Sort.Direction.DESC, "preparedDate"))
	}

	@GraphQLQuery(name = "poListRange", description = "list of all purchase order")
	Page<PurchaseOrder> poListRange(@GraphQLArgument(name = "id") UUID id,
									@GraphQLArgument(name = "filter") String filter,
									@GraphQLArgument(name = "startDate") String startDate,
									@GraphQLArgument(name = "endDate") String endDate,
									@GraphQLArgument(name = "page") Integer page,
									@GraphQLArgument(name = "pageSize") Integer pageSize,
									@GraphQLArgument(name = "consignment") Boolean consignment = false,
									@GraphQLArgument(name = "asset") Boolean asset = false) {
		return purchaseOrderRepository.findPurchaseOrderByPoNoOrPrNosRange(filter,
				id, startDate, endDate, consignment,asset, new PageRequest(page, pageSize, Sort.Direction.DESC, "preparedDate"))
	}

	@GraphQLQuery(name = "poItemPage", description = "list of all purchase order")
	Page<PurchaseOrderItems> poItemPage(@GraphQLArgument(name = "id") UUID id,
									@GraphQLArgument(name = "filter") String filter,
									@GraphQLArgument(name = "page") Integer page,
									@GraphQLArgument(name = "pageSize") Integer pageSize) {
		return purchaseOrderItemRepository.findByPurchaseOrderIdPageable(filter,
				id, new PageRequest(page, pageSize, Sort.Direction.DESC, "item.descLong"))
	}
	
	@GraphQLQuery(name = "poItemList", description = "list of all purchase order")
	List<PurchaseOrderItems> poItemList(@GraphQLArgument(name = "poId") UUID poId) {
		return purchaseOrderItemRepository.findByPurchaseOrderId(poId)
	}
	
	@GraphQLQuery(name = "get_po", description = "get po object")
	PurchaseOrder getPo(@GraphQLArgument(name = "id") UUID id) {
		if(id){
			return purchaseOrderRepository.findById(id).get()
		}else{
			return null;
		}
	}
	
	@GraphQLQuery(name = "getPoByStatus", description = "get po object")
	List<PurchaseOrder> findPurchaseOrderByStatus(@GraphQLArgument(name = "status") Boolean status) {
		return purchaseOrderRepository.findPurchaseOrderByStatus(status)
	}
	
	@GraphQLQuery(name = "get_poItems", description = "get po Item List Where reportReceiving is null")
	List<PurchaseOrderItems> findPoItem(@GraphQLArgument(name = "id") UUID id) {
		return purchaseOrderItemRepository.findPoItem(id).sort { it.item.descLong }
	}
	
	@GraphQLQuery(name = "getPoItemsById", description = "po item list")
	List<PurchaseOrderItems> getPoItemsById(@GraphQLArgument(name = "id") UUID id) {
		return purchaseOrderItemRepository.getPoItemsById(id).sort { it.item.descLong }
	}
	
	@GraphQLQuery(name = "getCountPoItem", description = "Count PO Items Where not Yet Delivered")
	Integer countPOItem(@GraphQLArgument(name = "id") UUID id) {
		return purchaseOrderItemRepository.countPOItem(id)
	}
	
	@Transactional(rollbackOn = Exception)
	@GraphQLMutation(name = "purchaseOrderMutation")
	PurchaseOrder purchaseOrderMutation(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "prItems") List<Map<String, Object>> prItems,
			@GraphQLArgument(name = "poItems") List<Map<String, Object>> poItems
	) {
		PurchaseOrder purchaseOrder = new PurchaseOrder()
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
		
		if (fields.get("id")) {
			purchaseOrder = purchaseOrderRepository.findById(UUID.fromString(fields.get('id').toString())).get()
			Employee empObject = employeeRepository.findById(UUID.fromString(fields.get('preparedBy').toString())).get()
			def prNos = fields.get("prNos") as ArrayList<String>
			purchaseOrder.prNos = prNos.join(",")
			purchaseOrder.supplier = supplierRepository.findById(UUID.fromString(fields.get('supplier').toString())).get()
			purchaseOrder.preparedDate = LocalDateTime.parse(fields.get('poDate').toString(), formatter)
			purchaseOrder.etaDate = LocalDateTime.parse(fields.get('etaDate').toString(), formatter)
			purchaseOrder.paymentTerms = paymentTermRepository.findById(UUID.fromString(fields.get('paymentTerms').toString())).get()
			purchaseOrder.deliveryTerms = fields.get('deliveryTerms').toString()
			purchaseOrder.preparedBy = empObject
			purchaseOrder.departmentFrom = empObject.department
			purchaseOrder.isCompleted = false
			purchaseOrder = purchaseOrderRepository.save(purchaseOrder)
			
			if (prItems) {
				prItems.eachWithIndex { Map<String, Object> entry, int i ->
					def dto = purchaseRequestItemRepository.findById(UUID.fromString(entry.get('id').toString())).get()
					if (dto.refPo == null) {
						dto.refPo = purchaseOrder
					}
					
					poItems.eachWithIndex { Map<String, Object> entry2, int i2 ->
						if (UUID.fromString(entry2.get('refItemId').toString()) == dto.item.id) {
							dto.lastUnitPrice = entry2.get('refUnitCost') as BigDecimal
						}
					}
					purchaseRequestItemRepository.save(dto)
				}
			}
			
			if (poItems) {
				poItems.eachWithIndex { Map<String, Object> entry, int i ->
					if (entry.get('id')) {
						def dto = purchaseOrderItemRepository.findById(UUID.fromString(entry.get('id').toString())).get()
						Item itemObject = itemRepository.findById(UUID.fromString(entry.get('refItemId').toString())).get()
						dto.purchaseOrder = purchaseOrder
						dto.item = itemObject
						dto.quantity = entry.get('refQty') as Integer
						dto.supplierLastPrice = entry.get('refUnitCost') as BigDecimal
						dto.qtyInSmall = (entry.get('refQty') as Integer) * itemObject.item_conversion
						dto.deliveryBalance = (entry.get('refQty') as Integer) * itemObject.item_conversion
						dto.deliveredQty = 0
						purchaseOrderItemRepository.save(dto)
						
					} else {
						def dto = new PurchaseOrderItems()
						Item itemObject = itemRepository.findById(UUID.fromString(entry.get('refItemId').toString())).get()
						dto.purchaseOrder = purchaseOrder
						dto.item = itemObject
						dto.quantity = entry.get('refQty') as Integer
						dto.supplierLastPrice = entry.get('refUnitCost') as BigDecimal
						dto.qtyInSmall = (entry.get('refQty') as Integer) * itemObject.item_conversion
						dto.deliveryBalance = (entry.get('refQty') as Integer) * itemObject.item_conversion
						dto.deliveredQty = 0
						purchaseOrderItemRepository.save(dto)
					}
					
				}
			}
			
			return purchaseOrder
		} else {
			def prNos = fields.get("prNos") as ArrayList<String>
			Employee empObject = employeeRepository.findById(UUID.fromString(fields.get('preparedBy').toString())).get()
			purchaseOrder.preparedDate = LocalDateTime.parse(fields.get('poDate').toString(), formatter)
			purchaseOrder.etaDate = LocalDateTime.parse(fields.get('etaDate').toString(), formatter)
			purchaseOrder.prNos = prNos.join(",")
			purchaseOrder.status = "NEW"
			purchaseOrder.poNumber = generatorService?.getNextValue(GeneratorType.PO_NO, { i ->
				StringUtils.leftPad(i.toString(), 6, "0")
			})
			purchaseOrder.preparedBy = employeeRepository.findById(UUID.fromString(fields.get('preparedBy').toString())).get()
			purchaseOrder.supplier = supplierRepository.findById(UUID.fromString(fields.get('supplier').toString())).get()
			purchaseOrder.paymentTerms = paymentTermRepository.findById(UUID.fromString(fields.get('paymentTerms').toString())).get()
			purchaseOrder.deliveryTerms = fields.get('deliveryTerms').toString()
			purchaseOrder.departmentFrom = empObject.department
			purchaseOrder.isCompleted = false
			purchaseOrder = purchaseOrderRepository.save(purchaseOrder)
			
			if (prItems) {
				prItems.eachWithIndex { Map<String, Object> entry, int i ->
					def dto = purchaseRequestItemRepository.findById(UUID.fromString(entry.get('id').toString())).get()
					dto.refPo = purchaseOrder
					poItems.eachWithIndex { Map<String, Object> entry2, int i2 ->
						if (UUID.fromString(entry2.get('refItemId').toString()) == dto.item.id) {
							dto.lastUnitPrice = entry2.get('refUnitCost') as BigDecimal
						}
					}
					purchaseRequestItemRepository.save(dto)
				}
			}
			
			if (poItems) {
				poItems.eachWithIndex { Map<String, Object> entry, int i ->
					def dto = new PurchaseOrderItems()
					Item itemObject = itemRepository.findById(UUID.fromString(entry.get('refItemId').toString())).get()
					dto.purchaseOrder = purchaseOrder
					dto.item = itemObject
					dto.quantity = entry.get('refQty') as Integer
					dto.supplierLastPrice = entry.get('refUnitCost') as BigDecimal
					dto.qtyInSmall = (entry.get('refQty') as Integer) * itemObject.item_conversion
					dto.deliveryBalance = (entry.get('refQty') as Integer) * itemObject.item_conversion
					dto.deliveredQty = 0
					purchaseOrderItemRepository.save(dto)
					
				}
			}
			
			return purchaseOrder
			
		}
	}
	
	@Transactional(rollbackOn = Exception)
	@GraphQLMutation(name = "updateStatus")
	PurchaseOrder updateStatus(@GraphQLArgument(name = "status") String status, @GraphQLArgument(name = "id") UUID id) {
		PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id).get()
		def poItems = purchaseOrderItemRepository.getPoItemsById(id).sort { it.item.descLong }
		purchaseOrder.status = status
		//link po to pr
		if(status.equalsIgnoreCase("APPROVED")){
			def pr = purchaseOrder.prNos.split(',') as List<String>
			def items = purchaseRequestItemRepository.getPrItemsByMultiplePrNos(pr)
			poItems.each {
				it ->
					items.each {
						tt ->
							if (it.item.id == tt.item.id) {
								def prItem = tt
								prItem.refPo = purchaseOrder
								purchaseRequestItemRepository.save(prItem)
							}
					}
			}
		}
		purchaseOrder = purchaseOrderRepository.save(purchaseOrder)
		return purchaseOrder
	}
	
	@Transactional(rollbackOn = Exception)
	@GraphQLMutation(name = "voidPurchaseOrder")
	PurchaseOrder voidPurchaseOrder(@GraphQLArgument(name = "id") UUID id) {
		def purchaseOrder = purchaseOrderRepository.findById(id).get()
		purchaseOrder.status = "VOIDED"
		List<PurchaseRequestItem> items = purchaseRequestItemRepository.getPrItemByPoId(purchaseOrder.id)
		items.eachWithIndex { PurchaseRequestItem entry, int i ->
			entry.refPo = null
			purchaseRequestItemRepository.save(entry)
		}
		purchaseOrder = purchaseOrderRepository.save(purchaseOrder)
		return purchaseOrder
	}
	
	@Transactional(rollbackOn = Exception)
	@GraphQLMutation(name = "upSertPurchaseOrder", description = "upsert po")
	PurchaseOrder upSertPurchaseaOrder(@GraphQLArgument(name = "fields") Map<String, Object> fields, @GraphQLArgument(name = "items") List<Map<String, Object>> items) {
		PurchaseOrder purchaseOrder = new PurchaseOrder()
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
		if (fields.get('id')) {
			purchaseOrder = purchaseOrderRepository.findById(UUID.fromString(fields.get('id').toString())).get()
			if (fields.containsKey('status')) {
				purchaseOrder.status = fields.get('status')
			}
			purchaseOrder.supplier = supplierRepository.findById(UUID.fromString(fields.get('supplier').toString())).get()
			purchaseOrder.departmentFrom = departmentRepository.findById(UUID.fromString(fields.get('departmentFrom') as String)).get()
			purchaseOrder.departmentTo = departmentRepository.findById(UUID.fromString(fields.get('departmentTo').toString())).get()
			purchaseOrder.paymentTerms = paymentTermRepository.findById(UUID.fromString(fields.get('paymentTerms').toString())).get()
			purchaseOrder.deliveryTerms = fields.get('deliveryTerms').toString()
			purchaseOrder.etaDate = LocalDateTime.parse(fields.get('etaDate').toString(), formatter)
			if (fields.get('prNo') as String) {
				purchaseOrder.purchaseRequest = purchaseRequestRepository.findById(UUID.fromString(fields.get('prNo').toString())).get()
			}
			
			if (items.size() != 0) {
				if (fields.get('noPr') as Boolean) {
					purchaseOrder.noPr = fields.get('noPr') as Boolean
					items.eachWithIndex { Map<String, Object> entry, int i ->
						def dto = new PurchaseOrderItems()
						
						if (entry.containsKey('id')) {
							if (entry.get('id', null)) {
								dto = purchaseOrderItemRepository.findById(UUID.fromString(entry.get('id').toString())).get()
							}
						}
						
						if (entry.containsKey('item')) {
							if (entry.get('item', null)) {
								def item = objectMapper.convertValue(entry.get('item'), Item)
								Item itemObject = itemRepository.findById(item.id).get()
								dto.item = itemObject
								dto.qtyInSmall = (entry.get('qty') as Integer) * itemObject.item_conversion
							}
							
						}
						
						dto.purchaseOrder = purchaseOrder
						dto.quantity = entry.get('qty') as Integer
						dto.supplierLastPrice = entry.get('cost') as BigDecimal
						
						purchaseOrderItemRepository.save(dto)
						
					}
				} else {
					purchaseOrder.noPr = fields.get('noPr') as Boolean
					items.eachWithIndex { Map<String, Object> entry, int i ->
						def dto = new PurchaseOrderItems()
						
						if (entry.containsKey('id')) {
							if (entry.get('id', null)) {
								dto = purchaseOrderItemRepository.findById(UUID.fromString(entry.get('id').toString())).get()
							}
						}
						
						if (entry.containsKey('item')) {
							if (entry.get('item', null)) {
								def item = objectMapper.convertValue(entry.get('item'), Item)
								Item itemObject = itemRepository.findById(item.id).get()
								dto.item = itemObject
								dto.qtyInSmall = (entry.get('qty') as Integer) * itemObject.item_conversion
							}
							
						}
						
						dto.purchaseOrder = purchaseOrder
						dto.quantity = entry.get('qty') as Integer
						dto.supplierLastPrice = entry.get('cost') as BigDecimal
						
						purchaseOrderItemRepository.save(dto)
						
					}
				}
			}
			
			purchaseOrderRepository.save(purchaseOrder)
		} else {
			purchaseOrder.supplier = supplierRepository.findById(UUID.fromString(fields.get('supplier').toString())).get()
			
			purchaseOrder.departmentFrom = departmentRepository.findById(UUID.fromString(fields.get('departmentFrom') as String)).get()
			
			purchaseOrder.departmentTo = departmentRepository.findById(UUID.fromString(fields.get('departmentTo').toString())).get()
			
			purchaseOrder.paymentTerms = paymentTermRepository.findById(UUID.fromString(fields.get('paymentTerms').toString())).get()
			
			purchaseOrder.deliveryTerms = fields.get('deliveryTerms').toString()
			purchaseOrder.status = "NEW"
			purchaseOrder.etaDate = LocalDateTime.parse(fields.get('etaDate').toString(), formatter)
			if (fields.get('prNo') as String) {
				purchaseOrder.purchaseRequest = purchaseRequestRepository.findById(UUID.fromString(fields.get('prNo').toString())).get()
			}
			purchaseOrder.poNumber = generatorService?.getNextValue(GeneratorType.PO_NO, { i ->
				StringUtils.leftPad(i.toString(), 6, "0")
			})
			purchaseOrder.isCompleted = false
			PurchaseOrder pOrder = purchaseOrderRepository.save(purchaseOrder)
			
			if (items.size() != 0) {
				if (fields.get('noPr') as Boolean) {
					def purchaseRequest = new PurchaseRequest()
					
					Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal()
					if (principal instanceof UserDetails) {
						String username = ((UserDetails) principal).getUsername()
						def user = userRepository.findOneByLogin(username)
						def employee = employeeRepository.findOneByUser(user)
						purchaseRequest.userId = employee.id
					}
					
					purchaseRequest.prNo = generatorService.getNextValue(GeneratorType.PR_NO) { Long no ->
						StringUtils.leftPad(no.toString(), 6, "0")
					}
					
					purchaseRequest.requestingDepartment = departmentRepository.findById(UUID.fromString(fields.get('departmentFrom') as String)).get()
					purchaseRequest.prDateRequested = pOrder.createdDate
					purchaseRequest.supplier = pOrder.supplier
					purchaseRequest.requestedDepartment = departmentRepository.findById(UUID.fromString(fields.get('departmentTo').toString())).get()
					def newPr = purchaseRequestRepository.save(purchaseRequest)
					purchaseOrder.noPr = fields.get('noPr') as Boolean
					items.eachWithIndex { Map<String, Object> entry, int i ->
						def dto = new PurchaseOrderItems()
						
						if (entry.containsKey('id')) {
							if (entry.get('id', null)) {
								dto = purchaseOrderItemRepository.findById(UUID.fromString(entry.get('id').toString())).get()
							}
						}
						
						if (entry.containsKey('item')) {
							if (entry.get('item', null)) {
								def item = objectMapper.convertValue(entry.get('item'), Item)
								Item itemObject = itemRepository.findById(item.id).get()
								dto.item = itemObject
								dto.qtyInSmall = (entry.get('qty') as Integer) * itemObject.item_conversion
							}
							
						}
						
						dto.purchaseOrder = purchaseOrder
						dto.quantity = entry.get('qty') as Integer
						dto.supplierLastPrice = entry.get('cost') as BigDecimal
						
						def poItem = purchaseOrderItemRepository.save(dto)
						
						if (poItem) {
							def purchaseRequestItem = new PurchaseRequestItem()
							
							purchaseRequestItem.purchaseRequest = newPr
							purchaseRequestItem.refPo = pOrder
							purchaseRequestItem.item = poItem.item
							purchaseRequestItem.refSupItemId = pOrder.supplier?.id
							purchaseRequestItem.lastUnitPrice = poItem.supplierLastPrice
							purchaseRequestItem.requestedQty = poItem.quantity
							
							purchaseRequestItemRepository.save(purchaseRequestItem)
						}
						
					}
					
				} else {
					purchaseOrder.noPr = fields.get('noPr') as Boolean
					items.eachWithIndex { Map<String, Object> entry, int i ->
						def dto = new PurchaseOrderItems()
						
						if (entry.containsKey('id')) {
							if (entry.get('id', null)) {
								dto = purchaseOrderItemRepository.findById(UUID.fromString(entry.get('id').toString())).get()
							}
						}
						
						if (entry.containsKey('item')) {
							if (entry.get('item', null)) {
								def item = objectMapper.convertValue(entry.get('item'), Item)
								Item itemObject = itemRepository.findById(item.id).get()
								dto.item = itemObject
								dto.qtyInSmall = (entry.get('qty') as Integer) * itemObject.item_conversion
							}
							
						}
						
						dto.purchaseOrder = pOrder
						dto.quantity = entry.get('qty') as Integer
						dto.supplierLastPrice = entry.get('cost') as BigDecimal

//						dto.prNos = entry.get('prNo') as String
//						def prNo = entry.get('prNo').toString().split(',')
//						prNo.eachWithIndex { String prNos, int idx ->
//							List<PurchaseRequestItem> prItems = purchaseRequestItemRepository.findByPrNo(prNos)
//
//							prItems.eachWithIndex { PurchaseRequestItem prItem, int prIdx ->
//								if (prItem.item.id == UUID.fromString(entry.get('refItemId').toString())) {
//									prItem.refPo = purchaseOrder
//									purchaseRequestItemRepository.save(prItem)
//								}
//							}
//						}
						
						if (fields.get('prNo') as String) {
							List<PurchaseRequestItem> prItems = purchaseRequestItemRepository.getByPrId(UUID.fromString(fields.get('prNo').toString()))
							
							prItems.eachWithIndex { PurchaseRequestItem prItem, int prIdx ->
								if (prItem) {
									if (entry.containsKey('refPrId')) {
										if (prItem.id == UUID.fromString(entry.get('refPrId').toString())) {
											prItem.refPo = pOrder
											purchaseRequestItemRepository.save(prItem)
										}
									}
									
								}
							}
						}
						purchaseOrderItemRepository.save(dto)
						
					}
				}
			}
		}
		
		return purchaseOrder
	}
	
	//update ni wilson
	@Transactional(rollbackOn = Exception)
	@GraphQLMutation(name = "mutatePurchaseOrder")
	PurchaseOrder mutatePurchaseOrder(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "poItems") ArrayList<Map<String, Object>> poItems
	) {
		PurchaseOrder upsert = new PurchaseOrder() //new for default
		def obj = objectMapper.convertValue(fields, PurchaseOrder)
		def items = poItems as ArrayList<PurchaseOrderItemsDto>
		if (id) {//update
			upsert = purchaseOrderRepository.findById(id).get()
			upsert.supplier = obj.supplier
			upsert.paymentTerms = obj.paymentTerms
			upsert.etaDate = obj.etaDate
			upsert.preparedDate = obj.preparedDate
			upsert.prNos = obj.prNos
			//insert items
			items.each {
				it ->
					def upsertItems = new PurchaseOrderItems()
					def item = objectMapper.convertValue(it.item, Item)
					if (it.isNew) {
						upsertItems.item = item
						upsertItems.quantity = it.quantity
						upsertItems.purchaseOrder = upsert
						upsertItems.supplierLastPrice = it.supplierLastPrice
						upsertItems.prNos = it.prNos
						upsertItems.qtyInSmall = it.quantity * item.item_conversion
						upsertItems.deliveryBalance = it.quantity * item.item_conversion
						upsertItems.deliveredQty = 0
						upsertItems.type = it.type
						upsertItems.type_text = it.type_text
						purchaseOrderItemRepository.save(upsertItems)
					} else {
						upsertItems = purchaseOrderItemRepository.findById(UUID.fromString(it.id)).get()
						upsertItems.quantity = it.quantity
						upsertItems.supplierLastPrice = it.supplierLastPrice
						upsertItems.prNos = it.prNos
						upsertItems.qtyInSmall = it.quantity * item.item_conversion
						upsertItems.deliveryBalance = it.quantity * item.item_conversion
						upsertItems.type = it.type
						upsertItems.type_text = it.type_text
						purchaseOrderItemRepository.save(upsertItems)

					}
			}
			purchaseOrderRepository.save(upsert)
		} else {//insert
			upsert.supplier = obj.supplier
			upsert.preparedBy = obj.preparedBy
			upsert.preparedDate = obj.preparedDate
			upsert.paymentTerms = obj.paymentTerms
			upsert.status = "NEW"
			upsert.poNumber = generatorService?.getNextValue(GeneratorType.PO_NO, { i ->
				StringUtils.leftPad(i.toString(), 6, "0")
			})
			upsert.etaDate = obj.etaDate
			upsert.noPr = obj.prNos ? false : true
			upsert.departmentFrom = obj.departmentFrom
			upsert.isCompleted = false
			upsert.prNos = obj.prNos
			upsert.consignment = false

			def afterSave = purchaseOrderRepository.save(upsert)
			
			//insert items
			items.each {
				it ->
					def upsertItems = new PurchaseOrderItems()
					def item = objectMapper.convertValue(it.item, Item)
					if (it.isNew) {
						upsertItems.item = item
						upsertItems.quantity = it.quantity
						upsertItems.purchaseOrder = afterSave
						upsertItems.supplierLastPrice = it.supplierLastPrice
						upsertItems.prNos = it.prNos
						upsertItems.qtyInSmall = it.quantity * item.item_conversion
						upsertItems.deliveryBalance = it.quantity * item.item_conversion
						upsertItems.deliveredQty = 0
						upsertItems.type = it.type
						upsertItems.type_text = it.type_text
						purchaseOrderItemRepository.save(upsertItems)
					}
			}
		}
		return upsert
	}
	//new update ni wilson
	@Transactional(rollbackOn = Exception)
	@GraphQLMutation(name = "upsertPO")
	PurchaseOrder upsertPO(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "poItems") ArrayList<Map<String, Object>> poItems,
			@GraphQLArgument(name = "forRemove") ArrayList<Map<String, Object>> forRemove
	) {
		PurchaseOrder upsert = new PurchaseOrder() //new for default
		def obj = objectMapper.convertValue(fields, PurchaseOrder)
		def items = poItems as ArrayList<PurchaseOrderItemsDto>
		//remove items if there is
		def poItemsRemove = forRemove as ArrayList<PurchaseOrderItemsDto>
		if (id) {//update
			upsert = purchaseOrderRepository.findById(id).get()
			upsert.supplier = obj.supplier
			upsert.paymentTerms = obj.paymentTerms
			upsert.etaDate = obj.etaDate
			upsert.preparedDate = obj.preparedDate
			upsert.prNos = obj.prNos
			upsert.consignment = obj.consignment
			upsert.isFixedAsset = obj.isFixedAsset
			//insert items
			items.each {
				it ->
					def upsertItems = new PurchaseOrderItems()
					def item = objectMapper.convertValue(it.item, Item)
					if (it.isNew) {
						upsertItems.item = item
						upsertItems.quantity = it.quantity
						upsertItems.purchaseOrder = upsert
						upsertItems.supplierLastPrice = it.supplierLastPrice
						upsertItems.prNos = it.prNos
						upsertItems.qtyInSmall = it.quantity * item.item_conversion
						upsertItems.deliveryBalance = it.quantity * item.item_conversion
						upsertItems.deliveredQty = 0
						upsertItems.deliveryStatus = 0
						upsertItems.type = it.type
						upsertItems.type_text = it.type_text
						purchaseOrderItemRepository.save(upsertItems)
					} else {
						upsertItems = purchaseOrderItemRepository.findById(UUID.fromString(it.id)).get()
						upsertItems.quantity = it.quantity
						upsertItems.supplierLastPrice = it.supplierLastPrice
						upsertItems.prNos = it.prNos
						upsertItems.qtyInSmall = it.quantity * item.item_conversion
						upsertItems.deliveryBalance = it.quantity * item.item_conversion
						upsertItems.deliveryStatus = 0
						upsertItems.type = it.type
						upsertItems.type_text = it.type_text
						purchaseOrderItemRepository.save(upsertItems)

					}
			}
			purchaseOrderRepository.save(upsert)
		} else {//insert
			upsert.supplier = obj.supplier
			upsert.preparedBy = obj.preparedBy
			upsert.preparedDate = obj.preparedDate
			upsert.paymentTerms = obj.paymentTerms
			upsert.status = "NEW"
			if(obj.consignment){
				upsert.poNumber = generatorService?.getNextValue(GeneratorType.PO_NO_CS, { i ->
					"CS-PO" + '-' + StringUtils.leftPad(i.toString(), 6, "0")
				})
			}else if(obj.isFixedAsset){
				upsert.poNumber = generatorService?.getNextValue(GeneratorType.PO_NO_FA, { i ->
					"FA-PO" + '-' + StringUtils.leftPad(i.toString(), 6, "0")
				})
			}else{
				upsert.poNumber = generatorService?.getNextValue(GeneratorType.PO_NO, { i ->
					StringUtils.leftPad(i.toString(), 6, "0")
				})
			}

			upsert.etaDate = obj.etaDate
			upsert.noPr = obj.prNos ? false : true
			upsert.departmentFrom = obj.departmentFrom
			upsert.isCompleted = false
			upsert.prNos = obj.prNos
			upsert.consignment = obj.consignment
			upsert.isFixedAsset = obj.isFixedAsset

			def afterSave = purchaseOrderRepository.save(upsert)

			//insert items
			items.each {
				it ->
					def upsertItems = new PurchaseOrderItems()
					def item = objectMapper.convertValue(it.item, Item)
					if (it.isNew) {
						upsertItems.item = item
						upsertItems.quantity = it.quantity
						upsertItems.purchaseOrder = afterSave
						upsertItems.supplierLastPrice = it.supplierLastPrice
						upsertItems.prNos = it.prNos
						upsertItems.qtyInSmall = it.quantity * item.item_conversion
						upsertItems.deliveryBalance = it.quantity * item.item_conversion
						upsertItems.deliveredQty = 0
						upsertItems.deliveryStatus = 0
						upsertItems.type = it.type
						upsertItems.type_text = it.type_text
						purchaseOrderItemRepository.save(upsertItems)
					}
			}
		}

		//execute remove
		if(poItemsRemove){
			poItemsRemove.each {
				if(!it.isNew){
					def remove = purchaseOrderItemRepository.findById(UUID.fromString(it.id)).get()
					purchaseOrderItemRepository.delete(remove)
				}
			}
		}
		return upsert
	}

    String getNextPOCodeByType(
            LocalDateTime poDate,
            String type
    ){
        String year = (poDate).format(DateTimeFormatter.ofPattern("yyyy"))
        String poCode = generatorService.getNextGeneratorFeatPrefix("manual_e_${type.toLowerCase()}_${year}"){
            it ->
                if(type.equalsIgnoreCase('FIXED_ASSETS'))
                    return "PO-FA-${year}${StringUtils.leftPad(it.toString() ,5,"0")}"
                if(type.equalsIgnoreCase('INVENTORY'))
                    return "PO-INV-${year}${StringUtils.leftPad(it.toString() ,5,"0")}"
        }
        return poCode
    }

//	@Transactional(rollbackOn = Exception)
	@GraphQLMutation(name = "mutatePurchaseOrderFixedAsset")
	GraphQLResVal<PurchaseOrder> mutatePurchaseOrderFixedAsset(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "poItems") ArrayList<Map<String, Object>> poItems
	) {
		try{
			PurchaseOrder purchaseOrder = new PurchaseOrder()
			if(id){
				purchaseOrder = purchaseOrderRepository.findById(id).get()
				objectMapper.updateValue(purchaseOrder,fields)
				purchaseOrder = purchaseOrderRepository.save(purchaseOrder)
			}
			else {
				objectMapper.updateValue(purchaseOrder,fields)
                purchaseOrder.poNumber = getNextPOCodeByType(purchaseOrder.preparedDate,'FIXED_ASSETS')
                purchaseOrder.status = "NEW"
                purchaseOrder.noPr = purchaseOrder.prNos ? false : true
				purchaseOrder.consignment = false
                purchaseOrder.isCompleted = false
				purchaseOrder = purchaseOrderRepository.save(purchaseOrder)
			}

			if(poItems.size() > 0)
				(poItems as ArrayList<PurchaseOrderItemsDto>).each {
					PurchaseOrderItems purchaseOrderItems = new PurchaseOrderItems()
					if(it['id']){
						purchaseOrderItems = purchaseOrderItemRepository.findById(UUID.fromString(it.id)).get()
						objectMapper.updateValue(purchaseOrderItems,it)
						purchaseOrderItems.qtyInSmall = it.quantity * purchaseOrderItems?.item?.item_conversion ?: 1
						purchaseOrderItems.deliveryBalance = it.quantity * purchaseOrderItems?.item?.item_conversion ?: 1
						purchaseOrderItemRepository.save(purchaseOrderItems)
					}
					else {
						objectMapper.updateValue(purchaseOrderItems,it)
						purchaseOrderItems.deliveredQty = 0
                        purchaseOrderItems.purchaseOrder = purchaseOrder
						Integer conversion = purchaseOrderItems?.item?.item_conversion ?: 1
						purchaseOrderItems.qtyInSmall = purchaseOrderItems.quantity * conversion
						purchaseOrderItems.deliveryBalance = purchaseOrderItems.quantity * conversion
						purchaseOrderItemRepository.save(purchaseOrderItems)
					}
				}

			return new GraphQLResVal<PurchaseOrder>(purchaseOrder,true,"Successfully saved ${purchaseOrder.poNumber}")
		}catch(e){
			return new GraphQLResVal<PurchaseOrder>(new PurchaseOrder(),true,e.message)
		}
	}


	//puchase order item remove
	@Transactional(rollbackOn = Exception)
	@GraphQLMutation(name = "removeUpdatePOItems")
    GraphQLRetVal<Boolean> removeUpdatePOItems(
			@GraphQLArgument(name = "items") ArrayList<Map<String, Object>> fields
	) {
		//remove Items
		def items = fields as ArrayList<PurchaseOrderItems>
		if(items){
            items.each {
                def deleted = objectMapper.convertValue(it, PurchaseOrderItems.class)
                purchaseOrderItemRepository.delete(deleted)
            }
		}
        return new GraphQLRetVal<Boolean>(true,true,"Purchase Order Items Removed")
	}

	@Transactional(rollbackOn = Exception)
	@GraphQLMutation(name = "removePOItem")
	PurchaseOrderItems removePOItem(
			@GraphQLArgument(name = "id") UUID id
	) {
		//remove Items
		def del = purchaseOrderItemRepository.findById(id).get()
		purchaseOrderItemRepository.delete(del)
	}

	@Transactional
	@GraphQLMutation(name = "setToCompleted")
	PurchaseOrder setToCompleted(
			@GraphQLArgument(name = "id") UUID id
	) {
		if(id){
			def upsert = purchaseOrderRepository.findById(id).get()
			Boolean checkpoint = false
			def check = servicePoItemMonitoring.checkBalancesByPO(id)
			//loop checking
			check.each {
				checkpoint = it.delBalance <= 0
			}

			if(checkpoint){
				upsert.isCompleted = true
				upsert.status = "DELIVERED"
				purchaseOrderRepository.save(upsert)
			}
			return upsert
		}else{
			return null
		}
	}





	@GraphQLQuery(name = "getFixedAssetPurchaseOrder", description = "list of all fixed asset purchase order")
	Page<PurchaseOrder> fixedAssetPOList(
									@GraphQLArgument(name = "filter") String filter,
									@GraphQLArgument(name = "startDate") String startDate,
									@GraphQLArgument(name = "endDate") String endDate,
									@GraphQLArgument(name = "page") Integer page,
									@GraphQLArgument(name = "pageSize") Integer pageSize) {
		return purchaseOrderRepository.getFixedAssetPurchaseOrder(filter,
				startDate, endDate, new PageRequest(page, pageSize))
	}

}
