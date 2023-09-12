package com.hisd3.hismk2.rest.dto

import groovy.transform.TupleConstructor

@TupleConstructor
class PatientBasicDto {
	String id,
	       pin,
	       firstname,
	       lastname,
	       middlename,
	       suffix,
	       age,
	       gender,
	       dob
}
