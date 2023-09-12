package com.hisd3.hismk2.rest.hrm

import com.hisd3.hismk2.domain.hrm.EmployeeAttendance
import com.hisd3.hismk2.domain.hrm.dto.EmployeeAccumulatedAttendanceDto
import com.hisd3.hismk2.domain.payroll.AccumulatedLogSummary
import com.hisd3.hismk2.domain.payroll.Totals
import com.hisd3.hismk2.repository.hrm.EmployeeAttendanceRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.services.PayrollTimeKeepingCalculatorService
import com.hisd3.hismk2.services.TimeKeepingCalculatorService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import java.time.Instant


@RestController
@RequestMapping("/hrm")
class EmployeeResource {
    @Autowired
    EmployeeRepository employeeRepository

    @Autowired
    TimeKeepingCalculatorService timeKeepingCalculatorService

    @Autowired
    EmployeeAttendanceRepository employeeAttendanceRepository

    @RequestMapping(value = "/getEmployeeByIdNumber", produces = ["application/json"])
    def getEmployeeByIdNumber(
            @RequestParam String id
    ) {
        return id ? employeeRepository.findByEmployeeId(id) : null
    }

    @RequestMapping(value = "/getEmployeeAccumulatedLogs", produces = ["application/json"])
    def getEmployeeAccumulatedLogs(
            @RequestParam UUID id,
            @RequestParam Instant startDate,
            @RequestParam Instant endDate
    ) {
        if (!id || !startDate || !endDate) {
            throw new RuntimeException("Failed to get accumulated logs.")
        }

        List<Totals> accumulatedLogs = []

        timeKeepingCalculatorService.getAccumulatedLogs(id, startDate, endDate).each {
            return accumulatedLogs.add(it.totals)
        }
//        List<AccumulatedLogSummary> accumulatedLogs = timeKeepingCalculatorService.getAccumulatedLogs(id, startDate, endDate)

        return accumulatedLogs
    }

    @RequestMapping(value = "/getEmployeePerformanceSummary", produces = ["application/json"])
    def getEmployeePerformanceSummary(
            @RequestParam UUID id,
            @RequestParam Instant startDate,
            @RequestParam Instant endDate

    ) {

        if (!id || !startDate || !endDate) throw new RuntimeException("Failed to get accumulated logs.")
        EmployeeAccumulatedAttendanceDto employeePerfSummary = timeKeepingCalculatorService.getEmployeePerformanceSummary(id, startDate, endDate)

        return employeePerfSummary
    }

    @RequestMapping(value = "/getEmployeeRawLogs", produces = ["application/json"])
    Page<EmployeeAttendance> getEmployeeRawLogs(
            @RequestParam Instant startDate,
            @RequestParam Instant endDate,
            @RequestParam UUID id,
            @RequestParam Integer page,
            @RequestParam Integer size
    ) {
        if (!startDate || !endDate || !id) throw new RuntimeException("Failed to get employee attendance.")
        return employeeAttendanceRepository.getEmployeeAttendanceExIgnored(id, startDate, endDate, new PageRequest(page, size, Sort.Direction.ASC, "attendance_time"))
    }
}
