package com.hisd3.hismk2.domain.pms

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.hrm.Employee
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*
import org.javers.core.metamodel.annotation.DiffIgnore
import org.javers.core.metamodel.annotation.ShallowReference

import javax.persistence.*
import java.time.Instant

@javax.persistence.Entity
@javax.persistence.Table(schema = "pms", name = "vital_signs")
@SQLDelete(sql = "UPDATE pms.vital_signs SET deleted = true where id =? ", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted <> true or deleted is null ")
class VitalSign extends AbstractAuditingEntity {
	
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
	
	@GraphQLQuery
	@Column(name = "systolic", columnDefinition = "varchar")
	String systolic
	
	@GraphQLQuery
	@Column(name = "diastolic", columnDefinition = "varchar")
	String diastolic
	
	@GraphQLQuery
	@Column(name = "temperature", columnDefinition = "varchar")
	String temperature
	
	@GraphQLQuery
	@Column(name = "pulse_rate", columnDefinition = "varchar")
	String pulseRate
	
	@GraphQLQuery
	@Column(name = "respiratory_rate", columnDefinition = "varchar")
	String respiratoryRate
	
	@GraphQLQuery
	@Column(name = "oxygen_saturation", columnDefinition = "varchar")
	String oxygenSaturation
	
	@GraphQLQuery
	@Column(name = "pain_score", columnDefinition = "varchar")
	String painScore
	
	@GraphQLQuery
	@Column(name = "fetal_hr", columnDefinition = "varchar")
	String fetalHr
	
	@GraphQLQuery
	@Column(name = "weight", columnDefinition = "varchar")
	String weight
	
	@GraphQLQuery
	@Column(name = "crt", columnDefinition = "varchar")
	String crt
	
	@GraphQLQuery
	@Column(name = "cbs", columnDefinition = "varchar")
	String cbs
	
	@GraphQLQuery
	@Column(name = "cgs", columnDefinition = "varchar")
	String cgs
	
	@GraphQLQuery
	@Column(name = "cbc", columnDefinition = "varchar")
	String cbc

	@GraphQLQuery
	@Column(name = "note", columnDefinition = "varchar")
	String note
	
	@GraphQLQuery
	@Column(name = "entry_datetime", columnDefinition = "timestamp")
	Instant entryDateTime
	
	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee", referencedColumnName = "id")
	Employee employee

	@GraphQLQuery
	@Column(name = "remarks", columnDefinition = "varchar")
	String remarks
}
