package com.hisd3.hismk2.rest.dto

import groovy.transform.TupleConstructor

@TupleConstructor
class PayablePFDto {
	String refNo
	String reference
	UUID supplier
	BigDecimal totalPf
}


