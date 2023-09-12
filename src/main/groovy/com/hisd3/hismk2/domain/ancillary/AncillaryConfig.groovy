package com.hisd3.hismk2.domain.ancillary

import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(schema = "ancillary", name = "ancillary_config")
class AncillaryConfig {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "entity", columnDefinition = "varchar")
	String entityName
	
	@GraphQLQuery
	@Column(name = "port", columnDefinition = "varchar")
	String port
	
	@GraphQLQuery
	@Column(name = "ip_address", columnDefinition = "varchar")
	String ipAddress
	
	@GraphQLQuery
	@Column(name = "smb_path", columnDefinition = "varchar")
	String smbPath
	
	@GraphQLQuery
	@Column(name = "username", columnDefinition = "varchar")
	String username
	
	@GraphQLQuery
	@Column(name = "password", columnDefinition = "varchar")
	String password
	
	@GraphQLQuery
	@Column(name = "active", columnDefinition = "bool")
	Boolean active
	
	@GraphQLQuery
	@Column(name = "tcp", columnDefinition = "bool")
	Boolean tcp
	
	@GraphQLQuery
	@Column(name = "demo", columnDefinition = "bool")
	Boolean demo
	
	@GraphQLQuery
	@Column(name = "enabled", columnDefinition = "bool")
	Boolean enabled
	
	@GraphQLQuery
	@Column(name = "url", columnDefinition = "varchar")
	String url
}


