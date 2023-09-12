package com.hisd3.hismk2.rest.dto

import com.hisd3.hismk2.domain.Department
import groovy.transform.TupleConstructor

@TupleConstructor
class ExpenseDetailsDto {
	String date_trans
	String reference_no
	Department sourceDep
	String itemCategory
	String expiryDate
	Integer onHand
	Integer physicalCount
	Integer variance
	BigDecimal unitCost
	BigDecimal totalCost
}


