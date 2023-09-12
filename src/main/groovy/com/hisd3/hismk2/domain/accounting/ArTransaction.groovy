package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.billing.CompanyAccount
import com.hisd3.hismk2.domain.cashiering.PaymentTracker
import com.hisd3.hismk2.domain.inventory.Supplier
import com.hisd3.hismk2.domain.types.AutoIntegrateable
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type
import org.springframework.data.annotation.CreatedDate

import javax.persistence.*
import java.time.Instant

enum AR_TRANS_TYPE {
	memo,
	payments,
	transfer
}

@Entity
@Table(schema = "accounting", name = "ar_transaction")
class ArTransaction extends AbstractAuditingEntity implements Serializable, AutoIntegrateable{

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@GraphQLQuery
	@Column(name = "tracking_code")
	String trackingNo

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_receivable_id", referencedColumnName = "id")
	AccountReceivable accountReceivable

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_tracker_id", referencedColumnName = "id")
	PaymentTracker paymentTracker

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trans_type_id", referencedColumnName = "id")
	ArTransactionType arTransactionType

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id", referencedColumnName = "id")
	CompanyAccount companyAccount

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "personal_account", referencedColumnName = "id")
	Supplier personalAccount

	@GraphQLQuery
	@Column(name = "amount")
	BigDecimal amount

	@GraphQLQuery
	@Column(name = "type")
	String type

	@GraphQLQuery
	@Column(name = "posted_ledger", columnDefinition = "uuid")
	UUID postedLedger

	@GraphQLQuery
	@Column(name = "remarks")
	String remarks

	@GraphQLQuery
	@Column(name = "status")
	String status

	@GraphQLQuery
	@CreatedDate
	@Column(name = "transaction_date", nullable = false)
	Instant transactionDate

	@GraphQLQuery
	@OneToMany(mappedBy="arTransaction", fetch = FetchType.LAZY)
	Set<ArTransactionDetails> arTransactionItems

	@Override
	String getDomain() {
		return ArTransaction.class.name
	}

	@Override
	Map<String, String> getDetails() {
		return [:]
	}

	@Transient
	String flagValue

	@Transient
	BigDecimal artA,artB,artC

	@Transient
	BigDecimal totalHci,totalPf,companyAmt,personalAmt

	@Transient
	BigDecimal negativeAmount
	BigDecimal getNegativeAmount() {
		def b = 0
		if(amount){
			b = -amount
		}
		return b
	}


}
