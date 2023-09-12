package com.hisd3.hismk2.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "T_GROUP_POLICY")
class GroupPolicy {
	
	@GraphQLQuery
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", columnDefinition = "uuid")
	@Type(type = "pg-uuid")
	UUID id
	
	@NotNull
	@Column(name = "name", columnDefinition = "varchar")
	String name
	
	@Column(name = "description", columnDefinition = "varchar")
	String description
	
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
	@JoinTable(name = "t_group_policy_permission",
			joinColumns = [@JoinColumn(name = "group_policy_id", referencedColumnName = "id")],
			inverseJoinColumns = [@JoinColumn(name = "permission_name", referencedColumnName = "name")])
	List<Permission> permissionsList = [] as List
	
	@Transient
	List<String> getPermissions() {
		def permissions = []
		if (permissionsList != null)
			permissionsList.each { permission ->
				
				permissions.add(permission.name)
			}
		
		return permissions as List
	}
}
