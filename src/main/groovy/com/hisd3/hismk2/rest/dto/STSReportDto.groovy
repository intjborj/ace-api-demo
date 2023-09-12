package com.hisd3.hismk2.rest.dto

class STSReportDto {
	String date, stsNo, issuing_dep, receiving_dep, ref_no, issuedBy, receivedBy
}

class STSReportItemDto {
	String code, description, uom
	Integer  request, issued
	BigDecimal unitCost, total
}
