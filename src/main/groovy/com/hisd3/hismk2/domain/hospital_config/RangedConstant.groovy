package com.hisd3.hismk2.domain.hospital_config

import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(schema = "hospital_configuration", name = "ranged_constants")
class RangedConstant {

	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@GraphQLQuery
	@Column(name = "field_name", columnDefinition = "text")
	String fieldName

	@GraphQLQuery
	@Column(name = "range_from", columnDefinition = "numeric")
	BigDecimal rangeFrom

	@GraphQLQuery
	@Column(name = "range_to", columnDefinition = "numeric")
	BigDecimal rangeTo
}