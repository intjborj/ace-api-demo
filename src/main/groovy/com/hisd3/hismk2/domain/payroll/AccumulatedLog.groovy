package com.hisd3.hismk2.domain.payroll

import com.fasterxml.jackson.annotation.JsonIgnore
import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.billing.BillingItem
import com.hisd3.hismk2.domain.payroll.enums.AccumulatedLogStatus
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToOne
import javax.persistence.Table
import javax.persistence.Transient
import java.time.Instant

class OriginalAccumulatedLogs {
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

class FinalAccumulatedLogs {

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

class Schedule{
    Instant start
    Instant end
    String type
    String title

}


@Entity
@Table(schema = "payroll", name = "accumulated_logs")
//@SQLDelete(sql = "UPDATE payroll.accumulated_logs SET deleted = true WHERE id = ?")
//@Where(clause = "deleted <> true or deleted is  null ")
class AccumulatedLog extends AbstractAuditingEntity implements Serializable {


    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

//    @GraphQLQuery
//    @Column(name = "title", columnDefinition = "timestamp")
//    String title



    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "summary", referencedColumnName = "id")
    AccumulatedLogSummary summary

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department", referencedColumnName = "id")
    Department department

    @Type(type = "jsonb")
    @Column(name="schedule",columnDefinition = "json")
    Schedule schedule

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "varchar")
    AccumulatedLogStatus status

    @Column(name = "date", columnDefinition = "timestamp")
    Instant date

    @Column(name = "in_time", columnDefinition = "timestamp")
    Instant inTime

    @Column(name = "out_time", columnDefinition = "timestamp")
    Instant outTime

    @Type(type = "jsonb")
    @Column(name="final_logs",columnDefinition = "jsonb")
    FinalAccumulatedLogs finalLogs


    @Type(type = "jsonb")
    @Column(name="original_logs",columnDefinition = "jsonb")
    OriginalAccumulatedLogs originalLogs

}
