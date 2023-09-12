package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.billing.Billing
import com.hisd3.hismk2.domain.billing.BillingItem
import com.hisd3.hismk2.domain.billing.CompanyAccount
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.*

@Entity
@Table(schema = "accounting", name = "ar_transfer")
class ArTransfer extends AbstractAuditingEntity implements Serializable {

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@GraphQLQuery
	@ManyToOne(fetch = FetchType.EAGER)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "company_id", referencedColumnName = "id")
	CompanyAccount companyAccount

	@GraphQLQuery
	@ManyToOne(fetch = FetchType.EAGER)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "source_company_id", referencedColumnName = "id")
	CompanyAccount companySourceAccount

	@GraphQLQuery
	@ManyToOne(fetch = FetchType.EAGER)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "billing_id", referencedColumnName = "id")
	Billing billing

	@GraphQLQuery
	@ManyToOne(fetch = FetchType.EAGER)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "billing_item_id", referencedColumnName = "id")
	BillingItem billingItem

	@GraphQLQuery
	@ManyToOne(fetch = FetchType.EAGER)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "transaction_id", referencedColumnName = "id")
	ArTransaction arTransaction

	@GraphQLQuery
	@OneToOne(fetch = FetchType.EAGER)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "transaction_details_id", referencedColumnName = "id")
	ArTransactionDetails arTransactionDetails

	@GraphQLQuery
	@Column(name = "amount")
	BigDecimal amount

	@GraphQLQuery
	@Column(name = "reference")
	String reference

	@GraphQLQuery
	@Column(name = "status")
	String status

	@GraphQLQuery
	@Transient
	String getLastTouch() {
		lastModifiedBy ?: ("createdBy" ?: "")
	}

	@GraphQLQuery
	@OneToMany(mappedBy="arTransfer")
	@Where(clause = "is_voided = false or is_voided IS NULL")
	Set<BillingScheduleItems> billingScheduleItems

}
