package com.hisd3.hismk2.domain.hrm

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type
import org.springframework.data.annotation.CreatedDate

import javax.persistence.*

@Entity
@Table(schema = "hrm", name = "employee_schedule")
class EmployeeLoan extends AbstractAuditingEntity {
	
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
	
	@GraphQLQuery
	@CreatedDate
	@Column(name = "loan_type", nullable = false)
	String loanType
	
	@GraphQLQuery
	@CreatedDate
	@Column(name = "note", nullable = false)
	String note
	
	@GraphQLQuery
	@CreatedDate
	@Column(name = "status", nullable = false)
	String status
	
	@GraphQLQuery
	@CreatedDate
	@Column(name = "loan_amount", nullable = false)
	BigDecimal loanAmount
	
	@GraphQLQuery
	@CreatedDate
	@Column(name = "loan_total_payable", nullable = false)
	BigDecimal loanTotalPayable
	
	@GraphQLQuery
	@CreatedDate
	@Column(name = "loan_monthly_payable", nullable = false)
	BigDecimal loanMonthlyPayable
	
}
