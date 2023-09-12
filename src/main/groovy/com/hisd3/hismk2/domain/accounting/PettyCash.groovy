package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.domain.inventory.Supplier
import com.hisd3.hismk2.domain.types.AutoIntegrateable
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant

@javax.persistence.Entity
@javax.persistence.Table(name = "petty_cash", schema = "accounting")
class PettyCash extends AbstractAuditingEntity implements Serializable, AutoIntegrateable {

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

	@GraphQLQuery
	@Column(name = "payee_name", columnDefinition = "varchar")
	@UpperCase
	String payeeName

	@GraphQLQuery
	@Column(name = "pcv_no", columnDefinition = "varchar")
	@UpperCase
	String pcvNo

	@GraphQLQuery
	@Column(name = "pcv_date", columnDefinition = "date")
	Instant pcvDate

	@GraphQLQuery
	@Column(name = "amount_issued", columnDefinition = "numeric")
	@UpperCase
	BigDecimal amountIssued

	@GraphQLQuery
	@Column(name = "amount_used", columnDefinition = "bool")
	@UpperCase
	BigDecimal amountUsed

	@GraphQLQuery
	@Column(name = "amount_unused", columnDefinition = "numeric")
	@UpperCase
	BigDecimal amountUnused

	@GraphQLQuery
	@Column(name = "vat_inclusive", columnDefinition = "numeric")
	Boolean vatInclusive

	@GraphQLQuery
	@Column(name = "vat_rate", columnDefinition = "numeric")
	BigDecimal vatRate

	@GraphQLQuery
	@Column(name = "status", columnDefinition = "varchar")
	@UpperCase
	String status

	@GraphQLQuery
	@Column(name = "posted", columnDefinition = "bool")
	Boolean posted

	@GraphQLQuery
	@Column(name = "posted_ledger", columnDefinition = "uuid")
	UUID postedLedger

	@GraphQLQuery
	@Column(name = "remarks", columnDefinition = "varchar")
	@UpperCase
	String remarks

	@GraphQLQuery
	@Column(name = "posted_by", columnDefinition = "varchar")
	String posted_by

	//accounting integrate
	@Override
	String getDomain() {
		return PettyCash.class.name
	}

	@Transient
	String flagValue

	@Override
	Map<String, String> getDetails() {
		return [:]
	}

	@Transient
	BigDecimal advanceAmount, disbursementAmount, discAmount


	
}

