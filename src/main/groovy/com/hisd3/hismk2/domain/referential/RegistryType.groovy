package com.hisd3.hismk2.domain.referential

import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*

@TypeChecked
@Entity
@Table(schema = "referential", name = "registry_types")
class RegistryType {
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "registry_code", columnDefinition = "varchar")
	String registryCode
	
	@GraphQLQuery
	@Column(name = "registry_title", columnDefinition = "varchar")
	String registryTitle
}
