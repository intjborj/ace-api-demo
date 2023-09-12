package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.*

@Entity
@Table(schema = "inventory", name = "payment_terms")
@SQLDelete(sql = "UPDATE inventory.payment_terms SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class PaymentTerm extends AbstractAuditingEntity {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "payment_term_code")
	String paymentCode
	
	@GraphQLQuery
	@Column(name = "payment_term_description")
	String paymentDesc
	
	@GraphQLQuery
	@Column(name = "payment_term_days")
	Integer paymentNoDays
	
	@GraphQLQuery
	@Column(name = "is_active")
	Boolean isActive
	
}
