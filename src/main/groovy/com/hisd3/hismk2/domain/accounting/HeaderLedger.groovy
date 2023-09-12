package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset


enum LedgerDocType { //transactype
	DS, // Discount
	AP, // AccountsPayable
	INV, // AccountsReceivableInvoice
	RM, // RoomSales
	CN, // CreditNote
	OR, // OfficialReceipts
	AR, // OfficialReceipts
	JV, // JournalVouchers
	AJ, // PatientAdjustments
	CS, // DisbursementCash
	CK, // DisbursementCheck
	PC, // PettyCash
	CM, // CreditMemo
	DM, // DebitMemo
	// START: inventory transactions//
	RT, // PurchaseReturns
	RR, // ReceivingReports
	EI, // ExpenseIssuances
	SI, // StockIssuances
	QA, // QuantityAdjustments
	PA, // ProductionAssemblies
	AM, // AssetManagement
	// END: inventory transactions//
	CH, // Charges
	CA, // Cash,
	XX, // Generated From Generator - Error when pushed through
	BB // Beginning Balance
}



enum JournalType { //books
	GENERAL, // - General Journal
	DISBURSEMENT, // - CV
	RECEIPTS, // - OR, AR
	SALES, // DM, CM
	PURCHASES_PAYABLES, // APV
	ALL,  // filter,
	XXXX // // Generated From Generator - Error when pushed through
}

enum LedgerHeaderDetailParam {

	QUANTITY,
	PRICETIERDETAILID,
	REGISTRATIONTYPE,
	COGS_PER_ITEM,
	BILLING_ID


}
@Entity
@Table(name = "header_ledger", schema = "accounting")
class HeaderLedger extends AbstractAuditingEntity implements Serializable {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fiscal", referencedColumnName = "id")
	Fiscal fiscal
	
	@GraphQLQuery
	@Column(name = "particulars", columnDefinition = "date")
	@UpperCase
	String particulars
	
	@GraphQLQuery
	@Enumerated(EnumType.STRING)
	@Column(name = "doctype", columnDefinition = "varchar")
	@UpperCase
	LedgerDocType docType
	
	@GraphQLQuery
	@Column(name = "docnum", columnDefinition = "date")
	@UpperCase
	String docnum
	
	@GraphQLQuery
	@Column(name = "transaction_date", columnDefinition = "date")
	@UpperCase
	Instant transactionDate

	@Column(name = "transaction_date_only")
	LocalDate transactionDateOnly

	@PrePersist
	@PreUpdate
	void updateTransactionDate() {
		// Subtract 8 hours from transactionDatetime and assign the date part to transactionDate
		transactionDateOnly = transactionDate.atOffset(ZoneOffset.UTC).plusHours(8).toLocalDate()
	}
	
	@GraphQLQuery
	@Enumerated(EnumType.STRING)
	@Column(name = "journal_type", columnDefinition = "varchar")
	@UpperCase
	JournalType journalType
	
	@GraphQLQuery
	@Column(name = "custom", columnDefinition = "boolean")
	@UpperCase
	Boolean custom
	
	@GraphQLQuery
	@Column(name = "parent_ledger", columnDefinition = "uuid")
	@UpperCase
	UUID parentLedger
	
	@GraphQLQuery
	@Column(name = "beginning_balance", columnDefinition = "boolean")
	@UpperCase
	Boolean beginningBalance
	
	@GraphQLQuery
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "header", cascade = [CascadeType.ALL])
	@OrderBy("debit DESC")
	// orphanRemoval is not used deliberately
	Set<Ledger> ledger = []


	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(schema = "accounting", name = "header_ledger_details",
			joinColumns = [@JoinColumn(name = "header_ledger")])
	@MapKeyColumn(name = "field_name")
	@Column(name = "field_value")
	@BatchSize(size = 20)
	Map<String, String> details = [:]

	@GraphQLQuery
	@Column(name = "entity_name", columnDefinition = "varchar")
	@UpperCase
	String entityName


	@GraphQLQuery
	@Column(name = "invoice_soa_reference", columnDefinition = "varchar")
	@UpperCase
	String invoiceSoaReference

	@GraphQLQuery
	@Column(name = "reversal", columnDefinition = "boolean")
	Boolean reversal

	@GraphQLQuery
	@Column(name = "reapply_payment_tracker", columnDefinition = "uuid")
	UUID reapplyPaymentTracker


	@GraphQLQuery
	@Column(name = "approved_by", columnDefinition = "varchar")
	String approvedBy

	@GraphQLQuery
	@Column(name = "approved_datetime", columnDefinition = "timestamp")
	Instant approvedDatetime


}

