package com.hisd3.hismk2.rest.dto

import groovy.transform.TupleConstructor

import java.time.Instant

@TupleConstructor
class ApLedgerDto {
	UUID id
	UUID supplier
	String supplier_fullname
	String ledger_type
	Instant ledger_date
	String ref_no
	UUID ref_id
	BigDecimal debit
	BigDecimal credit
	BigDecimal running_balance
	BigDecimal out_balance
	Boolean is_include
	BigDecimal beg_balance
}

class ApAgingSummaryDto {
	UUID id
	String supplier
	String supplier_type_id
	String supplier_type
	BigDecimal current_amount
	BigDecimal day_1_to_31
	BigDecimal day_31_to_60
	BigDecimal day_61_to_90
	BigDecimal day_91_to_120
	BigDecimal older
	BigDecimal total
}

class ApAgingDetailedDto {
	UUID id
	UUID supplier_id
	UUID supplier_type_id
	String ap_no
	String supplier
	String supplier_type
	String ap_category
	String invoice_date
	String apv_date
	String due_date
	String invoice_no
	String remarks_notes
	Boolean posted
	BigDecimal current_amount
	BigDecimal day_1_to_31
	BigDecimal day_31_to_60
	BigDecimal day_61_to_90
	BigDecimal day_91_to_120
	BigDecimal older
	BigDecimal total
}


