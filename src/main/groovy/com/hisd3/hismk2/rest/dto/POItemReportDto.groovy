package com.hisd3.hismk2.rest.dto

class POItemReportDto {
	String description, uom, deals
	Integer no, request_qty
	BigDecimal unit_cost, total, discount
}

class POReportDto {
	String date, poNum, prNum, supplier, department, terms, fullname, title
}
