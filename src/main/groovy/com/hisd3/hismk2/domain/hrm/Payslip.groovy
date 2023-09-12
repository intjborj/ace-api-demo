package com.hisd3.hismk2.domain.hrm

import com.fasterxml.jackson.annotation.JsonIgnore
import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.accounting.ArTransfer
import com.hisd3.hismk2.domain.hrm.enums.EmployeePayFrequency
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant

import static javax.persistence.EnumType.*

@Entity
@javax.persistence.Table(schema = "hrm", name = "payslips")
class Payslip extends AbstractAuditingEntity {


    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "basic_salary", columnDefinition = "numeric")
    Double basicSalary = 0

    @GraphQLQuery
    @Column(name = "hours_late", columnDefinition = "numeric")
    Double hoursLate = 0

    @GraphQLQuery
    @Column(name = "hours_under_time", columnDefinition = "numeric")
    Double hoursUnderTime = 0

    @GraphQLQuery
    @Column(name = "hours_absent", columnDefinition = "numeric")
    Double hoursAbsent = 0

    @GraphQLQuery
    @Column(name = "deduct_sss", columnDefinition = "numeric")
    Double deductSss = 0

    @GraphQLQuery
    @Column(name = "deduct_gsis", columnDefinition = "numeric")
    Double deductGsis = 0

    @GraphQLQuery
    @Column(name = "deduct_philhealth", columnDefinition = "numeric")
    Double deductPhilhealth = 0

    @GraphQLQuery
    @Column(name = "deduct_hdmf", columnDefinition = "numeric")
    Double deductHdmf = 0

    @GraphQLQuery
    @Column(name = "deduct_others", columnDefinition = "numeric")
    Double deductOthers = 0

    @GraphQLQuery
    @Column(name = "deduct_cash_advance", columnDefinition = "numeric")
    Double deductCashAdvance = 0

    @GraphQLQuery
    @Column(name = "withholding_tax", columnDefinition = "numeric")
    Double withholdingTax = 0

    @GraphQLQuery
    @Column(name = "include_in_payroll", columnDefinition = "bool")
    Boolean includeInPayroll = 0

    @GraphQLQuery
    @Column(name = "hours_regular_overtime", columnDefinition = "numeric")
    Double hoursRegularOvertime = 0

    @GraphQLQuery
    @Column(name = "hours_restday_overtime", columnDefinition = "numeric")
    Double hoursRestOvertime = 0

    @GraphQLQuery
    @Column(name = "hours_special_holiday_overtime", columnDefinition = "numeric")
    Double hoursSpecialHolidayOvertime = 0

    @GraphQLQuery
    @Column(name = "hours_special_holiday_and_rest_day_overtime", columnDefinition = "numeric")
    Double hoursSpecialHolidayAndRestDayOvertime = 0

    @GraphQLQuery
    @Column(name = "hours_regular_holiday_overtime", columnDefinition = "numeric")
    Double hoursRegularHolidayOvertime = 0

    @GraphQLQuery
    @Column(name = "hours_regular_holiday_and_rest_day_overtime", columnDefinition = "numeric")
    Double hoursRegularHolidayAndRestDayOvertime = 0

    @GraphQLQuery
    @Column(name = "hours_double_holiday_overtime", columnDefinition = "numeric")
    Double hoursDoubleHolidayOvertime = 0

    @GraphQLQuery
    @Column(name = "hours_double_holiday_and_rest_day_overtime", columnDefinition = "numeric")
    Double hoursDoubleHolidayAndRestDayOvertime = 0

    @GraphQLQuery
    @Column(name = "hours_regular", columnDefinition = "numeric")
    Double hoursRegular = 0

    @GraphQLQuery
    @Column(name = "hours_restday", columnDefinition = "numeric")
    Double hoursRestDay = 0

    @GraphQLQuery
    @Column(name = "hours_special_holiday", columnDefinition = "numeric")
    Double hoursSpecialHoliday = 0

    @GraphQLQuery
    @Column(name = "hours_special_holiday_and_rest_day", columnDefinition = "numeric")
    Double hoursSpecialHolidayAndRestDay = 0

    @GraphQLQuery
    @Column(name = "hours_regular_holiday", columnDefinition = "numeric")
    Double hoursRegularHoliday = 0

    @GraphQLQuery
    @Column(name = "hours_regular_holiday_and_rest_day", columnDefinition = "numeric")
    Double hoursRegularHolidayAndRestDay = 0

    @GraphQLQuery
    @Column(name = "hours_double_holiday", columnDefinition = "numeric")
    Double hoursDoubleHoliday = 0

    @GraphQLQuery
    @Column(name = "hours_double_holiday_and_rest_day", columnDefinition = "numeric")
    Double hoursDoubleHolidayAndRestDay = 0

    @GraphQLQuery
    @Column(name = "hours_night_differential", columnDefinition = "numeric")
    Double hoursNightDifferential = 0

    @GraphQLQuery
    @Column(name = "adjustment", columnDefinition = "numeric")
    Double adjustment = 0

    @GraphQLQuery
    @Column(name = "adjustment_reason", columnDefinition = "numeric")
    Double adjustmentReason = 0

    @GraphQLQuery
    @Column(name = "deduct_sss_employer", columnDefinition = "numeric")
    Double deductSssEmployer = 0

    @GraphQLQuery
    @Column(name = "deduct_gsis_employer", columnDefinition = "numeric")
    Double deductGsisEmployer = 0

    @GraphQLQuery
    @Column(name = "deduct_philhealth_employer", columnDefinition = "numeric")
    Double deductPhilhealthEmployer = 0

    @GraphQLQuery
    @Column(name = "deduct_hdmf_employer", columnDefinition = "numeric")
    Double deductHdmfEmployer = 0

    @GraphQLQuery
    @Column(name = "pay_frequency", columnDefinition = "varchar")
    String payFreq

//    @GraphQLQuery
//    @Column(name = "salary_rate_multiplier", columnDefinition = "varchar")
//    SalaryRateMultiplier salaryRateMultiplier

    @JsonIgnore
    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee", referencedColumnName = "id")
    Employee employee

    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payroll", referencedColumnName = "id")
    Payroll payroll

}
