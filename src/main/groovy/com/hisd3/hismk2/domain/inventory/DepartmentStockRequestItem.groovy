package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*

@javax.persistence.Entity
@javax.persistence.Table(schema = "inventory", name = "department_stock_request_items")
@SQLDelete(sql = "UPDATE inventory.department_stock_request_items SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class DepartmentStockRequestItem extends AbstractAuditingEntity {
	
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
	@JoinColumn(name = "item", referencedColumnName = "id")
	Item item
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "department_stock_request", referencedColumnName = "id")
	DepartmentStockRequest departmentStockRequest
	
	@GraphQLQuery
	@Column(name = "quantity_requested")
	Integer quantity_requested
	
	@GraphQLQuery
	@Column(name = "unit_cost")
	BigDecimal unit_cost
	
	@GraphQLQuery
	@Column(name = "prepared_qty")
	Integer preparedQty
	
	@GraphQLQuery
	@Column(name = "is_posted")
	Boolean isPosted
	
	@GraphQLQuery
	@Column(name = "is_rejected")
	Boolean isRejected
	
	@GraphQLQuery
	@Column(name = "remarks")
	String remarks

	@GraphQLQuery
	@Column(name = "status")
	String status

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "stock_issue_items", referencedColumnName = "id")
	DepartmentStockIssueItems stockIssueItems

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ref_issuance", referencedColumnName = "id")
	DepartmentStockIssue stockIssue

	@Transient
	String getItemCategory() {
		return "[${item.item_group.itemDescription}] ${item.item_category?.categoryDescription}"
	}

	@Transient
	String getUou() {
		return "${item.unit_of_usage?.unitDescription}"
	}
}

