package com.hisd3.hismk2.domain.accounting.dto

import groovy.transform.Canonical

@Canonical
class PatientCaseDto {
    String id
    String caseNo
    String dischargedDatetime
    String admissionDatetime
    String registryType
    String createdDate
}
