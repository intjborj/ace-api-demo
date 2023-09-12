package com.hisd3.hismk2.graphqlservices.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.billing.Billing
import com.hisd3.hismk2.domain.inventory.StockRequest
import com.hisd3.hismk2.domain.inventory.StockRequestItem
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.Patient
import com.hisd3.hismk2.graphqlservices.billing.BillingService
import com.hisd3.hismk2.repository.inventory.StockRequestItemRepository
import com.hisd3.hismk2.repository.inventory.StockRequestRepository
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.repository.pms.PatientRepository
import com.hisd3.hismk2.rest.dto.CreditLimitDto
import com.hisd3.hismk2.rest.dto.StockRequestItemDto
import com.hisd3.hismk2.rest.dto.StockRequestStatusCountDto
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import com.hisd3.hismk2.services.NotificationService
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@GraphQLApi
@TypeChecked
class StockRequestService {

	@Autowired
	StockRequestRepository stockRequestRepository

	@Autowired
	CaseRepository caseRepository

	@Autowired
	private StockRequestItemRepository stockRequestItemRepository

	@Autowired
	private PatientRepository patientRepository

	@Autowired
	NotificationService notificationService

	@Autowired
	BillingService billingService

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	GeneratorService generatorService

	@GraphQLQuery(name = "stockrequests", description = "List of Stock Requests")
	List<StockRequest> allStockRequests(@GraphQLArgument(name = "patientId") String patientId) {
		return stockRequestRepository.findAll()
	}

	@GraphQLQuery(name = "getStockRequest", description = "List of ")
	StockRequest findById(@GraphQLArgument(name = "id") UUID id) {

		StockRequest stockRequest = stockRequestRepository.findById(id).get()
		return stockRequest
	}

	@GraphQLQuery(name = "getStockRequestItems", description = "List of ")
	List<StockRequestItem> getStockRequestItems(@GraphQLArgument(name = "id") UUID id) {
		List<StockRequestItem> stockRequestItemList = stockRequestItemRepository.getSRItemsBySRId(id)
		return stockRequestItemList
	}

	@GraphQLQuery(name = "isCreditLimitReached", description = "status of ")
	CreditLimitDto isCreditLimitReached(@GraphQLArgument(name = "id") UUID id) {
		StockRequest stockRequest = stockRequestRepository.getById(id)
		CreditLimitDto creditLimitDto = new CreditLimitDto()
		creditLimitDto.credit_limit_reached = billingService.isCreditLimitReached(billingService.activeBilling(stockRequest.patientCase)) ;
		if(creditLimitDto.credit_limit_reached)
		{
			List<StockRequestItem> items = stockRequestItemRepository.getSRItemsBySRId(id)
			for(StockRequestItem item in items)
			{
				if(item.billingItemNo==null || StringUtils.isEmpty(item.billingItemNo))
				{
					creditLimitDto.paid = false
					return creditLimitDto
				}
			}
			creditLimitDto.paid = true
		}
		return creditLimitDto
	}

	@GraphQLQuery(name = "getStockRequestsByPatientAndStatus", description = "List of Stock Requests with patient and status")
	List<StockRequest> getStockRequestsByPatientAndStatus(@GraphQLArgument(name = "status") String status, @GraphQLArgument(name = "patientId") String patientId) {
		return stockRequestRepository.getStockRequestsByPatientAndStatus(status, UUID.fromString(patientId))
	}

	@GraphQLQuery(name = "getStockRequestsByPatient", description = "List of Stock Requests with patient")
	List<StockRequest> getStockRequestsByPatient(@GraphQLArgument(name = "patientId") String patientId) {
		return stockRequestRepository.getStockRequestsByPatient(UUID.fromString(patientId))
	}

	@GraphQLQuery(name = "getStockRequestsByStatus", description = "List of Stock Requests with status")
	List<StockRequest> getStockRequestsByStatus(@GraphQLArgument(name = "status") String status) {
		return stockRequestRepository.getStockRequestsByStatus(status)
	}

	@GraphQLQuery(name = "searchStockRequest", description = "Search stock request")
	List<StockRequest> searchStockRequest(@GraphQLArgument(name = "search") String search, @GraphQLArgument(name = "status") String status) {
		return stockRequestRepository.searchStockRequest(search, status)
	}

	@GraphQLQuery(name = "getStockRequestCountWithStatus", description = "getStockRequestCountWithStatus")
	StockRequestStatusCountDto getStockRequestCountWithStatus() {
		StockRequestStatusCountDto dto = new StockRequestStatusCountDto()
		dto.REQUESTED = stockRequestRepository.getStockRequestsCountByStatus("REQUESTED")
		dto.CLAIMABLE = stockRequestRepository.getStockRequestsCountByStatus("CLAIMABLE")
		dto.SENT = stockRequestRepository.getStockRequestsCountByStatus("SENT")
		dto.CLAIMED = stockRequestRepository.getStockRequestsCountByStatus("CLAIMED")
		return dto
	}

	@GraphQLMutation
	StockRequest addRequest(
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		StockRequest stockRequest = objectMapper.convertValue(fields.get("stockRequest"), StockRequest)
		Case ptCase = caseRepository.getPatientActiveCase(stockRequest.patient.id)
		List<Billing> billing=	billingService.getActiveBillingByCase(ptCase).findAll({it.locked })

		if(billing.size()>0)
			throw new IllegalArgumentException("Folio already locked! Can't request more medicines!");

		stockRequest.status = 'REQUESTED'
		stockRequest.patientCase = ptCase
		stockRequest.stockRequestNo = generatorService?.getNextValue(GeneratorType.STOCK_REQUEST_NO, { i ->
			StringUtils.leftPad(i.toString(), 6, "0")
		})
		stockRequest = stockRequestRepository.save(stockRequest)
		List<StockRequestItemDto> stockRequestItems = fields.get("stockRequestItems") as ArrayList<StockRequestItemDto>

		stockRequestItems.each {
			it ->
				def insert = new StockRequestItem()
				def item = objectMapper.convertValue(it, StockRequestItemDto)
				insert.expectedBarcode = item.expectedBarcode
				insert.itemDescription = item.itemDescription
				insert.requestedQty = item.requestedQty
				if(item.item){
					insert.item = item.item
				}
				if(item.medication){
					insert.medication = item.medication
				}
				insert.stockRequest = stockRequest
				stockRequestItemRepository.save(insert)
		}
		Patient patient = patientRepository.getOne(stockRequest.patient.id)

		String notificationTitle = "Medication Stock Request # " + stockRequest.stockRequestNo
		String notificationMessage = "Patient : " + patient.fullName
		notificationService.notifyUsersOfDepartment(stockRequest.requestedDepartment.id, notificationTitle, notificationMessage, "")
		notificationService.notifyNewStockRequest(stockRequest)
		return stockRequest
	}
}
