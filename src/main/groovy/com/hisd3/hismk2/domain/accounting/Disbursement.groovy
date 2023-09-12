package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.domain.cashiering.CashierTerminal
import com.hisd3.hismk2.domain.inventory.PaymentTerm
import com.hisd3.hismk2.domain.inventory.ReceivingReport
import com.hisd3.hismk2.domain.inventory.Supplier
import com.hisd3.hismk2.domain.types.AutoIntegrateable
import com.sun.org.apache.xpath.internal.operations.Bool
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant

@javax.persistence.Entity
@javax.persistence.Table(name = "disbursement", schema = "accounting")
class Disbursement extends AbstractAuditingEntity implements Serializable, AutoIntegrateable {

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "supplier", referencedColumnName = "id")
	Supplier supplier

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "trans_type", referencedColumnName = "id")
	ApTransaction transType

	@GraphQLQuery
	@Column(name = "payee_name", columnDefinition = "varchar")
	@UpperCase
	String payeeName
	
	@GraphQLQuery
	@Column(name = "dis_no", columnDefinition = "varchar")
	@UpperCase
	String disNo

	@GraphQLQuery
	@Column(name = "payment_cat", columnDefinition = "varchar")
	@UpperCase
	String paymentCategory

	@GraphQLQuery
	@Column(name = "dis_type", columnDefinition = "varchar")
	@UpperCase
	String disType

	@GraphQLQuery
	@Column(name = "dis_date", columnDefinition = "date")
	Instant disDate

	@GraphQLQuery
	@Column(name = "cash", columnDefinition = "numeric")
	@UpperCase
	BigDecimal cash

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "terminal", referencedColumnName = "id")
	CashierTerminal terminal

	@GraphQLQuery
	@Column(name = "checks", columnDefinition = "numeric")
	@UpperCase
	BigDecimal checks

	@GraphQLQuery
	@Column(name = "other_credits", columnDefinition = "numeric")
	@UpperCase
	BigDecimal otherCredits

	@GraphQLQuery
	@Column(name = "discount_amount", columnDefinition = "numeric")
	@UpperCase
	BigDecimal discountAmount

	@GraphQLQuery
	@Column(name = "ewt_amount", columnDefinition = "bool")
	@UpperCase
	BigDecimal ewtAmount

	@GraphQLQuery
	@Column(name = "voucher_amount", columnDefinition = "numeric")
	@UpperCase
	BigDecimal voucherAmount

	@GraphQLQuery
	@Column(name = "applied_amount", columnDefinition = "numeric")
	@UpperCase
	BigDecimal appliedAmount

	@GraphQLQuery
	@Column(name = "status", columnDefinition = "varchar")
	@UpperCase
	String status

	@GraphQLQuery
	@Column(name = "is_advance", columnDefinition = "bool")
	Boolean isAdvance

	@GraphQLQuery
	@Column(name = "posted", columnDefinition = "bool")
	@UpperCase
	Boolean posted

	@GraphQLQuery
	@Column(name = "posted_by", columnDefinition = "varchar")
	@UpperCase
	String postedBy

	@GraphQLQuery
	@Column(name = "posted_ledger", columnDefinition = "uuid")
	UUID postedLedger

	@GraphQLQuery
	@Column(name = "is_release", columnDefinition = "bool")
	Boolean isRelease

	@GraphQLQuery
	@Column(name = "remarks_notes", columnDefinition = "varchar")
	@UpperCase
	String remarksNotes

	@GraphQLQuery
	@Column(name = "reapplication", columnDefinition = "uuid")
	UUID reapplication

	@GraphQLQuery
	@Column(name = "rounding", columnDefinition = "int")
	Integer rounding


	//accounting integrate
	@Override
	String getDomain() {
		return Disbursement.class.name
	}

	@Transient
	String flagValue

	@Override
	Map<String, String> getDetails() {
		return [:]
	}

	@Transient
	Bank bank

	@Transient
	Department department

	@Transient
	BigDecimal cashOnHand, cashOnBank, supplierAmount, advancesSupplier, discAmount

	@Transient
	BigDecimal ewt1Percent,ewt2Percent,
			ewt3Percent,ewt4Percent,ewt5Percent,ewt7Percent,ewt10Percent,ewt15Percent,ewt18Percent,ewt30Percent

	//expense
	@Transient
	BigDecimal value_a,value_b,
			value_c,value_d,value_e,value_f,value_g,value_h,value_i,value_j,
			value_k,value_l,value_m,value_n,value_o,value_p,value_q,value_r,
			value_s,value_t,value_u, value_v, value_w, value_x, value_y, value_z

	@Transient
	BigDecimal value_z1,value_z2,
			   value_z3,value_z4,value_z5,value_z6,value_z7,value_z8,value_z9,value_z10,
			   value_z11,value_z12,value_z13,value_z14,value_z15,value_z16,value_z17,value_z18,
			   value_z19,value_z20,value_z21, value_z22, value_z23, value_z24, value_z25, value_z26



}

