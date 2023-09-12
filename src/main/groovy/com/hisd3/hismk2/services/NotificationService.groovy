package com.hisd3.hismk2.services

import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.inventory.StockRequest
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

/**
 *  For optimization purpose, methods are marked Async... But it will also loses Transaction.
 *  AOP is handled from another class NotificationServiceTransaction
 *
 */
@Service
@TypeChecked
class NotificationService {
	
	@Autowired
	NotificationServiceTransaction notificationServiceTransaction

	@Async
	void notifyUser(UUID userid, String title, String message) {
		notificationServiceTransaction.notifyUser(userid, title, message)
	}

	@Async
	void notifyEmployees(List<Employee> employees, String title, String message) {
		notificationServiceTransaction.notifyEmployees(employees, title, message)
	}
	
	@Async
	void notifyUsersOfDepartment(UUID departmentid, String title, String message, String url) {
		notificationServiceTransaction.notifyUsersOfDepartment(departmentid, title, message, url)
	}
	
	@Async
	void notifyUsersOfDepartments(List<Department> departments, String title, String message, String url) {
		notificationServiceTransaction.notifyUsersOfDepartments(departments, title, message, url)
	}
	
	@Async
	void notifyUsersByRoles(List<String> roles, String title, String message, String url) {
		
		notificationServiceTransaction.notifyUsersByRoles(roles, title, message, url)
	}
	
	@Async
	void notifyGroups(List<String> groups, String title, String message, String url) {
		notificationServiceTransaction.notifyGroups(groups, title, message, url)
	}
	
	@Async
	void notifyNewStockRequest(StockRequest stockRequest) {
		notificationServiceTransaction.notifyNewStockRequest(stockRequest)
	}
	
	@Async
	void notifySentClaimableStockRequest(StockRequest stockRequest) {
		notificationServiceTransaction.notifySentClaimableStockRequest(stockRequest)
	}
}
