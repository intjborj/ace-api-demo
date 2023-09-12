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

@Entity
@Table(schema = "inventory", name = "supplier_item")
@SQLDelete(sql = "UPDATE inventory.supplier_item SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class SupplierItem extends AbstractAuditingEntity {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "supplier", referencedColumnName = "id")
	Supplier supplier
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "item_id", referencedColumnName = "id")
	Item item
	
	@GraphQLQuery
	@Column(name = "cost")
	BigDecimal cost
	
	@Transient
	String getDescLong() {
		return item.descLong
	}

	@Transient
	String getBrand() {
		return item.brand
	}
	
	@Transient
	String getUnitOfPurchase() {
		return item.unit_of_purchase
	}

	@Transient
	String getUnitMeasurement() {
		return "${item.unit_of_purchase?.unitDescription} (${item.item_conversion} ${item.unit_of_usage?.unitDescription})"
	}

	@Transient
	String getGenericName() {
		return item.item_generics.genericDescription
	}


	@Transient
	String getItemId() {
		return item.id
	}
	
}
