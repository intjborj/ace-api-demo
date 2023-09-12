package com.hisd3.hismk2.graphqlservices.doh.dto

import groovy.transform.builder.Builder

@Builder
class OpdConsultationDto {
   String icdCode
   String longName
   Double reportingYear
   Double total
   String icd10Cat
}

@Builder
class OpdConsultation2Dto {
   String description
   Integer number
   String icdCode
   String icdCategory
}
