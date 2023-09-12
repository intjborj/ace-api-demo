package com.hisd3.hismk2.domain.appointment

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.accounting.TransactionType
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.Column
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import java.time.Instant

@javax.persistence.Entity
@javax.persistence.Table(schema = "appointment", name = "schedule_time")
@SQLDelete(sql = "UPDATE appointment.schedule_time SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class AppointmentScheduleTime extends AbstractAuditingEntity implements Serializable {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "schedule", referencedColumnName = "id")
	AppointmentSchedule schedule

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "config", referencedColumnName = "id")
	AppointmentConfig schedTime
	
	@GraphQLQuery
	@Column(name = "max_person", columnDefinition = "int")
	Integer maxPerson

	@GraphQLQuery
	@Column(name = "allowed_stat", columnDefinition = "bool")
	Boolean allowedStat

	@GraphQLQuery
	@Column(name = "stat_slot", columnDefinition = "time")
	Integer maxStat

	@GraphQLQuery
	@Column(name = "status", columnDefinition = "boolean")
	Boolean status
}
