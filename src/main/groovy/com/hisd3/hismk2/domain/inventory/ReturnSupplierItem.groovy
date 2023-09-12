package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*

@javax.persistence.Entity
@javax.persistence.Table(schema = "inventory", name = "return_supplier_items")
@SQLDelete(sql = "UPDATE inventory.return_supplier_items SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class ReturnSupplierItem extends AbstractAuditingEntity {
	
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
	@JoinColumn(name = "return_supplier", referencedColumnName = "id")
	ReturnSupplier returnSupplier
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item", referencedColumnName = "id")
	Item item
	
	@GraphQLQuery
	@Column(name = "return_qty", columnDefinition = 'int')
	Integer returnQty
	
	@GraphQLQuery
	@Column(name = "original_qty", columnDefinition = 'int')
	Integer originalQty
	
	@GraphQLQuery
	@Column(name = "return_unit_cost", columnDefinition = 'numeric')
	BigDecimal returnUnitCost
	
	@GraphQLQuery
	@Column(name = "return_remarks", columnDefinition = 'text')
	String return_remarks
	
	@GraphQLQuery
	@Column(name = "is_posted", columnDefinition = 'bool')
	Boolean isPosted

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
