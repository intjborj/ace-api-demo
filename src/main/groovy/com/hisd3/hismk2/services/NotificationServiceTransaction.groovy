package com.hisd3.hismk2.services

import com.hisd3.hismk2.domain.Authority
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.Notification
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.inventory.StockRequest
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.NotificationRepository
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.hospital_config.OperationalConfigurationRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.socket.HISD3MessageType
import com.hisd3.hismk2.socket.HISD3WebsocketMessage
import com.hisd3.hismk2.socket.SocketService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional
import java.time.Instant

@Service
class NotificationServiceTransaction {
	
	@Autowired
	SocketService socketService
	
	@Autowired
	NotificationRepository notificationRepository
	
	@Autowired
	EmployeeRepository employeeRepository
	
	@Autowired
	UserRepository userRepository
	
	@Autowired
	DepartmentRepository departmentRepository

    @Autowired
    OperationalConfigurationRepository opConfigRepo

	@Transactional
	void notifyUser(UUID recipientId, String title, String message) {
		try {
			Employee loggedEmployee = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()
			Employee recipientEmployee = employeeRepository.getOne(recipientId)

			if (recipientEmployee.user) {
                // ----------------- log notification on DB
				Notification notification = new Notification()
				notification.message = message
				notification.to = recipientId
				notification.from = loggedEmployee.id
				notification.title = title
				notification.datenotified = Instant.now()
				notificationRepository.save(notification)

                // ------------------ send notification
                // until we've fully tested ably, we will keep our old notif (via ActiveMQ) on standby as backup.
                // anything goes wrong, we can turn-off ably here.
                def opConfig = opConfigRepo.findAll().first();
                if(opConfig && opConfig.ablyNotification) {
                    AblyNotificationService ablyService = new AblyNotificationService()
                    ablyService.notifyChannel(recipientId.toString(), title, message);
                } else {
                    HISD3WebsocketMessage payload = new HISD3WebsocketMessage(loggedEmployee.fullName, message, title, HISD3MessageType.NOTIFICATION_NEW)
                    socketService.notificationToUser(payload, recipientEmployee.user.login)
                }
			}
		}
		catch (Exception e) {
			e.printStackTrace()
		}
	}

	@Transactional
	void notifyEmployees(List<Employee> employeeList, String title, String message) {
		for (Employee emp : employeeList) {
			notifyUser(emp.id, title, message)
		}
	}

	@Transactional
	void notifyUsersOfDepartment(UUID departmentid, String title, String message, String url) {
		List<Employee> employeeList = employeeRepository.findEmployeesByDepartment(departmentid)
		for (Employee emp : employeeList) {
			notifyUser(emp.id, title, message)
		}
	}
	
	@Transactional
	void notifyUsersOfDepartments(List<Department> departments, String title, String message, String url) {
		departments.each {
			it ->
				List<Employee> employeeList = employeeRepository.findEmployeesByDepartment(it.id)
				for (Employee emp : employeeList) {
					notifyUser(emp.id, title, message)
				}
		}
	}
	
	@Transactional
	void notifyUsersByRoles(List<String> roles, String title, String message, String url) {
		
		def authorities = []
		roles.each {
			authorities << new Authority(it)
		}
		def users = userRepository.findUserByRoles(authorities)
		Set<String> logins = new HashSet<>()
		users.each {
			if (!logins.contains(it.login)) {
				def emp = it.employee
				if (emp) {
					notifyUser(emp.id, title, message)
				}
				logins.add(it.login)
			}
		}
	}
	
	@Transactional
	void notifyGroups(List<String> groups, String title, String message, String url) {
		List<Department> recipient = []
		groups.each {
			def dept = departmentRepository.departmentsAlike(it)
			if (dept.size() > 0) {
				recipient.add(dept.first())
			}
		}
		
		if (recipient.size() > 0) {
			recipient.each {
				it ->
					notifyUsersOfDepartment(it.id, title, message, url)
			}
		}
	}
	
	@Transactional
	void notifyNewStockRequest(StockRequest stockRequest) {
		try {
			// HISD3WebsocketMessage payload = new HISD3WebsocketMessage()
			// payload.message =
			// payload.type = HISD3MessageType.STOCK_REQUEST_LIST_NEW
            List<Employee> employees = employeeRepository.findEmployeesByDepartment(stockRequest.requestedDepartment.id)
            for (Employee emp : employees) {
				notifyUser(emp.id, "Stock Request", "New Medicine Request : MSR # " + stockRequest.stockRequestNo)
            }

		} catch (Exception e) {
			e.printStackTrace()
		}
		
	}
	
	@Transactional
	void notifySentClaimableStockRequest(StockRequest stockRequest) {
		try {
			// HISD3WebsocketMessage payload = new HISD3WebsocketMessage()
			// payload.message = "Medicine Request : MSR # " + stockRequest.stockRequestNo + " is now " + stockRequest.status
			// payload.type = HISD3MessageType.STOCK_REQUEST_LIST_CLAIMABLE
			// simpMessagingTemplate.convertAndSend("/channel/notifications", payload)
			List<Employee> employees = employeeRepository.findEmployeesByDepartment(stockRequest.requestingDepartment.id)
			for (Employee emp : employees) {
				notifyUser(emp.id, "Stock Request", "Medicine Request : MSR # " + stockRequest.stockRequestNo + " is now " + stockRequest.status)
			}
		}
		catch (Exception e) {
			e.printStackTrace()
		}
		
	}
}
