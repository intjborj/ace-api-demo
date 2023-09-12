package com.hisd3.hismk2.graphqlservices.doh.dto

import groovy.transform.builder.Builder


@Builder
class ErVisitsDto {
    String icdCode
    String longName
    Double reportingYear
    Double total
    String icd10Cat
}
