package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.domain.inventory.PaymentTerm
import com.hisd3.hismk2.domain.inventory.ReceivingReport
import com.hisd3.hismk2.domain.inventory.Supplier
import com.hisd3.hismk2.domain.types.AutoIntegrateable
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*
import java.time.Instant

@javax.persistence.Entity
@javax.persistence.Table(name = "payables", schema = "accounting")
class AccountsPayable extends AbstractAuditingEntity implements Serializable, AutoIntegrateable {

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "receiving", referencedColumnName = "id")
	ReceivingReport receiving
	
	@GraphQLQuery
	@Column(name = "ap_no", columnDefinition = "varchar")
	@UpperCase
	String apNo

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "supplier", referencedColumnName = "id")
	Supplier supplier

	@GraphQLQuery
	@Column(name = "ap_category", columnDefinition = "varchar")
	@UpperCase
	String apCategory

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "payment_terms", referencedColumnName = "id")
	PaymentTerm paymentTerms

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "trans_type", referencedColumnName = "id")
	ApTransaction transType

	@GraphQLQuery
	@Column(name = "apv_date", columnDefinition = "date")
	@UpperCase
	Instant apvDate

	@GraphQLQuery
	@Column(name = "invoice_no", columnDefinition = "varchar")
	@UpperCase
	String invoiceNo

	@GraphQLQuery
	@Column(name = "gross_amount", columnDefinition = "numeric")
	@UpperCase
	BigDecimal grossAmount

	@GraphQLQuery
	@Column(name = "discount_amount", columnDefinition = "numeric")
	@UpperCase
	BigDecimal discountAmount

	@GraphQLQuery
	@Column(name = "net_of_discount", columnDefinition = "numeric")
	@UpperCase
	BigDecimal netOfDiscount

	@GraphQLQuery
	@Column(name = "vat_rate", columnDefinition = "numeric")
	@UpperCase
	BigDecimal vatRate

	@GraphQLQuery
	@Column(name = "vat_inclusive", columnDefinition = "bool")
	@UpperCase
	Boolean vatInclusive

	@GraphQLQuery
	@Column(name = "vat_amount", columnDefinition = "numeric")
	@UpperCase
	BigDecimal vatAmount

	@GraphQLQuery
	@Column(name = "net_of_vat", columnDefinition = "numeric")
	@UpperCase
	BigDecimal netOfVat

	@GraphQLQuery
	@Column(name = "ewt_amount", columnDefinition = "numeric")
	@UpperCase
	BigDecimal ewtAmount

	@GraphQLQuery
	@Column(name = "net_amount", columnDefinition = "numeric")
	@UpperCase
	BigDecimal netAmount

	@GraphQLQuery
	@Column(name = "applied_amount", columnDefinition = "numeric")
	@UpperCase
	BigDecimal appliedAmount

	@GraphQLQuery
	@Column(name = "da_amount", columnDefinition = "numeric")
	@UpperCase
	BigDecimal daAmount

	@GraphQLQuery
	@Column(name = "dm_amount", columnDefinition = "numeric")
	@UpperCase
	BigDecimal dmAmount

	@GraphQLQuery
	@Column(name = "disbursement", columnDefinition = "varchar")
	String disbursement

	@GraphQLQuery
	@Column(name = "status", columnDefinition = "varchar")
	@UpperCase
	String status

	@GraphQLQuery
	@Column(name = "posted", columnDefinition = "bool")
	@UpperCase
	Boolean posted

	@GraphQLQuery
	@Column(name = "posted_by", columnDefinition = "varchar")
	@UpperCase
	String postedBy

	@GraphQLQuery
	@Column(name = "remarks_notes", columnDefinition = "varchar")
	@UpperCase
	String remarksNotes

	@GraphQLQuery
	@Column(name = "posted_ledger", columnDefinition = "uuid")
	UUID postedLedger

	@GraphQLQuery
	@Column(name = "dm_ref_no", columnDefinition = "varchar")
	@UpperCase
	String dmRefNo

	@GraphQLQuery
	@Column(name = "due_date", columnDefinition = "date")
	Instant dueDate

	@GraphQLQuery
	@Column(name = "rounding", columnDefinition = "int")
	Integer rounding

	@GraphQLQuery(name = "balance")
	@Transient
	BigDecimal balance
	BigDecimal getBalance() {
		def b = (netAmount ?: BigDecimal.ZERO) - (appliedAmount ?: BigDecimal.ZERO) - (dmAmount ?: BigDecimal.ZERO) - (daAmount ?: BigDecimal.ZERO)
		b
	}

	@GraphQLQuery(name = "debitAmount")
	@Transient
	BigDecimal debitAmount
	BigDecimal getDebitAmount() {
		def a = (dmAmount ?: BigDecimal.ZERO) + (daAmount ?: BigDecimal.ZERO)
		a
	}


	//accounting integrate
	@Override
	String getDomain() {
		return AccountsPayable.class.name
	}

	@Transient
	String flagValue

	@Override
	Map<String, String> getDetails() {
		return [:]
	}

	@Transient
	BigDecimal clearingAmount, supplierAmount, discAmount

	@Transient
	BigDecimal doctorFee, dueToDoctors, readersFee

	@Transient
	BigDecimal ewt1Percent,ewt2Percent,
			ewt3Percent,ewt4Percent,ewt5Percent,ewt7Percent,ewt10Percent,ewt15Percent,ewt18Percent,ewt30Percent



}

