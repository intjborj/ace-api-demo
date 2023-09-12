package com.hisd3.hismk2.domain.referential

import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(schema = "referential", name = "doh_service_types")
class DohServiceType {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "tscode", columnDefinition = "numeric")
	Integer tscode
	
	@GraphQLQuery
	@Column(name = "tsdesc", columnDefinition = "varchar")
	String tsdesc
	
}
