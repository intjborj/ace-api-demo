package com.hisd3.hismk2.domain.philhealth

import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(schema = "philhealth", name = "phic_group")
class PhicGroup implements Serializable {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "phic_group_name", columnDefinition = "varchar")
	String phicGroupName
	
	@GraphQLQuery
	@Column(name = "phic_group_description", columnDefinition = "varchar")
	String description
	
	@GraphQLQuery
	@Column(name = "deleted", columnDefinition = "boolean")
	Boolean deleted
}
