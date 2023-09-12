package com.hisd3.hismk2.rest.dto

import groovy.transform.TupleConstructor

@TupleConstructor
class ReceivingAmountDto {
	BigDecimal grossAmount
	BigDecimal totalDiscount
	BigDecimal netDiscount
	BigDecimal inputTax
	BigDecimal netAmount
	BigDecimal amount
}
