package com.hisd3.hismk2.dao.inventory

import com.hisd3.hismk2.domain.inventory.PurchaseRequestItem
import com.hisd3.hismk2.repository.inventory.PurchaseRequestItemRepository
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

@TypeChecked
@Service
@Transactional
class PurchaseRequestDao {
	
	@Autowired
	PurchaseRequestItemRepository purchaseRequestItemRepository
	
	List<PurchaseRequestItem> getpRItems(UUID refPr) {
		purchaseRequestItemRepository.getByPrId(refPr)
	}
}
