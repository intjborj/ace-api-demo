package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.domain.inventory.PaymentTerm
import com.hisd3.hismk2.domain.inventory.ReceivingReport
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
@javax.persistence.Table(name = "disbursement_check", schema = "accounting")
class DisbursementCheck extends AbstractAuditingEntity implements Serializable {

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "disbursement", referencedColumnName = "id")
	Disbursement disbursement

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "bank", referencedColumnName = "id")
	Bank bank

	@GraphQLQuery
	@Column(name = "bank_branch", columnDefinition = "varchar")
	@UpperCase
	String bankBranch

	@GraphQLQuery
	@Column(name = "check_no", columnDefinition = "varchar")
	@UpperCase
	String checkNo

	@GraphQLQuery
	@Column(name = "check_date", columnDefinition = "date")
	@UpperCase
	Instant checkDate

	@GraphQLQuery
	@Column(name = "amount", columnDefinition = "numeric")
	@UpperCase
	BigDecimal amount

	@GraphQLQuery
	@Column(name = "releasing", columnDefinition = "uuid")
	UUID releasing


}

