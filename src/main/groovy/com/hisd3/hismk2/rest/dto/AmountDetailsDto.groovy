package com.hisd3.hismk2.rest.dto

import com.hisd3.hismk2.domain.billing.BillingItem
import groovy.transform.TupleConstructor

@TupleConstructor
class AmountDetailsDto {
	BillingItem billingItem
	BigDecimal discountAmount
}

@TupleConstructor
class ItemServiceDto {
	UUID id
	String desc
	String proccessCode
}


