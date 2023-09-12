package com.hisd3.hismk2.rest.dto

import com.hisd3.hismk2.domain.inventory.Markup
import groovy.transform.TupleConstructor

@TupleConstructor
class ItemPriceList {
	Markup markup
	BigDecimal vat
	BigDecimal sellingPrice
}
