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
@Table(schema = "inventory", name = "stock_issue_items")
@SQLDelete(sql = "UPDATE inventory.stock_issue_items SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class DepartmentStockIssueItems extends AbstractAuditingEntity {
	
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
	@JoinColumn(name = "stock_issue", referencedColumnName = "id")
	DepartmentStockIssue stockIssue
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "item", referencedColumnName = "id")
	Item item

	@GraphQLQuery
	@Column(name = "requested_qty")
	Integer requestedQty
	
	@GraphQLQuery
	@Column(name = "issue_qty")
	Integer issueQty
	
	@GraphQLQuery
	@Column(name = "unit_cost")
	BigDecimal unitCost
	
	@GraphQLQuery
	@Column(name = "is_posted")
	Boolean isPosted

	@GraphQLQuery
	@Column(name = "remarks")
	String remarks

	@GraphQLQuery
	@Column(name = "request_item")
	UUID requestItem

	@Transient
	String getItemCategory() {
		return "[${item.item_group.itemDescription}] ${item.item_category?.categoryDescription}"
	}

	@Transient
	String getUou() {
		return "${item.unit_of_usage?.unitDescription}"
	}
}
