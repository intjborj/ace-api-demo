package com.hisd3.hismk2.domain.payroll

import com.fasterxml.jackson.annotation.JsonIgnore
import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.payroll.enums.AccumulatedLogStatus
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*
import java.time.Instant

class Totals {
    Instant date
    Instant inTime
    Instant outTime

    String message

    Boolean isError
    Boolean isRestDay = false
    Boolean withNSD = true
    Boolean isLeave = false

    Boolean isAbsentOnly
    Boolean IsEmpty
    Boolean isOvertimeOnly
    Boolean isRestDayOnly


    BigDecimal undertime
    BigDecimal late
    BigDecimal hoursAbsent
    BigDecimal worked
    BigDecimal hoursRegularOvertime
    BigDecimal hoursWorkedNSD
    BigDecimal workedOIC
    BigDecimal hoursRegularOICOvertime
    BigDecimal hoursWorkedOICNSD
    BigDecimal hoursRestDay
    BigDecimal hoursRestDayNSD
    BigDecimal hoursRestOvertime
    BigDecimal hoursDoubleHoliday
    BigDecimal hoursDoubleHolidayNSD
    BigDecimal hoursDoubleHolidayOvertime
    BigDecimal hoursDoubleHolidayOIC
    BigDecimal hoursDoubleHolidayOICNSD
    BigDecimal hoursDoubleHolidayOICOvertime
    BigDecimal hoursDoubleHolidayAndRestDay
    BigDecimal hoursDoubleHolidayAndRestDayOvertime
    BigDecimal hoursDoubleHolidayAndRestDayNSD
    BigDecimal hoursRegularHoliday
    BigDecimal hoursRegularHolidayOvertime
    BigDecimal hoursRegularHolidayNSD
    BigDecimal hoursRegularHolidayOIC
    BigDecimal hoursRegularHolidayOICOvertime
    BigDecimal hoursRegularHolidayOICNSD
    BigDecimal hoursRegularHolidayAndRestDay
    BigDecimal hoursRegularHolidayAndRestDayOvertime
    BigDecimal hoursRegularHolidayAndRestDayNSD
    BigDecimal hoursSpecialHoliday
    BigDecimal hoursSpecialHolidayOvertime
    BigDecimal hoursSpecialHolidayNSD
    BigDecimal hoursSpecialHolidayOIC
    BigDecimal hoursSpecialHolidayOICOvertime
    BigDecimal hoursSpecialHolidayOICNSD
    BigDecimal hoursSpecialHolidayAndRestDay
    BigDecimal hoursSpecialHolidayAndRestDayOvertime
    BigDecimal hoursSpecialHolidayAndRestDayNSD


}


@javax.persistence.Entity
@javax.persistence.Table(schema = "payroll", name = "accumulated_logs_summary")
class AccumulatedLogSummary implements Serializable {


    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "timekeeping_employee", referencedColumnName = "employee")
    TimekeepingEmployee timekeepingEmployee

    @OneToMany(mappedBy = "summary")
    List<AccumulatedLog> accumulatedLogs = []

    @Type(type = "jsonb")
    @Column(name = "totals", columnDefinition = "jsonb")
    Totals totals

    @Type(type = "jsonb")
    @Column(name = "totals_original", columnDefinition = "jsonb")
    Totals totalsOriginal


    // TODO: generated
    @Transient
    Boolean isAbsentOnly, isEmpty, isError, isOvertimeOnly, isRestDay, isRestDayOnly, message
    //end generated



}