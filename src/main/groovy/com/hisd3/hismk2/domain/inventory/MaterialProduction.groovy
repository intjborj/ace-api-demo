package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.types.AutoIntegrateable
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*
import java.time.Instant

@javax.persistence.Entity
@javax.persistence.Table(schema = "inventory", name = "material_production")
@SQLDelete(sql = "UPDATE inventory.material_production SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class MaterialProduction extends AbstractAuditingEntity implements AutoIntegrateable {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "date_trans", columnDefinition = "date")
	Instant dateTransaction
	
	@GraphQLQuery
	@Column(name = "mp_no", columnDefinition = "varchar")
	String mpNo
	
	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item", referencedColumnName = "id")
	Item item
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "department", referencedColumnName = "id")
	Department department
	
	@GraphQLQuery
	@Column(name = "description")
	String description
	
	@GraphQLQuery
	@Column(name = "quantity")
	Integer quantity
	
	@GraphQLQuery
	@Column(name = "unitCost")
	BigDecimal unitCost
	
	@GraphQLQuery
	@Column(name = "status")
	String status

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "produced_by", referencedColumnName = "id")
	Employee producedBy

	@GraphQLQuery
	@Column(name = "acct_type", columnDefinition = 'uuid')
	UUID accType

	@GraphQLQuery
	@Column(name = "posted_ledger", columnDefinition = 'uuid')
	UUID postedLedger
	
	@GraphQLQuery
	@Column(name = "is_posted")
	Boolean isPosted

	//accounting
	@Override
	String getDomain() {
		return MaterialProduction.class.name
	}

	@Transient
	String flagValue

	@Transient
	AccountingCategory accountingCategory

	@Transient
	Department assignDepartment

	@Override
	Map<String, String> getDetails() {
		return [:]
	}

	@Transient
	BigDecimal output_value, source_value, diff_value

	@Transient
	BigDecimal asset_a,asset_b,asset_c,asset_d,asset_e,asset_f,
			   asset_g,asset_h,asset_i,asset_j,asset_k,asset_l,asset_m,asset_n,asset_o,
			   asset_p,asset_q,asset_r,asset_s,asset_t,asset_u,asset_v,asset_w,asset_x,
			   asset_y,asset_z

	@Transient
	BigDecimal negative_asset_a,negative_asset_b,negative_asset_c,negative_asset_d,negative_asset_e,negative_asset_f,
			   negative_asset_g,negative_asset_h,negative_asset_i,negative_asset_j,negative_asset_k,negative_asset_l,
			   negative_asset_m,negative_asset_n,negative_asset_o,negative_asset_p,negative_asset_q,negative_asset_r,
			   negative_asset_s,negative_asset_t,negative_asset_u,negative_asset_v,negative_asset_w,negative_asset_x,
			   negative_asset_y,negative_asset_z
	
}
