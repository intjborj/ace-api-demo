package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.*

@Entity
@Table(schema = "inventory", name = "unit_measurements")
@SQLDelete(sql = "UPDATE inventory.unit_measurements SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class UnitMeasurement extends AbstractAuditingEntity {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "unit_code")
	String unitCode
	
	@GraphQLQuery
	@Column(name = "unit_description")
	String unitDescription
	
	@GraphQLQuery
	@Column(name = "is_small")
	Boolean isSmall
	
	@GraphQLQuery
	@Column(name = "is_big")
	Boolean isBig
	
	@GraphQLQuery
	@Column(name = "is_active")
	Boolean isActive
}
