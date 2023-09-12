package com.hisd3.hismk2.services.eclaims.stash

import com.hisd3.hismk2.rest.dto.ComlogikItemDto
import groovy.transform.TupleConstructor

@TupleConstructor
class CaseRateDto {
	String description
	String icd
	String rvs
}

class IntegPatientDetailDto {
	String birthDate
	String firstName
	String lastName
	String middleName = ""
	String suffix = ""
	String pin = ""
	String type = ""
	String gender
}

class IntegPhilhMemnerDetailDto {
	String mem_birthDate
	String mem_firstName
	String mem_lastName
	String mem_middleName = ""
	String mem_suffix = ""
	String mem_pin = ""
	String mem_type = ""
	String mem_gender
}

class IntegEligibilityDto {
	String totalAmountActual
	String totalAmountClaimed
	String patientIs
	String isFinal
	String isDisabled
}

class DoctorAccreValidation {
	String doctorAccreCode
	String admissionDate
	String dischargeDate
	String showResult
}

class EcMedsCont {
	String id
	Object meds
}

class EcStructuredMeds {
	String id
	String drugcode
	String medicinedesc
	String route
	String freq
	String purchasedate
	String qty
	String price
}

class StashItemDto {
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
	Boolean medPnf

}



