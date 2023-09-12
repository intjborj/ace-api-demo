package com.hisd3.hismk2.domain.ancillary

import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(schema = "ancillary", name = "integration_config")
class IntegrationConfig {
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@GraphQLQuery
	@Column(name = "nas_location", columnDefinition = "varchar")
	String nasLocation
	
	@GraphQLQuery
	@Column(name = "middleware_ip", columnDefinition = "varchar")
	String middlewateIp
	
	@GraphQLQuery
	@Column(name = "enable_integration", columnDefinition = "bool")
	Boolean enableIntegrtion
	
	@GraphQLQuery
	@Column(name = "ldap_url", columnDefinition = "varchar")
	String ldapUrl
	
	@GraphQLQuery
	@Column(name = "port", columnDefinition = "numeric")
	BigDecimal port
	
	@GraphQLQuery
	@Column(name = "admin_dn", columnDefinition = "varchar")
	String adminDn
	
	@GraphQLQuery
	@Column(name = "password", columnDefinition = "varchar")
	String password
	
	@GraphQLQuery
	@Column(name = "smb_user", columnDefinition = "varchar")
	String smbUser
	
	@GraphQLQuery
	@Column(name = "smb_pass", columnDefinition = "varchar")
	String smbPass
}

