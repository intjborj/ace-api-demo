package com.hisd3.hismk2.rest.dto

import groovy.transform.TupleConstructor

@TupleConstructor
class NurseNotesDto {
	String dateTime, focus, data, action, response, employee
}
