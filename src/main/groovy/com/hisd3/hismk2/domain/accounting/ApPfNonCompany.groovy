package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.domain.billing.Billing
import com.hisd3.hismk2.domain.cashiering.PaymentTracker
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.inventory.Supplier
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant

@javax.persistence.Entity
@javax.persistence.Table(schema = "accounting", name = "pf_non_company")
class ApPfNonCompany implements Serializable {

	@GraphQLQuery
	@Id
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "billing", referencedColumnName = "id")
	Billing billing

	@GraphQLQuery
	@Column(name = "record_no", columnDefinition = "varchar")
	@UpperCase
	String recordNo

	@GraphQLQuery
	@Column(name = "description", columnDefinition = "varchar")
	String description

	@GraphQLQuery
	@Column(name = "transaction_date", columnDefinition = "varchar")
	Instant transactionDate

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "department", referencedColumnName = "id")
	Department department

	@GraphQLQuery
	@Column(name = "pf_fee", columnDefinition = "numeric")
	BigDecimal pfFee

	@GraphQLQuery
	@Column(name = "or_number", columnDefinition = "varchar")
	@UpperCase
	String orNumber

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "emp_id", referencedColumnName = "id")
	Employee employee

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "payment_tracker", referencedColumnName = "id")
	PaymentTracker paymentTracker

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "supplier_id", referencedColumnName = "id")
	Supplier supplier

	@GraphQLQuery
	@Column(name = "ap_process", columnDefinition = "bool")
	Boolean apProcess

	
}

