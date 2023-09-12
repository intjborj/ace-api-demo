package com.hisd3.hismk2.rest.dto

import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.accounting.ExpenseTransaction
import com.hisd3.hismk2.domain.inventory.Item
import groovy.transform.TupleConstructor

@TupleConstructor
class PCVItemsDto {
	String id
	Item item
	Department department
	Integer qty
	BigDecimal unitCost
	BigDecimal discRate
	BigDecimal discAmount
	BigDecimal netAmount
	Boolean isVat
	BigDecimal vatAmount
	Boolean	isNew
}

class PCVOthersDto{
	String id
	ExpenseTransaction transType
	Department department
	BigDecimal amount
	String remarks
	Boolean	isNew
}


