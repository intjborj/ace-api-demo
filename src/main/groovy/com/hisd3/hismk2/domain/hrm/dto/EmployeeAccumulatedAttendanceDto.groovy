package com.hisd3.hismk2.domain.hrm.dto


import java.time.Instant

class EmployeeAccumulatedAttendanceDto {
    Instant date
    Instant scheduleStart
    Instant scheduleEnd
    String scheduleTitle
    Instant inTime
    Instant outTime
    String message
    boolean isError
    Boolean isRestDay = false
    Boolean withNSD = true
    Boolean isLeave = false
    Long countRestDay = 0


    //==========================UNDER PERFORMANCE=========================\\
    BigDecimal late = 0
    BigDecimal undertime = 0
    BigDecimal hoursAbsent = 0
    Long countLate = 0
    Long countUndertime = 0
    Long countAbsent = 0
    //==========================UNDER PERFORMANCE=========================\\

    //==============================WORKDAY==============================\\
    BigDecimal worked = 0
    BigDecimal hoursRegularOvertime = 0
    BigDecimal hoursWorkedNSD = 0
    Long countWorked = 0
    Long countOvertime = 0
    Long countWorkedNSD = 0
    //==============================WORKDAY==============================\\

    //==============================OIC-WORKDAY==============================\\
    BigDecimal workedOIC = 0
    BigDecimal hoursRegularOICOvertime = 0
    BigDecimal hoursWorkedOICNSD = 0
    Long countWorkedOIC = 0
    Long countOICOvertime = 0
    Long countWorkedOICNSD = 0
    //==============================OIC-WORKDAY==============================\\

    //==============================REST DAY==============================\\
    BigDecimal hoursRestDay = 0
    BigDecimal hoursRestOvertime = 0
    BigDecimal hoursRestDayNSD = 0
    Long countRestDayWorked = 0
    Long countRestOvertime = 0
    Long countRestDayNSD = 0
    //==============================REST DAY==============================\\

    //===========================SPECIAL HOLIDAY===========================\\
    BigDecimal hoursSpecialHoliday = 0
    BigDecimal hoursSpecialHolidayOvertime = 0
    BigDecimal hoursSpecialHolidayNSD = 0
    Long countSpecialHoliday = 0
    Long countSpecialHolidayOvertime = 0
    Long countSpecialHolidayNSD = 0
    //===========================SPECIAL HOLIDAY===========================\\

    //===========================SPECIAL HOLIDAY OIC===========================\\
    BigDecimal hoursSpecialHolidayOIC = 0
    BigDecimal hoursSpecialHolidayOICOvertime = 0
    BigDecimal hoursSpecialHolidayOICNSD = 0
    Long countSpecialHolidayOIC = 0
    Long countSpecialHolidayOICOvertime = 0
    Long countSpecialHolidayOICNSD = 0
    //===========================SPECIAL HOLIDAY OIC===========================\\

    //====================SPECIAL HOLIDAY AND REST DAY=====================\\
    BigDecimal hoursSpecialHolidayAndRestDay = 0
    BigDecimal hoursSpecialHolidayAndRestDayOvertime = 0
    BigDecimal hoursSpecialHolidayAndRestDayNSD = 0
    Long countSpecialHolidayAndRestDay = 0
    Long countSpecialHolidayAndRestDayOvertime = 0
    Long countSpecialHolidayAndRestDayNSD = 0
    //====================SPECIAL HOLIDAY AND REST DAY=====================\\

    //====================REGULAR HOLIDAY=====================\\
    BigDecimal hoursRegularHoliday = 0
    BigDecimal hoursRegularHolidayOvertime = 0
    BigDecimal hoursRegularHolidayNSD = 0
    Long countRegularHoliday = 0
    Long countRegularHolidayOvertime = 0
    Long countRegularHolidayNSD = 0
    //====================REGULAR HOLIDAY=====================\\

    //====================REGULAR HOLIDAY OIC=====================\\
    BigDecimal hoursRegularHolidayOIC = 0
    BigDecimal hoursRegularHolidayOICOvertime = 0
    BigDecimal hoursRegularHolidayOICNSD = 0
    Long countRegularHolidayOIC = 0
    Long countRegularHolidayOICOvertime = 0
    Long countRegularHolidayOICNSD = 0
    //====================REGULAR HOLIDAY OIC=====================\\

    //==============REGULAR HOLIDAY AND REST DAY===============\\
    BigDecimal hoursRegularHolidayAndRestDay = 0
    BigDecimal hoursRegularHolidayAndRestDayOvertime = 0
    BigDecimal hoursRegularHolidayAndRestDayNSD = 0
    Long countRegularHolidayAndRestDay = 0
    Long countRegularHolidayAndRestDayOvertime = 0
    Long countRegularHolidayAndRestDayNSD = 0
    //==============REGULAR HOLIDAY AND REST DAY===============\\

    //=====================DOUBLE HOLIDAY======================\\
    BigDecimal hoursDoubleHoliday = 0
    BigDecimal hoursDoubleHolidayOvertime = 0
    BigDecimal hoursDoubleHolidayNSD = 0
    Long countDoubleHoliday = 0
    Long countDoubleHolidayOvertime = 0
    Long countDoubleHolidayNSD = 0
    //=====================DOUBLE HOLIDAY======================\\

    //=====================DOUBLE HOLIDAY OIC======================\\
    BigDecimal hoursDoubleHolidayOIC = 0
    BigDecimal hoursDoubleHolidayOICOvertime = 0
    BigDecimal hoursDoubleHolidayOICNSD = 0
    Long countDoubleHolidayOIC = 0
    Long countDoubleHolidayOICOvertime = 0
    Long countDoubleHolidayOICNSD = 0
    //=====================DOUBLE HOLIDAY OIC======================\\

    //===============DOUBLE HOLIDAY AND REST DAY================\\
    BigDecimal hoursDoubleHolidayAndRestDay = 0
    BigDecimal hoursDoubleHolidayAndRestDayOvertime = 0
    BigDecimal hoursDoubleHolidayAndRestDayNSD = 0
    Long countDoubleHolidayAndRestDay = 0
    Long countDoubleHolidayAndRestDayOvertime = 0
    Long countDoubleHolidayAndRestDayNSD = 0
    //===============DOUBLE HOLIDAY AND REST DAY================\\

    BigDecimal hoursNightDifferential = 0
    BigDecimal hoursNightDifferentialOvertime = 0


    //===========================TOTAL============================\\

    BigDecimal getTotalWorkingHours() {
        return (
                worked +
                        hoursRegularOvertime +
                        hoursWorkedNSD +
                        workedOIC +
                        hoursRegularOICOvertime +
                        hoursWorkedOICNSD +
                        hoursRestDay +
                        hoursRestOvertime +
                        hoursRestDayNSD +
                        hoursSpecialHoliday +
                        hoursSpecialHolidayOvertime +
                        hoursSpecialHolidayNSD +
                        hoursSpecialHolidayOIC +
                        hoursSpecialHolidayOICOvertime +
                        hoursSpecialHolidayOICNSD +
                        hoursSpecialHolidayAndRestDay +
                        hoursSpecialHolidayAndRestDayOvertime +
                        hoursSpecialHolidayAndRestDayNSD +
                        hoursRegularHoliday +
                        hoursRegularHolidayOvertime +
                        hoursRegularHolidayNSD +
                        hoursRegularHolidayOIC +
                        hoursRegularHolidayOICOvertime +
                        hoursRegularHolidayOICNSD +
                        hoursRegularHolidayAndRestDay +
                        hoursRegularHolidayAndRestDayOvertime +
                        hoursRegularHolidayAndRestDayNSD +
                        hoursDoubleHoliday +
                        hoursDoubleHolidayOvertime +
                        hoursDoubleHolidayNSD +
                        hoursDoubleHolidayOIC +
                        hoursDoubleHolidayOICOvertime +
                        hoursDoubleHolidayOICNSD +
                        hoursDoubleHolidayAndRestDay +
                        hoursDoubleHolidayAndRestDayOvertime +
                        hoursDoubleHolidayAndRestDayNSD
        )
    }

    BigDecimal getTotalOvertimeHours() {
        return (
                hoursRegularOvertime +
                        hoursRegularOICOvertime +
                        hoursRestOvertime +
                        hoursSpecialHolidayOvertime +
                        hoursSpecialHolidayOICOvertime +
                        hoursSpecialHolidayAndRestDayOvertime +
                        hoursRegularHolidayOvertime +
                        hoursRegularHolidayOICOvertime +
                        hoursRegularHolidayAndRestDayOvertime +
                        hoursDoubleHolidayOvertime +
                        hoursDoubleHolidayOICOvertime +
                        hoursDoubleHolidayAndRestDayOvertime
        )
    }

    BigDecimal getTotalOICHours() {
        return (
                workedOIC +
                        hoursRegularOICOvertime +
                        hoursWorkedOICNSD +
                        hoursSpecialHolidayOIC +
                        hoursSpecialHolidayOICOvertime +
                        hoursSpecialHolidayOICNSD +
                        hoursRegularHolidayOIC +
                        hoursRegularHolidayOICOvertime +
                        hoursRegularHolidayOICNSD +
                        hoursDoubleHolidayOIC +
                        hoursDoubleHolidayOICOvertime +
                        hoursDoubleHolidayOICNSD

        )
    }

    BigDecimal getTotalRestDayDutyHours() {
        return (
                hoursRestDay +
                        hoursRestOvertime +
                        hoursRestDayNSD +
                        hoursSpecialHolidayAndRestDay +
                        hoursSpecialHolidayAndRestDayOvertime +
                        hoursSpecialHolidayAndRestDayNSD +
                        hoursRegularHolidayAndRestDay +
                        hoursRegularHolidayAndRestDayOvertime +
                        hoursRegularHolidayAndRestDayNSD +
                        hoursDoubleHolidayAndRestDay +
                        hoursDoubleHolidayAndRestDayOvertime +
                        hoursDoubleHolidayAndRestDayNSD
        )
    }

    BigDecimal getHoursTotalWorked() { return worked + hoursRegularOvertime }

    BigDecimal getHoursTotalRestDay() { return hoursRestDay + hoursRestOvertime }

    BigDecimal getHoursTotalSpecialHoliday() { return hoursSpecialHoliday + hoursSpecialHolidayOvertime }

    BigDecimal getHoursTotalSpecialHolidayAndRestDay() {
        return hoursSpecialHolidayAndRestDay + hoursSpecialHolidayAndRestDayOvertime
    }

    BigDecimal getHoursTotalRegularHoliday() { return hoursRegularHoliday + hoursRegularOvertime }

    BigDecimal getHoursTotalRegularHolidayAndRestDay() {
        return hoursRegularHolidayAndRestDay + hoursRegularHolidayOvertime
    }

    BigDecimal getHoursTotalDoubleHoliday() { return hoursDoubleHoliday + hoursDoubleHolidayAndRestDay }

    BigDecimal getHoursTotalDoubleHolidayAndRestDay() {
        return hoursDoubleHolidayAndRestDay + hoursDoubleHolidayAndRestDayOvertime
    }

    BigDecimal getHoursTotalNSD() {
        BigDecimal hoursTotalNSD = (
                hoursWorkedNSD +
                        hoursRestDayNSD +
                        hoursSpecialHolidayNSD +
                        hoursSpecialHolidayAndRestDayNSD +
                        hoursRegularHolidayNSD +
                        hoursRegularHolidayAndRestDayNSD +
                        hoursDoubleHolidayNSD +
                        hoursDoubleHolidayAndRestDayNSD
        )
    }

    //===========================TOTAL============================\\

    Boolean getIsRestDayOnly() {

        if (worked > 0) return false
        if (hoursRegularOvertime > 0) return false
        if (hoursWorkedNSD > 0) return false
        if (workedOIC > 0) return false
        if (hoursRegularOICOvertime > 0) return false
        if (hoursWorkedOICNSD > 0) return false
        if (hoursRestDay > 0) return false
        if (hoursRestOvertime > 0) return false
        if (hoursRestDayNSD > 0) return false
        if (hoursSpecialHoliday > 0) return false
        if (hoursSpecialHolidayOvertime > 0) return false
        if (hoursSpecialHolidayNSD > 0) return false
        if (hoursSpecialHolidayOIC > 0) return false
        if (hoursSpecialHolidayOICOvertime > 0) return false
        if (hoursSpecialHolidayOICNSD > 0) return false
        if (hoursSpecialHolidayAndRestDay > 0) return false
        if (hoursSpecialHolidayAndRestDayOvertime > 0) return false
        if (hoursSpecialHolidayAndRestDayNSD > 0) return false
        if (hoursRegularHoliday > 0) return false
        if (hoursRegularHolidayOvertime > 0) return false
        if (hoursRegularHolidayNSD > 0) return false
        if (hoursRegularHolidayOIC > 0) return false
        if (hoursRegularHolidayOICOvertime > 0) return false
        if (hoursRegularHolidayOICNSD > 0) return false
        if (hoursRegularHolidayAndRestDay > 0) return false
        if (hoursRegularHolidayAndRestDayOvertime > 0) return false
        if (hoursRegularHolidayAndRestDayNSD > 0) return false
        if (hoursDoubleHoliday > 0) return false
        if (hoursDoubleHolidayOvertime > 0) return false
        if (hoursDoubleHolidayNSD > 0) return false
        if (hoursDoubleHolidayOIC > 0) return false
        if (hoursDoubleHolidayOICOvertime > 0) return false
        if (hoursDoubleHolidayOICNSD > 0) return false
        if (hoursDoubleHolidayAndRestDay > 0) return false
        if (hoursDoubleHolidayAndRestDayOvertime > 0) return false
        if (hoursDoubleHolidayAndRestDayNSD > 0) return false


        return hoursRestDay > 0
    }

    Boolean getIsOvertimeOnly() {

        if (worked > 0) return false
        if (hoursWorkedNSD > 0) return false
        if (workedOIC > 0) return false
        if (hoursWorkedOICNSD > 0) return false
        if (hoursRestDay > 0) return false
        if (hoursRestDayNSD > 0) return false
        if (hoursSpecialHoliday > 0) return false
        if (hoursSpecialHolidayNSD > 0) return false
        if (hoursSpecialHolidayOIC > 0) return false
        if (hoursSpecialHolidayOICNSD > 0) return false
        if (hoursSpecialHolidayAndRestDay > 0) return false
        if (hoursSpecialHolidayAndRestDayNSD > 0) return false
        if (hoursRegularHoliday > 0) return false
        if (hoursRegularHolidayNSD > 0) return false
        if (hoursRegularHolidayOIC > 0) return false
        if (hoursRegularHolidayOICNSD > 0) return false
        if (hoursRegularHolidayAndRestDay > 0) return false
        if (hoursRegularHolidayAndRestDayNSD > 0) return false
        if (hoursDoubleHoliday > 0) return false
        if (hoursDoubleHolidayNSD > 0) return false
        if (hoursDoubleHolidayOIC > 0) return false
        if (hoursDoubleHolidayOICNSD > 0) return false
        if (hoursDoubleHolidayAndRestDay > 0) return false
        if (hoursDoubleHolidayAndRestDayNSD > 0) return false

        if (hoursRegularOvertime > 0) return true
        if (hoursRegularOICOvertime > 0) return true
        if (hoursRestOvertime > 0) return true
        if (hoursSpecialHolidayOvertime > 0) return true
        if (hoursSpecialHolidayOICOvertime > 0) return true
        if (hoursSpecialHolidayAndRestDayOvertime > 0) return true
        if (hoursRegularHolidayOvertime > 0) return true
        if (hoursRegularHolidayOICOvertime > 0) return true
        if (hoursRegularHolidayAndRestDayOvertime > 0) return true
        if (hoursDoubleHolidayOvertime > 0) return true
        if (hoursDoubleHolidayOICOvertime > 0) return true
        if (hoursDoubleHolidayAndRestDayOvertime > 0) return true

        return false
    }

    Boolean getIsEmpty() {

        if (worked > 0) return false
        if (hoursRegularOvertime > 0) return false
        if (hoursWorkedNSD > 0) return false
        if (workedOIC > 0) return false
        if (hoursRegularOICOvertime > 0) return false
        if (hoursWorkedOICNSD > 0) return false
        if (hoursRestDay > 0) return false
        if (hoursRestOvertime > 0) return false
        if (hoursRestDayNSD > 0) return false
        if (hoursSpecialHoliday > 0) return false
        if (hoursSpecialHolidayOvertime > 0) return false
        if (hoursSpecialHolidayNSD > 0) return false
        if (hoursSpecialHolidayOIC > 0) return false
        if (hoursSpecialHolidayOICOvertime > 0) return false
        if (hoursSpecialHolidayOICNSD > 0) return false
        if (hoursSpecialHolidayAndRestDay > 0) return false
        if (hoursSpecialHolidayAndRestDayOvertime > 0) return false
        if (hoursSpecialHolidayAndRestDayNSD > 0) return false
        if (hoursRegularHoliday > 0) return false
        if (hoursRegularHolidayOvertime > 0) return false
        if (hoursRegularHolidayNSD > 0) return false
        if (hoursRegularHolidayOIC > 0) return false
        if (hoursRegularHolidayOICOvertime > 0) return false
        if (hoursRegularHolidayOICNSD > 0) return false
        if (hoursRegularHolidayAndRestDay > 0) return false
        if (hoursRegularHolidayAndRestDayOvertime > 0) return false
        if (hoursRegularHolidayAndRestDayNSD > 0) return false
        if (hoursDoubleHoliday > 0) return false
        if (hoursDoubleHolidayOvertime > 0) return false
        if (hoursDoubleHolidayNSD > 0) return false
        if (hoursDoubleHolidayOIC > 0) return false
        if (hoursDoubleHolidayOICOvertime > 0) return false
        if (hoursDoubleHolidayOICNSD > 0) return false
        if (hoursDoubleHolidayAndRestDay > 0) return false
        if (hoursDoubleHolidayAndRestDayOvertime > 0) return false
        if (hoursDoubleHolidayAndRestDayNSD > 0) return false

        return true
    }

    Boolean getIsAbsentOnly() { return isEmpty && hoursAbsent > 0 }
}
