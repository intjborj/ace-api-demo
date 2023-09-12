package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.fixed_assets.FixedAssetCategory
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.ResultCheckStyle
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.*

@Entity
@Table(schema = "inventory", name = "item")
@SQLDelete(sql = "UPDATE inventory.item SET deleted= true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class Item extends AbstractAuditingEntity implements Serializable {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "stock_code")
	String stockCode
	
	@GraphQLQuery
	@Column(name = "barcode")
	String barcode
	
	@GraphQLQuery
	@Column(name = "desc_long")
	String descLong
	
	@GraphQLQuery
	@Column(name = "unit_measure")
	String unitMeasure
	
	@GraphQLQuery
	@Column(name = "base_price")
	BigDecimal actualUnitCost
	
	@GraphQLQuery
	@Column(name = "otc_price")
	BigDecimal otcPrice
	
	@GraphQLQuery
	@Column(name = "active")
	Boolean active
	
	@GraphQLQuery
	@Column(name = "brand")
	String brand
	
	@GraphQLQuery
	@Column(name = "generic_name")
	String genericName
	
	@GraphQLQuery
	@Column(name = "is_medicine")
	Boolean isMedicine
	
	@GraphQLQuery
	@Column(name = "sku")
	String sku
	
	@GraphQLQuery
	@Column(name = "vatable")
	Boolean vatable
	
	@GraphQLQuery
	@Column(name = "fluid")
	Boolean fluid
	
	@GraphQLQuery
	@Column(name = "gas")
	Boolean gas
	
	@GraphQLQuery
	@Column(name = "consignor")
	UUID consignor
	
	@GraphQLQuery
	@Column(name = "consignment")
	Boolean consignment
	
	@GraphQLQuery
	@Column(name = "consignor_name")
	String consignorName
	
	@GraphQLQuery
	@Column(name = "comlogic_medicine_code")
	String comlogicMedicineCode
	
	@GraphQLQuery
	@Column(name = "comlogic_medicine_generic_code")
	String comlogicMedicineGenericCode
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_group", referencedColumnName = "id")
	ItemGroup item_group
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_category", referencedColumnName = "id")
	ItemCategory item_category
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_generics", referencedColumnName = "id")
	Generic item_generics
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unit_of_purchase", referencedColumnName = "id")
	UnitMeasurement unit_of_purchase
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unit_of_usage", referencedColumnName = "id")
	UnitMeasurement unit_of_usage

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "accounting_category", referencedColumnName = "id")
	AccountingCategory accountingCategory

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fixed_asset_category", referencedColumnName = "id")
	FixedAssetCategory fixedAssetCategory

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "accounting_expense_category", referencedColumnName = "id")
	AccountingCategory accountingExpenseCategory

	@GraphQLQuery
	@Column(name = "item_conversion")
	Integer item_conversion
	
	@GraphQLQuery
	@Column(name = "item_code")
	String itemCode
	
	@GraphQLQuery
	@Column(name = "item_demand_qty")
	BigDecimal item_demand_qty
	
	@GraphQLQuery
	@Column(name = "item_maximum")
	BigDecimal item_maximum
	
	@GraphQLQuery
	@Column(name = "item_minimum")
	BigDecimal item_minimum
	
	@GraphQLQuery
	@Column(name = "discountable")
	Boolean discountable
	
	@GraphQLQuery
	@Column(name = "production")
	Boolean production
	
	@GraphQLQuery
	@Column(name = "reagent")
	Boolean reagent
	
	@GraphQLQuery
	@Column(name = "item_dfs")
	String item_dfs
	
	@GraphQLQuery
	@Column(name = "item_markup")
	BigDecimal item_markup
	
	@GraphQLQuery
	@Column(name = "selling_price")
	BigDecimal sellingPrice
	
	@GraphQLQuery
	@Column(name = "is_edpms")
	Boolean isEdpms
	
	@GraphQLQuery
	@Column(name = "markup_lock")
	Boolean markupLock

	@GraphQLQuery
	@Column(name = "flag_value")
	String flagValue

	@GraphQLQuery
	@Column(name = "is_life_support")
	Boolean isLifeSupport

	@GraphQLQuery
	@Column(name = "fix_asset")
	Boolean fixAsset

	@GraphQLQuery
	@Column(name = "pnf")
	Boolean pnf

	@GraphQLQuery
	@Column(name = "nonpnf")
	Boolean nonpnf

	@GraphQLQuery
	@Column(name = "dimensions", columnDefinition = "varchar")
	String dimensions

	@Transient
	String getUnitMeasurement() {
		return "${unit_of_purchase.unitDescription} (${item_conversion} ${unit_of_usage.unitDescription})"
	}
}
