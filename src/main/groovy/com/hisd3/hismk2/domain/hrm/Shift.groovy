package com.hisd3.hismk2.domain.hrm

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.Id

@javax.persistence.Entity
@javax.persistence.Table(schema = "hrm", name = "shifts")
class Shift extends AbstractAuditingEntity {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "description", columnDefinition = "varchar")
	String description
	
	@GraphQLQuery
	@Column(name = "from_time", columnDefinition = "varchar")
	String fromTime
	
	@GraphQLQuery
	@Column(name = "to_time", columnDefinition = "varchar")
	String toTime
	
	@GraphQLQuery
	@Column(name = "move_back_days", columnDefinition = "integer")
	Integer moveBackDays
}
