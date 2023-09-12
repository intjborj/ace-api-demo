package com.hisd3.hismk2.rest

import com.google.gson.Gson
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.ancillary.*
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.Patient
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.ancillary.*
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.repository.pms.DoctorOrderItemRepository
import com.hisd3.hismk2.repository.pms.PatientRepository
import com.hisd3.hismk2.rest.dto.*
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import com.hisd3.hismk2.services.NotificationService
import groovy.transform.TypeChecked
import org.apache.commons.lang3.StringUtils
import org.apache.http.HttpEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.params.HttpProtocolParams
import org.apache.http.util.EntityUtils
import org.apache.tika.Tika
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*

@TypeChecked
@RestController
@Service
class MessageBuilderResource {

	@Autowired
	PatientRepository patientRepository

	@Autowired
	CaseRepository caseRepository

	@Autowired
	PanelContentRepository panelContentRepository

	@Autowired
	GeneratorService generatorService

	@Autowired
	OrderSlipItemRepository orderSlipItemRepository

	@Autowired
	DepartmentRepository departmentRepository

	@Autowired
	EmployeeRepository employeeRepository

	@Autowired
	DicNumberRepository dicNumberRepository

	@Autowired
	IntegrationConfigRepository integrationConfigRepository

	@Autowired
	ServiceRepository serviceRepository

	@Autowired
	OrderslipResource orderslipResource

	@Autowired
	DiagnosticsResultRepository diagnosticsResultRepository

	@Autowired
	NotificationService notificationService

	@Autowired
	DoctorOrderItemRepository doctorOrderItemRepository

	@RequestMapping(method = [RequestMethod.POST], value = ["/api/messageBuilder/"])
	ResponseEntity<String> buildJsonfMessage(
			@RequestParam("params") String params
	) {
		Gson gson = new Gson()
		OslipDto data = gson.fromJson(params, OslipDto.class)

		Department employeeDepartment = departmentRepository.findById(UUID.fromString(data.deptId)).get()

		List<OrderSlipItem> orderSlipItems = []
		for (OSlipItems item : (data.itemList as List<OSlipItems>)) {
			OrderSlipItem osi = orderSlipItemRepository.findById(UUID.fromString(item.id)).get()
			orderSlipItems.add(osi)
		}
		Case patientCase = caseRepository.findById(UUID.fromString(data.caseId)).get()

		OrmDto buildMsg = buildMessage(employeeDepartment, patientCase, orderSlipItems, '')

		String res = sendToMiddleWare(buildMsg)

		//println(res)
		return new ResponseEntity(res, HttpStatus.OK)
	}

	OrmDto buildMessage(Department emp_department, Case patientCase, List<OrderSlipItem> ordersItems, String device) {

		String accessionNumber = setAccession(ordersItems)

		Extras extras = new Extras()
		extras.integratedFacilities = emp_department.ancillaryConfig.entityName
		extras.ipAddress = emp_department.ancillaryConfig.ipAddress
		extras.tcp = emp_department.ancillaryConfig.tcp
		extras.port = emp_department.ancillaryConfig.port
		extras.smbUrl = emp_department.ancillaryConfig.smbPath
		extras.userLogin = emp_department.ancillaryConfig.username
		extras.passLogin = emp_department.ancillaryConfig.password

		OrmDto newMsg = new OrmDto()

		MsgHeader header = new MsgHeader()
		header.messageType = device == 'ABBOTT'? "OML_O33" : "ORM_O01"
		header.receivingApplication = device == 'ABBOTT'? 'ABBOTT' : emp_department.ancillaryConfig?.entityName
		header.sendingApplication = "ACEMC"
		header.messageControlId = "MSG" + generatorService?.getNextValue(GeneratorType.HL7_CONTROL_ID, { i -> StringUtils.leftPad(i.toString(), 5, "0") })
		header.sendingFacility = "ACEMCB"

		PatientDTO patient = new PatientDTO()
		patient.firstName = patientCase.patient.firstName
		patient.lastName = patientCase.patient.lastName
		patient.middleName = patientCase.patient.middleName
		patient.patientId = patientCase.patient.patientNo
		patient.patientNo = patientCase.patient.patientNo
		patient.extensionName = patientCase.patient.nameSuffix ?: ""
		patient.alternateId = getAlternateID(emp_department, patientCase.patient)
		patient.birthDate = patientCase.patient.dob
		if (patientCase.patient.gender == "MALE") {
			patient.gender = "M"
		} else {
			patient.gender = "F"
		}
		patient.fullName = patient.lastName + "," + patient.firstName + " " + patient.middleName + " " + patient.extensionName

		AddressDTO address = new AddressDTO()
		address.addressLine = patientCase.patient.address ?: ""
		address.city = patientCase.patient.cityMunicipality ?: ""
		address.province = patientCase.patient.stateProvince ?: ""
		address.country = patientCase.patient.country ?: ""

		PatientVisit visits = new PatientVisit()
		visits.admissionDateTime = patientCase.admissionDatetime
		visits.admissionType = ""
		if (patientCase.room) {
			visits.room = patientCase.room.roomNo
		} else {
			visits.room = "OPD"
		}
		visits.assignedLocation = patientCase.department.departmentName
		visits.patientClass = regType(patientCase.registryType)
		visits.visitNumber = patientCase.caseNo

		if(patientCase.attendingPhysician){
			visits.referringPhysicianId = patientCase.attendingPhysician.prcLicenseNo ?: ""
			visits.referringPhysicianName = patientCase.attendingPhysician.firstName?: ""
		}else{
			Employee emp
			if(ordersItems[0].orderslip.requestingPhysician)
				emp = employeeRepository.findById(ordersItems[0].orderslip.requestingPhysician).get()
			visits.referringPhysicianId = emp?.prcLicenseNo?:""
			visits.referringPhysicianName = ordersItems[0].orderslip.requestingPhysicianName?:""
		}

		OrderDTO order = new OrderDTO()
		order.orderDateTime = ordersItems[0].orderslip.createdDate ?: ""
		order.observationDate = ordersItems[0].orderslip.createdDate ?: ""
		order.modalityType = setModality(ordersItems)
		order.fileOrderNumber = accessionNumber
		order.placeOrderNumber = accessionNumber
		order.orderControl = accessionNumber
		order.enteringOrganization = "ACE MC BOHOL"
		order.priority = patientCase.registryType == "ERD" ? "STAT" : "ROUTINE"
		def odr = []
		for (OrderSlipItem osItem : ordersItems) {

			if (osItem.service.serviceType == ServiceTypes.PANEL) {
				List<PanelContent> contentList = panelContentRepository.findAllbyParentId(osItem.service.id)

				if (contentList.size() > 0) {
					contentList.each {
						OrderItem orderItem = new OrderItem()
						orderItem.identifier = it.service.processCode
						orderItem.serviceName = it.service.serviceName
						odr.add(orderItem)
					}
				}

			} else {
				OrderItem orderItem2 = new OrderItem()
				orderItem2.identifier = osItem.service.processCode ? osItem.service.processCode : osItem.service.serviceCode
				orderItem2.serviceName = osItem.service.serviceName
				odr.add(orderItem2)
			}

		}
		order.itemList = odr as ArrayList<OrderItem>
		newMsg.header = header
		newMsg.patient = patient
		newMsg.order = order
		newMsg.address = address
		newMsg.visits = visits
		newMsg.extras = extras

		return newMsg
	}

	String setModality(List<OrderSlipItem> oSlipItems) {
		def list = oSlipItems
		String m = ""

		if (list.size() == 1) {
			def osl = oSlipItems[0]
			def five = osl.service.processCode ? osl.service.processCode.take(5) : osl.service.serviceCode.take(5)
			switch (five) {
				case "DICGR": m = "DX"
					break
				case "DICMR": m = "MR"
					break
				case "DICUT": m = "US"
					break
				case "DICCT": m = "CT"
					break
				case "DICMM": m = "MG"
					break
				case "DICRF": m = "RF"
					break
				default: m = ""
			}
		}
		return m
	}

	String setAccession(List<OrderSlipItem> oSlipItems) {
		def accession
		def list = oSlipItems

		if (list.size() > 1) {
			if (list[0].accession == null) {
				accession = "BCH" + generatorService?.getNextValue(GeneratorType.DIAGNOSTICS, { i -> StringUtils.leftPad(i.toString(), 5, "0") })
				list.each {
					it.accession = accession
					orderSlipItemRepository.save(it)
				}
			} else {
				accession = list[0].accession
			}

		} else {
			def osl = oSlipItems[0]
			if (osl.accession == null) {
				def five = osl.service.processCode ? osl.service.processCode.take(5) : osl.service.serviceCode.take(5)
				switch (five) {
					case "DICGR": accession = "GRV2" + generatorService?.getNextValue(GeneratorType.GR, { i -> StringUtils.leftPad(i.toString(), 5, "0") })
						break
					case "DICMR": accession = "MRV2" + generatorService?.getNextValue(GeneratorType.MR, { i -> StringUtils.leftPad(i.toString(), 5, "0") })
						break
					case "DICUT": accession = "UTV2" + generatorService?.getNextValue(GeneratorType.UTZ, { i -> StringUtils.leftPad(i.toString(), 5, "0") })
						break
					case "DICCT": accession = "CTV2" + generatorService?.getNextValue(GeneratorType.CT, { i -> StringUtils.leftPad(i.toString(), 5, "0") })
						break
					case "DICMM": accession = "MMV2" + generatorService?.getNextValue(GeneratorType.MM, { i -> StringUtils.leftPad(i.toString(), 5, "0") })
						break
					case "DICRF": accession = "RFV2" + generatorService?.getNextValue(GeneratorType.RF, { i -> StringUtils.leftPad(i.toString(), 5, "0") })
						break
					default: accession = generatorService?.getNextValue(GeneratorType.DIAGNOSTICS, { i -> StringUtils.leftPad(i.toString(), 5, "0") })
				}
				osl.accession = accession
				orderSlipItemRepository.save(osl)

			} else {
				accession = osl.accession
			}
		}
		return accession
	}

	String regType(String type) {
		switch (type) {
			case 'ERD': return 'E'
				break
			case 'IPD': return 'I'
				break
			case 'OPD': return 'O'
				break
			default:
				return 'O'
				break
		}
	}

	String sendToMiddleWare(OrmDto msg) {

		Gson gson = new Gson()

		String jsonInString = gson.toJson(msg)

		StringEntity entity = new StringEntity(jsonInString, "UTF-8")

		CloseableHttpClient httpClient = HttpClients.createDefault()

		IntegrationConfig integrationConfig = integrationConfigRepository.findAll().first()

		def request = new HttpPost(integrationConfig.middlewateIp + "/msgreceiver")

		request.getParams().setParameter(HttpProtocolParams.HTTP_CONTENT_CHARSET, "UTF-8")
		request.entity = entity
		request.setHeader("Accept", "application/json")
		request.setHeader("Accept-Encoding", "UTF-8")
		request.setHeader("Content-type", "application/json")
		try {
			def response = httpClient.execute(request)
			HttpEntity responseEntity = response.entity

			def responseString = EntityUtils.toString(responseEntity, "UTF-8")

			if (response.statusLine.statusCode == 500) {
				throw new Exception()
				httpClient.close()
			}

			httpClient.close()
			return responseString
		}
		catch (e) {
			throw e
		}
	}

	String getAlternateID(Department emp_department, Patient patient) {

		List<DicNumber> dic = dicNumberRepository.getAltenaneNumber(emp_department.id, patient.id)
		if (dic.size() > 0) {
			return dic[0].dic_no
		} else {
			def prefix = emp_department.idPrefix ? emp_department.idPrefix : "ALT"
			String alternate = prefix + generatorService?.getNextValue(prefix as GeneratorType, { i -> StringUtils.leftPad(i.toString(), 5, "0") })
			DicNumber newAlternate = new DicNumber()
			newAlternate.dic_no = alternate
			newAlternate.patient = patient
			newAlternate.department = emp_department
			dicNumberRepository.save(newAlternate)
			return alternate
		}
	}

	@RequestMapping(method = [RequestMethod.POST], value = "/api/result/receiver")
	ResponseEntity<String> receiver(@RequestBody String payload) {
		HttpHeaders responseHeaders = new HttpHeaders()

		Gson gson = new Gson()
		Msgformat data = gson.fromJson(payload, Msgformat.class)

		byte[] attachment
		if (StringUtils.isNotEmpty(data.attachment)) {
			attachment = Base64.getDecoder().decode(data.attachment)
		}
		String sender = data.senderIp

		//def empDoc =  employeeRepository?.findByEmployeeId(data.docEmpId) }
		List<Patient> patients = patientRepository.getPatientByPatientNo(data.pId)
		Patient patient = patientRepository.getPatientByPatientNo(data.pId).first()

		com.hisd3.hismk2.domain.ancillary.Service service
		try {
			service = serviceRepository.serviceByProcessCode(data.processCode).first()
		}catch(e){
			saveResult(data,patient)
			return new ResponseEntity(responseHeaders, HttpStatus.OK)
		}

		List<OrderSlipItem> listItems = orderSlipItemRepository.findByAccession(data.bacthnum)
		DiagnosticResult result = new DiagnosticResult()

		if (listItems.size() > 0) {
			listItems.each {
				if (service.processCode == it.service.processCode) {
						result.orderSlipItem = it
						result.service = service
						result.patient = patient
						result.data = data.jsonList
						result.accession = data.bacthnum
						result.name = data.processCode
						result.parsed = data.msgXML
						if (attachment) {
							try {
								/*** ready for NAS***/
								String origin = patient.patientNo + "-" + service.serviceCode
								def mime = new Tika().detect(attachment)
								String idfname = StringUtils.trim(origin) + ".pdf"
								result.file_name = origin + ".pdf"
								result.url_path = orderslipResource.resultWitterOnSmb(it, attachment, idfname)
								result.mimetype = mime.toString()
							} catch (Exception e) {
								e.printStackTrace()
								throw e
							}
						}

						diagnosticsResultRepository.save(result)
				}else{
					println("No Match")
					saveResult(data,patient)
					return new ResponseEntity(responseHeaders, HttpStatus.OK)
				}

				it.status = "COMPLETED"
				orderSlipItemRepository.save(it)
				if (it.doctors_order_item) {
					def docItem = doctorOrderItemRepository.findById(it.doctors_order_item.id).get()
					if (docItem) {
						docItem.status = 'COMPLETED'
						doctorOrderItemRepository.save(docItem)
					}
				}

				try {
					def deptid
					if (it.service.department.parentDepartment.id) {
						deptid = it.service.department.parentDepartment.id
					} else {
						deptid = it.service.department.id
					}
					notificationService.notifyUsersOfDepartment(deptid, "Result released", "(" + it.service.category + ") " + it.service.serviceName, "")
				}
				catch (Exception e) {
					println(e)
				}
			}
		} else {
			saveResult(data , patient)
		}

		return new ResponseEntity(responseHeaders, HttpStatus.OK)

	}

	def saveResult(Msgformat data , Patient patient){

		DiagnosticResult result = new DiagnosticResult()
		result.parsed = data.msgXML
		result.patient = patient
		result.accession = data.bacthnum
		result.data = data.jsonList
		result.name = data.processCode
		diagnosticsResultRepository.save(result)
	}
}

