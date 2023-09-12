package com.hisd3.hismk2.rest.dto

import groovy.transform.TupleConstructor

@TupleConstructor
class ComlogikItemDto {
	UUID id,genericId,refId
	String philhealthDrugCode,
			philhealthPNDFCode,
			philhealthGenericName,
			philhealthBrandName,
			philhealthPreparation,
			philhealthRoute,
			philhealthInstructionFrequency,
			philhealthActualUnitPrice,
			philhealthPurchaseDate,
			philhealthDrugCodePhic

	Integer philhealthQuantity
	Boolean isPatientMed

}
