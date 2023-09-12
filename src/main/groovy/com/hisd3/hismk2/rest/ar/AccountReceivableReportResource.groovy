package com.hisd3.hismk2.rest.ar

import com.google.gson.Gson
import com.hisd3.hismk2.domain.accounting.AccReceivableGroupParam
import com.hisd3.hismk2.domain.accounting.BsPhilClaims
import com.hisd3.hismk2.domain.billing.BillingItemType
import com.hisd3.hismk2.domain.billing.CompanyAccount
import com.hisd3.hismk2.graphqlservices.accounting.AccountReceivableReportsService
import com.hisd3.hismk2.graphqlservices.accounting.AccountReceivableServices
import com.hisd3.hismk2.graphqlservices.accounting.BillingDeductionSummary
import com.hisd3.hismk2.graphqlservices.accounting.BillingScheduleServices
import com.hisd3.hismk2.graphqlservices.accounting.BsPhilClaimsServices
import com.hisd3.hismk2.graphqlservices.accounting.ReceivableDetailedList
import com.hisd3.hismk2.graphqlservices.billing.CompanyAccountServices
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.repository.accounting.AccountReceivableCompanyRepository
import com.hisd3.hismk2.repository.accounting.AccountReceivableRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.inventory.SupplierRepository
import com.hisd3.hismk2.rest.dto.BillingScheduleDto
import com.hisd3.hismk2.rest.dto.ManualBillingDto
import com.hisd3.hismk2.rest.dto.BillingScheduleSignDto
import com.hisd3.hismk2.rest.dto.BillingScheduleFieldsDto
import com.hisd3.hismk2.security.SecurityUtils
import groovy.json.JsonSlurper
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLArgument
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
import org.apache.commons.lang3.StringUtils
import org.apache.xmlbeans.impl.xb.xsdschema.AnyDocument
import org.hibernate.query.NativeQuery
import org.hibernate.transform.Transformers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import javax.persistence.EntityManager
import java.sql.ResultSet
import java.sql.SQLException
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Canonical
class ArSoaItem {
	
	String folio, patient, description
	BigDecimal amount
	
}

@Canonical
class AgingDto {
	
	BigDecimal current = BigDecimal.ZERO
	BigDecimal _1to30 = BigDecimal.ZERO
	BigDecimal _31to60 = BigDecimal.ZERO
	BigDecimal _61to90 = BigDecimal.ZERO
	BigDecimal _91 = BigDecimal.ZERO
	String folio = ""
	String patient = ""
	String dischargeDate = ""
	String status = ""
	
}


@Canonical
class AgingARCompanyDto {
	String company = ""
	String current = "-"
	String _1to30 = "-"
	String _31to60 = "-"
	String _61to90 = "-"
	String _91 = "-"
	String older = "-"
	String description = ""
	String date = ""
	String status = ""

	BigDecimal currentnum = BigDecimal.ZERO
	BigDecimal _1to30num = BigDecimal.ZERO
	BigDecimal _31to60num = BigDecimal.ZERO
	BigDecimal _61to90num = BigDecimal.ZERO
	BigDecimal _91num = BigDecimal.ZERO
	BigDecimal oldernum = BigDecimal.ZERO



}


@Canonical
class AgingDetailsDto {

	String title = ""
	String subtitle = ""
	String subHeader =""
}

@Canonical
class ArAgingReportPerPatient {
	Date billing_date
	String soa_no
	String arno
	String patient
	Date due_date
	BigDecimal current_days
	BigDecimal	day_1_to_30
	BigDecimal day_31_to_60
	BigDecimal day_61_to_90
	BigDecimal day_91_to_120
	BigDecimal day_older
	BigDecimal total

}


@Canonical
class ArPhilHealthReportPerPatient {
	Date discharged_date
	Date admitted_date
	String soa_no
	String folio
	String patient
	String icdRvs_code
	BigDecimal hci
	BigDecimal pf
	BigDecimal total
}

@Canonical
class ArDetailedListReport {
	Date discharged_date
	String soa_no
	String folio
	String patient
	BigDecimal hci
	BigDecimal pf
	BigDecimal total
}

@Canonical
class BsPhilClaimsRecord {

	String claimSeriesLhio = ""
	String claimNumber = ""
	String voucher =""
	String patient = ""
	String caseNo =""
	String status =""
	String arNo =""
	String transactionDate
	String folio = ""
	String admissionDate
	String dischargedDate
	String invoiceDate
	BigDecimal hci = 0.00
	BigDecimal pf = 0.00
	BigDecimal claimAmount = 0.00
}

@RestController
@RequestMapping("/arreports")
class AccountReceivableReportResource {
	
	@Autowired
	CompanyAccountServices companyAccountServices

	@Autowired
	AccountReceivableCompanyRepository accountReceivableCompanyRepository

	@Autowired
	AccountReceivableServices accountReceivableServices

	@Autowired
	EmployeeRepository employeeRepository

	@Autowired
	BillingScheduleServices billingScheduleServices

	@Autowired
	ApplicationContext applicationContext

	@Autowired
	HospitalConfigService hospitalConfigService

	@Autowired
	SupplierRepository supplierRepository

	@Autowired
	AccountReceivableRepository accountReceivableRepository

	@Autowired
	BsPhilClaimsServices bsPhilClaimsServices

	@Autowired
	JdbcTemplate jdbcTemplate

	@Autowired
	EntityManager entityManager

	@Autowired
	AccountReceivableReportsService accountReceivableReportsService

	@RequestMapping(value = "/printAgingForAR", produces = ["application/pdf"])
	ResponseEntity<byte[]> printAgingForAR(
			@RequestParam UUID companyAccountId
	) {
		def companyAccount = companyAccountServices.findOne(companyAccountId)
		
		def hospitalInfo = hospitalConfigService.hospitalInfo
		def res = applicationContext.getResource("classpath:/reports/ar/agingreport.jasper")
		def os = new ByteArrayOutputStream()
		def parameters = [:] as Map<String, Object>
		
		def logo = applicationContext.getResource("classpath:/reports/logo.jpg")
		
		parameters.put("logo", logo.inputStream)
		parameters.put("hospitalname", hospitalInfo?.hospitalName ?: "No hospital name")
		
		def fulladdress = (hospitalInfo?.address ?: "") + " " +
				(hospitalInfo?.addressLine2 ?: "") + "\n" +
				(hospitalInfo?.city ?: "") + " " +
				
				(hospitalInfo?.zip ?: "") + " " +
				(hospitalInfo?.country ?: "")
		
		parameters.put("hospitalfulladdress", fulladdress)
		
		parameters.put("contactline",
				"T: " + (hospitalInfo?.telNo ?: "No hospital contact") + " " +
						"E: " + (hospitalInfo?.email ?: "No hospital email")
		)
		
		parameters.put("companyName", companyAccount.companyname)

		def formatter = DateTimeFormatter.ofPattern("MMM-dd-yyyy")
		parameters.put("dateprinted", LocalDate.now().format(formatter))
		
		List<AgingDto> agingItems = []
		
		def allItems = companyAccountServices.getAllOutstandingClaimsList(companyAccountId.toString())
		
		allItems.each {
			bi ->
				
				def agingDto = new AgingDto()
				agingDto.patient = StringUtils.upperCase(bi.billing.patient.fullName)
				agingDto.dischargeDate = bi?.billing?.patientCase?.dischargedDatetime?.atZone(ZoneId.systemDefault())?.format(formatter) ?: ""
				agingDto.status = ""
				agingDto.folio = bi.billing.billingNo
				
				def daysDiff = ChronoUnit.DAYS.between(bi.transactionDate.atZone(ZoneId.systemDefault()).toLocalDateTime()
						, LocalDateTime.now())
				
				if (daysDiff == 0) {
					agingDto.current = bi.subTotal.abs()
				} else if (daysDiff >= 1 && daysDiff <= 30) {
					agingDto._1to30 = bi.subTotal.abs()
				} else if (daysDiff >= 31 && daysDiff <= 60) {
					agingDto._31to60 = bi.subTotal.abs()
				} else if (daysDiff >= 61 && daysDiff <= 90) {
					agingDto._61to90 = bi.subTotal.abs()
				} else {
					agingDto._91 = bi.subTotal.abs()
				}
				
				agingItems << agingDto
			
		}
		
		try {
			def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, new JRBeanCollectionDataSource(agingItems))
			
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
		params.add("Content-Disposition", "inline;filename=ARAging-\"" + (companyAccount?.companyname ?: "") + "\".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)
		
	}
	
	@RequestMapping(value = "/printSOAForAR", produces = ["application/pdf"])
	ResponseEntity<byte[]> printSOAForAR(
			@RequestParam UUID companyAccountId,
			@RequestParam(required = false) String recNo // comma separated
	) {
		
		def companyAccount = companyAccountServices.findOne(companyAccountId)
		
		def hospitalInfo = hospitalConfigService.hospitalInfo
		def res = applicationContext.getResource("classpath:/reports/ar/invoice.jasper")
		def os = new ByteArrayOutputStream()
		def parameters = [:] as Map<String, Object>
		//def logo = applicationContext.getResource("classpath:/reports/logo.jpg")
		def logo = applicationContext?.getResource("classpath:/reports/logo.png")

		//parameters.put("logo", logo.inputStream)
		if (logo.exists()) {
			parameters.put("logo", logo.inputStream)
		}
		parameters.put("hospitalname", hospitalInfo?.hospitalName ?: "No hospital name")
		
		def fulladdress = (hospitalInfo?.address ?: "") + " " +
				(hospitalInfo?.addressLine2 ?: "") + "\n" +
				(hospitalInfo?.city ?: "") + " " +
				
				(hospitalInfo?.zip ?: "") + " " +
				(hospitalInfo?.country ?: "")
		
		parameters.put("hospitalfulladdress", fulladdress)
		
		parameters.put("contactline",
				"Contact No: " + (hospitalInfo?.telNo ?: "No hospital contact") + " " +
						"Email: " + (hospitalInfo?.email ?: "No hospital email")
		)
		
		// companyAccount
		parameters.put("billtoValue", """${companyAccount.companyname}
${companyAccount.companyFullAddress}
""".toString())
		
		List<ArSoaItem> soaItemList = []
		
		BigDecimal totalInvoice = 0.0
		
		if (StringUtils.isBlank(recNo)) {
			
			def allItems = companyAccountServices.getAllOutstandingClaimsList(companyAccountId.toString())
			allItems.each {
				it ->
					
					String type = it.itemType == BillingItemType.DEDUCTIONS ? "Hospital" : "Doctor's Fee"
					soaItemList << new ArSoaItem(it.billing.billingNo, StringUtils.upperCase(it.billing?.patient?.fullName ?: "--"),
							type,
							it.subTotal.abs()
					)
					
					totalInvoice += it.subTotal.abs()
			}
		} else {
			def allItems = companyAccountServices.getAllOutstandingClaimsListFromRecno(companyAccountId.toString(),
					StringUtils.split(recNo, ",").toList())
			allItems.each {
				it ->
					
					String type = it.itemType == BillingItemType.DEDUCTIONS ? "Hospital" : "Doctor's Fee"
					soaItemList << new ArSoaItem(it.billing.billingNo, StringUtils.upperCase(it.billing?.patient?.fullName ?: "--"),
							type,
							it.subTotal.abs()
					)
					
					totalInvoice += it.subTotal.abs()
			}
			
		}
		
		parameters.put("invoiceAmount", totalInvoice)
		
		try {
			def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, new JRBeanCollectionDataSource(soaItemList))
			
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
		params.add("Content-Disposition", "inline;filename=ARSOA-\"" + (companyAccount?.companyname ?: "") + "\".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)
		
	}

	@RequestMapping(value = "/billing_schedule", produces = ["application/pdf"])
	ResponseEntity<byte[]> billing_schedule(
			@RequestParam UUID id
	) {
		//query
		def billSchedule = billingScheduleServices.findOne(id)
		def accRec = accountReceivableRepository.getARByBillingId(billSchedule.id)

		def res = applicationContext.getResource("classpath:/reports/ar/billingsched.jasper")
		def os = new ByteArrayOutputStream()
		def parameters = [:] as Map<String, Object>
		def billItemsDto = new ArrayList<BillingScheduleDto>()
		def billSignDto = new ArrayList<BillingScheduleSignDto>()

		def logo = applicationContext?.getResource("classpath:/reports/logo.png")
		def currentLogin = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
		DateTimeFormatter dateFormat2 = DateTimeFormatter.ofPattern("yyyy").withZone(ZoneId.systemDefault())

		SimpleDateFormat labelDateFormat1 = new SimpleDateFormat("MMMM dd, yyyy")
		SimpleDateFormat labelDateFormat2 = new SimpleDateFormat("MMMM, yyyy")


		if (logo.exists()) {
			parameters.put("logo", logo.inputStream)
		}

		def hmoAddress = billSchedule.companyAccount.companyname + "\n" +
				(billSchedule?.companyAccount?.companyFullAddress ?: "")


		Map<String, Map<String, Object>> parent = new HashMap<String ,Map<String, Object>>()
		ArrayList<String> dateRange =new ArrayList<String>()

		if(billSchedule.billingScheduleItems){
			billSchedule.billingScheduleItems.each {
				it ->
					def soa = dateFormat2.format(it.billing.createdDate)+'-'+it.billing.billingNo
					Map<String, Object> child = new HashMap<String ,Object>()
					if(it.isVoided == null || !it.isVoided){
						if(parent.containsKey(soa)){
							Map<String, Object> dupChild = parent.get(soa)
							if(it.type.equalsIgnoreCase('HCI')){
								def hci = dupChild.get("hci")
								hci = hci + it.totalAmount
								dupChild.put("hci",hci)
							}
							else{
								def pf = dupChild.get("pf")
								pf = pf + it.totalAmount
								dupChild.put("pf",pf)
							}

							def amount = dupChild.get("amount")
							amount = amount + it.totalAmount
							dupChild.put("amount",amount)
							parent.put(soa,dupChild)
						}
						else {
							child.put("transDate",it?.billing?.patientCase?.getDischargedDatetime() ? dateFormat.format(it.billing.patientCase.getDischargedDatetime()) : dateFormat.format(it.billingItem.transactionDate))
							def transDate  = it?.billing?.patientCase?.getDischargedDatetime() ? dateFormat.format(it.billing.patientCase.getDischargedDatetime()) : dateFormat.format(it.billingItem.transactionDate)

							child.put("soa",soa)
							child.put("patientName",it.billing.patient.fullName.toUpperCase())
							child.put("approvalCode",it.approvalCode ? it.approvalCode : "")

							if(it.type.equalsIgnoreCase('HCI')){
								child.put("hci",it.totalAmount)
								child.put("pf",0)
							}
							else{
								child.put("hci",0)
								child.put("pf",it.totalAmount)
							}
							child.put("amount",it.totalAmount)
							parent.put(soa,child)

							if(!dateRange.contains(transDate)){
								dateRange.add(transDate)
								Collections.sort(dateRange)
							}
						}
					}

			}
		}

		if(parent){
			parent.each {
				def billItems = new BillingScheduleDto(
						it.value
				)
				billItemsDto.add(billItems)
			}
		}

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy")
		LocalDateTime now = LocalDateTime.now()

		if(currentLogin.id != UUID.fromString("d9ebfc3d-dbb3-4500-8382-a50c4c9bcd83")){
			def position = currentLogin.positionType
			if(currentLogin.positionCode == 42)
				position = 'Accounting Staff'

			def firstSign = new BillingScheduleSignDto(
					signatureHeader: "Prepared by",
					signaturies:"${currentLogin.fullName.toUpperCase()} ${dtf.format(now)}",
					position: "${position}"
			)
			billSignDto.add(firstSign)
		}

		def secondSign = new BillingScheduleSignDto(
				signatureHeader: "Noted by",
				signaturies:"JOY Q. ESCABUSA ${dtf.format(now)}",
				position: "Billing Supervisor"
		)
		billSignDto.add(secondSign)


		parameters.put('sign_column', new JRBeanCollectionDataSource(billSignDto))

		if (billItemsDto) {
			parameters.put('items', new JRBeanCollectionDataSource(billItemsDto))
		}

		parameters.put('hmoAddress',hmoAddress)

		def dto = new BillingScheduleFieldsDto(
				date: labelDateFormat1.format(billSchedule.transactionDate),
				upperLabel: "Please find below the list of patients who have been admitted to our hospital as of "+labelDateFormat2.format(billSchedule.transactionDate),
				soaNo:"SOA No: ${billSchedule.billingScheduleNo.split("-", 2)[1]}",
				refNo:"AR No: ${accRec?.arNo ? accRec.arNo.split("-",2)[1] : ""}",
		)

		def gson = new Gson()
		def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
		def dataSource = new JsonDataSource(dataSourceByteArray)


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

		def data = os.toByteArray()
		def params = new LinkedMultiValueMap<String, String>()
		params.add("Content-Disposition", "inline;filename=Billing-schedule-of-\"" + billSchedule?.billingScheduleNo + "\".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)

	}

	@RequestMapping(value = "/printSOAFromAR", produces = ["application/pdf"])
	ResponseEntity<byte[]> printSOAFromAR(
			@RequestParam UUID billingSchedule,
			@RequestParam(required = false) String recNo // comma separated
	) {

//		def companyAccount = companyAccountServices.findOne(billingSchedule)
		def billSchedule = billingScheduleServices.findOne(billingSchedule)

		def hospitalInfo = hospitalConfigService.hospitalInfo
		def res = applicationContext.getResource("classpath:/reports/ar/invoice.jasper")
		def os = new ByteArrayOutputStream()
		def parameters = [:] as Map<String, Object>
		//def logo = applicationContext.getResource("classpath:/reports/logo.jpg")
		def logo = applicationContext?.getResource("classpath:/reports/logo.png")

		//parameters.put("logo", logo.inputStream)
		if (logo.exists()) {
			parameters.put("logo", logo.inputStream)
		}
		parameters.put("hospitalname", hospitalInfo?.hospitalName ?: "No hospital name")

		def fulladdress = (hospitalInfo?.address ?: "") + " " +
				(hospitalInfo?.addressLine2 ?: "") + "\n" +
				(hospitalInfo?.city ?: "") + " " +

				(hospitalInfo?.zip ?: "") + " " +
				(hospitalInfo?.country ?: "")

		parameters.put("hospitalfulladdress", fulladdress)

		parameters.put("contactline",
				"Contact No: " + (hospitalInfo?.telNo ?: "No hospital contact") + " " +
						"Email: " + (hospitalInfo?.email ?: "No hospital email")
		)

		// companyAccount
		parameters.put("billtoValue", """${billSchedule.companyAccount.companyname}\n ${billSchedule.companyAccount.companyFullAddress}
""".toString())

		List<ArSoaItem> soaItemList = []

		BigDecimal totalInvoice = 0.0

//		if (StringUtils.isBlank(recNo)) {

			billSchedule.billingScheduleItems.each {
				it ->
					String type = it.billingItem.itemType == BillingItemType.DEDUCTIONS ? "Hospital" : "Doctor's Fee"
					soaItemList << new ArSoaItem(it.billing.billingNo, StringUtils.upperCase(it.billing?.patient?.fullName ?: "--"),
							type,
							it.amount.abs()
					)

					totalInvoice += it.amount.abs()
			}

//		} else {
//			def allItems = companyAccountServices.getAllOutstandingClaimsListFromRecno(companyAccountId.toString(),
//					StringUtils.split(recNo, ",").toList())
//			allItems.each {
//				it ->
//
//					String type = it.itemType == BillingItemType.DEDUCTIONS ? "Hospital" : "Doctor's Fee"
//					soaItemList << new ArSoaItem(it.billing.billingNo, StringUtils.upperCase(it.billing?.patient?.fullName ?: "--"),
//							type,
//							it.subTotal.abs()
//					)
//
//					totalInvoice += it.subTotal.abs()
//			}
//
//		}

		parameters.put("invoiceAmount", totalInvoice)

		try {
			def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, new JRBeanCollectionDataSource(soaItemList))

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
		params.add("Content-Disposition", "inline;filename=ARSOA-\"" + (billSchedule?.billingScheduleNo ?: "") + "\".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)

	}


	@RequestMapping(value = "/printAgingForARPerCompany", produces = ["application/pdf"])
	ResponseEntity<byte[]> printAgingForARPerCompany(
			@RequestParam UUID companyAccountId,
			@RequestParam Date filterDAte
	) {
		def companyAccount = companyAccountServices.findOne(companyAccountId)

		def hospitalInfo = hospitalConfigService.hospitalInfo
		def res = applicationContext.getResource("classpath:/reports/ar/aragingreport.jasper")
		def os = new ByteArrayOutputStream()
		def parameters = [:] as Map<String, Object>

		def logo = applicationContext.getResource("classpath:/reports/logo.jpg")

		parameters.put("logo", logo.inputStream)
		parameters.put("hospitalname", "Allied Care Expert(Ace) Medical Center-Bohol, Inc 0368 Carlos P. Garcia East Avenue, Mansasa District \n" +
						"Tagbilaran City, Bohol, Philippines 6300 \n"+
						"Phone No: (038) 412 8888 \n"+
						"info@ace-mc-bohol.com")
		parameters.put("companyname", companyAccount.companyname)

		def fulladdress = (hospitalInfo?.address ?: "") + " " +
				(hospitalInfo?.addressLine2 ?: "") + "\n" +
				(hospitalInfo?.city ?: "") + " " +

				(hospitalInfo?.zip ?: "") + " " +
				(hospitalInfo?.country ?: "")

		parameters.put("hospitalfulladdress", fulladdress)

		parameters.put("contactline",
				"T: " + (hospitalInfo?.telNo ?: "No hospital contact") + " " +
						"E: " + (hospitalInfo?.email ?: "No hospital email")
		)

		parameters.put("companyName", companyAccount.companyname)
		def formatter = DateTimeFormatter.ofPattern("MMM-dd-yyyy")
		SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy")
		parameters.put("dateprinted", "As at "+LocalDate.now().format(formatter))
		parameters.put("description", "AR NUMBER")

		List<AgingARCompanyDto> agingCompanyItems = []
		def companyAR = accountReceivableServices.getARCompanyDetails(companyAccountId)
		NumberFormat money = NumberFormat.getInstance(Locale.ENGLISH)

		companyAR.each {
			it ->
				def agingARCompanyDto = new AgingARCompanyDto()
				agingARCompanyDto.description = StringUtils.upperCase(it.arNo)
				agingARCompanyDto.date = dateFormatter.format(it.transactionDate)
				agingARCompanyDto.status = ""
				def monthDiff = ChronoUnit.MONTHS.between(it.transactionDate.toLocalDate()
						, LocalDateTime.now())
				def amount = money.format(it.totals)


				if (monthDiff == 0) {
					def daysDiff = ChronoUnit.DAYS.between(it.transactionDate.toLocalDate()
							, LocalDateTime.now())
					if (daysDiff == 0) {
						agingARCompanyDto.current = amount
					}
					else {
						agingARCompanyDto._1to30 = amount

					}
				} else if (monthDiff == 1) {
					agingARCompanyDto._31to60 = amount
				} else if (monthDiff == 2) {
					agingARCompanyDto._61to90 = amount
				} else if (monthDiff == 3) {
					agingARCompanyDto._91 = amount
				} else if (monthDiff > 3) {
					agingARCompanyDto.older = amount
				}

				agingCompanyItems << agingARCompanyDto
		}

		try {
			def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, new JRBeanCollectionDataSource(agingCompanyItems))

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
		params.add("Content-Disposition", "inline;filename=ARAging-\"" + (companyAccount?.companyname ?: "") + "\".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)

	}

	@RequestMapping(value = "/printAgingARForAllComp", produces = ["application/pdf"])
	ResponseEntity<byte[]> printAgingARForAllComp() {

		def hospitalInfo = hospitalConfigService.hospitalInfo
		def res = applicationContext.getResource("classpath:/reports/ar/arallcompagingreport.jasper")
		def os = new ByteArrayOutputStream()
		def parameters = [:] as Map<String, Object>

		def logo = applicationContext.getResource("classpath:/reports/logo.jpg")

		parameters.put("logo", logo.inputStream)
		parameters.put("hospitalname", "Allied Care Expert(Ace) Medical Center-Bohol, Inc 0368 Carlos P. Garcia East Avenue, Mansasa District \n" +
				"Tagbilaran City, Bohol, Philippines 6300 \n"+
				"Phone No: (038) 412 8888 \n"+
				"info@ace-mc-bohol.com")

		def fulladdress = (hospitalInfo?.address ?: "") + " " +
				(hospitalInfo?.addressLine2 ?: "") + "\n" +
				(hospitalInfo?.city ?: "") + " " +

				(hospitalInfo?.zip ?: "") + " " +
				(hospitalInfo?.country ?: "")

		parameters.put("hospitalfulladdress", fulladdress)

		parameters.put("contactline",
				"T: " + (hospitalInfo?.telNo ?: "No hospital contact") + " " +
						"E: " + (hospitalInfo?.email ?: "No hospital email")
		)

		def formatter = DateTimeFormatter.ofPattern("MMM-dd-yyyy")
		SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy")
		parameters.put("dateprinted", "As at "+LocalDate.now().format(formatter))
		parameters.put("description", "AR NUMBER")

//		List<AgingDto> agingItems = []
		List<AgingARCompanyDto> agingCompanyItems = []
		def companyAR = accountReceivableCompanyRepository.findAll()
		NumberFormat money = NumberFormat.getInstance(Locale.ENGLISH)

		companyAR.each {
			it ->

				AgingARCompanyDto agingARCompanyDto = new AgingARCompanyDto()
				if(!it.accountReceivable.status.equalsIgnoreCase("voided")){

					agingARCompanyDto.company = it.companyAccount.companyname
					agingARCompanyDto.description = StringUtils.upperCase(it.accountReceivable.arNo)
					agingARCompanyDto.date = dateFormatter.format(it.accountReceivable.transactionDate)
					agingARCompanyDto.status = ""
					def monthDiff = ChronoUnit.MONTHS.between(it.accountReceivable.transactionDate.toLocalDate()
							, LocalDateTime.now())
					def amount = money.format(it.accountReceivable.totals)


					if (monthDiff == 0) {
						def daysDiff = ChronoUnit.DAYS.between(it.accountReceivable.transactionDate.toLocalDate()
								, LocalDateTime.now())
						if (daysDiff == 0) {
							agingARCompanyDto.current = amount
							agingARCompanyDto.currentnum = it.accountReceivable.totals
						}
						else {
							agingARCompanyDto._1to30 = amount
							agingARCompanyDto._1to30num = it.accountReceivable.totals

						}
					} else if (monthDiff == 1) {
						agingARCompanyDto._31to60 = amount
						agingARCompanyDto._31to60num = it.accountReceivable.totals
					} else if (monthDiff == 2) {
						agingARCompanyDto._61to90 = amount
						agingARCompanyDto._31to60num = it.accountReceivable.totals
					} else if (monthDiff == 3) {
						agingARCompanyDto._91 = amount
						agingARCompanyDto._91num = it.accountReceivable.totals
					} else if (monthDiff > 3) {
						agingARCompanyDto.older = amount
						agingARCompanyDto.oldernum = it.accountReceivable.totals
					}

					agingCompanyItems << agingARCompanyDto
				}
		}

		try {
			def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, new JRBeanCollectionDataSource(agingCompanyItems))

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
		params.add("Content-Disposition", "inline;filename=ARAging-summary.pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)

	}

	@RequestMapping(value = "/armanualbilling", produces = ["application/pdf"])
	ResponseEntity<byte[]> armanualbilling(
			@RequestParam UUID id
	) {
		//query
		def accRec = accountReceivableServices.findOne(id)
		def personalAccount = supplierRepository.findById(accRec.groups[AccReceivableGroupParam.PERSONAL_ACCOUNT_ID.name()]).get()

		def res = applicationContext.getResource("classpath:/reports/ar/armanualbill.jasper")
		def os = new ByteArrayOutputStream()
		def parameters = [:] as Map<String, Object>
		def manualItemsDto = new ArrayList<ManualBillingDto>()
		def billSignDto = new ArrayList<BillingScheduleSignDto>()

		def logo = applicationContext?.getResource("classpath:/reports/logo.png")
		def currentLogin = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()
		SimpleDateFormat labelDateFormat1 = new SimpleDateFormat("MMMM dd, yyyy")


		if (logo.exists()) {
			parameters.put("logo", logo.inputStream)
		}

		def hmoAddress = personalAccount.supplierFullname + "\n" +
				(personalAccount.primaryAddress ?: "")



		if(accRec.accountReceivableItems){
			accRec.accountReceivableItems.each {
				it ->

					def items = new ManualBillingDto()
					items.description = it.description
					items.charges = it.amount
					items.credits = 0
					items.balance = it.amount

					manualItemsDto.add(items)

					if(it.cwt){
						def itemTax = new ManualBillingDto()
						itemTax.description = "      -CREDITABLE WITHHOLDING TAX (Form 2307)"
						itemTax.charges = 0
						itemTax.credits = (it.amount * 0.05)
						itemTax.balance = - itemTax.credits
						manualItemsDto.add(itemTax)
					}
			}
		}

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy")
		LocalDateTime now = LocalDateTime.now()

		def firstSign = new BillingScheduleSignDto(
				signatureHeader: "Prepared by",
				signaturies:"${currentLogin.fullName.toUpperCase()} ${dtf.format(now)}",
				position: "Billing incharge"

		)
		billSignDto.add(firstSign)

		def secondSign = new BillingScheduleSignDto(
				signatureHeader: "Noted by",
				signaturies:"MA. ELISA S. CASTRODES, CPA ${dtf.format(now)}",
				position: "General Accountant"

		)
		billSignDto.add(secondSign)

		parameters.put('sign_column', new JRBeanCollectionDataSource(billSignDto))

		if (manualItemsDto) {
			parameters.put('items', new JRBeanCollectionDataSource(manualItemsDto))
		}

		parameters.put('hmoAddress',hmoAddress)

		def dto = new BillingScheduleFieldsDto(
				date: "Statement Date: ${labelDateFormat1.format(accRec.transactionDate)}",
				soaNo:"SOA No: ${accRec.referenceNo.split("-", 2).length > 1 ? accRec.referenceNo.split("-", 2)[1] : ""}",
				refNo:"AR No: ${accRec?.arNo ? accRec.arNo.split("-",2)[1] : ""}",
		)

		def gson = new Gson()
		def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
		def dataSource = new JsonDataSource(dataSourceByteArray)


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

		def data = os.toByteArray()
		def params = new LinkedMultiValueMap<String, String>()
		params.add("Content-Disposition", "inline;filename=Account-Receivable-of-\"" + accRec?.arNo + "\".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)

	}


	@RequestMapping(method = RequestMethod.GET, value = ["/report/ArAgingDetailsReport"])
	ResponseEntity<List<ArAgingReportPerPatient>> getArAgingPerPAtient(
			@RequestParam String dateFilter,
			@RequestParam String companyId
	)
	{
		List<ArAgingReportPerPatient> items = new ArrayList<ArAgingReportPerPatient>()
		if(companyId)
		{
			items = jdbcTemplate.query("""
			select *  from accounting.aragingreport(?::date,?::uuid)
			""", new RowMapper<ArAgingReportPerPatient>() {
					@Override
				ArAgingReportPerPatient mapRow(ResultSet rs, int rowNum) throws SQLException {

					return new ArAgingReportPerPatient(
							rs.getDate("billing_date"),
							rs.getString("soa_no"),
							rs.getString("arno"),
							rs.getString("patient"),
							rs.getDate("due_date"),
							rs.getBigDecimal("current_days"),
							rs.getBigDecimal("day_1_to_30"),
							rs.getBigDecimal("day_31_to_60"),
							rs.getBigDecimal("day_61_to_90"),
							rs.getBigDecimal("day_91_to_120"),
							rs.getBigDecimal("day_older")
					)
				}
			},
					dateFilter,
					companyId
			)
		}
		return new ResponseEntity(items, HttpStatus.OK)
	}


	@RequestMapping(value = "/report/ArAgingDetailsReport/print", produces = ["application/pdf"])
	ResponseEntity<byte[]> printARDetailsReport(
			@RequestParam String dateFilter,
			@RequestParam String companyId
	) {
		//query

		def res = applicationContext.getResource("classpath:/reports/ar/aragingdetail.jasper")
		def os = new ByteArrayOutputStream()
		def parameters = [:] as Map<String, Object>

		List<ArAgingReportPerPatient> items = new ArrayList<ArAgingReportPerPatient>()
		CompanyAccount company = new CompanyAccount()
		if(companyId)
		{
			company = companyAccountServices.findOne(UUID.fromString(companyId))
			items = jdbcTemplate.query("""
			select *  from accounting.aragingreport(?::date,?::uuid)
			""", new RowMapper<ArAgingReportPerPatient>() {
				@Override
				ArAgingReportPerPatient mapRow(ResultSet rs, int rowNum) throws SQLException {

					def details  = new ArAgingReportPerPatient(
							rs.getDate("billing_date"),
							rs.getString("soa_no"),
							rs.getString("arno"),
							rs.getString("patient"),
							rs.getDate("due_date"),
							rs.getBigDecimal("current_days"),
							rs.getBigDecimal("day_1_to_30"),
							rs.getBigDecimal("day_31_to_60"),
							rs.getBigDecimal("day_61_to_90"),
							rs.getBigDecimal("day_91_to_120"),
							rs.getBigDecimal("day_older"),
							rs.getBigDecimal("total")
					)
					return details
				}
			},
					dateFilter,
					companyId
			)
		}

		Date convertDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateFilter)
		LocalDate localConvertedDate = convertDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
		SimpleDateFormat labelDateFormat = new SimpleDateFormat("MMMM dd, yyyy")

		LocalDate convertedDate = LocalDate.parse(dateFilter, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
		convertedDate = convertedDate.withDayOfMonth(
				convertedDate.getMonth().length(convertedDate.isLeapYear()))

		if (items) {
			parameters.put('items', new JRBeanCollectionDataSource(items))
		}

		def subtitle
		def subHeader = "Monthly Aging of Accounts Receivable ${company?.companyType ? "from "+company.companyType.description: ""}"

		if(localConvertedDate == convertedDate){
			subtitle = "As of the month ended ${labelDateFormat.format(convertDate)}"
		}
		else {
			subtitle = "As of ${labelDateFormat.format(convertDate)}"
		}

		def dto = new AgingDetailsDto(
				title: "ACCOUNTS RECEIVABLE - ${company.companyname}",
				subtitle: subtitle,
				subHeader: subHeader,
		)

		def gson = new Gson()
		def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
		def dataSource = new JsonDataSource(dataSourceByteArray)

		try {
			def jrprint = JasperFillManager.fillReport(res.inputStream, parameters , dataSource)

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
		params.add("Content-Disposition", "inline;filename=AR-Aging-Details.pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)

	}

	@RequestMapping(method = RequestMethod.GET, value = ["/report/ArAgingDetailsReport/download"])
	ResponseEntity<AnyDocument.Any> getArAgingPerPatientDownloadCsv(
			@RequestParam String dateFilter,
			@RequestParam String companyId
	)
	{
		List<ArAgingReportPerPatient> items = new ArrayList<ArAgingReportPerPatient>()
		if(companyId)
		{
			items = jdbcTemplate.query("""
			select *  from accounting.aragingreport(?::date,?::uuid)
			""", new RowMapper<ArAgingReportPerPatient>() {
				@Override
				ArAgingReportPerPatient mapRow(ResultSet rs, int rowNum) throws SQLException {

					  	def details = new ArAgingReportPerPatient(
							rs.getDate("billing_date"),
							rs.getString("soa_no"),
							rs.getString("arno"),
							rs.getString("patient"),
							rs.getDate("due_date"),
							rs.getBigDecimal("current_days"),
							rs.getBigDecimal("day_1_to_30"),
							rs.getBigDecimal("day_31_to_60"),
							rs.getBigDecimal("day_61_to_90"),
							rs.getBigDecimal("day_91_to_120"),
							rs.getBigDecimal("day_older"))

					 	details.total = details.current_days + details.day_1_to_30 + details.day_31_to_60 + details.day_61_to_90 + details.day_91_to_120 + + details.day_91_to_120
						return details
				}
			},
					dateFilter,
					companyId
			)
		}


		StringBuffer buffer = new StringBuffer()

		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV.withHeader(
				"BILLING DATE",
				"SOA No.",
				"AR No.",
				"Patient",
				"Due Date",
				"Current",
				"1-30 Days",
				"31-60 Days",
				"61-90 Days",
				"91-120 Days",
				"Older"))

		try {
			items.each {
				item ->
					csvPrinter.printRecord(
							item.billing_date,
							item.soa_no,
							item.arno,
							item.patient,
							item.due_date,
							item.current_days,
							item.day_1_to_30,
							item.day_31_to_60,
							item.day_61_to_90,
							item.day_91_to_120,
							item.day_older
					)
			}

			LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
			extHeaders.add("Content-Disposition",
					"attachment;filename=AR_Aging_Per_Patient_Report_${dateFilter.replace("-", "_")}.csv".toString())
			return new ResponseEntity(buffer.toString().getBytes(), extHeaders, HttpStatus.OK)
		}
		catch (e) {
			throw e
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = ["/report/ArPhilHealthReport/download"])
	ResponseEntity<AnyDocument.Any> getArPhilHealthReportPdf(
			@RequestParam String startDate,
			@RequestParam String endDate
	)
	{
		List<ArPhilHealthReportPerPatient> philReport = new ArrayList<ArPhilHealthReportPerPatient>()

		if(startDate && endDate){
			philReport = jdbcTemplate.query(""" 
					select * from accounting.arphilhealthreport(?::date, ?::date, ?::uuid) 
			""", new RowMapper<ArPhilHealthReportPerPatient>() {
					@Override
					ArPhilHealthReportPerPatient mapRow(ResultSet rs, int rowNum) throws SQLException {
						def icds = "ICD: "
						def rvs = "RVS: "
						def docIcd = new JsonSlurper().parseText(rs.getString("icdcode"))
						docIcd.each {
							icds = icds + it['diagnosisCode'] + ', '
						}
						def docRvs = new JsonSlurper().parseText(rs.getString("rvscode"))
						docRvs.each {
							rvs = rvs + it['rvsCode'] + ', '
						}

						def code = ""
						if(docIcd){
							code = code + icds
						}
						if(docRvs){
							if(docIcd){
								code = code +" / "
							}
							code = code + rvs
						}

						SimpleDateFormat labelDateFormat = new SimpleDateFormat("yyyy")
						def entry = rs.getDate("entrydate")
						if(rs.getString("soano")){
							soa = labelDateFormat.format(entry).toString() +"-"+ rs.getString("soano")
						}
						else{
							soa = labelDateFormat.format(entry).toString() +"-"+ rs.getString("folio")
						}


						def details = new ArPhilHealthReportPerPatient(
								rs.getDate("dischargeddate"),
								rs.getDate("admitteddate"),
								soa,
								rs.getString("folio"),
								rs.getString("patient"),
								code ,
								rs.getBigDecimal("hci"),
								rs.getBigDecimal("pf"),
								rs.getBigDecimal("pf")+rs.getBigDecimal("hci")
						)

						return  details

					}
				},
				startDate,
				endDate,
				'179c6365-4fd6-4963-b54a-9de01598619b'
			)
		}

		StringBuffer buffer = new StringBuffer()

		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV.withHeader(
				"Admitted Date",
				"Discharged Date",
				"Soa",
				"Folio",
				"Patient",
				"Icd/Rvs code",
				"HCI",
				"PF",
				"Total"))

		try {
			philReport.each {
			item ->
				csvPrinter.printRecord(
						item.admitted_date,
						item.discharged_date,
						item.soa_no,
						item.folio,
						item.patient,
						item.icdRvs_code,
						item.hci,
						item.pf,
						item.total,
				)
		}

		LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
		extHeaders.add("Content-Disposition",
				"attachment;filename=AR_Philhealth_Report_${startDate.replace("-", "_")}.csv".toString())
		return new ResponseEntity(buffer.toString().getBytes(), extHeaders, HttpStatus.OK)
		}
		catch (e) {
			throw e
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = ["/report/arDetailedSubsidiaryReport/download"])
	ResponseEntity<AnyDocument.Any> getArDetailedSubsidiaryReport(
			@RequestParam String startDate,
			@RequestParam String endDate,
			@RequestParam String accountId,
			@RequestParam String searchFilter,
			@RequestParam String status,
			@RequestParam Integer pageSize

	)
	{

		StringBuffer buffer = new StringBuffer()

		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV.withHeader(
				"Discharged Date",
				"Soa",
				"Folio",
				"Patient",
				"ICD",
				"RVS",
				"HCI",
				"PF",
				"Total",
				"Change"
		))

		try {

		if(startDate && endDate && accountId){
			def company = companyAccountServices.findOne(UUID.fromString(accountId))
			List<ReceivableDetailedList> reportInfList =  accountReceivableReportsService.receivableDetailedQuery(accountId,"%${searchFilter}%",startDate,endDate,status,0,pageSize)

				reportInfList.each {
					it ->
						csvPrinter.printRecord(
								it.dischargeDate,
								it.finalSoa,
								it.billingNo,
								it.patient,
								it.rvs,
								it.icd,
								it.hci,
								it.pf,
								it.balance,
								it.change ? 'yes' : 'no'
						)
				}


				LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
				extHeaders.add("Content-Disposition",
						"attachment;filename= ${company.companyname.replaceAll("[,.]","")} AR DETAILED LIST ${startDate.replace("-", "_")}_${endDate.replace("-", "_")}.csv".toString())
				return new ResponseEntity(buffer.toString().getBytes(), extHeaders, HttpStatus.OK)

			}
		}
		catch (e) {
			throw e
		}

	}

	@RequestMapping(method = RequestMethod.GET, value = ["/report/BsClaimsReport/download"])
	ResponseEntity<AnyDocument.Any> downloadBsClaims(
			@RequestParam String dateType,
			@RequestParam String from,
			@RequestParam String to,
			@RequestParam String sort,
			@RequestParam ArrayList<String> status
	)
	{

		List<BsPhilClaims> claims = bsPhilClaimsServices.bsPhilClaimsAll(dateType,from,to,sort,status)

		StringBuffer buffer = new StringBuffer()

		DateTimeFormatter formatter =
				DateTimeFormatter.ofPattern("YYYY").withZone(ZoneId.systemDefault())
		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV.withHeader(
				"Claim Series",
				"Claim Number",
				"AR No",
				"Patient",
				"Case No",
				"Transaction Date",
				"Discharged Date",
				"Billing Date",
				"Admission Date",
				"Invoice Date",
				"Hospital Claims",
				"PF Claims",
				"PHIC Approved Claim Amount",
				"Status"))

		try {

			claims.each {
				item ->
					csvPrinter.printRecord(
							item.claimSeriesLhio,
							item.claimNumber,
							item.arNo,
							item.patient.fullName,
							item.patientCase.caseNo,
							item.billing.entryDateTime,
							item.billing.entryDateTime,
							item.patientCase.dischargedDatetime,
							item.patientCase.admissionDatetime,
							item.processStage,
							item.hci,
							item.pf,
							item.claimAmount,
							item.status
					)
			}

			LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
			extHeaders.add("Content-Disposition",
					"attachment;filename=BS_Claims_Report.csv".toString())
			return new ResponseEntity(buffer.toString().getBytes(), extHeaders, HttpStatus.OK)
		}
		catch (e) {
			throw e
		}
	}

	@RequestMapping(value = "/report/bsPhicClaimsReport/print", produces = ["application/pdf"])
	ResponseEntity<byte[]> printBsClaimsReport(
			@RequestParam String dateType,
			@RequestParam String from,
			@RequestParam String to,
			@RequestParam String sort,
			@RequestParam ArrayList<String> status
	) {
		//query
		def res = applicationContext.getResource("classpath:/reports/ar/arphilreport.jasper")
		def os = new ByteArrayOutputStream()
		def parameters = [:] as Map<String, Object>
		def bsClaimsItems = new ArrayList<BsPhilClaimsRecord>()
		def currentLogin = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()
		List<BsPhilClaims> claims = bsPhilClaimsServices.bsPhilClaimsAll(dateType,from,to,sort,status)
		SimpleDateFormat labelDateFormat = new SimpleDateFormat("MMMM dd, yyyy")
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault())
		def logo = applicationContext?.getResource("classpath:/reports/logo.png")

		if (logo.exists()) {
			parameters.put("logo", logo.inputStream)
		}

		claims.each {
			it ->
				def claimColumn = new BsPhilClaimsRecord()
				claimColumn.claimSeriesLhio = it.claimSeriesLhio
				claimColumn.claimNumber = it.claimNumber
				claimColumn.patient = it.patient.fullName
				claimColumn.caseNo = it.patientCase.caseNo
				claimColumn.transactionDate = dateFormat.format(it.billingItemTransactionDate)
				if(it.claimNumber.split("-")[0].equalsIgnoreCase("IP")){
					claimColumn.dischargedDate = dateFormat.format(it.patientCase.dischargedDatetime)
					claimColumn.admissionDate = it.patientCase.admissionDatetime ? dateFormat.format(it.patientCase.admissionDatetime) : ""
				}
				else{
					claimColumn.dischargedDate = ""
					claimColumn.admissionDate = ""
				}
				claimColumn.invoiceDate = dateFormat.format(it.claimDateCreated)
				claimColumn.hci = it.hci ? it.hci : 0
				claimColumn.pf = it.pf ? it.pf : 0
				claimColumn.claimAmount = it.claimAmount
				claimColumn.folio = it.billing.billingNo
				claimColumn.voucher = it.voucherNo

				claimColumn.status = it.status
				bsClaimsItems.push(claimColumn)

		}
		if (bsClaimsItems) {
			parameters.put('items', new JRBeanCollectionDataSource(bsClaimsItems))
		}

		def dateLabel = ""
		if(dateType.equalsIgnoreCase("c.patientCase.dischargedDatetime")){
			dateLabel = "Discharged date"
		}
		else if(dateType.equalsIgnoreCase("c.patientCase.admissionDatetime")){
			dateLabel = "Admission date"
		}
		else if(dateType.equalsIgnoreCase("c.billingItemTransactionDate")){
			dateLabel = "Transaction date"
		}
		else if(dateType.equalsIgnoreCase("c.claimDateCreated")){
			dateLabel = "Invoice date"
		}

		def dto = new BillingScheduleFieldsDto(
				upperLabel: "From the ${dateLabel}  ${from} TO ${to}",
		)

		def billSignDto = new ArrayList<BillingScheduleSignDto>()
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		LocalDateTime now = LocalDateTime.now();

		def firstSign = new BillingScheduleSignDto(
				signatureHeader: "Prepared by",
				signaturies:"${currentLogin.fullName.toUpperCase()} ${dtf.format(now)}",
				position: "Billing incharge"

		)
		billSignDto.add(firstSign)

		def secondSign = new BillingScheduleSignDto(
				signatureHeader: "Noted by",
				signaturies:"MA. ELISA S. CASTRODES, CPA ${dtf.format(now)}",
				position: "General Accountant"

		)
		billSignDto.add(secondSign)

		parameters.put('sign_column', new JRBeanCollectionDataSource(billSignDto))




		def gson = new Gson()
		def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
		def dataSource = new JsonDataSource(dataSourceByteArray)

		try {
			def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, dataSource )

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
		params.add("Content-Disposition", "inline;filename=Bs-Phic-Claims.pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)

	}

	@RequestMapping(method = RequestMethod.GET, value = ["/billingdeductions/download"])
	ResponseEntity<AnyDocument.Any> downloadBillingDeductions(
			@RequestParam String id,
			@RequestParam String status,
			@RequestParam String startDate,
			@RequestParam String endDate
	)
	{
		List<BillingDeductionSummary> content = accountReceivableReportsService.billingDeductionsNativeQuery(startDate,endDate,'',id,status,0,0)
		CompanyAccount companyAccount = companyAccountServices.findOne(UUID.fromString(id))

		StringBuffer buffer = new StringBuffer()
		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV.withHeader(
				"NO",
				"DISCHARGED DATE",
				"SOA",
				"FOLIO",
				"PATIENT",
				"ICD",
				"RVS",
				"HCI",
				"PF",
				"TOTAL"))

		try {

			content.each {
				item ->
					csvPrinter.printRecord(
							item.recordNo,
							item.dischargedDate,
							item.soa,
							item.billingNo,
							item.patient,
							item.icd,
							item.rvs,
							item.hci,
							item.pf,
							item.total
					)
			}

			LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
			extHeaders.add("Content-Disposition",
					"attachment;filename=${companyAccount.companyname}-${startDate}-${endDate}.csv".toString())
			return new ResponseEntity(buffer.toString().getBytes(), extHeaders, HttpStatus.OK)
		}
		catch (e) {
			throw e
		}
	}
}
