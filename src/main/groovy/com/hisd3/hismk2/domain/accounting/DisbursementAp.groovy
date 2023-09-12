package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant

@javax.persistence.Entity
@javax.persistence.Table(name = "disbursement_ap", schema = "accounting")
class DisbursementAp extends AbstractAuditingEntity implements Serializable {

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "payable", referencedColumnName = "id")
	AccountsPayable payable

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "disbursement", referencedColumnName = "id")
	Disbursement disbursement

	@GraphQLQuery
	@Column(name = "applied_amount", columnDefinition = "numeric")
	BigDecimal appliedAmount

	@GraphQLQuery
	@Column(name = "vat_rate", columnDefinition = "numeric")
	BigDecimal vatRate

	@GraphQLQuery
	@Column(name = "vat_inclusive", columnDefinition = "bool")
	Boolean vatInclusive

	@GraphQLQuery
	@Column(name = "vat_amount", columnDefinition = "numeric")
	BigDecimal vatAmount

	@GraphQLQuery
	@Column(name = "ewt_desc", columnDefinition = "varchar")
	@UpperCase
	String ewtDesc

	@GraphQLQuery
	@Column(name = "ewt_rate", columnDefinition = "numeric")
	@UpperCase
	BigDecimal ewtRate

	@GraphQLQuery
	@Column(name = "ewt_amount", columnDefinition = "numeric")
	@UpperCase
	BigDecimal ewtAmount

	@GraphQLQuery
	@Column(name = "gross_amount", columnDefinition = "numeric")
	@UpperCase
	BigDecimal grossAmount

	@GraphQLQuery
	@Column(name = "discount", columnDefinition = "numeric")
	@UpperCase
	BigDecimal discount

	@GraphQLQuery
	@Column(name = "net_amount", columnDefinition = "numeric")
	@UpperCase
	BigDecimal netAmount

	@GraphQLQuery
	@Column(name = "reapplication", columnDefinition = "uuid")
	UUID reapplication

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "debit_memo", referencedColumnName = "id")
	DebitMemo debitMemo

	@GraphQLQuery
	@Column(name = "posted", columnDefinition = "uuid")
	Boolean posted

}

