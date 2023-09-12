package com.hisd3.hismk2.domain.hospital_config

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(schema = "hospital_configuration", name = "comlogik_settings")
class ComlogikSetting extends AbstractAuditingEntity {
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "login", columnDefinition = "varchar")
	String login
	
	@GraphQLQuery
	@Column(name = "password", columnDefinition = "varchar")
	String password
	
	@GraphQLQuery
	@Column(name = "hos", columnDefinition = "varchar")
	String host
}
