package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.billing.Billing
import com.hisd3.hismk2.domain.billing.BillingItem
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*


enum AccReceivableItemsDetailParam {
	PATIENT_ID,
	BILLING_ITEM_ID,
	PARENT_AR_ITEM,
	PF_EMPLOYEEID

}

@Entity
@Table(schema = "accounting", name = "account_receivable_items")
class AccountReceivableItems extends AbstractAuditingEntity implements Serializable {

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@ManyToOne(fetch = FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "account_receivable_id", referencedColumnName = "id")
	AccountReceivable accountReceivable

	@GraphQLQuery
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(schema = "accounting", name = "account_receivable_items_details",
			joinColumns = [@JoinColumn(name = "account_receivable_items")])
	@MapKeyColumn(name = "field_name")
	@Column(name = "field_value")
	@BatchSize(size = 20)
	Map<String, String> details = [:]

	@GraphQLQuery
	@Column(name = "type")
	String type

	@GraphQLQuery
	@Column(name = "debit")
	BigDecimal debit

	@GraphQLQuery
	@Column(name = "credit")
	BigDecimal credit

	@GraphQLQuery
	@Column(name = "description")
	String description

	@GraphQLQuery
	@Column(name = "status")
	String status

	@GraphQLQuery
	@Column(name = "trans_type")
	String transType

	@GraphQLQuery
	@Column(name = "record_no")
	String recordNo

	@GraphQLQuery
	@Column(name = "bill_month")
	Date billMonth

	@GraphQLQuery
	@Column(name = "cwt")
	Boolean cwt

	@GraphQLQuery
	@Column(name = "discount")
	BigDecimal discount

	@GraphQLQuery
	@Column(name = "amount")
	BigDecimal amount

	@GraphQLQuery
	@Column(name = "ap_process")
	Boolean apProcess

	@GraphQLQuery
	@Transient
	String getLastTouch() {
		lastModifiedBy ?: ("createdBy" ?: "")
	}

	@GraphQLQuery
	@OneToMany(mappedBy="accountReceivableItems")
	Set<ArTransactionDetails> arTransactionItems

	@GraphQLQuery
	@Formula("(Select sum(COALESCE(b.amount,0)) from accounting.ar_transaction_details b where b.type='payments' and (b.is_voided is null or b.is_voided = false) and b.account_receivable_item_id=id)")
	BigDecimal payment

	@GraphQLQuery
	@Formula("(Select sum(COALESCE(b.amount,0)) from accounting.ar_transaction_details b where b.type='memo' and (b.is_voided is null or b.is_voided = false) and b.account_receivable_item_id=id)")
	BigDecimal memo

	@Transient
	BigDecimal transfer


}
