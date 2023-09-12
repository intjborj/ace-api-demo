package com.hisd3.hismk2.domain.accounting.dto

import groovy.transform.Canonical

@Canonical
class BillingDto {
    String id
    String billingNo
    PatientDto patient
    PatientCaseDto patientCase
}
