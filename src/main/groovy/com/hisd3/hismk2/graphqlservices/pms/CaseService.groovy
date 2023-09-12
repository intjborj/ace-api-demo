package com.hisd3.hismk2.graphqlservices.pms

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.dao.census.CaseDao
import com.hisd3.hismk2.dao.price_tier.PriceTierDetailDao
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.User
import com.hisd3.hismk2.domain.billing.Billing
import com.hisd3.hismk2.domain.billing.BillingItem
import com.hisd3.hismk2.domain.bms.Room
import com.hisd3.hismk2.domain.dietary.Diet
import com.hisd3.hismk2.domain.dietary.DietLog
import com.hisd3.hismk2.domain.hospital_config.OperationalConfiguration
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.pms.*
import com.hisd3.hismk2.graphqlservices.billing.BillingItemServices
import com.hisd3.hismk2.graphqlservices.billing.BillingService
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.bms.RoomRepository
import com.hisd3.hismk2.repository.dietary.DietLogRepository
import com.hisd3.hismk2.repository.dietary.DietRepository
import com.hisd3.hismk2.repository.hospital_config.OperationalConfigurationRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.pms.*
import com.hisd3.hismk2.rest.dto.CensusDTO
import com.hisd3.hismk2.rest.dto.CreditLimitDto
import com.hisd3.hismk2.rest.dto.DepartmentCensusDto
import com.hisd3.hismk2.rest.dto.MyCensusDTO
import com.hisd3.hismk2.rest.dto.TierDTO
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import com.hisd3.hismk2.services.NotificationService
import groovy.transform.TypeChecked
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

import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@TypeChecked
@Component
@GraphQLApi
class CaseService {

	@Autowired
	private PatientRepository patientRepository

	@Autowired
	private CaseRepository caseRepository

	@Autowired
	private RoomRepository roomRepository

	@Autowired
	private NurseNoteRepository nurseNoteRepository

	@Autowired
	private VitalSignRepository vitalSignRepository

	@Autowired
	private TransferRepository transferRepository

	@Autowired
	private MedicationRepository medicationRepository

	@Autowired
	private IntakeRepository intakeRepository

	@Autowired
	private OutputRepository outputRepository

	@Autowired
	private DepartmentRepository departmentRepository

	@Autowired
	private CaseInsuranceRepository caseInsuranceRepository

	@Autowired
	private OperationalConfigurationRepository operationalConfigurationRepository

	@Autowired
	private FileAttachmentRepository fileAttachmentRepository

	@Autowired
	private PriceTierDetailDao priceTierDetailDao

	@Autowired
	private CaseDao caseDao

	@Autowired
	GeneratorService generatorService

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	DietRepository dietRepository

	@Autowired
	BillingService billingService

	@Autowired
	DietLogRepository dietLogRepository

	@Autowired
	UserRepository userRepository

	@Autowired
	DoctorOrderRepository doctorOrderRepository

	@Autowired
	EmployeeRepository employeeRepository

	@Autowired
	NotificationService notificationService

	@Autowired
	BillingItemServices billingItemServices

	@Autowired
	DoctorOrderItemRepository doctorOrderItemRepository

	@Autowired
	ObgynHistoryRepository obgynHistoryRepository

	@Autowired
	DoctorOrderProgressNoteRepository doctorOrderProgressNoteRepository

	//============== All Queries ====================

	@GraphQLQuery(name = "cases", description = "Get all Cases")
	List<Case> findAll() {
		return caseRepository.findAll()
	}

	@GraphQLQuery(name = "case", description = "Get Case By Id")
	Case findById(@GraphQLArgument(name = "id") UUID id, @GraphQLArgument(name = "sort") String sort) {
		if(id){
			return caseRepository.findById(id).get()
		}else{
			return null
		}
	}

	@GraphQLQuery(name = "isCreditLimitReached", description = "status of ")
	CreditLimitDto isCreditLimitReached(@GraphQLArgument(name = "caseid") UUID caseid) {
		Case ptCase = caseRepository.getOne(caseid)
		CreditLimitDto creditLimitDto = new CreditLimitDto()
		creditLimitDto.credit_limit_reached = billingService.isCreditLimitReached(billingService.activeBilling(ptCase)) ;
		return creditLimitDto
	}

	@GraphQLQuery(name = "billingrecords")
	List<Billing> getBillingByPatientCase(@GraphQLContext Case aCase) {

		return billingService.findByPatientCase(aCase.id)
	}

	@GraphQLQuery(name = "patientActiveBillingByCase")
	List<Billing> getPatientActiveBillingByCase(@GraphQLArgument(name = "caseId") UUID caseId) {
		def aCase = caseRepository.findById(caseId).get()
		return billingService.getActiveBillingByCase(aCase)
	}

	@GraphQLQuery(name = "patientActiveBillingItems")
	Page<BillingItem> getPatientActiveBillingItems(
			@GraphQLArgument(name = "billingId") UUID billingId,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "pageSize") Integer pageSize,
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "types") List<String> types
	) {

		if (billingId) {
			return billingItemServices.getBillingItemsByTypes(billingId, page, pageSize, filter?:"", types)
		}

		return null
	}

	@GraphQLQuery(name = "patientActiveCase", description = "Get Patient active Case")
	Case getPatientActiveCase(@GraphQLArgument(name = "patientId") UUID patientId) {
		return caseRepository.getPatientActiveCase(patientId)
	}

	@GraphQLQuery(name = "caseNurseNotes", description = "Get all Case NurseNotes")
	List<NurseNote> getNurseNotes(@GraphQLContext Case parentCase) {
		return nurseNoteRepository.getNurseNotesByCase(parentCase.id).sort { it.entryDateTime }
	}

	@GraphQLQuery(name = "caseVitalSigns", description = "Get all Case VitalSigns")
	List<VitalSign> getVitalSigns(@GraphQLContext Case parentCase) {

		return vitalSignRepository.getVitalSignsByCase(parentCase.id).sort { it.entryDateTime }
	}

	@GraphQLQuery(name = "caseInsurances", description = "Get all Case VitalSigns")
	List<CaseInsurance> getCaseInsurances(@GraphQLContext Case parentCase) {
		return caseInsuranceRepository.getCaseInsurancesByCase(parentCase.id).sort { it.createdDate }
	}

	@GraphQLQuery(name = "caseTransfers", description = "Get all Case Transfers by Case")
	List<Transfer> getTransfers(@GraphQLContext Case parentCase) {

		return transferRepository.getTransfersByCase(parentCase.id, Sort.by(Sort.Direction.DESC, "entryDateTime"))
	}

	@GraphQLQuery(name = "doctorOrders", description = "Get all DoctorOrders by Case Id")
	List<DoctorOrder> getDoctorOrdersByCase(@GraphQLContext Case parentCase) {
		return doctorOrderRepository.getDoctorOrdersByCase(parentCase.id).sort { it.entryDateTime }
	}

	@GraphQLQuery(name = "getCensus", description = "Search Transfers")
	CensusDTO getCensus() {
		List<Transfer> transfers = transferRepository.getAllTransfers()

		Map<String, List<Transfer>> combinedList

		List<Transfer> inPatients = []
		List<Transfer> outPatients = []
		List<Transfer> erPatients = []

		transfers.forEach({
			Transfer row ->
				switch (row.registryType) {
					case 'IPD': inPatients.add(row); break
					case 'OPD': outPatients.add(row); break
					case 'ERD': erPatients.add(row); break
					default: break
				}
		})

		def censusDTO = new CensusDTO()

		censusDTO.inPatientsCount = inPatients.size()
		censusDTO.outPatientsCount = outPatients.size()
		censusDTO.erPatientsCount = erPatients.size()

		return censusDTO
	}

	@GraphQLQuery(name = "caseMedications", description = "Get all Case Medications")
	List<Medication> getMedicationsByCase(@GraphQLContext Case parentCase) {
		return medicationRepository.getMedicationsByCase(parentCase.id).sort { it.entryDateTime }
	}

	@GraphQLQuery(name = "caseIntakes", description = "Get all Case Intakes")
	List<Intake> getIntakesByCase(@GraphQLContext Case parentCase) {
		return intakeRepository.getIntakesByCase(parentCase.id).sort { it.entryDateTime }
	}

	@GraphQLQuery(name = "caseOutputs", description = "Get all Case Outputs")
	List<Output> getOutputsByCase(@GraphQLContext Case parentCase) {
		return outputRepository.getOutputsByCase(parentCase.id).sort { it.entryDateTime }
	}

	@GraphQLQuery(name = "getItemPrice", description = "Get price")
	TierDTO getItemPrice(
			@GraphQLArgument(name = "parentCase") UUID parentCase,
			@GraphQLArgument(name = "item") UUID item
	) {
		return priceTierDetailDao.getItemTier(parentCase, item)
	}

	@GraphQLQuery(name = "getServicePrice", description = "Get price")
	TierDTO getServicePrice(
			@GraphQLArgument(name = "parentCase") UUID parentCase,
			@GraphQLArgument(name = "service") UUID service,
			@GraphQLArgument(name = "room") String room
	) {
		return priceTierDetailDao.getServiceTier(parentCase, service)
	}

	@GraphQLQuery(name = "casePhotosByCase")
	List<FileAttachment> casePhotosByCase(@GraphQLArgument(name = "caseId") UUID caseId) {
		return fileAttachmentRepository.casePhotosByCase(caseId).sort { it.fileName }
	}

	@GraphQLQuery(name = "latestMeal", description = "Get latest Meal")
	DietLog getLatest(@GraphQLContext Case parentCase) {
		return dietLogRepository.getLatest(parentCase.id)
	}

	Boolean hasActiveCase(String patientId) {
		def currentCase = caseRepository.getPatientActiveCase(UUID.fromString(patientId))

		if (currentCase)
			return true
		else
			return false
	}

	@GraphQLMutation
	Case upsertCase(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {

		if (id) {
			Case caseObj = caseRepository.findById(id).get()
			objectMapper.updateValue(caseObj, fields)
			return caseRepository.save(caseObj)
		} else {
			def serviceType = fields["serviceType"] as String
			def registryType = fields["registryType"] as String
			def accommodationType = fields["accommodationType"] as String
			def departmentId = fields["departmentId"] as String
			def patientId = fields["patientId"] as String

			//Initialize patient data
			def caseObj = objectMapper.convertValue(fields, Case)

			Department department = departmentRepository.findById(UUID.fromString(departmentId)).get()

			def caseNo = generatorService?.getNextValue(GeneratorType.CASE_NO, { i ->
				StringUtils.leftPad(i.toString(), 6, "0")
			})

			caseObj.patient = patientRepository.findById(UUID.fromString(patientId)).get()

			caseObj.department = department
			caseObj.caseNo = caseNo
			caseObj.serviceType = serviceType
			caseObj.registryType = registryType
			caseObj.accommodationType = accommodationType
			caseObj.entryDateTime = Instant.now()
			caseObj.priceAccommodationType = "STANDARD"

			if (hasActiveCase(patientId)) {
				caseObj.status = ""
			}

			OperationalConfiguration oc = operationalConfigurationRepository.findAll().find()
			caseObj.creditLimit = oc.defaultCreditLimit

			caseObj = caseRepository.save(caseObj)

			//END.Initialize case data -------

			//Initialize transfer data -------
			Transfer pTransfer = new Transfer()

			def consultation = fields["consultation"] as String
			pTransfer.consultation = consultation
			pTransfer.registryType = registryType
			pTransfer.department = department
			pTransfer.entryDateTime = Instant.now()
			pTransfer.parentCase = caseObj
			//pTransfer.active = true

			transferRepository.save(pTransfer)
			//END.Initialize transfer data -------

			if(StringUtils.isNotBlank(patientId)){
				// prevent auto duplicate of folios
				def activeBilling = billingService.activeBilling(caseObj)
				if(!activeBilling){
					billingService.createBilling(
							UUID.fromString(patientId),
							caseObj.id
					)
				}

			}


			return caseObj
		}
	}

	@GraphQLMutation
	Case changeCaseStatus(
			@GraphQLArgument(name = "id") String id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {

		Case caseObj = caseRepository.findById(UUID.fromString(fields["caseId"] as String)).get()
		String status = fields["status"] as String

		if (status == 'CLOSED' || status == 'DISCHARGED') {
			if (caseObj.room) {
				Room room = roomRepository.findById(caseObj.room.id).get()
				room.status = 'AVAILABLE'
				room.notes = null
				roomRepository.save(room)
			}
		}

		caseObj.status = status
		return caseRepository.save(caseObj)
	}

	/**
	 * @author Albert Oclarit
	 */

	@GraphQLMutation
	Case patientMayGoHome(@GraphQLArgument(name = "caseId") UUID caseId,
	                      @GraphQLArgument(name = "mayGoHomeDateTime") String mayGoHomeDateTime) {

		Case caseObj = caseRepository.findById(caseId).get()

		if (caseObj.mayGoHomeDatetime) {
			caseObj.mayGoHomeDatetime = null

			List<Department> departmentsToNotify = departmentRepository.departmentsByEvent("maygohome_order_deferred")

			if (departmentsToNotify) {
				notificationService.notifyUsersOfDepartments(departmentsToNotify, "Discharge Notification", "Patient " + caseObj.patient.fullName + " discharge order has been deferred.", "")
			}
		} else {
			if (mayGoHomeDateTime != null) {
				Timestamp mghdt = Timestamp.valueOf(mayGoHomeDateTime)
				caseObj.mayGoHomeDatetime = mghdt.toInstant()
			} else {
				caseObj.mayGoHomeDatetime = Instant.now()
			}

			List<Department> departmentsToNotify = departmentRepository.departmentsByEvent("maygohome_order_executed")

			if (departmentsToNotify) {
				notificationService.notifyUsersOfDepartments(departmentsToNotify, "Discharge Notification", "Patient " + caseObj.patient.fullName + " is now ready for final discharge processing.", "")
			}
		}

		try {

//			List<String> filters = ['MEDICAL RECORDS', 'BILLING', 'ADMITTING']
//			notificationService.notifyGroups(filters, "Discharge Notification", "Patient" + caseObj.patient.fullName + " may GO Home","")
		}
		catch (Exception e) {
			println("Notification Error on patientMayGohome" + e)
		}

		return caseRepository.save(caseObj)
	}

	@GraphQLMutation
	Case updateCaseAndTransfer(
			@GraphQLArgument(name = "caseId") UUID caseId,
			@GraphQLArgument(name = "registryType") String registryType,
			@GraphQLArgument(name = "department") UUID department,
			@GraphQLArgument(name = "room") String room
	) {

		Case caseObj = caseRepository.findById(caseId).get()
		caseObj.registryType = registryType
		caseObj.department = departmentRepository.findById(department).get()

		if (room != "")
			caseObj.room = roomRepository.findById(UUID.fromString(room)).get()

		Transfer transfer = new Transfer()

		transfer.registryType = registryType
		transfer.parentCase = caseRepository.findById(caseId).get()
		transfer.department = departmentRepository.findById(department).get()
		transfer.status = ''
		transfer.entryDateTime = Instant.now()

		if (room != "")
			transfer.room = roomRepository.findById(UUID.fromString(room)).get()

		transferRepository.save(transfer)
		return caseRepository.save(caseObj)
	}

	@GraphQLMutation
	Case changeCaseActiveTransfer(
			@GraphQLArgument(name = "caseId") UUID caseId,
			@GraphQLArgument(name = "transferId") UUID transferId
	) {
		Case caseObj = caseRepository.findById(caseId).get()
		return caseRepository.save(caseObj)
	}

	@GraphQLQuery(name = "getAllCaseByEntryDatetime", description = "Get price")
	MyCensusDTO getAllCaseByEntryDatetime(
			@GraphQLArgument(name = "allcasesbyregistrytype") String allcasesbyregistrytype,
			@GraphQLArgument(name = "from") String from,
			@GraphQLArgument(name = "to") String to
	) {
		List<Case> caseList = caseDao.getAllCaseByEntryDatetime(allcasesbyregistrytype, from, to)
		Integer caseListCount = caseList.size()

		MyCensusDTO mcdto = new MyCensusDTO()

		mcdto.caseList = caseList
		mcdto.caseListCount = caseListCount

		return mcdto
	}

	@GraphQLQuery(name = "getAllAccommodationType", description = "accommodation date and Time")
	List<Case> getAllAccommodationType(
			@GraphQLArgument(name = "accommodation") String accommodation
	) {
		return caseDao.getAllAccommodationType(accommodation)
	}

	@GraphQLQuery(name = "getAllInPatients", description = "Get all in patients")
	List<Case> getAllInPatients() {
		return caseRepository.getAllInPatients()
	}

	@GraphQLQuery(name = "getAllNewborn", description = "Get all the new borns")
	List<Case> getAllNewborn() {
		return caseRepository.getAllNewborn()
	}

	@GraphQLQuery(name = "getAllDischarged", description = "Get all Discharge")
	List<Case> getAllDischarged() {
		return caseRepository.getAllDischarged()
	}

	@GraphQLQuery(name = "case_by_patient_pageable")
	Page<Case> getByPatientPageable(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "pageSize") Integer pageSize
	) {
		return caseRepository.getPatientCasesPageable(id, PageRequest.of(page, pageSize, Sort.Direction.ASC, 'status'))
	}

	@GraphQLQuery(name = "case_by_patient_list_pageable")
	Page<Case> getByPatientListPageable(
			@GraphQLArgument(name = "ids") List<UUID> ids,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "pageSize") Integer pageSize
	) {
		if(ids){
			return caseRepository.getByPatientListPageable(ids, PageRequest.of(page, pageSize, Sort.Direction.ASC, 'status'))
		}
	}

	@GraphQLQuery(name = "fetchActivePatients", description = "Fetch active patients")
	Page<Case> fetchActivePatients(
			@GraphQLArgument(name = "lastName") String lastName,
			@GraphQLArgument(name = "firstName") String firstName,
			@GraphQLArgument(name = "registryType") String registryType,
			@GraphQLArgument(name = "sort") String sort,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name ="pageSize") Integer pageSize
	){
		def sortType = PageRequest.of(page, pageSize, Sort.Direction.ASC, "patient.lastName")

		if (sort == "date_added")
			sortType = PageRequest.of(page, pageSize, Sort.Direction.ASC, "entryDateTime")

		List<String> registry = [registryType.toLowerCase()]

		if (registryType == "all")
			registry = ["ipd", "erd", "opd"]

		return caseRepository.fetchActivePatients(lastName, firstName, registry, sortType)
	}

	@GraphQLMutation(name = "notifyAdmission", description = "save diet on case")
	Case notifyAdmission(
			@GraphQLArgument(name = "id") UUID id
	) {
		Case patientCase = caseRepository.findById(id).get()

		List<Department> departmentsToNotify = departmentRepository.departmentsByEvent("admission_order_executed")

		if (departmentsToNotify) {
			notificationService.notifyUsersOfDepartments(departmentsToNotify, "Admission Order", "Patient " + patientCase.patient.fullName + " has been ordered to be admitted as In-Patient.", "")
		}

		return caseRepository.save(patientCase)
	}

	@GraphQLMutation(name = "saveDiet", description = "save diet on case")
	Case saveDiet(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "dietId") UUID dietId,
			@GraphQLArgument(name = "status") String status,
			@GraphQLArgument(name = "dateAdded") Instant dateAdded
	) {
		Case patientCase = caseRepository.findById(id).get()
		Diet diet = dietRepository.findById(dietId).get()

		DietLog log = new DietLog()
		def username = SecurityUtils.currentLogin()
		def user = userRepository.findOneByLogin(username)
		def emp = employeeRepository.findOneByUser(user)
		log.aCase = patientCase
		log.employee = emp
		log.reason = status
		if (dateAdded) {
			log.dateAdded = dateAdded
		} else {
			log.dateAdded = LocalDateTime.now().toInstant(ZoneOffset.UTC)
		}
		//if(patientCase?.diet?.id != diet.id){
		log.diet = patientCase.diet
		//}

		patientCase.diet = diet

		dietLogRepository.save(log)

		if (StringUtils.equalsIgnoreCase(status, 'CARRIED')) {
			notificationService.notifyUsersOfDepartment(emp.department.id, "Diet Order", "Diet order has been carried.", "")
		}

		List<Department> departmentsToNotify = departmentRepository.departmentsByEvent("dietary_order_executed")

		if (departmentsToNotify) {
			notificationService.notifyUsersOfDepartments(departmentsToNotify, "Diet Order", "Diet order has been carried.", "")
		}

		return caseRepository.save(patientCase)
	}

	@GraphQLMutation(name = "saveDietv2", description = "save diet on case")
	Case saveDietv2(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "dietId") UUID dietId,
			@GraphQLArgument(name = "status") String status,
			@GraphQLArgument(name = "dateAdded") Instant dateAdded,
            @GraphQLArgument(name = "additionalInstructions") String additionalInstructions
	) {
		Case patientCase = caseRepository.findById(id).get()
		Diet diet = dietRepository.findById(dietId).get()

		DietLog log = new DietLog()
		def username = SecurityUtils.currentLogin()
		def user = userRepository.findOneByLogin(username)
		def emp = employeeRepository.findOneByUser(user)
		log.aCase = patientCase
		log.employee = emp
		log.reason = status
        log.additional_instructions = additionalInstructions
		if (dateAdded) {
			log.dateAdded = dateAdded
		} else {
			log.dateAdded = LocalDateTime.now().toInstant(ZoneOffset.UTC)
		}
		//if(patientCase?.diet?.id != diet.id){
		log.diet = patientCase.diet
		//}

		patientCase.diet = diet

		dietLogRepository.save(log)

		if (StringUtils.equalsIgnoreCase(status, 'CARRIED')) {
			notificationService.notifyUsersOfDepartment(emp.department.id, "Diet Order", "Diet order has been carried.", "")
		}

		List<Department> departmentsToNotify = departmentRepository.departmentsByEvent("dietary_order_executed")

		if (departmentsToNotify) {
			notificationService.notifyUsersOfDepartments(departmentsToNotify, "Diet Order", "Diet order has been carried.", "")
		}

		return caseRepository.save(patientCase)
	}

	@GraphQLMutation(name = "generateCourseInTheWard")
	String generateCourseInTheWard(@GraphQLArgument(name = "caseId") UUID caseId) {
		def dOrders = doctorOrderRepository.getDoctorOrdersByCase(caseId).sort { it.entryDateTime }
		List<String> courseInTheWard = new ArrayList<>()

		if (dOrders) {
			dOrders.each {
				def dOItems = doctorOrderProgressNoteRepository.getDoctorOrderProgressNotesByDoctorOrder(it.id).sort {
					it.entryDateTime
				}
				if (dOItems) {
					dOItems.each {
						courseInTheWard.add(it.note)
					}
				}
			}
		}

		return courseInTheWard.join(',\r\n')
	}


    @GraphQLMutation(name = "saveObgynHistory")
    ObgynHistory saveObgynHistory(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields,
            @GraphQLArgument(name = "caseId") UUID caseId
    ) {
        Case aCase = null
        caseRepository.findById(caseId).ifPresent { aCase = it }
        if (!caseId || !aCase) throw new Exception("Case ID not found or case is not found.")
        if (id) {
            ObgynHistory obgynHistory = null
            obgynHistoryRepository.findById(id).ifPresent { obgynHistory = it }
            if (!obgynHistory) throw new Exception("No Obgyn History found")
            obgynHistory = objectMapper.updateValue(obgynHistory, fields)
            obgynHistory.aCase = aCase
            return obgynHistoryRepository.save(obgynHistory)
        } else {
            ObgynHistory obgynHistory = objectMapper.convertValue(fields, ObgynHistory.class)
            obgynHistory.aCase = aCase
            return obgynHistoryRepository.save(obgynHistory)
        }
    }

	@GraphQLMutation
	Case updateCase(
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		Case caseObj = caseRepository.findById(UUID.fromString(fields.get("id").toString())).get()
		return caseRepository.save(objectMapper.updateValue(caseObj, fields))
	}

	@GraphQLMutation
	void toggleLockCase(
			@GraphQLArgument(name = "id") String id
	) {
		Case caseObj = caseRepository.findById(UUID.fromString(id)).get()
		caseObj.locked = !caseObj.locked
		caseRepository.save(caseObj)
		return
	}

	@GraphQLQuery(name = "getPatientCensus")
	List<DepartmentCensusDto> getPatientCensus(@GraphQLArgument(name = "filter") String filter, @GraphQLArgument(name = "year") Integer year){
		return caseDao.getCensus(filter, year)
	}

	@GraphQLMutation
	Case updateWithInfection(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "userId") UUID userId,
			@GraphQLArgument(name = "infectionType") String infectionType,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		if(userId) {
			Employee pUser = null
			employeeRepository.findById(userId).ifPresent { pUser = it }

			if (!pUser) throw new RuntimeException("No User found.")

			if (id) {
				Case caseObj = caseRepository.findById(id).get()
				if (infectionType == 'vap') {
					caseObj.vapInfectionBy = fields.get('vapInfection') ? pUser : null
				}
				if (infectionType == 'bsi') {
					caseObj.bsiInfectionBy = fields.get('bsiInfection') ? pUser : null
				}
				if (infectionType == 'uti') {
					caseObj.utiInfectionBy = fields.get('utiInfection') ? pUser : null
				}
				return caseRepository.save(objectMapper.updateValue(caseObj, fields))

			}
		}
	}

}
