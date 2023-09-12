package com.hisd3.hismk2.rest.dto

import com.hisd3.hismk2.domain.billing.PriceTierDetail
import com.hisd3.hismk2.domain.inventory.Item
import groovy.transform.TupleConstructor

@TupleConstructor
class ItemPriceControlDto {
	UUID id
	Item item
	PriceTierDetail tierDetail
	
	BigDecimal basePrice
	BigDecimal amountValue
	BigDecimal percentageValue
	
	//calculated fields
	BigDecimal addon
	BigDecimal margin
	BigDecimal totalMarkup
	Boolean locked
}
