package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.Department
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(schema = "inventory", name = "item_without_beg")
class BeginningItem implements Serializable {

	@GraphQLQuery
	@Id
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item", referencedColumnName = "id")
	Item item

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "department", referencedColumnName = "id")
	Department department

	@GraphQLQuery
	@Column(name = "reorder_quantity")
	BigDecimal reorder_quantity

	@GraphQLQuery
	@Column(name = "allow_trade")
	Boolean allow_trade

	@GraphQLQuery
	@Column(name = "is_assign")
	Boolean is_assign

	@GraphQLQuery
	@Column(name = "last_unit_cost")
	BigDecimal lastUnitCost

	@GraphQLQuery
	@Column(name = "reference_no")
	String referenceNo

	@GraphQLQuery
	@Column(name = "document_types")
	UUID documentTypes

	@Transient
	String getUnitMeasurement() {
		return "${item.unit_of_purchase?.unitDescription} (${item.item_conversion} ${item.unit_of_usage?.unitDescription})"
	}

	@Transient
	String getUou() {
		return "${item.unit_of_usage?.unitDescription}"
	}

	@Transient
	String getItemCategory() {
		return "[${item.item_group.itemDescription}] ${item.item_category?.categoryDescription}"
	}
}
