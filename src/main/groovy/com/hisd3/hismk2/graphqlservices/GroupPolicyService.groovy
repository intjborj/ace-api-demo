package com.hisd3.hismk2.graphqlservices

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.GroupPolicy
import com.hisd3.hismk2.repository.GroupPolicyRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class GroupPolicyService {
	
	@Autowired
	private GroupPolicyRepository groupPolicyRepository
	
	@Autowired
	ObjectMapper objectMapper
	
	@Autowired
	JdbcTemplate jdbcTemplate
	
	//============== All Queries ====================
	
	@GraphQLQuery(name = "groupPolicies", description = "Get all Group Policies")
	List<GroupPolicy> findAll() {
		return groupPolicyRepository.findAll().sort { it.name }
	}
	
	@GraphQLQuery(name = "getGroupPolicyById", description = "Get Group Policy by name")
	List<GroupPolicy> getGroupPolicyById(@GraphQLArgument(name = "groupPolicyId") List<UUID> id) {
		if(id.isEmpty()) return []
		return groupPolicyRepository.findByIdIn(id)
	}
	
	// mutations
	
	@GraphQLMutation
	GroupPolicy upsertGroupPolicy(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "permissions") Set<String> permissions,
			@GraphQLArgument(name = "deletedPermissions") Set<String> deletedPermissions
	) {
		if (id) {
			GroupPolicy groupPolicy = groupPolicyRepository.findById(id).get()
			objectMapper.updateValue(groupPolicy, fields)
			
			deletedPermissions.each {
				jdbcTemplate.update(
						"DELETE FROM public.t_group_policy_permission WHERE group_policy_id = ? AND permission_name = ?",
						groupPolicy.id, it
				)
			}
			
			permissions.each {
				jdbcTemplate.update(
						"INSERT INTO public.t_group_policy_permission (group_policy_id, permission_name) VALUES (?, ?) ON CONFLICT DO NOTHING",
						groupPolicy.id, it
				)
			}
			
			return groupPolicyRepository.save(groupPolicy)
		} else {
			GroupPolicy groupPolicy = objectMapper.convertValue(fields, GroupPolicy)
			
			groupPolicyRepository.save(groupPolicy)
			
			permissions.each {
				jdbcTemplate.update(
						"INSERT INTO public.t_group_policy_permission (group_policy_id, permission_name) VALUES (?, ?)",
						groupPolicy.id, it
				)
			}
			
			return groupPolicy
		}
	}
}
