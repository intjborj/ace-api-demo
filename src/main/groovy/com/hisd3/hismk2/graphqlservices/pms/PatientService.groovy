package com.hisd3.hismk2.graphqlservices.pms

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.User
import com.hisd3.hismk2.domain.dietary.Diet
import com.hisd3.hismk2.domain.hospital_config.OperationalConfiguration
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.Patient
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
import com.hisd3.hismk2.rest.dto.PatientBasicDto
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
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

import java.time.Instant

@Component
@GraphQLApi
class PatientService {

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
	JdbcTemplate jdbcTemplate

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	NotificationService notificationService

	//============== All Queries ====================

	@GraphQLQuery(name = "patients", description = "Get All Patients")
	List<Patient> findAll() {
		patientRepository.findAll().sort { it.fullName }
	}

	@GraphQLQuery(name = "patientsListByFilter", description = "Get All Patients")
	List<Patient> patientsListByFilter(@GraphQLArgument(name = "filter") String filter) {
		if(filter == ""){
			return
		}
		def list =patientRepository.patientsListByFilter(filter).sort{it.fullName}
		return list
	}

	@GraphQLQuery(name = "patient", description = "Get Patient By Id")
	Patient findById(@GraphQLArgument(name = "id") UUID id) {
		return id ? patientRepository.findById(id).get() : null
	}

	@GraphQLQuery(name = "searchPatients", description = "Search patients")
	List<Patient> searchPatients(@GraphQLArgument(name = "filter") String filter, @GraphQLArgument(name = "idFilter") UUID idFilter) {
		Set<Patient> pts = patientRepository.searchPatients(filter).toSet()
		for (Patient pt in patientRepository.searchPatientsWithRoom(filter)) {
			pts.add(pt)
		}
		pts.toList().sort { it.fullName }
	}

	@GraphQLQuery(name = "filterPatientsPageable", description = "Search patients pageable")
	Page<Patient> filterPatientsPageable(
			@GraphQLArgument(name = 'filter') String filter,
			@GraphQLArgument(name = 'page') Integer page,
			@GraphQLArgument(name = 'pageSize') Integer pageSize
	) {
		patientRepository.searchPatientsPageable(filter, PageRequest.of(page, pageSize, Sort.Direction.ASC, 'fullName'))
	}

	@GraphQLQuery(name = "searchPatientsPageable", description = "Search patients pageable")
	Page<Patient> searchPatientsPageable(
			@GraphQLArgument(name = 'filter') String filter,
			@GraphQLArgument(name = 'page') Integer page,
			@GraphQLArgument(name = 'pageSize') Integer pageSize,
			@GraphQLArgument(name = 'idFilter') UUID idFilter
	) {
		switch (filter) {
			case 'ADMITTED': patientRepository.filterPatientsByAdmittedPageable(filter, PageRequest.of(page, pageSize, Sort.Direction.ASC, 'patient.fullName'))
				break
			case 'FOR-DISCHARGE': patientRepository.filterPatientsByForDischargePageable(filter, PageRequest.of(page, pageSize, Sort.Direction.ASC, 'patient.fullName'))
				break
			case 'DISCHARGED': patientRepository.filterPatientsByForDischargePageable(filter, PageRequest.of(page, pageSize, Sort.Direction.ASC, 'patient.fullName'))
				break
			case 'ASSIGNED-TO-ME':
				User user = userRepository.findOneByLogin(SecurityUtils.currentLogin())
				Employee employee = employeeRepository.findOneByUser(user)
				patientRepository.filterPatientsByEmployeePageable(employee.id, PageRequest.of(page, pageSize, Sort.Direction.ASC, 'parentCase.patient.fullName'))
				break
			case 'ROOM': patientRepository.filterPatientsByRoomPageable(idFilter, PageRequest.of(page, pageSize, Sort.Direction.ASC, 'patient.fullName'))
				break
			case 'ATTENDING-PHYSICIAN': patientRepository.filterPatientsByEmployeePageable(idFilter, PageRequest.of(page, pageSize, Sort.Direction.ASC, 'parentCase.patient.fullName'))
				break
			default: patientRepository.searchPatientsPageable(filter, PageRequest.of(page, pageSize, Sort.Direction.ASC, 'fullName'))
		}
	}

	@GraphQLQuery(name = "patientsByFilter", description = "Filter patients")
	Set<Patient> patientsByFilter(@GraphQLArgument(name = "type") String type, @GraphQLArgument(name = "filter") String filter) {
		User user = userRepository.findOneByLogin(SecurityUtils.currentLogin())
		Employee employee = employeeRepository.findOneByUser(user)

		switch (type) {
			case 'MY PATIENTS':
				patientRepository.filterPatientsByEmployee(employee.id, filter).sort { it.fullName } as Set
				break
			case 'ER': patientRepository.filterActivePatients('ERD', filter).sort { it.fullName } as Set
				break
			case 'ADMITTED': patientRepository.filterActivePatients('IPD', filter).sort { it.fullName } as Set
				break
			case 'FOR ADMISSION': patientRepository.filterActivePatients('ADM', filter).sort { it.fullName } as Set
				break
			case 'OUTPATIENTS': patientRepository.filterActivePatients('OPD', filter).sort { it.fullName } as Set
				break
			case 'FOR DISCHARGE': patientRepository.filterPatientsByForDischarge(filter).sort { it.fullName } as Set
				break
			case 'DISCHARGED': patientRepository.filterPatientsByDischarge(filter).sort { it.fullName } as Set
				break
			default: patientRepository.filterPatients(filter).sort { it.fullName } as Set
		}
	}

	@GraphQLQuery(name = "patientsDailySummary", description = "Patients Daily Summary")
	Map<String, Integer> patientsDailySummary() {
		User user = userRepository.findOneByLogin(SecurityUtils.currentLogin())
		Employee employee = employeeRepository.findOneByUser(user)

		return [
				'MY PATIENTS'  : patientRepository.countPatientsByEmployee(employee.id),
				'ER'           : patientRepository.countActivePatients('ERD'),
				'ADMITTED'     : patientRepository.countActivePatients('IPD'),
				'FOR ADMISSION': patientRepository.countActivePatients('ADM'),
				'OUTPATIENTS'  : patientRepository.countActivePatients('OPD'),
				'FOR DISCHARGE': patientRepository.countPatientsByForDischarge(),
		]
	}

	@GraphQLQuery(name = "patientCases", description = "Get All Patient Cases")
	List<Case> getCases(@GraphQLContext Patient patient) {
		return caseRepository.getPatientCases(patient.id).sort { it.status }
	}

	@GraphQLQuery(name = "patientActiveCase", description = "Get Patient active Case")
	Case getPatientActiveCase(@GraphQLContext Patient patient) {
		return caseRepository.getPatientActiveCase(patient.id)
	}

	//============== All Mutations ====================

	@GraphQLMutation
	Patient upsertPatient(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {

		if (id) {
			Patient patient = patientRepository.findById(id).get()
			objectMapper.updateValue(patient, fields)
			return patientRepository.save(patient)
		} else {

			def serviceType = fields["serviceType"] as String
			def serviceCode = fields["serviceCode"] as Integer
			def registryType = fields["registryType"] as String
			def accommodationType = fields["accommodationType"] as String
			def departmentId = fields["departmentId"] as String

			def tsCode = 0

			//Initialize patient data
			def patientObj = objectMapper.convertValue(fields, Patient)

			Department department = departmentRepository.findById(UUID.fromString(departmentId)).get()

			patientObj.firstName = patientObj.firstName.trim()
			patientObj.lastName = patientObj.lastName.trim()
			patientObj.middleName = patientObj.middleName.trim()


			patientObj.patientNo = generatorService.getNextValue(GeneratorType.PATIENT_NO) { Long no ->
				StringUtils.leftPad(no.toString(), 6, "0")
			}

			def patient = patientRepository.save(patientObj)

			//Initialize case data
			Case pCase = new Case()

			def caseNo = generatorService?.getNextValue(GeneratorType.CASE_NO, { i ->
				StringUtils.leftPad(i.toString(), 6, "0")
			})

//			if (serviceType) {
//				DohServiceType st = dohServiceTypeRepository.getDOHServiceTypesByDesc(serviceType)
//				pCase.serviceCode = st.tscode
//			}

			pCase.patient = patient
			pCase.caseNo = caseNo
			pCase.serviceType = serviceType
			pCase.serviceCode = serviceCode
			pCase.registryType = registryType
			pCase.accommodationType = accommodationType
			pCase.entryDateTime = Instant.now()
			pCase.department = department
			pCase.priceAccommodationType = "STANDARD"

//			if (registryType == "ERD" || registryType == "OPD") {
//				pCase.priceTierDetail = priceTierDetailRepository.getTier(registryType, accommodationType)
//			}
			OperationalConfiguration oc = operationalConfigurationRepository.findAll().find()

			pCase.creditLimit = oc.defaultCreditLimit

			pCase = caseRepository.save(pCase)

			//END.Initialize case data -------

			//Initialize transfer data -------
			Transfer pTransfer = new Transfer()

			pTransfer.registryType = registryType
			pTransfer.department = department
			pTransfer.entryDateTime = Instant.now()
			pTransfer.parentCase = pCase
			//pTransfer.active = true

			transferRepository.save(pTransfer)
			//END.Initialize transfer data -------

			if(patient && patient.id){
				// prevent auto duplicate of folios
				def activeBilling = billingService.activeBilling(pCase)
				if(!activeBilling){
					billingService.createBilling(
							patient.id,
							pCase.id
					)
				}

			}

			try {
				//Notify
				List<String> filters = ['MEDICAL RECORDS', 'BILLING', 'ADMITTING', '']
				String message = "Patient " + patient.lastName + ', ' + patient.firstName + " has been successfully registered."
				notificationService.notifyGroups(filters, "New Patient Added", message, "")
				List<String> roles = ['RESIDENT_PHYSICIAN']
				notificationService.notifyUsersByRoles(roles, "New Patient Added", message, "")
			} catch (Exception e) {
				println(e.message)
			} finally {
				return patient
			}

			return patient
		}
	}

	@GraphQLMutation(name = "update_patient_diet")
	Patient updatePatientDiet(@GraphQLArgument(name = "patientId") UUID patientId, @GraphQLArgument(name = 'dietId') UUID dietId) {
		Patient patient = new Patient()
		if (patientId) {
			patient = patientRepository.findById(patientId).get()

			Case patientCase = caseRepository.getPatientActiveCase(patient.id)

			if (patientCase) {
				Diet diet = dietRepository.findById(dietId).get()
				patientCase.diet = diet

				caseRepository.save(patientCase)
			}
		}

		return patient
	}

	List<PatientBasicDto> getAllPatientDto(){
		String sql = "select id,first_name as \"firstname\", last_name as \"lastname\", middle_name as \"middlename\", dob,name_suffix as \"suffix\" from pms.patients"
		List<PatientBasicDto> items = jdbcTemplate.query(sql, new BeanPropertyRowMapper(PatientBasicDto.class))
		return items
	}
}
