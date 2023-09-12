package com.hisd3.hismk2.domain.hrm

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where
import org.springframework.data.annotation.CreatedDate

import javax.persistence.*
import java.time.Instant

@Entity
@Table(schema = "hrm", name = "event_calendar")
@SQLDelete(sql = "UPDATE hrm.event_calendar SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class EventCalendar extends AbstractAuditingEntity {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "name", columnDefinition = "varchar")
	String name
	
	@GraphQLQuery
	@Column(name = "start_date")
	Instant startDate
	
	@GraphQLQuery
	@Column(name = "end_date")
	Instant endDate
	
	@GraphQLQuery
	@Column(name = "fixed", columnDefinition = "varchar")
	String fixed
	
	@GraphQLQuery
	@Column(name = "holiday_type", columnDefinition = "varchar")
	String holidayType


}
