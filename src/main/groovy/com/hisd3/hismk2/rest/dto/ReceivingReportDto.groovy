package com.hisd3.hismk2.rest.dto

import groovy.transform.TupleConstructor

@TupleConstructor
class ReceivingReportDto {
	String srrNo,
	       date,
	       poNo,
	       refNo,
	       supplier,
	       remarks,
			title
}

@TupleConstructor
class ReceivingReportItemDto {
	String item_code
	Integer uou_qty
	String uou_unit
	BigDecimal uop_qty
	String uop_unit
	String item_description
	String expiry
	BigDecimal unit_cost
	BigDecimal input_tax
	BigDecimal inventory
	BigDecimal total
}
