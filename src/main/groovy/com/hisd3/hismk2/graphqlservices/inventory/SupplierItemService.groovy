package com.hisd3.hismk2.graphqlservices.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.inventory.DepartmentItem
import com.hisd3.hismk2.domain.inventory.Generic
import com.hisd3.hismk2.domain.inventory.SupplierItem
import com.hisd3.hismk2.repository.inventory.SupplierItemRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.checkerframework.checker.units.qual.A
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@GraphQLApi
@TypeChecked
class SupplierItemService {
	
	@Autowired
	SupplierItemRepository supplierItemRepository

	@Autowired
	ObjectMapper objectMapper
	
	@GraphQLQuery(name = "allSupplyItemsBySupplier", description = "List of Supplies")
	List<SupplierItem> allSupplyItems(
			@GraphQLArgument(name = "id") String id,
			@GraphQLArgument(name = "filter") String filter
	) {
		return supplierItemRepository.findBySupplier(UUID.fromString(id), filter).sort{it.item.descLong}
	}

	@GraphQLQuery(name = "supplyItemsBySupplier", description = "List of Supplies")
	List<SupplierItem> supplyItemsBySupplier(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "filter") String filter
	) {
		return supplierItemRepository.findBySupplier(id, filter).sort{it.item.descLong}
	}
	
	@GraphQLQuery(name = "getSupplyItem", description = "Supply Item")
	SupplierItem getSupplyItem(
			@GraphQLArgument(name = "id") String id
	) {
		return supplierItemRepository.findById(UUID.fromString(id)).get()
	}
	
	@GraphQLQuery(name = "getSupplierByItem", description = "Supplier Items")
	List<SupplierItem> getSupplierByItem(
			@GraphQLArgument(name = "id") UUID id
	) {
		return supplierItemRepository.findSupplierByItem(id)
	}

	@GraphQLQuery(name = "getSupItemByBarcode", description = "Supplier Items")
	List<SupplierItem> getSupItemByBarcode(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "barcode") String barcode
	) {
		return supplierItemRepository.getSupItemByBarcode(id, barcode)
	}

	@GraphQLQuery(name = "supplierItemPage", description = "List of Page Supplier Item")
	Page<SupplierItem> genericPageFilter(@GraphQLArgument(name = "id") UUID id,
										 @GraphQLArgument(name = "page") Integer page, // zero based
										 @GraphQLArgument(name = "size") Integer size,
										 @GraphQLArgument(name = "filter") String filter) {

		return supplierItemRepository.findBySupplierPageable(id, filter, new PageRequest(page, size, Sort.Direction.ASC, "item.descLong"))
	}
	//mutation
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertItemSupplier", description = "upsert Department Item")
	SupplierItem upsertItemSupplier(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "id") UUID id
	) {
		SupplierItem supItem = new SupplierItem()
		def obj = objectMapper.convertValue(fields, SupplierItem.class)
		def check = supplierItemRepository.findIfExist(obj.supplier.id, obj.item.id)
		if(check){
			supItem = supplierItemRepository.findById(check.id).get()
			supItem.cost = obj.cost
		}else {
			if (id) { //update
				supItem = supplierItemRepository.findById(id).get()
				supItem.supplier = obj.supplier
				supItem.item = obj.item
				supItem.cost = obj.cost
			} else { //insert
				supItem.supplier = obj.supplier
				supItem.item = obj.item
				supItem.cost = obj.cost
			}
		}
		supplierItemRepository.save(supItem)
	}

	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "removeItemSupplier", description = "Remove")
	SupplierItem removeItemSupplier(
			@GraphQLArgument(name = "id") UUID id
	) {
		SupplierItem rm = supplierItemRepository.findById(id).get()
		supplierItemRepository.delete(rm)
	}
	
}
