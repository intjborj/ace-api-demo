package com.hisd3.hismk2.graphqlservices.versioning

import groovy.transform.Canonical

import java.time.Instant

@Canonical
class Hisd3EntityVersionInfoDetail{
    String property
    Object newValue
}

@Canonical
class Hisd3EntityVersionInfoHistory {
    Instant modifiedDateTime
    String author
    List<Hisd3EntityVersionInfoDetail> details = []
}
