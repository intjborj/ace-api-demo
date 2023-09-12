package com.hisd3.hismk2.domain.billing

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.Patient
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*
import org.javers.core.metamodel.annotation.DiffIgnore
import org.springframework.data.annotation.CreatedDate

import javax.persistence.*
import java.time.Instant

@javax.persistence.Entity
@javax.persistence.Table(name = "billing", schema = "billing")
class Billing extends AbstractAuditingEntity implements Serializable {
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@CreatedDate
	@Column(name = "entry_datetime", nullable = false)
	Instant entryDateTime
	
	@GraphQLQuery
	@Column(name = "billing_no", columnDefinition = "varchar")
	String billingNo
	
	@GraphQLQuery
	@Column(name = "locked", columnDefinition = "boolean")
	Boolean locked
	
	@GraphQLQuery
	@Column(name = "status", columnDefinition = "varchar")
	String status
	/*
	ACTIVE,
	INACTIVE
	 */
	@DiffIgnore
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "patient", referencedColumnName = "id")
	Patient patient
	
	@GraphQLQuery
	@Column(name = "otcname", columnDefinition = "varchar")
	String otcname
	
	@GraphQLQuery
	@Column(name = "locked_by", columnDefinition = "varchar")
	String lockedBy
	
	@GraphQLQuery
	@Column(name = "pricetiermanual", columnDefinition = "uuid")
	UUID pricetiermanual
	
	@GraphQLQuery
	@Column(name = "override_credit_limit", columnDefinition = "numeric")
	BigDecimal overrideCreditLimit


	@GraphQLQuery
	@Column(name = "finalized", columnDefinition = "boolean")
	Boolean finalizedSoa

	@GraphQLQuery
	@Column(name = "override_progress_payment", columnDefinition = "boolean")
	Boolean overrideProgressPayment


	@DiffIgnore
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "patient_case", referencedColumnName = "id")
	Case patientCase

	@DiffIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "billing")
	List<BillingItem> billingItemList = []


	@DiffIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "billing",cascade = [javax.persistence.CascadeType.ALL], orphanRemoval = true)
	@javax.persistence.OrderBy("code")
	Set<CashBasisItem> cashBasisItems = []
	
	@GraphQLQuery
	@Formula("(Select sum((COALESCE(b.debit,0)  -   COALESCE(b.credit,0)) * b.qty) from billing.billing_item b where b.billing=id and b.status='ACTIVE' and b.item_type not in ('DEDUCTIONS','DEDUCTIONSPF','PAYMENTS'))")
	BigDecimal totals
	
	@GraphQLQuery
	@Formula("(Select sum(COALESCE(b.credit,0) * b.qty) from billing.billing_item b where b.billing=id and b.item_type in ('DEDUCTIONS','DEDUCTIONSPF') and b.status='ACTIVE')")
	String deductions
	
	@GraphQLQuery
	@Formula("(Select sum((COALESCE(b.credit,0)  -   COALESCE(b.debit,0)) * b.qty) from billing.billing_item b where b.billing=id and b.item_type = 'PAYMENTS' and b.status='ACTIVE')")
	BigDecimal payments
	
	@GraphQLQuery
	@Formula("(Select sum((COALESCE(b.debit,0)  -   COALESCE(b.credit,0)) * b.qty) from billing.billing_item b where b.billing=id and b.status='ACTIVE')")
	BigDecimal balance

	@GraphQLQuery
	@Column(name = "final_soa", columnDefinition = "varchar")
	String finalSoa

	@GraphQLQuery
	@Column(name = "admission_charge", columnDefinition = "boolean")
	Boolean admissionCharge
}
