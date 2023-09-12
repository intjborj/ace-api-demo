package com.hisd3.hismk2.domain.pms

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.ancillary.Service
import com.hisd3.hismk2.domain.dietary.Diet
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.inventory.Item
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type
import org.javers.core.metamodel.annotation.DiffIgnore
import org.javers.core.metamodel.annotation.ShallowReference

import javax.persistence.*
import java.time.Instant

@Entity
@Table(schema = "pms", name = "doctor_order_items")
class DoctorOrderItem extends AbstractAuditingEntity {

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
	@JoinColumn(name = "doctor_order", referencedColumnName = "id")
	DoctorOrder doctorOrder

	@GraphQLQuery
	@Column(name = "`order`", columnDefinition = "text")
	String order

	@GraphQLQuery
	@Column(name = "type", columnDefinition = "varchar")
	String type

	@GraphQLQuery
	@Column(name = "status", columnDefinition = "varchar")
	String status

	@GraphQLQuery
	@Column(name = "action", columnDefinition = "varchar")
	String action

	@GraphQLQuery
	@Column(name = "volume", columnDefinition = "varchar")
	String volume

	@GraphQLQuery
	@Column(name = "flow_rate", columnDefinition = "varchar")
	String flowRate

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

	@GraphQLQuery
	@Column(name = "medication_type", columnDefinition = "varchar")
	String medicationType

	@GraphQLQuery
	@Column(name = "frequency", columnDefinition = "varchar")
	String frequency

	@GraphQLQuery
	@Column(name = "dose", columnDefinition = "varchar")
	String dose

	@GraphQLQuery
	@Column(name = "route", columnDefinition = "varchar")
	String route

	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "service", referencedColumnName = "id")
	Service service

	@GraphQLQuery
	@Column(name = "additional_instructions", columnDefinition = "text")
	String additionalInstructions

	@GraphQLQuery
	@Column(name = "role_position", columnDefinition = "text")
	String rolePosition

	@GraphQLQuery
	@Column(name = "entry_datetime", columnDefinition = "timestamp")
	Instant entryDateTime

	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item", referencedColumnName = "id")
	Item item

	@GraphQLQuery
	@Column(name = "start_time", columnDefinition = "timestamp")
	Instant startTime

	@GraphQLQuery
	@Column(name = "end_time", columnDefinition = "timestamp")
	Instant endTime

	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "diet", referencedColumnName = "id")
	Diet diet

	@GraphQLQuery
	@Column(name = "times_executed", columnDefinition = "integer")
	Integer timesExecuted

	@GraphQLQuery
	@Column(name = "execution_logs", columnDefinition = "text")
	String executionLogs

	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "carried_by", referencedColumnName = "id")
	Employee carriedBy

	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attending_physician", referencedColumnName = "id")
	Employee attendingPhysician

	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "managing_physician", referencedColumnName = "id")
	Employee managingPhysician

	@GraphQLQuery
	@Column(name = "discontinue_datetime", columnDefinition = "timestamp")
	Instant discontinuedDatetime

	@GraphQLQuery
	@Column(name = "hidden", columnDefinition = "timestamp")
	Instant hidden

	@GraphQLQuery
	@Column(name = "discontinued_do_item_ref", columnDefinition = "uuid")
	UUID discontinuedDoItemRef
}
