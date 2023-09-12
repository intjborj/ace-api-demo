package com.hisd3.hismk2.graphqlservices.doh.dto

import groovy.transform.Canonical
import groovy.transform.builder.Builder

@Builder
class DohTestingDetailsDTo {
    String code
    String description
    Integer number
    String groupCode
}

@Canonical
class DohTestingDto {
    List<DohTestingDetailsDTo> imaging = []
    List<DohTestingDetailsDTo> laboratoryAndDiagnostic = []
}