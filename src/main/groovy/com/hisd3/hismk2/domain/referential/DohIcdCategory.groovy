package com.hisd3.hismk2.domain.referential

import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*

@TypeChecked
@Entity
@Table(schema = "referential", name = "doh_icd_category")
class DohIcdCategory {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "icd_10_code", columnDefinition = "varchar")
	String icdCategoryCode
	
	@GraphQLQuery
	@Column(name = "diseases_conditions", columnDefinition = "varchar")
	String icdCategoryDesc
	
}
