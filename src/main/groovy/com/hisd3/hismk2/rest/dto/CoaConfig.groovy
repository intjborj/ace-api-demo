package com.hisd3.hismk2.rest.dto

import groovy.transform.TupleConstructor

@TupleConstructor
class CoaConfig {
    Boolean show
    Boolean showDepartments
    List<UUID> motherAccounts = null
}
