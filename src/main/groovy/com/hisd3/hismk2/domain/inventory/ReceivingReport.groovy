package com.hisd3.hismk2.domain.inventory

import com.fasterxml.jackson.annotation.JsonIgnore
import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.fixed_assets.FixedAssetCategory
import com.hisd3.hismk2.domain.types.AutoIntegrateable
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*
import java.time.Instant

@javax.persistence.Entity
@javax.persistence.Table(schema = "inventory", name = "receiving_report")
@SQLDelete(sql = "UPDATE inventory.receiving_report SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class ReceivingReport extends AbstractAuditingEntity implements AutoIntegrateable {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "received_type", columnDefinition = 'varchar')
	String receivedType
	
	@GraphQLQuery
	@Column(name = "received_no", columnDefinition = 'varchar')
	String rrNo
	
	@GraphQLQuery
	@Column(name = "received_date", columnDefinition = 'timestamp without time zone')
	Instant receiveDate
	
	@GraphQLQuery
	@Column(name = "user_id", columnDefinition = "uuid")
	UUID userId
	
	@GraphQLQuery
	@Column(name = "user_fullname", columnDefinition = "varchar")
	String userFullname
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "purchase_order", referencedColumnName = "id")
	PurchaseOrder purchaseOrder
	
	@GraphQLQuery
	@Column(name = "received_ref_no", columnDefinition = 'varchar')
	String receivedRefNo
	
	@GraphQLQuery
	@Column(name = "received_ref_date", columnDefinition = 'timestamp without time zone')
	Instant receivedRefDate
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "received_dep", referencedColumnName = "id")
	Department receiveDepartment
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "supplier", referencedColumnName = "id")
	Supplier supplier
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "payment_terms", referencedColumnName = "id")
	PaymentTerm paymentTerms
	
	@GraphQLQuery
	@Column(name = "received_remarks", columnDefinition = 'text')
	String receivedRemarks
	
	@GraphQLQuery
	@Column(name = "fix_discount", columnDefinition = 'numeric')
	BigDecimal fixDiscount
	
	@GraphQLQuery
	@Column(name = "gross_amount", columnDefinition = 'numeric')
	BigDecimal grossAmount
	
	@GraphQLQuery
	@Column(name = "total_discount", columnDefinition = 'numeric')
	BigDecimal totalDiscount
	
	@GraphQLQuery
	@Column(name = "net_of_discount", columnDefinition = 'numeric')
	BigDecimal netDiscount
	
	@GraphQLQuery
	@Column(name = "amount", columnDefinition = 'numeric')
	BigDecimal amount
	
	@GraphQLQuery
	@Column(name = "vat_rate", columnDefinition = 'numeric')
	BigDecimal vatRate
	
	@GraphQLQuery
	@Column(name = "input_tax", columnDefinition = 'numeric')
	BigDecimal inputTax
	
	@GraphQLQuery
	@Column(name = "net_amount", columnDefinition = 'numeric')
	BigDecimal netAmount
	
	@GraphQLQuery
	@Column(name = "vat_inclusive", columnDefinition = 'bool')
	Boolean vatInclusive
	
	@GraphQLQuery
	@Column(name = "is_posted", columnDefinition = 'bool')
	Boolean isPosted
	
	@GraphQLQuery
	@Column(name = "is_void", columnDefinition = 'bool')
	Boolean isVoid
	
	@GraphQLQuery
	@Column(name = "acct_type", columnDefinition = 'uuid')
	UUID account

	@GraphQLQuery
	@Column(name = "posted_ledger", columnDefinition = 'uuid')
	UUID postedLedger

	@GraphQLQuery
	@Column(name = "ref_ap", columnDefinition = 'uuid')
	UUID refAp

	@GraphQLQuery
	@Column(name = "consignment", columnDefinition = "bool")
	Boolean consignment

	@GraphQLQuery
	@Column(name = "asset", columnDefinition = "bool")
	Boolean asset
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "receivingReport")
	Set<ReceivingReportItem> receivingItems = [] as Set
	
	@JsonIgnore
	@Transient
	Instant getDateCreated() {
		return createdDate
	}
	
	@JsonIgnore
	@Transient
	String getCreatedByString() {
		return createdBy
	}

	//accounting
	@Override
	String getDomain() {
		return ReceivingReport.class.name
	}

	@Transient
	String flagValue

	@Override
	Map<String, String> getDetails() {
		return [:]
	}

	@Transient
	AccountingCategory category

	@Transient
	BigDecimal cost, supplies, medicine, clearingAmount, fixedAsset

	@Transient
	BigDecimal asset_a,asset_b,asset_c,asset_d,asset_e,asset_f,
			asset_g,asset_h,asset_i,asset_j,asset_k,asset_l,asset_m,asset_n,asset_o,
			asset_p,asset_q,asset_r,asset_s,asset_t,asset_u,asset_v,asset_w,asset_x,
			asset_y,asset_z

	@Transient
	BigDecimal expense_a,expense_b,expense_c,expense_d,expense_e,expense_f,
			expense_g,expense_h,expense_i,expense_j,expense_k,expense_l,expense_m,expense_n,expense_o,
			expense_p,expense_q,expense_r,expense_s,expense_t,expense_u,expense_v,expense_w,expense_x,
			expense_y,expense_z

	@Transient
	FixedAssetCategory fixedAssetCategory

}
