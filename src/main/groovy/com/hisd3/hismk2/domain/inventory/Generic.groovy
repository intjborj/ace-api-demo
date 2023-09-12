package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.*

@Entity
@Table(schema = "inventory", name = "generics")
@SQLDelete(sql = "UPDATE inventory.generics SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class Generic extends AbstractAuditingEntity {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "generic_code")
	String genericCode
	
	@GraphQLQuery
	@Column(name = "generic_description")
	String genericDescription
	
	@GraphQLQuery
	@Column(name = "is_active")
	Boolean isActive

	@GraphQLQuery
	@Column(name = "phic_code")
	String phicCode


	@GraphQLQuery
	@Column(name = "phic_description")
	String phicDescription
}
