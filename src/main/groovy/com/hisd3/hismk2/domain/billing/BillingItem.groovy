package com.hisd3.hismk2.domain.billing

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.accounting.Bank
import com.hisd3.hismk2.domain.inventory.AccountingCategory
import com.hisd3.hismk2.domain.types.AutoIntegrateable
import com.hisd3.hismk2.repository.pms.TransferRepository
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*
import java.time.Instant

enum BillingItemType {
	//Hospital Charges
	ROOMBOARD,
	MEDICINES,
	DIAGNOSTICS,
	ORFEE,
	SUPPLIES,
	OTHERS,
	CATHLAB,
	// Doctors
	PF,


	DEDUCTIONS,
	DEDUCTIONSPF,
	PAYMENTS,

	// Utility converted to SUPPLIES actually

	OXYGEN,


	ANNOTATION_PAYMENTS_GROUPS,
	ANNOTATION_NOTIFICATION_GROUPS
}

enum BillingItemStatus {
	ACTIVE,
	CANCELED
}

enum BillingItemDetailParam {
	INVENTORYID,
	ITEMID,
	SERVICEID,
	ORDERSLIPITEM,
	ROOMNO,
	BEDNO,

	PF_NET,
	PF_WTX_RATE,
	PF_WTX_AMT,
	PF_VAT_RATE,
	PF_VAT_AMT,
	PF_VAT_APPLIED,
	PF_EMPLOYEEID, // DOCTOR IS AN EMPLOYEE
	PF_DEDUCT_MODE, // FIXEDAMOUNT, PERCENTAGE
	PF_DEDUCT_IS_VAT,

	DISCOUNT_ID,
	DISCOUNT_TYPE, // Custom or Fixed
	DISCOUNT_MULTIPLIER, // if Fixed is a number. if Custom its a map
	FROM_INITIAL, // put 1

	COMPANY_ACCOUNT_ID,
	COMPANY_ACCOUNT_CLAIM_REFERENCE,
	PAYTRACKER_ID,
	REAPPLICATION, // Depcreciated
	APPLICATION,
	ORNUMBER,
	VOIDTYPE,
	BILLING_ID,
	ANNOTATIONS
}

@javax.persistence.Entity
@javax.persistence.Table(name = "billing_item", schema = "billing")
class BillingItem extends AbstractAuditingEntity implements Serializable, AutoIntegrateable {

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billing", referencedColumnName = "id")
	Billing billing

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "soa_group_id", referencedColumnName = "id")
	SoaGrouping soaGrouping



	@GraphQLQuery
	@Column(name = "record_no", columnDefinition = "varchar")
	String recordNo

	@GraphQLQuery
	@Enumerated(EnumType.STRING)
	@Column(name = "item_type", columnDefinition = "varchar")
	BillingItemType itemType

	@GraphQLQuery
	@Column(name = "item", columnDefinition = "varchar")
	String item

	@GraphQLQuery
	@Column(name = "for_posting", columnDefinition = "boolean")
	Boolean forPosting

	@GraphQLQuery
	@Column(name = "description", columnDefinition = "varchar")
	String description

	@GraphQLQuery
	@Column(name = "qty", columnDefinition = "decimal")
	Integer qty

	@Deprecated()
	// no longer used by subtotal
	@GraphQLQuery
	@Column(name = "price", columnDefinition = "decimal")
	BigDecimal price

	@GraphQLQuery
	@Column(name = "debit", columnDefinition = "numeric")
	BigDecimal debit

	@GraphQLQuery
	@Column(name = "credit", columnDefinition = "numeric")
	BigDecimal credit

	@GraphQLQuery
	@Column(name = "cogs_per_item", columnDefinition = "numeric")
	BigDecimal cogsPerItem



	@Enumerated(EnumType.STRING)
	@GraphQLQuery
	@Column(name = "status", columnDefinition = "varchar")
	BillingItemStatus status

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "department", referencedColumnName = "id")
	Department department

	@GraphQLQuery
	@Column(name = "postedledger", columnDefinition = "uuid")
	UUID postedledger

	@GraphQLQuery
	@Column(name = "cash_basis_user", columnDefinition = "varchar")
	String cashBasisUser


	@GraphQLQuery
	@Column(name = "canceledref", columnDefinition = "uuid")
	UUID canceledref // /returned ref actually since canceled is true

	@GraphQLQuery
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(schema = "billing", name = "billing_item_details",
			joinColumns = [@JoinColumn(name = "billingitem")])
	@MapKeyColumn(name = "field_name")
	@Column(name = "field_value")
	@BatchSize(size = 20)
	Map<String, String> details = [:]

	@GraphQLQuery(name = "subtotal")
	@Transient

	BigDecimal subTotal
	BigDecimal getSubTotal() {
		def b = (debit ?: BigDecimal.ZERO) - (credit ?: BigDecimal.ZERO)
		b * qty
	}

	@GraphQLQuery(name = "entrydate")
	@Transient
	Instant getEntryDate() {
		transactionDate
	}

	@GraphQLQuery
	@Transient
	String getLastTouch() {

		if(cashBasisUser)
			return cashBasisUser

		lastModifiedBy ?:  ("createdBy" ?: "")
	}

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pricing_tier", referencedColumnName = "id")
	PriceTierDetail priceTierDetail

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "package_id", referencedColumnName = "id")
	Package apackage

	@GraphQLQuery
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(schema = "billing", name = "billingitems_amountdetails",
			joinColumns = [@JoinColumn(name = "billingitem")])
	@MapKeyColumn(name = "billingitemsid")
	@Column(name = "amount")
	@BatchSize(size = 20)
	Map<String, BigDecimal> amountdetails = [:]

	@GraphQLQuery
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(schema = "billing", name = "billingitems_vat_amountdetails",
			joinColumns = [@JoinColumn(name = "billingitem")])
	@MapKeyColumn(name = "billingitemsid")
	@Column(name = "amount")
	@BatchSize(size = 20)
	Map<String, BigDecimal> vatAmountdetails = [:]

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "billingItem", cascade = [javax.persistence.CascadeType.ALL], orphanRemoval = true)
	List<SupportingDoc> supportingDocs = []

	@GraphQLQuery
	@Column(name = "transaction_date")
	Instant transactionDate = Instant.now()
//
//	@Column(name = "transaction_date_only")
//	LocalDate transactionDateOnly
//
//	@PrePersist
//	@PreUpdate
//	void updateTransactionDate() {
//		// Subtract 8 hours from transactionDatetime and assign the date part to transactionDate
//		transactionDateOnly = transactionDate.atOffset(ZoneOffset.UTC).minusHours(8).toLocalDate()
//	}

	@GraphQLQuery
	@Column(name = "recompute_datetime")
	Instant recomputeDatetime

	@GraphQLQuery
	@Column(name = "reapplied_datetime")
	Instant reappliedDatetime


	@GraphQLQuery
	@Column(name = "tagged_cash_allowed_by")
	String taggedCashAllowedBy

	@GraphQLQuery
	@Column(name = "tagged_or_number")
	String taggedOrNumber

	@GraphQLQuery
	@Transient
	String orderSlipItemNo


	@GraphQLQuery
	@Column(name = "payment_recomp", columnDefinition = "boolean")
	Boolean paymentRecomp

	@GraphQLQuery
	@Column(name = "pf_payment_recomp", columnDefinition = "boolean")
	Boolean pfPaymentRecomp


	@GraphQLQuery
	@Column(name = "rf_fee", columnDefinition = "numeric")
	BigDecimal rfFee

	@GraphQLQuery
	@Column(name = "rf_details", columnDefinition = "varchar")
	String rfDetails

	@GraphQLQuery
	@Column(name = "ap_process", columnDefinition = "bool")
	Boolean apProcess


	@GraphQLQuery
	@Column(name = "is_progress", columnDefinition = "bool")
	Boolean isProgress

	@GraphQLQuery
	@Column(name = "registry_type_charged", columnDefinition = "varchar")
	String registryTypeCharged

	@GraphQLQuery
	@Column(name = "vat_output_tax", columnDefinition = "numeric")
	BigDecimal vatOutputTax = 0.0

	@GraphQLQuery
	@Column(name = "annotation_amount", columnDefinition = "numeric")
	BigDecimal annotationAmount = 0.0

	@GraphQLQuery
	@Column(name = "approval_code", columnDefinition = "varchar")
	String approvalCode

	@GraphQLQuery
	@Column(name = "is_billed_ar", columnDefinition = "bool")
	Boolean arBilled


	// use in Recomp
	@Transient
	BigDecimal tmpBalance

	@Override
	String getDomain() {
		return BillingItem.class.name
	}


	@Transient
	String flagValue

	@Transient
	BigDecimal costOfSale

	@Transient
	BigDecimal inventoryDeduct

	@Transient
	Boolean tempCanceled

	@Transient
	Department roomAndBoard



	@Transient
	BigDecimal serviceOverhead

	@Transient
	BigDecimal income


	// Discounts

	@Transient
	Discount discount

	@Transient
	Department discountDepartment


	@Transient
	BigDecimal discountAmount


	@Transient
	BigDecimal discountAmountVat


	@Transient
	BigDecimal discountAmountArOPD

	@Transient
	BigDecimal discountAmountArIP

	@Transient
	BigDecimal discountAmountArER

	@Transient
	BigDecimal discountAmountArOTC

	// === ACCT Category
	@Transient
	AccountingCategory itemCategory

	/// ====== AR Entries
	@Transient
	CompanyAccount  companyAccount

	@Transient
	BigDecimal arInPatient

	@Transient
	BigDecimal arOutPatient

	@Transient
	BigDecimal arErPatient

	@Transient
	BigDecimal arOtcPatient


	@Transient
	BigDecimal arDeductionTotalClearing

	@Transient
	BigDecimal arDeductionTotalDirect


	// Revenue and Costing department
	@Transient
	Department revenueDept

	@Transient
	Department costDept

	@Transient
	Department inventoryDept

	@Transient
	Boolean pnf

	@Transient
	Boolean nonpnf

	//direct materials
	@Transient
	BigDecimal dmInventory

	@Transient
	BigDecimal dmPackage

	@Transient
	BigDecimal dmConsignment

	@Transient
	BigDecimal dmConsignmentPayable
	//end direct materials


}
