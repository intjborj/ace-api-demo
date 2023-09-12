package com.hisd3.hismk2.domain.hrm.dto


import com.hisd3.hismk2.domain.hrm.enums.EmployeeAttendanceMethod

import java.time.Instant

interface EmployeeAttendanceDto {


    Instant getOriginalAttendanceTime()
    String getOriginalType()
    String getSource()
    Boolean getIsManual()
    String getEmployeeId()


//    Instant original_attendance_time
//    String originalType
//    String source
//    Boolean isManual
//    UUID employeeId
//
//    EmployeeAttendanceDto(Instant original_attendance_time, String originalType, String source, Boolean isManual, UUID employeeId) {
//        this.original_attendance_time = original_attendance_time
//        this.originalType = originalType
//        this.source = source
//        this.isManual = isManual
//        this.employeeId = employeeId
//    }
}
