package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.domain.billing.BillingItem
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.inventory.Supplier
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*
import java.time.Instant

@javax.persistence.Entity
@javax.persistence.Table(schema = "accounting", name = "pf_company")
class ApPfCompany implements Serializable {

	@GraphQLQuery
	@Id
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "account_receivable_id", referencedColumnName = "id")
	AccountReceivable accountReceivable

	@GraphQLQuery
	@Column(name = "type", columnDefinition = "varchar")
	@UpperCase
	String type

	@GraphQLQuery
	@Column(name = "status", columnDefinition = "varchar")
	String status

	@GraphQLQuery
	@Column(name = "patient", columnDefinition = "varchar")
	@UpperCase
	String patient

	@GraphQLQuery
	@Column(name = "pf_fee", columnDefinition = "numeric")
	BigDecimal pfFee

	@GraphQLQuery
	@Column(name = "total_payments", columnDefinition = "numeric")
	BigDecimal totalPayments

	@GraphQLQuery
	@Column(name = "amount", columnDefinition = "numeric")
	BigDecimal amount

	@GraphQLQuery
	@Column(name = "discount", columnDefinition = "numeric")
	BigDecimal discount

	@GraphQLQuery
	@Column(name = "credit_memo", columnDefinition = "numeric")
	BigDecimal creditMemo

	@GraphQLQuery
	@Column(name = "pf_payable", columnDefinition = "numeric")
	BigDecimal pfPayable

	@GraphQLQuery
	@Column(name = "balance", columnDefinition = "numeric")
	BigDecimal balance

	@GraphQLQuery
	@Column(name = "cwt", columnDefinition = "bool")
	Boolean cwt

	@GraphQLQuery
	@Column(name = "trans_type", columnDefinition = "bool")
	@UpperCase
	String trans_type

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "billing_item", referencedColumnName = "id")
	BillingItem billingItem

	@GraphQLQuery
	@Column(name = "doctor", columnDefinition = "bool")
	@UpperCase
	String doctor

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "emp_id", referencedColumnName = "id")
	Employee employee

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "supplier_id", referencedColumnName = "id")
	Supplier supplier

	@GraphQLQuery
	@Column(name = "ap_process", columnDefinition = "bool")
	Boolean apProcess

	
}

