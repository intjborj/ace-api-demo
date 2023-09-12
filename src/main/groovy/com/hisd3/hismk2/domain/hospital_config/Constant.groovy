package com.hisd3.hismk2.domain.hospital_config

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(schema = "hospital_configuration", name = "constants")
class Constant extends AbstractAuditingEntity {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "name", columnDefinition = "varchar")
	String name
	
	@GraphQLQuery
	@Column(name = "value", columnDefinition = "varchar")
	String value
	
	@GraphQLQuery
	@Column(name = "short_code", columnDefinition = "varchar")
	String shortCode
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "type", referencedColumnName = "id")
	ConstantType type

	@GraphQLQuery
	@Column(name = "status", columnDefinition = "bool")
	Boolean status
}
