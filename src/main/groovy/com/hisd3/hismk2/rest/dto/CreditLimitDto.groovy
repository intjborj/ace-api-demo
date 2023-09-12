package com.hisd3.hismk2.rest.dto

import com.hisd3.hismk2.domain.hrm.Employee

import java.time.Instant

class CreditLimitDto {

	UUID billingId

	Double credit_limit

	Boolean credit_limit_reached = false

	Boolean paid = false


}
