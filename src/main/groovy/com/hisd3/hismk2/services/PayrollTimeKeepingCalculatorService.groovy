package com.hisd3.hismk2.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hrm.EventCalendar
import com.hisd3.hismk2.domain.hrm.dto.EmployeeAccumulatedAttendanceDto
import com.hisd3.hismk2.domain.hrm.dto.EmployeeScheduleDto
import com.hisd3.hismk2.graphqlservices.hrm.EventCalendarHolidayType
import com.hisd3.hismk2.graphqlservices.hrm.dtotransformer.EmployeeAttendanceDtoTransformer
import com.hisd3.hismk2.graphqlservices.hrm.dtotransformer.EmployeeScheduleDtoTransformer
import com.hisd3.hismk2.repository.hrm.*
import org.apache.commons.lang3.tuple.*
import org.hibernate.jpa.QueryHints
import org.hibernate.query.Query
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.util.stream.Collectors

@Component
class PayrollTimeKeepingCalculatorService {

    @Autowired
    EmployeeRepository employeeRepository

    @Autowired
    EventCalendarRepository eventCalendarRepository

    @Autowired
    EmployeeScheduleRepository employeeScheduleRepository

    @Autowired
    EmployeeAttendanceRepository employeeAttendanceRepository

    @Autowired
    PayslipRepository payslipRepository

    @Autowired
    LogFlagRepository logFlagRepository

    @Autowired
    NotificationService notificationService

    @Autowired
    ObjectMapper objectMapper

    @PersistenceContext
    EntityManager entityManager

    //used for accumulated logs
    EmployeeAccumulatedAttendanceDto computeHoursLateAndUndertime(
            EmployeeAccumulatedAttendanceDto accumulatedAttendance,
            String date,
            String yesterdayDate,
            Map<String, Object> schedules,
            Boolean useYesterdaySched
    ) {

        accumulatedAttendance.inTime = accumulatedAttendance.inTime.with(ChronoField.MILLI_OF_SECOND, 0)
        accumulatedAttendance.outTime = accumulatedAttendance.outTime.with(ChronoField.MILLI_OF_SECOND, 0)

        EmployeeScheduleDto schedule = schedules.get(date) as EmployeeScheduleDto
        List<EmployeeScheduleDto> overtimes = schedules.get("${date}_OVERTIME") as List<EmployeeScheduleDto>
        EmployeeScheduleDto rest = schedules.get("${date}_REST") as EmployeeScheduleDto
        if (useYesterdaySched) {
            schedule = schedules.get(yesterdayDate) as EmployeeScheduleDto
            overtimes = schedules.get("${yesterdayDate}_OVERTIME") as List<EmployeeScheduleDto>
            rest = schedules.get("${yesterdayDate}_REST") as EmployeeScheduleDto
        }

        if (!schedule) {
            accumulatedAttendance.isError = true
            accumulatedAttendance.message = "No schedule found"
            return accumulatedAttendance
        }
        Instant scheduleDateTimeStart = schedule.dateTimeStartRaw
        Instant scheduleDateTimeEnd = schedule.dateTimeEndRaw
        Instant mealBreakStart = schedule.mealBreakStart
        Instant mealBreakEnd = schedule.mealBreakEnd
        BigDecimal HOUR_COMPUTATION = 60 * 60
        String DECIMAL_FORMAT = "%.4f"
        Integer TIME_ALLOWANCE = -59

        EmployeeAccumulatedAttendanceDto computedSchedule = objectMapper.convertValue(accumulatedAttendance, EmployeeAccumulatedAttendanceDto)
        def inTimeDifference = Duration.between(computedSchedule.inTime, scheduleDateTimeStart).getSeconds()
        def outTimeDifference = 0
        if (!computedSchedule.outTime) {
            computedSchedule.isError = true
            computedSchedule.message = "Employee did not time-out."
            return computedSchedule
        } else {

            outTimeDifference = Duration.between(scheduleDateTimeEnd, computedSchedule.outTime).getSeconds()

            //in difference if the value is positive then employee is early else they are late
            def consideredIn, consideredOut
            //get the in time that will be used in the computation
            if (inTimeDifference >= TIME_ALLOWANCE) consideredIn = scheduleDateTimeStart
            else consideredIn = computedSchedule.inTime //time form the biometrics device
            //get the out time that will be used in the computation
            //if the out difference value is positive then the employee timed out late (which is good for the employee)
            //if the out difference value is negative then the employee timed out early (bad for employee)
            if (outTimeDifference >= TIME_ALLOWANCE) consideredOut = scheduleDateTimeEnd
            else consideredOut = computedSchedule.outTime //time from the biometrics device

            BigDecimal workedHours = 0
            BigDecimal underTime = 0
            BigDecimal late = 0
            if (inTimeDifference < TIME_ALLOWANCE) late = (Duration.between(scheduleDateTimeStart, computedSchedule.inTime).getSeconds() / HOUR_COMPUTATION)

            // start of worked hour deduction of meal breaks(if there is any)
            if (mealBreakStart && mealBreakEnd) {
                BigDecimal mealBreakDuration = Duration.between(mealBreakStart, mealBreakEnd).getSeconds() / HOUR_COMPUTATION
                if ((consideredOut.isAfter(mealBreakStart) || consideredOut == mealBreakStart)
                        && (consideredOut.isBefore(mealBreakEnd) || consideredOut == mealBreakEnd)
                ) {
                    // this means the employee timed-out at lunch time, which means we will use the meal break start as
                    // the considered time out
                    workedHours = Duration.between(consideredIn, mealBreakStart).getSeconds() / HOUR_COMPUTATION
                    underTime = Duration.between(mealBreakEnd, scheduleDateTimeEnd).getSeconds() / HOUR_COMPUTATION
                } else if ((consideredIn.isAfter(mealBreakStart) || consideredIn == mealBreakStart)
                        && (consideredIn.isBefore(mealBreakEnd) || consideredIn == mealBreakEnd)
                ) {
                    // this is when employee timed in between meal break, we will start counting at the end of meal break
                    // until the considered time out
                    workedHours = Duration.between(mealBreakEnd, consideredOut).getSeconds() / HOUR_COMPUTATION
                    if (consideredOut.isBefore(scheduleDateTimeEnd)) {
                        underTime = Duration.between(consideredOut, scheduleDateTimeEnd).getSeconds() / HOUR_COMPUTATION
                    }
                    late = Duration.between(scheduleDateTimeStart, mealBreakStart).getSeconds() / HOUR_COMPUTATION
                } else if (consideredOut.isBefore(mealBreakStart)) {
                    workedHours = Duration.between(consideredIn, consideredOut).getSeconds() / HOUR_COMPUTATION
                    underTime = Duration.between(consideredOut, scheduleDateTimeEnd).getSeconds() / HOUR_COMPUTATION
                    underTime -= mealBreakDuration
                } else {
                    workedHours = Duration.between(consideredIn, consideredOut).getSeconds() / HOUR_COMPUTATION
                    workedHours -= mealBreakDuration
                    underTime = Duration.between(consideredOut, scheduleDateTimeEnd).getSeconds() / HOUR_COMPUTATION
                }
            } else {
                // we will go with the normal computation
                workedHours = Duration.between(consideredIn, consideredOut).getSeconds() / HOUR_COMPUTATION
                if (outTimeDifference < TIME_ALLOWANCE) underTime = (((outTimeDifference) * (-1)) / HOUR_COMPUTATION)
            }

            if (workedHours < 0) {
                return computeHoursLateAndUndertime(
                        accumulatedAttendance,
                        date,
                        yesterdayDate,
                        schedules,
                        true)
            }


            computedSchedule.worked = workedHours
            computedSchedule.undertime = underTime
            computedSchedule.late = late

            return computedSchedule
        }
    }

    // used to calculate hours given schedule and log
    // a schedule can be regular or overtime
    Triple<EmployeeAccumulatedAttendanceDto, Instant, Instant> computeHours(
            EmployeeScheduleDto schedule,
            Map<String, Instant> log
    ) {

//        Instant scheduleDateTimeStart = schedule.dateTimeStartRaw.with(ChronoField.MILLI_OF_SECOND,0).with(ChronoField.SECOND_OF_MINUTE,0)
//        Instant scheduleDateTimeEnd = schedule.dateTimeEndRaw.with(ChronoField.MILLI_OF_SECOND,0).with(ChronoField.SECOND_OF_MINUTE,0)
//        Instant mealBreakStart = schedule.mealBreakStart.with(ChronoField.MILLI_OF_SECOND,0).with(ChronoField.SECOND_OF_MINUTE,0)
//        Instant mealBreakEnd = schedule.mealBreakEnd.with(ChronoField.MILLI_OF_SECOND,0).with(ChronoField.SECOND_OF_MINUTE,0)
        Instant scheduleDateTimeStart = schedule.dateTimeStartRaw
        Instant scheduleDateTimeEnd = schedule.dateTimeEndRaw
        Instant mealBreakStart = schedule.mealBreakStart
        Instant mealBreakEnd = schedule.mealBreakEnd

        Instant inTime = log.get("IN")
        Instant outTime = log.get("OUT")
        if (inTime) inTime = inTime.with(ChronoField.MILLI_OF_SECOND, 0)
        if (outTime) outTime = outTime.with(ChronoField.MILLI_OF_SECOND, 0)
        BigDecimal HOUR_COMPUTATION = 60 * 60
        String DECIMAL_FORMAT = "%.4f"
        Integer TIME_ALLOWANCE = -59
        EmployeeAccumulatedAttendanceDto scheduleComputation = new EmployeeAccumulatedAttendanceDto()

        scheduleComputation.inTime = inTime
        scheduleComputation.outTime = outTime
        if (!scheduleComputation.inTime) {
            scheduleComputation.isError = true
            scheduleComputation.message = "Employee did not time-in."
            return new ImmutableTriple<EmployeeAccumulatedAttendanceDto, Instant, Instant>(scheduleComputation, inTime, outTime)
        }
        def inTimeDifference = Duration.between(scheduleComputation.inTime, scheduleDateTimeStart).getSeconds()
        def outTimeDifference = 0
        if (!scheduleComputation.outTime) {
            scheduleComputation.isError = true
            scheduleComputation.message = "Employee did not time-out."
            return new ImmutableTriple<EmployeeAccumulatedAttendanceDto, Instant, Instant>(scheduleComputation, inTime, outTime)
//            return scheduleComputation
        } else {

            outTimeDifference = Duration.between(scheduleDateTimeEnd, scheduleComputation.outTime).getSeconds()

            //in difference if the value is positive then employee is early else they are late
            def consideredIn, consideredOut
            //get the in time that will be used in the computation
            if (inTimeDifference >= TIME_ALLOWANCE) consideredIn = scheduleDateTimeStart
            else consideredIn = scheduleComputation.inTime //time form the biometrics device
            //get the out time that will be used in the computation
            //if the out difference value is positive then the employee timed out late (which is good for the employee)
            //if the out difference value is negative then the employee timed out early (bad for employee)
            if (outTimeDifference >= TIME_ALLOWANCE) consideredOut = scheduleDateTimeEnd
            else consideredOut = scheduleComputation.outTime //time from the biometrics device

            BigDecimal workedHours = 0
            BigDecimal underTime = 0
            BigDecimal late = 0
            if (inTimeDifference < TIME_ALLOWANCE) late = (Duration.between(scheduleDateTimeStart, scheduleComputation.inTime).getSeconds() / HOUR_COMPUTATION)

            consideredIn
            consideredOut = consideredOut.with(ChronoField.MILLI_OF_SECOND, 0)


            // start of worked hour deduction of meal breaks(if there is any)
            BigDecimal mealBreakDuration = 0

            if (mealBreakStart && mealBreakEnd && consideredIn.isBefore(mealBreakEnd)) {
                mealBreakDuration = Duration.between(mealBreakStart, mealBreakEnd).getSeconds() / HOUR_COMPUTATION
                if ((consideredOut.isAfter(mealBreakStart) || consideredOut == mealBreakStart)
                        && (consideredOut.isBefore(mealBreakEnd) || consideredOut == mealBreakEnd)
                ) {
                    // this means the employee timed-out at lunch time, which means we will use the meal break start as
                    // the considered time out
                    workedHours = Duration.between(consideredIn, mealBreakStart).getSeconds() / HOUR_COMPUTATION
                    underTime = Duration.between(mealBreakEnd, scheduleDateTimeEnd).getSeconds() / HOUR_COMPUTATION
                } else if ((consideredIn.isAfter(mealBreakStart) || consideredIn == mealBreakStart)
                        && (consideredIn.isBefore(mealBreakEnd) || consideredIn == mealBreakEnd)
                ) {
                    // this is when employee timed in between meal break, we will start counting at the end of meal break
                    // until the considered time out
                    workedHours = Duration.between(mealBreakEnd, consideredOut).getSeconds() / HOUR_COMPUTATION
                    if (consideredOut.isBefore(scheduleDateTimeEnd)) {
                        underTime = Duration.between(consideredOut, scheduleDateTimeEnd).getSeconds() / HOUR_COMPUTATION
                    }
                    late = Duration.between(scheduleDateTimeStart, mealBreakStart).getSeconds() / HOUR_COMPUTATION
                } else if (consideredOut.isBefore(mealBreakStart)) {
                    workedHours = Duration.between(consideredIn, consideredOut).getSeconds() / HOUR_COMPUTATION
                    underTime = Duration.between(consideredOut, scheduleDateTimeEnd).getSeconds() / HOUR_COMPUTATION
                    underTime -= mealBreakDuration
                } else {
                    workedHours = Duration.between(consideredIn, consideredOut).getSeconds() / HOUR_COMPUTATION
                    workedHours -= mealBreakDuration
                    underTime = Duration.between(consideredOut, scheduleDateTimeEnd).getSeconds() / HOUR_COMPUTATION

                    if(consideredIn.isAfter(mealBreakEnd)){
                        workedHours += mealBreakDuration
                        late -= mealBreakDuration
                    }
                }
            } else {
                // we will go with the normal computation
                workedHours = Duration.between(consideredIn, consideredOut).getSeconds() / HOUR_COMPUTATION
                if (outTimeDifference < TIME_ALLOWANCE) underTime = (((outTimeDifference) * (-1)) / HOUR_COMPUTATION)
            }

            scheduleComputation.worked = workedHours
            scheduleComputation.undertime = underTime
            scheduleComputation.late = late

            return new ImmutableTriple<EmployeeAccumulatedAttendanceDto, Instant, Instant>(scheduleComputation, consideredIn, consideredOut)
//            return scheduleComputation
        }
    }

    // this computes worked hours only, no late and undertime
    BigDecimal computeHours(EmployeeScheduleDto schedule, Instant logStart, Instant logEnd) {
        logStart = logStart.with(ChronoField.MILLI_OF_SECOND, 0)
        logEnd = logEnd.with(ChronoField.MILLI_OF_SECOND, 0)
        BigDecimal HOUR_COMPUTATION = 60 * 60
        Instant scheduleStart = schedule.dateTimeStartRaw
        Instant scheduleEnd = schedule.dateTimeEndRaw
        Instant mealBreakStart = schedule.mealBreakStart
        Instant mealBreakEnd = schedule.mealBreakEnd
        Instant consideredIn
        Instant consideredOut
        BigDecimal workedHours
        Long ALLOWANCE = 60
        Long scheduleStartLogStartDiff = Duration.between(scheduleStart, logStart).getSeconds()

        if (scheduleStartLogStartDiff < ALLOWANCE)
            consideredIn = scheduleStart
        else if (scheduleStart.isBefore(logStart))
            consideredIn = logStart
        else
            consideredIn = scheduleStart

        if (scheduleEnd.isAfter(logEnd))
            consideredOut = logEnd
        else
            consideredOut = scheduleEnd

        if (mealBreakStart && mealBreakEnd) {
            BigDecimal mealBreakDuration = Duration.between(mealBreakStart, mealBreakEnd).getSeconds() / HOUR_COMPUTATION
            if ((consideredOut.isAfter(mealBreakStart) || consideredOut == mealBreakStart)
                    && (consideredOut.isBefore(mealBreakEnd) || consideredOut == mealBreakEnd)
            ) {
                // this means the employee timed-out at lunch time, which means we will use the meal break start as
                // the considered time out
                workedHours = Duration.between(consideredIn, mealBreakStart).getSeconds() / HOUR_COMPUTATION
            } else if ((consideredIn.isAfter(mealBreakStart) || consideredIn == mealBreakStart)
                    && (consideredIn.isBefore(mealBreakEnd) || consideredIn == mealBreakEnd)
            ) {
                // this is when employee timed in between meal break, we will start counting at the end of meal break
                // until the considered time out
                workedHours = Duration.between(mealBreakEnd, consideredOut).getSeconds() / HOUR_COMPUTATION
            } else if (consideredOut.isBefore(mealBreakStart)) {
                workedHours = Duration.between(consideredIn, consideredOut).getSeconds() / HOUR_COMPUTATION
            } else {
                workedHours = Duration.between(consideredIn, consideredOut).getSeconds() / HOUR_COMPUTATION
                workedHours -= mealBreakDuration
            }
        } else {
            // we will go with the normal computation
            workedHours = Duration.between(consideredIn, consideredOut).getSeconds() / HOUR_COMPUTATION
        }
        return workedHours
    }

    //====================================TIME KEEPING====================================\\

    List<EmployeeAccumulatedAttendanceDto> getAccumulatedLogs(UUID id, Instant startDate, Instant endDate) {

        Map<String, Object> employeeSchedule = getSchedule(id, startDate, endDate)

        Map<String, Object> employeeRestSchedule = getRestSchedule(id, startDate, endDate)

        Map<String, Object> employeeOvertimeSchedule = getOvertimeSchedule(id, startDate, endDate)

        Map<String, Object> employeeOICSchedule = getOICSchedule(id, startDate, endDate)

        Map<String, Object> employeeOICOvertimeSchedule = getOvertimeOICSchedule(id, startDate, endDate)

        Map<String, Object> employeeLeaveSchedule = getLeaveSchedule(id, startDate, endDate)

        Map<String, List<Map<String, Instant>>> logs = getLogs(id, startDate, endDate)

        Map<String, List<EventCalendar>> holidays = getHolidays(startDate, endDate)

//        Instant lastdate = lastDayOfSchedules(employeeSchedule, employeeRestSchedule, employeeOvertimeSchedule)

        List<EmployeeAccumulatedAttendanceDto> finalAccumulatedLogs = []
        Instant date = startDate
        while (date.isBefore(endDate)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd").withZone(ZoneId.systemDefault())

            EmployeeAccumulatedAttendanceDto firstLog = calculateAccumulatedLog(date, employeeSchedule, employeeRestSchedule, holidays, logs)
            EmployeeAccumulatedAttendanceDto secondLog = calculateOvertimeAccumulatedLogs(date, employeeOvertimeSchedule, employeeRestSchedule, holidays, logs)
            EmployeeAccumulatedAttendanceDto oicLog = calculateOICAccumulatedLog(date, employeeOICSchedule, employeeRestSchedule, holidays, logs)
            EmployeeAccumulatedAttendanceDto oicOvertimeLog = calculateOvertimeOICAccumulatedLogs(date, employeeOICOvertimeSchedule, employeeRestSchedule, holidays, logs)
            EmployeeAccumulatedAttendanceDto leavesLog = calculateLeaveAccumulatedLogs(date, employeeLeaveSchedule)

            EmployeeAccumulatedAttendanceDto finalLog = addAccumulatedLogs(firstLog, secondLog)
            finalLog = addAccumulatedLogs(finalLog, oicLog)
            finalLog = addAccumulatedLogs(finalLog, oicOvertimeLog)
            finalLog = addAccumulatedLogs(finalLog, leavesLog)
            if (!firstLog.inTime && !firstLog.outTime) {
                finalLog.inTime = secondLog.inTime
                finalLog.outTime = secondLog.outTime
            }

            finalLog.date = date
            finalLog = reduceSchedule(finalLog)
            finalAccumulatedLogs.add(finalLog)
            date = date.plus(1, ChronoUnit.DAYS)

        }

        return finalAccumulatedLogs
    }

    EmployeeAccumulatedAttendanceDto getEmployeePerformanceSummary(UUID id, Instant startDate, Instant endDate) {
        Map<String, Object> employeeSchedule = getSchedule(id, startDate, endDate)

        Map<String, Object> employeeRestSchedule = getRestSchedule(id, startDate, endDate)

        Map<String, Object> employeeOvertimeSchedule = getOvertimeSchedule(id, startDate, endDate)

        Map<String, Object> employeeOICSchedule = getOICSchedule(id, startDate, endDate)

        Map<String, Object> employeeOICOvertimeSchedule = getOvertimeOICSchedule(id, startDate, endDate)

        Map<String, Object> employeeLeaveSchedule = getLeaveSchedule(id, startDate, endDate)

        Map<String, List<Map<String, Instant>>> logs = getLogs(id, startDate, endDate)

        Map<String, List<EventCalendar>> holidays = getHolidays(startDate, endDate)

        EmployeeAccumulatedAttendanceDto employeePerfSummary = new EmployeeAccumulatedAttendanceDto()
        Instant date = startDate
        while (date.isBefore(endDate)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd").withZone(ZoneId.systemDefault())

            EmployeeAccumulatedAttendanceDto firstLog = calculateAccumulatedLog(date, employeeSchedule, employeeRestSchedule, holidays, logs)
            EmployeeAccumulatedAttendanceDto secondLog = calculateOvertimeAccumulatedLogs(date, employeeOvertimeSchedule, employeeRestSchedule, holidays, logs)
            EmployeeAccumulatedAttendanceDto oicLog = calculateOICAccumulatedLog(date, employeeOICSchedule, employeeRestSchedule, holidays, logs)
            EmployeeAccumulatedAttendanceDto oicOvertimeLog = calculateOvertimeOICAccumulatedLogs(date, employeeOICOvertimeSchedule, employeeRestSchedule, holidays, logs)
            EmployeeAccumulatedAttendanceDto leavesLog = calculateLeaveAccumulatedLogs(date, employeeLeaveSchedule)

            EmployeeAccumulatedAttendanceDto finalLog = addAccumulatedLogs(firstLog, secondLog)
            finalLog = addAccumulatedLogs(finalLog, oicLog)
            finalLog = addAccumulatedLogs(finalLog, oicOvertimeLog)

            finalLog.date = date
            finalLog = reduceSchedule(finalLog)
            finalLog = calculateAccumulatedLogsCount(finalLog)
            finalLog = addAccumulatedLogs(finalLog, leavesLog)

            employeePerfSummary = addAccumulatedLogs(employeePerfSummary, finalLog)

            date = date.plus(1, ChronoUnit.DAYS)

        }
        return employeePerfSummary
    }

    // used to calculate accumulated logs
    EmployeeAccumulatedAttendanceDto calculateAccumulatedLog(
            Instant date,
            Map<String, Object> employeeSchedule,
            Map<String, Object> employeeRestDaySchedule,
            Map<String, List<EventCalendar>> holidays,
            Map<String, List<Map<String, Instant>>> employeeLogs
    ) {
        EmployeeAccumulatedAttendanceDto accumulatedLog = new EmployeeAccumulatedAttendanceDto()
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd").withZone(ZoneId.systemDefault())
        String formattedDate = formatter.format(date)
        String restFormattedDate = formattedDate

        EmployeeScheduleDto schedule = employeeSchedule.get(formattedDate) as EmployeeScheduleDto
        List<Map<String, Instant>> logs = employeeLogs.get(formattedDate)

        EmployeeScheduleDto restSchedule = employeeRestDaySchedule.get(restFormattedDate) as EmployeeScheduleDto

        // do this only if there is a schedule
        if (schedule) {
            BigDecimal scheduleDuration = computeHours(schedule, schedule.dateTimeStartRaw, schedule.dateTimeEndRaw)
            if (!logs) {
                if (restSchedule) {
                    accumulatedLog.isRestDay = true
                    return accumulatedLog
                }
                //check next day logs
                Instant nextDayDate = date.plus(1, ChronoUnit.DAYS)
                String formattedNextDayDate = formatter.format(nextDayDate)
                logs = employeeLogs.get(formattedNextDayDate)
                if (!logs) {
                    // no logs found even the next day hence counted as absent
                    accumulatedLog.hoursAbsent += computeHours(schedule, schedule.dateTimeStartRaw, schedule.dateTimeEndRaw)
                    return accumulatedLog
                }
            }

            Integer matchedLogs = 0
            for (it in logs) {
                Instant inTime = it.get("IN") as Instant
                Instant outTime = it.get("OUT") as Instant
                Triple<EmployeeAccumulatedAttendanceDto, Instant, Instant> computedHoursTriple = computeHours(schedule, it)
                EmployeeAccumulatedAttendanceDto computedHours = computedHoursTriple.left
                if (computedHours.isError) {
                    if (computedHours.message == "Employee did not time-out." || computedHours.message == "Employee did not time-in.") {
                        accumulatedLog.hoursAbsent = computeHours(schedule, schedule.dateTimeStartRaw, schedule.dateTimeEndRaw)
                    }
                    accumulatedLog.isError = computedHours.isError
                    accumulatedLog.message = computedHours.message
                    accumulatedLog.inTime = computedHoursTriple.middle
                    accumulatedLog.outTime = computedHoursTriple.right
                    return accumulatedLog
                }
                if (computedHours.worked > 0) {
                    matchedLogs += 1
                    accumulatedLog.worked += computedHours.worked
                    accumulatedLog.late += computedHours.late
                    accumulatedLog.undertime += computedHours.undertime

                    Instant consideredInTime = computedHoursTriple.middle
                    Instant consideredOutTime = computedHoursTriple.right
                    BigDecimal nightShiftDifferential = computeNightShiftDifferential(schedule, consideredInTime, consideredOutTime, date)
                    if (schedule.isRestDay) {
                        accumulatedLog.isRestDay = true
                        accumulatedLog.hoursRestDay += computedHours.worked
                        if (schedule.withNSD)
                            accumulatedLog.hoursRestDayNSD += nightShiftDifferential
                    }
                    if (schedule.withNSD) {
                        accumulatedLog.hoursWorkedNSD += nightShiftDifferential
                    } else {
                        accumulatedLog.withNSD = true
                    }

                    accumulatedLog.inTime = it.get("IN")
                    accumulatedLog.outTime = it.get("OUT")
                    Boolean HAS_ADDED_HOURS = false
                    Boolean HAS_ADDED_COUNT = false

                    //holidays
                    //check with holiday
                    DateTimeFormatter holidayFormatter = DateTimeFormatter.ofPattern("MM_dd_yyyy").withZone(ZoneId.systemDefault())
                    List<Instant> days = daysScheduleHasPass(consideredInTime, consideredOutTime)

                    // checking possible holiday hours in
                    // each possible days of the schedule
                    if (schedule.withHoliday) {
                        List<Instant> addedHoursRegularHolidays = []
                        days.each { Instant holidayDate ->
                            String currentHolidayFormattedDate = holidayFormatter.format(holidayDate)
                            List<EventCalendar> eachHoliday = holidays.get(currentHolidayFormattedDate)

                            if (eachHoliday) {
                                if (eachHoliday.size() > 1) {
                                    // double holiday
                                    EventCalendar holiday = eachHoliday.first()
                                    EmployeeScheduleDto dummySchedule = new EmployeeScheduleDto()
                                    dummySchedule.dateTimeStartRaw = holiday.startDate
                                    dummySchedule.dateTimeEndRaw = holiday.endDate
                                    dummySchedule.mealBreakStart = schedule.mealBreakStart
                                    dummySchedule.mealBreakEnd = schedule.mealBreakEnd
                                    BigDecimal hoursHoliday = computeHours(dummySchedule, consideredInTime, consideredOutTime)
                                    BigDecimal holidayNSD = computeNightShiftDifferential(dummySchedule, consideredInTime, consideredOutTime, date)
                                    BigDecimal holidayFinalHours = hoursHoliday

                                    accumulatedLog.hoursDoubleHoliday += holidayFinalHours
                                    if (schedule.isRestDay) {
                                        accumulatedLog.hoursDoubleHolidayAndRestDay += holidayFinalHours
                                        if (schedule.withNSD)
                                            accumulatedLog.hoursDoubleHolidayAndRestDayNSD += holidayNSD
                                    }
                                    if (schedule.withNSD) {
                                        accumulatedLog.hoursDoubleHolidayNSD += holidayNSD
                                    }

                                    addedHoursRegularHolidays.add(holidayDate)
                                } else {
                                    if (!eachHoliday.empty) {
                                        EventCalendar holiday = eachHoliday.first()
                                        EmployeeScheduleDto dummySchedule = new EmployeeScheduleDto()
                                        dummySchedule.dateTimeStartRaw = holiday.startDate
                                        dummySchedule.dateTimeEndRaw = holiday.endDate
                                        dummySchedule.mealBreakStart = schedule.mealBreakStart
                                        dummySchedule.mealBreakEnd = schedule.mealBreakEnd
                                        BigDecimal hoursHoliday = computeHours(dummySchedule, consideredInTime, consideredOutTime)
                                        BigDecimal holidayNSD = computeNightShiftDifferential(dummySchedule, consideredInTime, consideredOutTime, date)
                                        BigDecimal totalHolidayHours = hoursHoliday
                                        BigDecimal holidayFinalHours = totalHolidayHours
                                        if (holiday.holidayType == EventCalendarHolidayType.SPECIAL_NON_WORKING.toString()) {
                                            //calculate hours for special holiday
                                            accumulatedLog.hoursSpecialHoliday += holidayFinalHours
                                            if (schedule.isRestDay) {
                                                accumulatedLog.hoursSpecialHolidayAndRestDay += holidayFinalHours
                                                if (schedule.withNSD)
                                                    accumulatedLog.hoursSpecialHolidayAndRestDayNSD += holidayNSD
                                            }
                                            if (schedule.withNSD)
                                                accumulatedLog.hoursSpecialHolidayNSD += holidayNSD
                                            addedHoursRegularHolidays.add(holidayDate)
                                        } else if (holiday.holidayType == EventCalendarHolidayType.REGULAR.toString()) {
                                            //calculate hours for regular holiday
                                            accumulatedLog.hoursRegularHoliday += holidayFinalHours

                                            if (schedule.isRestDay) {
                                                accumulatedLog.hoursRegularHolidayAndRestDay += holidayFinalHours
                                                if (schedule.withNSD)
                                                    accumulatedLog.hoursRegularHolidayAndRestDayNSD += holidayNSD
                                            }
                                            if (schedule.withNSD)
                                                accumulatedLog.hoursRegularHolidayNSD += holidayNSD


                                            addedHoursRegularHolidays.add(holidayDate)
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }

            if (restSchedule) {
                if (schedule && matchedLogs > 1) {
                    //in case of on call duties meaning no schedule
                } else {

                    accumulatedLog.isRestDay = true
                    if (logs) {
                        if (logs.size() > 0 && !schedule) {
                            accumulatedLog.inTime = logs.first().get("IN")
                            accumulatedLog.outTime = logs.first().get("OUT")
                            accumulatedLog.isError = true
                            accumulatedLog.message = "No Schedule"
                        }
                    }
                    if (accumulatedLog.undertime + accumulatedLog.late + accumulatedLog.worked == scheduleDuration * 2 && logs.size() == 2 && schedule.mealBreakEnd && schedule.mealBreakStart) {
                        accumulatedLog = mealBreakConsolidateUnderperformances(accumulatedLog, logs, schedule)
                    }
                    return accumulatedLog
                }
            } else if (matchedLogs == 0) {
                // found logs but the number of worked hours is negative
                // hence the time did not match and the employee is absent
                accumulatedLog.hoursAbsent += computeHours(schedule, schedule.dateTimeStartRaw, schedule.dateTimeEndRaw)
            }

            if (accumulatedLog.undertime + accumulatedLog.late + accumulatedLog.worked == scheduleDuration * 2 && logs.size() == 2 && schedule.mealBreakEnd && schedule.mealBreakStart) {
                accumulatedLog = mealBreakConsolidateUnderperformances(accumulatedLog, logs, schedule)
            }

            return accumulatedLog
        } else {
            // check for logs
            if (logs && restSchedule) {
                accumulatedLog.isRestDay = true
            } else if (!logs && restSchedule) {
                accumulatedLog.isRestDay = true
            } else if (logs) {
                if (logs.size() > 0) {
                    accumulatedLog.isError = true
                    accumulatedLog.message = "No Schedule"
                }
            } else {
                accumulatedLog.isError = true
                accumulatedLog.message = "No Schedule"
            }
            return accumulatedLog
        }

    }


    // used to calculate accumulated logs
    EmployeeAccumulatedAttendanceDto calculateOICAccumulatedLog(
            Instant date,
            Map<String, Object> employeeSchedule,
            Map<String, Object> employeeRestDaySchedule,
            Map<String, List<EventCalendar>> holidays,
            Map<String, List<Map<String, Instant>>> employeeLogs
    ) {
        EmployeeAccumulatedAttendanceDto accumulatedLog = new EmployeeAccumulatedAttendanceDto()
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd").withZone(ZoneId.systemDefault())
        String formattedDate = formatter.format(date)
        String restFormattedDate = formattedDate + "_REST"

        EmployeeScheduleDto schedule = employeeSchedule.get(formattedDate + "_OIC") as EmployeeScheduleDto
        List<Map<String, Instant>> logs = employeeLogs.get(formattedDate)

        EmployeeScheduleDto restSchedule = employeeRestDaySchedule.get(restFormattedDate) as EmployeeScheduleDto

        // do this only if there is a schedule
        if (schedule) {
            BigDecimal scheduleDuration = computeHours(schedule, schedule.dateTimeStartRaw, schedule.dateTimeEndRaw)
            BigDecimal totalLate = 0
            BigDecimal totalUndertime = 0
            Integer matchedLogs = 0
            for (it in logs) {
                Instant inTime = it.get("IN") as Instant
                Instant outTime = it.get("OUT") as Instant
                Triple<EmployeeAccumulatedAttendanceDto, Instant, Instant> computedHoursTriple = computeHours(schedule, it)
                EmployeeAccumulatedAttendanceDto computedHours = computedHoursTriple.left
                totalLate += computedHours.late
                totalUndertime += computedHours.undertime
                if (computedHours.worked > 0) {
                    matchedLogs += 1
                    accumulatedLog.workedOIC += computedHours.worked
                    Instant consideredInTime = computedHoursTriple.middle
                    Instant consideredOutTime = computedHoursTriple.right
                    BigDecimal nightShiftDifferential = computeNightShiftDifferential(schedule, consideredInTime, consideredOutTime, date)

                    if (schedule.withNSD)
                        accumulatedLog.hoursWorkedOICNSD += nightShiftDifferential
                    accumulatedLog.inTime = it.get("IN")
                    accumulatedLog.outTime = it.get("OUT")
                    Boolean HAS_ADDED_HOURS = false
                    Boolean HAS_ADDED_COUNT = false

                    //holidays
                    //check with holiday
                    DateTimeFormatter holidayFormatter = DateTimeFormatter.ofPattern("MM_dd_yyyy").withZone(ZoneId.systemDefault())
                    List<Instant> days = daysScheduleHasPass(consideredInTime, consideredOutTime)


                    // checking possible holiday hours in
                    // each possible days of the schedule
                    if (schedule.withHoliday) {

                        List<Instant> addedHoursRegularHolidays = []
                        days.each { Instant holidayDate ->
                            String currentHolidayFormattedDate = holidayFormatter.format(holidayDate)
                            List<EventCalendar> eachHoliday = holidays.get(currentHolidayFormattedDate)

                            if (eachHoliday) {
                                if (eachHoliday.size() > 1) {
                                    // double holiday
                                    EventCalendar holiday = eachHoliday.first()
                                    EmployeeScheduleDto dummySchedule = new EmployeeScheduleDto()
                                    dummySchedule.dateTimeStartRaw = holiday.startDate
                                    dummySchedule.dateTimeEndRaw = holiday.endDate
                                    dummySchedule.mealBreakStart = schedule.mealBreakStart
                                    dummySchedule.mealBreakEnd = schedule.mealBreakEnd
                                    BigDecimal hoursHoliday = computeHours(dummySchedule, consideredInTime, consideredOutTime)
                                    BigDecimal holidayNSD = computeNightShiftDifferential(dummySchedule, consideredInTime, consideredOutTime, date)

                                    accumulatedLog.hoursDoubleHolidayOIC += hoursHoliday
                                    if (schedule.withNSD)
                                        accumulatedLog.hoursDoubleHolidayOICNSD += holidayNSD


                                    addedHoursRegularHolidays.add(holidayDate)
                                } else {
                                    if (!eachHoliday.empty) {
                                        EventCalendar holiday = eachHoliday.first()
                                        EmployeeScheduleDto dummySchedule = new EmployeeScheduleDto()
                                        dummySchedule.dateTimeStartRaw = holiday.startDate
                                        dummySchedule.dateTimeEndRaw = holiday.endDate
                                        dummySchedule.mealBreakStart = schedule.mealBreakStart
                                        dummySchedule.mealBreakEnd = schedule.mealBreakEnd
                                        BigDecimal hoursHoliday = computeHours(dummySchedule, consideredInTime, consideredOutTime)
                                        BigDecimal holidayNSD = computeNightShiftDifferential(dummySchedule, consideredInTime, consideredOutTime, date)
                                        if (holiday.holidayType == EventCalendarHolidayType.SPECIAL_NON_WORKING.toString()) {
                                            //calculate hours for special holiday
                                            accumulatedLog.hoursSpecialHolidayOIC += hoursHoliday
                                            if (schedule.withNSD)
                                                accumulatedLog.hoursSpecialHolidayOICNSD += holidayNSD

                                            addedHoursRegularHolidays.add(holidayDate)
                                            String a = "asdfa"
                                        } else if (holiday.holidayType == EventCalendarHolidayType.REGULAR.toString()) {
                                            //calculate hours for regular holiday
                                            accumulatedLog.hoursRegularHolidayOIC += hoursHoliday

                                            if (schedule.withNSD)
                                                accumulatedLog.hoursRegularHolidayOICNSD += holidayNSD

                                            addedHoursRegularHolidays.add(holidayDate)
                                            String a = "asdfa"
                                        }
                                    }
                                }
                            }
                        }
                    }

//                    days = days.findAll { addedHoursRegularHolidays.indexOf(it) < 0 }
                }
            }
            BigDecimal test = totalUndertime + totalLate + accumulatedLog.workedOIC

            if (totalUndertime + totalLate + accumulatedLog.workedOIC == scheduleDuration * 2 && logs.size() == 2 && schedule.mealBreakEnd && schedule.mealBreakStart) {
                accumulatedLog = mealBreakConsolidateOICUnderperformances(accumulatedLog, logs, schedule)
            }

            return accumulatedLog
        } else return accumulatedLog

    }

    // used to calculate overtime oic hours
    // this will calculate the employee payslip based on the given date
    EmployeeAccumulatedAttendanceDto calculateOvertimeOICAccumulatedLogs(
            Instant date,
            Map<String, Object> employeeSchedule,
            Map<String, Object> employeeRestDaySchedule,
            Map<String, List<EventCalendar>> holidays,
            Map<String, List<Map<String, Instant>>> employeeLogs
    ) {
        EmployeeAccumulatedAttendanceDto accumulatedLog = new EmployeeAccumulatedAttendanceDto()
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd").withZone(ZoneId.systemDefault())
        String formattedDate = formatter.format(date)
        String overtimeFormattedDate = "${formatter.format(date)}_OVERTIME_OIC"
        String restFormattedDate = formattedDate + "_REST"

        List<EmployeeScheduleDto> schedule = employeeSchedule.get(overtimeFormattedDate) as List<EmployeeScheduleDto>
        List<Map<String, Instant>> logs = employeeLogs.get(formattedDate)

        EmployeeScheduleDto restSchedule = employeeRestDaySchedule.get(restFormattedDate) as EmployeeScheduleDto

        // do this only if there is a schedule
        schedule.each { EmployeeScheduleDto currentSchedule ->
            if (!logs) {
                //check next day logs
                Instant nextDayDate = date.plus(1, ChronoUnit.DAYS)
                String formattedNextDayDate = formatter.format(nextDayDate)
                logs = employeeLogs.get(formattedNextDayDate)
                if (!logs) {
                    // no logs found even the next day hence counted as absent
                    // employeePayslip.hoursAbsent = Duration.between(schedule.dateTimeStartRaw, schedule.dateTimeEndRaw).toMinutes() / 60
                    return accumulatedLog
                }
            }

            Integer matchedLogs = 0
            for (it in logs) {
                Instant inTime = it.get("IN") as Instant
                Instant outTime = it.get("OUT") as Instant
                Triple<EmployeeAccumulatedAttendanceDto, Instant, Instant> computedHoursTriple = computeHours(currentSchedule, it)
                EmployeeAccumulatedAttendanceDto computedHours = computedHoursTriple.left
                if (computedHours.worked > 0) {
                    matchedLogs += 1

                    // employeePayslip.hoursLate += computedHours.late
                    // employeePayslip.hoursUnderTime += computedHours.undertime
                    Instant consideredInTime = computedHoursTriple.middle
                    Instant consideredOutTime = computedHoursTriple.right

                    accumulatedLog.hoursRegularOICOvertime += computedHours.worked

                    accumulatedLog.date = date
                    accumulatedLog.inTime = it.get("IN")
                    accumulatedLog.outTime = it.get("OUT")

                    //holidays
                    //check with holiday
                    DateTimeFormatter holidayFormatter = DateTimeFormatter.ofPattern("MM_dd_yyyy").withZone(ZoneId.systemDefault())
                    String holidayFormattedDate = holidayFormatter.format(date)
                    List<EventCalendar> currentHolidays = holidays.get(holidayFormattedDate)
                    Boolean HAS_ADDED_HOURS = false
                    List<Instant> days = daysScheduleHasPass(consideredInTime, consideredOutTime)

                    /*Regular Holidays*/
                    if (currentSchedule.withHoliday) {

                        List<Instant> regularHolidays = []
                        days.each { Instant holidayDate ->
                            String currentHolidayFormattedDate = holidayFormatter.format(holidayDate)
                            List<EventCalendar> eachHoliday = holidays.get(currentHolidayFormattedDate)
                            if (eachHoliday) {
                                if (eachHoliday.size() > 1) {
                                    /*BigDecimal Holidays*/
                                    EventCalendar holiday = eachHoliday.first()
                                    EmployeeScheduleDto dummySchedule = new EmployeeScheduleDto()
                                    dummySchedule.dateTimeStartRaw = holiday.startDate
                                    dummySchedule.dateTimeEndRaw = holiday.endDate
                                    dummySchedule.mealBreakStart = currentSchedule.mealBreakStart
                                    dummySchedule.mealBreakEnd = currentSchedule.mealBreakEnd
                                    BigDecimal hoursHoliday = computeHours(dummySchedule, consideredInTime, consideredOutTime)
                                    accumulatedLog.hoursDoubleHolidayOICOvertime += hoursHoliday
                                    regularHolidays.add(holidayDate)
                                } else {
                                    /*Single Holidays*/
                                    if (!eachHoliday.empty) {
                                        EventCalendar holiday = eachHoliday.first()
                                        EmployeeScheduleDto dummySchedule = new EmployeeScheduleDto()
                                        dummySchedule.dateTimeStartRaw = holiday.startDate
                                        dummySchedule.dateTimeEndRaw = holiday.endDate
                                        dummySchedule.mealBreakStart = currentSchedule.mealBreakStart
                                        dummySchedule.mealBreakEnd = currentSchedule.mealBreakEnd
                                        BigDecimal hoursHoliday = computeHours(dummySchedule, consideredInTime, consideredOutTime)

                                        if (holiday.holidayType == EventCalendarHolidayType.SPECIAL_NON_WORKING.toString()) {
                                            //calculate hours for special holiday
                                            accumulatedLog.hoursSpecialHolidayOICOvertime += hoursHoliday
                                            regularHolidays.add(holidayDate)
                                            String a = "asdfa"
                                        } else if (holiday.holidayType == EventCalendarHolidayType.REGULAR.toString()) {
                                            //calculate hours for regular holiday
                                            accumulatedLog.hoursRegularHolidayOICOvertime += hoursHoliday
                                            regularHolidays.add(holidayDate)
                                            String a = "asdfa"
                                        }
                                    }
                                }
                            }
                        }
                        days = days.findAll { regularHolidays.indexOf(it) < 0 }
                    }

                }
            }
        }

        return accumulatedLog

    }

    // used to calculate overtime hours
    // this will calculate the employee payslip based on the given date
    EmployeeAccumulatedAttendanceDto calculateOvertimeAccumulatedLogs(
            Instant date,
            Map<String, Object> employeeSchedule,
            Map<String, Object> employeeRestDaySchedule,
            Map<String, List<EventCalendar>> holidays,
            Map<String, List<Map<String, Instant>>> employeeLogs
    ) {
        EmployeeAccumulatedAttendanceDto accumulatedLog = new EmployeeAccumulatedAttendanceDto()
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd").withZone(ZoneId.systemDefault())
        String formattedDate = formatter.format(date)
        String overtimeFormattedDate = "${formatter.format(date)}_OVERTIME"
        String restFormattedDate = formattedDate + "_REST"

        List<EmployeeScheduleDto> schedule = employeeSchedule.get(overtimeFormattedDate) as List<EmployeeScheduleDto>
        List<Map<String, Instant>> logs = employeeLogs.get(formattedDate)

        EmployeeScheduleDto restSchedule = employeeRestDaySchedule.get(restFormattedDate) as EmployeeScheduleDto

        // do this only if there is a schedule
        schedule.each { EmployeeScheduleDto currentSchedule ->
            if (!logs) {
                //check next day logs
                Instant nextDayDate = date.plus(1, ChronoUnit.DAYS)
                String formattedNextDayDate = formatter.format(nextDayDate)
                logs = employeeLogs.get(formattedNextDayDate)
                if (!logs) {
                    // no logs found even the next day hence counted as absent
                    // employeePayslip.hoursAbsent = Duration.between(schedule.dateTimeStartRaw, schedule.dateTimeEndRaw).toMinutes() / 60
                    return accumulatedLog
                }
            }

            Integer matchedLogs = 0
            for (it in logs) {
                Instant inTime = it.get("IN") as Instant
                Instant outTime = it.get("OUT") as Instant
                Triple<EmployeeAccumulatedAttendanceDto, Instant, Instant> computedHoursTriple = computeHours(currentSchedule, it)
                EmployeeAccumulatedAttendanceDto computedHours = computedHoursTriple.left
                if (computedHours.worked > 0) {
                    matchedLogs += 1

                    // employeePayslip.hoursLate += computedHours.late
                    // employeePayslip.hoursUnderTime += computedHours.undertime
                    Instant consideredInTime = computedHoursTriple.middle
                    Instant consideredOutTime = computedHoursTriple.right

                    accumulatedLog.hoursRegularOvertime += computedHours.worked
                    if (currentSchedule.isRestDay)
                        accumulatedLog.hoursRestOvertime += computedHours.worked

                    accumulatedLog.date = date
                    accumulatedLog.inTime = it.get("IN")
                    accumulatedLog.outTime = it.get("OUT")

                    //holidays
                    //check with holiday
                    DateTimeFormatter holidayFormatter = DateTimeFormatter.ofPattern("MM_dd_yyyy").withZone(ZoneId.systemDefault())
                    String holidayFormattedDate = holidayFormatter.format(date)
                    List<EventCalendar> currentHolidays = holidays.get(holidayFormattedDate)
                    Boolean HAS_ADDED_HOURS = false
                    List<Instant> days = daysScheduleHasPass(consideredInTime, consideredOutTime)

                    /*Regular Holidays*/
                    if (currentSchedule.withHoliday) {

                        List<Instant> regularHolidays = []
                        days.each { Instant holidayDate ->
                            String currentHolidayFormattedDate = holidayFormatter.format(holidayDate)
                            List<EventCalendar> eachHoliday = holidays.get(currentHolidayFormattedDate)
                            if (eachHoliday) {
                                if (eachHoliday.size() > 1) {
                                    /*BigDecimal Holidays*/
                                    EventCalendar holiday = eachHoliday.first()
                                    EmployeeScheduleDto dummySchedule = new EmployeeScheduleDto()
                                    dummySchedule.dateTimeStartRaw = holiday.startDate
                                    dummySchedule.dateTimeEndRaw = holiday.endDate
                                    dummySchedule.mealBreakStart = currentSchedule.mealBreakStart
                                    dummySchedule.mealBreakEnd = currentSchedule.mealBreakEnd

                                    BigDecimal hoursHoliday = computeHours(dummySchedule, consideredInTime, consideredOutTime)
                                    if (currentSchedule.isRestDay)
                                        accumulatedLog.hoursDoubleHolidayAndRestDayOvertime += hoursHoliday

                                    accumulatedLog.hoursDoubleHolidayOvertime += hoursHoliday
//                                accumulatedLog.hoursRegularOvertime -= hoursHoliday
                                    regularHolidays.add(holidayDate)
                                } else {
                                    /*Single Holidays*/
                                    if (!eachHoliday.empty) {
                                        EventCalendar holiday = eachHoliday.first()
                                        EmployeeScheduleDto dummySchedule = new EmployeeScheduleDto()
                                        dummySchedule.dateTimeStartRaw = holiday.startDate
                                        dummySchedule.dateTimeEndRaw = holiday.endDate
                                        dummySchedule.mealBreakStart = currentSchedule.mealBreakStart
                                        dummySchedule.mealBreakEnd = currentSchedule.mealBreakEnd
                                        BigDecimal hoursHoliday = computeHours(dummySchedule, consideredInTime, consideredOutTime)

                                        if (holiday.holidayType == EventCalendarHolidayType.SPECIAL_NON_WORKING.toString()) {
                                            //calculate hours for special holiday
                                            if (currentSchedule.isRestDay)
                                                accumulatedLog.hoursSpecialHolidayAndRestDayOvertime += hoursHoliday
                                            accumulatedLog.hoursSpecialHolidayOvertime += hoursHoliday
                                            regularHolidays.add(holidayDate)
                                            String a = "asdfa"
                                        } else if (holiday.holidayType == EventCalendarHolidayType.REGULAR.toString()) {
                                            //calculate hours for regular holiday
                                            if (currentSchedule.isRestDay)
                                                accumulatedLog.hoursRegularHolidayAndRestDayOvertime += hoursHoliday
                                            accumulatedLog.hoursRegularHolidayOvertime += hoursHoliday
//                                        accumulatedLog.hoursRegularOvertime -= hoursHoliday
                                            regularHolidays.add(holidayDate)
                                            String a = "asdfa"
                                        }
                                    }
                                }
                            }
                        }
                        days = days.findAll { regularHolidays.indexOf(it) < 0 }
                    }

                }
            }
        }

        return accumulatedLog

    }

    EmployeeAccumulatedAttendanceDto calculateLeaveAccumulatedLogs(
            Instant date,
            Map<String, Object> employeeSchedule
    ) {
        BigDecimal HOUR_COMPUTATION = 60 * 60
        EmployeeAccumulatedAttendanceDto accumulatedLog = new EmployeeAccumulatedAttendanceDto()
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd").withZone(ZoneId.systemDefault())
        String formattedDate = formatter.format(date)
        String leaveFormattedDate = "${formatter.format(date)}_LEAVE"

        EmployeeScheduleDto schedule = employeeSchedule.get(leaveFormattedDate) as EmployeeScheduleDto

        if (schedule) {
            BigDecimal hours = Duration.between(schedule.dateTimeStartRaw, schedule.dateTimeEndRaw).seconds / HOUR_COMPUTATION
            accumulatedLog.isLeave = true
            if (schedule.withPay)
                accumulatedLog.worked = hours
            else accumulatedLog.hoursAbsent = hours
            accumulatedLog.withNSD = schedule.withNSD
            return accumulatedLog
        } else return accumulatedLog
    }


    // TODO: We should compute in both morning and evening night shift differentials and just add them.
    BigDecimal computeNightShiftDifferential(EmployeeScheduleDto schedule, Instant inTime, Instant outTime, Instant date) {
        BigDecimal HOUR_COMPUTATION = 60 * 60
        Instant mealBreakStart = schedule.mealBreakStart
        Instant mealBreakEnd = schedule.mealBreakEnd
        BigDecimal nightShiftDifferentialHours = 0

        def schedules = nightShiftDifferentialScheduleExtractor(schedule, date)
        List<Pair<Instant, Instant>> consideredSchedules = []
        schedules.each {
            def consideredTimes = consideredTimesExtractor(inTime, outTime, it.left, it.right)
            if (consideredTimes != null) {
                consideredSchedules.add(consideredTimes)
            }
        }

        consideredSchedules.each {
            Instant consideredIn = it.left
            Instant consideredOut = it.right

            // check if the considered timestamps is between meal break
            if (mealBreakStart && mealBreakEnd && mealBreakStart < mealBreakEnd) {
                BigDecimal mealBreakDuration = Duration.between(mealBreakStart, mealBreakEnd).getSeconds() / HOUR_COMPUTATION
                if ((consideredOut.isAfter(mealBreakStart) || consideredOut == mealBreakStart)
                        && (consideredOut.isBefore(mealBreakEnd) || consideredOut == mealBreakEnd)
                ) {
                    nightShiftDifferentialHours = Duration.between(consideredIn, mealBreakStart).getSeconds() / HOUR_COMPUTATION
                } else if ((consideredIn.isAfter(mealBreakStart) || consideredIn == mealBreakStart)
                        && (consideredIn.isBefore(mealBreakEnd) || consideredIn == mealBreakEnd)
                ) {
                    nightShiftDifferentialHours = Duration.between(mealBreakEnd, consideredOut).getSeconds() / HOUR_COMPUTATION
                } else if (consideredOut.isBefore(mealBreakStart)) {
                    nightShiftDifferentialHours = Duration.between(consideredIn, consideredOut).getSeconds() / HOUR_COMPUTATION
                } else {
                    nightShiftDifferentialHours = Duration.between(consideredIn, consideredOut).getSeconds() / HOUR_COMPUTATION
                    nightShiftDifferentialHours -= mealBreakDuration
                }
            } else {
                // we will go with the normal computation
                nightShiftDifferentialHours += Duration.between(consideredIn, consideredOut).getSeconds() / HOUR_COMPUTATION
            }
        }


        if (nightShiftDifferentialHours < 0) return 0
        return nightShiftDifferentialHours
    }

//====================================TIME KEEPING====================================\\


//====================================UTILITY METHODS====================================\\


    /**
     * This method is used to get the night shift differential start and end. We need
     * to determine the right start and end of the night shift differential because
     * the schedule might start or end at the middle.
     *
     * @param schedule the schedule that will be used to compare
     * @param date the date that is used to compare to the schedule, this might
     *                    be from the loop of {@link #getAccumulatedLogs GetAccumulatedLogs}, {@link #getEmployeePerformanceSummary GetEmployeePerformanceSummary}.
     * @return a tuple of instants, the left is the start and right is end.
     */
    List<Pair<Instant, Instant>> nightShiftDifferentialScheduleExtractor(EmployeeScheduleDto schedule, Instant date) {
        Instant nightShiftDifferentialStart = date.atZone(ZoneId.systemDefault())
                .with(ChronoField.HOUR_OF_DAY, 22)
                .with(ChronoField.MINUTE_OF_HOUR, 0)
                .with(ChronoField.SECOND_OF_MINUTE, 0)
                .with(ChronoField.MILLI_OF_SECOND, 0)
                .toInstant()
        Instant nightShiftDifferentialEnd = date.atZone(ZoneId.systemDefault())
                .with(ChronoField.HOUR_OF_DAY, 6)
                .with(ChronoField.MINUTE_OF_HOUR, 0)
                .with(ChronoField.SECOND_OF_MINUTE, 0)
                .with(ChronoField.MILLI_OF_SECOND, 0)
                .toInstant()
                .plus(1, ChronoUnit.DAYS)
        Instant nightShiftDifferentialStartAM = date.atZone(ZoneId.systemDefault())
                .with(ChronoField.HOUR_OF_DAY, 0)
                .with(ChronoField.MINUTE_OF_HOUR, 0)
                .with(ChronoField.SECOND_OF_MINUTE, 0)
                .with(ChronoField.MILLI_OF_SECOND, 0)
                .toInstant()
        Instant nightShiftDifferentialEndAM = date.atZone(ZoneId.systemDefault())
                .with(ChronoField.HOUR_OF_DAY, 6)
                .with(ChronoField.MINUTE_OF_HOUR, 0)
                .with(ChronoField.SECOND_OF_MINUTE, 0)
                .with(ChronoField.MILLI_OF_SECOND, 0)
                .toInstant()
        Instant scheduleStart = schedule.dateTimeStartRaw
        Instant scheduleEnd = schedule.dateTimeEndRaw
        Instant consideredStart, consideredEnd, consideredAMStart, consideredAMEnd

        List<ImmutablePair<Instant, Instant>> consideredSchedules = []

        if (scheduleStart < nightShiftDifferentialEnd && nightShiftDifferentialStart < scheduleEnd) {
            // regular considered start defining
            if (nightShiftDifferentialStart >= scheduleStart) {
                consideredStart = nightShiftDifferentialStart
            } else consideredStart = scheduleStart

            // regular considered end defining
            if (nightShiftDifferentialEnd <= scheduleEnd) {
                consideredEnd = nightShiftDifferentialEnd
            } else consideredEnd = scheduleEnd
        }

        if (scheduleStart < nightShiftDifferentialEndAM && nightShiftDifferentialStartAM < scheduleEnd) {
            // morning considered start defining
            if (nightShiftDifferentialStartAM >= scheduleStart) {
                consideredAMStart = nightShiftDifferentialStartAM
            } else consideredAMStart = scheduleStart

            // morning considered end defining
            if (nightShiftDifferentialEndAM <= scheduleEnd) {
                consideredAMEnd = nightShiftDifferentialEndAM
            } else consideredAMEnd = scheduleEnd
        }

        if (consideredStart && consideredEnd) {
            if (consideredStart < consideredEnd)
                consideredSchedules.add(new ImmutablePair<Instant, Instant>(consideredStart, consideredEnd))
        }

        if (consideredAMStart && consideredAMEnd) {
            if (consideredAMStart < consideredAMEnd)
                consideredSchedules.add(new ImmutablePair<Instant, Instant>(consideredAMStart, consideredAMEnd))
        }

        return consideredSchedules
    }

    /**
     * This method is used to get the considered in and outs. This is
     * necessary because the employee might be late or has gone under time.
     *
     * @param inTime this could be the start of a schedule.
     * @param outTime this could be the end of the schedule
     * @param scheduleStart this could be the the time employee has logged-in in the biometrics.
     * @param scheduleEnd this could be the the time employee has logged-out in the biometrics
     * @return a tuple of instants, the left is the considered in and right is considered out.
     */
    Pair<Instant, Instant> consideredTimesExtractor(Instant inTime, Instant outTime, Instant scheduleStart, Instant scheduleEnd) {
        Instant consideredIn, consideredOut
        if ((inTime.isBefore(outTime)) && inTime.isBefore(scheduleEnd)) {
            if (inTime.isBefore(scheduleStart)) {
                consideredIn = scheduleStart
            } else consideredIn = inTime
        }

        if (outTime.isAfter(inTime) && outTime.isAfter(scheduleStart)) {
            if (outTime.isBefore(scheduleEnd)) {
                consideredOut = outTime
            } else consideredOut = scheduleEnd
        }

        if (consideredIn == null || consideredOut == null) return null
        return new MutablePair<Instant, Instant>(consideredIn, consideredOut)
    }


    /**
     * This method will consolidate if the employee has timed out for meal break and timed in after the meal break.
     * Note: only use this after the computation and before you return the fully calculated accumulated log. This
     * is only to fix the miscalculation of under performances when employee does a meal break timed out and timed back
     * in again.
     * @param accumulatedLog — the computed accumulated log
     * @param logs — logs used in the computation
     * @param schedule — employee schedule
     * @return accumulatedLog — the adjusted accumulated log.
     */
    EmployeeAccumulatedAttendanceDto mealBreakConsolidateUnderperformances(
            EmployeeAccumulatedAttendanceDto accumulatedLog,
            List<Map<String, Instant>> logs,
            EmployeeScheduleDto schedule
    ) {

        // since the logs are sorted we can safely say that the first log is for the first half
        // while the second logs is for the second half
        Map<String, Instant> firstHalfLogs = logs[0]
        Map<String, Instant> secondHalfLogs = logs[1]

        EmployeeScheduleDto firstHalf = new EmployeeScheduleDto()
        firstHalf.dateTimeStartRaw = schedule.dateTimeStartRaw
        firstHalf.dateTimeEndRaw = schedule.mealBreakStart
        EmployeeScheduleDto secondHalf = new EmployeeScheduleDto()
        secondHalf.dateTimeStartRaw = schedule.mealBreakEnd
        secondHalf.dateTimeEndRaw = schedule.dateTimeEndRaw

        //compute first half
        EmployeeAccumulatedAttendanceDto firstHalfAccumulatedLog = computeHours(firstHalf, firstHalfLogs).left
        EmployeeAccumulatedAttendanceDto secondHalfAccumulatedLog = computeHours(secondHalf, secondHalfLogs).left

        //reset the late and undertime
        accumulatedLog.late = 0
        accumulatedLog.undertime = 0

        //add the first half underperformances
        accumulatedLog.late += firstHalfAccumulatedLog.late
        accumulatedLog.undertime += firstHalfAccumulatedLog.undertime
        //add the second half underperformances
        accumulatedLog.late += secondHalfAccumulatedLog.late
        accumulatedLog.undertime += secondHalfAccumulatedLog.undertime
        //set the new time-in and time-out
        accumulatedLog.inTime = firstHalfLogs.IN
        accumulatedLog.outTime = secondHalfLogs.OUT
        //set work hours
        BigDecimal newTotalWorked = firstHalfAccumulatedLog.worked + secondHalfAccumulatedLog.worked
        accumulatedLog.worked = newTotalWorked
        if (accumulatedLog.hoursRestDay > 0)
            accumulatedLog.hoursRestDay = newTotalWorked
        if (accumulatedLog.workedOIC > 0)
            accumulatedLog.workedOIC = newTotalWorked
        if (accumulatedLog.hoursSpecialHoliday > 0)
            accumulatedLog.hoursSpecialHoliday = newTotalWorked
        if (accumulatedLog.hoursSpecialHolidayOIC > 0)
            accumulatedLog.hoursSpecialHolidayOIC = newTotalWorked
        if (accumulatedLog.hoursSpecialHolidayAndRestDay > 0)
            accumulatedLog.hoursSpecialHolidayAndRestDay = newTotalWorked
        if (accumulatedLog.hoursRegularHoliday > 0)
            accumulatedLog.hoursRegularHoliday = newTotalWorked
        if (accumulatedLog.hoursRegularHolidayOIC > 0)
            accumulatedLog.hoursRegularHolidayOIC = newTotalWorked
        if (accumulatedLog.hoursRegularHolidayAndRestDay > 0)
            accumulatedLog.hoursRegularHolidayAndRestDay = newTotalWorked
        if (accumulatedLog.hoursDoubleHoliday > 0)
            accumulatedLog.hoursDoubleHoliday = newTotalWorked
        if (accumulatedLog.hoursDoubleHolidayOIC > 0)
            accumulatedLog.hoursDoubleHolidayOIC = newTotalWorked
        if (accumulatedLog.hoursDoubleHolidayAndRestDay > 0)
            accumulatedLog.hoursDoubleHolidayAndRestDay = newTotalWorked


        return accumulatedLog
    }

    EmployeeAccumulatedAttendanceDto mealBreakConsolidateOICUnderperformances(
            EmployeeAccumulatedAttendanceDto accumulatedLog,
            List<Map<String, Instant>> logs,
            EmployeeScheduleDto schedule
    ) {

        // since the logs are sorted we can safely say that the first log is for the first half
        // while the second logs is for the second half
        Map<String, Instant> firstHalfLogs = logs[0]
        Map<String, Instant> secondHalfLogs = logs[1]

        EmployeeScheduleDto firstHalf = new EmployeeScheduleDto()
        firstHalf.dateTimeStartRaw = schedule.dateTimeStartRaw
        firstHalf.dateTimeEndRaw = schedule.mealBreakStart
        EmployeeScheduleDto secondHalf = new EmployeeScheduleDto()
        secondHalf.dateTimeStartRaw = schedule.mealBreakEnd
        secondHalf.dateTimeEndRaw = schedule.dateTimeEndRaw

        //compute first half
        EmployeeAccumulatedAttendanceDto firstHalfAccumulatedLog = computeHours(firstHalf, firstHalfLogs).left
        EmployeeAccumulatedAttendanceDto secondHalfAccumulatedLog = computeHours(secondHalf, secondHalfLogs).left

        //reset the late and undertime
        accumulatedLog.late = 0
        accumulatedLog.undertime = 0

        //add the first half underperformances
        accumulatedLog.late += firstHalfAccumulatedLog.late
        accumulatedLog.undertime += firstHalfAccumulatedLog.undertime
        //add the second half underperformances
        accumulatedLog.late += secondHalfAccumulatedLog.late
        accumulatedLog.undertime += secondHalfAccumulatedLog.undertime
        //set the new time-in and time-out
        accumulatedLog.inTime = firstHalfLogs.IN
        accumulatedLog.outTime = secondHalfLogs.OUT
        //set work hours
        BigDecimal newTotalWorked = firstHalfAccumulatedLog.worked + secondHalfAccumulatedLog.worked
        accumulatedLog.workedOIC = newTotalWorked
        if (accumulatedLog.hoursSpecialHolidayOIC > 0)
            accumulatedLog.hoursSpecialHolidayOIC = newTotalWorked
        if (accumulatedLog.hoursRegularHolidayOIC > 0)
            accumulatedLog.hoursRegularHolidayOIC = newTotalWorked
        if (accumulatedLog.hoursDoubleHolidayOIC > 0)
            accumulatedLog.hoursDoubleHolidayOIC = newTotalWorked

        return accumulatedLog
    }

    Boolean willScheduleEndTheNextDay(EmployeeScheduleDto schedule) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd").withZone(ZoneId.systemDefault())
        String SCHEDULE_DATE_START = formatter.format(schedule.dateTimeStartRaw)
        String SCHEDULE_DATE_END = formatter.format(schedule.dateTimeEndRaw)
        Instant startDay = schedule.dateTimeStartRaw.atZone(ZoneId.systemDefault()).with(ChronoField.HOUR_OF_DAY, 0).toInstant()
        Instant endDate = schedule.dateTimeEndRaw.atZone(ZoneId.systemDefault()).with(ChronoField.HOUR_OF_DAY, 0).toInstant()
        Long duration = Duration.between(startDay, endDate).toDays()

        return duration > 0
    }

    Boolean willScheduleEndTheNextDay(Instant start, Instant end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd").withZone(ZoneId.systemDefault())
        Instant startDay = start.atZone(ZoneId.systemDefault()).with(ChronoField.HOUR_OF_DAY, 0).toInstant()
        Instant endDate = end.atZone(ZoneId.systemDefault()).with(ChronoField.HOUR_OF_DAY, 0).toInstant()
        Long duration = Duration.between(startDay, endDate).toDays()

        return duration > 0
    }

    List<Instant> daysScheduleHasPass(Instant start, Instant end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd").withZone(ZoneId.systemDefault())
        Instant startDay = start.atZone(ZoneId.systemDefault())
                .with(ChronoField.HOUR_OF_DAY, 0)
                .with(ChronoField.MINUTE_OF_HOUR, 0)
                .with(ChronoField.SECOND_OF_MINUTE, 0)
                .with(ChronoField.MILLI_OF_SECOND, 0)
                .toInstant()
        Instant endDate = end.atZone(ZoneId.systemDefault()).with(ChronoField.HOUR_OF_DAY, 0).toInstant()

        List<Instant> dates = []
        while (startDay <= endDate) {
            dates.add(startDay)
            startDay = startDay.plus(1, ChronoUnit.DAYS)
        }

        return dates
    }

    //used to deduct hours
    // e.g deduct nsd hours to worked hours
    EmployeeAccumulatedAttendanceDto reduceSchedule(EmployeeAccumulatedAttendanceDto schedule) {
        EmployeeAccumulatedAttendanceDto newSchedule = objectMapper.updateValue(schedule, EmployeeAccumulatedAttendanceDto)

        //==========================UNDER PERFORMANCE=========================\\
        newSchedule.late = schedule.late
        newSchedule.undertime = schedule.undertime
        newSchedule.hoursAbsent = schedule.hoursAbsent
        //==========================UNDER PERFORMANCE=========================\\
        //==============================WORKDAY==============================\\
        newSchedule.worked = schedule.worked - schedule.hoursWorkedNSD
        newSchedule.hoursRegularOvertime = schedule.hoursRegularOvertime
        newSchedule.hoursWorkedNSD = schedule.hoursWorkedNSD
        //==============================WORKDAY==============================\\
        //==============================OIC-WORKDAY==============================\\
        newSchedule.workedOIC = schedule.workedOIC - schedule.hoursWorkedOICNSD
        newSchedule.hoursRegularOICOvertime = schedule.hoursRegularOICOvertime
        newSchedule.hoursWorkedOICNSD = schedule.hoursWorkedOICNSD
        //==============================OIC-WORKDAY==============================\\
        //==============================REST DAY==============================\\
        newSchedule.hoursRestDay = schedule.hoursRestDay - schedule.hoursRestDayNSD
        newSchedule.hoursRestOvertime = schedule.hoursRestOvertime
        newSchedule.hoursRestDayNSD = schedule.hoursRestDayNSD
        //==============================REST DAY==============================\\
        //===========================SPECIAL HOLIDAY===========================\\
        newSchedule.hoursSpecialHoliday = schedule.hoursSpecialHoliday - schedule.hoursSpecialHolidayNSD
        newSchedule.hoursSpecialHolidayOvertime = schedule.hoursSpecialHolidayOvertime
        newSchedule.hoursSpecialHolidayNSD = schedule.hoursSpecialHolidayNSD
        //===========================SPECIAL HOLIDAY===========================\\
        //===========================SPECIAL HOLIDAY OIC===========================\\
        newSchedule.hoursSpecialHolidayOIC = schedule.hoursSpecialHolidayOIC - schedule.hoursSpecialHolidayOICNSD
        newSchedule.hoursSpecialHolidayOICOvertime = schedule.hoursSpecialHolidayOICOvertime
        newSchedule.hoursSpecialHolidayOICNSD = schedule.hoursSpecialHolidayOICNSD
        //===========================SPECIAL HOLIDAY OIC===========================\\
        //====================SPECIAL HOLIDAY AND REST DAY=====================\\
        newSchedule.hoursSpecialHolidayAndRestDay = schedule.hoursSpecialHolidayAndRestDay - schedule.hoursSpecialHolidayAndRestDayNSD
        newSchedule.hoursSpecialHolidayAndRestDayOvertime = schedule.hoursSpecialHolidayAndRestDayOvertime
        newSchedule.hoursSpecialHolidayAndRestDayNSD = schedule.hoursSpecialHolidayAndRestDayNSD
        //====================SPECIAL HOLIDAY AND REST DAY=====================\\
        //====================REGULAR HOLIDAY=====================\\
        newSchedule.hoursRegularHoliday = schedule.hoursRegularHoliday - schedule.hoursRegularHolidayNSD
        newSchedule.hoursRegularHolidayOvertime = schedule.hoursRegularHolidayOvertime
        newSchedule.hoursRegularHolidayNSD = schedule.hoursRegularHolidayNSD
        //====================REGULAR HOLIDAY=====================\\
        //====================REGULAR HOLIDAY OIC=====================\\
        newSchedule.hoursRegularHolidayOIC = schedule.hoursRegularHolidayOIC - schedule.hoursRegularHolidayOICNSD
        newSchedule.hoursRegularHolidayOICOvertime = schedule.hoursRegularHolidayOICOvertime
        newSchedule.hoursRegularHolidayOICNSD = schedule.hoursRegularHolidayOICNSD
        //====================REGULAR HOLIDAY OIC=====================\\
        //==============REGULAR HOLIDAY AND REST DAY===============\\
        newSchedule.hoursRegularHolidayAndRestDay = schedule.hoursRegularHolidayAndRestDay - schedule.hoursRegularHolidayAndRestDayNSD
        newSchedule.hoursRegularHolidayAndRestDayOvertime = schedule.hoursRegularHolidayAndRestDayOvertime
        newSchedule.hoursRegularHolidayAndRestDayNSD = schedule.hoursRegularHolidayAndRestDayNSD
        //==============REGULAR HOLIDAY AND REST DAY===============\\
        //=====================DOUBLE HOLIDAY======================\\
        newSchedule.hoursDoubleHoliday = schedule.hoursDoubleHoliday - schedule.hoursDoubleHolidayNSD
        newSchedule.hoursDoubleHolidayOvertime = schedule.hoursDoubleHolidayOvertime
        newSchedule.hoursDoubleHolidayNSD = schedule.hoursDoubleHolidayNSD
        //=====================DOUBLE HOLIDAY======================\\
        //=====================DOUBLE HOLIDAY OIC======================\\
        newSchedule.hoursDoubleHolidayOIC = schedule.hoursDoubleHolidayOIC - schedule.hoursDoubleHolidayOICNSD
        newSchedule.hoursDoubleHolidayOICOvertime = schedule.hoursDoubleHolidayOICOvertime
        newSchedule.hoursDoubleHolidayOICNSD = schedule.hoursDoubleHolidayOICNSD
        //=====================DOUBLE HOLIDAY OIC======================\\
        //===============DOUBLE HOLIDAY AND REST DAY================\\
        newSchedule.hoursDoubleHolidayAndRestDay = schedule.hoursDoubleHolidayAndRestDay - schedule.hoursDoubleHolidayAndRestDayNSD
        newSchedule.hoursDoubleHolidayAndRestDayOvertime = schedule.hoursDoubleHolidayAndRestDayOvertime
        newSchedule.hoursDoubleHolidayAndRestDayNSD = schedule.hoursDoubleHolidayAndRestDayNSD
        //===============DOUBLE HOLIDAY AND REST DAY================\\

        //==========================FINAL HOURS===========================\\


        newSchedule.hoursSpecialHoliday = newSchedule.hoursSpecialHoliday - (newSchedule.hoursSpecialHolidayOIC + newSchedule.hoursSpecialHolidayAndRestDay)
        newSchedule.hoursSpecialHolidayOvertime = newSchedule.hoursSpecialHolidayOvertime - (newSchedule.hoursSpecialHolidayOICOvertime + newSchedule.hoursSpecialHolidayAndRestDayOvertime)
        newSchedule.hoursSpecialHolidayNSD = newSchedule.hoursSpecialHolidayNSD - (newSchedule.hoursSpecialHolidayOICNSD + newSchedule.hoursSpecialHolidayAndRestDayNSD)

        newSchedule.hoursRegularHoliday = newSchedule.hoursRegularHoliday - (newSchedule.hoursRegularHolidayOIC + newSchedule.hoursRegularHolidayAndRestDay)
        newSchedule.hoursRegularHolidayOvertime = newSchedule.hoursRegularHolidayOvertime - (newSchedule.hoursRegularHolidayOICOvertime + newSchedule.hoursRegularHolidayAndRestDayOvertime)
        newSchedule.hoursRegularHolidayNSD = newSchedule.hoursRegularHolidayNSD - (newSchedule.hoursRegularHolidayOICNSD + newSchedule.hoursRegularHolidayAndRestDayNSD)

        newSchedule.hoursDoubleHoliday = newSchedule.hoursDoubleHoliday - (newSchedule.hoursDoubleHolidayOIC + newSchedule.hoursDoubleHolidayAndRestDay)
        newSchedule.hoursDoubleHolidayOvertime = newSchedule.hoursDoubleHolidayOvertime - (newSchedule.hoursDoubleHolidayOICOvertime + newSchedule.hoursDoubleHolidayAndRestDayOvertime)
        newSchedule.hoursDoubleHolidayNSD = newSchedule.hoursDoubleHolidayNSD - (newSchedule.hoursDoubleHolidayOICNSD + newSchedule.hoursDoubleHolidayAndRestDayNSD)


        newSchedule.workedOIC = newSchedule.workedOIC - (
                newSchedule.hoursSpecialHolidayOIC +
                        newSchedule.hoursRegularHolidayOIC +
                        newSchedule.hoursDoubleHolidayOIC
        )

        newSchedule.hoursRegularOICOvertime = newSchedule.hoursRegularOICOvertime - (
                newSchedule.hoursSpecialHolidayOICOvertime +
                        newSchedule.hoursRegularHolidayOICOvertime +
                        newSchedule.hoursDoubleHolidayOICOvertime
        )

        newSchedule.hoursWorkedOICNSD = newSchedule.hoursWorkedOICNSD - (
                newSchedule.hoursSpecialHolidayOICNSD +
                        newSchedule.hoursRegularHolidayOICNSD +
                        newSchedule.hoursDoubleHolidayOICNSD
        )

        newSchedule.hoursRestDay = newSchedule.hoursRestDay - (
                newSchedule.hoursSpecialHolidayAndRestDay +
                        newSchedule.hoursRegularHolidayAndRestDay +
                        newSchedule.hoursDoubleHolidayAndRestDay
        )

        newSchedule.hoursRestOvertime = newSchedule.hoursRestOvertime - (
                newSchedule.hoursSpecialHolidayAndRestDayOvertime +
                        newSchedule.hoursRegularHolidayAndRestDayOvertime +
                        newSchedule.hoursDoubleHolidayAndRestDayOvertime
        )

        newSchedule.hoursRestDayNSD = newSchedule.hoursRestDayNSD - (
                newSchedule.hoursSpecialHolidayAndRestDayNSD +
                        newSchedule.hoursRegularHolidayAndRestDayNSD +
                        newSchedule.hoursDoubleHolidayAndRestDayNSD
        )

        newSchedule.worked = newSchedule.worked - (
                newSchedule.hoursRestDay +
                        newSchedule.hoursSpecialHoliday +
                        newSchedule.hoursSpecialHolidayAndRestDay +
                        newSchedule.hoursRegularHoliday +
                        newSchedule.hoursRegularHolidayAndRestDay +
                        newSchedule.hoursDoubleHoliday +
                        newSchedule.hoursDoubleHolidayAndRestDay +
                        newSchedule.workedOIC +
                        newSchedule.hoursSpecialHolidayOIC +
                        newSchedule.hoursRegularHolidayOIC +
                        newSchedule.hoursDoubleHolidayOIC
        )

        newSchedule.hoursRegularOvertime = newSchedule.hoursRegularOvertime - (
                newSchedule.hoursRestOvertime +
                        newSchedule.hoursSpecialHolidayOvertime +
                        newSchedule.hoursSpecialHolidayAndRestDayOvertime +
                        newSchedule.hoursRegularHolidayOvertime +
                        newSchedule.hoursRegularHolidayAndRestDayOvertime +
                        newSchedule.hoursDoubleHolidayOvertime +
                        newSchedule.hoursDoubleHolidayAndRestDayOvertime +
                        newSchedule.hoursRegularOICOvertime +
                        newSchedule.hoursSpecialHolidayOICOvertime +
                        newSchedule.hoursRegularHolidayOICOvertime +
                        newSchedule.hoursDoubleHolidayOICOvertime
        )

        newSchedule.hoursWorkedNSD = newSchedule.hoursWorkedNSD - (
                newSchedule.hoursRestDayNSD +
                        newSchedule.hoursSpecialHolidayNSD +
                        newSchedule.hoursSpecialHolidayAndRestDayNSD +
                        newSchedule.hoursRegularHolidayNSD +
                        newSchedule.hoursRegularHolidayAndRestDayNSD +
                        newSchedule.hoursDoubleHolidayNSD +
                        newSchedule.hoursDoubleHolidayAndRestDayNSD +
                        newSchedule.hoursWorkedOICNSD +
                        newSchedule.hoursSpecialHolidayOICNSD +
                        newSchedule.hoursRegularHolidayOICNSD +
                        newSchedule.hoursDoubleHolidayOICNSD
        )


        //==========================FINAL HOURS===========================\\


        return newSchedule
    }

    EmployeeAccumulatedAttendanceDto addAccumulatedLogs(EmployeeAccumulatedAttendanceDto firstLog, EmployeeAccumulatedAttendanceDto secondLog) {
        EmployeeAccumulatedAttendanceDto finalLog = firstLog

        //==========================UNDER PERFORMANCE=========================\\
        finalLog.late += secondLog.late
        finalLog.undertime += secondLog.undertime
        finalLog.hoursAbsent += secondLog.hoursAbsent
        finalLog.countLate += secondLog.countLate
        finalLog.countUndertime += secondLog.countUndertime
        finalLog.countAbsent += secondLog.countAbsent
        //==========================UNDER PERFORMANCE=========================\\

        //==============================WORKDAY==============================\\
        finalLog.worked += secondLog.worked
        finalLog.hoursRegularOvertime += secondLog.hoursRegularOvertime
        finalLog.hoursWorkedNSD += secondLog.hoursWorkedNSD
        finalLog.countWorked += secondLog.countWorked
        finalLog.countOvertime += secondLog.countOvertime
        finalLog.countWorkedNSD += secondLog.countWorkedNSD
        //==============================WORKDAY==============================\\

        //==============================OIC-WORKDAY==============================\\
        finalLog.workedOIC += secondLog.workedOIC
        finalLog.hoursRegularOICOvertime += secondLog.hoursRegularOICOvertime
        finalLog.hoursWorkedOICNSD += secondLog.hoursWorkedOICNSD
        finalLog.countWorkedOIC += secondLog.countWorkedOIC
        finalLog.countOICOvertime += secondLog.countOICOvertime
        finalLog.countWorkedOICNSD += secondLog.countWorkedOICNSD
        //==============================OIC-WORKDAY==============================\\

        //==============================REST DAY==============================\\
        finalLog.hoursRestDay += secondLog.hoursRestDay
        finalLog.hoursRestOvertime += secondLog.hoursRestOvertime
        finalLog.hoursRestDayNSD += secondLog.hoursRestDayNSD
        finalLog.countRestDayWorked += secondLog.countRestDayWorked
        finalLog.countRestOvertime += secondLog.countRestOvertime
        finalLog.countRestDayNSD += secondLog.countRestDayNSD
        //==============================REST DAY==============================\\

        //===========================SPECIAL HOLIDAY===========================\\
        finalLog.hoursSpecialHoliday += secondLog.hoursSpecialHoliday
        finalLog.hoursSpecialHolidayOvertime += secondLog.hoursSpecialHolidayOvertime
        finalLog.hoursSpecialHolidayNSD += secondLog.hoursSpecialHolidayNSD
        finalLog.countSpecialHoliday += secondLog.countSpecialHoliday
        finalLog.countSpecialHolidayOvertime += secondLog.countSpecialHolidayOvertime
        finalLog.countSpecialHolidayNSD += secondLog.countSpecialHolidayNSD
        //===========================SPECIAL HOLIDAY===========================\\

        //===========================SPECIAL HOLIDAY OIC===========================\\
        finalLog.hoursSpecialHolidayOIC += secondLog.hoursSpecialHolidayOIC
        finalLog.hoursSpecialHolidayOICOvertime += secondLog.hoursSpecialHolidayOICOvertime
        finalLog.hoursSpecialHolidayOICNSD += secondLog.hoursSpecialHolidayOICNSD
        finalLog.countSpecialHolidayOIC += secondLog.countSpecialHolidayOIC
        finalLog.countSpecialHolidayOICOvertime += secondLog.countSpecialHolidayOICOvertime
        finalLog.countSpecialHolidayOICNSD += secondLog.countSpecialHolidayOICNSD
        //===========================SPECIAL HOLIDAY OIC===========================\\

        //====================SPECIAL HOLIDAY AND REST DAY=====================\\
        finalLog.hoursSpecialHolidayAndRestDay += secondLog.hoursSpecialHolidayAndRestDay
        finalLog.hoursSpecialHolidayAndRestDayOvertime += secondLog.hoursSpecialHolidayAndRestDayOvertime
        finalLog.hoursSpecialHolidayAndRestDayNSD += secondLog.hoursSpecialHolidayAndRestDayNSD
        finalLog.countSpecialHolidayAndRestDay += secondLog.countSpecialHolidayAndRestDay
        finalLog.countSpecialHolidayAndRestDayOvertime += secondLog.countSpecialHolidayAndRestDayOvertime
        finalLog.countSpecialHolidayAndRestDayNSD += secondLog.countSpecialHolidayAndRestDayNSD
        //====================SPECIAL HOLIDAY AND REST DAY=====================\\

        //====================REGULAR HOLIDAY=====================\\
        finalLog.hoursRegularHoliday += secondLog.hoursRegularHoliday
        finalLog.hoursRegularHolidayOvertime += secondLog.hoursRegularHolidayOvertime
        finalLog.hoursRegularHolidayNSD += secondLog.hoursRegularHolidayNSD
        finalLog.countRegularHoliday += secondLog.countRegularHoliday
        finalLog.countRegularHolidayOvertime += secondLog.countRegularHolidayOvertime
        finalLog.countRegularHolidayNSD += secondLog.countRegularHolidayNSD
        //====================REGULAR HOLIDAY=====================\\

        //====================REGULAR HOLIDAY OIC=====================\\
        finalLog.hoursRegularHolidayOIC += secondLog.hoursRegularHolidayOIC
        finalLog.hoursRegularHolidayOICOvertime += secondLog.hoursRegularHolidayOICOvertime
        finalLog.hoursRegularHolidayOICNSD += secondLog.hoursRegularHolidayOICNSD
        finalLog.countRegularHolidayOIC += secondLog.countRegularHolidayOIC
        finalLog.countRegularHolidayOICOvertime += secondLog.countRegularHolidayOICOvertime
        finalLog.countRegularHolidayOICNSD += secondLog.countRegularHolidayOICNSD
        //====================REGULAR HOLIDAY OIC=====================\\

        //==============REGULAR HOLIDAY AND REST DAY===============\\
        finalLog.hoursRegularHolidayAndRestDay += secondLog.hoursRegularHolidayAndRestDay
        finalLog.hoursRegularHolidayAndRestDayOvertime += secondLog.hoursRegularHolidayAndRestDayOvertime
        finalLog.hoursRegularHolidayAndRestDayNSD += secondLog.hoursRegularHolidayAndRestDayNSD
        finalLog.countRegularHolidayAndRestDay += secondLog.countRegularHolidayAndRestDay
        finalLog.countRegularHolidayAndRestDayOvertime += secondLog.countRegularHolidayAndRestDayOvertime
        finalLog.countRegularHolidayAndRestDayNSD += secondLog.countRegularHolidayAndRestDayNSD
        //==============REGULAR HOLIDAY AND REST DAY===============\\

        //=====================DOUBLE HOLIDAY======================\\
        finalLog.hoursDoubleHoliday += secondLog.hoursDoubleHoliday
        finalLog.hoursDoubleHolidayOvertime += secondLog.hoursDoubleHolidayOvertime
        finalLog.hoursDoubleHolidayNSD += secondLog.hoursDoubleHolidayNSD
        finalLog.countDoubleHoliday += secondLog.countDoubleHoliday
        finalLog.countDoubleHolidayOvertime += secondLog.countDoubleHolidayOvertime
        finalLog.countDoubleHolidayNSD += secondLog.countDoubleHolidayNSD
        //=====================DOUBLE HOLIDAY======================\\

        //=====================DOUBLE HOLIDAY OIC======================\\
        finalLog.hoursDoubleHolidayOIC += secondLog.hoursDoubleHolidayOIC
        finalLog.hoursDoubleHolidayOICOvertime += secondLog.hoursDoubleHolidayOICOvertime
        finalLog.hoursDoubleHolidayOICNSD += secondLog.hoursDoubleHolidayOICNSD
        finalLog.countDoubleHolidayOIC += secondLog.countDoubleHolidayOIC
        finalLog.countDoubleHolidayOICOvertime += secondLog.countDoubleHolidayOICOvertime
        finalLog.countDoubleHolidayOICNSD += secondLog.countDoubleHolidayOICNSD
        //=====================DOUBLE HOLIDAY OIC======================\\

        //===============DOUBLE HOLIDAY AND REST DAY================\\
        finalLog.hoursDoubleHolidayAndRestDay += secondLog.hoursDoubleHolidayAndRestDay
        finalLog.hoursDoubleHolidayAndRestDayOvertime += secondLog.hoursDoubleHolidayAndRestDayOvertime
        finalLog.hoursDoubleHolidayAndRestDayNSD += secondLog.hoursDoubleHolidayAndRestDayNSD
        finalLog.countDoubleHolidayAndRestDay += secondLog.countDoubleHolidayAndRestDay
        finalLog.countDoubleHolidayAndRestDayOvertime += secondLog.countDoubleHolidayAndRestDayOvertime
        finalLog.countDoubleHolidayAndRestDayNSD += secondLog.countDoubleHolidayAndRestDayNSD
        //===============DOUBLE HOLIDAY AND REST DAY================\\
        finalLog.countRestDay += secondLog.countRestDay

        if (firstLog.isError && !secondLog.isEmpty) {
            if (firstLog.message == "No Schedule") {
                finalLog.isError = false
                finalLog.message = null
            }
        } else if (!firstLog.isError && secondLog.isError && secondLog.isEmpty) {
            finalLog.isError = secondLog.isError
            finalLog.message = secondLog.message
        }

        if (secondLog.isLeave) {
            finalLog.isLeave = firstLog.isLeave || secondLog.isLeave
            if (finalLog.isError && finalLog.message == "No Schedule") {
                if (secondLog.hoursAbsent > 0 || secondLog.worked > 0) {
                    finalLog.message = null
                    finalLog.isError = false
                }
            }
        }
        return finalLog
    }

    EmployeeAccumulatedAttendanceDto calculateAccumulatedLogsCount(EmployeeAccumulatedAttendanceDto log) {

        //==========================UNDER PERFORMANCE=========================\\
        if (log.late > 0) log.countLate += 1
        if (log.undertime > 0) log.countUndertime += 1
        if (log.hoursAbsent > 0) log.countAbsent += 1
        //==========================UNDER PERFORMANCE=========================\\
        //==============================WORKDAY==============================\\
        if (log.worked > 0) log.countWorked += 1
        if (log.hoursRegularOvertime > 0) log.countOvertime += 1
        if (log.hoursWorkedNSD > 0) log.countWorkedNSD += 1
        //==============================WORKDAY==============================\\
        //==============================OIC-WORKDAY==============================\\
        if (log.workedOIC > 0) log.countWorkedOIC += 1
        if (log.hoursRegularOICOvertime > 0) log.countOICOvertime += 1
        if (log.hoursWorkedOICNSD > 0) log.countWorkedOICNSD += 1
        //==============================OIC-WORKDAY==============================\\
        //==============================REST DAY==============================\\
        if (log.hoursRestDay > 0) log.countRestDayWorked += 1
        if (log.hoursRestOvertime > 0) log.countRestOvertime += 1
        if (log.hoursRestDayNSD > 0) log.countRestDayNSD += 1
        //==============================REST DAY==============================\\
        //===========================SPECIAL HOLIDAY===========================\\
        if (log.hoursSpecialHoliday > 0) log.countSpecialHoliday += 1
        if (log.hoursSpecialHolidayOvertime > 0) log.countSpecialHolidayOvertime += 1
        if (log.hoursSpecialHolidayNSD > 0) log.countSpecialHolidayNSD += 1
        //===========================SPECIAL HOLIDAY===========================\\
        //===========================SPECIAL HOLIDAY OIC===========================\\
        if (log.hoursSpecialHolidayOIC > 0) log.countSpecialHolidayOIC += 1
        if (log.hoursSpecialHolidayOICOvertime > 0) log.countSpecialHolidayOICOvertime += 1
        if (log.hoursSpecialHolidayOICNSD > 0) log.countSpecialHolidayOICNSD += 1
        //===========================SPECIAL HOLIDAY OIC===========================\\
        //====================SPECIAL HOLIDAY AND REST DAY=====================\\
        if (log.hoursSpecialHolidayAndRestDay > 0) log.countSpecialHolidayAndRestDay += 1
        if (log.hoursSpecialHolidayAndRestDayOvertime > 0) log.countSpecialHolidayAndRestDayOvertime += 1
        if (log.hoursSpecialHolidayAndRestDayNSD > 0) log.countSpecialHolidayAndRestDayNSD += 1
        //====================SPECIAL HOLIDAY AND REST DAY=====================\\
        //====================REGULAR HOLIDAY=====================\\
        if (log.hoursRegularHoliday > 0) log.countRegularHoliday += 1
        if (log.hoursRegularHolidayOvertime > 0) log.countRegularHolidayOvertime += 1
        if (log.hoursRegularHolidayNSD > 0) log.countRegularHolidayNSD += 1
        //====================REGULAR HOLIDAY=====================\\
        //====================REGULAR HOLIDAY OIC=====================\\
        if (log.hoursRegularHolidayOIC > 0) log.countRegularHolidayOIC += 1
        if (log.hoursRegularHolidayOICOvertime > 0) log.countRegularHolidayOICOvertime += 1
        if (log.hoursRegularHolidayOICNSD > 0) log.countRegularHolidayOICNSD += 1
        //====================REGULAR HOLIDAY OIC=====================\\
        //==============REGULAR HOLIDAY AND REST DAY===============\\
        if (log.hoursRegularHolidayAndRestDay > 0) log.countRegularHolidayAndRestDay += 1
        if (log.hoursRegularHolidayAndRestDayOvertime > 0) log.countRegularHolidayAndRestDayOvertime += 1
        if (log.hoursRegularHolidayAndRestDayNSD > 0) log.countRegularHolidayAndRestDayNSD += 1
        //==============REGULAR HOLIDAY AND REST DAY===============\\
        //=====================DOUBLE HOLIDAY======================\\
        if (log.hoursDoubleHoliday > 0) log.countDoubleHoliday += 1
        if (log.hoursDoubleHolidayOvertime > 0) log.countDoubleHolidayOvertime += 1
        if (log.hoursDoubleHolidayNSD > 0) log.countDoubleHolidayNSD += 1
        //=====================DOUBLE HOLIDAY======================\\
        //=====================DOUBLE HOLIDAY OIC======================\\
        if (log.hoursDoubleHolidayOIC > 0) log.countDoubleHolidayOIC += 1
        if (log.hoursDoubleHolidayOICOvertime > 0) log.countDoubleHolidayOICOvertime += 1
        if (log.hoursDoubleHolidayOICNSD > 0) log.countDoubleHolidayOICNSD += 1
        //=====================DOUBLE HOLIDAY OIC======================\\
        //===============DOUBLE HOLIDAY AND REST DAY================\\
        if (log.hoursDoubleHolidayAndRestDay > 0) log.countDoubleHolidayAndRestDay += 1
        if (log.hoursDoubleHolidayAndRestDayOvertime > 0) log.countDoubleHolidayAndRestDayOvertime += 1
        if (log.hoursDoubleHolidayAndRestDayNSD > 0) log.countDoubleHolidayAndRestDayNSD += 1
        //===============DOUBLE HOLIDAY AND REST DAY================\\

        return log
    }

    Boolean checkIfLogIsValid(EmployeeAccumulatedAttendanceDto log) {

        if (log.inTime) return true
        else if (log.outTime) return true
        else if (log.message) return true
        else if (log.isError) return true
        else if (log.late > 0) return true
        else if (log.undertime > 0) return true
        else if (log.worked > 0) return true
        else if (log.hoursRegularOvertime > 0) return true
        else if (log.hoursRestOvertime > 0) return true
        else if (log.hoursSpecialHolidayOvertime > 0) return true
        else if (log.hoursSpecialHolidayAndRestDayOvertime > 0) return true
        else if (log.hoursRegularHolidayOvertime > 0) return true
        else if (log.hoursRegularHolidayAndRestDayOvertime > 0) return true
        else if (log.hoursDoubleHolidayOvertime > 0) return true
        else if (log.hoursDoubleHolidayAndRestDayOvertime > 0) return true
        else if (log.hoursRestDay > 0) return true
        else if (log.hoursSpecialHoliday > 0) return true
        else if (log.hoursSpecialHolidayAndRestDay > 0) return true
        else if (log.hoursRegularHoliday > 0) return true
        else if (log.hoursRegularHolidayAndRestDay > 0) return true
        else if (log.hoursDoubleHoliday > 0) return true
        else if (log.hoursDoubleHolidayAndRestDay > 0) return true
        else if (log.hoursNightDifferential > 0) return true
        else if (log.hoursAbsent > 0) return true

        return false
    }

//====================================UTILITY METHODS====================================\\


//===========================================DB Operations===========================================\\

    Map<String, Object> getSchedule(UUID id, Instant startDate, Instant endDate) {
        List<Map<String, Object>> scannedEmployeeSchedule = entityManager.createQuery("""
                Select 
                    es.dateTimeStartRaw as e_s_date_time_start_raw,
                    es.dateTimeEndRaw as e_s_date_time_end_raw,
                    es.isRestDay as e_s_is_rest_day,
                    es.isOvertime as e_s_is_overtime,
                    es.mealBreakStart,
                    es.mealBreakEnd,
                    es.isOIC as e_s_is_oic,
                    es.withNSD as e_s_with_nsd,
                    es.withHoliday as e_s_with_holiday,
                    es.withPay as e_s_with_pay,
                    es.isLeave as e_s_is_leave,
                    es.assignedDate as e_s_assigned_date
                from EmployeeSchedule es 
                left join es.employee e
                where 
                    (e.id = :id 
                    and es.dateTimeStartRaw >= :startDate 
                    and es.dateTimeStartRaw <= :endDate
                    and es.isOvertime != TRUE
                    and (es.isLeave != TRUE or es.isLeave is null)
                    and es.label != 'R'
                    and es.title != 'Rest Day'
                    and es.color != '#95a5a6')
                    or 
                        (es.isCustom = true and e.id = :id
                    and es.dateTimeStartRaw >= :startDate 
                    and es.dateTimeStartRaw <= :endDate)
                order by es.dateTimeStartRaw
            """).setParameter("id", id)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .unwrap(Query.class)
                .setResultTransformer(new EmployeeScheduleDtoTransformer())
                .getResultList()

        Map<String, Object> employeeSchedule = scannedEmployeeSchedule[0] as Map<String, Object>

        return employeeSchedule
    }

    Map<String, Object> getOvertimeSchedule(UUID id, Instant startDate, Instant endDate) {
        List<Map<String, Object>> scannedOvertimeSchedule = entityManager.createQuery("""
                Select 
                    es.dateTimeStartRaw as e_s_date_time_start_raw,
                    es.dateTimeEndRaw as e_s_date_time_end_raw,
                    es.isRestDay as e_s_is_rest_day,
                    es.isOvertime as e_s_is_overtime,
                    es.mealBreakStart,
                    es.mealBreakEnd,
                    es.isOIC as e_s_is_oic,
                    es.withNSD as e_s_with_nsd,
                    es.withHoliday as e_s_with_holiday,
                    es.withPay as e_s_with_pay,
                    es.isLeave as e_s_is_leave,
                    es.assignedDate as e_s_assigned_date
                from EmployeeSchedule es 
                left join es.employee e
                where 
                    e.id = :id 
                    and ((es.dateTimeStartRaw >= :startDate and es.dateTimeStartRaw <= :endDate)
                    or (es.assignedDate >= :startDate and es.assignedDate <= :endDate and es.isOvertime is true))
                    and es.isOvertime = TRUE
                order by es.dateTimeStartRaw
            """).setParameter("id", id)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .unwrap(Query.class)
                .setResultTransformer(new EmployeeScheduleDtoTransformer(true))
                .getResultList()

        Map<String, Object> employeeOvertimeSchedule = scannedOvertimeSchedule[0] as Map<String, Object>
        return employeeOvertimeSchedule
    }

    Map<String, Object> getOvertimeOICSchedule(UUID id, Instant startDate, Instant endDate) {
        List<Map<String, Object>> scannedOvertimeSchedule = entityManager.createQuery("""
                Select 
                    es.dateTimeStartRaw as e_s_date_time_start_raw,
                    es.dateTimeEndRaw as e_s_date_time_end_raw,
                    es.isRestDay as e_s_is_rest_day,
                    es.isOvertime as e_s_is_overtime,
                    es.mealBreakStart,
                    es.mealBreakEnd,
                    es.isOIC as e_s_is_oic,
                    es.withNSD as e_s_with_nsd,
                    es.withHoliday as e_s_with_holiday,
                    es.withPay as e_s_with_pay,
                    es.isLeave as e_s_is_leave,
                    es.assignedDate as e_s_assigned_date
                from EmployeeSchedule es 
                left join es.employee e
                where 
                    e.id = :id 
                    and ((es.dateTimeStartRaw >= :startDate and es.dateTimeStartRaw <= :endDate)
                    or (es.assignedDate >= :startDate and es.assignedDate <= :endDate and es.isOvertime is true))
                    and es.isOvertime = TRUE
                    and es.isOIC = TRUE
                order by es.dateTimeStartRaw
            """).setParameter("id", id)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .unwrap(Query.class)
                .setResultTransformer(new EmployeeScheduleDtoTransformer())
                .getResultList()

        Map<String, Object> employeeOvertimeSchedule = scannedOvertimeSchedule[0] as Map<String, Object>
        return employeeOvertimeSchedule
    }

    Map<String, Object> getRestSchedule(UUID id, Instant startDate, Instant endDate) {
        endDate = endDate.plus(1, ChronoUnit.DAYS)
        List<Map<String, Object>> scannedRestSchedule = entityManager.createQuery("""
                Select 
                    es.dateTimeStartRaw as e_s_date_time_start_raw,
                    es.dateTimeEndRaw as e_s_date_time_end_raw,
                    es.isRestDay as e_s_is_rest_day,
                    es.isOvertime as e_s_is_overtime,
                    es.mealBreakStart,
                    es.mealBreakEnd,
                    es.isOIC as e_s_is_oic,
                    es.withNSD as e_s_with_nsd,
                    es.withHoliday as e_s_with_holiday,
                    es.withPay as e_s_with_pay,
                    es.isLeave as e_s_is_leave,
                    es.assignedDate as e_s_assigned_date
                from EmployeeSchedule es 
                left join es.employee e
                where 
                    e.id = :id 
                    and es.dateTimeStartRaw >= :startDate 
                    and es.dateTimeStartRaw <= :endDate
                    and es.isRestDay = TRUE
                    and es.label = 'R'
                    and es.title = 'Rest Day'
                    and es.color = '#95a5a6'
                order by es.dateTimeStartRaw
            """).setParameter("id", id)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .unwrap(Query.class)
                .setResultTransformer(new EmployeeScheduleDtoTransformer())
                .getResultList()

        Map<String, Object> employeeRestSchedule = scannedRestSchedule[0] as Map<String, Object>
        return employeeRestSchedule
    }

    Map<String, Object> getOICSchedule(UUID id, Instant startDate, Instant endDate) {
        endDate = endDate.plus(2, ChronoUnit.DAYS)
        List<Map<String, Object>> scannedOvertimeSchedule = entityManager.createQuery("""
                Select 
                    es.dateTimeStartRaw as e_s_date_time_start_raw,
                    es.dateTimeEndRaw as e_s_date_time_end_raw,
                    es.isRestDay as e_s_is_rest_day,
                    es.isOvertime as e_s_is_overtime,
                    es.mealBreakStart,
                    es.mealBreakEnd,
                    es.isOIC as e_s_is_oic,
                    es.withNSD as e_s_with_nsd,
                    es.withHoliday as e_s_with_holiday,
                    es.withPay as e_s_with_pay,
                    es.isLeave as e_s_is_leave,
                    es.assignedDate as e_s_assigned_date
                from EmployeeSchedule es 
                left join es.employee e
                where 
                    e.id = :id 
                    and es.dateTimeStartRaw >= :startDate 
                    and es.dateTimeStartRaw <= :endDate
                    and es.isOIC = TRUE
                order by es.dateTimeStartRaw
            """).setParameter("id", id)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .unwrap(Query.class)
                .setResultTransformer(new EmployeeScheduleDtoTransformer())
                .getResultList()

        Map<String, Object> employeeOvertimeSchedule = scannedOvertimeSchedule[0] as Map<String, Object>
        return employeeOvertimeSchedule
    }


    Map<String, Object> getLeaveSchedule(UUID id, Instant startDate, Instant endDate) {
        List<Map<String, Object>> scannedEmployeeSchedule = entityManager.createQuery("""
                Select 
                    es.dateTimeStartRaw as e_s_date_time_start_raw,
                    es.dateTimeEndRaw as e_s_date_time_end_raw,
                    es.isRestDay as e_s_is_rest_day,
                    es.isOvertime as e_s_is_overtime,
                    es.mealBreakStart,
                    es.mealBreakEnd,
                    es.isOIC as e_s_is_oic,
                    es.withNSD as e_s_with_nsd,
                    es.withHoliday as e_s_with_holiday,
                    es.withPay as e_s_with_pay,
                    es.isLeave as e_s_is_leave,
                    es.assignedDate as e_s_assigned_date
                from EmployeeSchedule es 
                left join es.employee e
                where 
                    e.id = :id 
                    and es.dateTimeStartRaw >= :startDate 
                    and es.dateTimeStartRaw <= :endDate
                    and es.isOvertime IS NOT TRUE
                    and es.isLeave IS TRUE
                    and (es.isRestDay IS NOT TRUE or es.isRestDay = null)
                    and es.label != 'R'
                    and es.title != 'Rest Day'
                    and es.color != '#95a5a6'
                    and es.request is not null
                order by es.dateTimeStartRaw
            """).setParameter("id", id)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .unwrap(Query.class)
                .setResultTransformer(new EmployeeScheduleDtoTransformer())
                .getResultList()

        Map<String, Object> employeeSchedule = scannedEmployeeSchedule[0] as Map<String, Object>

        return employeeSchedule
    }

    Map<String, List<EventCalendar>> getHolidays(Instant startDate, Instant endDate) {
        Instant addedEndDate = Instant.ofEpochSecond(endDate.epochSecond, endDate.nano).plus(5, ChronoUnit.DAYS)
        startDate = startDate.minus(2, ChronoUnit.DAYS)
        Map<String, List<EventCalendar>> holidays = entityManager.createQuery("""
            Select e from EventCalendar e
            where
                e.startDate >= :startDate and e.endDate <= :endDate
            order by e.startDate
        """).setParameter("startDate", startDate)
                .setParameter("endDate", addedEndDate)
                .getResultStream()
                .collect(Collectors.groupingBy({
                    EventCalendar d ->
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM_dd_yyyy").withZone(ZoneId.systemDefault())
                        String dateTime = formatter.format(d.startDate)
                        return dateTime
                })) as Map
        return holidays
    }

    Map<String, List<Map<String, Instant>>> getLogs(UUID id, Instant startDate, Instant endDate) {
        Instant addedEndDate = Instant.ofEpochSecond(endDate.epochSecond, endDate.nano).plus(5, ChronoUnit.DAYS)
        startDate = startDate.minus(5, ChronoUnit.DAYS)

        List<Map<String, List<Map<String, Instant>>>> scannedLogs = entityManager.createQuery("""
            Select ea.attendance_time, ea.type 
            from EmployeeAttendance ea
                left join ea.employee e
            where
                e.id = :id
                and 
                    ea.attendance_time >= :startDate and ea.attendance_time <= :plusOneDate
                    and (ea.isIgnored = false or ea.isIgnored is null)
                order by ea.attendance_time
        """).setHint(QueryHints.HINT_READONLY, true)
                .setParameter("id", id)
                .setParameter("startDate", startDate)
                .setParameter("plusOneDate", addedEndDate)
                .unwrap(Query.class)
                .setResultTransformer(new EmployeeAttendanceDtoTransformer()).getResultList()

        Map<String, List<Map<String, Instant>>> logs = scannedLogs.first()
        return logs
    }

    //===========================================DB Operations===========================================\\


}
