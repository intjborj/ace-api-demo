package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.billing.Billing
import com.hisd3.hismk2.domain.billing.CompanyAccount
import com.hisd3.hismk2.domain.types.AutoIntegrateable
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type
import org.springframework.data.annotation.CreatedDate

import javax.persistence.*
import java.sql.Date
import java.time.Instant

@Entity
@Table(schema = "accounting", name = "ar_manual_deductions")
class ArManualDeductions extends AbstractAuditingEntity implements Serializable{

    @GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@GraphQLQuery
	@Column(name = "record_no")
	String recordNo

	@GraphQLQuery
	@ManyToOne(fetch = FetchType.EAGER)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "billing_id", referencedColumnName = "id")
	Billing billing

	@GraphQLQuery
	@ManyToOne(fetch = FetchType.EAGER)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "company_id", referencedColumnName = "id")
	CompanyAccount companyAccount

	@GraphQLQuery
	@Column(name = "item_type")
	String itemType

	@GraphQLQuery
	@Column(name = "credit")
	BigDecimal credit

	@GraphQLQuery
	@Column(name = "status", columnDefinition = "boolean")
	String status

	@GraphQLQuery
	@Column(name = "remarks")
	String remarks

	@GraphQLQuery
	@CreatedDate
	@Column(name = "transaction_date", nullable = false)
	Instant transactionDate

}
