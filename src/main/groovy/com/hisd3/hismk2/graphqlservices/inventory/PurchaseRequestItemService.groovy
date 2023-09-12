package com.hisd3.hismk2.graphqlservices.inventory

import com.hisd3.hismk2.domain.inventory.PurchaseRequest
import com.hisd3.hismk2.domain.inventory.PurchaseRequestItem
import com.hisd3.hismk2.domain.inventory.ReceivingReportItem
import com.hisd3.hismk2.repository.inventory.PurchaseRequestItemRepository
import com.hisd3.hismk2.rest.dto.QueryErrorException
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import java.time.Instant
import java.time.ZoneId

@TypeChecked
@Component
@GraphQLApi
class PurchaseRequestItemService {
	
	@Autowired
	PurchaseRequestItemRepository purchaseRequestItemRepository
	
	@GraphQLQuery(name = "purchaseRequestItemList", description = "List of Purchase Request Items")
	List<PurchaseRequestItem> getAllPurchaseRequestItems() {
		return purchaseRequestItemRepository.findAll().sort { it.createdDate }
	}
	
	@GraphQLQuery(name = "purchaseRequestItemByPr", description = "List of Purchase Request Items By PR Id")
	List<PurchaseRequestItem> purchaseRequestItemByPr(
			@GraphQLArgument(name = "id") UUID id
	) {
		return purchaseRequestItemRepository.getByPrId(id).sort{it.item.descLong}
	}
	
	@GraphQLQuery(name = "getPrItemsNotYetPo", description = "List of Purchase Request Items Not Yet Po")
	List<PurchaseRequest> getPrItemsNotYetPo() {
		return purchaseRequestItemRepository.getPrItemsNotYetPo().sort{it.prNo}.reverse(true)
	}

	@GraphQLQuery(name = "getPrItemsNotYetPoCS", description = "List of Purchase Request Items Not Yet Po")
	List<PurchaseRequest> getPrItemsNotYetPoCS(
			@GraphQLArgument(name = "consignment") Boolean consignment = false,
			@GraphQLArgument(name = "asset") Boolean asset = false
	) {
		return purchaseRequestItemRepository.getPrItemsNotYetPo(consignment, asset).sort{it.prNo}.reverse(true)
	}

	//mutation
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "removePrItem")
	PurchaseRequestItem removePrItem(
			@GraphQLArgument(name = "id") UUID id
	) {
		def obj = purchaseRequestItemRepository.findById(id).get()
		if(obj){
			purchaseRequestItemRepository.delete(obj)
		}
		return obj
	}

	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "updatePRQty")
	PurchaseRequestItem updatePRQty(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "qty") Integer qty
	) {
		def obj = purchaseRequestItemRepository.findById(id).get()
		obj.requestedQty = qty
		purchaseRequestItemRepository.save(obj)
	}

	@GraphQLQuery(name = "getPRItemsReport")
	List<PurchaseRequestItem> getPRItemsReport(@GraphQLArgument(name = "start") Instant start,
													 @GraphQLArgument(name = "end") Instant end,
													 @GraphQLArgument(name = "filter") String filter,
													 @GraphQLArgument(name = "supplier") UUID supplier) {

		Instant fromDate = start.atZone(ZoneId.systemDefault()).toInstant()
		Instant toDate = end.atZone(ZoneId.systemDefault()).toInstant()

		if(supplier){
			return purchaseRequestItemRepository.getPRItemByDateRangeSupplier(fromDate,toDate,filter,supplier)
		}else{
			return purchaseRequestItemRepository.getPRItemByDateRange(fromDate,toDate,filter)
		}

	}
	
}
