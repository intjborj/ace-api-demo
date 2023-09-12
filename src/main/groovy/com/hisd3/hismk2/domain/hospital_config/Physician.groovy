package com.hisd3.hismk2.domain.hospital_config

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.hrm.Employee
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where
import org.javers.core.metamodel.annotation.ShallowReference

import javax.persistence.*

@Entity

@Table(schema = "hospital_configuration", name = "physicians")
class Physician extends AbstractAuditingEntity {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "first_name", columnDefinition = "varchar")
	String firstName

	@GraphQLQuery
	@Column(name = "middle_name", columnDefinition = "varchar")
	String middleName

	@GraphQLQuery
	@Column(name = "last_name", columnDefinition = "varchar")
	String lastName

	@GraphQLQuery
	@Column(name = "suffix", columnDefinition = "varchar")
	String suffix

	@GraphQLQuery
	@Column(name = "title", columnDefinition = "varchar")
	String title

	@GraphQLQuery
	@Formula("upper(concat(first_name , coalesce(' ' || nullif(middle_name,'') , ''), coalesce(' ' || nullif(last_name,'') , ''), coalesce(' ' || nullif(suffix,'') , ''), coalesce(', ' || nullif(title,'') , '')))")
	String fullnameWithTitle

	@GraphQLQuery
	@Column(name = "general_practitioner", columnDefinition = "boolean")
	Boolean generalPractitioner
	@ShallowReference
	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "specialization", referencedColumnName = "id")
	Specialty specialization
}
