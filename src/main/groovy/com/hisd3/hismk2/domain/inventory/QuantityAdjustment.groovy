package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.domain.types.AutoIntegrateable
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*
import java.time.Instant

@javax.persistence.Entity
@javax.persistence.Table(schema = "inventory", name = "quantity_adjustment")
@SQLDelete(sql = "UPDATE inventory.quantity_adjustment SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class QuantityAdjustment extends AbstractAuditingEntity implements AutoIntegrateable  {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "ref_num")
	String refNum
	
	@GraphQLQuery
	@Column(name = "date_trans")
	Instant dateTrans
	
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
	@Column(name = "quantity")
	Integer quantity

	@GraphQLQuery
	@Column(name = "unit_cost")
	BigDecimal unit_cost
	
	@GraphQLQuery
	@Column(name = "is_posted")
	Boolean isPosted
	
	@GraphQLQuery
	@Column(name = "is_cancel")
	Boolean isCancel

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "adjustment_type", referencedColumnName = "id")
	QuantityAdjustmentType quantityAdjustmentType

	@GraphQLQuery
	@Column(name = "remarks")
	String remarks

	@GraphQLQuery
	@Column(name = "posted_by", columnDefinition = "varchar")
	@UpperCase
	String postedBy

	@GraphQLQuery
	@Column(name = "posted_ledger")
	UUID postedLedger

	@Transient
	String getUou() {
		return "${item.unit_of_usage?.unitDescription}"
	}

	//accounting
	@Override
	String getDomain() {
		return QuantityAdjustment.class.name
	}

	@Transient
	String flagValue

	@Transient
	AccountingCategory accountingCategory


	@Override
	Map<String, String> getDetails() {
		return [:]
	}


	@Transient
	BigDecimal adj_1,adj_2,adj_3,adj_4,adj_5,adj_6,adj_7,adj_8,adj_9,adj_10,adj_11,adj_12,adj_13,adj_14,
			adj_15,adj_16,adj_17,adj_18,adj_19,adj_20,adj_21,adj_22,adj_23,adj_24,adj_25,adj_26,adj_27,
			adj_28,adj_29,adj_30,adj_31,adj_32,adj_33,adj_34,adj_35,adj_36,adj_37,adj_38,adj_39,adj_40,
			adj_41,adj_42,adj_43,adj_44,adj_45,adj_46,adj_47,adj_48,adj_49,adj_50,adj_51,adj_52,adj_53,
			adj_54,adj_55,adj_56,adj_57,adj_58,adj_59,adj_60,adj_61,adj_62,adj_63,adj_64,adj_65,adj_66,
			adj_67,adj_68,adj_69,adj_70,adj_71,adj_72,adj_73,adj_74,adj_75,adj_76,adj_77,adj_78,adj_79,
			adj_80,adj_81,adj_82,adj_83,adj_84,adj_85,adj_86,adj_87,adj_88,adj_89,adj_90,adj_91,adj_92,
			adj_93,adj_94,adj_95,adj_96,adj_97,adj_98,adj_99,adj_100

	@Transient
	BigDecimal asset_a,asset_b,asset_c,asset_d,asset_e,asset_f,
			   asset_g,asset_h,asset_i,asset_j,asset_k,asset_l,asset_m,asset_n,asset_o,
			   asset_p,asset_q,asset_r,asset_s,asset_t,asset_u,asset_v,asset_w,asset_x,
			   asset_y,asset_z

}
