package com.hisd3.hismk2.graphqlservices.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.dao.price_tier.PriceTierDetailDao
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.billing.PriceTierDetail
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.inventory.Inventory
import com.hisd3.hismk2.domain.inventory.PhysicalCount
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.graphqlservices.billing.BillingService
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.billing.PriceTierDetailRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.inventory.InventoryRepository
import com.hisd3.hismk2.repository.inventory.ItemRepository
import com.hisd3.hismk2.repository.inventory.MaterialProductionItemRepository
import com.hisd3.hismk2.repository.inventory.MaterialProductionRepository
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.rest.InventoryResource
import com.hisd3.hismk2.rest.dto.InventoryDto
import com.hisd3.hismk2.rest.dto.OnHandReport
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.GeneratorService
import groovy.transform.Canonical
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLContext
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext


@Canonical
class InventoryItem{

	UUID id
	UUID itemId
	String  sku
	String  itemCode
	String  descLong
	String  brand
	BigDecimal reorderQty
	Boolean allowTrade
	Long onHand
	BigDecimal lastUnitCost
	BigDecimal calculatedAmount

}
@Component
@GraphQLApi
@TypeChecked
@Slf4j
class InventoryService {

	@Autowired
	InventoryRepository inventoryRepository

	@Autowired
	MaterialProductionRepository materialProductionRepository

	@Autowired
	MaterialProductionItemRepository materialProductionItemRepository

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	InventoryLedgerService inventoryLedgerService

	@Autowired
	EmployeeRepository employeeRepository

	@Autowired
	GeneratorService generatorService

	@Autowired
	PriceTierDetailDao priceTierDetailDao

	@Autowired
	PriceTierDetailRepository priceTierDetailRepository

	@Autowired
	BillingService billingService

	@Autowired
	InventoryResource inventoryResource

	@Autowired
	ItemRepository itemRepository

	@Autowired
	DepartmentRepository departmentRepository

	@Autowired
	CaseRepository caseRepository

	@PersistenceContext
	EntityManager entityManager

	@Autowired
	JdbcTemplate jdbcTemplate


	//context last_wcost
	@GraphQLQuery(name = "last_wcost")
	BigDecimal getPhysicalCountById(@GraphQLContext Inventory inventory) {
		def id = inventory.item.id.toString()
		return inventoryResource.getLastUnitPrice(id)
	}


	@GraphQLQuery(name = "inventory_list", description = "List of Inventory filtered by department")
	List<Inventory> allItems(@GraphQLArgument(name = "departmentid") UUID departmentid, @GraphQLArgument(name = "filter") String filter, @GraphQLArgument(name = "group") UUID group, @GraphQLArgument(name = "category") String[] category) {
		if (group && !category) {
			return inventoryRepository.inventoryByDepartmentAndFilterAndGroup(departmentid, filter, group)
		} else if (group && category) {
			List<UUID> cat = new ArrayList<UUID>()
			category.each {
				it ->
					cat.add(UUID.fromString(it))
			}
			return inventoryRepository.inventoryByDepartmentAndFilterAndGroupAndCat(departmentid, filter, group, cat)
		} else {
			return inventoryRepository.inventoryByDepartmentAndFilter(departmentid, filter)
		}

	}

	@GraphQLQuery(name = "inventory_list_paged", description = "List of Inventory filtered by department")
	Page<Inventory> inventory_list_paged(
			@GraphQLArgument(name = "departmentid") UUID departmentid,
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "group") UUID group,
			@GraphQLArgument(name = "category") String[] category,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {
		if (group && !category) {
			return inventoryRepository.inventoryByDepartmentAndFilterAndGroupPaged(departmentid, filter, group, new PageRequest(page, size, Sort.Direction.ASC, "item.descLong"))
		} else if (group && category) {
			List<UUID> cat = new ArrayList<UUID>()
			category.each {
				it ->
					cat.add(UUID.fromString(it))
			}
			return inventoryRepository.inventoryByDepartmentAndFilterAndGroupAndCatPaged(departmentid, filter, group, cat, new PageRequest(page, size, Sort.Direction.ASC, "item.descLong"))
		} else {
			return inventoryRepository.inventoryByDepartmentAndFilterPaged(departmentid, filter, new PageRequest(page, size, Sort.Direction.ASC, "item.descLong"))
		}

	}

	@GraphQLQuery(name = "onHandReport", description = "List of Inventory filtered by department")
	List<OnHandReport> onHandReport(
			@GraphQLArgument(name = "departmentid") UUID departmentid,
			@GraphQLArgument(name = "date") String date,
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "status") String status = "ACTIVE"
	) {
		if (departmentid && date && status.equalsIgnoreCase("ALL")) {
			return inventoryResource.getOnhandReportByDate(departmentid, date, filter)
		}else if (departmentid && date && (status.equalsIgnoreCase("ACTIVE") || status.equalsIgnoreCase("INACTIVE"))) {
			return inventoryResource.getOnHandReport(departmentid, date, filter, status)
		} else {
			return null
		}
	}

	@GraphQLQuery(name = "inventory_list_by_supplies", description = "List of Inventory Supplies filtered by department")
	List<Inventory> getSupplies(@GraphQLArgument(name = "filter") String filter) {
		Employee e = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()
		return inventoryRepository.inventoryByDepartmentAndFilterSupplies(e.departmentOfDuty.id, filter)
	}

	//Albert Oclarit
	@GraphQLQuery(name = "inventory_list_by_supplies_pageable", description = "List of Inventory Supplies filtered by department Pageable")
	Page<InventoryItem> getSuppliesPageable(@GraphQLArgument(name = "filter") String filter,
											@GraphQLArgument(name = "caseId") UUID caseId,
											@GraphQLArgument(name = "page") Integer page,
											@GraphQLArgument(name = "size") Integer size
	) {
		Employee e = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()
		//def forProcess = inventoryRepository.inventoryByDepartmentAndFilterSuppliesPageable(e.departmentOfDuty.id, filter,
		//		new PageRequest(page, size, Sort.Direction.ASC, "item.descLong"))

/*	def matches =entityManager.createQuery("""
Select inv from Inventory inv where inv.depId=:departmentid and (inv.item.isMedicine = false or inv.item.isMedicine is null ) and inv.active = true and upper(inv.descLong) like  upper(concat('%',:filter,'%')) 
""",Inventory.class)
			.setParameter("departmentid",e.departmentOfDuty.id)
			.setParameter("filter",filter)
	        .setMaxResults(size)
	        .setFirstResult(page * size)
			.resultList*/
/*
 						SELECT  department_item.id,
						department_item.item,
						department_item.department,
						department_item.reorder_quantity,
						department_item.allow_trade,
						COALESCE(( SELECT inventory.onhand(department_item.department, department_item.item) AS onhand), 0) AS onhand,
						COALESCE(( SELECT inventory.last_unit_price(department_item.item) AS last_unit_price), 0::numeric) AS last_unit_cost,
						COALESCE(( SELECT inventory.last_wcost(department_item.item) AS last_wcost), 0::numeric) AS last_wcost,
						COALESCE(( SELECT inventory.expiry_date(department_item.item) AS expiry_date), NULL::date) AS expiration_date,
						item.desc_long,
						item.sku,
						item.item_code,
						item.active,
 					    item.brand,
						department_item.department AS dep_id,
						department_item.item AS item_id
						   FROM inventory.department_item,
							inventory.item
						  WHERE department_item.item = item.id and department_item.department = ? and (item.is_medicine = false or item.is_medicine is null)
						  and item.active = true and department_item.is_assign = true  and  item.desc_long ilike concat('%',?,'%') order by item.desc_long limit ? offset  ?*/


		def matches = jdbcTemplate.queryForList("""
 						SELECT a.id,
 						       a.item,
 						       a.department,
 						       a.reorder_quantity,
 						       a.allow_trade,
 						       COALESCE(c.onhand, 0::bigint) AS onhand,
 						       COALESCE(d.unitcost, 0::numeric) AS last_unit_cost,
 						       b.desc_long,
 						       b.sku,
 						       b.item_code,
 						       b.active,
 						       b.brand,
 						       a.department AS dep_id,
 						       a.item AS item_id
 						FROM inventory.department_item a
 						    LEFT JOIN inventory.onhandref c ON c.item = a.item AND c.source_dep = a.department
 						    LEFT JOIN inventory.unitcostref d ON d.item = a.item
 						    LEFT JOIN inventory.item b ON b.id = a.item
 						WHERE a.is_assign = true and a.department = ?
 						  and (b.is_medicine = false or b.is_medicine is null) and b.active = true and  b.desc_long ilike concat('%',?,'%') order by b.desc_long limit ? offset  ?
""",e.departmentOfDuty.id,filter,size,page * size)?.collect {
			new InventoryItem(
					it.get("id") as UUID,
					it.get("item") as UUID,
					it.get("sku") as String,
					it.get("item_code") as String,
					it.get("desc_long") as String,
					it.get("brand") as String,
					it.get("reorder_quantity") as BigDecimal,
					it.get("allow_trade") as Boolean,
					it.get("onhand") as Long,
					it.get("last_unit_cost") as BigDecimal,
					0.0
			)
		}


		def count =	jdbcTemplate.queryForObject("""
						 SELECT count(*)
						   FROM inventory.department_item,
							inventory.item
						  WHERE department_item.item = item.id and department_item.department = ? and (item.is_medicine = false or item.is_medicine is null)
						  and item.active = true and department_item.is_assign = true  and  item.desc_long ilike concat('%',?,'%')
  """,Long.class,e.departmentOfDuty.id,filter)



		PriceTierDetail priceTier = null

		// try manual billing
		def billing = billingService.findOne(caseId)
		if (!billing) {
			//log.warn("Billing is not Detected Trying Through Case")
			// this is not a billing object but as a case.... Prioritizing active Billing object which will rarely happened
			// This is to accomodate Ancillary pricing detection
			Case patientCase = caseRepository.findById(caseId).get()
			billing = billingService.activeBilling(patientCase)
		}

		if (billing?.pricetiermanual) {
			//log.warn("This is override Price Tier")
			priceTier = priceTierDetailRepository.findById(billing.pricetiermanual).get()
		}

		if (!priceTier) {
			//log.warn("Price Tier based from Case")
			if (billing)
				priceTier = priceTierDetailDao.getDetail(billing.patientCase.id)
		}

		if (!billing)
			log.warn("Billing not found")

		if (priceTier) {

			matches.each {
				it.calculatedAmount = priceTierDetailDao.getItemPrice(priceTier.id, it.itemId)
			}

		}



		return new PageImpl<InventoryItem>(matches,PageRequest.of(page,size),count)

	}

	@GraphQLQuery(name = "inventory_list_by_medicine", description = "List of Inventory Medicine filtered by department")
	List<Inventory> getMedicines(@GraphQLArgument(name = "filter") String filter) {
		Employee e = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()
		def forProcess = inventoryRepository.inventoryByDepartmentAndFilterMedicine(e.departmentOfDuty.id, filter)

		forProcess
	}

	@GraphQLQuery(name = "pharmacyInventoryList", description = "List of pharmacy inventory")
	Page<Inventory> pharmacyInventoryList(@GraphQLArgument(name = "filter") String filter) {
		Department dept = departmentRepository.getMedicationStockRequestDepartment().find()
		return inventoryRepository.inventoryByDepartmentAndFilterMedicinePageable(dept.id, filter,
				new PageRequest(0, 10, Sort.Direction.ASC, "item.descLong"))
	}

	@GraphQLQuery(name = "inventoryByDate", description = "List of Inventiry by dep")
	InventoryDto inventoryByDate(
			@GraphQLArgument(name = "date") String date,
			@GraphQLArgument(name = "dep") UUID dep,
			@GraphQLArgument(name = "item") UUID item,
			@GraphQLArgument(name = "phy") UUID phy
	) {
		return inventoryResource.getOnHandLastWcostPhy(item, dep, date,phy )
	}



	//Albert Oclarit
	@GraphQLQuery(name = "inventory_list_by_medicine_pageable", description = "List of Inventory Medicine filtered by department Pageable")
	Page<InventoryItem> getMedicinesPageable(@GraphQLArgument(name = "filter") String filter,
											 @GraphQLArgument(name = "caseId") UUID caseId,
											 @GraphQLArgument(name = "page") Integer page,
											 @GraphQLArgument(name = "size") Integer size) {
		Employee e = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()
		/*	def forProcess = inventoryRepository.inventoryByDepartmentAndFilterMedicinePageable(e.departmentOfDuty.id, filter,
                    new PageRequest(page, size, Sort.Direction.ASC, "item.descLong"))*/


/*		def matches =entityManager.createQuery("""
Select inv from Inventory inv where inv.depId=:departmentid and (inv.item.isMedicine = true ) and inv.active = true and upper(inv.descLong) like  upper(concat('%',:filter,'%')) 
""",Inventory.class)
				.setParameter("departmentid",e.departmentOfDuty.id)
				.setParameter("filter",filter)
				.setMaxResults(size)
				.setFirstResult(page * size)
				.resultList*/

		def matches = jdbcTemplate.queryForList("""
 						SELECT a.id,
 						       a.item,
 						       a.department,
 						       a.reorder_quantity,
 						       a.allow_trade,
 						       COALESCE(c.onhand, 0::bigint) AS onhand,
 						       COALESCE(d.unitcost, 0::numeric) AS last_unit_cost,
 						       b.desc_long,
 						       b.sku,
 						       b.item_code,
 						       b.active,
 						       b.brand,
 						       a.department AS dep_id,
 						       a.item AS item_id
 						FROM inventory.department_item a
 						    LEFT JOIN inventory.onhandref c ON c.item = a.item AND c.source_dep = a.department
 						    LEFT JOIN inventory.unitcostref d ON d.item = a.item
 						    LEFT JOIN inventory.item b ON b.id = a.item
 						WHERE a.is_assign = true and a.department = ?
 						  and (b.is_medicine = true) and b.active = true and  b.desc_long ilike concat('%',?,'%') order by b.desc_long limit ? offset  ?
""",e.departmentOfDuty.id,filter,size,page * size)?.collect {
			new InventoryItem(
					it.get("id") as UUID,
					it.get("item") as UUID,
					it.get("sku") as String,
					it.get("item_code") as String,
					it.get("desc_long") as String,
					it.get("brand") as String,
					it.get("reorder_quantity") as BigDecimal,
					it.get("allow_trade") as Boolean,
					it.get("onhand") as Long,
					it.get("last_unit_cost") as BigDecimal,
					0.0
			)
		}



		def count =	jdbcTemplate.queryForObject("""
						 SELECT count(*)
						   FROM inventory.department_item,
							inventory.item
						  WHERE department_item.item = item.id and department_item.department = ? and (item.is_medicine = true)
						  and item.active = true and department_item.is_assign = true and  item.desc_long ilike concat('%',?,'%')
  """,Long.class,e.departmentOfDuty.id,filter)

		PriceTierDetail priceTier = null
		// try manual billing
		// try manual billing
		def billing = billingService.findOne(caseId)
		if (!billing) {
			//log.warn("Billing is not Detected Trying Through Case")
			// this is not a billing object but as a case.... Prioritizing active Billing object which will rarely happened
			// This is to accomodate Ancillary pricing detection
			Case patientCase = caseRepository.findById(caseId).get()
			billing = billingService.activeBilling(patientCase)
		}

		if (billing?.pricetiermanual) {
			//log.warn("This is override Price Tier")
			priceTier = priceTierDetailRepository.findById(billing.pricetiermanual).get()
		}

		if (!priceTier) {
			//log.warn("Price Tier based from Case")
			if (billing)
				priceTier = priceTierDetailDao.getDetail(billing.patientCase.id)
		}

		if (!billing)
			log.warn("Billing not found")

		if (priceTier) {

			matches.each {
				it.calculatedAmount = priceTierDetailDao.getItemPrice(priceTier.id, it.itemId)
			}

		}

		return new PageImpl<InventoryItem>(matches,PageRequest.of(page,size),count)
	}

	@GraphQLQuery(name = "inventory_list_pageable", description = "List of Inventory filtered by department")
	Page<Inventory> allItems(
			@GraphQLArgument(name = "departmentid") UUID departmentid,
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "pageSize") Integer pageSize
	) {
		return inventoryRepository.inventoryByDepartmentAndFilterPageable(departmentid, filter, new PageRequest(page, pageSize, Sort.Direction.ASC, "item.descLong"))
	}

	@GraphQLQuery(name = "inventory_list_pageable_non_production", description = "List of Inventory filtered by department")
	Page<Inventory> allItemsNonProduction(
			@GraphQLArgument(name = "departmentid") UUID departmentid,
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "pageSize") Integer pageSize
	) {
		return inventoryRepository.inventoryByDepartmentAndFilterPageableNonProduction(departmentid, filter, new PageRequest(page, pageSize, Sort.Direction.ASC, "item.descLong"))
	}

	@GraphQLQuery(name = "invetory_list_all_pageable", description = "List of Inventory filtered by department")
	Page<Inventory> allItemsPageable(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "department") UUID department,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "pageSize") Integer pageSize
	) {
		return inventoryRepository.inventoryFilterPageable(filter, department, new PageRequest(page, pageSize))
	}

	@GraphQLQuery(name = "inventoryItemsByFilter", description = "List of Inventory Items")
	List<Inventory> getInventoryByFilter(@GraphQLArgument(name = "filter") String filter) {
		return inventoryRepository.itemsByFilter(filter).sort { it.item.descLong }
	}
}
