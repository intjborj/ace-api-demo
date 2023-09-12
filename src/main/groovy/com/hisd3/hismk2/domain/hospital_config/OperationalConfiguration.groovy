package com.hisd3.hismk2.domain.hospital_config

import com.hisd3.hismk2.graphqlservices.accounting.ChartOfAccountGenerate
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*

@Canonical
class ContactNumbers {
	String contactNum
	Boolean active
}

@Entity
@Table(schema = "hospital_configuration", name = "operational_configuration")
class OperationalConfiguration {

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@GraphQLQuery
	@Column(name = "order_posting_empty", columnDefinition = "bool")
	Boolean orderPostingEmpty

	@GraphQLQuery
	@Column(name = "default_credit_limit", columnDefinition = "numeric")
	BigDecimal defaultCreditLimit

	@GraphQLQuery
	@Column(name = "room_in_deduction", columnDefinition = "numeric")
	BigDecimal roomInDeduction

	@GraphQLQuery
	@Column(name = "auto_lock_ipd", columnDefinition = "boolean")
	Boolean autolockIpd

	@GraphQLQuery
	@Column(name = "auto_lock_opd", columnDefinition = "boolean")
	Boolean autolockOpd

	@GraphQLQuery
	@Column(name = "auto_lock_er", columnDefinition = "boolean")
	Boolean autolockEr

	@GraphQLQuery
	@Column(name = "allow_phic_onempty_doh_icd", columnDefinition = "boolean")
	Boolean  allowPhicEmptyDocIcd

	@GraphQLQuery
	@Column(name = "allow_rvs_onempty_doh_ops", columnDefinition = "boolean")
	Boolean  allowRvsEmptyDohOps

	@GraphQLQuery
	@Column(name = "ably_notification", columnDefinition = "boolean")
	Boolean ablyNotification;

	@GraphQLQuery
	@Type(type = "jsonb")
	@Column(name="contact_numbers",columnDefinition = "jsonb")
	List<ContactNumbers> contactNumbers
}
