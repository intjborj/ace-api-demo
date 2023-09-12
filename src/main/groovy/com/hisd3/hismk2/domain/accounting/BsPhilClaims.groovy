package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.billing.Billing
import com.hisd3.hismk2.domain.billing.BillingItem
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.Patient
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant

@Entity
@Table(schema = "accounting", name = "bs_phil_claims")
class BsPhilClaims extends AbstractAuditingEntity implements Serializable {

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
	@JoinColumn(name = "billing_id", referencedColumnName = "id")
	Billing billing

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "patient_id", referencedColumnName = "id")
	Patient patient

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "case_id", referencedColumnName = "id")
	Case patientCase

	@GraphQLQuery
	@Column(name = "case_no")
	String caseNo

	@GraphQLQuery
	@Column(name = "claim_number")
	String claimNumber

	@GraphQLQuery
	@Column(name = "process_stage")
	String processStage

	@GraphQLQuery
	@Column(name = "voucher_no")
	String voucherNo

	@GraphQLQuery
	@Column(name = "voucher_date")
	Date voucherDate

	@GraphQLQuery
	@Column(name = "claim_amount")
	BigDecimal claimAmount

	@GraphQLQuery
	@Column(name = "claim_series_lhio")
	String claimSeriesLhio

	@GraphQLQuery
	@Column(name = "status")
	String status

	@GraphQLQuery
	@Column(name = "claim_date_created")
	Instant claimDateCreated

	@GraphQLQuery
	@Column(name = "claim_creator")
	String claimCreator

	@GraphQLQuery
	@Column(name = "process_date")
	Instant processDate

	@GraphQLQuery
	@Formula("(Select sum(COALESCE(b.amount,0)) from accounting.bs_phil_claims_items b where b.bs_claim_id=id and b.type = 'HCI')")
	BigDecimal hci

	@GraphQLQuery
	@Formula("(Select sum(COALESCE(b.amount,0)) from accounting.bs_phil_claims_items b where b.bs_claim_id=id and b.type = 'PF')")
	BigDecimal pf

	@GraphQLQuery
	@Formula("(select string_agg(distinct(ar.ar_no), ', ') from  accounting.bs_phil_claims_items bpci left join accounting.account_receivable ar on ar.id = bpci.receivable_id where bpci.bs_claim_id=id group  by ar.ar_no)")
	String arNo

	@GraphQLQuery
	@Formula("(select bi.transaction_date from accounting.bs_phil_claims_items bp left join billing.billing_item bi on bi.id = bp.billing_item_id  where bp.bs_claim_id=id group by bi.transaction_date limit 1)")
	Instant billingItemTransactionDate
}
