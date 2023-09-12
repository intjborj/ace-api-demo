package com.hisd3.hismk2.graphqlservices

import com.hisd3.hismk2.dao.UserDao
import com.hisd3.hismk2.domain.Authority
import com.hisd3.hismk2.domain.Permission
import com.hisd3.hismk2.domain.PersistentToken
import com.hisd3.hismk2.domain.User
import com.hisd3.hismk2.domain.hospital_config.OperationalConfiguration
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.hospital_config.OperationalConfigurationRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.security.SecurityUtils
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLContext
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
@GraphQLApi
class UserService {
	
	@Autowired
	private UserRepository userRepository
	
	@Autowired
	private EmployeeRepository employeeRepository

	@Autowired
	private OperationalConfigurationRepository opConfigRepo
	
	@Autowired
	UserDao userDao
	
	@Autowired
	PasswordEncoder passwordEncoder
	//============== All Queries ====================
	
	@GraphQLMutation
	User resetPassword(@GraphQLArgument(name = "username") String username,
	                   @GraphQLArgument(name = "newPassword") String newPassword) {
		
		User user = userRepository.findOneByLogin(username)
		
		user.password = passwordEncoder.encode(newPassword)
		user.activated = true
		user = userRepository.save(user)
		return user
	}
	
	@GraphQLMutation
	String changePassword(@GraphQLArgument(name = "username") String username) {
		String tempPassword = RandomStringUtils.random(8, true, true)
		User user = userRepository.findOneByLogin(username)
		
		user.password = passwordEncoder.encode(tempPassword)
		user.activated = false
		
		userRepository.save(user)
		return tempPassword
	}
	
	@GraphQLQuery(name = "account", description = "Get User by login")
	Employee findOneByLogin() {
		OperationalConfiguration opConfig = opConfigRepo.findAll().first()
		User user = userRepository.findOneByLogin(SecurityUtils.currentLogin())
		Employee foundEmployee = employeeRepository.findOneByUser(user)
		foundEmployee.notificationType = opConfig.ablyNotification ? 'ably' : 'default';

		return foundEmployee;
	}

	@GraphQLQuery(name = "persistentTokens", description = "Get all User persistentTokens")
	List<PersistentToken> getPersistentTokens(@GraphQLContext User user) {
		return userDao.getPersistentTokens(user)
	}

	@GraphQLQuery(name = "roles", description = "Get all User roles")
	List<String> getRoles(@GraphQLContext User user) {
		user.authorities.collect { Authority it -> it.name }
	}
	
	@GraphQLQuery(name = "access", description = "Get all User access")
	List<String> getAccess(@GraphQLContext User user) {
		user.permissions.collect { Permission it -> it.name }
	}
	
	@GraphQLQuery(name = "isLoginUnique", description = "Check if username exists")
	Boolean isLoginUnique(@GraphQLArgument(name = "login") String login) {
		return !userRepository.findOneByLogin(login.toLowerCase())
	}
}
