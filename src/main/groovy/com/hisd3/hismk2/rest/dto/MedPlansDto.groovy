package com.hisd3.hismk2.rest.dto

import groovy.transform.TupleConstructor

@TupleConstructor
class MedPlansDto {
	String generic_name
	Integer quantity
	BigDecimal total_cost
}
