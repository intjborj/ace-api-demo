package com.hisd3.hismk2.domain.hrm

import com.fasterxml.jackson.annotation.JsonIgnore
import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.hrm.enums.EmployeeAttendanceMethod
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.*
import java.time.Instant

@Entity
@Table(schema = "hrm", name = "employee_attendance")
@SQLDelete(sql = "UPDATE hrm.employee_attendance SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class EmployeeAttendance extends AbstractAuditingEntity {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@JsonIgnore
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee", referencedColumnName = "id")
	Employee employee
	
	@GraphQLQuery
	@Column(name = "attendance_time", nullable = false)
	Instant attendance_time
	
	@GraphQLQuery
	@Column(name = "original_attendance_time", nullable = false)
	Instant original_attendance_time
	
	@GraphQLQuery
	@Column(name = "type", columnDefinition = "varchar")
	String type

	@GraphQLQuery
	@Column(name = "original_type", columnDefinition = "varchar")
	String originalType
	
	@GraphQLQuery
	@Column(name = "source", columnDefinition = "varchar")
	String source
	
	@GraphQLQuery
	@Column(name = "additional_note", columnDefinition = "varchar")
	String additionalNote

	@GraphQLQuery
	@Enumerated(EnumType.STRING)
	@Column(name = "method", columnDefinition = "varchar")
	EmployeeAttendanceMethod method

	@GraphQLQuery
	@Column(name = "is_manual", columnDefinition = "bool")
	Boolean isManual

	@GraphQLQuery
	@Column(name = "is_ignored", columnDefinition = "bool")
	Boolean isIgnored

	
}
