package com.hisd3.hismk2.rest.dto

import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.accounting.AccountsPayable
import com.hisd3.hismk2.domain.accounting.Bank
import com.hisd3.hismk2.domain.accounting.Disbursement
import com.hisd3.hismk2.domain.accounting.ExpenseTransaction
import groovy.transform.TupleConstructor

import java.time.Instant

@TupleConstructor
class DisbursementDto {
	String id
	Bank bank
	String bankBranch
	String checkNo
	Instant checkDate
	BigDecimal amount
	Boolean isNew
}

class DisbursementApDto{
	UUID id
	AccountsPayable payable
	BigDecimal appliedAmount
	BigDecimal vatRate
	Boolean vatInclusive
	BigDecimal vatAmount
	String ewtDesc
	BigDecimal ewtRate
	BigDecimal ewtAmount
	BigDecimal grossAmount
	BigDecimal discount
	BigDecimal netAmount
	Boolean isNew
}

class DisbursementExpDto{
	String id
	ExpenseTransaction transType
	Department department
	BigDecimal amount
	String remarks
	Boolean	isNew
}

class DisbursementWtxDto {
	String id
	String ewtDesc
	BigDecimal ewtRate
	BigDecimal ewtAmount
	Boolean isNew
}


