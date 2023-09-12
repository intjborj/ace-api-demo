package com.hisd3.hismk2.graphqlservices.accounting

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.accounting.AccountsPayable
import com.hisd3.hismk2.domain.accounting.Disbursement
import com.hisd3.hismk2.domain.accounting.DisbursementAp
import com.hisd3.hismk2.domain.accounting.PettyCash
import com.hisd3.hismk2.domain.accounting.PettyCashItem
import com.hisd3.hismk2.domain.accounting.Reapplication
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.rest.dto.DisbursementApDto
import com.hisd3.hismk2.rest.dto.PCVItemsDto
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
class PettyCashItemServices extends AbstractDaoService<PettyCashItem> {

	@Autowired
	GeneratorService generatorService

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	DepartmentRepository departmentRepository


    PettyCashItemServices() {
		super(PettyCashItem.class)
	}
	
	@GraphQLQuery(name = "pettyCashItemById")
	PettyCashItem pettyCashItemById(
			@GraphQLArgument(name = "id") UUID id
	) {
		findOne(id)
	}
	
	@GraphQLQuery(name = "purchaseItemsByPetty")
	List<PettyCashItem> purchaseItemsByPetty(@GraphQLArgument(name = "id") UUID id) {
		createQuery("Select da from PettyCashItem da where da.pettyCash.id = :id", ["id": id]).resultList
	}


	//mutations
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertPurchaseItems")
	PettyCashItem upsertPurchaseItems(
			@GraphQLArgument(name="it") PCVItemsDto it,
			@GraphQLArgument(name="parent") PettyCash parent
	) {
		PettyCashItem upsert = new PettyCashItem()
		def item = objectMapper.convertValue(it.item, Item.class)
		def dep = objectMapper.convertValue(it.department, Department.class)
		if(!it.isNew){
			upsert = findOne(UUID.fromString(it.id))
		}
		upsert.item = item
		upsert.department = dep
		upsert.pettyCash = parent
		upsert.qty = it.qty
		upsert.unitCost = it.unitCost
		upsert.discRate = it.discRate
		upsert.discAmount = it.discAmount
		upsert.netAmount = it.netAmount
		upsert.isVat = it.isVat
		upsert.vatAmount = it.vatAmount
		save(upsert)
	}

}
