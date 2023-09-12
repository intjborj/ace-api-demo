package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.rest.InventoryResource
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant

@Entity
@Table(schema = "inventory", name = "inventory")
class Inventory implements Serializable {

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
	Integer reOrderQty

	@GraphQLQuery
	@Column(name = "onhand")
	Integer onHand

	@GraphQLQuery
	@Column(name = "last_unit_cost")
	BigDecimal lastUnitCost //last unit cost upon receiving

	//@GraphQLQuery
	//@Column(name = "last_wcost")
	//BigDecimal last_wcost //cost of sale (unit cost for charging, transfer, physical count, etc.)
	//last_wcost g balhin nako sa contex

	@GraphQLQuery
	@Column(name = "expiration_date")
	Instant expiration_date

	@GraphQLQuery
	@Column(name = "allow_trade")
	Boolean allowTrade

	@GraphQLQuery
	@Column(name = "desc_long")
	String descLong

	@GraphQLQuery
	@Column(name = "sku")
	String sku

	@GraphQLQuery
	@Column(name = "item_code")
	String itemCode

	@GraphQLQuery
	@Column(name = "dep_id")
	UUID depId

	@GraphQLQuery
	@Column(name = "item_id")
	UUID itemId

	@GraphQLQuery
	@Column(name = "item_group")
	UUID item_group

	@GraphQLQuery
	@Column(name = "item_category")
	UUID item_category

	@GraphQLQuery
	@Column(name = "active")
	Boolean active

	@GraphQLQuery
	@Column(name = "production")
	Boolean production

	@GraphQLQuery
	@Column(name = "is_medicine")
	Boolean isMedicine

	@GraphQLQuery
	@Column(name = "fluid")
	Boolean fluid

	@GraphQLQuery
	@Column(name = "consignment")
	Boolean consignment

	@GraphQLQuery
	@Column(name = "fix_asset")
	Boolean fixAsset

	@GraphQLQuery(name = "status")
	@Transient
	String status
	String getStatus() {
		def res = "Critical"
		if(reOrderQty < onHand){
			res = "Healthy"
		}
		if(onHand == 0){
			res = "No Stock"
		}
		if(onHand < 0){
			res = "Negative Stock"
		}
		res
	}

	@GraphQLQuery
	@Transient
	BigDecimal calculatedAmount

	@Transient
	String getUnitMeasurement() {
		return "${item.unit_of_purchase?.unitDescription} (${item.item_conversion} ${item.unit_of_usage?.unitDescription})"
	}

	@Transient
	String getItemCategory() {
		return "[${item.item_group.itemDescription}] ${item.item_category?.categoryDescription}"
	}

	@Transient
	String getBrand() {
		return item.brand
	}
}
