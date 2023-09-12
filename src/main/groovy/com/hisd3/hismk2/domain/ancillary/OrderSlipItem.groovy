package com.hisd3.hismk2.domain.ancillary

import com.fasterxml.jackson.annotation.JsonFormat
import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.billing.BillingItem
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.pms.DoctorOrderItem
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant

enum ItemTypes {
	MEDICINES,
	DIAGNOSTICS,
	SUPPLIES,
}

enum MemberTypes {
	BUNDLE,
	PACKAGE,
	REGULAR
}

@TypeChecked
@Entity
@Table(schema = "ancillary", name = "orderslip_item")
class OrderSlipItem extends AbstractAuditingEntity {

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "service", referencedColumnName = "id")
	Service service

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orderslip", referencedColumnName = "id")
	Orderslip orderslip

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billing_item", referencedColumnName = "id")
	BillingItem billing_item

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reader", referencedColumnName = "id")
	Employee reader

	@GraphQLQuery
	@Column(name = "schedule_date", columnDefinition = "timestamp")
	Instant scheduleDate

	@GraphQLQuery
	@Column(name = "time_started", columnDefinition = "timestamp")
	Instant timeStarted

	@GraphQLQuery
	@Column(name = "time_completed", columnDefinition = "timestamp")
	Instant timeCompleted

	@GraphQLQuery
	@Column(name = "status", columnDefinition = "varchar")
	String status

	@GraphQLQuery
	@Column(name = "posted", columnDefinition = "boolean")
	Boolean posted

	@GraphQLQuery
	@Column(name = "deleted", columnDefinition = "boolean")
	Boolean deleted

	@GraphQLQuery
	@Column(name = "batch_num", columnDefinition = "varchar")
	String batchNumber

	@GraphQLQuery
	@Column(name = "item_no", columnDefinition = "varchar")
	String itemNo

	@GraphQLQuery
	@Column(name = "accession", columnDefinition = "varchar")
	String accession

	@GraphQLQuery
	@Column(name = "waved", columnDefinition = "decimal")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	BigDecimal waved

	@GraphQLQuery
	@Column(name = "submittedto3rdparty", columnDefinition = "boolean")
	Boolean transmitted

	@GraphQLQuery
	@Column(name = "stat", columnDefinition = "boolean")
	Boolean stat

	@GraphQLQuery
	@Enumerated(EnumType.STRING)
	@Column(name = "item_type", columnDefinition = "varchar")
	ItemTypes itemType

	@GraphQLQuery
	@Enumerated(EnumType.STRING)
	@Column(name = "member_of", columnDefinition = "varchar")
	MemberTypes memberOF

	@GraphQLQuery
	//@Enumerated(EnumType.STRING)
	@Column(name = "transaction_type", columnDefinition = "varchar")
	String transaction_type

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "doctors_order_item", referencedColumnName = "id")
	DoctorOrderItem doctors_order_item

	@GraphQLQuery
	@Column(name = "discontinued_datetime", columnDefinition = "timestamp")
	Instant discontinuedDatetime

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "discontinued_by", referencedColumnName = "id")
	Employee discontinuedBy

	@Transient
	Instant getCreated() {
		return createdDate
	}

	@Transient
	List<PackageContent> packageItems = []

}
