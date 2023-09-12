package com.hisd3.hismk2.graphqlservices.doh.dto

import groovy.transform.builder.Builder


@Builder
class ErConsultationDto {
    String icdCode
    String longName
    Double reportingYear
    Double total
    String icd10Cat
}


@Builder
class ErdConsultation2Dto {
    String description
    Integer number
    String icdCode
    String icdCategory
}
