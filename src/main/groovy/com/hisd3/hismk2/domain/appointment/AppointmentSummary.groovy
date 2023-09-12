package com.hisd3.hismk2.domain.appointment

import com.hisd3.hismk2.domain.annotations.UpperCase
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(schema = "appointment", name = "summary_appointment")
class AppointmentSummary implements Serializable {

	@GraphQLQuery
	@Id
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schedule", referencedColumnName = "id")
	AppointmentSchedule schedule

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schedule_time", referencedColumnName = "id")
	AppointmentScheduleTime scheduleTime
	
	@GraphQLQuery
	@Column(name = "start_", columnDefinition = "varchar")
	String start_

	@GraphQLQuery
	@Column(name = "end_", columnDefinition = "varchar")
	String end_

	@GraphQLQuery
	@UpperCase
	@Column(name = "person", columnDefinition = "int")
	Integer person
}
