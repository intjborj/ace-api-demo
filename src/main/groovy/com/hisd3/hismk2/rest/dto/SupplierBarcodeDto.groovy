package com.hisd3.hismk2.rest.dto

import com.hisd3.hismk2.domain.inventory.Item
import groovy.transform.TupleConstructor

@TupleConstructor
class SupplierBarcodeDto {
	UUID id
	UUID item
	BigDecimal unitCost
}

@TupleConstructor
class SupplierBarcodeItemDto {
	UUID id
	Item item
	BigDecimal unitCost
}

@TupleConstructor
class IssuanceBarcodeDto {
	UUID id
	UUID item
	BigDecimal wcost
}

@TupleConstructor
class IssuanceBarcodeItemDto {
	UUID id
	Item item
	BigDecimal wcost
	String itemCategory
}

@TupleConstructor
class BillingBarcodeDto {
	UUID id
	Integer quantity
	SupplyDto supply
}

class SupplyDto {
	UUID id
	String sku
	String itemCode
	String descLong
	String brand
	BigDecimal reorderQty
	Boolean allowTrade
	BigDecimal calculatedAmount
}