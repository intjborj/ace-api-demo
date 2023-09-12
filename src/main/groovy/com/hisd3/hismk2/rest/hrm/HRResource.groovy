package com.hisd3.hismk2.rest.hrm

import ar.com.fdvs.dj.core.DJConstants
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager
import ar.com.fdvs.dj.domain.DynamicReport
import ar.com.fdvs.dj.domain.Style
import ar.com.fdvs.dj.domain.builders.ColumnBuilder
import ar.com.fdvs.dj.domain.builders.FastReportBuilder
import ar.com.fdvs.dj.domain.constants.Border
import ar.com.fdvs.dj.domain.constants.Font
import ar.com.fdvs.dj.domain.constants.HorizontalAlign
import ar.com.fdvs.dj.domain.constants.Page
import ar.com.fdvs.dj.domain.constants.VerticalAlign
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.hrm.EventCalendar
import com.hisd3.hismk2.domain.hrm.dto.EmployeeDto
import com.hisd3.hismk2.domain.hrm.dto.EmployeeScheduleDto
import com.hisd3.hismk2.graphqlservices.hrm.dtotransformer.EmployeeDtoTransformer
import com.hisd3.hismk2.services.PayrollTimeKeepingCalculatorService
import com.hisd3.hismk2.services.ReportTabularGeneratorService
import net.sf.jasperreports.engine.type.LineStyleEnum
import org.apache.commons.text.StringEscapeUtils
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.hibernate.jpa.QueryHints
import org.hibernate.query.Query
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import java.awt.Color
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.stream.Collectors

@RestController
@RequestMapping("/api/hrm")
class HRResource {

    @Autowired
    ReportTabularGeneratorService reportTabularGeneratorService

    @Autowired
    PayrollTimeKeepingCalculatorService payrollTimeKeepingCalculatorService

    @PersistenceContext
    EntityManager entityManager

    /*
    *    This method is almost the same with EmployeeScheduleService.class "getAllEmployeeSchedule
    *    the only difference is that the result is not paginated.
    */

    @RequestMapping(value = "/printschedule", produces = ["application/pdf"])
    def test(
            @RequestParam Instant startDate,
            @RequestParam Instant endDate,
            @RequestParam UUID department,
            @RequestParam String title,
            @RequestParam String subtitle
    ) {

        // number of loops/number of subreports
        Integer NUMBER_OF_DATE_CELLS = 8
        Long daysDuration = Duration.between(startDate, endDate).toDays() + 1
        Double weeksDuration = (daysDuration / NUMBER_OF_DATE_CELLS)

        if (weeksDuration % 1 != 0) {
            weeksDuration += 1
            weeksDuration = weeksDuration.trunc()
        }

        List<UUID> employeeIds = entityManager.createQuery("""
            Select e.id from Employee e
                left join e.department d
            where d.id = :department and e.isActive IS NOT NULL and e.isActive = true
            order by e.fullName
        """, UUID)
                .setParameter("department", department)
                .getResultList()

        Map<String, EmployeeDto> employees = entityManager.createQuery("""
                Select distinct 
                    e.id as e_id, 
                    e.fullName as e_full_name, 
                    d.id as d_department_of_duty_id,
                    d.departmentName as d_department_name,
                    dd.id as dd_department_of_duty_id,
                    dd.departmentName as dd_department_name,
                    es.id as e_s_id, 
                    es.dateTimeStart as e_s_date_time_start, 
                    es.timeStart as e_s_time_start, 
                    es.dateTimeStartRaw as e_s_date_time_start_raw, 
                    es.dateTimeEnd as e_s_date_time_end, 
                    es.timeEnd as e_s_time_end, 
                    es.dateTimeEndRaw as e_s_date_time_end_raw, 
                    es.color as e_s_color, 
                    es.isRestDay as e_s_is_rest_day, 
                    es.isOvertime as e_s_is_overtime, 
                    es.isLeave as e_s_is_leave, 
                    es.title as e_s_title, 
                    es.label as e_s_label, 
                    es.locked as e_s_locked,
                    es.mealBreakStart as e_s_meal_break_start,
                    es.mealBreakEnd as e_s_meal_break_end,
                    es.isCustom as e_s_is_custom,
                    es.isOIC as e_s_is_oic,
                    es.isMultiDay as e_s_is_multi_day,
                    es.withNSD as e_s_with_nsd,
                    es.withHoliday as e_s_with_holiday,
                    es.withPay as e_s_with_pay,
                    es.assignedDate as e_s_assigned_date,
                    r.id as r_request_id,
                    dd.id as dd_department_of_duty_id,
                    dd.departmentName as dd_department_name,
                    esd.id as esd_department_of_duty_id,
                    esd.departmentName as esd_department_name 
                    from Employee e
                    left outer join e.employeeSchedule es
                    left join es.request r
                    left join e.department dd
                    left join e.department d
                    left join es.department esd
                where
                    (
                    ((es.dateTimeStartRaw >= :startDate and es.dateTimeStartRaw <= :endDate ) or (es.dateTimeEndRaw >= :startDate and es.dateTimeEndRaw <= :endDate) 
                    or (es.dateTimeStartRaw <= :startDate and es.dateTimeEndRaw >= :endDate)
                    or (es.assignedDate >= :startDate and es.assignedDate <= :endDate and es.isOvertime is true)
                    ) 
                    and e.id in :employees) 
                    
                order by e.fullName, es.dateTimeStartRaw
            """)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setParameter("employees", employeeIds)
                .unwrap(Query.class)
                .setResultTransformer(new EmployeeDtoTransformer())
                .getResultStream().inject([:]) { Map<String, EmployeeDto> map, EmployeeDto it -> map << [(it.id.toString()): it] } as Map

        List<EmployeeDto> finalEmployees = []
        List<Employee> newEmployees = entityManager.createQuery("""
                Select e from Employee e
                    left join fetch e.department
                where e.id in :employees 
                order by e.fullName
            """, Employee.class)
                .setParameter("employees", employeeIds)
                .getResultList()

        newEmployees.forEach({ Employee it ->
            if (employees.get(it.id.toString())) {
                EmployeeDto employee = employees.get(it.id.toString())
                finalEmployees.add(employee)
            } else finalEmployees.add(new EmployeeDto(it.id, it.fullName, it.departmentOfDuty?.id, it.departmentOfDuty?.departmentName))
        })

        def datasource = []
        finalEmployees.each {
            Map<String, String> finalValue = [:]
            finalValue.put("employee", it.fullName)
            Map<String, Object> employeeSchedule = it.employeeSchedule as Map<String, Object>

            def scheduleKeys = employeeSchedule
                    .keySet()
                    .sort {
                        if (it.contains("LEAVE_REST")) return 0
                        else if (it.contains("LEAVE")) return 2
                        else if (it.contains("OIC")) return 3
                        else if (it.contains("OVERTIME")) return 4
                        else if (it.contains("OVERTIME_OIC")) return 5
                        else return 1
                    }

            Map<String, String> scheduleMap = [:]
            scheduleKeys.each { keyValue ->
                String scheduleType = null
                String scheduleDate = keyValue.substring(0, 10)
                String scheduleValue = ""

                if (scheduleMap[scheduleDate]) scheduleValue = scheduleMap[scheduleDate]

                if (keyValue.size() >= 11)
                    scheduleType = keyValue.substring(11)

                EmployeeScheduleDto currentEmployeeSchedule = null
                List<EmployeeScheduleDto> overtimeSchedule = []

                if (keyValue.contains("OVERTIME")) overtimeSchedule = employeeSchedule.get(keyValue) as List<EmployeeScheduleDto>
                else currentEmployeeSchedule = employeeSchedule.get(keyValue) as EmployeeScheduleDto

                if (scheduleMap[scheduleDate]) scheduleValue = scheduleMap[scheduleDate]

                DateTimeFormatter scheduleFormatter = DateTimeFormatter.ofPattern("hh:mma").withZone(ZoneId.systemDefault())
                String startEndFormat = null
                if (currentEmployeeSchedule) {
                    String start = scheduleFormatter.format(currentEmployeeSchedule.dateTimeStartRaw)
                    String end = scheduleFormatter.format(currentEmployeeSchedule.dateTimeEndRaw)
                    startEndFormat = "${start}-${end}"
                }

                if (scheduleValue) scheduleValue += "\n"

                if (scheduleType == "LEAVE_REST") scheduleValue += "LEAVE REST"
                else if (scheduleDate == keyValue) {
                    if (currentEmployeeSchedule.isRestDay)
                        scheduleValue += "REST DAY"
                    if (currentEmployeeSchedule.label != "R") {
                        if (scheduleValue) scheduleValue += "\n"
                        scheduleValue += "SCHEDULE\n${startEndFormat}"
                    }
                } else if (scheduleType == "LEAVE") {
                    Instant start = currentEmployeeSchedule.dateTimeStartRaw
                    Instant end = currentEmployeeSchedule.dateTimeEndRaw
                    BigDecimal duration = Duration.between(start, end).seconds / (60 * 60)
                    scheduleValue += "LEAVE\n(${duration} hours)"
                } else if (scheduleType == "OIC") {
                    scheduleValue += "OIC\n${startEndFormat}"
                } else if (scheduleType == "OVERTIME") {
                    scheduleValue += "OVERTIME"
                    overtimeSchedule.each {
                        String start = scheduleFormatter.format(it.dateTimeStartRaw)
                        String end = scheduleFormatter.format(it.dateTimeEndRaw)
                        scheduleValue += "\n${start}-${end}"
                    }
                } else if (scheduleType == "OVERTIME_OIC") {
                    scheduleValue += "OIC OVERTIME"
                    overtimeSchedule.each {
                        String start = scheduleFormatter.format(it.dateTimeStartRaw)
                        String end = scheduleFormatter.format(it.dateTimeEndRaw)
                        scheduleValue += "\n${start}-${end}"
                    }
                }

                scheduleMap[scheduleDate] = scheduleValue
            }

            finalValue = finalValue << scheduleMap
            return datasource << finalValue
        }

        reportTabularGeneratorService.generateReport([], title, subtitle,
                { it, parameters ->

                    it.setTemplateFile("reports/hrm/schedule_landscape.jrxml")
                    it.setUseFullPageWidth(true)
                    it.setWhenNoDataAllSectionNoDetail()
                    it.setPageSizeAndOrientation(Page.Page_Legal_Landscape())

                    Map<String, List<EventCalendar>> holidays = payrollTimeKeepingCalculatorService.getHolidays(startDate, endDate)

                    Integer weekCount = 1
                    while (weekCount <= weeksDuration) {
                        Instant weekStart
                        Instant weekEnd

                        if (weekCount == 1) weekStart = startDate
                        else weekStart = startDate.plus((weekCount - 1) * NUMBER_OF_DATE_CELLS, ChronoUnit.DAYS)

                        if (Double.valueOf(weekCount) == weeksDuration) weekEnd = endDate
                        else weekEnd = weekStart.plus(NUMBER_OF_DATE_CELLS - 1, ChronoUnit.DAYS)

                        it.addConcatenatedReport(generateWeeklySubReport(weekStart, weekEnd, holidays), new ClassicLayoutManager(), "schedule", DJConstants.DATA_SOURCE_ORIGIN_PARAMETER, DJConstants.DATA_SOURCE_TYPE_COLLECTION, false)
                        weekCount += 1
                    }
                    parameters.put("schedule", datasource)
                    it.build()
                })

    }


    private static DynamicReport generateWeeklySubReport(Instant startDate, Instant endDate, Map<String, List<EventCalendar>> holidays) throws Exception {
        FastReportBuilder it = new FastReportBuilder()
        String title
        Long daysDuration = Duration.between(startDate, endDate).toDays() + 1
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy").withZone(ZoneId.systemDefault())
        if (daysDuration == 1) title = formatter.format(startDate)
        else title = "${formatter.format(startDate)}—${formatter.format(endDate)}"


        Style employeeNameStyle = new Style()
        employeeNameStyle.border = new Border(1.0, LineStyleEnum.SOLID.getValue(), Color.BLACK)
        employeeNameStyle.setHorizontalAlign(HorizontalAlign.LEFT)
        employeeNameStyle.setVerticalAlign(VerticalAlign.MIDDLE)
        employeeNameStyle.padding = 10
        employeeNameStyle.setFont(new Font(9, "DejaVu Sans", false, false, false))

        Style employeeHeaderStyle = new Style()
        employeeHeaderStyle.border = new Border(1.0, LineStyleEnum.SOLID.getValue(), Color.BLACK)
        employeeHeaderStyle.setHorizontalAlign(HorizontalAlign.LEFT)
        employeeHeaderStyle.setVerticalAlign(VerticalAlign.MIDDLE)
        employeeHeaderStyle.padding = 10
        employeeHeaderStyle.setFont(new Font(9, "DejaVu Sans", true, false, false))

        AbstractColumn columnTitle = ColumnBuilder.getNew()
                .setColumnProperty("employee", String.class)
                .setTitle("Employee Name")
                .setWidth(50)
                .setStyle(employeeNameStyle)
                .setHeaderStyle(employeeHeaderStyle)
                .build()
        it.addColumn(columnTitle)

        while (startDate <= endDate) {
            DateTimeFormatter dateKeyFormatter = DateTimeFormatter.ofPattern("MM_dd_yyyy").withZone(ZoneId.systemDefault())
            DateTimeFormatter dateHeaderFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy").withZone(ZoneId.systemDefault())

            Style dateCellStyle = new Style()
            dateCellStyle.border = new Border(1.0, LineStyleEnum.SOLID.getValue(), Color.BLACK)
            dateCellStyle.setHorizontalAlign(HorizontalAlign.CENTER)
            dateCellStyle.setVerticalAlign(VerticalAlign.MIDDLE)
            dateCellStyle.padding = 10
            dateCellStyle.setFont(new Font(8, "DejaVu Sans", false, false, false))

            Style dateHeaderStyle = new Style()
            dateHeaderStyle.border = new Border(1.0, LineStyleEnum.SOLID.getValue(), Color.BLACK)
            dateHeaderStyle.setHorizontalAlign(HorizontalAlign.CENTER)
            dateHeaderStyle.setVerticalAlign(VerticalAlign.MIDDLE)
            dateHeaderStyle.padding = 10
            dateHeaderStyle.setFont(new Font(8, "DejaVu Sans", true, false, false))

            String dateTitle = dateHeaderFormatter.format(startDate)
            String key = dateKeyFormatter.format(startDate)
            if (holidays.get(key)?.size() > 0) {
                List<EventCalendar> currentHolidays = holidays.get(key)
                currentHolidays.each {
                    dateTitle += "\n${it.name}"
                }
            }
            AbstractColumn newColumn = ColumnBuilder.getNew()
                    .setColumnProperty(key, String.class)
                    .setTitle(StringEscapeUtils.escapeJava(dateTitle).toString())
                    .setStyle(dateCellStyle)
                    .setHeaderStyle(dateHeaderStyle)
                    .setWidth(25)
                    .build()
            it.addColumn(newColumn)

            startDate = startDate.plus(1, ChronoUnit.DAYS)
        }

        Style titleStyle = new Style()

        titleStyle.setHorizontalAlign(HorizontalAlign.LEFT)
        titleStyle.setFont(new Font(10, "DejaVu Sans", true, false, false))

        DynamicReport dr = it.setMargins(5, 5, 20, 20)
                .setUseFullPageWidth(true)
                .setTitleStyle(titleStyle)
                .setTitle(title)
                .build()
        return dr
    }

}
