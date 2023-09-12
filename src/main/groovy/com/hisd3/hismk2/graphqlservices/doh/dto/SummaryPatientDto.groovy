package com.hisd3.hismk2.graphqlservices.doh.dto

import groovy.transform.Canonical

@Canonical
class SummaryPatientDto {
    Integer key
    String name
    Long value
}

@Canonical
class SummaryPatientV2Dto {
    String totalinpatients
    String totalnewborn
    String totaldischarges
    String totalpad
    String totalibd
    String totalinpatienttransto
    String totalinpatienttransfrom
    String totalpatientsremaining
}
