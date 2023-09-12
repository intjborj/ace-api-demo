package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.billing.CompanyAccount
import com.hisd3.hismk2.domain.inventory.Supplier
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import org.springframework.data.annotation.CreatedDate

import javax.persistence.*
import java.time.Instant

@Entity
@Table(schema = "accounting", name = "ar_ledger")
class ArLedger extends AbstractAuditingEntity implements Serializable{

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@GraphQLQuery
	@Column(name = "ledger_no")
	String ledgerNo

	@GraphQLQuery
	@Column(name = "reference")
	String reference

	@GraphQLQuery
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_receivable", referencedColumnName = "id")
	AccountReceivable accountReceivable

	@GraphQLQuery
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company", referencedColumnName = "id")
	CompanyAccount companyAccount

	@GraphQLQuery
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "personal_account", referencedColumnName = "id")
	Supplier personalAccount

	@GraphQLQuery
	@Column(name = "description")
	String description

	@GraphQLQuery
	@Column(name = "debit")
	BigDecimal debit

	@GraphQLQuery
	@Column(name = "credit")
	BigDecimal credit

	@GraphQLQuery
	@Column(name = "balance")
	BigDecimal balance

	@GraphQLQuery
	@Column(name = "journal_ledger", columnDefinition = "uuid")
	UUID journalLedger

	@GraphQLQuery
	@CreatedDate
	@Column(name = "ledger_date", nullable = false)
	Instant ledgerDate

	@GraphQLQuery
	@Column(name = "status")
	String status

}
