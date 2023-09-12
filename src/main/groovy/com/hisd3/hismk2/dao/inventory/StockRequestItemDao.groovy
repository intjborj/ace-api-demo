package com.hisd3.hismk2.dao.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.inventory.StockRequest
import com.hisd3.hismk2.domain.inventory.StockRequestItem
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.inventory.ItemRepository
import com.hisd3.hismk2.repository.inventory.StockRequestItemRepository
import com.hisd3.hismk2.repository.inventory.StockRequestRepository
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@TypeChecked
@Service
@Transactional
class StockRequestItemDao {
	
	@Autowired
	EmployeeRepository employeeRepository
	
	@Autowired
	StockRequestItemRepository stockRequestItemRepository
	
	@Autowired
	StockRequestRepository stockRequestRepository
	
	@Autowired
	ItemRepository itemRepository
	
	@Autowired
	ObjectMapper objectMapper
	
	List<StockRequestItem> saveStockRequestItem(String stockRequestNo, List<Map<String, Object>> stockRequestItems) {
		
		List<StockRequestItem> sriList = []
		
		stockRequestItems.each {
			Map<String, Object> srItem ->
				
				StockRequest stockRequest = stockRequestRepository.stockRequestBySRNo(stockRequestNo)
				StockRequestItem stockRequestItem = new StockRequestItem()
				
				def sentKey = srItem['id'].toString()
				
				Item item = itemRepository.findById(UUID.fromString(sentKey)).get()
				
				stockRequestItem.stockRequest = stockRequest
				stockRequestItem.item = item
				stockRequestItem.itemDescription = item.descLong
				stockRequestItem.expectedBarcode = item.barcode
				stockRequestItem.requestedQty = srItem['qty'] as Integer
				stockRequestItem.billedToPatient = false
				stockRequestItem.deductedToInventory = 0
				stockRequestItem.preparedQty = 0
				stockRequestItem.orderedBy = employeeRepository.findById(UUID.fromString(srItem['ordered_by'] as String)).get()
				
				stockRequestItemRepository.save(stockRequestItem)
				
				sriList.push(stockRequestItem)
		}
		
		return sriList
	}
}