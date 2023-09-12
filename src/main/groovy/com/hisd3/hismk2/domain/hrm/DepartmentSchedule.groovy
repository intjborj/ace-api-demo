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
@Table(schema = "hrm", name = "department_schedule")
@SQLDelete(sql = "UPDATE hrm.department_schedule SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class DepartmentSchedule extends AbstractAuditingEntity {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "`department`", referencedColumnName = "id")
	Department department

	@GraphQLQuery
	@Column(name = "title", columnDefinition = "varchar")
	String title

	@GraphQLQuery
	@Column(name = "label", columnDefinition = "varchar")
	String label
	
	@GraphQLQuery
	@Column(name = "date_time_start", nullable = false)
	Instant dateTimeStartRaw

	@GraphQLQuery
	@Formula("to_char(date_time_start + '8h','HH12:MIAM')")
	String dateTimeStart

	@GraphQLQuery
	@Column(name = "date_time_end", nullable = false)
	Instant dateTimeEndRaw

	@GraphQLQuery
	@Formula("to_char(date_time_end + '8h','HH12:MIAM')")
	String dateTimeEnd

	@GraphQLQuery
	@Column(name = "meal_break_start", nullable = false)
	Instant mealBreakStart

	@GraphQLQuery
	@Column(name = "meal_break_end", nullable = false)
	Instant mealBreakEnd

	@GraphQLQuery
	@Column(name = "color", nullable = false)
	String color
	
}
