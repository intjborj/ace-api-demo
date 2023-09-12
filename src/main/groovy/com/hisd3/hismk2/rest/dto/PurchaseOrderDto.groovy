package com.hisd3.hismk2.rest.dto

import com.hisd3.hismk2.domain.inventory.Item

class PurchaseOrderDto {
	UUID refItemId
	String prNo
	String dept
	String itemCode
	String description
	Integer qty
	String pkg
	String pkg_price
	BigDecimal totalAmount
}

class PurchaseOrderItemsDto {
	String id
	Item item
	Integer quantity
	BigDecimal supplierLastPrice
	String prNos
	Boolean isNew
	Boolean noPr
	String type
	String type_text
}
