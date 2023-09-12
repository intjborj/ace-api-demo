package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*

@javax.persistence.Entity
@javax.persistence.Table(schema = "inventory", name = "purchase_request_items")
@SQLDelete(sql = "UPDATE inventory.purchase_request_items SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class PurchaseRequestItem extends AbstractAuditingEntity {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "`item`", referencedColumnName = "id")
	Item item
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "`purchase_request`", referencedColumnName = "id")
	PurchaseRequest purchaseRequest
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "`ref_po`", referencedColumnName = "id")
	PurchaseOrder refPo
	
	@GraphQLQuery
	@Column(name = "ref_supitem_id", columnDefinition = "uuid")
	UUID refSupItemId
	
	@GraphQLQuery
	@Column(name = "requested_qty", columnDefinition = "int")
	Integer requestedQty
	
	@GraphQLQuery
	@Column(name = "unit_cost", columnDefinition = "numeric")
	BigDecimal unitCost
	
	@GraphQLQuery
	@Column(name = "total", columnDefinition = "numeric")
	BigDecimal total
	
	@GraphQLQuery
	@Column(name = "deals", columnDefinition = "varchar")
	String deals
	
	@GraphQLQuery
	@Column(name = "last_unit_price", columnDefinition = "numeric")
	BigDecimal lastUnitPrice
	
	@GraphQLQuery
	@Column(name = "on_hand_qty", columnDefinition = "int")
	Integer onHandQty
	
	@GraphQLQuery
	@Column(name = "remarks", columnDefinition = "varchar")
	String remarks

	@Transient
	String getUnitMeasurement() {
		return "${item.unit_of_purchase?.unitDescription} (${item.item_conversion} ${item.unit_of_usage?.unitDescription})"
	}
	
}
