package com.hisd3.hismk2.rest.dto

import groovy.transform.TupleConstructor

import java.time.Instant

@TupleConstructor
class SalesReportDetailedDto {
	UUID id
	Instant transaction_date
	String folio_no
	String billing_reference_no
	String barcode
	String item_code
	String service_code
	String description
	String department
	BigDecimal qty
	BigDecimal amount
	BigDecimal total_amount
}


