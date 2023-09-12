package com.hisd3.hismk2.graphqlservices.ancillary

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.ancillary.OrderSlipItem
import com.hisd3.hismk2.domain.ancillary.OrderSlipItemPackageContent
import com.hisd3.hismk2.domain.ancillary.PackageContent
import com.hisd3.hismk2.domain.inventory.BeginningDetails
import com.hisd3.hismk2.domain.inventory.BeginningTransaction
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.inventory.InventoryLedgerService
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.inventory.ItemRepository
import com.hisd3.hismk2.rest.InventoryResource
import com.hisd3.hismk2.rest.dto.BeginningItemDto
import com.hisd3.hismk2.services.GeneratorService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@GraphQLApi
@Transactional(rollbackFor = Exception.class)
class OrderSlipItemPackageContentService extends AbstractDaoService<OrderSlipItemPackageContent> {

	OrderSlipItemPackageContentService() {
		super(OrderSlipItemPackageContent.class)
	}

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	GeneratorService generatorService

	@Autowired
	ItemRepository itemRepository

	@Autowired
	DepartmentRepository departmentRepository

	@Autowired
	InventoryLedgerService inventoryLedgerService

	@Autowired
	InventoryResource inventoryResource

	@GraphQLQuery(name = "orderSlipItemPackageList")
	List<OrderSlipItemPackageContent> orderSlipItemPackageList(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "filter") String filter
	) {
		createQuery("Select b from OrderSlipItemPackageContent b where b.orderSlipItem.id = :id and lower(b.itemName) like lower(concat('%',:filter,'%'))",
				[filter: filter, id: id]).resultList.sort{it.itemName}
	}

	@GraphQLQuery(name = "orderSlipItemPackageByParent")
	List<OrderSlipItemPackageContent> orderSlipItemPackageByParent(
			@GraphQLArgument(name = "id") UUID id
	) {
		createQuery("Select b from OrderSlipItemPackageContent b where b.orderSlipItem.id = :id",
				[id: id]).resultList.sort{it.itemName}
	}

	// Mutation
	@GraphQLMutation(name = "upsertOrderSlipItemPackage")
	OrderSlipItemPackageContent upsertOrderSlipItemPackage(
			@GraphQLArgument(name="orderSlipItem") OrderSlipItem it,
			@GraphQLArgument(name="department") Department department,
			@GraphQLArgument(name="item") Item item,
			@GraphQLArgument(name="qty") Integer qty
	) {
		OrderSlipItemPackageContent upsert = new OrderSlipItemPackageContent()
		upsert.orderSlipItem = it
		upsert.department = department
		upsert.item = item
		upsert.itemName = item.descLong
		upsert.qty = qty
		def id = item.id
		if(id){
			def cost = inventoryResource.getLastUnitPrice(id.toString())
			upsert.unitCost = cost.abs()
		}
		save(upsert)
	}

	// Mutation
	@GraphQLMutation(name = "updateBillingPackageItem")
	OrderSlipItemPackageContent updateBillingPackageItem(
			@GraphQLArgument(name="item") OrderSlipItemPackageContent item,
			@GraphQLArgument(name="id") UUID id
	) {
		OrderSlipItemPackageContent upsert = item
		upsert.refBillingItem = id
		save(upsert)
	}

	@GraphQLMutation(name = "updateBillingPackageList")
	List<OrderSlipItemPackageContent> updateBillingPackageList(
			@GraphQLArgument(name="id") UUID id,
			@GraphQLArgument(name="refNo") String refNo
	) {
		def items = this.orderSlipItemPackageByParent(id)
		items.each {
			def upsert = it
			upsert.refBillingItem = null

			//remove to stock card
			inventoryLedgerService.deleteInventoryGlobal(refNo)
		}
		return items
	}


}
