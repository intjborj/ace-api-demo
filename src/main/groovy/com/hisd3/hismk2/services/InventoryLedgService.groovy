package com.hisd3.hismk2.services

import com.hisd3.hismk2.domain.inventory.InventoryLedger
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.inventory.DocumentTypeRepository
import com.hisd3.hismk2.repository.inventory.InventoryLedgerRepository
import com.hisd3.hismk2.repository.inventory.ItemRepository
import com.hisd3.hismk2.rest.InventoryResource
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.time.Duration
import java.time.Instant

@Service
@TypeChecked
class InventoryLedgService {
	
	@Autowired
	DepartmentRepository departmentRepository
	
	@Autowired
	DocumentTypeRepository documentTypeRepository
	
	@Autowired
	ItemRepository itemRepository
	
	@Autowired
	InventoryResource inventoryResource
	
	@Autowired
	InventoryLedgerRepository inventoryLedgerRepository
	
	void InventoryCharge(UUID department, UUID itemId, String reference_no, String type, Integer qty, UUID billing, UUID billingItem) {
		String typeId = '19c0c388-7e85-4abf-aa13-cdafecf8dc8c'
		if (type.equalsIgnoreCase('rcs')) {
			typeId = '5776d7f2-6972-4980-a0ef-360642ee7572'
		} else if (type.equalsIgnoreCase('cs')) {
			typeId = '19c0c388-7e85-4abf-aa13-cdafecf8dc8c'
		} else if (type.equalsIgnoreCase('return')) {
			typeId = '0702930b-a1ec-4f64-be6a-7f656ac4c300'
		} else if(type.equalsIgnoreCase("dm")){
			typeId = '4818a0e9-de3f-4c44-a74c-a668af5f4f9a'
		}
		def inv = new InventoryLedger()
		Item item = itemRepository.findById(itemId).get()
		if (!item.gas || item.gas == null) {
			inv.sourceDep = departmentRepository.findById(department).get()
			inv.destinationDep = departmentRepository.findById(department).get()
			inv.documentTypes = documentTypeRepository.findById(UUID.fromString(typeId)).get()
			inv.item = item
			inv.referenceNo = reference_no
			inv.ledgerDate = Instant.now()
			inv.ledgerQtyIn = type.equalsIgnoreCase('rcs') || type.equalsIgnoreCase('return') ? qty : 0
			inv.ledgerQtyOut = type.equalsIgnoreCase('cs') || type.equalsIgnoreCase('dm') ? qty : 0
			inv.ledgerPhysical = 0
			inv.ledgerUnitCost = inventoryResource.getLastUnitPrice(itemId.toString())
			inv.isInclude = true
			inv.billing = billing
			inv.billingItem = billingItem
			inventoryLedgerRepository.save(inv)
		}
		
	}
	
	void InventoryProduction(String department, String itemId, String reference_no, String type, Integer qty, BigDecimal cost) {
		String typeId = '27d236bb-c023-44dc-beac-18ddfe1daf79'
		def inv = new InventoryLedger()
		inv.sourceDep = departmentRepository.findById(UUID.fromString(department)).get()
		inv.destinationDep = departmentRepository.findById(UUID.fromString(department)).get()
		inv.documentTypes = documentTypeRepository.findById(UUID.fromString(typeId)).get()
		inv.item = itemRepository.findById(UUID.fromString(itemId)).get()
		inv.referenceNo = reference_no
		inv.ledgerDate = Instant.now().plus(Duration.ofHours(8))
		inv.ledgerQtyIn = type.equalsIgnoreCase('output') ? qty : 0
		inv.ledgerQtyOut = type.equalsIgnoreCase('source') ? qty : 0
		inv.ledgerPhysical = 0
		inv.ledgerUnitCost = type.equalsIgnoreCase('source') ? inventoryResource.getLastUnitPrice(itemId) : cost
		inv.isInclude = true
		inventoryLedgerRepository.save(inv)
	}
	
}
