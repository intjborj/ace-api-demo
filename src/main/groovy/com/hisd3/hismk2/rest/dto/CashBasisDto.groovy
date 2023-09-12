package com.hisd3.hismk2.rest.dto

import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.inventory.CashBasis
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.Patient

class CashBasisDto {
	Patient patient
	String cashBasisNo = null
	Case patientCase
	Department department
	String status = null
}

class CashBasisITemDto {
	String id
	CashBasis cashBasis
	String item
	String orNumber = null
	String sriNumber = null
	BigDecimal quantity = null
	BigDecimal price = null
	String type = null
}