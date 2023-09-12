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
@javax.persistence.Table(schema = "inventory", name = "stock_issue")
@SQLDelete(sql = "UPDATE inventory.stock_issue SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class DepartmentStockIssue extends AbstractAuditingEntity implements AutoIntegrateable {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "issue_no")
	String issueNo
	
	@GraphQLQuery
	@Column(name = "issue_date")
	Instant issueDate
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "issue_from", referencedColumnName = "id")
	Department issueFrom
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "issue_to", referencedColumnName = "id")
	Department issueTo
	
	@GraphQLQuery
	@Column(name = "issue_type")
	String issueType
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "issued_by", referencedColumnName = "id")
	Employee issued_by
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "claimed_by", referencedColumnName = "id")
	Employee claimed_by

	@GraphQLQuery
	@Column(name = "request_no")
	String requestNo

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "request", referencedColumnName = "id")
	DepartmentStockRequest request

	@GraphQLQuery
	@Column(name = "acct_type", columnDefinition = 'uuid')
	UUID accType

	@GraphQLQuery
	@Column(name = "posted_ledger", columnDefinition = 'uuid')
	UUID postedLedger
	
	@GraphQLQuery
	@Column(name = "is_cancel")
	Boolean isCancel
	
	@GraphQLQuery
	@Column(name = "is_posted")
	Boolean isPosted
	
	@Transient
	Instant getCreated() {
		return createdDate
	}

	@Override
	String getDomain() {
		return DepartmentStockIssue.class.name
	}

	@Override
	Map<String, String> getDetails() {
		return [:]
	}

	@Transient
	String flagValue

	@Transient
	AccountingCategory category

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

	@Transient
	BigDecimal expense_a,expense_b,expense_c,expense_d,expense_e,expense_f,
			   expense_g,expense_h,expense_i,expense_j,expense_k,expense_l,expense_m,expense_n,expense_o,
			   expense_p,expense_q,expense_r,expense_s,expense_t,expense_u,expense_v,expense_w,expense_x,
			   expense_y,expense_z


}
