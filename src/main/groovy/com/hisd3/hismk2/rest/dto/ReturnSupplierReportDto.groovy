package com.hisd3.hismk2.rest.dto

import groovy.transform.TupleConstructor

@TupleConstructor
class ReturnSuppItemReportDto {
	String stockCode, itemDesc, uom, reasonForReturn, srrNo
	Integer quantityReturn
}

@TupleConstructor
class ReturnSuppReportDto {
	String date, rts, supplierCode, supplierName, returnBy, returnDate, receivedBy, receivedDate
}
