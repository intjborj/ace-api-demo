package com.hisd3.hismk2.graphqlservices.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.billing.Investor
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.inventory.BeginningBalance
import com.hisd3.hismk2.domain.inventory.Supplier
import com.hisd3.hismk2.domain.inventory.SupplierItem
import com.hisd3.hismk2.repository.billing.InvestorsRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.inventory.PaymentTermRepository
import com.hisd3.hismk2.repository.inventory.PurchaseRequestItemRepository
import com.hisd3.hismk2.repository.inventory.SupplierItemRepository
import com.hisd3.hismk2.repository.inventory.SupplierRepository
import com.hisd3.hismk2.repository.inventory.SupplierTypeRepository
import com.hisd3.hismk2.rest.dto.QueryErrorException
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

@Component
@GraphQLApi
@TypeChecked
class SupplierService {
	
	@Autowired
	SupplierRepository supplierRepository
	
	@Autowired
	PurchaseRequestItemRepository purchaseRequestItemRepository
	
	@Autowired
	SupplierItemRepository supplierItemRepository

	@Autowired
	SupplierTypeRepository supplierTypeRepository

	@Autowired
	PaymentTermRepository paymentTermRepository

	@Autowired
	EmployeeRepository employeeRepository

	@Autowired
	InvestorsRepository investorsRepository

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	GeneratorService generatorService
	
	@GraphQLQuery(name = "supplier_list", description = "List of Suppliers")
	List<Supplier> allSupplier(
			@GraphQLArgument(name = "filter") String filter
	) {
		return supplierRepository.findAllByFilter(filter)
	}

	@GraphQLQuery(name = "supplier_list_pageable", description = "List of Suppliers")
	Page<Supplier> allSupplierPageable(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "size") Integer size,
			@GraphQLArgument(name = "page") Integer page
	) {
		return supplierRepository.findAllByFilterPageable(filter, new PageRequest(page, size, Sort.Direction.ASC, "supplierFullname"))
	}

	@GraphQLQuery(name = "supplier_list_pageable_active", description = "List of Suppliers")
	Page<Supplier> allSupplierPageableActive(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "size") Integer size,
			@GraphQLArgument(name = "page") Integer page
	) {
		return supplierRepository.findAllByFilterActivePageable(filter, new PageRequest(page, size, Sort.Direction.ASC, "supplierFullname"))
	}

	@GraphQLQuery(name = "supplier", description = "List of Suppliers")
	Supplier getSupplier(
			@GraphQLArgument(name = "id") String id
	) {
		return supplierRepository.findById(UUID.fromString(id)).get()
	}

	@GraphQLQuery(name = "supplierById", description = "List of Suppliers")
	Supplier supplierById(
			@GraphQLArgument(name = "id") UUID id
	) {
		return supplierRepository.findById(id).get()
	}

	@GraphQLQuery(name = "supplierByInvstorId", description = "Get one supplier by investor ID")
	Supplier supplierByInvstorId(
			@GraphQLArgument(name = "id") UUID id
	) {
		return supplierRepository.findById(id).get()
	}
	
	@GraphQLQuery(name = "supplierActive", description = "List of Active Supplier")
	List<Supplier> SupplierActive() {
		return supplierRepository.SupplierActive().sort { it.createdDate }
	}
	
	//validation
	@GraphQLQuery(name = "isSupplierCodeUnique", description = "Check if Supplier Code exists")
	Boolean findOneBySupplierCode(@GraphQLArgument(name = "supplierCode") String supplierCode) {
		return !supplierRepository.findOneBySupplierCode(supplierCode)
	}
	
	@GraphQLQuery(name = "isSupplierNameUnique", description = "Check if Supplier Name exists")
	Boolean findOneBySupplierName(@GraphQLArgument(name = "supplierFullname") String supplierFullname) {
		return !supplierRepository.findOneBySupplierName(supplierFullname)
	}
	//end validation
	
	@GraphQLQuery(name = "supplier_by_pr", description = "List of supplier base on pr items")
	Set<Supplier> supplierByPr() {
		def prItems = purchaseRequestItemRepository.getPrItemByWherePoIsNotNullandStatusIsApproved()
		Set<Supplier> supplierSet = []
		prItems.each {
			it ->
				List<SupplierItem> supplierItems = supplierItemRepository.findByItem(it.item.id)
				
				supplierItems.each {
					it2 ->
						supplierSet.add(it2.supplier)
				}
			
		}
		
		supplierSet.unique {
			supplier -> supplier.id
		}
		
		return supplierSet
	}

	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "upsertSupplier", description = "insert BEG")
	Supplier upsertSupplier(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "id") UUID id
	) {
		Supplier insert = new Supplier()
		def supplier =  objectMapper.convertValue(fields, Supplier)
		try {
			if(id){
				insert = supplierRepository.findById(id).get()
			}
			if(!id){
				insert.supplierCode = generatorService.getNextValue(GeneratorType.SUPPLIER_CODE, {
					return "SUP-" + StringUtils.leftPad(it.toString(), 6, "0")
				})
			}
			insert.supplierFullname = supplier.supplierFullname
			insert.supplierTin = supplier.supplierTin
			insert.supplierEmail = supplier.supplierEmail
			insert.paymentTerms = supplier.paymentTerms
			insert.supplierEntity = supplier.supplierEntity
			insert.supplierTypes = supplier.supplierTypes
			insert.creditLimit = supplier.creditLimit
			insert.isVatable = supplier.isVatable
			insert.isVatInclusive = supplier.isVatInclusive
			insert.remarks = supplier.remarks
			insert.leadTime = supplier.leadTime
			insert.primaryAddress = supplier.primaryAddress
			insert.primaryTelphone = supplier.primaryTelphone
			insert.primaryContactPerson = supplier.primaryContactPerson
			insert.primaryFax = supplier.primaryFax
			insert.secondaryAddress = supplier.secondaryAddress
			insert.secondaryTelphone = supplier.secondaryTelphone
			insert.secondaryContactPerson = supplier.secondaryContactPerson
			insert.secondaryFax = supplier.secondaryFax
			insert.isActive = supplier.isActive
			insert.atcNo = supplier.atcNo
			insert.ewtRate = supplier.ewtRate
			supplierRepository.save(insert)

		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		return insert
	}

	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "upsertSupplierByInvestor")
	Supplier upsertSupplierByInvestor(
			@GraphQLArgument(name = "investorId") UUID investorId
	) {
		Supplier insert = new Supplier()
		try {
			Investor investor = investorsRepository.findById(investorId).get()
				insert = new Supplier()
				insert.supplierCode = generatorService.getNextValue(GeneratorType.SUPPLIER_CODE, {
					return "SUP-" + StringUtils.leftPad(it.toString(), 6, "0")
				})
				insert.supplierFullname = investor.fullName.toUpperCase()
				insert.supplierTin = investor.tinNo
				insert.supplierEmail = investor.emailAddress
				insert.paymentTerms = paymentTermRepository.findAll().first()
				insert.supplierEntity = "PERSONAL"
				insert.supplierTypes = supplierTypeRepository.findAll().first()
				insert.remarks = null
				insert.leadTime = 0
				insert.isVatable = true
				insert.isVatInclusive = true
				insert.ewtRate = 0.10
				insert.primaryAddress = investor.address
				insert.employeeId = null
				insert.investorId = investorId
				insert.isActive = true
				supplierRepository.save(insert)

		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		return insert
	}

	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "upsertSupplierByEmployee")
	Supplier upsertSupplierByEmployee(
			@GraphQLArgument(name = "fields") ArrayList<Map<String, Object>> fields,
			@GraphQLArgument(name = "payment") UUID payment,
			@GraphQLArgument(name = "category") UUID category
	) {
		Supplier insert = new Supplier()
		try {
			fields.each {
				def emp =  objectMapper.convertValue(it, Employee.class)
				insert = new Supplier()
				insert.supplierCode = generatorService.getNextValue(GeneratorType.SUPPLIER_CODE, {
					return "SUP-" + StringUtils.leftPad(it.toString(), 6, "0")
				})
				insert.supplierFullname = emp.fullName.toUpperCase()
				insert.supplierTin = emp.tinNo
				insert.supplierEmail = emp.emailAddress
				insert.paymentTerms = paymentTermRepository.findById(payment).get()
				insert.supplierEntity = "PERSONAL"
				insert.supplierTypes = supplierTypeRepository.findById(category).get()
				insert.remarks = null
				insert.leadTime = 0
				insert.isVatable = true
				insert.isVatInclusive = true
				insert.ewtRate = 0.10
				insert.primaryAddress = "${emp.address} ${emp.address2}, ${emp.cityMunicipality} ${emp.stateProvince}".toUpperCase()
				insert.employeeId = emp.id
				insert.isActive = true
				def afterSave = supplierRepository.save(insert)

				//update employee
				def updateEmp = employeeRepository.findById(emp.id).get()
				updateEmp.supplierId = afterSave.id
				employeeRepository.save(updateEmp)
			}

		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		return insert
	}

	@GraphQLQuery(name = "supplier_list_by_supp_entity_pageable", description = "List of Suppliers per entity")
	Page<Supplier> supplier_list_by_supp_entity_pageable(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "size") Integer size,
			@GraphQLArgument(name = "page") Integer page
	) {
		return supplierRepository.findDoctorPersonalAccountByFilterPageable(filter, new PageRequest(page, size, Sort.Direction.ASC, "supplierFullname"))
	}

	@GraphQLQuery(name = "supplier_list_by_type_pageable_active", description = "List of Suppliers")
	Page<Supplier> supplierByTypePageableActive(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "type") String type,
			@GraphQLArgument(name = "size") Integer size,
			@GraphQLArgument(name = "page") Integer page
	) {
		return supplierRepository.findAllByFilterActiveByTypePageable(filter,type, new PageRequest(page, size, Sort.Direction.ASC, "supplierFullname"))
	}
}
