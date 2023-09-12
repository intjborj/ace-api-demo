package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.domain.inventory.Supplier
import com.hisd3.hismk2.domain.inventory.SupplierType
import com.hisd3.hismk2.domain.types.AutoIntegrateable
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*

@javax.persistence.Entity
@javax.persistence.Table(name = "reapplication", schema = "accounting")
class Reapplication extends AbstractAuditingEntity implements Serializable, AutoIntegrateable {

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "transaction_type", referencedColumnName = "id")
	ApTransaction transType

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "supplier", referencedColumnName = "id")
	Supplier supplier

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "disbursement", referencedColumnName = "id")
	Disbursement disbursement

	@GraphQLQuery
	@Column(name = "discount_amount", columnDefinition = "numeric")
	@UpperCase
	BigDecimal discountAmount

	@GraphQLQuery
	@Column(name = "ewt_amount", columnDefinition = "bool")
	@UpperCase
	BigDecimal ewtAmount

	@GraphQLQuery
	@Column(name = "applied_amount", columnDefinition = "numeric")
	@UpperCase
	BigDecimal appliedAmount

	@GraphQLQuery
	@Column(name = "prev_applied", columnDefinition = "numeric")
	@UpperCase
	BigDecimal prevApplied

	@GraphQLQuery
	@Column(name = "status", columnDefinition = "varchar")
	@UpperCase
	String status

	@GraphQLQuery
	@Column(name = "is_posted", columnDefinition = "bool")
	Boolean posted

	@GraphQLQuery
	@Column(name = "posted_ledger", columnDefinition = "uuid")
	UUID postedLedger

	@GraphQLQuery
	@Column(name = "remarks", columnDefinition = "varchar")
	@UpperCase
	String remarks

	@GraphQLQuery
	@Column(name = "rounding", columnDefinition = "int")
	Integer rounding

	//accounting integrate
	@Override
	String getDomain() {
		return Reapplication.class.name
	}

	@Transient
	String flagValue

	@Override
	Map<String, String> getDetails() {
		return [:]
	}

	@Transient
	BigDecimal advanceAmount, disbursementAmount, discAmount

	@Transient
	BigDecimal ewt1Percent,ewt2Percent,
			   ewt3Percent,ewt4Percent,ewt5Percent,ewt7Percent,ewt10Percent,ewt15Percent,ewt18Percent,ewt30Percent

	
}

