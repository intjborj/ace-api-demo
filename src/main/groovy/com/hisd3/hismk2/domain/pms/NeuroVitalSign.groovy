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
@javax.persistence.Table(schema = "pms", name = "neuro_vital_signs")
@SQLDelete(sql = "UPDATE pms.neuro_vital_signs SET deleted = true where id =? ", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted <> true or deleted is null ")
class NeuroVitalSign extends AbstractAuditingEntity {
	
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
	@Column(name = "gcs_eye_response", columnDefinition = "varchar")
	String gcsEyeResponse
	
	@GraphQLQuery
	@Column(name = "gcs_verbal_response", columnDefinition = "varchar")
	String gcsVerbalResponse
	
	@GraphQLQuery
	@Column(name = "gcs_best_motor_response", columnDefinition = "varchar")
	String gcsBestMotorResponse
	
	@GraphQLQuery
	@Column(name = "gcs_total", columnDefinition = "varchar")
	String gcsTotal
	
	@GraphQLQuery
	@Column(name = "es_right_size", columnDefinition = "varchar")
	String esRightSize
	
	@GraphQLQuery
	@Column(name = "es_right_reaction", columnDefinition = "varchar")
	String esRightReaction
	
	@GraphQLQuery
	@Column(name = "es_left_size", columnDefinition = "varchar")
	String esLeftSize
	
	@GraphQLQuery
	@Column(name = "es_left_reaction", columnDefinition = "varchar")
	String esLeftReaction
	
	@GraphQLQuery
	@Column(name = "ls_arms_left", columnDefinition = "varchar")
	String lsArmsLeft
	
	@GraphQLQuery
	@Column(name = "ls_arms_right", columnDefinition = "varchar")
	String lsArmsRight
	
	@GraphQLQuery
	@Column(name = "ls_legs_left", columnDefinition = "varchar")
	String lsLegsLeft
	
	@GraphQLQuery
	@Column(name = "ls_legs_right", columnDefinition = "varchar")
	String lsLegsRight
	
	@GraphQLQuery
	@Column(name = "entry_datetime", columnDefinition = "timestamp")
	Instant entryDateTime
	
	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee", referencedColumnName = "id")
	Employee employee
}
