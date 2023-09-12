package com.hisd3.hismk2.rest.ap

import groovy.transform.TupleConstructor

@TupleConstructor
class HeaderDto {
	String supplier
	String refNo
	String date
	String employee
	String particulars
}

@TupleConstructor
class ApvItemsDto {
	String date
	String refNo
	String description
	BigDecimal ewt
	BigDecimal vat
	BigDecimal amount
	BigDecimal origAmount
}

@TupleConstructor
class JEntriesDto {
	String date
	String docNo
	String actCode
	String actName
	BigDecimal debit
	BigDecimal credit
}

@TupleConstructor
class BankDto {
	String date
	String checkNo
	String bank
	String branch
	BigDecimal amount
}

@TupleConstructor
class PrintCheckDto {
	String date
	String payee
	String amountWords
	String amount
}


