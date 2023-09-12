package com.hisd3.hismk2.graphqlservices.doh.dto
import groovy.transform.builder.Builder

@Builder
class DeliveriesDto {
    String infacility, caesarian, vaginal, other
}
