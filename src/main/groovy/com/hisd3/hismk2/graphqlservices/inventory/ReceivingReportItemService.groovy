package com.hisd3.hismk2.graphqlservices.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.inventory.ReceivingReport
import com.hisd3.hismk2.domain.inventory.ReceivingReportItem
import com.hisd3.hismk2.repository.inventory.ReceivingReportItemRepository
import com.hisd3.hismk2.repository.inventory.ReceivingReportRepository
import com.hisd3.hismk2.rest.dto.QueryErrorException
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@TypeChecked
@Component
@GraphQLApi
class ReceivingReportItemService {
	
	@Autowired
	ReceivingReportRepository receivingReportRepository
	
	@Autowired
	ReceivingReportItemRepository receivingReportItemRepository
	
	@Autowired
	ObjectMapper objectMapper
	
	@GraphQLQuery(name = "receivingReportItemLists", description = "get receiving report items")
	List<ReceivingReportItem> findItemsByReceivingReportId(@GraphQLArgument(name = "id") UUID id) {
		return receivingReportItemRepository.findItemsByReceivingReportId(id).sort { it.item.descLong }
	}

	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "updateStatusSRRItems", description = "update SRR items")
	ReceivingReportItem updateStatusSRRItems(
			@GraphQLArgument(name = "receivingId") UUID receivingId
	) {
		ReceivingReportItem items = new ReceivingReportItem();
		def list = receivingReportItemRepository.findItemsByReceivingReportId(receivingId).sort { it.item.descLong }
		list.each {
			it->
				items = it
				items.isPosted = false
				receivingReportItemRepository.save(items)
		}
		return items
	}

	//code ni dons
	@GraphQLQuery(name = "getSrrItemByDateRange", description = "List of receiving report list per date range")
	List<ReceivingReportItem> getSrrItemPerDateRange(@GraphQLArgument(name = "start") Instant start,
												 @GraphQLArgument(name = "end") Instant end,
												 @GraphQLArgument(name = "filter") String filter,
													 @GraphQLArgument(name = "supplier") UUID supplier) {

		Instant fromDate = start.atZone(ZoneId.systemDefault()).toInstant()
		Instant toDate = end.atZone(ZoneId.systemDefault()).toInstant()

		if(supplier){
			return receivingReportItemRepository.getSrrItemByDateRangeSupplier(fromDate,toDate,filter,supplier)
		}else{
			return receivingReportItemRepository.getSrrItemByDateRange(fromDate,toDate,filter)
		}

	}

	//removeitem
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "removeSRRItem")
	ReceivingReportItem removeSRRItem(
			@GraphQLArgument(name = "id") UUID id
	) {
		def del = receivingReportItemRepository.findById(id).get()
		receivingReportItemRepository.delete(del)
	}

	//code ni wilson
	@GraphQLQuery(name = "getItemsWithExpiry")
	Page<ReceivingReportItem> getItemsWithExpiry(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "depId") UUID depId,
			@GraphQLArgument(name = "status") String status = "HEALTHY",
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "pageSize") Integer pageSize
	) {
		def now = Instant.now()
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault())
		String dateParam = dateFormat.format(now)
		if(status.equalsIgnoreCase("EXPIRED")){
			receivingReportItemRepository.getItemsWithExpired(depId, dateParam,filter, new PageRequest(page, pageSize, Sort.Direction.DESC, "expirationDate"))
		}else{
			receivingReportItemRepository.getItemsWithExpiry(depId, dateParam,filter, new PageRequest(page, pageSize, Sort.Direction.ASC, "expirationDate"))
		}

	}

	@GraphQLQuery( name = "getReceivingReportItem")
	Page<ReceivingReportItem>getReceivingReportItem(
			@GraphQLArgument( name = "id") UUID id,
			@GraphQLArgument( name = "filter") String filter,
			@GraphQLArgument( name = "page") Integer page,
			@GraphQLArgument( name = "pageSize") Integer pageSize
	){
		return receivingReportItemRepository.getReceivingReportItem(id, filter, new PageRequest(page, pageSize))
	}
	
}
