package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.billing.BillingItem
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction

import javax.persistence.*
import org.hibernate.annotations.Type

@Entity
@Table(schema = "accounting", name = "all_receivable_items")
class ReceivablesItems extends AbstractAuditingEntity implements Serializable {

	@GraphQLQuery
	@Id
	@Column(name = "ar_item_id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@GraphQLQuery
	@Column(name = "ar_id")
	UUID arId

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billing_id", referencedColumnName = "billing_id")
	BsPhilClaims bsPhilClaims

	@GraphQLQuery
	@Column(name = "account_id")
	UUID companyId

	@GraphQLQuery
	@Column(name = "ar_no")
	String arNo

	@GraphQLQuery
	@Column(name = "ar_record_no")
	String arRecordNo

	@GraphQLQuery
	@Column(name = "ar_description")
	String arDescription

	@GraphQLQuery
	@Column(name = "billing_description")
	String billingDescription

	@GraphQLQuery
	@Column(name = "type")
	String type

	@GraphQLQuery
	@Column(name = "debit")
	BigDecimal debit

	@GraphQLQuery
	@Column(name = "credit")
	BigDecimal credit

}
