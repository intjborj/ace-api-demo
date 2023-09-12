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
@javax.persistence.Table(schema = "pms", name= "doctor_notes")
@SQLDelete(sql = "UPDATE pms.doctor_notes SET deleted= true WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted <> true or deleted is  null ")
class DoctorNote extends AbstractAuditingEntity {
	
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
	@Column(name = "subjective", columnDefinition = "varchar")
	String subjective
	
	@GraphQLQuery
	@Column(name = "objective", columnDefinition = "varchar")
	String objective
	
	@GraphQLQuery
	@Column(name = "assessment", columnDefinition = "varchar")
	String assessment
	
	@GraphQLQuery
	@Column(name = "plan", columnDefinition = "varchar")
	String plan

	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee", referencedColumnName = "id")
	Employee employee
	
	@GraphQLQuery
	@Column(name = "entry_datetime", columnDefinition = "timestamp")
	Instant entryDateTime

}
