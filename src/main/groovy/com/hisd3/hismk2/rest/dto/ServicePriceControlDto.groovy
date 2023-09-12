package com.hisd3.hismk2.rest.dto

import com.hisd3.hismk2.domain.ancillary.Service
import com.hisd3.hismk2.domain.billing.PriceTierDetail
import groovy.transform.TupleConstructor

@TupleConstructor
class ServicePriceControlDto {
	UUID id
	Service service
	PriceTierDetail tierDetail
	
	BigDecimal basePrice
	BigDecimal percentageValue
	BigDecimal amountValue
	
	//calculated fields
	BigDecimal addon
	BigDecimal margin
	BigDecimal totalMarkup
	Boolean locked
}
