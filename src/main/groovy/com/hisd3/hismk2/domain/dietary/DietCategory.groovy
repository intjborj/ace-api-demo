package com.hisd3.hismk2.domain.dietary

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.types.JaversResolvable
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(name = "diet_categories", schema = "dietary")
class DietCategory extends AbstractAuditingEntity{
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id

	@Column(name = "diet_category_code", columnDefinition = "varchar")
	String dietCategoryCode
	
	@Column(name = "diet_category_description", columnDefinition = "varchar")
	String dietCategoryDescription
	
	@Column(name = "status", columnDefinition = "varchar")
	String status

}
