package com.hisd3.hismk2.domain.accounting

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.Id

@javax.persistence.Entity
@javax.persistence.Table(name = "expense_trans_type", schema = "accounting")
class ExpenseTransaction extends AbstractAuditingEntity implements Serializable {

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
	@Column(name = "type", columnDefinition = "varchar")
	String type

	@GraphQLQuery
	@Column(name = "source", columnDefinition = "varchar")
	String source

	@GraphQLQuery
	@Column(name = "is_active", columnDefinition = "bool")
	@UpperCase
	Boolean status

	@GraphQLQuery
	@Column(name = "is_reverse", columnDefinition = "bool")
	@UpperCase
	Boolean isReverse

	@GraphQLQuery
	@Column(name = "remarks", columnDefinition = "varchar")
	@UpperCase
	String remarks

}

