package com.hisd3.hismk2.domain.referential

import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*

@TypeChecked
@Entity
@Table(schema = "referential", name = "doh_icd_codes")
class DohIcdCode {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "icd10_code", columnDefinition = "varchar")
	String icdCode
	
	@GraphQLQuery
	@Column(name = "icd10_desc", columnDefinition = "varchar")
	String icdDesc

	@GraphQLQuery
	@Column(name = "icd10_cat", columnDefinition = "varchar")
	String icdCategory

}
