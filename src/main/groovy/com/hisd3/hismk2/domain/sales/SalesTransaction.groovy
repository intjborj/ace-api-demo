package com.hisd3.hismk2.domain.sales

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant

@Entity
@Table(schema = "sales", name = "sales_transaction")
class SalesTransaction extends AbstractAuditingEntity {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "customer_name")
	String customerName
	
	@GraphQLQuery
	@Column(name = "customer_id")
	UUID customerId
	
	@GraphQLQuery
	@Column(name = "address")
	String address
	
	@GraphQLQuery
	@Column(name = "date_purchase")
	Instant datePurchase
	
	@GraphQLQuery
	@Column(name = "cashier_name")
	String cashierName
	
	@GraphQLQuery
	@Column(name = "cashier_id")
	UUID cashierId
	
	@GraphQLQuery
	@Column(name = "status")
	String status
	
	@GraphQLQuery
	@Column(name = "deleted")
	Boolean deleted
	
	@GraphQLQuery
	@Column(name = "void")
	Boolean voided
}
