package com.hisd3.hismk2.rest.dto

import groovy.transform.TupleConstructor

import java.time.Instant

@TupleConstructor
class JournalEntryViewDto {
	String code
	String desc
	BigDecimal debit
	BigDecimal credit
}


