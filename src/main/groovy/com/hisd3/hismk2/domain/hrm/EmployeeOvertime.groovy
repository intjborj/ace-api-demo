package com.hisd3.hismk2.domain.hrm

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type
import org.springframework.data.annotation.CreatedDate

import javax.persistence.*
import java.time.Instant

@Entity
@Table(schema = "hrm", name = "employee_schedule")
class EmployeeOvertime extends AbstractAuditingEntity {
	
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
	@JoinColumn(name = "`approved_by`", referencedColumnName = "id")
	Employee approvedBy
	
	@GraphQLQuery
	@CreatedDate
	@Column(name = "overtime_start", nullable = false)
	Instant overtimeStart
	
	@GraphQLQuery
	@CreatedDate
	@Column(name = "overtime_end", nullable = false)
	Instant overtimeEnd
	
	@GraphQLQuery
	@CreatedDate
	@Column(name = "payslip", nullable = false)
	UUID payslip
	
	@GraphQLQuery
	@Column(name = "overtime_reason", columnDefinition = "varchar")
	String overtimeReason
	
	@GraphQLQuery
	@Column(name = "status", columnDefinition = "varchar")
	String status
	
}
