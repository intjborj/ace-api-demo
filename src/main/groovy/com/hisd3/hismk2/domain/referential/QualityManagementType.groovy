package com.hisd3.hismk2.domain.referential

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*

@TypeChecked
@Entity
@Table(schema = "referential", name = "quality_management_types")
class QualityManagementType extends AbstractAuditingEntity {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "title", columnDefinition = "varchar")
	String title
	
	@GraphQLQuery
	@Column(name = "description", columnDefinition = "varchar")
	String description
	
}
