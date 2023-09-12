package com.hisd3.hismk2.domain.ancillary

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.pms.Case
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*

import javax.persistence.*
import java.time.Instant

@TypeChecked
@javax.persistence.Entity
@javax.persistence.Table(schema = "ancillary", name = "orderslips")
@SQLDelete(sql = "UPDATE orderslips SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class Orderslip extends AbstractAuditingEntity {

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "case_number", referencedColumnName = "id")
	Case parentCase

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "department", referencedColumnName = "id")
	Department department

	@Column(name = "doctors_order", columnDefinition = "uuid")
	UUID doctorsOrder

	@Column(name = "requesting_physician", columnDefinition = "uuid")
	UUID requestingPhysician

	@Column(name = "requesting_physician_prc", columnDefinition = "varchar")
	String requestingPhysicianPrc

	@GraphQLQuery
	@Column(name = "requesting_physician_name", columnDefinition = "varchar")
	String requestingPhysicianName

	@GraphQLQuery
	@Column(name = "orderslip_no", columnDefinition = "varchar")
	String orderSlipNo

//	@GraphQLQuery
//	@Column(name = "time_started", columnDefinition = "timestamp")
//	LocalDateTime timeStarted
//
//	@GraphQLQuery
//	@Column(name = "time_completed", columnDefinition = "timestamp")
//	LocalDateTime timeCompleted
//
	@GraphQLQuery
	@Column(name = "status", columnDefinition = "varchar")
	String status

	@GraphQLQuery
	@Column(name = "package", columnDefinition = "boolean")
	Boolean packageType

	@GraphQLQuery
	@Column(name = "deleted", columnDefinition = "boolean")
	Boolean deleted

	@Transient
	Instant getCreated() {
		return createdDate
	}

	@GraphQLQuery
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "orderslip")
	List<OrderSlipItem> orderSlipItemList

}
