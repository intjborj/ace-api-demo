package com.hisd3.hismk2.domain.eclaims

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.hrm.Employee
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(schema = "eclaims", name = "eclaims_settings")
class EclaimsIntegrationSetting extends AbstractAuditingEntity implements Serializable {
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "host", columnDefinition = "varchar")
	String host

	@GraphQLQuery
	@Column(name = "provider", columnDefinition = "varchar")
	String provider

	@GraphQLQuery
	@Column(name = "client_host", columnDefinition = "varchar")
	String clientHost
}
