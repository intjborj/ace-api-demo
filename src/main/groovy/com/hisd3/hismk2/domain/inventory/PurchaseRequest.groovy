package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*
import java.time.Instant

@javax.persistence.Entity
@javax.persistence.Table(schema = "inventory", name = "purchase_request")
@SQLDelete(sql = "UPDATE inventory.purchase_request SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class PurchaseRequest extends AbstractAuditingEntity {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "pr_no", columnDefinition = "varchar")
	String prNo
	
	@GraphQLQuery
	@Column(name = "pr_date_requested", columnDefinition = "timestamp without time zone")
	Instant prDateRequested
	
	@GraphQLQuery
	@Column(name = "pr_date_needed", columnDefinition = "timestamp without time zone")
	Instant prDateNeeded
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "supplier", referencedColumnName = "id")
	Supplier supplier
	
	@GraphQLQuery
	@Column(name = "user_id", columnDefinition = "uuid")
	UUID userId
	
	@GraphQLQuery
	@Column(name = "user_fullname", columnDefinition = "varchar")
	String userFullname
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "requested_dep", referencedColumnName = "id")
	Department requestedDepartment
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "requesting_dep", referencedColumnName = "id")
	Department requestingDepartment
	
	@GraphQLQuery
	@Column(name = "pr_type", columnDefinition = "varchar")
	String prType
	
	@GraphQLQuery
	@Column(name = "is_approve", columnDefinition = "bool")
	Boolean isApprove
	
	@GraphQLQuery
	@Column(name = "approver", columnDefinition = "uuid")
	UUID approver
	
	@GraphQLQuery
	@Column(name = "approver_fullname", columnDefinition = "varchar")
	String approverFullname
	
	@GraphQLQuery
	@Column(name = "is_po_create", columnDefinition = "bool")
	Boolean isPoCreated
	
	@GraphQLQuery
	@Column(name = "status", columnDefinition = "varchar")
	String status

	@GraphQLQuery
	@Column(name = "consignment", columnDefinition = "bool")
	Boolean consignment

	@GraphQLQuery
	@Column(name = "asset", columnDefinition = "bool")
	Boolean asset
	
}
