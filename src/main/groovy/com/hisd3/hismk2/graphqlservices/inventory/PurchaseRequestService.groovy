package com.hisd3.hismk2.graphqlservices.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.dao.inventory.PurchaseRequestDao
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.inventory.PurchaseRequest
import com.hisd3.hismk2.domain.inventory.PurchaseRequestItem
import com.hisd3.hismk2.domain.inventory.SupplierItem
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.inventory.*
import com.hisd3.hismk2.rest.InventoryResource
import com.hisd3.hismk2.rest.dto.PrItems
import com.hisd3.hismk2.rest.dto.PurchaseOrderDto
import com.hisd3.hismk2.rest.dto.PurchaseRequestDto
import com.hisd3.hismk2.rest.dto.QueryErrorException
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.data.domain.Page

import java.time.Instant

@TypeChecked
@Component
@GraphQLApi
class PurchaseRequestService {
	
	@Autowired
	PurchaseRequestRepository purchaseRequestRepository
	
	@Autowired
	PurchaseRequestItemRepository purchaseRequestItemRepository
	
	@Autowired
	ItemRepository itemRepository
	
	@Autowired
	PurchaseRequestDao purchaseRequestDao
	
	@Autowired
	EmployeeRepository employeeRepository
	
	@Autowired
	SupplierItemRepository supplierItemRepository
	
	@Autowired
	SupplierRepository supplierRepository
	
	@Autowired
	DepartmentRepository departmentRepository

	@Autowired
	InventoryResource inventoryResource
	
	@Autowired
	GeneratorService generatorService
	
	@Autowired
	ObjectMapper objectMapper
	
	@GraphQLQuery(name = "get_pr_list", description = "get all purchase request")
	List<PurchaseRequest> getAllPurchaseRequests(@GraphQLArgument(name = "supplierId") UUID supplierId) {
		
		List<PurchaseRequest> purchaseRequests
		
		purchaseRequests = purchaseRequestRepository.findAll()
		
		List<PurchaseRequest> purchaseRequests2 = new ArrayList<PurchaseRequest>()
		
		purchaseRequests.each {
			it ->
				def prItems = purchaseRequestItemRepository.countGetByPrIdAndPoNull(it.id)
				if (prItems != 0) {
					purchaseRequests2.add(it)
				}
			
		}
		
		return purchaseRequests2
	}
	
	@GraphQLQuery(name = "purchaseRequestList", description = "List of Purchase Request")
	List<PurchaseRequest> getAllPurchaseRequest() {
		return purchaseRequestRepository.findAll().sort { it.createdDate }.reverse(true)
	}

	@GraphQLQuery(name = "prById", description = "Purchase Request By ID")
	PurchaseRequest prById(@GraphQLArgument(name = "id") UUID id) {
		return purchaseRequestRepository.getPrById(id)
	}
	
	@GraphQLQuery
	List<PurchaseRequestItem> getAllPurchaseRequestItems(@GraphQLArgument(name = "prId") UUID prId) {
		purchaseRequestDao.getpRItems(prId)
	}
	
	@GraphQLQuery(name = "purchaseRequestListByDep", description = "List of Purchase Request by Dep")
	List<PurchaseRequest> findPrByDepartment(@GraphQLArgument(name = "dep") UUID dep, @GraphQLArgument(name = "filter") String filter) {
		purchaseRequestRepository.findPrByDepartment(dep, filter).sort { it.createdDate }.reverse(true)
	}

	@GraphQLQuery(name = "purchaseRequestListByDepRange", description = "List of Purchase Request by Dep")
	List<PurchaseRequest> findPrByDepartmentRange(@GraphQLArgument(name = "dep") UUID dep, @GraphQLArgument(name = "filter") String filter,
												  @GraphQLArgument(name = "startDate") String startDate,
												  @GraphQLArgument(name = "endDate") String endDate,
												  @GraphQLArgument(name = "consignment") Boolean consignment = false,
												  @GraphQLArgument(name = "asset") Boolean asset = false) {
		purchaseRequestRepository.findPrByDepartmentRange(dep, filter, startDate, endDate, consignment, asset).sort { it.createdDate }.reverse(true)
	}

	@GraphQLQuery(name = "purchaseRequestListByDepRangePage", description = "List of Purchase Request by Dep")
	Page<PurchaseRequest> findPrByDepartmentRangePage(@GraphQLArgument(name = "dep") UUID dep, @GraphQLArgument(name = "filter") String filter,
													  @GraphQLArgument(name = "startDate") String startDate,
													  @GraphQLArgument(name = "endDate") String endDate,
													  @GraphQLArgument(name = "page") Integer page, // zero based
													  @GraphQLArgument(name = "size") Integer pageSize) {
		purchaseRequestRepository.findPrByDepartmentRangePage(dep, filter, startDate, endDate,new PageRequest(page, pageSize, Sort.Direction.DESC, "createdDate"))
	}


	@GraphQLQuery(name = 'get_pr_items_by_id', description = "get pr items")
	List<PurchaseRequestItem> getPrItemsById(@GraphQLArgument(name = "id") UUID id) {
		if (id) {
			return purchaseRequestItemRepository.getByPrIdAndPoNull(id)
		}
	}
	
	@GraphQLQuery(name = 'get_pr_items_by_multiple_prNos', description = "get pr items")
	List<PurchaseRequestItem> getPrItemsByMultiplePrNos(@GraphQLArgument(name = "prNos") List<String> prNos,
														@GraphQLArgument(name = "status") String status,
														@GraphQLArgument(name = "id") UUID id) {
		if (prNos) {
			if (status.equalsIgnoreCase('APPROVED')) {
				return purchaseRequestItemRepository.getPrItemsByMultiplePrNosAll(prNos, id)
			} else {
				return purchaseRequestItemRepository.getPrItemsByMultiplePrNos(prNos)
			}
			
		}
	}
	
	@GraphQLQuery(name = "pritem_by_supplier", description = "List of pr items by supplier")
	List<PurchaseOrderDto> prItemsBySupplier(@GraphQLArgument(name = "supplier") UUID supplier) {
		List<SupplierItem> supplierItems = supplierItemRepository.findBySupplierId(supplier)
		List<PurchaseRequestItem> purchaseRequestItems = purchaseRequestItemRepository.getPrItemByWherePoIsNotNullandStatusIsApproved()
		List<PurchaseOrderDto> poItemList = new ArrayList<>()
		List<PurchaseOrderDto> poItemList2 = new ArrayList<>()
		supplierItems.each {
			it ->
				purchaseRequestItems.each {
					it3 ->
						if (it3.item.id == it.item.id) {
							PurchaseOrderDto dto = new PurchaseOrderDto()
							dto.refItemId = it3.item.id
							dto.prNo = it3.purchaseRequest.prNo
							dto.description = it3.item.descLong
							dto.itemCode = it3.item.stockCode
							dto.qty = it3.requestedQty
							//dto.pkg = it3.refItem.packageName
							dto.pkg_price = it.cost
							poItemList.add(dto)
						}
				}
			
		}
		
		poItemList.eachWithIndex { PurchaseOrderDto entry, int i ->
			if (poItemList2.isEmpty()) {
				poItemList2.add(entry)
			} else {
				def dto = poItemList2.find {
					PurchaseOrderDto dto ->
						(dto.refItemId == entry.refItemId)
				}
				
				if (dto) {
					dto.prNo = dto.prNo.concat("," + entry.prNo)
					
					dto.qty += entry.qty
				} else {
					poItemList2.add(entry)
				}
			}
		}
		return poItemList2.sort {
			sort -> sort.prNo
		}
	}
	
	@GraphQLMutation(name = "savePurchaseRequestItems")
	List<PurchaseRequestItem> savePurchaseRequestItems(@GraphQLArgument(name = "prItems") List<PurchaseRequestItem> prItems) {
		def prItemses = new ArrayList<PurchaseRequestItem>()
		prItems.eachWithIndex { PurchaseRequestItem entry, int i ->
			def prItem = purchaseRequestItemRepository.findById(entry.id).get()
			prItem.unitCost = entry.unitCost
			prItem.requestedQty = entry.requestedQty
			prItemses.add(prItem)
		}
		return purchaseRequestItemRepository.saveAll(prItemses)
	}
	
	// mutation //
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "upsertPr")
	PurchaseRequest upsertPr(
			@GraphQLArgument(name = "pr") Map<String, Object> pr,
			@GraphQLArgument(name = "prItems") ArrayList<Map<String, Object>> prItems,
			@GraphQLArgument(name = "prId") String prId
	) {
		def upsert = new PurchaseRequest()
		def prDto = objectMapper.convertValue(pr, PurchaseRequestDto)
		def request_items = prItems as ArrayList<PrItems>
		//def user = employeeRepository.findById(UUID.fromString(prDto.user_id)).get()
		try {
			if (prId) { //update
				//update
				upsert = purchaseRequestRepository.findById(UUID.fromString(prId)).get()
				upsert.prDateNeeded = Instant.parse(prDto.date_needed)
				if (prDto.supplier) {
					upsert.supplier = supplierRepository.findById(UUID.fromString(prDto.supplier)).get()
				}
				upsert.requestedDepartment = departmentRepository.findById(UUID.fromString(prDto.request_to)).get()
				upsert.prType = prDto.request_type
				upsert.consignment = prDto.consignment
				upsert.asset = prDto.asset
				//loop items
				request_items.each {
					it ->
						if (!it.prItemId) {

							def pr_items = new PurchaseRequestItem()
							pr_items.item = itemRepository.findById(UUID.fromString(it.itemId)).get()
							pr_items.purchaseRequest = purchaseRequestRepository.findById(UUID.fromString(prId)).get()
							pr_items.onHandQty = it.onHand as Integer
							if (it.supplierItemId) {
								pr_items.refSupItemId = UUID.fromString(it.supplierItemId)
								//pr_items.onHandQty = inventoryResource.getOnhandByDepartment(user.departmentOfDuty.id, UUID.fromString(it.itemId))
							}
							pr_items.requestedQty = it.qty as Integer
							pr_items.unitCost = it.cost as BigDecimal
							pr_items.total = it.total as BigDecimal
							purchaseRequestItemRepository.save(pr_items)
							//
							
						} else {
							//update
							def pr_item = purchaseRequestItemRepository.findById(UUID.fromString(it.prItemId)).get()
							pr_item.requestedQty = it.qty as Integer
							pr_item.unitCost = it.cost as BigDecimal
							pr_item.total = it.total as BigDecimal
							pr_item.onHandQty = it.onHand as Integer
							pr_item.lastUnitPrice = it.cost as BigDecimal
							purchaseRequestItemRepository.save(pr_item)
							//
						}
				}
				purchaseRequestRepository.save(upsert)
			} else {
				//insert
				if(prDto.consignment){
					upsert.prNo = generatorService.getNextValue(GeneratorType.PR_NO_CS) { Long no ->
						"CS-PR" + '-' + StringUtils.leftPad(no.toString(), 6, "0")
					}
				}else if(prDto.asset){
					upsert.prNo = generatorService.getNextValue(GeneratorType.PR_NO_FA) { Long no ->
						"FA-PR" + '-' + StringUtils.leftPad(no.toString(), 6, "0")
					}
				}else{
					upsert.prNo = generatorService.getNextValue(GeneratorType.PR_NO) { Long no ->
						StringUtils.leftPad(no.toString(), 6, "0")
					}
				}

				upsert.prDateRequested = Instant.now()
				upsert.prDateNeeded = Instant.parse(prDto.date_needed)
				if (prDto.supplier) {
					upsert.supplier = supplierRepository.findById(UUID.fromString(prDto.supplier)).get()
				}
				upsert.userId = UUID.fromString(prDto.user_id)
				upsert.userFullname = prDto.requested_by
				upsert.requestedDepartment = departmentRepository.findById(UUID.fromString(prDto.request_to)).get()
				upsert.requestingDepartment = departmentRepository.findById(UUID.fromString(prDto.requestingDep)).get()
				upsert.prType = prDto.request_type
				upsert.isApprove = false
				upsert.isPoCreated = false
				upsert.consignment = prDto.consignment
				upsert.asset = prDto.asset
				upsert.status = "For Approval"
				def afterSave = purchaseRequestRepository.save(upsert)
				
				//loop items
				request_items.each {
					it ->
						if (!it.prItemId) {
							def pr_items = new PurchaseRequestItem()
							pr_items.item = itemRepository.findById(UUID.fromString(it.itemId)).get()
							pr_items.purchaseRequest = afterSave
							pr_items.onHandQty = it.onHand as Integer
							if (it.supplierItemId) {
								pr_items.refSupItemId = UUID.fromString(it.supplierItemId)
								//pr_items.onHandQty = inventoryResource.getOnhandByDepartment(user.departmentOfDuty.id, UUID.fromString(it.itemId))
							}
							pr_items.requestedQty = it.qty as Integer
							pr_items.unitCost = it.cost as BigDecimal
							pr_items.total = it.total as BigDecimal
							purchaseRequestItemRepository.save(pr_items)
							//
						}
				}
			}
		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		return upsert
	}

	//
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "updatePRStatus")
	PurchaseRequest updatePRStatus(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "status") Boolean status,
			@GraphQLArgument(name = "type") String type
	) {
		PurchaseRequest pr = purchaseRequestRepository.findById(id).get()
		Employee e = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()

		if(status){
			pr.isApprove = status
			pr.approver = e.id
			pr.approverFullname = e.fullName
			pr.status = 'Approve'
			purchaseRequestRepository.save(pr)
		}else{
			pr.isApprove = status
			pr.approver = null
			pr.approverFullname = null
			pr.status = type.equalsIgnoreCase("void") ? 'Voided' : 'For Approval'
			purchaseRequestRepository.save(pr)
		}
		return pr
	}
	
}
