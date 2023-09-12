package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.annotations.UpperCase
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*

@javax.persistence.Entity
@javax.persistence.Table(schema = "inventory", name = "quantity_adjustment_type")
@SQLDelete(sql = "UPDATE inventory.quantity_adjustment_type SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class QuantityAdjustmentType extends AbstractAuditingEntity implements Serializable {

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@GraphQLQuery
	@Column(name = "code", columnDefinition = 'varchar')
	String code

	@GraphQLQuery
	@Column(name = "description", columnDefinition = 'varchar')
	@UpperCase
	String description

	@GraphQLQuery
	@Column(name = "flag_value", columnDefinition = 'varchar')
	String flagValue

	@GraphQLQuery
	@Column(name = "source_value", columnDefinition = 'varchar')
	String sourceValue

	@GraphQLQuery
	@Column(name = "reverse", columnDefinition = 'varchar')
	Boolean reverse

	@GraphQLQuery
	@Column(name = "is_active", columnDefinition = 'bool')
	Boolean is_active

}
