package com.hisd3.hismk2.graphqlservices.doh.dto
import groovy.transform.builder.Builder

@Builder
class OpdMinorOpDto {
    String proccode
    String icdCode
    String longName
    Integer total
}
