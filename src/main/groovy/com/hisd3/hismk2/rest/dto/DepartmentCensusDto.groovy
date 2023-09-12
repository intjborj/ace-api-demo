package com.hisd3.hismk2.rest.dto

import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.Transfer
import groovy.transform.TupleConstructor

@TupleConstructor
class DepartmentCensusDto {
	String registry_type
	String entry_datetime
	String department
	String department_name
	String last_name
	String first_name
	String middle_name
}

class CaseDepartmentCensusDto {
	Department department
	List<Case> caseList
}

