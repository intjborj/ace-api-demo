package com.hisd3.hismk2.domain.cashiering

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.accounting.Bank
import com.hisd3.hismk2.domain.accounting.Ledger
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.domain.types.AutoIntegrateable
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import javax.security.auth.callback.CallbackHandler
import java.time.Instant

enum ReceiptType {
	AR,
	OR
}

@Entity
@Table(name = "payment_tracker", schema = "cashiering")
class PaymentTracker extends AbstractAuditingEntity implements Serializable, AutoIntegrateable {
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "paymentTracker")
	List<PaymentTrackerDetails> paymentDetails = []
	
	@GraphQLQuery
	@Column(name = "totalpayments", columnDefinition = "numeric")
	BigDecimal totalpayments
	
	@GraphQLQuery
	@Column(name = "total_cash", columnDefinition = "numeric")
	BigDecimal totalCash
	
	@GraphQLQuery
	@Column(name = "total_check", columnDefinition = "numeric")
	BigDecimal totalCheck
	
	@GraphQLQuery
	@Column(name = "total_card", columnDefinition = "numeric")
	BigDecimal totalCard
	
	@GraphQLQuery
	@Column(name = "total_deposit", columnDefinition = "numeric")
	BigDecimal totalDeposit

	@GraphQLQuery
	@Column(name = "total_e_wallet", columnDefinition = "numeric")
	BigDecimal totalEWallet
	
	@GraphQLQuery
	@Column(name = "change", columnDefinition = "numeric")
	BigDecimal change
	
	@GraphQLQuery
	@Column(name = "hosp", columnDefinition = "numeric")
	BigDecimal hosp
	
	@GraphQLQuery
	@Column(name = "pf", columnDefinition = "numeric")
	BigDecimal pf
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "ornumber", columnDefinition = "varchar")
	String ornumber
	
	@GraphQLQuery
	@Column(name = "completed", columnDefinition = "boolean")
	Boolean completed

	@GraphQLQuery
	@UpperCase
	@Column(name = "payor_name", columnDefinition = "varchar")
	String payorName

	@GraphQLQuery
	@UpperCase
	@Column(name = "reference", columnDefinition = "varchar")
	String reference

	@GraphQLQuery
	@UpperCase
	@Column(name = "description", columnDefinition = "varchar")
	String description

	@GraphQLQuery
	@Column(name = "transaction_category", columnDefinition = "varchar")
	String transactionCategory

	@GraphQLQuery
	@Column(name = "billingid", columnDefinition = "uuid")
	UUID billingid

	@GraphQLQuery
	@Column(name = "investorid", columnDefinition = "uuid")
	UUID investorId

	@GraphQLQuery
	@Column(name = "employee_id", columnDefinition = "uuid")
	UUID employeeId

	@GraphQLQuery
	@Column(name = "supplier_id", columnDefinition = "uuid")
	UUID supplierId

	@GraphQLQuery
	@Column(name = "company_id", columnDefinition = "uuid")
	UUID companyId

	@GraphQLQuery
	@Column(name = "patient_id", columnDefinition = "uuid")
	UUID patientId

	@GraphQLQuery
	@Column(name = "transaction_category_id", columnDefinition = "uuid")
	UUID transactionCategoryId

	@GraphQLQuery
	@Column(name = "ledger_header", columnDefinition = "uuid")
	UUID ledgerHeader


	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shiftid", referencedColumnName = "id")
	Shifting shift
	
	@GraphQLQuery
	@Enumerated(EnumType.STRING)
	@Column(name = "receipt_type", columnDefinition = "varchar")
	ReceiptType receiptType
	
	@GraphQLQuery
	@Column(name = "voided", columnDefinition = "boolean")
	Boolean voided
	
	@GraphQLQuery
	@Column(name = "void_date", columnDefinition = "timestamp")
	Instant voidDate
	
	@GraphQLQuery
	@UpperCase
	@Column(name = "void_type", columnDefinition = "varchar")
	String voidType
	
	@OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "paymentTracker")
	List<PaymentTarget> paymentTargets = []

	@GraphQLQuery
	@OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "paymentTracker")
	List<AppliedOr> appliedOrs = []


	@GraphQLQuery
	@Transient
	BigDecimal totalApplied = 0.0

	BigDecimal getTotalApplied(){
		BigDecimal val = totalApplied
		appliedOrs.each {
			val += it.amount

		}

		val
	}



	@GraphQLQuery
	@Transient
	List<Ledger> allCredits = []





	@Override
	String getDomain() {
		return this.class.name
	}

	@Transient
	String flagValue

	@Override
	Map<String, String> getDetails() {
		return [:]
	}


	// For Autointegration

	@Transient
	CashierTerminal cashierTerminal
	CashierTerminal getCashierTerminal()
	{
		return shift.terminal
	}

	 @Transient
	 Bank  bankForCashDeposit

 	 @Transient
	 Bank  bankForCreditCard

	 @Transient
	 BigDecimal  amountForCreditCard

	 @Transient
	 BigDecimal  amountForCashDeposit



	@Transient
	BigDecimal erPayments


	@Transient
	BigDecimal opdPayments

	@Transient
	BigDecimal ipdPayments

	@Transient
	BigDecimal otcPayments


	@Transient
	BigDecimal pfPaymentsAll


	@Transient
	BigDecimal advancesFromPatients

	//Investors
	@Transient
	BigDecimal subscribedShareCapital, subscriptionReceivable, additionalPaidInCapital, discountOnShareCapital,
	advancesFromInvestors, shareCapital, payableToInvestor

}




