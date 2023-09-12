package com.hisd3.hismk2.domain.sales

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.inventory.Item
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(schema = "sales", name = "sales_transaction_item")
class SalesTransactionItem extends AbstractAuditingEntity {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "sales_transaction", referencedColumnName = "id")
	SalesTransaction salesTransaction
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "item", referencedColumnName = "id")
	Item item
	
	@GraphQLQuery
	@Column(name = "qty")
	BigDecimal qty
	
	@GraphQLQuery
	@Column(name = "base_price")
	BigDecimal basePrice
	
	@GraphQLQuery
	@Column(name = "amount")
	BigDecimal amount
	
	@GraphQLQuery
	@Column(name = "non_vat")
	Boolean nonVat
	
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
