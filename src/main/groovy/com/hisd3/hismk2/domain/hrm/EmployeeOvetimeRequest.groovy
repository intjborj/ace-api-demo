package com.hisd3.hismk2.domain.hrm

import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type
import org.springframework.data.annotation.CreatedDate

import javax.persistence.*
import java.time.Instant

@Entity
@Table(schema = "hrm", name = "employee_overtime_request")
class EmployeeOvetimeRequest {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "`requested_by`", referencedColumnName = "id")
	Employee requestedBy
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "`approved_by`", referencedColumnName = "id")
	Employee approvedBy
	
	@GraphQLQuery
	@Column(name = "status", columnDefinition = "varchar")
	String status
	
	@GraphQLQuery
	@Column(name = "reason", columnDefinition = "varchar")
	String reason
	
	@GraphQLQuery
	@CreatedDate
	@Column(name = "start_datetime", nullable = false)
	Instant startDatetime
	
	@GraphQLQuery
	@CreatedDate
	@Column(name = "end_datetime", nullable = false)
	Instant endDatetime
}
