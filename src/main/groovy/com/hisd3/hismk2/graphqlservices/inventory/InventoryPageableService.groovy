package com.hisd3.hismk2.graphqlservices.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.dao.price_tier.PriceTierDetailDao
import com.hisd3.hismk2.domain.User
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.inventory.Inventory
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.inventory.DepartmentItemRepository
import com.hisd3.hismk2.rest.InventoryResource
import com.hisd3.hismk2.rest.dto.BillingBarcodeDto
import com.hisd3.hismk2.rest.dto.IssuanceBarcodeItemDto
import com.hisd3.hismk2.rest.dto.SupplierBarcodeItemDto
import com.hisd3.hismk2.rest.dto.SupplyDto
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.GeneratorService
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

import java.time.Duration
import java.time.Instant

@Component
@GraphQLApi
@TypeChecked
class InventoryPageableService extends AbstractDaoService<Inventory> {

	InventoryPageableService() {
		super(Inventory.class)
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
	InventoryResource inventoryResource

	@Autowired
	PriceTierDetailDao priceTierDetailDao

	@Autowired
	DepartmentItemRepository departmentItemRepository

	@GraphQLQuery(name = "inventoryListPageable")
	Page<Inventory> findByFilters(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "group") UUID group,
			@GraphQLArgument(name = "depId") UUID depId,
			@GraphQLArgument(name = "category") List<UUID> category,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		Instant start = Instant.now();
		User user = userRepository.findOneByLogin(SecurityUtils.currentLogin())
		Employee employee = employeeRepository.findOneByUser(user)

		String query = '''Select inv from Inventory inv where
						lower(concat(inv.descLong,inv.sku)) like lower(concat('%',:filter,'%'))
						and inv.depId=:departmentid and
						inv.active = true'''

		String countQuery = '''Select count(inv) from Inventory inv where
							lower(concat(inv.descLong,inv.sku)) like lower(concat('%',:filter,'%'))
							and inv.depId=:departmentid and
							inv.active = true'''

		Map<String, Object> params = new HashMap<>()
		params.put('departmentid', employee.departmentOfDuty.id)
		params.put('filter', filter)

		if (group) {
			query += ''' and (inv.item_group = :group)'''
			countQuery += ''' and (inv.item_group = :group)'''
			params.put("group", group)
		}

		if (category) {
			query += ''' and (inv.item_category IN (:category))'''
			countQuery += ''' and (inv.item_category IN (:category))'''
			params.put("category", category)
		}

		query += ''' ORDER BY inv.descLong ASC'''

		Page<Inventory> result = getPageable(query, countQuery, page, size, params)
		return result
	}

	@GraphQLQuery(name = "inventoryListPageableByDep")
	Page<Inventory> inventoryListPageableByDep(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "dep") UUID dep,
			@GraphQLArgument(name = "group") UUID group,
			@GraphQLArgument(name = "category") List<UUID> category,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		String query = '''Select inv from Inventory inv where
						lower(concat(inv.descLong,inv.sku)) like lower(concat('%',:filter,'%'))
						and inv.depId=:departmentid and
						inv.active = true'''

		String countQuery = '''Select count(inv) from Inventory inv where
							lower(concat(inv.descLong,inv.sku)) like lower(concat('%',:filter,'%'))
							and inv.depId=:departmentid and
							inv.active = true'''

		Map<String, Object> params = new HashMap<>()
		params.put('departmentid', dep)
		params.put('filter', filter)

		if (group) {
			query += ''' and (inv.item_group = :group)'''
			countQuery += ''' and (inv.item_group = :group)'''
			params.put("group", group)
		}

		if (category) {
			query += ''' and (inv.item_category IN (:category))'''
			countQuery += ''' and (inv.item_category IN (:category))'''
			params.put("category", category)
		}

		query += ''' ORDER BY inv.descLong ASC'''

		Page<Inventory> result = getPageable(query, countQuery, page, size, params)
		return result
	}

	@GraphQLQuery(name = "inventoryOutputPageable")
	Page<Inventory> inventoryOutputPageable(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "dep") UUID dep,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {
		String query = '''Select inv from Inventory inv where
						lower(concat(inv.descLong,inv.sku)) like lower(concat('%',:filter,'%'))
						and inv.depId=:departmentid and
						inv.active = true and inv.production = true'''

		String countQuery = '''Select count(inv) from Inventory inv where
							lower(concat(inv.descLong,inv.sku)) like lower(concat('%',:filter,'%'))
							and inv.depId=:departmentid and
							inv.active = true and inv.production = true'''

		Map<String, Object> params = new HashMap<>()
		params.put('departmentid', dep)
		params.put('filter', filter)

		query += ''' ORDER BY inv.descLong ASC'''

		Page<Inventory> result = getPageable(query, countQuery, page, size, params)
		return result
	}

	@GraphQLQuery(name = "inventorySourcePageable")
	Page<Inventory> inventorySourcePageable(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "dep") UUID dep,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {
		String query = '''Select inv from Inventory inv where
						lower(concat(inv.descLong,inv.sku)) like lower(concat('%',:filter,'%'))
						and inv.depId=:departmentid and
						inv.active = true and (inv.production = false or inv.production is null )'''

		String countQuery = '''Select count(inv) from Inventory inv where
							lower(concat(inv.descLong,inv.sku)) like lower(concat('%',:filter,'%'))
							and inv.depId=:departmentid and
							inv.active = true and (inv.production = false or inv.production is null )'''

		Map<String, Object> params = new HashMap<>()
		params.put('departmentid', dep)
		params.put('filter', filter)

		query += ''' ORDER BY inv.descLong ASC'''

		Page<Inventory> result = getPageable(query, countQuery, page, size, params)
		return result
	}

	@GraphQLQuery(name = "inventoryListPageableByDepStatus")
	Page<Inventory> inventoryListPageableByDepStatus(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "dep") UUID dep,
			@GraphQLArgument(name = "group") UUID group,
			@GraphQLArgument(name = "category") List<UUID> category,
			@GraphQLArgument(name = "status") String status,
			@GraphQLArgument(name = "consignment") Boolean consignment,
			@GraphQLArgument(name = "asset") Boolean asset,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		String query = '''Select inv from Inventory inv where
						lower(concat(inv.descLong,inv.sku)) like lower(concat('%',:filter,'%'))
						and inv.depId=:departmentid and
						inv.active = true'''

		String countQuery = '''Select count(inv) from Inventory inv where
							lower(concat(inv.descLong,inv.sku)) like lower(concat('%',:filter,'%'))
							and inv.depId=:departmentid and
							inv.active = true'''

		Map<String, Object> params = new HashMap<>()
		params.put('departmentid', dep)
		params.put('filter', filter)


		if (group) {
			query += ''' and (inv.item_group = :group)'''
			countQuery += ''' and (inv.item_group = :group)'''
			params.put("group", group)
		}

		if (category) {
			query += ''' and (inv.item_category IN (:category))'''
			countQuery += ''' and (inv.item_category IN (:category))'''
			params.put("category", category)
		}

		if(consignment){
			query += ''' and inv.consignment=:consignment'''
			countQuery += ''' and inv.consignment=:consignment'''
			params.put('consignment', consignment)
		}else{
			query += ''' and (inv.consignment=:consignment or inv.consignment is null)'''
			countQuery += ''' and (inv.consignment=:consignment or inv.consignment is null)'''
			params.put('consignment', consignment)
		}

		if(asset){
			query += ''' and inv.fixAsset=:asset'''
			countQuery += ''' and inv.fixAsset=:asset'''
			params.put('asset', asset)
		}else{
			query += ''' and (inv.fixAsset=:asset or inv.fixAsset is null)'''
			countQuery += ''' and (inv.fixAsset=:asset or inv.fixAsset is null)'''
			params.put('asset', asset)
		}

		//filter by status
		if(status){
			if(status.equalsIgnoreCase("healthy")){
				query += ''' and (inv.reOrderQty < inv.onHand)'''
				countQuery += ''' and (inv.reOrderQty < inv.onHand)'''
			}else if(status.equalsIgnoreCase("no_stock")){
				query += ''' and (inv.onHand = 0)'''
				countQuery += ''' and (inv.onHand = 0)'''
			}else if(status.equalsIgnoreCase("negative")){
				query += ''' and (inv.onHand < 0)'''
				countQuery += ''' and (inv.onHand < 0)'''
			}else {
				query += ''' and (inv.onHand < inv.reOrderQty) and (inv.onHand != 0) and (inv.onHand > 0)'''
				countQuery += ''' and (inv.onHand < inv.reOrderQty) and (inv.onHand != 0) and (inv.onHand > 0)'''
			}
		}

		query += ''' ORDER BY inv.descLong ASC'''

		Page<Inventory> result = getPageable(query, countQuery, page, size, params)
		return result
	}

	@GraphQLQuery(name = "inventoryListByDepStatus", description = "transaction type by tag")
	List<Inventory> inventoryListByDepStatus(@GraphQLArgument(name = "filter") String filter,
										   @GraphQLArgument(name = "dep") UUID dep,
										   @GraphQLArgument(name = "group") UUID group,
										   @GraphQLArgument(name = "category") List<UUID> category,
										   @GraphQLArgument(name = "status") String status,
											 @GraphQLArgument(name = "consignment") Boolean consignment) {

		String query = '''Select inv from Inventory inv where
						lower(concat(inv.descLong,inv.sku)) like lower(concat('%',:filter,'%'))
						and inv.depId=:departmentid and
						inv.active = true and inv.consignment=:consignment'''

		Map<String, Object> params = new HashMap<>()
		params.put('departmentid', dep)
		params.put('filter', filter)
		params.put('consignment', consignment)

		if (group) {
			query += ''' and (inv.item_group = :group)'''
			params.put("group", group)
		}

		if (category) {
			query += ''' and (inv.item_category IN (:category))'''
			params.put("category", category)
		}

		//filter by status
		if(status){
			if(status.equalsIgnoreCase("healthy")){
				query += ''' and (inv.reOrderQty < inv.onHand)'''
			}else if(status.equalsIgnoreCase("no_stock")){
				query += ''' and (inv.onHand = 0)'''
			}else if(status.equalsIgnoreCase("negative")){
				query += ''' and (inv.onHand < 0)'''
			}else {
				query += ''' and (inv.onHand < inv.reOrderQty) and (inv.onHand != 0) and (inv.onHand > 0)'''
			}
		}

		createQuery(query, params).resultList.sort { it.descLong }
	}


	@GraphQLQuery(name = "inventoryBarcode", description = "transaction type by tag")
	Inventory inventoryBarcode(@GraphQLArgument(name = "barcode") String barcode,
							   @GraphQLArgument(name = "depId") UUID depId) {
		String query = '''Select inv from Inventory inv where
						lower(inv.sku) = lower(:barcode)
						and inv.depId=:departmentid and
						inv.active = true'''

		Map<String, Object> params = new HashMap<>()
		params.put('departmentid', depId)
		params.put('barcode', barcode)

		createQuery(query, params).resultList.find()
	}


	@GraphQLQuery(name = "supplierBarcode")
	SupplierBarcodeItemDto supplierBarcode(@GraphQLArgument(name = "barcode") String barcode,
										   @GraphQLArgument(name = "supplier") UUID supplier) {
		if(supplier){
			inventoryResource.supplierBarcode(supplier, barcode)
		}else{
			return null
		}
	}

	@GraphQLQuery(name = "issuanceBarcode")
	IssuanceBarcodeItemDto issuanceBarcodeDto(@GraphQLArgument(name = "barcode") String barcode,
											  @GraphQLArgument(name = "department") UUID department) {
		if(department){
			inventoryResource.issuanceBarcode(department, barcode)
		}else{
			return null
		}
	}

	@GraphQLQuery(name = "billingChargeBarcode")
	BillingBarcodeDto billingChargeBarcode(@GraphQLArgument(name = "sku") String barcode,
										   @GraphQLArgument(name = "priceTierId") UUID priceTierId,
										   @GraphQLArgument(name = "depId") UUID depId) {
		if(priceTierId){
			def inv = departmentItemRepository.findListByItemSKU(barcode, depId)
			BigDecimal price = priceTierDetailDao.getItemPrice(priceTierId, inv.item.id)

			def supply = new SupplyDto(
					id: inv.id,
					sku: inv.item.sku,
					itemCode: inv.item.itemCode,
					descLong: inv.item.descLong,
					brand: inv.item.brand,
					reorderQty: inv.reorder_quantity,
					allowTrade: inv.allow_trade,
					calculatedAmount: price
			);
			def result = new BillingBarcodeDto(
					id: inv.id,
					quantity: 1,
					supply: supply,
			)
			return result
		}else{
			return null
		}
	}

}
