package com.hisd3.hismk2.domain

import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Transient
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Entity
@Table(name = "notifications", schema = "public")
class Notification {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "\"from\"", columnDefinition = "uuid")
	UUID from
	
	@GraphQLQuery
	@Column(name = "\"to\"", columnDefinition = "uuid")
	UUID to
	
	@GraphQLQuery
	@Column(name = "department", columnDefinition = "uuid")
	UUID department
	
	@GraphQLQuery
	@Column(name = "message", columnDefinition = "varchar")
	String message
	
	@GraphQLQuery
	@Column(name = "title", columnDefinition = "varchar")
	String title
	
	@GraphQLQuery
	@Column(name = "url", columnDefinition = "varchar")
	String url
	
	@GraphQLQuery
	@Column(name = "date_notified", columnDefinition = "timestamp")
	Instant datenotified
	
	@GraphQLQuery
	@Column(name = "date_seen", columnDefinition = "timestamp")
	Instant date_seen
	
	@GraphQLQuery
	@Transient
	String date_notified_string() {
		if (datenotified != null)
		//	return new LocalDateTime(datenotified.toEpochMilli()).toString("MM/dd/yyyy hh:mma")
			return datenotified.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a"))
		else
			return null
	}
	
}
