package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.*
import java.time.LocalDateTime

@Entity
@Table(schema = "inventory", name = "purchase_order_items")
@SQLDelete(sql = "UPDATE inventory.purchase_order_items SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class PurchaseOrderItems extends AbstractAuditingEntity {
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item", referencedColumnName = "id")
	Item item
	
	@GraphQLQuery
	@Column(name = "quantity", columnDefinition = "numeric")
	Integer quantity
	
	@GraphQLQuery
	@Column(name = "qty_in_small", columnDefinition = "int")
	Integer qtyInSmall
	
	@GraphQLQuery
	@Column(name = "pr_nos", columnDefinition = "varchar")
	String prNos
	
	@GraphQLQuery
	@Column(name = "unit_measure", columnDefinition = "varchar")
	Integer unitMeasure
	
	@GraphQLQuery
	@Column(name = "date_completed", columnDefinition = "timestamp")
	LocalDateTime dateCompleted
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "purchase_order", referencedColumnName = "id")
	PurchaseOrder purchaseOrder
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiving_report", referencedColumnName = "id")
	ReceivingReport receivingReport
	
	@GraphQLQuery
	@Column(name = "supplier_last_price", columnDefinition = "numeric")
	BigDecimal supplierLastPrice
	
	@GraphQLQuery
	@Column(name = "delivery_status", columnDefinition = "int")
	Integer deliveryStatus //-- 0: for delivery 1: partial delivery 2: completed
	
	@GraphQLQuery
	@Column(name = "delivery_balance", columnDefinition = "int")
	Integer deliveryBalance
	
	@GraphQLQuery
	@Column(name = "delivered_qty", columnDefinition = "int")
	Integer deliveredQty

	@GraphQLQuery
	@Column(name = "type", columnDefinition = "varchar")
	String type

	@GraphQLQuery
	@Column(name = "type_text", columnDefinition = "varchar")
	String type_text

	@GraphQLQuery(name = "unitMeasurement")
	@Transient
	String getUnitMeasurement() {
		return "${item.unit_of_purchase?.unitDescription} (${item.item_conversion} ${item.unit_of_usage?.unitDescription})"
	}

	@GraphQLQuery(name = "uou")
	@Transient
	String getUou() {
		return "${item.unit_of_usage?.unitDescription}"
	}
	
}
