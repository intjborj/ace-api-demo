package com.hisd3.hismk2.rest.dto

import groovy.transform.TupleConstructor

@TupleConstructor
class Msgformat {
	String msgXML
	String senderIp
	String orderslipId
	String pId
	String jsonList
	String casenum
	String docEmpId
	String attachment
	String bacthnum
	String processCode
	String name
}
