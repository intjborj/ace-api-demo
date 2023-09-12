package com.hisd3.hismk2.rest.reports.pms

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hisd3.hismk2.domain.ancillary.OrderSlipItem
import com.hisd3.hismk2.domain.billing.Billing
import com.hisd3.hismk2.domain.billing.BillingItem
import com.hisd3.hismk2.domain.billing.BillingItemType
import com.hisd3.hismk2.domain.pms.*
import com.hisd3.hismk2.graphqlservices.ancillary.OrderSlipDto
import com.hisd3.hismk2.graphqlservices.ancillary.OrderslipService
import com.hisd3.hismk2.graphqlservices.billing.BillingItemServices
import com.hisd3.hismk2.graphqlservices.billing.BillingService
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.pms.PatientCaseViewService
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.ancillary.OrderSlipItemRepository
import com.hisd3.hismk2.repository.ancillary.OrderslipRepository
import com.hisd3.hismk2.repository.hospital_config.ConstantRepository
import com.hisd3.hismk2.repository.hospital_config.HospitalInfoRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.inventory.ItemRepository
import com.hisd3.hismk2.repository.pms.*
import com.hisd3.hismk2.rest.dto.*
import com.hisd3.hismk2.security.SecurityUtils
import groovy.json.JsonSlurper
import net.sf.jasperreports.engine.JRException
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import net.sf.jasperreports.engine.data.JsonDataSource
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.export.SimpleExporterInput
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput
import net.sf.jasperreports.export.SimplePdfExporterConfiguration
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.apache.coyote.Response
import org.apache.groovy.json.internal.LazyMap
import org.apache.http.entity.ContentType
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.xmlbeans.impl.xb.xsdschema.AnyDocument
import org.exolab.castor.types.DateTime
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.ResourceUtils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.xmlsoap.schemas.soap.encoding.Date

import javax.imageio.ImageIO
import javax.swing.text.DateFormatter
import javax.xml.bind.DatatypeConverter
import java.awt.image.BufferedImage
import java.lang.reflect.Type
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.concurrent.Callable

class AllergyMap {
    String name
    List<String> content
}

@RestController
@RequestMapping(value = ['/reports/print'])
class PatientReportResource {

    @Autowired
    ApplicationContext applicationContext

    @Autowired
    HospitalConfigService hospitalConfigService

    @Autowired
    CaseRepository caseRepository

    @Autowired
    HospitalInfoRepository hospitalInfoRepository

    @Autowired
    DoctorOrderRepository doctorOrderRepository

    @Autowired
    DoctorOrderItemRepository doctorOrderItemRepository

    @Autowired
    EmployeeRepository employeeRepository

    @Autowired
    ConstantRepository constantRepository

    @Autowired
    BillingService billingService

    @Autowired
    ItemRepository itemRepository

    @Autowired
    DoctorOrderProgressNoteRepository doctorOrderProgressNoteRepository

    @Autowired
    IntakeRepository intakeRepository

    @Autowired
    OutputRepository outputRepository


    @Autowired
    OrderslipService orderslipService

    @Autowired
    NurseNoteRepository nurseNoteRepository

    @Autowired
    VitalSignRepository vitalSignRepository

    @Autowired
    OrderslipRepository orderslipRepository

    @Autowired
    PatientCaseViewService patientCaseViewService

    @Autowired
    OrderSlipItemRepository orderSlipItemRepository

    @Autowired
    BillingItemServices billingItemServices

    @Autowired
    UserRepository userRepository

    @Autowired
    AdministrationRepository administrationRepository

    @Autowired
    JdbcTemplate jdbcTemplate

    @Autowired
    MedicationRepository	medicationRepository

    @Autowired
    DoctorNotesRepository doctorNotesRepository

    @Autowired
    CathLabNoteRepository cathLabNoteRepository

    @RequestMapping(value = ['/pdsc_report'], produces = ['application/pdf'])
    ResponseEntity<byte[]> printPdscReport(@RequestParam('caseId') UUID caseId) {
        Case patientCase = caseRepository.findById(caseId).get()
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())

        DateTimeFormatter dobFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy")

        DateTimeFormatter formatterWithTime =
                DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a").withZone(ZoneId.systemDefault())
        def res = applicationContext.getResource("classpath:/reports/pms/patient_data_sheet_report.jasper")
        def logo = applicationContext.getResource("classpath:/reports/logo.png")
        Resource chkchecked = applicationContext.getResource("classpath:/reports/check.png")
        Resource chkunchecked = applicationContext.getResource("classpath:/reports/uncheck.png")
        def dto = new PatientDataSheetDto()
        def parameters = [:] as Map<String, Object>
        if (patientCase) {
            dto.patientFullname = patientCase?.patient?.fullName
            dto.patientPin = patientCase?.patient?.patientNo
            dto.patientRoomNo = patientCase?.room?.roomNo
            dto.patientFullAddress = patientCase?.patient?.fullAddress
            dto.patientContactNo = patientCase?.patient?.contactNo
            dto.patientCitizenship = patientCase?.patient?.citizenship
            dto.patientCivilStatus = patientCase?.patient?.civilStatus
            dto.patientBirthdate = dobFormat.format(patientCase.patient.dob)

            Period age = Period.between(patientCase.patient.dob, LocalDate.now())

            if (age.getYears() < 1) {
                if (age.getMonths() < 1) {
                    dto.patientAge = age.getDays() + " d.o"
                } else {
                    dto.patientAge = age.getMonths() + " m.o"
                }
            } else {
                dto.patientAge = age.getYears() + " y.o"

            }
            dto.patientSex = patientCase?.patient?.gender
            dto.patientNameOfSpouse = patientCase?.patient?.nameOfSpouse
            dto.patientOccupation = patientCase?.occupation
            dto.patientCompany = patientCase?.companyName
            dto.patientCompanyAddress = patientCase?.companyAddress
            dto.patientCompanyContactNo = patientCase?.companyContact

            dto.isPrevAdmissionAceMcBohol = StringUtils.equalsIgnoreCase(patientCase?.previousAdmission, "ACEMC BOHOL")
            dto.isPrevAdmissionOther = StringUtils.equalsIgnoreCase(patientCase?.previousAdmission, "OTHER/TRANSFEREE")
            dto.isPrevAdmissionNone = StringUtils.equalsIgnoreCase(patientCase?.previousAdmission, "NONE")

            dto.isHmo = false
            dto.isPhicMember = false
            dto.isPhicDependent = false
            dto.isPhicNone = false

            dto.patientReligion = patientCase?.patient?.religion
            dto.patientBirthPlace = patientCase?.patient?.pob
            dto.inCaseOfEmergencyName = patientCase?.emergencyContactName
            dto.inCaseOfEmergencyAddress = patientCase?.emergencyContactAddress
            dto.inCaseOfEmergencyContact = patientCase?.emergencyContact
            dto.inCaseOfEmergencyRelation = patientCase?.emergencyContactRelation
            dto.informantName = patientCase?.informant
            dto.informantAddress = patientCase?.informantAddress
            dto.informantContact = patientCase?.informantContact
            dto.informantRelation = patientCase?.informantRelation
            dto.responsibleHospitalBillName = patientCase?.guarantorName
            dto.responsibleHospitalBillAddress = patientCase?.guarantorAddress
            dto.responsibleHospitalBillContactNo = patientCase?.guarantorContact
            dto.responsibleHospitalBillRelation = patientCase?.guarantorRelation
            if (patientCase?.admissionDatetime) {
                dto.addmissionDate = formatterWithTime.format(patientCase?.admissionDatetime)
            }
            dto.admittingPhysician = patientCase?.admittingPhysician?.fullName
            if (patientCase?.dischargedDatetime) {
                dto.dischargeDate = formatterWithTime.format(patientCase?.dischargedDatetime)
            }
            dto.howTakenToRoom = patientCase?.howTakenToRoom

            dto.dischargeDispositionDischarged = StringUtils.equalsIgnoreCase(patientCase?.dischargeDisposition, "discharged")
            dto.dischargeDispositionAbsconded = StringUtils.equalsIgnoreCase(patientCase?.dischargeDisposition, "ABSCONDED")
            dto.dischargeDispositionDama = StringUtils.equalsIgnoreCase(patientCase?.dischargeDisposition, "DAMA/HAMA")
            dto.dischargeDispositionTransfered = StringUtils.equalsIgnoreCase(patientCase?.dischargeDisposition, "TRANSFERRED")
            dto.dischargeDispositionAutopsied = StringUtils.equalsIgnoreCase(patientCase?.dischargeDisposition, "AUTOPSIED")

            dto.dischargeConditionExpired = StringUtils.equalsIgnoreCase(patientCase?.dischargeCondition, "EXPIRED")
            dto.dischargeConditionImproved = StringUtils.equalsIgnoreCase(patientCase?.dischargeCondition, "IMPROVED")
            dto.dischargeConditionRecovered = StringUtils.equalsIgnoreCase(patientCase?.dischargeCondition, "RECOVERED")
            dto.dischargeConditionUnimproved = StringUtils.equalsIgnoreCase(patientCase?.dischargeCondition, "UNIMPROVED")

            dto.isServiceSurgery = patientCase?.serviceCode == 6 || patientCase?.serviceCode == 5
            dto.isServiceInternalMedicine = patientCase?.serviceCode == 1 || patientCase?.serviceCode == 7
            dto.isServiceObGyn = patientCase?.serviceCode == 2 || patientCase?.serviceCode == 3
            dto.isServicePediatric = patientCase?.serviceCode == 4 || patientCase?.serviceCode == 9 || patientCase?.serviceCode == 8

            dto.admittingDiagnosis = patientCase?.admittingDiagnosis
            dto.primaryPhysician = patientCase?.attendingPhysician?.fullName
            if (patientCase?.primaryDx) {
                JSONObject primaryDxJson = new JSONObject(patientCase?.primaryDx)
                if (primaryDxJson.has('rvsCode')) {
                    dto.operationProcedure = primaryDxJson.get('longName')
                    dto.operationProcedureIcd = primaryDxJson.get('rvsCode')
                } else if (primaryDxJson.has('diagnosisCode')) {
                    dto.finalDiagnosis = primaryDxJson.get('longName')
                    dto.finalDiagnosisIcd = primaryDxJson.get('diagnosisCode')
                }
            }

            if (patientCase?.admittingOfficer) {
                dto.admittingOfficer = patientCase?.admittingOfficer?.fullName
            }

            if (patientCase?.secondaryDx) {
                JSONObject secondaryDxJson = new JSONObject(patientCase?.secondaryDx)
                if (secondaryDxJson.has('rvsCode')) {
                    dto.operationProcedure = secondaryDxJson.get('longName')
                    dto.operationProcedureIcd = secondaryDxJson.get('rvsCode')
                } else if (secondaryDxJson.has('diagnosisCode')) {
                    dto.finalDiagnosis = secondaryDxJson.get('longName')
                    dto.finalDiagnosisIcd = secondaryDxJson.get('diagnosisCode')
                }
            }

            if (patientCase?.patient?.philHealthId && StringUtils.equalsIgnoreCase(patientCase?.accommodationType, 'NHIP/MEMBER')) {
                dto.isPhicMember = true
            }

            if (patientCase?.patient?.philHealthId && StringUtils.equalsIgnoreCase(patientCase?.accommodationType, 'NHIP/DEPENDENT')) {
                dto.isPhicDependent = true
            }

            if (patientCase?.patient?.philHealthId) {
                dto.philHealthId = patientCase?.patient?.philHealthId
            }

            dto.fatherName = patientCase?.patient?.father
            dto.fatherOccupation = patientCase?.patient?.fatherOccupation

            dto.motherName = patientCase?.patient?.mother
            dto.motherOccupation = patientCase?.patient?.motherOccupation
            dto.allergies = ""

            //patientCase?.patient?.allergies ? patientCase?.patient?.allergies : 'None';

        }

        if (patientCase?.hmoCompany) {
            dto.hmoName = patientCase?.hmoCompany?.companyName
            dto.isHmo = true
        }

        def hospitalInfo = hospitalConfigService.hospitalInfo

        if (!res.exists()) {
            return ResponseEntity.notFound().build()
        }

        parameters.put("logo", logo.URL)
        def fulladdress = (hospitalInfo?.address ?: "") + " " +
                (hospitalInfo?.addressLine2 ?: "") + "\n" +
                (hospitalInfo?.city ?: "") + " " +

                (hospitalInfo?.zip ?: "") + " " +
                (hospitalInfo?.country ?: "")

        parameters.put("hospitalfulladdress", fulladdress)
        parameters.put("checked", chkchecked.URL)
        parameters.put("unchecked", chkunchecked.URL)

        def os = new ByteArrayOutputStream()

        /*

        Gson gson = new Gson()
        // Conveting to ByteArray and not setting charsets will cause problems based on OS
        ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(gson.toJson(dto).bytes)
        */

        List<PatientDataSheetDto> ds = []
        ds << dto

        if(hospitalConfigService?.hospitalInfo){
            parameters.put('hospitalName', hospitalConfigService?.hospitalInfo?.hospitalName?:"")
            String address = ""

            address +=  hospitalConfigService?.hospitalInfo?.address
            address +=  hospitalConfigService?.hospitalInfo?.addressLine2

            parameters.put('hospitalAddress', address)
        }

        try {
            def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, new JRBeanCollectionDataSource(ds))

            def pdfExporter = new JRPdfExporter()

            def outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(os)

            pdfExporter.setExporterInput(new SimpleExporterInput(jrprint))
            pdfExporter.setExporterOutput(outputStreamExporterOutput)
            def configuration = new SimplePdfExporterConfiguration()
            pdfExporter.setConfiguration(configuration)
            pdfExporter.exportReport()

        } catch (JRException e) {
            e.printStackTrace()
        } catch (IOException e) {
            e.printStackTrace()
        }

        def data = os.toByteArray()
        def params = new LinkedMultiValueMap<String, String>()
        params.add("Content-Disposition", "inline;filename=PDSC-\"" + (patientCase?.patient?.fullName ?: "") + "\".pdf")
        return new ResponseEntity(data, params, HttpStatus.OK)
    }

    @RequestMapping(method = [RequestMethod.GET], value = "/cf4", produces = ["application/pdf"])
    ResponseEntity<byte[]> printcf4(@RequestParam("caseId") UUID caseId) {
        def caseDto = caseRepository.findById(caseId).get()
        def doList = doctorOrderRepository.getDoctorOrdersByCase(caseId)
        def doctorOrderDtoList = new ArrayList<>()
        def hospitalInfo = hospitalInfoRepository.findAll().first()
        def medplans = new ArrayList<MedPlansDto>()
        def gson = new Gson()
        def formatter = DateTimeFormatter.ofPattern("MMddyyyy").withZone(ZoneId.systemDefault())
        def timeFormatter = DateTimeFormatter.ofPattern("hhmm").withZone(ZoneId.systemDefault())
        def periodFormatter = DateTimeFormatter.ofPattern("a").withZone(ZoneId.systemDefault())
        def dto = new PhilhleathFieldsDto()
        def parameters = [:] as Map<String, Object>
        def res = applicationContext?.getResource("classpath:/reports/philhealth/cf4.jasper")
        def logo = applicationContext?.getResource("classpath:/reports/philhealth_logo.png")
        def billing = billingService.getActiveBillingByCase(caseDto)

        Resource chkchecked = applicationContext.getResource("classpath:/reports/check.png")
        Resource chkunchecked = applicationContext.getResource("classpath:/reports/uncheck.png")

        if (!res?.exists()) {
            ResponseEntity.notFound()
        }

        if (logo?.exists()) {
            parameters.put("logo", logo?.getURL())
        }

        if (chkchecked?.exists()) {
            parameters.put("check", chkchecked.getURL())
        }

        if (chkunchecked?.exists()) {
            parameters.put("uncheck", chkunchecked.getURL())
        }

        if (hospitalInfo) {
            dto.hci_name = hospitalInfo.hospitalName
            dto.hci_address = (hospitalInfo?.address ?: "") + " " +
                    (hospitalInfo?.addressLine2 ?: "") + "\n" +
                    (hospitalInfo?.city ?: "") + " " +

                    (hospitalInfo?.zip ?: "") + " " +
                    (hospitalInfo?.country ?: "")
        }

        Period age = Period.between(caseDto.patient.dob, LocalDate.now())

        if (age.getYears() < 1) {
            if (age.getMonths() < 1) {
                dto.patient_age = age.getDays() + "d.o"
            } else {
                dto.patient_age = age.getMonths() + "m.o"
            }
        } else {
            dto.patient_age = age.getYears() + "y.o"

        }

        dto.patient_first = caseDto.patient.firstName
        dto.patient_middle = caseDto.patient.middleName
        dto.patient_last = caseDto.patient.lastName
        if (caseDto.admissionDatetime) {
            dto.date_admitted = formatter.format(caseDto.admissionDatetime)
            dto.time_admitted = timeFormatter.format(caseDto.admissionDatetime)
            dto.time_admitted_aa = periodFormatter.format(caseDto.admissionDatetime).toLowerCase()
        }
        if (caseDto.dischargedDatetime) {
            dto.date_discharge = formatter.format(caseDto.dischargedDatetime)
            dto.time_discharge = timeFormatter.format(caseDto.dischargedDatetime)
            dto.time_admitted_aa = periodFormatter.format(caseDto.dischargedDatetime).toLowerCase()
        }
        dto.patient_gender = caseDto.patient.gender.toLowerCase()
        dto.patient_pin = caseDto.patient.philHealthId
        dto.chief_complaints = caseDto.chiefComplaint
        dto.admitting_diag = caseDto.admittingDiagnosis
        dto.history_presentillness = caseDto.historyPresentIllness

        def physicalExamMap = [:] as Map<String, Object>
        def pertinentDataMap = [:] as Map<String, Object>

        if (caseDto.physicalExamList) {
            Type listType = new TypeToken<ArrayList<JSONObjectDto>>() {}.type

            ArrayList<JSONObjectDto> physicalExams = gson.fromJson(caseDto.physicalExamList, listType)
            physicalExams.eachWithIndex { JSONObjectDto entry, int i ->
                physicalExamMap.put(entry.field, entry.value.toString())
            }
        }

        if (caseDto.pertinentSymptomsList) {
            Type listType = new TypeToken<ArrayList<JSONObjectDto>>() {}.type

            ArrayList<JSONObjectDto> physicalExams = gson.fromJson(caseDto.pertinentSymptomsList, listType)
            physicalExams.eachWithIndex { JSONObjectDto entry, int i ->
                pertinentDataMap.put(entry.field, entry.value.toString())
            }
        }

        if (doList) {
            doList.eachWithIndex { DoctorOrder entry, int i ->
                List<DoctorOrderItem> doItemList = doctorOrderItemRepository.getDoctorOrderItemsByDoctorOrder(entry.id)
                if (doItemList) {
                    doItemList.eachWithIndex { DoctorOrderItem entry2, int i2 ->
                        DoctorOrderDto doDto = new DoctorOrderDto()
                        doDto.order_datetime = formatter.format(entry.entryDateTime)
                        doDto.order = entry2.order
                        doctorOrderDtoList.add(doDto)
                    }
                }
            }
        }

        if (billing) {
            billing.eachWithIndex { Billing entry, int i ->
                entry.billingItemList.eachWithIndex { BillingItem entry2, int i2 ->
                    if (entry2.itemType == BillingItemType.MEDICINES) {
                        def item = itemRepository.findById(UUID.fromString(entry2.item)).get()

                        medplans.add(
                                new MedPlansDto(
                                        generic_name: item.genericName,
                                        quantity: entry2.qty,
                                        total_cost: entry2.subTotal
                                )
                        )
                    }
                }
            }
        }

        dto.dischargeDispositionDischarged = StringUtils.equalsIgnoreCase(caseDto?.dischargeDisposition, "discharged")
        dto.dischargeDispositionAbsconded = StringUtils.equalsIgnoreCase(caseDto?.dischargeDisposition, "ABSCONDED")
        dto.dischargeDispositionDama = StringUtils.equalsIgnoreCase(caseDto?.dischargeDisposition, "DAMA/HAMA")
        dto.dischargeDispositionTransfered = StringUtils.equalsIgnoreCase(caseDto?.dischargeDisposition, "TRANSFERRED")
        dto.dischargeDispositionAutopsied = StringUtils.equalsIgnoreCase(caseDto?.dischargeDisposition, "AUTOPSIED")

        dto.dischargeConditionExpired = StringUtils.equalsIgnoreCase(caseDto?.dischargeCondition, "EXPIRED")
        dto.dischargeConditionImproved = StringUtils.equalsIgnoreCase(caseDto?.dischargeCondition, "IMPROVED")
        dto.dischargeConditionRecovered = StringUtils.equalsIgnoreCase(caseDto?.dischargeCondition, "RECOVERED")
        dto.dischargeConditionUnimproved = StringUtils.equalsIgnoreCase(caseDto?.dischargeCondition, "UNIMPROVED")

        parameters.put("pe", physicalExamMap)
        parameters.put("pertinentData", pertinentDataMap)
        parameters.put("medications", new JRBeanCollectionDataSource(medplans))
        parameters.put("nursenotes", new JRBeanCollectionDataSource(doctorOrderDtoList))
        ByteArrayInputStream bytearray = new ByteArrayInputStream()
        ByteArrayOutputStream os = new ByteArrayOutputStream()
        ByteArrayInputStream dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
        JsonDataSource dataSource = new JsonDataSource(dataSourceByteArray)

        try {
            def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, dataSource)

            def pdfExporter = new JRPdfExporter()

            def outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(os)

            pdfExporter.setExporterInput(new SimpleExporterInput(jrprint))
            pdfExporter.setExporterOutput(outputStreamExporterOutput)
            def configuration = new SimplePdfExporterConfiguration()
            pdfExporter.setConfiguration(configuration)
            pdfExporter.exportReport()

        } catch (JRException e) {
            e.printStackTrace()
        } catch (IOException e) {
            e.printStackTrace()
        }

        if (bytearray != null)
            IOUtils.closeQuietly(bytearray)

        def data = os.toByteArray()
        def params = new LinkedMultiValueMap<String, String>()
        params.add("Content-Disposition", "inline;filename=CF4-\"" + caseDto?.patient?.fullName + "\".pdf")
        return new ResponseEntity(data, params, HttpStatus.OK)

    }

    @RequestMapping(value = ['/doctors-instruction'], produces = ['application/pdf'])
    ResponseEntity<byte[]> printDoctorsInstruction(@RequestParam("caseId") UUID caseId) {

        def caseDto = caseRepository.findById(caseId).get()
        def dto = new DischargeInstructionDto()
        def res = applicationContext?.getResource("classpath:/reports/pms/discharge_instruction.jasper")
        def logo = applicationContext?.getResource("classpath:/reports/logo.png")
        def parameters = [:] as Map<String, Object>
        def gson = new Gson()
        def chkchecked = applicationContext.getResource("classpath:/reports/check.png")
        def chkunchecked = applicationContext.getResource("classpath:/reports/uncheck.png")
        def formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
        def dobFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        def age = Period.between(caseDto.patient.dob, LocalDate.now()).years
        def emp = employeeRepository.findByUsername(SecurityUtils.currentLogin())

        dto.patientFullName = caseDto?.patient?.fullName
        dto.address = caseDto?.patient?.fullAddress
        dto.date = formatter.format(Instant.now())
        dto.pin = caseDto?.patient?.patientNo
        dto.caseNo = caseDto?.caseNo
        dto.roomNo = caseDto?.room?.roomNo
        dto.age = age
        dto.gender = caseDto?.patient?.gender
        dto.civilStatus = caseDto?.patient?.civilStatus
        dto.dob = dobFormat.format(caseDto?.patient?.dob)
        dto.attendingPhysician = caseDto?.attendingPhysician?.fullName + " M.D."
        dto.licenseNo = caseDto?.attendingPhysician?.prcLicenseNo

        dto.dateAdmitted = caseDto?.admissionDatetime ? formatter.format(caseDto?.admissionDatetime) : '--'
        dto.dateDischarged = caseDto?.dischargedDatetime ? formatter.format(caseDto?.dischargedDatetime) : '--'
        dto.followUpDate = caseDto?.followupDatetime ? formatter.format(caseDto?.followupDatetime) : '--'
        dto.specialInstructions = caseDto?.specialInstructions ? caseDto?.specialInstructions : 'None specified';

        if (emp) {
            def employee = emp.first()
            dto.nurseName = employee.fullName

        }

        if (caseDto?.takeHomeMedications) {
            ArrayList<DischargeMedicationDto2> medicationList = new ArrayList<>()
            def jsonSlurper = new JsonSlurper()
            def objects = jsonSlurper.parseText(caseDto?.takeHomeMedications)
            if (objects) {
                objects.eachWithIndex { LazyMap entry, int i ->
                    def dischargeMedicationDto = new DischargeMedicationDto2()
                    def checked = entry.get('checked') as Boolean

                    if(checked) {
                        def medicine = entry.get('medicine') as LazyMap

                        def constant = '--'
                        dischargeMedicationDto.dosage = constant

                        dischargeMedicationDto.qty = entry.get('qty').toString()
                        dischargeMedicationDto.breakfast_instructions = entry.get('breakfast_instructions') ? entry.get('breakfast_instructions').toString() : '--'
                        dischargeMedicationDto.breakfast = entry.get('breakfast') ? entry.get('breakfast').toString() : '--'
                        dischargeMedicationDto.lunch_instructions = entry.get('lunch_instructions') ? entry.get('lunch_instructions').toString() : '--'
                        dischargeMedicationDto.lunch = entry.get('lunch') ? entry.get('lunch').toString() : '--'
                        dischargeMedicationDto.supper_instructions = entry.get('supper_instructions') ? entry.get('supper_instructions').toString() : '--'
                        dischargeMedicationDto.supper = entry.get('supper') ? entry.get('supper').toString() : '--'
                        dischargeMedicationDto.bedtime = entry.get('bedtime') ? entry.get('bedtime').toString() : '--'

                        if (entry.get('dosage')) {
                            constant = constantRepository.getOne(UUID.fromString(entry.get('dosage').toString()))
                            dischargeMedicationDto.dosage = constant.name
                        }

                        dischargeMedicationDto.instructions = entry.get('instructions') ? entry.get('instructions').toString() : '--'
                        dischargeMedicationDto.medication = medicine.get('descLong')

                        medicationList.add(dischargeMedicationDto)
                        parameters.put('medications', new JRBeanCollectionDataSource(medicationList))
                    }
                }
            }
        }

        def bytearray = new ByteArrayInputStream()
        def os = new ByteArrayOutputStream()
        def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
        def dataSource = new JsonDataSource(dataSourceByteArray)

        if (logo?.exists()) {
            parameters.put("logo", logo?.inputStream)
        }

        if (chkchecked?.exists()) {
            parameters.put("check", chkchecked.getURL())
        }

        if (chkunchecked?.exists()) {
            parameters.put("uncheck", chkunchecked.getURL())
        }

        if(hospitalConfigService?.hospitalInfo){
            parameters.put('hospitalName', hospitalConfigService?.hospitalInfo?.hospitalName?:"")
            String address = ""

            address +=  hospitalConfigService?.hospitalInfo?.address
            address +=  hospitalConfigService?.hospitalInfo?.addressLine2

            parameters.put('hospitalAddress', address)
        }

        try {
            def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, dataSource)

            def pdfExporter = new JRPdfExporter()

            def outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(os)

            pdfExporter.setExporterInput(new SimpleExporterInput(jrprint))
            pdfExporter.setExporterOutput(outputStreamExporterOutput)
            def configuration = new SimplePdfExporterConfiguration()
            pdfExporter.setConfiguration(configuration)
            pdfExporter.exportReport()

        } catch (JRException e) {
            e.printStackTrace()
        } catch (IOException e) {
            e.printStackTrace()
        }

        if (bytearray != null)
            IOUtils.closeQuietly(bytearray)

        def data = os.toByteArray()
        def params = new LinkedMultiValueMap<String, String>()
        params.add("Content-Disposition", "inline;filename=Discharge-Instruction-of-\"" + caseDto?.patient?.fullName + "\".pdf")
        return new ResponseEntity(data, params, HttpStatus.OK)
    }

    @RequestMapping(value = '/doctors-order', produces = 'application/pdf')
    ResponseEntity<byte[]> printDoctorsOrder(
            @RequestParam('caseId') UUID caseId,
            @RequestParam('from') String from,
            @RequestParam('to') String to
    ) {
        DateTimeFormatter dobFormatter =
                DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
        DateTimeFormatter dateTimeFormatter =
                DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a").withZone(ZoneId.systemDefault())
        def caseDto = caseRepository.findById(caseId).get()
        def res = applicationContext?.getResource("classpath:/reports/pms/doctors_order.jasper")
        def logo = applicationContext?.getResource("classpath:/reports/logo.png")
        def parameters = [:] as Map<String, Object>
        def gson = new Gson()
        def chkchecked = applicationContext.getResource("classpath:/reports/check.png")
        def chkunchecked = applicationContext.getResource("classpath:/reports/uncheck.png")
        def format = new SimpleDateFormat("MMddyyyy")
        def doList = new ArrayList()
        def doListDto = new ArrayList<DoctorsOrderDtoV2>()
        def age = Period.between(caseDto.patient.dob, LocalDate.now()).years
        def dto = new DischargeInstructionDto(
                patientFullName: caseDto?.patient?.fullName,
                pin: caseDto?.patient?.patientNo,
                caseNo: caseDto?.caseNo,
                roomNo: caseDto?.room?.roomNo,
                age: age,
                gender: caseDto?.patient?.gender?.toLowerCase(),
                address: caseDto?.patient?.fullAddress,
                admittingDiagnosis: caseDto?.admittingDiagnosis,
                dob: dobFormatter.format(caseDto?.patient?.dob)
        )
        if (from && to) {
            def fromInstant = format.parse(from).toInstant()
            def toInstant = format.parse(to).toInstant()
            doList = doctorOrderRepository.getDoctorOrdersByCaseByDateRange(caseId, fromInstant, toInstant)
        }

        if (doList) {
            doList.eachWithIndex { DoctorOrder entry, int i ->
                def doItems = doctorOrderItemRepository.getDoctorOrderItemsByDoctorOrder(entry.id)
                def doNotes = doctorOrderProgressNoteRepository.getDoctorOrderProgressNotesByDoctorOrder(entry.id)
                def items = new ArrayList<DoctorsOrderItemDto>()
                def notes = new ArrayList<DoctorsOrderNotesDto>()

                doItems.eachWithIndex { DoctorOrderItem entryItem, int iItem ->
                    def doDto = new DoctorsOrderItemDto(
                            entryDateTime: entry.entryDateTime,
                            order: entryItem.order
                    )
                    items.add(doDto)
                }

                doNotes.eachWithIndex { DoctorOrderProgressNote entryNote, int iNote ->
                    def note = new DoctorsOrderNotesDto(
                            entryDateTime: entryNote.entryDateTime,
                            note: entryNote.note
                    )
                    notes.add(note)
                }

                def doctorsOrder = new DoctorsOrderDtoV2(
                        entryDateTime: dateTimeFormatter.format(entry.entryDateTime),
                        items: items,
                        notes: notes
                )

                doListDto.add(doctorsOrder)

            }

        }

        if (doListDto) {
            parameters.put('doctorsOrder', new JRBeanCollectionDataSource(doListDto))
        }

        def bytearray = new ByteArrayInputStream()
        def os = new ByteArrayOutputStream()
        def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
        def dataSource = new JsonDataSource(dataSourceByteArray)

        if (logo.exists()) {
            parameters.put("logo", logo?.getURL())
        }

        if (chkchecked.exists()) {
            parameters.put("check", chkchecked.getURL())
        }

        if (chkunchecked.exists()) {
            parameters.put("uncheck", chkunchecked.getURL())
        }

        if(hospitalConfigService?.hospitalInfo){
            parameters.put('hospitalName', hospitalConfigService?.hospitalInfo?.hospitalName?:"")
            String address = ""

            address +=  hospitalConfigService?.hospitalInfo?.address
            address +=  hospitalConfigService?.hospitalInfo?.addressLine2

            parameters.put('hospitalAddress', address)
        }

        try {
            def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, dataSource)

            def pdfExporter = new JRPdfExporter()

            def outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(os)

            pdfExporter.setExporterInput(new SimpleExporterInput(jrprint))
            pdfExporter.setExporterOutput(outputStreamExporterOutput)
            def configuration = new SimplePdfExporterConfiguration()
            pdfExporter.setConfiguration(configuration)
            pdfExporter.exportReport()

        } catch (JRException e) {
            e.printStackTrace()
        } catch (IOException e) {
            e.printStackTrace()
        }

        if (bytearray != null)
            IOUtils.closeQuietly(bytearray)

        def data = os.toByteArray()
        def params = new LinkedMultiValueMap<String, String>()
        params.add("Content-Disposition", "inline;filename=Discharge-Instruction-of-\"" + caseDto?.patient?.fullName + "\".pdf")
        return new ResponseEntity(data, params, HttpStatus.OK)

    }

    @RequestMapping(value = '/nurse_notes_report', produces = 'application/pdf')
    ResponseEntity<byte[]> printNurseNotes(
            @RequestParam('caseId') UUID caseId,
            @RequestParam('from') String from,
            @RequestParam('to') String to
    ) {
        DateTimeFormatter dobFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a").withZone(ZoneId.systemDefault())
        def caseDto = caseRepository.findById(caseId).get()
        def res = applicationContext?.getResource("classpath:/reports/pms/nurse_notes_report.jasper")
        def logo = applicationContext?.getResource("classpath:/reports/logo.png")
        def parameters = [:] as Map<String, Object>
        def gson = new Gson()
        def chkchecked = applicationContext.getResource("classpath:/reports/check.png")
        def chkunchecked = applicationContext.getResource("classpath:/reports/uncheck.png")
        def format = new SimpleDateFormat("MMddyyyy")
        def nurseNotes = new ArrayList<NurseNote>()
        def nurseNotesDto = new ArrayList<NurseNotesDto>()

        def age = Period.between(caseDto.patient.dob, LocalDate.now()).years
        def dto = new PatientInfoDto(
                patientFullName: caseDto?.patient?.fullName,
                pin: caseDto?.patient?.patientNo,
                caseNo: caseDto?.caseNo,
                roomNo: caseDto?.room?.roomNo,
                age: age,
                gender: caseDto?.patient?.gender?.toLowerCase(),
                address: caseDto?.patient?.fullAddress,
                admittingDiagnosis: caseDto?.admittingDiagnosis,
                dob: dobFormatter.format(caseDto?.patient?.dob),
                civilStatus: caseDto?.patient?.civilStatus
        )

        if (from && to) {
            def fromInstant = format.parse(from).toInstant()
            def toInstant = format.parse(to).toInstant()
            nurseNotes = nurseNoteRepository.getNurseNotesByCase(caseId, fromInstant, toInstant)
        }

        if (nurseNotes) {
            nurseNotes.eachWithIndex { NurseNote entry, int i ->
                def nurseNoteDto = new NurseNotesDto(
                        dateTime: dateTimeFormatter.format(entry.entryDateTime),
                        focus: entry.focus ? entry.focus : '--',
                        data: entry.data ? entry.data : '--',
                        action: entry.action ? entry.action : '--',
                        response: entry.response ? entry.response : '--',
                        employee: entry.employee.fullInitialName
                )

                nurseNotesDto.add(nurseNoteDto)
            }
        }

        if (nurseNotesDto) {
            parameters.put('nurseNotesDatasource', new JRBeanCollectionDataSource(nurseNotesDto))
        }

        def bytearray = new ByteArrayInputStream()
        def os = new ByteArrayOutputStream()
        def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
        def dataSource = new JsonDataSource(dataSourceByteArray)

        if (logo.exists()) {
            parameters.put("logo", logo?.getURL())
        }

        if (chkchecked.exists()) {
            parameters.put("check", chkchecked.getURL())
        }

        if (chkunchecked.exists()) {
            parameters.put("uncheck", chkunchecked.getURL())
        }
        try {
            def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, dataSource)

            def pdfExporter = new JRPdfExporter()

            def outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(os)

            pdfExporter.setExporterInput(new SimpleExporterInput(jrprint))
            pdfExporter.setExporterOutput(outputStreamExporterOutput)
            def configuration = new SimplePdfExporterConfiguration()
            pdfExporter.setConfiguration(configuration)
            pdfExporter.exportReport()

        } catch (JRException e) {
            e.printStackTrace()
        } catch (IOException e) {
            e.printStackTrace()
        }

        if (bytearray != null)
            IOUtils.closeQuietly(bytearray)

        def data = os.toByteArray()
        def params = new LinkedMultiValueMap<String, String>()
        params.add("Content-Disposition", "inline;filename=Nurse-Notes-of-\"" + caseDto?.patient?.fullName + "\".pdf")
        return new ResponseEntity(data, params, HttpStatus.OK)

    }



    @RequestMapping(value = '/doctor_notes_report', produces = 'application/pdf')
    ResponseEntity<byte[]> printDoctorNotes(
            @RequestParam('caseId') UUID caseId,
            @RequestParam('from') String from,
            @RequestParam('to') String to
    ) {
        DateTimeFormatter dobFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a").withZone(ZoneId.systemDefault())
        def caseDto = caseRepository.findById(caseId).get()
        def res = applicationContext?.getResource("classpath:/reports/pms/doctor_notes_report_9.jasper")
        def logo = applicationContext?.getResource("classpath:/reports/logo.png")
        def parameters = [:] as Map<String, Object>
        def gson = new Gson()
        def chkchecked = applicationContext.getResource("classpath:/reports/check.png")
        def chkunchecked = applicationContext.getResource("classpath:/reports/uncheck.png")
        def format = new SimpleDateFormat("MMddyyyy")
        def doctorNotes = new ArrayList<DoctorNote>()
        def doctorNotesDto = new ArrayList<DoctorNotesDto>()

        def age = Period.between(caseDto.patient.dob, LocalDate.now()).years
        def dto = new PatientInfoDto(
                patientFullName: caseDto?.patient?.fullName,
                pin: caseDto?.patient?.patientNo,
                caseNo: caseDto?.caseNo,
                roomNo: caseDto?.room?.roomNo,
                age: age,
                gender: caseDto?.patient?.gender?.toLowerCase(),
                address: caseDto?.patient?.fullAddress,
                admittingDiagnosis: caseDto?.admittingDiagnosis,
                dob: dobFormatter.format(caseDto?.patient?.dob),
                civilStatus: caseDto?.patient?.civilStatus
        )

        if (from && to) {
            def fromInstant = format.parse(from).toInstant()
            def toInstant = format.parse(to).toInstant()
            doctorNotes = doctorNotesRepository.getDoctorNotesByCase(caseId, fromInstant, toInstant)
        }

        if (doctorNotes) {
            doctorNotes.eachWithIndex { DoctorNote entry, int i ->
                def doctorNoteDto = new DoctorNotesDto(
                        dateTime: dateTimeFormatter.format(entry.entryDateTime),
                        subjective: entry.subjective ? entry.objective: '--',
                        objective: entry.objective ? entry.objective : '--',
                        assessment: entry.assessment ? entry.assessment : '--',
                        plan: entry.plan ? entry.plan : '--',
                        employee: entry.employee.fullInitialName
                )

                doctorNotesDto.add(doctorNoteDto)
            }
        }

        if (doctorNotesDto) {
            parameters.put('doctorNotesDatasource', new JRBeanCollectionDataSource(doctorNotesDto))
        }

        def bytearray = new ByteArrayInputStream()
        def os = new ByteArrayOutputStream()
        def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
        def dataSource = new JsonDataSource(dataSourceByteArray)

        if (logo.exists()) {
            parameters.put("logo", logo?.getURL())
        }

        if (chkchecked.exists()) {
            parameters.put("check", chkchecked.getURL())
        }

        if (chkunchecked.exists()) {
            parameters.put("uncheck", chkunchecked.getURL())
        }
        try {
            def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, dataSource)

            def pdfExporter = new JRPdfExporter()

            def outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(os)

            pdfExporter.setExporterInput(new SimpleExporterInput(jrprint))
            pdfExporter.setExporterOutput(outputStreamExporterOutput)
            def configuration = new SimplePdfExporterConfiguration()
            pdfExporter.setConfiguration(configuration)
            pdfExporter.exportReport()

        } catch (JRException e) {
            e.printStackTrace()
        } catch (IOException e) {
            e.printStackTrace()
        }

        if (bytearray != null)
            IOUtils.closeQuietly(bytearray)

        def data = os.toByteArray()
        def params = new LinkedMultiValueMap<String, String>()
        params.add("Content-Disposition", "inline;filename=Doctor-Notes-of-\"" + caseDto?.patient?.fullName + "\".pdf")
        return new ResponseEntity(data, params, HttpStatus.OK)

    }

    @RequestMapping(value = '/cathlab_notes_report', produces = 'application/pdf')
    ResponseEntity<byte[]> printCathLabNote(
            @RequestParam('caseId') UUID caseId,
            @RequestParam('from') String from,
            @RequestParam('to') String to
    ) {
        DateTimeFormatter dobFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a").withZone(ZoneId.systemDefault())
        def caseDto = caseRepository.findById(caseId).get()
        def res = applicationContext?.getResource("classpath:/reports/pms/cathlab_notes_report.jasper")
        def logo = applicationContext?.getResource("classpath:/reports/logo.png")
        def parameters = [:] as Map<String, Object>
        def gson = new Gson()
        def chkchecked = applicationContext.getResource("classpath:/reports/check.png")
        def chkunchecked = applicationContext.getResource("classpath:/reports/uncheck.png")
        def format = new SimpleDateFormat("MMddyyyy")
        def cathlabNote = new ArrayList<CathLabNote>()
        def cathLabNotesDto = new ArrayList<CathLabNotesDto>()

        def age = Period.between(caseDto.patient.dob, LocalDate.now()).years
        def dto = new PatientInfoDto(
                patientFullName: caseDto?.patient?.fullName,
                pin: caseDto?.patient?.patientNo,
                caseNo: caseDto?.caseNo,
                roomNo: caseDto?.room?.roomNo,
                age: age,
                gender: caseDto?.patient?.gender?.toLowerCase(),
                address: caseDto?.patient?.fullAddress,
                admittingDiagnosis: caseDto?.admittingDiagnosis,
                dob: dobFormatter.format(caseDto?.patient?.dob),
                civilStatus: caseDto?.patient?.civilStatus
        )

        cathlabNote = cathLabNoteRepository.getCathlabNotesByCaseId (caseId)

        if (cathlabNote) {
            cathlabNote.eachWithIndex { CathLabNote entry, int i ->
                def cathLabNoteDto = new CathLabNotesDto(
                        dateTime: dateTimeFormatter.format(entry.entryDateTime),
                        summary: entry.summary ? entry.summary: '--',
                        comment: entry.comment ? entry.comment : '--',
                        employee: entry.employee.fullInitialName
                )

                cathLabNotesDto.add(cathLabNoteDto)
            }
        }

        if (cathLabNotesDto) {
            parameters.put('cathLabNotesDatasource', new JRBeanCollectionDataSource(cathLabNotesDto))
        }

        def bytearray = new ByteArrayInputStream()
        def os = new ByteArrayOutputStream()
        def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
        def dataSource = new JsonDataSource(dataSourceByteArray)

        if (logo.exists()) {
            parameters.put("logo", logo?.getURL())
        }

        if (chkchecked.exists()) {
            parameters.put("check", chkchecked.getURL())
        }

        if (chkunchecked.exists()) {
            parameters.put("uncheck", chkunchecked.getURL())
        }
        try {
            def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, dataSource)

            def pdfExporter = new JRPdfExporter()

            def outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(os)

            pdfExporter.setExporterInput(new SimpleExporterInput(jrprint))
            pdfExporter.setExporterOutput(outputStreamExporterOutput)
            def configuration = new SimplePdfExporterConfiguration()
            pdfExporter.setConfiguration(configuration)
            pdfExporter.exportReport()

        } catch (JRException e) {
            e.printStackTrace()
        } catch (IOException e) {
            e.printStackTrace()
        }

        if (bytearray != null)
            IOUtils.closeQuietly(bytearray)

        def data = os.toByteArray()
        def params = new LinkedMultiValueMap<String, String>()
        params.add("Content-Disposition", "inline;filename=Doctor-Notes-of-\"" + caseDto?.patient?.fullName + "\".pdf")
        return new ResponseEntity(data, params, HttpStatus.OK)

    }



    @RequestMapping(value = '/io_report', produces = 'application/pdf')
    ResponseEntity<byte[]> printIoReport(
            @RequestParam('caseId') UUID caseId,
            @RequestParam('from') String from,
            @RequestParam('to') String to
    ) {
        DateTimeFormatter dobFormatter =
                DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
        DateTimeFormatter dateTimeFormatter =
                DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a").withZone(ZoneId.systemDefault())
        def caseDto = caseRepository.findById(caseId).get()
        def res = applicationContext?.getResource("classpath:/reports/pms/io_report.jasper")
        def logo = applicationContext?.getResource("classpath:/reports/logo.png")
        def parameters = [:] as Map<String, Object>
        def gson = new Gson()
        def chkchecked = applicationContext.getResource("classpath:/reports/check.png")
        def chkunchecked = applicationContext.getResource("classpath:/reports/uncheck.png")
        def format = new SimpleDateFormat("MMddyyyy")
        def intakeList = new ArrayList<Intake>()
        def outputList = new ArrayList<Output>()
        def intakeListDto = new ArrayList<IntakeDto>()
        def outputListDto = new ArrayList<OutputDto>()
        def age = Period.between(caseDto.patient.dob, LocalDate.now()).years
        def dto = new DischargeInstructionDto(
                patientFullName: caseDto?.patient?.fullName,
                pin: caseDto?.patient?.patientNo,
                caseNo: caseDto?.caseNo,
                roomNo: caseDto?.room?.roomNo,
                age: age,
                gender: caseDto?.patient?.gender?.toLowerCase(),
                address: caseDto?.patient?.fullAddress,
                admittingDiagnosis: caseDto?.admittingDiagnosis,
                dob: dobFormatter.format(caseDto?.patient?.dob)
        )
        if (from && to) {
            def fromInstant = format.parse(from).toInstant()
            def toInstant = format.parse(to).toInstant()
            intakeList = intakeRepository.intakesWithin24hrs(caseId, fromInstant, toInstant)
            outputList = outputRepository.outputsWithin24hrs(caseId, fromInstant, toInstant)
        }

        if (intakeList) {

            intakeList.eachWithIndex { Intake entry, int i ->
                def intakeDto = new IntakeDto(
                        dateTime: dateTimeFormatter.format(entry.entryDateTime),
                        po: entry.poIntake,
                        tubeNgt: entry.tubeIntake,
                        ivf: entry.ivfIntake,
                        blood: entry.bloodIntake,
                        tpn: entry.tpnIntake,
                        medication: entry.medicationIntake

                )

                intakeListDto.add(intakeDto)
            }

        }

        if (outputList) {

            outputList.eachWithIndex { Output entry, int i ->
                def outputDto = new OutputDto(
                        dateTime: dateTimeFormatter.format(entry.entryDateTime),
                        voided: entry.voidedOutput,
                        foleyCatheter: entry.catheterOutput,
                        ng: entry.ngOutput,
                        insesibleLoss: entry.insensibleLossOutput,
                        stool: entry.stoolOutput,
                        emisis: entry.emesisOutput
                )

                outputListDto.add(outputDto)
            }

        }

        if (intakeListDto) {
            parameters.put('intakeDataSource', new JRBeanCollectionDataSource(intakeListDto))
        }

        if (outputListDto) {
            parameters.put('outputDatasource', new JRBeanCollectionDataSource(outputListDto))
        }

        def bytearray = new ByteArrayInputStream()
        def os = new ByteArrayOutputStream()
        def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
        def dataSource = new JsonDataSource(dataSourceByteArray)

        if (logo.exists()) {
            parameters.put("logo", logo?.getURL())
        }

        if (chkchecked.exists()) {
            parameters.put("check", chkchecked.getURL())
        }

        if (chkunchecked.exists()) {
            parameters.put("uncheck", chkunchecked.getURL())
        }

        if(hospitalConfigService?.hospitalInfo){
            parameters.put('hospitalName', hospitalConfigService?.hospitalInfo?.hospitalName?:"")
            String address = ""

            address +=  hospitalConfigService?.hospitalInfo?.address
            address +=  hospitalConfigService?.hospitalInfo?.addressLine2

            parameters.put('hospitalAddress', address)
        }

        try {
            def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, dataSource)

            def pdfExporter = new JRPdfExporter()

            def outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(os)

            pdfExporter.setExporterInput(new SimpleExporterInput(jrprint))
            pdfExporter.setExporterOutput(outputStreamExporterOutput)
            def configuration = new SimplePdfExporterConfiguration()
            pdfExporter.setConfiguration(configuration)
            pdfExporter.exportReport()

        } catch (JRException e) {
            e.printStackTrace()
        } catch (IOException e) {
            e.printStackTrace()
        }

        if (bytearray != null)
            IOUtils.closeQuietly(bytearray)

        def data = os.toByteArray()
        def params = new LinkedMultiValueMap<String, String>()
        params.add("Content-Disposition", "inline;filename=Discharge-Instruction-of-\"" + caseDto?.patient?.fullName + "\".pdf")
        return new ResponseEntity(data, params, HttpStatus.OK)

    }

    @RequestMapping(value = '/vital_signs', produces = 'application/pdf')
    ResponseEntity<byte[]> printVitalSigns(
            @RequestParam('caseId') UUID caseId,
            @RequestParam('from') String from,
            @RequestParam('to') String to
    ) {
        DateTimeFormatter dobFormatter =
                DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
        DateTimeFormatter dateTimeFormatter =
                DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a").withZone(ZoneId.systemDefault())
        def caseDto = caseRepository.findById(caseId).get()
        def res = applicationContext?.getResource("classpath:/reports/pms/tpr_sheet_report.jasper")
        def logo = applicationContext?.getResource("classpath:/reports/logo.png")
        def parameters = [:] as Map<String, Object>
        def gson = new Gson()
        def chkchecked = applicationContext.getResource("classpath:/reports/check.png")
        def chkunchecked = applicationContext.getResource("classpath:/reports/uncheck.png")
        def format = new SimpleDateFormat("MMddyyyy")
        def age = Period.between(caseDto.patient.dob, LocalDate.now()).years
        def vitalSigns = new ArrayList<VitalSign>()
        def vitalSignsDto = new ArrayList<VitalSignDto>()
        def dto = new DischargeInstructionDto(
                patientFullName: caseDto?.patient?.fullName,
                pin: caseDto?.patient?.patientNo,
                caseNo: caseDto?.caseNo,
                roomNo: caseDto?.room?.roomNo,
                age: age,
                gender: caseDto?.patient?.gender?.toLowerCase(),
                address: caseDto?.patient?.fullAddress,
                admittingDiagnosis: caseDto?.admittingDiagnosis,
                dob: dobFormatter.format(caseDto?.patient?.dob)
        )
        if (from && to) {
            def fromInstant = format.parse(from).toInstant()
            def toInstant = format.parse(to).toInstant()
            vitalSigns = vitalSignRepository.getVitalSignFromTo(caseId, fromInstant, toInstant)
        }

        if (vitalSigns) {
            vitalSigns.eachWithIndex { VitalSign entry, int i ->
                def vitalSignDto = new VitalSignDto(
                        dateTime: dateTimeFormatter.format(entry.entryDateTime),
                        systolic: entry.systolic,
                        diastolic: entry.diastolic,
                        temp: entry.temperature,
                        pulse: entry.pulseRate,
                        resp: entry.respiratoryRate,
                        o2Sat: entry.oxygenSaturation,
                        painScore: entry.painScore,
                        heartRate: entry.fetalHr
                )

                vitalSignsDto.add(vitalSignDto)
            }
        }

        if (vitalSignsDto) {
            parameters.put("vitalSignsDatasource", new JRBeanCollectionDataSource(vitalSignsDto))
        }

        def bytearray = new ByteArrayInputStream()
        def os = new ByteArrayOutputStream()
        def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
        def dataSource = new JsonDataSource(dataSourceByteArray)

        if (logo.exists()) {
            parameters.put("logo", logo?.getURL())
        }

        if (chkchecked.exists()) {
            parameters.put("check", chkchecked.getURL())
        }

        if (chkunchecked.exists()) {
            parameters.put("uncheck", chkunchecked.getURL())
        }

        if(hospitalConfigService?.hospitalInfo){
            parameters.put('hospitalName', hospitalConfigService?.hospitalInfo?.hospitalName?:"")
            String address = ""

            address +=  hospitalConfigService?.hospitalInfo?.address
            address +=  hospitalConfigService?.hospitalInfo?.addressLine2

            parameters.put('hospitalAddress', address)
        }

        try {
            def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, dataSource)

            def pdfExporter = new JRPdfExporter()

            def outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(os)

            pdfExporter.setExporterInput(new SimpleExporterInput(jrprint))
            pdfExporter.setExporterOutput(outputStreamExporterOutput)
            def configuration = new SimplePdfExporterConfiguration()
            pdfExporter.setConfiguration(configuration)
            pdfExporter.exportReport()

        } catch (JRException e) {
            e.printStackTrace()
        } catch (IOException e) {
            e.printStackTrace()
        }

        if (bytearray != null)
            IOUtils.closeQuietly(bytearray)

        def data = os.toByteArray()
        def params = new LinkedMultiValueMap<String, String>()
        params.add("Content-Disposition", "inline;filename=Discharge-Instruction-of-\"" + caseDto?.patient?.fullName + "\".pdf")
        return new ResponseEntity(data, params, HttpStatus.OK)

    }

    @RequestMapping(value = '/charge_slip', produces = 'application/pdf')
    ResponseEntity<byte[]> printChargeSlip(@RequestParam('caseId') UUID caseId, @RequestParam('orderSlipNo') String orderSlipNo) {
        def caseDto = caseRepository.findById(caseId).get()
        def res = applicationContext?.getResource("classpath:/reports/pms/charge_slip.jasper")
        def bytearray = new ByteArrayInputStream()
        def os = new ByteArrayOutputStream()

        def logo = applicationContext?.getResource("classpath:/reports/logo.png")
        def parameters = [:] as Map<String, Object>
        def chkchecked = applicationContext.getResource("classpath:/reports/check.png")
        def chkunchecked = applicationContext.getResource("classpath:/reports/uncheck.png")
        def username = SecurityUtils.currentLogin()
        def user = userRepository.findOneByLogin(username)
        def emp = employeeRepository.findOneByUser(user)


        def age = Period.between(caseDto.patient.dob, LocalDate.now()).years
        DateTimeFormatter dobFormatter =
                DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
        BigDecimal totalAmt = 0.0

        def dto = new DischargeInstructionDto(
                patientFullName: caseDto?.patient?.fullName,
                pin: caseDto?.patient?.patientNo,
                caseNo: caseDto?.caseNo,
                roomNo: caseDto?.room?.roomNo,
                age: age,
                gender: caseDto?.patient?.gender?.toLowerCase(),
                address: caseDto?.patient?.fullAddress,
                admittingDiagnosis: caseDto?.admittingDiagnosis,
                dob: dobFormatter.format(caseDto?.patient?.dob),
                preparedByFullName: emp?.fullName,

        )

        List<OrderSlipItem> orderItems = orderSlipItemRepository.getByOrderSlipNo(orderSlipNo)
        List<ChargeSlipDto> itemsDto = new  ArrayList<ChargeSlipDto>()

        if (orderItems) {
            orderItems.forEach{
                if(it.billing_item) {
                    def item = new ChargeSlipDto(
                            description: it.service.description,
                            price: it.billing_item?.debit ?: BigDecimal.ZERO,
                            subTotal: it.billing_item?.debit ?: BigDecimal.ZERO,
                            qty: it.billing_item?.qty ?: 0,
                            requesting: it.orderslip?.requestingPhysicianName ?: "",
                            itemNo: it?.itemNo ?: "")
                    itemsDto.add(item)
                }
                totalAmt += it.billing_item?.debit?: BigDecimal.ZERO
            }
        }

        if (logo.exists()) {
            parameters.put("logo", logo?.getURL())
        }

        if (chkchecked.exists()) {
            parameters.put("check", chkchecked.getURL())
        }

        if (chkunchecked.exists()) {
            parameters.put("uncheck", chkunchecked.getURL())
        }

        if (itemsDto) {
            parameters.put('items', new JRBeanCollectionDataSource(itemsDto))
        }

        if (totalAmt) {
            parameters.put('totalAmt', totalAmt)
        }

        if (emp?.departmentOfDuty) {
            parameters.put('departmentOfDuty', emp.departmentOfDuty.departmentName)
        }


        if(hospitalConfigService?.hospitalInfo){
            parameters.put('hospitalname', hospitalConfigService?.hospitalInfo?.hospitalName?:"")
            String address = ""

            address +=  hospitalConfigService?.hospitalInfo?.address
            address +=  hospitalConfigService?.hospitalInfo?.addressLine2

            parameters.put('hospitaladdress', address)
        }

        List<DischargeInstructionDto> ds = []
        ds << dto

        try {
            def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, new JRBeanCollectionDataSource(ds) )

            def pdfExporter = new JRPdfExporter()

            def outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(os)

            pdfExporter.setExporterInput(new SimpleExporterInput(jrprint))
            pdfExporter.setExporterOutput(outputStreamExporterOutput)
            def configuration = new SimplePdfExporterConfiguration()
            pdfExporter.setConfiguration(configuration)
            pdfExporter.exportReport()

        } catch (JRException e) {
            e.printStackTrace()
        } catch (IOException e) {
            e.printStackTrace()
        }

        if (bytearray != null)
            IOUtils.closeQuietly(bytearray)

        def data = os.toByteArray()
        def params = new LinkedMultiValueMap<String, String>()
        params.add("Content-Disposition", "inline;filename=Discharge-Instruction-of-\"" + caseDto?.patient?.fullName + "\".pdf")
        return new ResponseEntity(data, params, HttpStatus.OK)

    }

    @RequestMapping(value = '/requisition_slip', produces = 'application/pdf')
    ResponseEntity<byte[]> requisitionSlip(
                @RequestParam('caseId') UUID caseId,
                @RequestParam('orderSlipNo') String orderSlipNo
//                @RequestParam('billingItems') List<UUID> billingItems
    ) {
        def caseDto = caseRepository.findById(caseId).get()
        def res = applicationContext?.getResource("classpath:/reports/pms/requisition_slip.jasper")
        def bytearray = new ByteArrayInputStream()
        def os = new ByteArrayOutputStream()

        def logo = applicationContext?.getResource("classpath:/reports/logo.png")
        def parameters = [:] as Map<String, Object>
        def chkchecked = applicationContext.getResource("classpath:/reports/check.png")
        def chkunchecked = applicationContext.getResource("classpath:/reports/uncheck.png")
        def username = SecurityUtils.currentLogin()
        def user = userRepository.findOneByLogin(username)
        def emp = employeeRepository.findOneByUser(user)


        def age = Period.between(caseDto.patient.dob, LocalDate.now()).years
        DateTimeFormatter dobFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())


        def dto = new DischargeInstructionDto(
                patientFullName: caseDto?.patient?.fullName,
                pin: caseDto?.patient?.patientNo,
                caseNo: caseDto?.caseNo,
                roomNo: caseDto?.room?.roomNo,
                age: age,
                gender: caseDto?.patient?.gender?.toLowerCase(),
                address: caseDto?.patient?.fullAddress,
                admittingDiagnosis: caseDto?.admittingDiagnosis,
                dob: dobFormatter.format(caseDto?.patient?.dob),
                preparedByFullName: emp?.fullName,

        )
        def gson = new Gson()
        def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
        def dataSource = new JsonDataSource(dataSourceByteArray)
        def orderItems = orderslipService.getOrderSlipItemByGroup(UUID.fromString(orderSlipNo))
        List<RequisitionDto> itemsDto = new  ArrayList<RequisitionDto>()
        List<OrderSlipDto> itemsDtos = new ArrayList<OrderSlipDto>()


        if (orderItems) {
            orderItems.forEach {

                def item = new RequisitionDto(
                        description: it.description,
                        qty: it.cnt.toBigInteger(),
                        requesting: it.requestingPhysicianName,
                        itemNo: it.item_no,
                        dateTime: dobFormatter.format(it.created_date)
                )
                itemsDto.add(item)
            }
        }

        if (logo.exists()) {
            parameters.put("logo", logo?.getURL())
        }

        if (chkchecked.exists()) {
            parameters.put("check", chkchecked.getURL())
        }

        if (chkunchecked.exists()) {
            parameters.put("uncheck", chkunchecked.getURL())
        }

        if (itemsDto) {
            parameters.put('items', new JRBeanCollectionDataSource(itemsDto))
        }

        if (emp?.departmentOfDuty) {
            parameters.put('departmentOfDuty', emp.departmentOfDuty.departmentName)
        }


        if(hospitalConfigService?.hospitalInfo){
            parameters.put('hospitalname', hospitalConfigService?.hospitalInfo?.hospitalName?:"")
            String address = ""

            address +=  hospitalConfigService?.hospitalInfo?.address
            address +=  hospitalConfigService?.hospitalInfo?.addressLine2

            parameters.put('hospitaladdress', address)
        }

        try {
            def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, dataSource)

            def pdfExporter = new JRPdfExporter()

            def outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(os)

            pdfExporter.setExporterInput(new SimpleExporterInput(jrprint))
            pdfExporter.setExporterOutput(outputStreamExporterOutput)
            def configuration = new SimplePdfExporterConfiguration()
            pdfExporter.setConfiguration(configuration)
            pdfExporter.exportReport()

        } catch (JRException e) {
            e.printStackTrace()
        } catch (IOException e) {
            e.printStackTrace()
        }

        if (bytearray != null)
            IOUtils.closeQuietly(bytearray)

        def data = os.toByteArray()
        def params = new LinkedMultiValueMap<String, String>()
        params.add("Content-Disposition", "inline;filename=Discharge-Instruction-of-\"" + caseDto?.patient?.fullName + "\".pdf")
        return new ResponseEntity(data, params, HttpStatus.OK)

    }

    @RequestMapping(value = '/charge_slip_by_item', produces = 'application/pdf')
    ResponseEntity<byte[]> printChargeSlipByItem(@RequestParam("caseId") UUID caseId, @RequestParam('billingItems') List<UUID> billingItems, @RequestParam('empId') UUID empId) {
        def caseDto = caseRepository.findById(caseId).get()
        def res = applicationContext?.getResource("classpath:/reports/pms/charge_slip.jasper")
        def bytearray = new ByteArrayInputStream()
        def os = new ByteArrayOutputStream()
        def emp = employeeRepository.findById(empId).get()
        def logo = applicationContext?.getResource("classpath:/reports/logo.png")
        def parameters = [:] as Map<String, Object>
        def chkchecked = applicationContext.getResource("classpath:/reports/check.png")
        def chkunchecked = applicationContext.getResource("classpath:/reports/uncheck.png")

        def items = billingItems.collect { billingItemServices.findOne(it) }
        def itemsDto = new ArrayList<ChargeSlipDto>()
        def age = Period.between(caseDto.patient.dob, LocalDate.now()).years
        DateTimeFormatter dobFormatter =
                DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())

        String signature

        if (emp.signature1) {
            signature = emp.signature1.split(",")[1]
        } else if (emp.signature2) {
            signature = emp.signature2.split(",")[1]
        } else if (emp.signature3) {
            signature = emp.signature3.split(",")[1]
        } else {
            signature = ""
        }
        BufferedImage image = null

        if (signature) {
            byte[] imageBytes = DatatypeConverter.parseBase64Binary(signature)

            image = ImageIO.read(new ByteArrayInputStream(imageBytes))
        }

        def dto = new DischargeInstructionDto(
                patientFullName: caseDto?.patient?.fullName,
                pin: caseDto?.patient?.patientNo,
                caseNo: caseDto?.caseNo,
                roomNo: caseDto?.room?.roomNo?:"OPD",
                age: age,
                gender: caseDto?.patient?.gender?.toLowerCase(),
                address: caseDto?.patient?.fullAddress,
                admittingDiagnosis: caseDto?.admittingDiagnosis,
                dob: dobFormatter.format(caseDto?.patient?.dob),
                preparedByFullName: emp?.fullName,
        )
        def gson = new Gson()
        def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
        def dataSource = new JsonDataSource(dataSourceByteArray)

        BigDecimal totalAmt = 0.0

        if (items) {
            items.each {
                it ->
                    List<OrderSlipItem> orderItem = orderSlipItemRepository.getByBillingItem(it)
                    def itemDto = new ChargeSlipDto(
                            description: it.description,
                            price: it.debit,
                            subTotal: it.subTotal,
                            qty: it.qty,
                            requesting: orderItem[0]?.orderslip?.requestingPhysicianName?:"",
                            itemNo: orderItem[0]?.itemNo?:''
                    )
                    totalAmt += it.subTotal
                    itemsDto.add(itemDto)
            }
        }

        if (logo.exists()) {
            parameters.put("logo", logo?.getURL())
        }

        if (chkchecked.exists()) {
            parameters.put("check", chkchecked.getURL())
        }

        if (chkunchecked.exists()) {
            parameters.put("uncheck", chkunchecked.getURL())
        }

        if (itemsDto) {
            parameters.put('items', new JRBeanCollectionDataSource(itemsDto))
        }

        if (image) {
            parameters.put('preparedBySignature', image)
        }

        if (totalAmt) {
            parameters.put('totalAmt', totalAmt)
        }

        if (emp?.departmentOfDuty) {
            parameters.put('departmentOfDuty', emp.departmentOfDuty.departmentName)
        }

        if(hospitalConfigService?.hospitalInfo){
            parameters.put('hospitalname', hospitalConfigService?.hospitalInfo?.hospitalName?:"")
            String address = ""

            address +=  hospitalConfigService?.hospitalInfo?.address
            address +=  hospitalConfigService?.hospitalInfo?.addressLine2

            parameters.put('hospitaladdress', address)
        }

        try {
            def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, dataSource)

            def pdfExporter = new JRPdfExporter()

            def outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(os)

            pdfExporter.setExporterInput(new SimpleExporterInput(jrprint))
            pdfExporter.setExporterOutput(outputStreamExporterOutput)
            def configuration = new SimplePdfExporterConfiguration()
            pdfExporter.setConfiguration(configuration)
            pdfExporter.exportReport()

        } catch (JRException e) {
            e.printStackTrace()
        } catch (IOException e) {
            e.printStackTrace()
        }

        if (bytearray != null)
            IOUtils.closeQuietly(bytearray)

        def data = os.toByteArray()
        def params = new LinkedMultiValueMap<String, String>()
        params.add("Content-Disposition", "inline;filename=Discharge-Instruction-of-\"" + caseDto?.patient?.fullName + "\".pdf")
        return new ResponseEntity(data, params, HttpStatus.OK)
    }

    @RequestMapping(value = '/admissionReport')
    ResponseEntity<AnyDocument.Any> admissionReport(@RequestParam("registry_type") String registry_type, @RequestParam('start') String start, @RequestParam('end') String end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddyyyy");
        DateTimeFormatter dtformatter = DateTimeFormatter.ofPattern("MM/dd/yyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        LocalDate startDate = LocalDate.parse(start, formatter);
        LocalDate endDate = LocalDate.parse(end, formatter);
        Instant[] instants = [startDate.atStartOfDay().toInstant(ZoneOffset.UTC), endDate.atStartOfDay().toInstant(ZoneOffset.UTC)]
        List<PatientCaseView> ret = patientCaseViewService.patientCaseViewForReportList("",null,registry_type,"",instants,"","","",false,0,9999,"")
        def buffer = new StringBuffer()
        CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV
                .withHeader( "PIN","Case Number","Admission Date and Time","First Name","Middle Name","Last Name","Suffix","DoB", "Age","Gender","Address","PHIC","Admitting diagnosis","Admitting Physician","Attending Physician","Discharge Date","Discharge Time","Disposition","Final Diagnosis"))
        ret.forEach {
            csvPrinter.printRecord(
                    it.patientNo,
                    it.parentCase?.caseNo,
                    it.parentCase.admissionDatetime.atZone(ZoneId.systemDefault()).toLocalDateTime().format(dtformatter)+"_"+it.admissionDatetime.atZone(ZoneId.systemDefault()).toLocalDateTime().format(timeFormatter),
                    it.firstName,
                    it.middleName,
                    it.lastName,
                    it.nameSuffix,
                    ""+it.parentCase?.patient?.dob?.getMonth() +"/"+
                            it.parentCase?.patient?.dob?.getDayOfMonth() +"/"+it.parentCase?.patient?.dob?.getYear(),
                    Period.between(it.parentCase?.patient?.dob, it.admissionDatetime.atZone(ZoneId.systemDefault()).toLocalDate()).getYears(),
                    it.parentCase?.patient.gender,
                    it.parentCase?.patient.address,
                    "PHICID:"+it.parentCase?.patient.philHealthId,
                    it.parentCase?.admittingDiagnosis,
                    it.parentCase?.admittingPhysician?.fullName,
                    it.parentCase?.attendingPhysician?.fullName,
                    it.parentCase?.dischargedDatetime,
                    it.parentCase?.dischargedDatetime,
                    it.parentCase?.dischargeDisposition,
                    it.parentCase?.dischargeDiagnosis

            )
        }

        def paramz = new LinkedMultiValueMap<String, String>()
        paramz.add("Content-Disposition", "inline;filename=Admission Report.csv")
        //return new ResponseEntity(buffer.toString().getBytes(), paramz, HttpStatus.OK)
        return new ResponseEntity(String.valueOf(buffer).getBytes(), paramz, HttpStatus.OK)


    }

    @RequestMapping(value = '/medicineAdministration',produces = 'application/pdf')
    ResponseEntity<AnyDocument.Any> administration(@RequestParam("parentCase") String parentCase,@RequestParam('empId') UUID empId) {

        def caseDto = caseRepository.findById(UUID.fromString(parentCase)).get()

        def res = applicationContext?.getResource("classpath:/reports/pms/medicaion_administration.jasper")
        def bytearray = new ByteArrayInputStream()
        def os = new ByteArrayOutputStream()
        def emp = employeeRepository.findById(empId).get()
        def logo = applicationContext?.getResource("classpath:/reports/logo.png")
        def parameters = [:] as Map<String, Object>
        def chkchecked = applicationContext.getResource("classpath:/reports/check.png")
        def chkunchecked = applicationContext.getResource("classpath:/reports/uncheck.png")

        def age = Period.between(caseDto.patient.dob, LocalDate.now()).years
        def formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
        def dobFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy")

        def dto = new DischargeInstructionDto(
                patientFullName: caseDto?.patient?.fullName,
                pin: caseDto?.patient?.patientNo,
                caseNo: caseDto?.caseNo,
                roomNo: caseDto?.room?.roomNo?:"OPD",
                age: age,
                gender: caseDto?.patient?.gender?.toLowerCase(),
                address: caseDto?.patient?.fullAddress,
                admittingDiagnosis: caseDto?.admittingDiagnosis,
                dob: dobFormat.format(caseDto?.patient?.dob),
                preparedByFullName: emp?.fullName,
                attendingPhysician:caseDto?.admittingPhysician?.fullName,
                dateAdmitted :caseDto?.admissionDatetime ? formatter.format(caseDto?.admissionDatetime) : '--',
                dateDischarged: caseDto?.dischargedDatetime ? formatter.format(caseDto?.dischargedDatetime) : '--',
                civilStatus:caseDto?.patient?.civilStatus,
                date:formatter.format(Instant.now())
        )

        String signature

        if (emp.signature1) {
            signature = emp.signature1.split(",")[1]
        } else if (emp.signature2) {
            signature = emp.signature2.split(",")[1]
        } else if (emp.signature3) {
            signature = emp.signature3.split(",")[1]
        } else {
            signature = ""
        }
        BufferedImage image = null

        if (signature) {
            byte[] imageBytes = DatatypeConverter.parseBase64Binary(signature)

            image = ImageIO.read(new ByteArrayInputStream(imageBytes))
        }
        def gson = new Gson()
        def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
        def dataSource = new JsonDataSource(dataSourceByteArray)

        def items =  administrationRepository.getMedicationAdministrationsByCase(UUID.fromString(parentCase)).sort {
            it.entryDateTime
        }
        DateTimeFormatter dateFormat =
                DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm").withZone(ZoneId.systemDefault())

        ArrayList<AdministrationReport> itemsDto = new ArrayList<>()
        if (items) {
            items.each {
                it ->
                    def itemDto = new AdministrationReport(
                            medicine: it.medication.medicine.descLong,
                            entryDateTime: dateFormat.format(it.entryDateTime),
                            employee: it.employee.fullName,
                            dose: it.dose,
                            remarks: it.remarks
                    )
                    itemsDto.add(itemDto)
            }
        }

        if (itemsDto) {
            parameters.put('items', new JRBeanCollectionDataSource(itemsDto))
        }
//
//		if (logo.exists()) {
//			parameters.put("logo", logo?.getURL())
//		}

        if (chkchecked.exists()) {
            parameters.put("check", chkchecked.getURL())
        }

        if (image) {
            parameters.put('preparedBySignature', image)
        }

        if (chkunchecked.exists()) {
            parameters.put("uncheck", chkunchecked.getURL())
        }

        if(hospitalConfigService?.hospitalInfo){
            parameters.put('hospitalname', hospitalConfigService?.hospitalInfo?.hospitalName?:"")
            String address = ""

            address +=  hospitalConfigService?.hospitalInfo?.address
            address +=  hospitalConfigService?.hospitalInfo?.addressLine2

            parameters.put('hospitaladdress', address)
        }

        try {
            def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, dataSource)

            def pdfExporter = new JRPdfExporter()

            def outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(os)

            pdfExporter.setExporterInput(new SimpleExporterInput(jrprint))
            pdfExporter.setExporterOutput(outputStreamExporterOutput)
            def configuration = new SimplePdfExporterConfiguration()
            pdfExporter.setConfiguration(configuration)
            pdfExporter.exportReport()

        } catch (JRException e) {
            e.printStackTrace()
        } catch (IOException e) {
            e.printStackTrace()
        }

        if (bytearray != null)
            IOUtils.closeQuietly(bytearray)

        def data = os.toByteArray()
        def params = new LinkedMultiValueMap<String, String>()
        params.add("Content-Disposition", "inline;filename=Medicine-Administration.pdf")
        return new ResponseEntity(data, params, HttpStatus.OK)
    }

    @RequestMapping(value = '/medication',produces = 'application/pdf')
    ResponseEntity<AnyDocument.Any> medication(@RequestParam("parentCase") String parentCase,@RequestParam('empId') UUID empId) {

        def caseDto = caseRepository.findById(UUID.fromString(parentCase)).get()

        def res = applicationContext?.getResource("classpath:/reports/pms/medications.jasper")
        def bytearray = new ByteArrayInputStream()
        def os = new ByteArrayOutputStream()
        def emp = employeeRepository.findById(empId).get()
        def logo = applicationContext?.getResource("classpath:/reports/logo.png")
        def parameters = [:] as Map<String, Object>
        def chkchecked = applicationContext.getResource("classpath:/reports/check.png")
        def chkunchecked = applicationContext.getResource("classpath:/reports/uncheck.png")

        def age = Period.between(caseDto.patient.dob, LocalDate.now()).years
        def formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
        def dobFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy")

        def dto = new DischargeInstructionDto(
                patientFullName: caseDto?.patient?.fullName,
                pin: caseDto?.patient?.patientNo,
                caseNo: caseDto?.caseNo,
                roomNo: caseDto?.room?.roomNo?:"OPD",
                age: age,
                gender: caseDto?.patient?.gender?.toLowerCase(),
                address: caseDto?.patient?.fullAddress,
                admittingDiagnosis: caseDto?.admittingDiagnosis,
                dob: dobFormat.format(caseDto?.patient?.dob),
                preparedByFullName: emp?.fullName,
                attendingPhysician:caseDto?.admittingPhysician?.fullName,
                dateAdmitted :caseDto?.admissionDatetime ? formatter.format(caseDto?.admissionDatetime) : '--',
                dateDischarged: caseDto?.dischargedDatetime ? formatter.format(caseDto?.dischargedDatetime) : '--',
                civilStatus:caseDto?.patient?.civilStatus,
                date:formatter.format(Instant.now())
        )

        String signature

        if (emp.signature1) {
            signature = emp.signature1.split(",")[1]
        } else if (emp.signature2) {
            signature = emp.signature2.split(",")[1]
        } else if (emp.signature3) {
            signature = emp.signature3.split(",")[1]
        } else {
            signature = ""
        }
        BufferedImage image = null

        if (signature) {
            byte[] imageBytes = DatatypeConverter.parseBase64Binary(signature)

            image = ImageIO.read(new ByteArrayInputStream(imageBytes))
        }
        def gson = new Gson()
        def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
        def dataSource = new JsonDataSource(dataSourceByteArray)

        def medicationItems = medicationRepository.getMedicationsByCase(UUID.fromString(parentCase))

//		def items =  administrationRepository.getMedicationAdministrationsByCase(UUID.fromString(parentCase)).sort {
//			it.entryDateTime
//		}
        DateTimeFormatter dateFormat =
                DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm").withZone(ZoneId.systemDefault())

        ArrayList<AdministrationReport> itemsDto = new ArrayList<>()
        if (medicationItems) {
            medicationItems.each {
                it ->
                    def itemDto = new AdministrationReport(
                            medicine: it.medicine.descLong,
                            entryDateTime: dateFormat.format(it.entryDateTime),
                            employee: "",
                            dose: it.dose,
                            remarks: it.remarks
                    )
                    itemsDto.add(itemDto)
            }
        }

        if (itemsDto) {
            parameters.put('items', new JRBeanCollectionDataSource(itemsDto))
        }
//
//		if (logo.exists()) {
//			parameters.put("logo", logo?.getURL())
//		}

        if (chkchecked.exists()) {
            parameters.put("check", chkchecked.getURL())
        }

        if (image) {
            parameters.put('preparedBySignature', image)
        }

        if (chkunchecked.exists()) {
            parameters.put("uncheck", chkunchecked.getURL())
        }

        if(hospitalConfigService?.hospitalInfo){
            parameters.put('hospitalname', hospitalConfigService?.hospitalInfo?.hospitalName?:"")
            String address = ""

            address +=  hospitalConfigService?.hospitalInfo?.address
            address +=  hospitalConfigService?.hospitalInfo?.addressLine2

            parameters.put('hospitaladdress', address)
        }

        try {
            def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, dataSource)

            def pdfExporter = new JRPdfExporter()

            def outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(os)

            pdfExporter.setExporterInput(new SimpleExporterInput(jrprint))
            pdfExporter.setExporterOutput(outputStreamExporterOutput)
            def configuration = new SimplePdfExporterConfiguration()
            pdfExporter.setConfiguration(configuration)
            pdfExporter.exportReport()

        } catch (JRException e) {
            e.printStackTrace()
        } catch (IOException e) {
            e.printStackTrace()
        }

        if (bytearray != null)
            IOUtils.closeQuietly(bytearray)

        def data = os.toByteArray()
        def params = new LinkedMultiValueMap<String, String>()
        params.add("Content-Disposition", "inline;filename=Medicine-Administration.pdf")
        return new ResponseEntity(data, params, HttpStatus.OK)
    }

    @RequestMapping(value = ['/downloadIpdReports'])
    ResponseEntity<AnyDocument.Any> downloadIpdReports(
            @RequestParam("start") String start,
            @RequestParam("end") String end
    ) {
        StringBuffer buffer = new StringBuffer()
        List<String> headers = [
                "patient_no",
                "Case Number",
                "Admission Date",
                "Admission Time",
                "Admission Date and Time",
                "Lastname",
                "Firstname",
                "Middlename",
                "Suffix",
                "Year",
                "Month",
                "Day",
                "Birth Date",
                "Age",
                "Age_Years",
                "Age_Month",
                "Age_Day",
                "Gender",
                "Address",
                "Membership",
                "Admitting Physician",
                "Attending Physician",
                "Discharge Date and Time",
                "Discharge Date ",
                "Discharge Time",
                "Disposition",
                "Condition",
                "Final Diagnosis",
                "district"
        ]

        CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV
                .withHeader(*headers))
        String query = """
						select * 
						from pms.inpatient_report_pamela 
						where to_date("Admission Date", 'MM/DD/YYYY') 
						between to_date(${start}, 'MM/DD/YYYY') + time '00:00' 
						and to_date(${end}, 'MM/DD/YYYY') + time '23:59'
						order by "Lastname" asc
					"""

        def qResult = jdbcTemplate.queryForList(query)

        qResult.each {
            csvPrinter.printRecord(
                    it.patient_no,
                    it."Case Number",
                    it."Admission Date",
                    it."Admission Time",
                    it."Admission Date and Time",
                    it."Lastname",
                    it."Firstname",
                    it."Middlename",
                    it."Suffix",
                    it."Year",
                    it."Month",
                    it."Day",
                    it."Birth Date",
                    it."Age",
                    it."Age_Years",
                    it."Age_Month",
                    it."Age_Day",
                    it."Gender",
                    it."Address",
                    it."Membership",
                    it."Admitting Physician",
                    it."Attending Physician",
                    it."Discharge Date and Time",
                    it."Discharge Date",
                    it."Discharge Time",
                    it."Disposition",
                    it."Condition",
                    it."Final Diagnosis",
                    it."district"
            )
        }

        def data = buffer.toString().getBytes(Charset.defaultCharset())
        def responseHeaders = new HttpHeaders()
        responseHeaders.setContentType(MediaType.TEXT_PLAIN)
        responseHeaders.setContentLength(data.length)
        responseHeaders.add("Content-Disposition", """attachment;filename=Inpatient_Report-${start}-${end}.csv""")


        return new ResponseEntity(data, responseHeaders, HttpStatus.OK)

    }

    @RequestMapping(value = ['/downloadIpdReportsXlsx'])
    ResponseEntity<byte[]> downloadIpdReportsXlsx(
            @RequestParam("start") String start,
            @RequestParam("end") String end
    ) {
        List<String> headers = [
                "patient_no",
                "Case Number",
                "Admission Date",
                "Admission Time",
                "Admission Date and Time",
                "Lastname",
                "Firstname",
                "Middlename",
                "Suffix",
                "Year",
                "Month",
                "Day",
                "Birth Date",
                "Age",
                "Age_Years",
                "Age_Month",
                "Age_Day",
                "Gender",
                "Address",
                "Membership",
                "Admitting Diagnosis",
                "Admitting Physician",
                "Attending Physician",
                "Discharge Date and Time",
                "Discharge Date ",
                "Discharge Time",
                "Disposition",
                "Condition",
                "Final Diagnosis",
                "district"
        ]
        Workbook workbook = new XSSFWorkbook()
        Sheet sheet = workbook.createSheet()
        Font headerFont = workbook.createFont()
        headerFont.bold = true
        headerFont.fontHeightInPoints = (short) 14
        headerFont.color = IndexedColors.RED.index
        CellStyle headerCellStyle = workbook.createCellStyle()
        headerCellStyle.font = headerFont
        Row headerRow = sheet.createRow(0)

        for (int i in 0..headers.size() - 1){
            Cell cell = headerRow.createCell(i)
            cell.cellValue = headers[i]
            cell.cellStyle = headerCellStyle
        }

        String query = """select 
							"patient_no",
							"Case Number",
							"Admission Date",
							"Admission Time",
							"Admission Date and Time",
							"Lastname",
							"Firstname",
							"Middlename",
							"Suffix",
							"Year",
							"Month",
							"Day",
							"Birth Date",
							"Age",
							"Age_Years",
							"Age_Month",
							"Age_Day",
							"Gender",
							"Address",
							"Membership",
							"Admitting Diagnosis",
							"Admitting Physician",
							"Attending Physician",
							"Discharge Date and Time",
							"Discharge Date ",
							"Discharge Time",
							"Disposition",
							"Condition",
							"Final Diagnosis",
							"district"
							from pms.inpatient_report_pamela 
							where to_date("Admission Date", 'MM/DD/YYYY') 
							between to_date($start, 'MM/DD/YYYY') + time '00:00' 
							and to_date($end, 'MM/DD/YYYY') + time '23:59'
							order by "Admission Date" asc"""

        def qResult = jdbcTemplate.queryForList(query)

        int rowNum = 1

        qResult.each {
            Row row = sheet.createRow(rowNum++)
            def keys = it.keySet()

            if(keys){
                keys.eachWithIndex{ String entry, int i ->
                    row.createCell(i).cellValue = it[entry]
                }
            }
        }
        for(int i in 0..headers.size() - 1){
            sheet.autoSizeColumn(i)
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream()
        def responseHeaders = new HttpHeaders()
        try {
            responseHeaders.add("Content-Type", "application/vnd.ms-excel")
            responseHeaders.add("Content-Disposition", """attachment;filename=Inpatient_Report-${start}-${end}.xlsx""")

            workbook.write(buffer)
            return new ResponseEntity<>(buffer.toByteArray(), responseHeaders, HttpStatus.OK)
        } catch (Exception e) {
            e.printStackTrace()
            throw e
        }
    }


    @RequestMapping(value = ['/downloadIpdReportsXlsxMRD'])
    ResponseEntity<byte[]> downloadIpdReportsXlsxMRD(
            @RequestParam("start") String start,
            @RequestParam("end") String end
    ) {
        List<String> headers = [
                "Case Number",
                "Admission Date and Time",
                "Firstname",
                "Middlename",
                "Lastname",
                "Birth Date",
                "Age",
                "Gender",
                "Address",
                "Membership",
                "Admitting Diagnosis",
                "Admitting Physician",
                "Attending Physician",
                "Discharge Date and Time",
                "Disposition",
                "Final Diagnosis"
        ]
        Workbook workbook = new XSSFWorkbook()
        Sheet sheet = workbook.createSheet()
        Font headerFont = workbook.createFont()
        headerFont.bold = true
        headerFont.fontHeightInPoints = (short) 14
        headerFont.color = IndexedColors.RED.index
        CellStyle headerCellStyle = workbook.createCellStyle()
        headerCellStyle.font = headerFont
        Row headerRow = sheet.createRow(0)

        for (int i in 0..headers.size() - 1){
            Cell cell = headerRow.createCell(i)
            cell.cellValue = headers[i]
            cell.cellStyle = headerCellStyle
        }

        String query = """select
                            mrd."Case Number", 
                            mrd."Admission Date and Time",
                            mrd."Firstname", mrd."Middlename",
                            mrd."Lastname", 
                            mrd."Birth Date",
                            mrd."Age",
                            mrd."Gender",
                            mrd."Address",
                            mrd."Membership",
                            mrd."Admitting Diagnosis",
                            mrd."Admitting Physician",
                            mrd."Attending Physician",
                            mrd."Discharge Date and Time",
                            mrd."Disposition",
                            mrd."Final Diagnosis"
                         from pms.inpatient_report_mrd mrd
							where to_date(mrd."Admission Date", 'MM/DD/YYYY') 
							between to_date($start, 'MM/DD/YYYY') + time '00:00' 
							and to_date($end, 'MM/DD/YYYY') + time '23:59'
							order by mrd."Admission Date" asc"""

        def qResult = jdbcTemplate.queryForList(query)

        int rowNum = 1

        qResult.each {
            Row row = sheet.createRow(rowNum++)
            def keys = it.keySet()

            if(keys){
                keys.eachWithIndex{ String entry, int i ->
                    row.createCell(i).cellValue = it[entry]
                }
            }
        }
        for(int i in 0..headers.size() - 1){
            sheet.autoSizeColumn(i)
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream()
        def responseHeaders = new HttpHeaders()
        try {
            responseHeaders.add("Content-Type", "application/vnd.ms-excel")
            responseHeaders.add("Content-Disposition", """attachment;filename=Inpatient_Report_MRD-${start}-${end}.xlsx""")

            workbook.write(buffer)
            return new ResponseEntity<>(buffer.toByteArray(), responseHeaders, HttpStatus.OK)
        } catch (Exception e) {
            e.printStackTrace()
            throw e
        }
    }

}
