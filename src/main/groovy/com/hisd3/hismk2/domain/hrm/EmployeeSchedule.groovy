package com.hisd3.hismk2.domain.hrm

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.*
import java.time.Instant

@Entity
@Table(schema = "hrm", name = "employee_schedule")
@SQLDelete(sql = "UPDATE hrm.employee_schedule SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class EmployeeSchedule extends AbstractAuditingEntity {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "`employee`", referencedColumnName = "id")
	Employee employee

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "`department`", referencedColumnName = "id")
	Department department

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "`request`", referencedColumnName = "id")
	EmployeeRequest request
	
	@GraphQLQuery
	@Column(name = "date_time_start", nullable = false)
	Instant dateTimeStartRaw

	@GraphQLQuery
	@Formula("to_char(date_time_start + '8h', 'MM_DD_YYYY_HH24:MI')")
	String dateTimeStart

	@GraphQLQuery
	@Formula("to_char(date_time_start + '8h', 'HH12:MIAM')")
	String timeStart
	
	@GraphQLQuery
	@Column(name = "date_time_end", nullable = false)
	Instant dateTimeEndRaw

	@GraphQLQuery
	@Formula("to_char(date_time_end + '8h','MM_DD_YYYY_HH24:MI')")
	String dateTimeEnd

	@GraphQLQuery
	@Formula("to_char(date_time_end + '8h','HH12:MIAM')")
	String timeEnd
	
	@GraphQLQuery
	@Column(name = "meal_break_start", nullable = false)
	Instant mealBreakStart
	
	@GraphQLQuery
	@Column(name = "meal_break_end", nullable = false)
	Instant mealBreakEnd
	
	@GraphQLQuery
	@Column(name = "is_rest_day", columnDefinition = "bool")
	Boolean isRestDay = false
	
	@GraphQLQuery
	@Column(name = "is_overtime", columnDefinition = "bool")
	Boolean isOvertime = false

	@GraphQLQuery
	@Column(name = "is_leave", columnDefinition = "bool")
	Boolean isLeave = false

	@GraphQLQuery
	@Column(name = "locked", columnDefinition = "bool")
	Boolean locked

	@GraphQLQuery
	@Column(name = "label", columnDefinition = "varchar(255)")
	String label

	@GraphQLQuery
	@Column(name = "title", columnDefinition = "varchar(255)")
	String title

	@GraphQLQuery
	@Column(name = "color", columnDefinition = "varchar(255)")
	String color

	@GraphQLQuery
	@Column(name = "is_custom", columnDefinition = "bool")
	Boolean isCustom = false

	@GraphQLQuery
	@Column(name = "is_oic", columnDefinition = "bool")
	Boolean isOIC = false

	@GraphQLQuery
	@Column(name = "is_multi_day", columnDefinition = "bool")
	Boolean isMultiDay = false

	@GraphQLQuery
	@Column(name = "with_nsd", columnDefinition = "bool")
	Boolean withNSD = true

	@GraphQLQuery
	@Column(name = "with_holiday", columnDefinition = "bool default true")
	Boolean withHoliday = true

	@GraphQLQuery
	@Column(name = "with_pay", columnDefinition = "bool default true")
	Boolean withPay = true

	@GraphQLQuery
	@Column(name = "assigned_date", columnDefinition = "timestamp")
	Instant assignedDate
	
}
