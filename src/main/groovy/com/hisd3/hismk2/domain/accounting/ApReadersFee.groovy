package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.ancillary.RfFees
import com.hisd3.hismk2.domain.ancillary.Service
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.domain.billing.Billing
import com.hisd3.hismk2.domain.billing.BillingItem
import com.hisd3.hismk2.domain.billing.PriceTierDetail
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.inventory.Supplier
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant

@javax.persistence.Entity
@javax.persistence.Table(schema = "accounting", name = "readers_fees")
class ApReadersFee implements Serializable {

	@GraphQLQuery
	@Id
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@GraphQLQuery
	@Column(name = "transaction_date")
	Instant transactionDate

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "billing", referencedColumnName = "id")
	Billing billing

	@GraphQLQuery
	@Column(name = "record_no", columnDefinition = "varchar")
	String recordNo

	@GraphQLQuery
	@Column(name = "description", columnDefinition = "varchar")
	@UpperCase
	String description

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "department", referencedColumnName = "id")
	Department department

	@GraphQLQuery
	@Column(name = "price", columnDefinition = "numeric")
	BigDecimal price

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "pricing_tier", referencedColumnName = "id")
	PriceTierDetail pricingTier

	@GraphQLQuery
	@Column(name = "package_id", columnDefinition = "uuid")
	UUID package_id

	@GraphQLQuery
	@Column(name = "rf_fee", columnDefinition = "numeric")
	BigDecimal rfFee

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "serviceid", referencedColumnName = "id")
	Service service

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "doctorsid", referencedColumnName = "id")
	Employee doctor

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "supplier", referencedColumnName = "id")
	Supplier supplier

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "rftableid", referencedColumnName = "id")
	RfFees rfFees

	@GraphQLQuery
	@Column(name = "percentage", columnDefinition = "numeric")
	BigDecimal percentage

	@GraphQLQuery
	@Column(name = "registry_type_charged", columnDefinition = "varchar")
	String registryTypeCharged

	@GraphQLQuery
	@Column(name = "ap_process", columnDefinition = "bool")
	Boolean apProcess

	
}

