package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.billing.Billing
import com.hisd3.hismk2.domain.billing.BillingItem
import com.hisd3.hismk2.domain.inventory.CashBasis
import com.hisd3.hismk2.domain.inventory.Item
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration

import javax.persistence.*

@Entity
@Table(schema = "accounting", name = "billing_schedule_items")
class BillingScheduleItems extends AbstractAuditingEntity implements Serializable {

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billing_schedule_id", referencedColumnName = "id")
	BillingSchedule billingSchedule

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billing_id", referencedColumnName = "id")
	Billing billing

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billing_item_id", referencedColumnName = "id")
	BillingItem billingItem

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "transaction_id", referencedColumnName = "id")
	ArTransfer arTransfer

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ar_manual_deductions_id", referencedColumnName = "id")
	ArManualDeductions arManualDeductions

	@GraphQLQuery
	@Column(name = "type")
	String type

	@GraphQLQuery
	@Column(name = "approval_code")
	String approvalCode

	@GraphQLQuery
	@Column(name = "debit_adjustment")
	BigDecimal debitAdjustment

	@GraphQLQuery
	@Column(name = "amount")
	BigDecimal amount

	@GraphQLQuery
	@Column(name = "is_voided" ,nullable = false)
	Boolean isVoided

	@Transient
	BigDecimal totalAmount
	BigDecimal getTotalAmount() {
		def debit = 0
		if(debitAdjustment){
			debit = debitAdjustment
		}
		return amount + debit
	}

	@GraphQLQuery
	@Transient
	String getLastTouch() {
		lastModifiedBy ?: ("createdBy" ?: "")
	}

}
