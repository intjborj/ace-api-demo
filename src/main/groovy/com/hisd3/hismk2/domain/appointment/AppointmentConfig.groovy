package com.hisd3.hismk2.domain.appointment

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.annotations.UpperCase
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.Id
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime

@javax.persistence.Entity
@javax.persistence.Table(schema = "appointment", name = "config")
@SQLDelete(sql = "UPDATE appointment.config SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class AppointmentConfig extends AbstractAuditingEntity implements Serializable {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "code", columnDefinition = "varchar")
	String code

	@GraphQLQuery
	@Column(name = "t_start", columnDefinition = "time")
	Instant timeStart

	@GraphQLQuery
	@Column(name = "t_end", columnDefinition = "time")
	Instant timeEnd

	@GraphQLQuery
		@Formula("concat(to_char(t_start + make_interval(0, 0, 0, 0, 8), 'HH24:MI'),' - ', to_char(t_end + make_interval(0, 0, 0, 0, 8), 'HH24:MI'))")
	String formattedTime

	@GraphQLQuery
	@Column(name = "default_max_person", columnDefinition = "int")
	Integer defaultMaxPerson

	@GraphQLQuery
	@Column(name = "allowed_stat", columnDefinition = "boolean")
	Boolean allowedStat

	@GraphQLQuery
	@Column(name = "default_max_stat", columnDefinition = "int")
	Integer defaultMaxStat

	@GraphQLQuery
	@Column(name = "status", columnDefinition = "boolean")
	Boolean status
}
