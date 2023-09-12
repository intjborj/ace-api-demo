package com.hisd3.hismk2.domain.appointment

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.accounting.AccountReceivableItems
import com.hisd3.hismk2.domain.annotations.UpperCase
import io.leangen.graphql.annotations.GraphQLQuery
import org.apache.commons.lang3.StringUtils
import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.OrderBy
import javax.persistence.Transient
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@javax.persistence.Entity
@javax.persistence.Table(schema = "appointment", name = "schedule")
@SQLDelete(sql = "UPDATE appointment.schedule SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class AppointmentSchedule extends AbstractAuditingEntity implements Serializable {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "schedule_code", columnDefinition = "time")
	String scheduleCode

	@GraphQLQuery
	@Column(name = "schedule_date", columnDefinition = "time")
	Instant scheduleDate

	@GraphQLQuery
	@UpperCase
	@Formula("to_char(schedule_date, 'Mon dd, yyyy')")
	String formattedScheduleDate

	@GraphQLQuery
	@Column(name = "status", columnDefinition = "boolean")
	Boolean status
}
