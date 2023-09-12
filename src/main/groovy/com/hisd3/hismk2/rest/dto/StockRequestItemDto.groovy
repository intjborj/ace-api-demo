package com.hisd3.hismk2.rest.dto

import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.pms.Medication
import groovy.transform.TupleConstructor

@TupleConstructor
class StockRequestItemDto {

	String expectedBarcode
	String itemDescription
	BigDecimal requestedQty
	Item item
	Medication medication



}
