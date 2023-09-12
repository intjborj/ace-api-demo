package com.hisd3.hismk2.rest.dto

import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.accounting.ExpenseTransaction
import com.hisd3.hismk2.domain.inventory.Item
import groovy.transform.TupleConstructor

@TupleConstructor
class DmDetailsDto{
	String id
	ExpenseTransaction transType
	Department department
	String type
	BigDecimal percent
	BigDecimal amount
	String remarks
	Boolean	isNew
}


