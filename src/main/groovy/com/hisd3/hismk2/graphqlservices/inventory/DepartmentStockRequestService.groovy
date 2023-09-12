package com.hisd3.hismk2.graphqlservices.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.inventory.DepartmentStockRequest
import com.hisd3.hismk2.domain.inventory.DepartmentStockRequestItem
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.inventory.ReturnSupplier
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.inventory.DepartmentStockRequestRepository
import com.hisd3.hismk2.repository.inventory.DepartmentStockRequestItemRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.rest.dto.DepartmentStockRequestItemsDto
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
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import java.time.Duration
import java.time.Instant

@Component
@GraphQLApi
@TypeChecked
class DepartmentStockRequestService {
	
	@Autowired
	DepartmentStockRequestRepository departmentStockRequestRepository

	@Autowired
	DepartmentStockRequestItemRepository departmentStockRequestItemRepository

	@Autowired
	EmployeeRepository employeeRepository

	@Autowired
	DepartmentRepository departmentRepository


	@Autowired
	GeneratorService generatorService

	@Autowired
	private ObjectMapper objectMapper
	//
	@GraphQLQuery(name = "departmentStockRequest", description = "List of Departmental Request")
	List<DepartmentStockRequest> getDepartmentStockRequest() {
		return departmentStockRequestRepository.findAll().sort { it.createdDate }.reverse(true)
	}

	@GraphQLQuery(name = "depRequestById", description = "Departmental Request")
	DepartmentStockRequest depRequestById(@GraphQLArgument(name = "id") UUID id) {
		if(id){
			return departmentStockRequestRepository.findById(id).get()
		}else{
			return null
		}
	}
	
	@GraphQLQuery(name = "RequestIncoming", description = "List of Departmental Request Incoming")
	List<DepartmentStockRequest> getRequestIncoming(@GraphQLArgument(name = "dep") UUID dep, @GraphQLArgument(name = "status") Integer status, @GraphQLArgument(name = "filter") String filter) {
		return departmentStockRequestRepository.findIncomingRequest(dep, status, filter)
	}
	
	@GraphQLQuery(name = "RequestOutgoing", description = "List of Departmental Request Outgoing")
	List<DepartmentStockRequest> getRequestOutgoing(@GraphQLArgument(name = "dep") UUID dep, @GraphQLArgument(name = "status") Integer status, @GraphQLArgument(name = "filter") String filter) {
		return departmentStockRequestRepository.findOutgoingRequest(dep, status, filter)
	}

	//	code ni Dons

	@GraphQLQuery(name = "getDepartmentStockRequestByReqDept", description = "List of Departmental Request")
	List<DepartmentStockRequest> getDepartmentStockRequestByReqDept(@GraphQLArgument(name = "dep") UUID dep, @GraphQLArgument(name = "filter") String filter) {
		return departmentStockRequestRepository.findDepartmentStockRequestByReqDept(dep, filter)
	}

	// code ni dons
	@GraphQLQuery(name = "getDepartmentStockRequestByIssueDept", description = "List of Departmental Request by issue dept")
	List<DepartmentStockRequest> getDepartmentStockRequestByIssueDept(@GraphQLArgument(name = "dep") UUID dep, @GraphQLArgument(name = "filter") String filter) {
		return departmentStockRequestRepository.findDepartmentStockRequestByIssueDept(dep, filter)
	}

	@GraphQLQuery(name = "getIncomingRequest", description = "get incoming request list by dep")
	Page<DepartmentStockRequest> getIncomingRequest(@GraphQLArgument(name = "id") UUID id,
												 @GraphQLArgument(name = "filter") String filter,
												 @GraphQLArgument(name = "startDate") String startDate,
												 @GraphQLArgument(name = "endDate") String endDate,
												 @GraphQLArgument(name = "page") Integer page,
												 @GraphQLArgument(name = "pageSize") Integer pageSize) {
		return departmentStockRequestRepository.getIncomingRequest(filter,
				id, startDate, endDate, new PageRequest(page, pageSize, Sort.Direction.DESC, "requestDate"))
	}

	@GraphQLQuery(name = "getIncomingRequestList", description = "get incoming request list by dep")
	Page<DepartmentStockRequest> getIncomingRequestList(@GraphQLArgument(name = "filter") String filter,
													@GraphQLArgument(name = "page") Integer page,
													@GraphQLArgument(name = "pageSize") Integer pageSize) {
		Employee emp = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()
		return departmentStockRequestRepository.getIncomingRequestList(filter,emp.department.id,  new PageRequest(page, pageSize, Sort.Direction.DESC, "requestDate"))
	}

	@GraphQLQuery(name = "getOutgoingRequest", description = "get outgoing request list by dep")
	Page<DepartmentStockRequest> getOutgoingRequest(@GraphQLArgument(name = "id") UUID id,
													@GraphQLArgument(name = "filter") String filter,
													@GraphQLArgument(name = "startDate") String startDate,
													@GraphQLArgument(name = "endDate") String endDate,
													@GraphQLArgument(name = "page") Integer page,
													@GraphQLArgument(name = "pageSize") Integer pageSize) {
		return departmentStockRequestRepository.getOutgoingRequest(filter,
				id, startDate, endDate, new PageRequest(page, pageSize, Sort.Direction.DESC, "requestDate"))
	}

    // code ni dons
    @GraphQLQuery(name = "getDepartmentStockRequestReqNoByReqDept", description = "List of Departmental Request by request no from requested dept")
	List<DepartmentStockRequest> getDepartmentStockRequestReqNoByReqDept(@GraphQLArgument(name = "toDept") UUID toDept, @GraphQLArgument(name = "fromDept") UUID fromDept, @GraphQLArgument(name = "type") String type, @GraphQLArgument(name = "status") Integer status) {
		return departmentStockRequestRepository.getDepartmentStockRequestReqNo(fromDept,toDept, type, status)
	}

	@GraphQLQuery(name = "getIncomingRequestCount")
	Long getIncomingRequestCount(@GraphQLArgument(name = "id") UUID id) {
		return departmentStockRequestRepository.getIncomingRequestCount(id)
	}

	// code ni dons
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "upsertDepartmentStock", description = "insert BEG")
	DepartmentStockRequest upsertDepartmentStock(
			@GraphQLArgument(name = "parentId") UUID parentId,
			@GraphQLArgument(name = "otherValue") Map<String, Object> otherValue,
			@GraphQLArgument(name = "itemsReq") ArrayList<Map<String, Object>> itemsReq
	) {
		DepartmentStockRequest upsert = new DepartmentStockRequest()
		def obj = objectMapper.convertValue(otherValue, DepartmentStockRequest)
		def itemList = itemsReq as ArrayList<DepartmentStockRequestItemsDto>
		Employee emp = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()
		try { //try clause
			if (parentId) { //update
				upsert = departmentStockRequestRepository.findById(parentId).get()
				upsert.purpose = obj.purpose
				upsert.issuingDepartment = obj.issuingDepartment
				upsert.requestedBy = emp
				upsert.requestType = obj.requestType
				def afterSave = departmentStockRequestRepository.save(upsert)
				itemList.each {
					it ->
					def item = objectMapper.convertValue(it.item, Item)
					def upsertItems = new DepartmentStockRequestItem()
					if (it.isNew) {
						upsertItems.departmentStockRequest = afterSave
						upsertItems.item = item
						upsertItems.quantity_requested = it.quantity_requested
						upsertItems.unit_cost = it.unit_cost
						departmentStockRequestItemRepository.save(upsertItems)
					} else {
						upsertItems = departmentStockRequestItemRepository.findById(UUID.fromString(it.id)).get()
						upsertItems.quantity_requested = it.quantity_requested
						departmentStockRequestItemRepository.save(upsertItems)
					}
				}

			} else { //insert

				upsert.requestNo = generatorService.getNextValue(GeneratorType.DEPT_STOCK_REQ) { Long no ->
					'DSR-' + StringUtils.leftPad(no.toString(), 6, "0")
				}
				upsert.requestDate = obj.requestDate
				upsert.requestingDepartment = departmentRepository.findById(emp.departmentOfDuty.id).get()
				upsert.issuingDepartment = obj.issuingDepartment
				upsert.requestedBy = emp
				upsert.requestType = obj.requestType
				upsert.purpose = obj.purpose
				upsert.isCanceled = false
				upsert.status = 1
				def afterSave = departmentStockRequestRepository.save(upsert)

				//insert sources
				itemList.each {
					it ->
						def item = objectMapper.convertValue(it.item, Item)
						def upsertItems = new DepartmentStockRequestItem()
						upsertItems.departmentStockRequest = afterSave
						upsertItems.item = item
						upsertItems.quantity_requested = it.quantity_requested
						upsertItems.unit_cost = it.unit_cost
						departmentStockRequestItemRepository.save(upsertItems)
				}

			}
		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		return upsert
	}
	//code ni wilson
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "upsertDepStock", description = "insert BEG")
	DepartmentStockRequest upsertDepStock(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "payload") Map<String, Object> payload,
			@GraphQLArgument(name = "items") ArrayList<Map<String, Object>> items
	) {
		DepartmentStockRequest upsert = new DepartmentStockRequest()
		def obj = objectMapper.convertValue(payload, DepartmentStockRequest)
		def itemList = items as ArrayList<DepartmentStockRequestItemsDto>
		Employee emp = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()
		try { // try clause
			if (id) { //update
				upsert = departmentStockRequestRepository.findById(id).get()
				upsert.purpose = obj.purpose
				upsert.issuingDepartment = obj.issuingDepartment
				upsert.requestedBy = emp
				upsert.requestType = obj.requestType
				def afterSave = departmentStockRequestRepository.save(upsert)
				itemList.each {
					it ->
						def item = objectMapper.convertValue(it.item, Item)
						def upsertItems = new DepartmentStockRequestItem()
						if (it.isNew) {
							upsertItems.departmentStockRequest = afterSave
							upsertItems.item = item
							upsertItems.quantity_requested = it.quantity_requested
							upsertItems.unit_cost = it.unit_cost
							upsertItems.isPosted = false
							upsertItems.isRejected = false
							upsertItems.remarks = it.remarks
                            upsertItems.status = "Pending"
							departmentStockRequestItemRepository.save(upsertItems)
						} else {
							upsertItems = departmentStockRequestItemRepository.findById(UUID.fromString(it.id)).get()
							upsertItems.quantity_requested = it.quantity_requested
							upsertItems.remarks = it.remarks
							departmentStockRequestItemRepository.save(upsertItems)
						}
				}

			} else { //insert

				upsert.requestNo = generatorService.getNextValue(GeneratorType.DEPT_STOCK_REQ) { Long no ->
					'DSR-' + StringUtils.leftPad(no.toString(), 6, "0")
				}
				upsert.requestDate = obj.requestDate
				upsert.requestingDepartment = obj.requestingDepartment
				upsert.issuingDepartment = obj.issuingDepartment
				upsert.requestedBy = emp
				upsert.requestType = obj.requestType
				upsert.purpose = obj.purpose
				upsert.isCanceled = false
				upsert.status = 0
				def afterSave = departmentStockRequestRepository.save(upsert)

				//insert sources
				itemList.each {
					it ->
						def item = objectMapper.convertValue(it.item, Item)
						def upsertItems = new DepartmentStockRequestItem()
						upsertItems.departmentStockRequest = afterSave
						upsertItems.item = item
						upsertItems.quantity_requested = it.quantity_requested
						upsertItems.unit_cost = it.unit_cost
						upsertItems.preparedQty = 0
						upsertItems.isPosted = false
						upsertItems.isRejected = false
						upsertItems.remarks = it.remarks
                        upsertItems.status = "Pending"
						departmentStockRequestItemRepository.save(upsertItems)
				}

			}
		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		return upsert
	}

	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "postVoidDepStock")
	DepartmentStockRequest postVoidDepStock(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "status") Boolean status
	) {
		def upsert = departmentStockRequestRepository.findById(id).get()
		def items = departmentStockRequestItemRepository.findItemsByRequest(id)
		if(status){
			upsert.isPosted = true
			upsert.isCanceled = false
		}else{
			upsert.isPosted = false
			upsert.isCanceled = true

			//items
			items.each {
				def upsertItem = it
				upsertItem.status = "Cancelled"
				departmentStockRequestItemRepository.save(upsertItem)
			}
		}
		departmentStockRequestRepository.save(upsert)
	}
}

