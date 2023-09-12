package com.hisd3.hismk2.graphqlservices.bundy

import groovy.transform.Canonical

import java.time.Instant

class AttLogDto {
    String date,
            idno,
            name,
            time,
            status,
            verification,
            employeeId,
            deviceName,
            department,
            departmentId
    Instant dateTime

    Instant getDateTime() {
        return dateTime
    }
}
