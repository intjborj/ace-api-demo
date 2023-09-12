package com.hisd3.hismk2.graphqlservices

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Notification
import com.hisd3.hismk2.repository.NotificationRepository
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.NotificationService
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class NotificationGQLService {
	
	@Autowired
	private NotificationService notificationService
	
	@Autowired
	private NotificationRepository notificationRepository
	
	@Autowired
	GeneratorService generatorService
	
	@Autowired
	ObjectMapper objectMapper
	
	@Autowired
	UserRepository userRepository
	
	@Autowired
	EmployeeRepository employeeRepository
	
	//============== All Queries ====================
	
	@GraphQLQuery(name = "mynotifications", description = "Get All My Notifications")
	List<Notification> mynotifications(@GraphQLArgument(name = "id") UUID id) {
		def results = notificationRepository.findTop10ByToOrderByDatenotifiedDesc(id)
		return results
	}
	
	@GraphQLQuery(name = "myownnotifications", description = "Get All My Notifications")
	List<Notification> myownnotifications() {
		
		def username = SecurityUtils.currentLogin()
		def user = userRepository.findOneByLogin(username)
		def emp = employeeRepository.findOneByUser(user)
		def results = notificationRepository.findTop10ByToOrderByDatenotifiedDesc(emp.id)
		return results
	}
}
