package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.types.AutoIntegrateable
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*
import java.time.Instant

@javax.persistence.Entity
@javax.persistence.Table(schema = "inventory", name = "return_supplier")
@SQLDelete(sql = "UPDATE inventory.return_supplier SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class ReturnSupplier extends AbstractAuditingEntity implements AutoIntegrateable{
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "rts_no", columnDefinition = 'varchar')
	String rtsNo
	
	@GraphQLQuery
	@Column(name = "return_date", columnDefinition = 'timestamp without time zone')
	Instant returnDate

	@GraphQLQuery
	@Column(name = "ref_srr", columnDefinition = 'varchar')
	String refSrr
	
	@GraphQLQuery
	@Column(name = "received_ref_no", columnDefinition = 'varchar')
	String receivedRefNo
	
	@GraphQLQuery
	@Column(name = "received_ref_date", columnDefinition = 'timestamp without time zone')
	Instant receivedRefDate
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "department", referencedColumnName = "id")
	Department department
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "supplier", referencedColumnName = "id")
	Supplier supplier
	
	@GraphQLQuery
	@Column(name = "received_by", columnDefinition = 'varchar')
	String received_by

	@GraphQLQuery
	@Column(name = "return_by", columnDefinition = 'varchar')
	String returnBy

	@GraphQLQuery
	@Column(name = "return_user", columnDefinition = 'uuid')
	UUID returnUser

	@GraphQLQuery
	@Column(name = "acct_type", columnDefinition = 'uuid')
	UUID accType

	@GraphQLQuery
	@Column(name = "posted_ledger", columnDefinition = 'uuid')
	UUID postedLedger
	
	@GraphQLQuery
	@Column(name = "is_posted", columnDefinition = 'bool')
	Boolean isPosted
	
	@GraphQLQuery
	@Column(name = "is_void", columnDefinition = 'bool')
	Boolean isVoid

	@GraphQLQuery(name = "refSrrList")
	@Transient
	List<String> getRefSrrList() {
		if(refSrr){
			def list = refSrr.split(',').collect{it}
			return list
		}else {
			return null
		}
	}

	//accounting
	@Override
	String getDomain() {
		return ReturnSupplier.class.name
	}

	@Transient
	String flagValue

	@Override
	Map<String, String> getDetails() {
		return [:]
	}

	@Transient
	AccountingCategory category

	@Transient
	BigDecimal cost, supplierAmount

	@Transient
	BigDecimal asset_a,asset_b,asset_c,asset_d,asset_e,asset_f,
			   asset_g,asset_h,asset_i,asset_j,asset_k,asset_l,asset_m,asset_n,asset_o,
			   asset_p,asset_q,asset_r,asset_s,asset_t,asset_u,asset_v,asset_w,asset_x,
			   asset_y,asset_z

	@Transient
	BigDecimal expense_a,expense_b,expense_c,expense_d,expense_e,expense_f,
			   expense_g,expense_h,expense_i,expense_j,expense_k,expense_l,expense_m,expense_n,expense_o,
			   expense_p,expense_q,expense_r,expense_s,expense_t,expense_u,expense_v,expense_w,expense_x,
			   expense_y,expense_z
	
}
