package com.hisd3.hismk2.rest.dto

@groovy.transform.builder.Builder
import java.time.Instant

class LeadingErConsult {
	String hfhucode
	String erconsultaions
	String number
	String icdCode
	String icdCategory
	Instant reportingyear
	
}
