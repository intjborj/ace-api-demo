package com.hisd3.hismk2.domain.pms

import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(schema = "pms", name = "flowrate")
class FlowRate {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "liter_per_minute", columnDefinition = "numeric")
	BigDecimal literPerMinute
	
	@GraphQLQuery
	@Column(name = "price_per_hour", columnDefinition = "numeric")
	BigDecimal pricePerHour
	
	@GraphQLQuery
	@Column(name = "description", columnDefinition = "varchar")
	String description
	
}
