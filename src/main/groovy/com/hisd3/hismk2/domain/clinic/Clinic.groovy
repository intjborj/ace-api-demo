package com.hisd3.hismk2.domain.clinic

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(name = "clinics", schema = "clinic")
class Clinic extends AbstractAuditingEntity {
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "clinic_name", columnDefinition = "varchar")
	String clinicName
	
	@GraphQLQuery
	@Column(name = "clinic_address", columnDefinition = "varchar")
	String clinicAddress
	
	@GraphQLQuery
	@Column(name = "clinic_contact", columnDefinition = "varchar")
	String clinicContact
	
	@GraphQLQuery
	@Column(name = "clinic_contact_name", columnDefinition = "varchar")
	String clinicContactName
}
