package com.hisd3.hismk2.rest.dto

import groovy.transform.TupleConstructor

import java.time.Instant

@TupleConstructor
class RequisitionDto {
	String description
	BigDecimal price, subTotal
	Integer qty
	String requesting
	String itemNo
	String dateTime
}
