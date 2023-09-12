package com.hisd3.hismk2.rest.dto

import com.hisd3.hismk2.domain.inventory.Item

class ReturnSupplierItemDto {
	String id
	Item item
	Integer returnQty
	BigDecimal returnUnitCost
	String return_remarks
	Boolean isPosted
	Boolean isNew
}
