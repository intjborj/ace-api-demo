package com.hisd3.hismk2.rest.dto

import groovy.transform.TupleConstructor

@TupleConstructor
class DoctorsOrderDtoV2 {
	String entryDateTime
	ArrayList<DoctorsOrderItemDto> items
	ArrayList<DoctorsOrderNotesDto> notes
}

@TupleConstructor
class DoctorsOrderItemDto {
	String entryDateTime
	String order
}

@TupleConstructor
class DoctorsOrderNotesDto {
	String entryDateTime
	String note
}
