package com.hisd3.hismk2.domain.hrm

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(schema = "hrm", name = "salary_rate_multiplier")
@SQLDelete(sql = "UPDATE hrm.salary_rate_multiplier SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class SalaryRateMultiplier extends AbstractAuditingEntity{

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "regular")
    Float regular

    @GraphQLQuery
    @Column(name = "restday")
    Float restday

    @GraphQLQuery
    @Column(name = "special_holiday")
    Float specialHoliday

    @GraphQLQuery
    @Column(name = "special_holiday_and_rest_day")
    Float specialHolidayAndRestDay

    @GraphQLQuery
    @Column(name = "regular_holiday")
    Float regularHoliday

    @GraphQLQuery
    @Column(name = "regular_holiday_and_rest_day")
    Float regularHolidayAndRestDay

    @GraphQLQuery
    @Column(name = "double_holiday")
    Float doubleHoliday

    @GraphQLQuery
    @Column(name = "double_holiday_and_rest_day")
    Float doubleHolidayAndRestDay

    @GraphQLQuery
    @Column(name = "regular_overtime")
    Float regularOvertime

    @GraphQLQuery
    @Column(name = "restday_overtime")
    Float restdayOvertime

    @GraphQLQuery
    @Column(name = "special_holiday_overtime")
    Float specialHolidayOvertime

    @GraphQLQuery
    @Column(name = "special_holiday_and_rest_day_overtime")
    Float specialHolidayAndRestDayOvertime

    @GraphQLQuery
    @Column(name = "regular_holiday_overtime")
    Float regularHolidayOvertime

    @GraphQLQuery
    @Column(name = "regular_holiday_and_rest_day_overtime")
    Float regularHolidayAndRestDayOvertime

    @GraphQLQuery
    @Column(name = "double_holiday_overtime")
    Float doubleHolidayOvertime

    @GraphQLQuery
    @Column(name = "double_holiday_and_rest_day_overtime")
    Float doubleHolidayAndRestDayOvertime

    @GraphQLQuery
    @Column(name = "night_differential")
    Float nightDifferential

}

