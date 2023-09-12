package com.hisd3.hismk2.graphqlservices.pms

import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.ManagingPhysician
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.repository.pms.ManagingPhysicianRepository
import com.hisd3.hismk2.services.NotificationService
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class ManagingPhysicianService {
	
	@Autowired
	private ManagingPhysicianRepository managingPhysicianRepository
	
	@Autowired
	private EmployeeRepository employeeRepository
	
	@Autowired
	private CaseRepository caseRepository
	
	@Autowired
	private NotificationService notificationService
	
	//============== All Queries ====================
	@GraphQLQuery(name = "managingPhysician", description = "Get Managing Physician By Id")
	ManagingPhysician findById(@GraphQLArgument(name = "id") UUID id) {
		return managingPhysicianRepository.findById(id).get()
	}
	
	@GraphQLQuery(name = "managingPhysiciansByCase", description = "Get all patient managing physicians by Case Id")
	List<ManagingPhysician> getManagingPhysiciansByCase(@GraphQLArgument(name = "caseId") UUID caseId) {
		return managingPhysicianRepository.getManagingPhysiciansByCase(caseId).sort { it.createdDate }
	}
	
	@GraphQLQuery(name = "managingStaffByCase", description = "Get all patient managing staff by Case Id")
	List<ManagingPhysician> getManagingStaffByCase(@GraphQLArgument(name = "caseId") UUID caseId) {
		return managingPhysicianRepository.getManagingStaffByCase(caseId).sort { it.createdDate }
	}
	
	@GraphQLMutation
	ManagingPhysician upsertManagingPhysician(
			@GraphQLArgument(name = 'employeeId') UUID employeeId,
			@GraphQLArgument(name = 'caseId') UUID caseId,
			@GraphQLArgument(name = 'position') String position
	) {
		ManagingPhysician managingPhysician = new ManagingPhysician()
		
		Employee employee = employeeRepository.findById(employeeId).get()
		Case parentCase = caseRepository.findById(caseId).get()
		
		managingPhysician.employee = employee
		managingPhysician.parentCase = parentCase
		managingPhysician.position = position
		
		if (employee) {
			notificationService.notifyUser(employee.id, "Patient Management", "Patient " + parentCase.patient.fullName + " has been assigned to your care. God bless you")
		}
		
		return managingPhysicianRepository.save(managingPhysician)
	}
	
	@GraphQLMutation
	ManagingPhysician upsertManagingPhysicianByFullName(
			@GraphQLArgument(name = 'employeeFullName') String employeeFullName,
			@GraphQLArgument(name = 'caseId') UUID caseId,
			@GraphQLArgument(name = 'position') String position
	) {
		ManagingPhysician managingPhysician = new ManagingPhysician()
		
		Employee employee = employeeRepository.getEmployeeByFullName(employeeFullName).find()
		Case parentCase = caseRepository.findById(caseId).get()
		
		managingPhysician.employee = employee
		managingPhysician.parentCase = parentCase
		managingPhysician.position = position
		
		if (employee) {
			notificationService.notifyUser(employee.id, "Patient Management", "Patient " + parentCase.patient.fullName + " has been assigned to your care. God bless you")
		}
		
		return managingPhysicianRepository.save(managingPhysician)
	}
	
	@GraphQLQuery(name = "getPF", description = "Get PF")
	ManagingPhysician getPF(
			@GraphQLArgument(name = 'employeeId') UUID employeeId,
			@GraphQLArgument(name = 'caseId') UUID caseId
	) {
		return managingPhysicianRepository.getPhysician(caseId, employeeId).findAll().find()
	}
	
	@GraphQLMutation
	def postPF(
			@GraphQLArgument(name = 'employeeId') UUID employeeId,
			@GraphQLArgument(name = 'caseId') UUID caseId,
			@GraphQLArgument(name = 'pf') String pf
	) {
		ManagingPhysician managingPhysician = managingPhysicianRepository.getPhysician(caseId, employeeId).findAll().find()
		
		if (managingPhysician) {
			Employee employee = employeeRepository.findById(employeeId).get()
			managingPhysician.professionalFee = pf as BigDecimal
			
			if (employee) {
				notificationService.notifyUser(employee.id, "Professional Fee", "Professional Fee successfully posted")
			}
			
			managingPhysicianRepository.save(managingPhysician)
		}
	}
}
