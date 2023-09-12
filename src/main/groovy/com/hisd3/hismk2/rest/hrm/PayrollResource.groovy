package com.hisd3.hismk2.rest.hrm

import com.hisd3.hismk2.domain.payroll.Payroll
import com.hisd3.hismk2.domain.payroll.TimekeepingEmployee
import com.hisd3.hismk2.rest.hrm.dto.AccumulatedLogsTotalDTO
import com.hisd3.hismk2.services.ReportTabularGeneratorService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.hibernate.jpa.QueryHints
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import java.nio.charset.Charset

class RequestDto {
    List<UUID> timekeepingEmployeeIds
}


@RestController
@RequestMapping("/api/payroll")
class PayrollResource {

    @Autowired
    ReportTabularGeneratorService reportTabularGeneratorService


    @PersistenceContext
    EntityManager entityManager



    @PostMapping(value = "/{id}/timekeeping/csv")
    ResponseEntity<byte[]> getAllChartOfAccountGenerateDownload(
            @PathVariable("id") UUID id,
            @RequestBody RequestDto requestDto
    )

    {

        List<TimekeepingEmployee> records = entityManager.createQuery("""
            Select distinct te from TimekeepingEmployee te
                left join fetch te.accumulatedLogSummaryList 
                left join te.timekeeping t
                left join t.payroll p
                where te.id in :ids and p.id = :id
        """, TimekeepingEmployee.class)
                .setParameter("ids", requestDto.timekeepingEmployeeIds)
                .setParameter("id", id)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .resultList

        StringBuffer buffer = new StringBuffer()

        CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV
                .withHeader(
                        "employee-name",
                        "department",
                        "designation",
                        "id-no",
                        "basic-salary",
                        "undertime",
                        "late",
                        "hoursAbsent",
                        "worked",
                        "work-ot",
                        "work-nsd",
                        "oic",
                        "oic-ot",
                        "oic-nsd",
                        "rest-day",
                        "rest-day-nsd",
                        "rest-day-ot",
                        "special-holiday",
                        "special-holiday-ot",
                        "special-holiday-nsd",
                        "special-holiday-oic",
                        "special-holiday-oic-ot",
                        "special-holiday-oic-nsd",
                        "special-holiday-rest",
                        "special-holiday-rest-ot",
                        "special-holiday-rest-nsd",
                        "regular-holiday",
                        "regular-holiday-ot",
                        "regular-holiday-nsd",
                        "regular-holiday-oic",
                        "regular-holiday-oic-ot",
                        "regular-holiday-oic-nsd",
                        "regular-holiday-rest",
                        "regular-holiday-rest-ot",
                        "regular-holiday-rest-nsd",
                        "double-holiday",
                        "double-holiday-nsd",
                        "double-holiday-ot",
                        "double-holiday-oic",
                        "double-holiday-oic-nsd",
                        "double-holiday-oic-ot",
                        "double-holiday-rest",
                        "double-holiday-rest-ot",
                        "double-holiday-rest-nsd",
                ))

        records.each {
            AccumulatedLogsTotalDTO summaryDto = new AccumulatedLogsTotalDTO()
            it.accumulatedLogSummaryList.each {
                summaryDto.undertime += it.totals.undertime
                summaryDto.late += it.totals.late
                summaryDto.hoursAbsent += it.totals.hoursAbsent
                summaryDto.worked += it.totals.worked
                summaryDto.hoursRegularOvertime += it.totals.hoursRegularOvertime
                summaryDto.hoursWorkedNSD += it.totals.hoursWorkedNSD
                summaryDto.workedOIC += it.totals.workedOIC
                summaryDto.hoursRegularOICOvertime += it.totals.hoursRegularOICOvertime
                summaryDto.hoursWorkedOICNSD += it.totals.hoursWorkedOICNSD
                summaryDto.hoursRestDay += it.totals.hoursRestDay
                summaryDto.hoursRestDayNSD += it.totals.hoursRestDayNSD
                summaryDto.hoursRestOvertime += it.totals.hoursRestOvertime
                summaryDto.hoursDoubleHoliday += it.totals.hoursDoubleHoliday
                summaryDto.hoursDoubleHolidayNSD += it.totals.hoursDoubleHolidayNSD
                summaryDto.hoursDoubleHolidayOvertime += it.totals.hoursDoubleHolidayOvertime
                summaryDto.hoursDoubleHolidayOIC += it.totals.hoursDoubleHolidayOIC
                summaryDto.hoursDoubleHolidayOICNSD += it.totals.hoursDoubleHolidayOICNSD
                summaryDto.hoursDoubleHolidayOICOvertime += it.totals.hoursDoubleHolidayOICOvertime
                summaryDto.hoursDoubleHolidayAndRestDay += it.totals.hoursDoubleHolidayAndRestDay
                summaryDto.hoursDoubleHolidayAndRestDayOvertime += it.totals.hoursDoubleHolidayAndRestDayOvertime
                summaryDto.hoursDoubleHolidayAndRestDayNSD += it.totals.hoursDoubleHolidayAndRestDayNSD
                summaryDto.hoursRegularHoliday += it.totals.hoursRegularHoliday
                summaryDto.hoursRegularHolidayOvertime += it.totals.hoursRegularHolidayOvertime
                summaryDto.hoursRegularHolidayNSD += it.totals.hoursRegularHolidayNSD
                summaryDto.hoursRegularHolidayOIC += it.totals.hoursRegularHolidayOIC
                summaryDto.hoursRegularHolidayOICOvertime += it.totals.hoursRegularHolidayOICOvertime
                summaryDto.hoursRegularHolidayOICNSD += it.totals.hoursRegularHolidayOICNSD
                summaryDto.hoursRegularHolidayAndRestDay += it.totals.hoursRegularHolidayAndRestDay
                summaryDto.hoursRegularHolidayAndRestDayOvertime += it.totals.hoursRegularHolidayAndRestDayOvertime
                summaryDto.hoursRegularHolidayAndRestDayNSD += it.totals.hoursRegularHolidayAndRestDayNSD
                summaryDto.hoursSpecialHoliday += it.totals.hoursSpecialHoliday
                summaryDto.hoursSpecialHolidayOvertime += it.totals.hoursSpecialHolidayOvertime
                summaryDto.hoursSpecialHolidayNSD += it.totals.hoursSpecialHolidayNSD
                summaryDto.hoursSpecialHolidayOIC += it.totals.hoursSpecialHolidayOIC
                summaryDto.hoursSpecialHolidayOICOvertime += it.totals.hoursSpecialHolidayOICOvertime
                summaryDto.hoursSpecialHolidayOICNSD += it.totals.hoursSpecialHolidayOICNSD
                summaryDto.hoursSpecialHolidayAndRestDay += it.totals.hoursSpecialHolidayAndRestDay
                summaryDto.hoursSpecialHolidayAndRestDayOvertime += it.totals.hoursSpecialHolidayAndRestDayOvertime
                summaryDto.hoursSpecialHolidayAndRestDayNSD += it.totals.hoursSpecialHolidayAndRestDayNSD
            }
            csvPrinter.printRecord(
                    it.payrollEmployee.employee.fullName,
                    it.payrollEmployee.employee.department?.departmentName,
                    it.payrollEmployee.employee.positionDesignation,
                    it.payrollEmployee.employee.employeeNo,
                    it.payrollEmployee.employee.basicSalary,
                    summaryDto.undertime == 0 ? 0 : summaryDto.undertime.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.late == 0 ? 0 : summaryDto.late.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursAbsent == 0 ? 0 : summaryDto.hoursAbsent.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.worked == 0 ? 0 : summaryDto.worked.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursRegularOvertime == 0 ? 0 : summaryDto.hoursRegularOvertime.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursWorkedNSD == 0 ? 0 : summaryDto.hoursWorkedNSD.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.workedOIC == 0 ? 0 : summaryDto.workedOIC.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursRegularOICOvertime == 0 ? 0 : summaryDto.hoursRegularOICOvertime.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursWorkedOICNSD == 0 ? 0 : summaryDto.hoursWorkedOICNSD.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursRestDay == 0 ? 0 : summaryDto.hoursRestDay.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursRestDayNSD == 0 ? 0 : summaryDto.hoursRestDayNSD.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursRestOvertime == 0 ? 0 : summaryDto.hoursRestOvertime.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursSpecialHoliday == 0 ? 0 : summaryDto.hoursSpecialHoliday.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursSpecialHolidayOvertime == 0 ? 0 : summaryDto.hoursSpecialHolidayOvertime.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursSpecialHolidayNSD == 0 ? 0 : summaryDto.hoursSpecialHolidayNSD.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursSpecialHolidayOIC == 0 ? 0 : summaryDto.hoursSpecialHolidayOIC.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursSpecialHolidayOICOvertime == 0 ? 0 : summaryDto.hoursSpecialHolidayOICOvertime.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursSpecialHolidayOICNSD == 0 ? 0 : summaryDto.hoursSpecialHolidayOICNSD.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursSpecialHolidayAndRestDay == 0 ? 0 : summaryDto.hoursSpecialHolidayAndRestDay.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursSpecialHolidayAndRestDayOvertime == 0 ? 0 : summaryDto.hoursSpecialHolidayAndRestDayOvertime.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursSpecialHolidayAndRestDayNSD == 0 ? 0 : summaryDto.hoursSpecialHolidayAndRestDayNSD.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursRegularHoliday == 0 ? 0 : summaryDto.hoursRegularHoliday.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursRegularHolidayOvertime == 0 ? 0 : summaryDto.hoursRegularHolidayOvertime.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursRegularHolidayNSD == 0 ? 0 : summaryDto.hoursRegularHolidayNSD.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursRegularHolidayOIC == 0 ? 0 : summaryDto.hoursRegularHolidayOIC.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursRegularHolidayOICOvertime == 0 ? 0 : summaryDto.hoursRegularHolidayOICOvertime.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursRegularHolidayOICNSD == 0 ? 0 : summaryDto.hoursRegularHolidayOICNSD.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursRegularHolidayAndRestDay == 0 ? 0 : summaryDto.hoursRegularHolidayAndRestDay.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursRegularHolidayAndRestDayOvertime == 0 ? 0 : summaryDto.hoursRegularHolidayAndRestDayOvertime.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursRegularHolidayAndRestDayNSD == 0 ? 0 : summaryDto.hoursRegularHolidayAndRestDayNSD.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursDoubleHoliday == 0 ? 0 : summaryDto.hoursDoubleHoliday.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursDoubleHolidayNSD == 0 ? 0 : summaryDto.hoursDoubleHolidayNSD.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursDoubleHolidayOvertime == 0 ? 0 : summaryDto.hoursDoubleHolidayOvertime.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursDoubleHolidayOIC == 0 ? 0 : summaryDto.hoursDoubleHolidayOIC.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursDoubleHolidayOICNSD == 0 ? 0 : summaryDto.hoursDoubleHolidayOICNSD.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursDoubleHolidayOICOvertime == 0 ? 0 : summaryDto.hoursDoubleHolidayOICOvertime.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursDoubleHolidayAndRestDay == 0 ? 0 : summaryDto.hoursDoubleHolidayAndRestDay.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursDoubleHolidayAndRestDayOvertime == 0 ? 0 : summaryDto.hoursDoubleHolidayAndRestDayOvertime.setScale(4, BigDecimal.ROUND_HALF_UP),
                    summaryDto.hoursDoubleHolidayAndRestDayNSD == 0 ? 0 : summaryDto.hoursDoubleHolidayAndRestDayNSD.setScale(4, BigDecimal.ROUND_HALF_UP),
            )
        }

        def data = buffer.toString().getBytes(Charset.defaultCharset())
        def responseHeaders = new HttpHeaders()
        responseHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM)
        responseHeaders.setContentLength(data.length)

        return new ResponseEntity(data, responseHeaders, HttpStatus.OK)

    }
}

