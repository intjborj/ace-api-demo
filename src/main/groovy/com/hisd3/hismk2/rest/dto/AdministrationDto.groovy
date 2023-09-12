package com.hisd3.hismk2.rest.dto

import com.hisd3.hismk2.domain.hrm.Employee

import java.time.Instant

class AdministrationDto {
	
	UUID id
	
	String medicine
	
	String action
	
	String dose
	
	String remarks
	
	Instant entryDateTime

	String entryDateTimeText
	
	Employee employee
	
	Boolean patientOwn = false
	
	AdministrationDto(UUID id, String medicine, String action, String dose, String remarks, Instant entryDateTime, Employee employee, Boolean patientOwn) {
		this.id = id
		this.medicine = medicine
		this.action = action
		this.dose = dose
		this.remarks = remarks
		this.entryDateTime = entryDateTime
		this.employee = employee
		this.patientOwn = patientOwn
	}

	AdministrationDto(UUID id, String medicine, String action, String dose, String remarks, Instant entryDateTime, String entryDateTimeText, Employee employee, Boolean patientOwn) {
		this.id = id
		this.medicine = medicine
		this.action = action
		this.dose = dose
		this.remarks = remarks
		this.entryDateTime = entryDateTime
		this.employee = employee
		this.patientOwn = patientOwn
		this.entryDateTimeText = entryDateTimeText
	}
}
