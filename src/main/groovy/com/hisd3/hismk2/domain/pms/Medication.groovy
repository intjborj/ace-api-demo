package com.hisd3.hismk2.domain.pms

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.inventory.Item
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.ResultCheckStyle
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where
import org.javers.core.metamodel.annotation.DiffIgnore
import org.javers.core.metamodel.annotation.ShallowReference

import javax.persistence.*
import java.time.Instant

@Entity
@Table(schema = "pms", name = "medications")
@SQLDelete(sql = "UPDATE pms.medications SET deleted = true where id = ? ", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted <> true or deleted is null")
class Medication extends AbstractAuditingEntity {

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@DiffIgnore
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "`case`", referencedColumnName = "id")
	Case parentCase

	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "medicine", referencedColumnName = "id")
	Item medicine

	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "additive", referencedColumnName = "id")
	Item additive

	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee", referencedColumnName = "id")
	Employee orderingPhysician

	@GraphQLQuery
	@Column(name = "frequency", columnDefinition = "varchar")
	String frequency

	@GraphQLQuery
	@Column(name = "dose", columnDefinition = "varchar")
	String dose

	@GraphQLQuery
	@Column(name = "route", columnDefinition = "varchar")
	String route

	@GraphQLQuery
	@Column(name = "volume", columnDefinition = "varchar")
	String volume

	@GraphQLQuery
	@Column(name = "flow_rate", columnDefinition = "varchar")
	String flowRate

	@GraphQLQuery
	@Column(name = "remarks", columnDefinition = "varchar")
	String remarks

	@GraphQLQuery
	@Column(name = "type", columnDefinition = "varchar")
	String type

	@GraphQLQuery
	@Column(name = "onhand", columnDefinition = "numeric")
	BigDecimal onhand

	@Column(name = "doctors_order_item_id", columnDefinition = "uuid")
	UUID doctorsOrderItemId

	@GraphQLQuery
	@Column(name = "entry_datetime", columnDefinition = "timestamp")
	Instant entryDateTime

	@Transient
	BigDecimal pending

	@Transient
	BigDecimal claimable

	@Transient
	BigDecimal noStockOrCancel

	@Transient
	Boolean hasStockInPharma

	@GraphQLQuery
	@Column(name = "discontinue_datetime", columnDefinition = "timestamp")
	Instant discontinuedDatetime

	@GraphQLQuery
	@Column(name = "shift_hold", columnDefinition = "varchar")
	String shiftHold
}
