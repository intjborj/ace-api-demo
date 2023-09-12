package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "transaction_type", schema = "accounting")
class TransactionType extends AbstractAuditingEntity implements Serializable {
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "description", columnDefinition = "varchar")
	@UpperCase
	String description

	@GraphQLQuery
	@Column(name = "flag_value", columnDefinition = "varchar")
	@UpperCase
	String flagValue

	@GraphQLQuery
	@Column(name = "is_active", columnDefinition = "bool")
	Boolean status

	@GraphQLQuery
	@Column(name = "asset", columnDefinition = "bool")
	Boolean asset

	@GraphQLQuery
	@Column(name = "consignment", columnDefinition = "bool")
	Boolean consignment
	
	@GraphQLQuery
	@Column(name = "tag", columnDefinition = "varchar")
	@UpperCase
	String tag
}

