package com.hisd3.hismk2.domain.inventory

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.hrm.Employee
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant
import java.time.LocalDateTime

@Entity
@Table(schema = "inventory", name = "purchase_order")
class PurchaseOrder extends AbstractAuditingEntity {
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "`supplier`", referencedColumnName = "id")
	Supplier supplier
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "`department_from`", referencedColumnName = "id")
	Department departmentFrom
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "`department_to`", referencedColumnName = "id")
	Department departmentTo
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "`prepared_by`", referencedColumnName = "id")
	Employee preparedBy
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "`approvedBy`", referencedColumnName = "id")
	Employee approvedBy
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "`reviewed_by`", referencedColumnName = "id")
	Employee reviewdBy
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "`sent_by`", referencedColumnName = "id")
	Employee sentBy
	
	@GraphQLQuery
	@Column(name = "sent_date", columnDefinition = "timestamp")
	LocalDateTime sentDate
	
	@GraphQLQuery
	@Column(name = "prepared_date", columnDefinition = "timestamp")
	LocalDateTime preparedDate
	
	@GraphQLQuery
	@Column(name = "approved_date", columnDefinition = "timestamp")
	LocalDateTime approvedDate
	
	@GraphQLQuery
	@Column(name = "reviewed_date", columnDefinition = "timestamp")
	LocalDateTime reviewedDate
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "`payment_terms`", referencedColumnName = "id")
	PaymentTerm paymentTerms
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "`purchase_request`", referencedColumnName = "id")
	PurchaseRequest purchaseRequest
	
	@GraphQLQuery
	@Column(name = "delivery_terms", columnDefinition = "varchar")
	String deliveryTerms
	
	@GraphQLQuery
	@Column(name = "remarks", columnDefinition = "varchar")
	String remarks
	
	@GraphQLQuery
	@Column(name = "status", columnDefinition = "varchar")
	String status
	
	@GraphQLQuery
	@Column(name = "po_number", columnDefinition = "varchar")
	String poNumber
	
	@GraphQLQuery
	@Column(name = "prepared_by_signature", columnDefinition = "bytea")
	Byte[] preparedBySignature
	
	@GraphQLQuery
	@Column(name = "approved_by_2", columnDefinition = "uuid")
	UUID approvedBy2
	
	@GraphQLQuery
	@Column(name = "approved_by_2_date", columnDefinition = "timestamp")
	LocalDateTime approvedBy2Date
	
	@GraphQLQuery
	@Column(name = "approved_by_2_signature", columnDefinition = "bytea")
	Byte[] approvedBy2Signature
	
	@GraphQLQuery
	@Column(name = "approved_by_3", columnDefinition = "uuid")
	UUID approvedBy3
	
	@GraphQLQuery
	@Column(name = "approved_by_3_signature", columnDefinition = "bytea")
	Byte[] approvedBy3Signature
	
	@GraphQLQuery
	@Column(name = "approved_by_3_date", columnDefinition = "timestamp")
	LocalDateTime approvedBy3Date
	
	@GraphQLQuery
	@Column(name = "approved_by_4", columnDefinition = "uuid")
	UUID approvedBy4
	
	@GraphQLQuery
	@Column(name = "approved_by_4_signature", columnDefinition = "bytea")
	Byte[] approvedBy4Signature
	
	@GraphQLQuery
	@Column(name = "approved_by_4_date", columnDefinition = "timestamp")
	LocalDateTime approvedBy4Date
	
	@GraphQLQuery
	@Column(name = "revision_number", columnDefinition = "numeric")
	Integer revisionNumber
	
	@GraphQLQuery
	@Column(name = "revision_note", columnDefinition = "varchar")
	String revisionNote
	
	@GraphQLQuery
	@Column(name = "approved_by_position", columnDefinition = "varchar")
	String approvedByPosition
	
	@GraphQLQuery
	@Column(name = "approved_by_2_position", columnDefinition = "varchar")
	String approvedBy2Position
	
	@GraphQLQuery
	@Column(name = "approved_by_3_position", columnDefinition = "varchar")
	String approvedBy3Position
	
	@GraphQLQuery
	@Column(name = "approved_by_4_position", columnDefinition = "varchar")
	String approvedBy4Position
	
	@GraphQLQuery
	@Column(name = "po_sent_to_supplier", columnDefinition = "bool")
	Boolean poSentToSupplier
	
	@GraphQLQuery
	@Column(name = "no_pr", columnDefinition = "bool")
	Boolean noPr
	
	@GraphQLQuery
	@Column(name = "po_sent_notes", columnDefinition = "varchar")
	String poSentNotes
	
	@GraphQLQuery
	@Column(name = "last_revision_by", columnDefinition = "uuid")
	UUID lastRevisionBy
	
	@GraphQLQuery
	@Column(name = "last_revision_date", columnDefinition = "timestamp")
	LocalDateTime lastRevisionDate
	
	@GraphQLQuery
	@Column(name = "revision_by_history", columnDefinition = "varchar")
	String revisionByHistory
	
	@GraphQLQuery
	@Column(name = "pr_nos", columnDefinition = "varchar")
	String prNos
	
	@GraphQLQuery
	@Column(name = "eta_date", columnDefinition = "timestamp")
	LocalDateTime etaDate
	
	@GraphQLQuery
	@Column(name = "is_completed", columnDefinition = "bool")
	Boolean isCompleted

	@GraphQLQuery
	@Column(name = "consignment", columnDefinition = "bool")
	Boolean consignment

	@GraphQLQuery
	@Column(name ="is_fixed_asset", columnDefinition = "bool")
	Boolean isFixedAsset

	@Transient
	Instant getCreated() {
		return createdDate
	}
}
