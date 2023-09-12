package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.hrm.Employee
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*
import java.time.Instant

@javax.persistence.Entity
@javax.persistence.Table(schema = "inventory", name = "department_stock_request")
@SQLDelete(sql = "UPDATE inventory.department_stock_request SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class DepartmentStockRequest extends AbstractAuditingEntity {
	
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
	@JoinColumn(name = "requesting_department", referencedColumnName = "id")
	Department requestingDepartment
	
	@GraphQLQuery
	@Column(name = "request_no")
	String requestNo
	
	@GraphQLQuery
	@Column(name = "request_date")
	Instant requestDate
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "issuing_department", referencedColumnName = "id")
	Department issuingDepartment
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "requested_by", referencedColumnName = "id")
	Employee requestedBy
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "prepared_by", referencedColumnName = "id")
	Employee preparedBy
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "dispensed_by", referencedColumnName = "id")
	Employee dispensedBy
	
	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "claimed_by", referencedColumnName = "id")
	Employee claimedBy
	
	@GraphQLQuery
	@Column(name = "request_type")
	String requestType
	
	@GraphQLQuery
	@Column(name = "purpose")
	String purpose
	
	@GraphQLQuery
	@Column(name = "remarks")
	String remarks
	
	@GraphQLQuery
	@Column(name = "is_canceled")
	Boolean isCanceled
	
	@GraphQLQuery
	@Column(name = "status")
	Integer status // 0 = Pending | 1 = Issued
	
	@GraphQLQuery
	@Column(name = "is_posted")
	Boolean isPosted
	
	@Transient
	Instant getCreated() {
		return createdDate
	}

	@GraphQLQuery
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "stock_issue", referencedColumnName = "id")
	DepartmentStockIssue stockIssue


}
