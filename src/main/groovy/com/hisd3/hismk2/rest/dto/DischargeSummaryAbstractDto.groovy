package com.hisd3.hismk2.rest.dto

import groovy.transform.TupleConstructor

@TupleConstructor
class DischargeSummaryAbstractDto {
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
		   specialInstructions,
		   dischargeDiagnosis,
		   procedurePerformed,
		   courseInTheWard,
		   dischargeDisposition,
		   medication,
		   logo,
		   aog,
		   generalSurvey,
	       g,
		   p,
	       abort,
	       chiefComplaint
}
