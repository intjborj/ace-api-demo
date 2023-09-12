package com.hisd3.hismk2.graphqlservices.inventory

import com.hisd3.hismk2.dao.inventory.StockRequestItemDao
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.inventory.ReturnMedication
import com.hisd3.hismk2.domain.inventory.ReturnMedicationItem
import com.hisd3.hismk2.domain.inventory.StockRequestItem
import com.hisd3.hismk2.domain.pms.Medication
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.inventory.ReturnMedicationItemRepository
import com.hisd3.hismk2.repository.inventory.ReturnMedicationRepository
import com.hisd3.hismk2.repository.inventory.StockRequestItemRepository
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.repository.pms.MedicationRepository
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.InventoryLedgService
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import java.time.Instant

@Component
@GraphQLApi
@TypeChecked
class StockRequestItemService {
	
	@Autowired
	StockRequestItemRepository stockRequestItemRepository
	
	@Autowired
	EmployeeRepository employeeRepository
	
	@Autowired
	CaseRepository caseRepository
	
	@Autowired
	MedicationRepository medicationRepository
	
	@Autowired
	ReturnMedicationRepository returnMedicationRepository
	
	@Autowired
	ReturnMedicationItemRepository returnMedicationItemRepository
	
	@Autowired
	StockRequestItemDao stockRequestItemDao
	
	@Autowired
	InventoryLedgService inventoryLedgService
	
	@GraphQLQuery(name = "getSRItemsBySRId", description = "List of SR Items by SR Id")
	List<StockRequestItem> getSRItemsBySRId(@GraphQLArgument(name = "srId") String srId) {
		return stockRequestItemRepository.getSRItemsBySRId(UUID.fromString(srId))
	}
	
	@GraphQLMutation
	List<StockRequestItem> upsertStockRequestItems(
			@GraphQLArgument(name = "stockRequestId") String stockRequestId,
			@GraphQLArgument(name = "stockRequestItems") List<Map<String, Object>> stockRequestItems) {
		
		return stockRequestItemDao.saveStockRequestItem(stockRequestId, stockRequestItems)
	}
	
	@Transactional
	@GraphQLMutation
	ReturnMedication returnMedication(
			@GraphQLArgument(name = "_case") Map<String, Object> _case,
			@GraphQLArgument(name = "items") List<Map<String, Object>> items,
			@GraphQLArgument(name = "returnedById") UUID returnedById
	) {
		def patient_case = caseRepository.getOne(UUID.fromString(_case.get("id") as String))
		def returnedBy = employeeRepository.getOne(returnedById)
		def receivedBy = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()
		ReturnMedication returnMedication = new ReturnMedication()
		returnMedication.patient = patient_case.patient
		returnMedication.receivedBy = receivedBy
		returnMedication.returnedBy = returnedBy
		returnMedication.case_ = patient_case
		returnMedication.returnedDate = Instant.now()
		returnMedication = returnMedicationRepository.save(returnMedication)
		
		for (Map<String, Object> med : items) {
			Integer currentReturningQty = med.get("qtyReturned") as Integer
			Medication medication = medicationRepository.getOne(UUID.fromString(med.get("id") as String))
			Item item = medication.medicine
			
			ReturnMedicationItem returnMedicationItem = new ReturnMedicationItem()
			returnMedicationItem.medicine = item
			returnMedicationItem.quantity_returned = currentReturningQty
			returnMedicationItem.returnMedication = returnMedication
			returnMedicationItem = returnMedicationItemRepository.save(returnMedicationItem)
			
			// RETURN TO INVENTORY CODE INSIDE
			inventoryLedgService.InventoryCharge(receivedBy.departmentOfDuty.id,
					item.id,
					patient_case.caseNo,
					"return",
					currentReturningQty, null, null)
			// RETURN TO INVENTORY CODE INSIDE
			
			// REVERSE PATIENT BILL HERE INSIDE
			//
			// REVERSE PATIENT BILL HERE INSIDE
		}
		returnMedication
	}
}
