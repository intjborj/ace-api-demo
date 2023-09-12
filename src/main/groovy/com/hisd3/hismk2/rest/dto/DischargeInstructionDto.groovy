package com.hisd3.hismk2.rest.dto

import groovy.transform.TupleConstructor

@TupleConstructor
class DischargeInstructionDto {
	String date,
	       pin,
	       caseNo,
	       roomNo,
	       patientFullName,
	       age,
	       gender,
	       civilStatus,
	       dob,
	       address,
	       attendingPhysician,
	       licenseNo,
	       dateAdmitted,
	       dateDischarged,
	       followUpDate,
	       nurseName,
	       admittingDiagnosis,
	       preparedByFullName,
		   specialInstructions
}
