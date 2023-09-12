package com.hisd3.hismk2.graphqlservices.pms

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.User
import com.hisd3.hismk2.domain.dietary.Diet
import com.hisd3.hismk2.domain.hospital_config.OperationalConfiguration
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.Patient
import com.hisd3.hismk2.domain.pms.PatientCaseView
import com.hisd3.hismk2.domain.pms.Transfer
import com.hisd3.hismk2.graphqlservices.billing.BillingService
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.dietary.DietRepository
import com.hisd3.hismk2.repository.hospital_config.OperationalConfigurationRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.repository.pms.PatientRepository
import com.hisd3.hismk2.repository.pms.TransferRepository
import com.hisd3.hismk2.repository.referential.DohServiceTypeRepository
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import com.hisd3.hismk2.services.NotificationService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLContext
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

import java.time.Instant

@Component
@GraphQLApi
class PatientReportService {
	
	@Autowired
	private PatientRepository patientRepository
	
	@Autowired
	private CaseRepository caseRepository
	
	@Autowired
	private TransferRepository transferRepository
	
	@Autowired
	private DepartmentRepository departmentRepository
	
	@Autowired
	private DohServiceTypeRepository dohServiceTypeRepository
	
	@Autowired
	GeneratorService generatorService
	
	@Autowired
	OperationalConfigurationRepository operationalConfigurationRepository
	
	@Autowired
	UserRepository userRepository
	
	@Autowired
	EmployeeRepository employeeRepository
	
	@Autowired
	DietRepository dietRepository
	
	@Autowired
	BillingService billingService
	
	@Autowired
	ObjectMapper objectMapper
	
	@Autowired
	NotificationService notificationService
	
	//============== All Queries ====================


}
