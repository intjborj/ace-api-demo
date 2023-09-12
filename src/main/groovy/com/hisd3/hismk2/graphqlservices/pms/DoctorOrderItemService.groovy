package com.hisd3.hismk2.graphqlservices.pms

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.pms.DoctorOrder
import com.hisd3.hismk2.domain.pms.DoctorOrderItem
import com.hisd3.hismk2.domain.pms.Medication
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.ancillary.OrderSlipItemRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.pms.DoctorOrderItemRepository
import com.hisd3.hismk2.repository.pms.DoctorOrderRepository
import com.hisd3.hismk2.repository.pms.MedicationRepository
import com.hisd3.hismk2.security.SecurityUtils
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.time.Instant

@TypeChecked
@Component
@GraphQLApi
class DoctorOrderItemService {
	
	@Autowired
	private DoctorOrderItemRepository doctorOrderItemRepository
	
	@Autowired
	private DoctorOrderRepository doctorOrderRepository

	@Autowired
	private MedicationRepository medicationRepository

	@Autowired
	private OrderSlipItemRepository orderSlipItemRepository

	@Autowired
	private EmployeeRepository employeeRepository
	
	@Autowired
	ObjectMapper objectMapper
	
	//============== All Queries ====================
	
	@GraphQLQuery(name = "doctorOrderItem", description = "Get DoctorOrderItem By Id")
	DoctorOrderItem findById(@GraphQLArgument(name = "id") UUID id) {
		return doctorOrderItemRepository.findById(id).get()
	}
	
	@GraphQLQuery(name = "procedureDoctorOrderItems", description = "Get all DoctorOrder DoctorOrderItems")
	List<DoctorOrderItem> getProcedureDoctorOrderItemsByDoctorOrder(@GraphQLArgument(name = "doctorOrderId") UUID doctorOrderId) {
		return doctorOrderItemRepository.getProcedureDoctorOrderItemsByDoctorOrder(doctorOrderId).sort { it.entryDateTime }
	}

	@GraphQLMutation
	def discontinueOrderItem(@GraphQLArgument(name = "id") UUID id) {

		def currentTimestamp = Instant.now()

		def doOrderItem = doctorOrderItemRepository.findById(id).get();
		def orderSlipItem = orderSlipItemRepository.getOrderSlipItemByDOItem(id);

		if(orderSlipItem) {
			orderSlipItem.discontinuedDatetime = currentTimestamp
			orderSlipItem.discontinuedBy = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first();
			orderSlipItemRepository.save(orderSlipItem);
		}

		doOrderItem.status = "DISCONTINUED";
		doOrderItem.discontinuedDatetime = currentTimestamp;

		doctorOrderItemRepository.save(doOrderItem);

		return new GraphQLRetVal<String>("Successfully discontinued", true)
	}
	
	@GraphQLMutation
	def updateOrderItem(
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		DoctorOrderItem doctorOrderItem = doctorOrderItemRepository.findById(UUID.fromString(fields.get("id").toString())).get()
		return doctorOrderItemRepository.save(objectMapper.updateValue(doctorOrderItem, fields))
	}
	
	@GraphQLMutation
	def ignoreOrderItem(@GraphQLArgument(name = "orderItemId") String orderItemId) {
		DoctorOrderItem doctorOrderItem = doctorOrderItemRepository.findById(UUID.fromString(orderItemId)).get()
		doctorOrderItem.hidden = Instant.now()
		
		List<DoctorOrderItem> doList = doctorOrderItemRepository.getDoctorOrderItemsByDoctorOrder(doctorOrderItem.doctorOrder.id)
		
		if (doList.size() == 1) {
			DoctorOrder doctorOrder = doctorOrderRepository.findById(doctorOrderItem.doctorOrder.id).get()
			doctorOrder.hidden = Instant.now()
			doctorOrderRepository.save(doctorOrder)
		}
		
		return doctorOrderItemRepository.save(doctorOrderItem)
	}
}
