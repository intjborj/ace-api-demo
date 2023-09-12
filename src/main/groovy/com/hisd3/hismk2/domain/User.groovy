package com.hisd3.hismk2.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.hisd3.hismk2.domain.hrm.Employee
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLQuery

import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import java.time.LocalDateTime

@TypeChecked
@Entity
@Table(schema = "public", name = "t_user")
class User extends AbstractAuditingEntity {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id
	
	@OneToOne(mappedBy = "user")
	Employee employee
	
	@GraphQLQuery
	@NotNull
	@Size(min = 1, max = 50)
	@Column(length = 50, unique = true, nullable = false)
	String login
	
	@GraphQLQuery
	@JsonIgnore
	@NotNull
	@Size(min = 60, max = 60)
	@Column(length = 60)
	String password
	
	@GraphQLQuery
	@Size(max = 50)
	@Column(name = "first_name", length = 50)
	String firstName
	
	@GraphQLQuery
	@Size(max = 50)
	@Column(name = "last_name", length = 50)
	String lastName
	
	@GraphQLQuery
	@Email
	@Size(max = 100)
	@Column(length = 100, unique = true)
	String email
	
	@GraphQLQuery
	@Column(nullable = false)
	boolean activated = false
	
	@GraphQLQuery
	@Size(min = 2, max = 5)
	@Column(name = "lang_key", length = 5)
	String langKey
	
	@GraphQLQuery
	@Size(max = 20)
	@Column(name = "activation_key", length = 20)
	@JsonIgnore
	String activationKey
	
	@GraphQLQuery
	@Size(max = 20)
	@Column(name = "reset_key", length = 20)
	String resetKey
	
	@GraphQLQuery
	@Column(name = "reset_date", nullable = true)
	LocalDateTime resetDate
	
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
	@JoinTable(name = "t_user_authority",
			joinColumns = [@JoinColumn(name = "user_id", referencedColumnName = "id")],
			inverseJoinColumns = [@JoinColumn(name = "authority_name", referencedColumnName = "name")])
	List<Authority> authorities = [] as List
	
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "user")
	List<PersistentToken> persistentTokens = [] as List
	
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
	@JoinTable(name = "t_user_permission",
			joinColumns = [@JoinColumn(name = "user_id", referencedColumnName = "id")],
			inverseJoinColumns = [@JoinColumn(name = "permission_name", referencedColumnName = "name")])
	List<Permission> permissions = [] as List
	
	// samok samok
	/*@Transient
	List<String> getRoles() {
		def roles = []
		if (authorities != null)
			authorities.each { authority ->
				
				roles.add(authority.name)
			}
		
		return roles as List
	}
	
	@Transient
	List<String> getAccess() {
		def access = []
		if (permissions != null)
			permissions.each { permission ->
				
				access.add(permission.name)
			}
		
		return access as List
	}*/
}
