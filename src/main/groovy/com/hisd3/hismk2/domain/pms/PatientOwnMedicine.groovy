package com.hisd3.hismk2.domain.pms

import com.hisd3.hismk2.domain.hrm.Employee
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant

@Entity
@Table(schema = "pms", name = "patient_own_medicines")
class PatientOwnMedicine {

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@GraphQLQuery
	@Column(name = "medicine_name", columnDefinition = "varchar")
	String medicine_name

	@GraphQLQuery
	@Column(name = "description", columnDefinition = "varchar")
	String description

	@GraphQLQuery
	@Column(name = "entry_datetime", columnDefinition = "timestamp")
	Instant entry_datetime

	@GraphQLQuery
	@Column(name = "qty_onhand", columnDefinition = "numeric")
	BigDecimal qty_onhand

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "linked_medication", referencedColumnName = "id")
	Medication linked_medication

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "patient_case", referencedColumnName = "id")
	Case patientCase

	@GraphQLQuery
	@Column(name = "active", columnDefinition = "bool")
	Boolean active

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee", referencedColumnName = "id")
	Employee employee

}
