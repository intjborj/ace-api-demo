package com.hisd3.hismk2.rest.dto

import com.hisd3.hismk2.domain.billing.PriceTierDetail
import groovy.transform.TupleConstructor

@TupleConstructor
class TierDTO {
	PriceTierDetail tierDetail
	BigDecimal markup
	BigDecimal calculatedValue
}