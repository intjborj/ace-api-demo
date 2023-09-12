package com.hisd3.hismk2.graphqlservices.doh.dto

import groovy.transform.builder.Builder

@Builder
class MajorOpDto {
    String proccode
    String longName
    Integer total
}
