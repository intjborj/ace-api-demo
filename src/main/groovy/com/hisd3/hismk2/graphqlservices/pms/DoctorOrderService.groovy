package com.hisd3.hismk2.graphqlservices.pms

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.DoctorOrder
import com.hisd3.hismk2.domain.pms.DoctorOrderItem
import com.hisd3.hismk2.domain.pms.DoctorOrderProgressNote
import com.hisd3.hismk2.repository.pms.DoctorOrderItemRepository
import com.hisd3.hismk2.repository.pms.DoctorOrderProgressNoteRepository
import com.hisd3.hismk2.repository.pms.DoctorOrderRepository
import com.hisd3.hismk2.repository.pms.ManagingPhysicianRepository
import com.hisd3.hismk2.services.NotificationService
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLContext
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoField
import java.util.stream.Collector
import java.util.stream.Collectors

@TypeChecked
@Component
@GraphQLApi
class DoctorOrderService {
	
	@Autowired
	private DoctorOrderRepository doctorOrderRepository
	
	@Autowired
	private DoctorOrderProgressNoteRepository doctorOrderProgressNoteRepository
	
	@Autowired
	private DoctorOrderItemRepository doctorOrderItemRepository

	@Autowired
	NotificationService notificationService

	@Autowired
	ManagingPhysicianRepository managingPhysicianRepository
	
	@Autowired
	ObjectMapper objectMapper
	
	//============== All Queries ====================
	
	@GraphQLQuery(name = "doctorOrders", description = "Get all DoctorOrders")
	List<DoctorOrder> findAll() {
		return doctorOrderRepository.findAll().sort { it.entryDateTime }
	}
	
	@GraphQLQuery(name = "doctorOrder", description = "Get DoctorOrder By Id")
	DoctorOrder findById(@GraphQLArgument(name = "id") UUID id) {
		return doctorOrderRepository.findById(id).get()
	}
	
	@GraphQLQuery(name = "doctorOrderByCase", description = "Get doctor's orders by case")
	List<DoctorOrder> getDoctorOrdersByCase(@GraphQLArgument(name = "caseId") UUID caseId) {
		return doctorOrderRepository.getDoctorOrdersByCase(caseId)
	}
	
	@GraphQLQuery(name = "doctorOrdersWithSort", description = "Get doctor's orders by case with sort")
	List<DoctorOrder> getDoctorOrdersByCaseWithSort(@GraphQLArgument(name = "caseId") UUID caseId, @GraphQLArgument(name = "sort") String sort) {
		def sortDirection = sort == 'ASC' ? Sort.Direction.ASC : Sort.Direction.DESC
		List<DoctorOrder> orders = doctorOrderRepository.getDoctorOrdersByCase(caseId).sort {it.entryDateTime }
		return sort != "ASC" ? orders.reverse() : orders;
	}

	@GraphQLQuery(name = "doctorsOrdersPageable", description = "Get doctor's orders by case by page")
	Page<DoctorOrder> getDoctorsOrdersPageable(
			@GraphQLArgument(name = "caseId") UUID caseId,
			@GraphQLArgument(name = "sort") String sort,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "pageSize") Integer pageSize

	) {
		def sortDirection = sort == 'ASC' ? Sort.Direction.ASC : Sort.Direction.DESC
		return doctorOrderRepository.getDoctorOrdersByCasePageable(caseId, PageRequest.of(page, pageSize, Sort.by(sortDirection, 'entryDateTime')))
	}
	
	@GraphQLQuery(name = "doctorOrderProgressNotes", description = "Get all DoctorOrder DoctorOrderProgressNotes")
	List<DoctorOrderProgressNote> getDoctorOrderProgressNotesByDoctorOrder(@GraphQLContext DoctorOrder doctorOrder) {
		return doctorOrderProgressNoteRepository.getDoctorOrderProgressNotesByDoctorOrder(doctorOrder.id)
	}
	
	@GraphQLQuery(name = "doctorOrderItems", description = "Get all DoctorOrder DoctorOrderItems")
	List<DoctorOrderItem> getDoctorOrderItemsByDoctorOrder(@GraphQLContext DoctorOrder doctorOrder) {
		return doctorOrderItemRepository.getDoctorOrderItemsByDoctorOrder(doctorOrder.id)
	}

	
	@GraphQLMutation
	DoctorOrder addDoctorOrder(
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		DoctorOrder doctorOrder = doctorOrderRepository.save(objectMapper.convertValue(fields.get("doctorOrder"), DoctorOrder))
		
		DoctorOrderProgressNote doctorOrderProgressNote = objectMapper.convertValue(fields.get("doctorOrderProgressNote"), DoctorOrderProgressNote)
		if (doctorOrderProgressNote != null) {
			doctorOrderProgressNote.entryDateTime = doctorOrder.entryDateTime
			doctorOrderProgressNote.doctorOrder = doctorOrder
			doctorOrderProgressNoteRepository.save(doctorOrderProgressNote)
		}

		List<DoctorOrderItem> doctorOrderItems = fields.get("doctorOrderItems") as ArrayList<DoctorOrderItem>
		
		doctorOrderItems.each {
			it ->
				it = objectMapper.convertValue(it, DoctorOrderItem)
				it.doctorOrder = doctorOrder
				it.status = it.status != null ? it.status : "PENDING"
				it.entryDateTime = doctorOrder.entryDateTime.plusMillis(Instant.now().get(ChronoField.MILLI_OF_SECOND)).plusNanos(Instant.now().get(ChronoField.NANO_OF_SECOND))
				it.timesExecuted = 0
				doctorOrderItemRepository.save(it)
		}

		def managers = managingPhysicianRepository.getManagingPhysiciansByCase(doctorOrder.parentCase.id)

		List<Employee> employees = managers.stream().map {it -> it.employee }.collect(Collectors.toList())
		notificationService.notifyEmployees(employees, "Test Title", "Test Message")
		
		return doctorOrder
	}
	
	@GraphQLMutation
	DoctorOrder addDoctorOrderForFlutter(
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		DoctorOrder doctorOrder = doctorOrderRepository.save(
				new DoctorOrder().tap {
					Map<String, Object> doctorOrderMap = fields.get("doctorOrder") as Map<String, Object>
					entryDateTime = Timestamp.valueOf(doctorOrderMap.get("entryDateTime") as String).toInstant()
					orderingPhysician = objectMapper.convertValue(doctorOrderMap.get("orderingPhysician"), Employee)
					parentCase = objectMapper.convertValue(doctorOrderMap.get("parentCase"), Case)
				}
		)
		
		DoctorOrderProgressNote doctorOrderProgressNote = objectMapper.convertValue(fields.get("doctorOrderProgressNote"), DoctorOrderProgressNote)
		if (doctorOrderProgressNote != null) {
			doctorOrderProgressNote.entryDateTime = doctorOrder.entryDateTime
			doctorOrderProgressNote.doctorOrder = doctorOrder
			doctorOrderProgressNoteRepository.save(doctorOrderProgressNote)
		}
		
		List<DoctorOrderItem> doctorOrderItems = fields.get("doctorOrderItems") as ArrayList<DoctorOrderItem>
		
		doctorOrderItems.each {
			it ->
				it = objectMapper.convertValue(it, DoctorOrderItem)
				it.doctorOrder = doctorOrder
				it.status = it.status != null ? it.status : "PENDING"
				it.entryDateTime = doctorOrder.entryDateTime.plusMillis(Instant.now().get(ChronoField.MILLI_OF_SECOND)).plusNanos(Instant.now().get(ChronoField.NANO_OF_SECOND))
				it.timesExecuted = 0
				doctorOrderItemRepository.save(it)
		}
		
		return doctorOrder
	}
	
	@GraphQLMutation
	DoctorOrder ignoreOrder(@GraphQLArgument(name = "orderId") String orderId) {
		DoctorOrder doctorOrder = doctorOrderRepository.findById(UUID.fromString(orderId)).get()
		doctorOrder.hidden = Instant.now()
		return doctorOrderRepository.save(doctorOrder)
	}
}
