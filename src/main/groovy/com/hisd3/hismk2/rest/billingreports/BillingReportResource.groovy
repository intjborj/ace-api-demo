package com.hisd3.hismk2.rest.billingreports

import com.hisd3.hismk2.domain.billing.BillingItem
import com.hisd3.hismk2.domain.billing.BillingItemDetailParam
import com.hisd3.hismk2.domain.billing.BillingItemStatus
import com.hisd3.hismk2.domain.billing.BillingItemType
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.graphqlservices.SoaGroupingService
import com.hisd3.hismk2.graphqlservices.billing.BillingItemServices
import com.hisd3.hismk2.graphqlservices.billing.BillingService
import com.hisd3.hismk2.graphqlservices.billing.CompanyAccountServices
import com.hisd3.hismk2.graphqlservices.billing.Salesreportitem
import com.hisd3.hismk2.graphqlservices.cashiering.PaymentTrackerServices
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.ancillary.ServiceRepository
import com.hisd3.hismk2.repository.hospital_config.OperationalConfigurationRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.inventory.ItemRepository
import com.hisd3.hismk2.security.SecurityUtils
import com.sun.org.apache.xpath.internal.operations.Bool
import groovy.json.JsonSlurper
import groovy.transform.Canonical
import net.sf.jasperreports.engine.JRException
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.export.SimpleExporterInput
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput
import net.sf.jasperreports.export.SimplePdfExporterConfiguration
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.lang3.BooleanUtils
import org.apache.commons.lang3.StringUtils
import org.apache.xmlbeans.impl.xb.xsdschema.AnyDocument
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

import java.sql.ResultSet
import java.sql.SQLException
import java.text.DecimalFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.stream.Stream

@Canonical
class SoaItems {
	
	String description
	BigDecimal totalCharges
	BigDecimal adjustments
	BigDecimal outpocket
	
	Boolean bold = false, boldAll = false, center = false,summaryline=false
}

@Canonical
class FolioPrintItems {
	
	String date, doctype, docno, description, qty, price, debit, credit, runningbal,
	       reference, category

	Integer ordering
	Boolean pnf,nonpnf;
	BigDecimal subtotal
	
}

@Canonical
class AddendumLine {

	String description
	String value
}



@Canonical
class RevenueAndReconciliationReportDto {
	UUID uuid
	String folio_no
	String case_no
	String soa_no
	String patient_name
	String registration_type
	String admission_date
	String discharge_date
	String transaction_date
	String maker_name
	String maker_section
	String maker_department
	String revenue_center
	String record_no
	String item_code
	String description
	Integer quantity
	BigDecimal price
	BigDecimal room_board
	BigDecimal drugs_medicine
	BigDecimal laboratory_diagnostic
	BigDecimal operating_room_fee
	BigDecimal supplies
	BigDecimal oxygen
	BigDecimal infusion_pump
	BigDecimal miscellaneous_services
	BigDecimal gross_sales_vat_exclusive
	BigDecimal vat_revenue
	BigDecimal vat_exempt
	BigDecimal vat
	BigDecimal liability_pf
	BigDecimal deduction_hci
	BigDecimal deduction_pf
	String shift_no
	String terminal
	String payment_reference_no
	String mode_of_payment
	BigDecimal payment_amount
	String remarks
	BigDecimal outstanding_balance
	BigDecimal running_balance

}


@RestController
@RequestMapping("/billingreports")
class BillingReportResource {
	
	@Autowired
	ApplicationContext applicationContext
	
	@Autowired
	HospitalConfigService hospitalConfigService
	
	@Autowired
	BillingService billingService
	
	@Autowired
	UserRepository userRepository
	
	@Autowired
	EmployeeRepository employeeRepository
	
	@Autowired
	CompanyAccountServices companyAccountServices
	
	@Autowired
	PaymentTrackerServices paymentTrackerServices
	
	@Autowired
	BillingItemServices billingItemServices
	
	@Autowired
	ServiceRepository serviceRepository
	
	@Autowired
	ItemRepository itemRepository
	
	@Autowired
	JdbcTemplate jdbcTemplate

	@Autowired
	SoaGroupingService soaGroupingService

	@Autowired
	OperationalConfigurationRepository operationalConfigurationRepository
	
	@RequestMapping(value = "/printDischargeSlip", produces = ["application/pdf"])
	ResponseEntity<byte[]> printDischargeSlip(
			@RequestParam UUID billingId
	) {
		def res = applicationContext.getResource("classpath:/reports/cashier/dischargeSlip.jasper")
		def os = new ByteArrayOutputStream()
		def billing = billingService.findOne(billingId)
		
		def parameters = [:] as Map<String, Object>
		
		parameters["patientname"] = StringUtils.upperCase(billing.patient?.fullName ?: "")
		parameters["datetime"] = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a").format(LocalDateTime.now())
		parameters["roomNo"] = billing.patientCase?.room?.roomNo
		
		def username = SecurityUtils.currentLogin()
		def user = userRepository.findOneByLogin(username)
		def emp = employeeRepository.findOneByUser(user)
		
		parameters["cashier"] = emp?.fullName ?: ""
		
		try {
			def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, new JRBeanCollectionDataSource([]))
			
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
		params.add("Content-Disposition", "inline;filename=DCSLIP-\"" + (billing?.patient?.fullName ?: "") + "\".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)
		
	}
	
	@RequestMapping(value = "/soa", produces = ["application/pdf"])
	ResponseEntity<byte[]> soa(
			@RequestParam UUID billingId
	) {
		def res = applicationContext.getResource("classpath:/reports/billing/soa.jasper")
		//def logo = applicationContext.getResource("classpath:/reports/logo.jpg")
		def logo = applicationContext?.getResource("classpath:/reports/logo.png")
		def os = new ByteArrayOutputStream()
		def yearFormat = DateTimeFormatter.ofPattern("yyyy")
		
		def hospitalInfo = hospitalConfigService.hospitalInfo
		
		if (!res.exists()) {
			return ResponseEntity.notFound().build()
		}
		
		def parameters = [:] as Map<String, Object>
		
		parameters.put("first_case_rate", "0.00")
		parameters.put("second_case_rate", "0.00")
		//parameters.put("logo", logo.inputStream)
		if (logo.exists()) {
			parameters.put("logo", logo?.inputStream)
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
		
		def billing = billingService.findOne(billingId)

		if(BooleanUtils.isNotTrue(billing.finalizedSoa)){
			parameters.put("soaref", "Temporary SOA Ref. No. ${billing.createdDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${billing.billingNo}".toString())
		}
		else {

			parameters.put("soaref", "SOA Ref. No. ${billing.createdDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${billing.finalSoa}".toString())

		}

		def format = DateTimeFormatter.ofPattern("MM/dd/yyyy")
		def formatdt = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")
		
		parameters.put("patient_fullname", StringUtils.upperCase(billing?.patient?.fullName ?: billing.otcname))
		parameters.put("hospitalizationplan", billing?.patientCase?.accommodationType ?: "")
		parameters.put("patient_address", billing?.patient?.fullAddress)
		
		if (billing.patient && billing.patientCase) {
			LocalDate l = billing.patient.dob
			LocalDate now = LocalDate.now() //gets localDate
			Period diff = Period.between(l, now) //difference between the dates is calculated
			
			parameters.put("age", diff.getYears() + "y " + diff.getMonths() + "m " + diff.getDays() + "d")
			
			parameters["datetime_admitted"] = billing?.patientCase?.admissionDatetime ?
					billing.patientCase.admissionDatetime.atZone(ZoneId.systemDefault()).format(formatdt) : ""
			
			parameters["date_discharged"] = billing?.patientCase?.dischargedDatetime ?
					billing.patientCase.dischargedDatetime.atZone(ZoneId.systemDefault()).format(formatdt) : ""
			
			parameters.put("attendingphysician", billing?.patientCase?.attendingPhysician?.fullName ?: "")
			parameters.put("admissionno", "${billing?.patientCase?.caseNo} / ${billing?.billingNo}".toString())
			parameters.put("roomno", billing?.patientCase?.room?.roomNo ?: "")
		}
		
		def jsonSlurper = new JsonSlurper()
		if (StringUtils.isNotBlank(billing?.patientCase?.primaryDx)) {
			
			def primary = jsonSlurper.parseText(billing?.patientCase?.primaryDx) as Map<String, Object>
			
			if (primary.containsKey("diagnosisCode"))
				parameters.put("first_case_rate", primary["diagnosisCode"])
			
			if (primary.containsKey("rvsCode"))
				parameters.put("first_case_rate", primary["rvsCode"])
			
		}
		
		if (StringUtils.isNotBlank(billing?.patientCase?.secondaryDx)) {
			
			def primary = jsonSlurper.parseText(billing?.patientCase?.secondaryDx) as Map<String, Object>
			
			if (primary.containsKey("diagnosisCode"))
				parameters.put("second_case_rate", primary["diagnosisCode"])
			
			if (primary.containsKey("rvsCode"))
				parameters.put("second_case_rate", primary["rvsCode"])
		}

		def primary
		def secondary
		def icdDiagnosis = ''
		def rvsDiagnosis = ''
		
		if (StringUtils.isNotBlank(billing?.patientCase?.icdDiagnosis)) { // always display primary

			icdDiagnosis = billing?.patientCase?.icdDiagnosis
			 primary = jsonSlurper.parseText(billing?.patientCase?.primaryDx)
			 secondary = jsonSlurper.parseText(billing?.patientCase?.secondaryDx)

		}
		
		if (StringUtils.isNotBlank(billing?.patientCase?.rvsDiagnosis)) { // display primary and secondary

			rvsDiagnosis = billing?.patientCase?.rvsDiagnosis
			 primary = jsonSlurper.parseText(billing?.patientCase?.primaryDx)
			 secondary = jsonSlurper.parseText(billing?.patientCase?.secondaryDx)

		}

		List<Map<String, Object>> icd = []
		List<Map<String, Object>> rvs = []
		if(icdDiagnosis) {
			icd = (List<Map<String, Object>>) jsonSlurper.parseText(icdDiagnosis)
		}

		if(rvsDiagnosis) {
			rvs = (List<Map<String, Object>>) jsonSlurper.parseText(rvsDiagnosis)
		}
		def primaryTag = ''
		def secondaryTag = ''

		String primaryDiagnosis = ''


		List<String> listStr = []



		icd.each {

			if(primary.diagnosisCode == it["diagnosisCode"].toString())
			{
				primaryTag = 'ICD';
			}
			if(secondary.diagnosisCode == it["diagnosisCode"].toString())
			{
				secondaryTag = 'ICD';
			}
		}

		rvs.each {
			if(it["rvsCode"].toString() == primary.rvsCode)
			{
				primaryTag = 'RVS';
			}
			if(it["rvsCode"].toString() == secondary.rvsCode)
			{
				secondaryTag = 'RVS';
			}
		}

		String icdPrimaryLabel = primary?.diagnosisCode + ' - ' + primary?.longName
		String icdSecondaryLabel = secondary?.diagnosisCode + ' - ' + secondary?.longName
		String rvsPrimaryLabel = primary?.rvsCode + ' - ' + primary?.longName
		String rvsSecondaryLabel = secondary?.rvsCode + ' - ' + secondary?.longName

		List<String> listRVS = []

		if(primaryTag.equalsIgnoreCase('RVS')){
			if(secondaryTag.equalsIgnoreCase('RVS')){
				listRVS << rvsPrimaryLabel
				listRVS << rvsSecondaryLabel
			}
			else if(secondaryTag.equalsIgnoreCase('ICD')){
				primaryDiagnosis = icdSecondaryLabel
				listRVS << rvsPrimaryLabel
			}else{
				listRVS << rvsPrimaryLabel
			}
		}
		else if (secondaryTag.equalsIgnoreCase('RVS')){
			 if(primaryTag.equalsIgnoreCase('ICD')){
				 primaryDiagnosis = icdPrimaryLabel
				 listRVS << rvsSecondaryLabel
			 }
		}
		else if(primaryTag.equalsIgnoreCase('ICD')){
			primaryDiagnosis = icdPrimaryLabel
			if(secondaryTag.equalsIgnoreCase('RVS')){
				listRVS << rvsSecondaryLabel
			}
		}


		parameters.put("final_diagnosis", primaryDiagnosis)

		parameters.put("secondary_diagnosis", listRVS.join("\n"))




		/*
		ICD
			 diagnosisCode
			 longName
			 primaryAmount1
			 primaryHospShare1
			 primaryProfShare1
			*/
		
		/*
		RVS
			 rvsCode
			 longName
			 primaryAmount1
			 primaryHospShare1
			 primaryProfShare1
		 */
		
		//   parameters.put("first_case_rate","")
		//   parameters.put("second_case_rate","")
		//   billing?.patientCase?.primaryDx
		//   billing?.patientCase?.secondaryDx
		//icdDiagnosis
		//rvsDiagnosis
		
		//   parameters.put("final_diagnosis","XXXXXXXXX")
		//    parameters.put("secondary_diagnosis","XXXXXXXXX")
		
		parameters.put("prepared_by", "N/A")
		parameters.put("prepared_date", "N/A")
		parameters.put("prepared_contact", "N/A")


		def opConfig = operationalConfigurationRepository.findAll().find()
		boolean isArrayNull = Stream.of(opConfig.contactNumbers).anyMatch({ element -> element == null })

		String activeContact = ''
		if(!isArrayNull) {
			opConfig?.contactNumbers.each{
				if (it?.active == true) {
					 activeContact = it?.contactNum
				}
			}
		}

		if (StringUtils.isNotBlank(billing.lockedBy)) {
			def user = userRepository.findOneByLogin(billing.lockedBy)
			def emp = employeeRepository.findOneByUser(user)


			parameters.put("prepared_by", emp.fullName?.toUpperCase())
			parameters.put("prepared_date", DateTimeFormatter.ofPattern("MM/dd/yyyy").format(LocalDate.now()))
			parameters.put("prepared_contact", activeContact)
		} else {
			def user = userRepository.findOneByLogin(SecurityUtils.currentLogin())
			def emp = employeeRepository.findOneByUser(user)
			
			parameters.put("prepared_by", emp.fullName?.toUpperCase())
			parameters.put("prepared_date", DateTimeFormatter.ofPattern("MM/dd/yyyy").format(LocalDate.now()))
			parameters.put("prepared_contact", activeContact)
			
		}
		
		//  parameters.put("member","XXXXXXXXX")
		// parameters.put("relation_to_member","XXXXXXXXX")
		
		//  parameters.put("conforme_contact","XXXXXXXXX")
		//   parameters.put("conforme_date","XXXXXXXXX")
		
		def items = [] as List<SoaItems>
		
		def line1 = null
		
		def hospitalTotal = 0.0
		def hospitalDeductions = 0.0
		if (billing.patientCase) {
			if (billing?.patientCase?.admissionDatetime == null)
				line1 = new SoaItems(("OPD Case #${billing.patientCase.caseNo} for ${billing.patient.fullName}").toUpperCase(),
						null,
						null,
						null)
			else
				line1 = new SoaItems(("Admission Case #${billing.patientCase.caseNo} for ${billing.patient.fullName} from ${billing?.patientCase?.admissionDatetime?.atZone(ZoneId.systemDefault())?.format(format) ?: ""} " +
						" to ${billing?.patientCase?.dischargedDatetime?.atZone(ZoneId.systemDefault())?.format(format) ?: ""}").toUpperCase(),
						null,
						null,
						null)
		}
		
		items.add(
				line1
		)
		
		items.add(new SoaItems("HCI CHARGES",
				null,
				null,
				null,
				true,
				true,
				true)
		)
		
		items.add(new SoaItems("ROOM ACCOMMODATIONS",
				null,
				null,
				null,
		)
		)
		
		def mapAmount = new HashMap<BigDecimal, Integer>()
		
		billing?.billingItemList?.findAll {
			it.itemType == BillingItemType.ROOMBOARD &&
					it.status == BillingItemStatus.ACTIVE
		}?.each {
			
			hospitalTotal += it.subTotal
			/*items.add(new SoaItems(it.qty + " days @" + new DecimalFormat("#,##0.00").format(it.subTotal),
					it.subTotal,
					null)
			)*/
			
			if (!mapAmount.containsKey(it.debit)) {
				mapAmount.put(it.debit, it.qty)
			} else {
				def currentCount = mapAmount.get(it.debit)
				mapAmount.put(it.debit, currentCount + it.qty)
			}
			
		}
		
		mapAmount.each {
			k, v ->
				items.add(new SoaItems(v + " days @ " + new DecimalFormat("#,##0.00").format(k),
						k * v,
						null))
		}
		
		def currentSubtotal = 0.0
		
		billing?.billingItemList?.findAll {
			it.itemType == BillingItemType.MEDICINES &&
					it.status == BillingItemStatus.ACTIVE
		}?.each {
			currentSubtotal += it.subTotal
			hospitalTotal += it.subTotal
		}
		
		items.add(new SoaItems("Drugs and Medicines".toUpperCase(),
				currentSubtotal,
				null)
		)
		
		currentSubtotal = 0.0
		
		billing?.billingItemList?.findAll {
			it.itemType == BillingItemType.DIAGNOSTICS &&
					it.status == BillingItemStatus.ACTIVE
		}?.each {
			currentSubtotal += it.subTotal
			hospitalTotal += it.subTotal
		}
		
		items.add(new SoaItems("Laboratory and Diagnostics".toUpperCase(),
				currentSubtotal,
				null)
		)
		
		currentSubtotal = 0.0
		
		billing?.billingItemList?.findAll {
			it.itemType == BillingItemType.ORFEE &&
					it.status == BillingItemStatus.ACTIVE
		}?.each {
			currentSubtotal += it.subTotal
			hospitalTotal += it.subTotal
		}
		
		items.add(new SoaItems("Operating Room".toUpperCase(),
				currentSubtotal,
				null)
		)

		//cath
		currentSubtotal = 0.0

		billing?.billingItemList?.findAll {
			it.itemType == BillingItemType.CATHLAB &&
					it.status == BillingItemStatus.ACTIVE
		}?.each {
			currentSubtotal += it.subTotal
			hospitalTotal += it.subTotal
		}

		items.add(new SoaItems("Catheterization Laboratory".toUpperCase(),
				currentSubtotal,
				null)
		)
		//end
		
		currentSubtotal = 0.0
		
		billing?.billingItemList?.findAll {
			it.itemType == BillingItemType.SUPPLIES &&
					it.status == BillingItemStatus.ACTIVE
		}?.each {
			currentSubtotal += it.subTotal
			hospitalTotal += it.subTotal
		}
		
		items.add(new SoaItems("Supplies".toUpperCase(),
				currentSubtotal,
				null)
		)
		
		items.add(new SoaItems("OTHERS",
				null,
				null,
				null,
				true,
				true,
				true)
		)
		

		
		def mapAmountOthers = new HashMap<String, Integer>()

	    def soaGroupings = soaGroupingService.getSoaGroupingsbyBillingId(billingId)

		soaGroupings.each {

			String description = it.groupName
			BigDecimal totalGroup = 0.0



			it.contents.each {bi->
				if(bi.status == BillingItemStatus.ACTIVE){
					hospitalTotal += bi.subTotal
					totalGroup += bi.subTotal
				}
			}

			items.add(new SoaItems(description ,
					totalGroup,
					null))

		}

		// implement the grouping system

		billing?.billingItemList?.findAll {
			it.itemType == BillingItemType.OTHERS &&
					it.status == BillingItemStatus.ACTIVE &&
					it.soaGrouping == null
		}?.each {
			hospitalTotal += it.subTotal
			
			String key = "${it.description}~***~${new DecimalFormat("0.00").format(it.debit)}"
			if (!mapAmountOthers.containsKey(key)) {
				mapAmountOthers.put(key, it.qty)
			} else {
				def currentCount = mapAmountOthers.get(key)
				mapAmountOthers.put(key, currentCount + it.qty)
			}
			
		}
		
		mapAmountOthers.each {
			k, v ->
				
				String[] parts = StringUtils.split(k, "~***~")
				String desc = parts[0]
				BigDecimal debit = new BigDecimal(parts[1])
				BigDecimal subTotal = debit * v
				
				if (v == 1) {
					items.add(new SoaItems(desc,
							subTotal,
							null))
				} else {
					items.add(new SoaItems(desc + " x " + v,
							subTotal,
							null))
				}
			
		}
		
		items.add(new SoaItems(null,
				null,
				null,
				null)
		)
		
		items.add(new SoaItems("TOTAL HCI",
				hospitalTotal,
				null,
				null,
				true,
				true,
				true)
		)
		
		items.add(new SoaItems("DEDUCTIONS HCI",
				null,
				null,
				null,
				true,
				true,
				true)
		)
		
		billing?.billingItemList?.findAll {
			it.itemType == BillingItemType.DEDUCTIONS &&
					it.status == BillingItemStatus.ACTIVE
		}?.sort(false) {
			a, b ->
				a.createdDate <=> b.createdDate
		}?.each {
			
			if (it.details.containsKey(BillingItemDetailParam.COMPANY_ACCOUNT_ID.name())) {
				
				def cid = it.details[BillingItemDetailParam.COMPANY_ACCOUNT_ID.name()] as String
				def companyAccount = companyAccountServices.findOne(UUID.fromString(cid))
				
				if (BooleanUtils.isNotTrue(companyAccount?.hideInSoa)) {
					
					hospitalDeductions += it.subTotal
					items.add(new SoaItems(it.description,
							null,
							it.subTotal,
							null)
					)
				}
			} else {
				hospitalDeductions += it.subTotal
				items.add(new SoaItems(it.description,
						null,
						it.subTotal,
						null)
				)
			}
			
		}
		
		items.add(new SoaItems("Hospital Balance".toUpperCase(),
				null,
				null,
				hospitalTotal + hospitalDeductions,
				true,
				true,
				true)
		)
		
		items.add(new SoaItems("PROFESSIONAL FEES",
				null,
				null,
				null,
				true,
				true,
				true)
		)
		
		def totalAllPf = 0.0
		def totalAllPfDeduct = 0.0
		billing?.billingItemList?.findAll {
			it.itemType == BillingItemType.PF &&
					it.status == BillingItemStatus.ACTIVE
		}?.each {
			
			doctorpf ->


				def PF_VAT_APPLIED = doctorpf.details[BillingItemDetailParam.PF_VAT_APPLIED.name()]
				def vatAmount = new BigDecimal(doctorpf.details[BillingItemDetailParam.PF_VAT_AMT.name()]?:"0.0")

				if(StringUtils.equalsIgnoreCase(PF_VAT_APPLIED,"YES")){
					boolean attending = StringUtils.containsIgnoreCase(doctorpf.description, billing?.patientCase?.attendingPhysician?.fullName)
					items.add(new SoaItems((attending ? "AP - " : "") + doctorpf.description,
							doctorpf.subTotal,
							null,
							null,
							attending,
							false,
							false)
					)

					totalAllPf += doctorpf.subTotal
				}
				else {


					boolean attending = StringUtils.containsIgnoreCase(doctorpf.description, billing?.patientCase?.attendingPhysician?.fullName)
					items.add(new SoaItems((attending ? "AP - " : "") + doctorpf.description,
							doctorpf.subTotal  + vatAmount,
							null,
							null,
							attending,
							false,
							false)
					)

					totalAllPf += (doctorpf.subTotal + vatAmount)

					if(vatAmount > 0.0){
						items.add(new SoaItems( "VAT EXEMPT",
								null,
								vatAmount * -1,
								null)
						)
					}
				}





				// Identify Vat Exempt


				
				def totalPFDeduction = 0.0
				
				billing?.billingItemList?.findAll {
					it.itemType == BillingItemType.DEDUCTIONSPF &&
							it.status == BillingItemStatus.ACTIVE && it.details[BillingItemDetailParam.PF_EMPLOYEEID.name()] == doctorpf.details[BillingItemDetailParam.PF_EMPLOYEEID.name()]
				}?.sort(false) {
					a, b ->
						a.createdDate <=> b.createdDate
				}?.each {
					
					if (it.details.containsKey(BillingItemDetailParam.COMPANY_ACCOUNT_ID.name())) {
						
						def cid = it.details[BillingItemDetailParam.COMPANY_ACCOUNT_ID.name()] as String
						def companyAccount = companyAccountServices.findOne(UUID.fromString(cid))
						
						if (BooleanUtils.isNotTrue(companyAccount?.hideInSoa)) {
							items.add(new SoaItems(it.description,
									null,
									it.subTotal,
									null)
							)
							totalPFDeduction += it.subTotal
						}
					} else {
						items.add(new SoaItems(it.description,
								null,
								it.subTotal,
								null)
						)
						totalPFDeduction += it.subTotal
					}
					
				}
				
				totalAllPfDeduct += totalPFDeduction
				items.add(new SoaItems("${doctorpf.description} - Balance".toUpperCase(),
						null,
						null,
						doctorpf.subTotal + totalPFDeduction)
				)
			
		}
		
		items.add(new SoaItems(" TOTAL PF",
				totalAllPf,
				null,
				null,
				true,
				true,
				true)
		)
		
		//	def hospitalBalance = hospitalTotal + hospitalDeductions
		//	def pfBalance = totalAllPf + totalAllPfDeduct
		//	def allPayments = 0.0
		// Payments
		
		def formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
		
		paymentTrackerServices.getPaymentsByBillingId(billingId).findAll {
			BooleanUtils.isNotTrue(it.voided)
		}.each {
			
			//	allPayments += it.totalpayments
			items.add(new SoaItems(StringUtils.upperCase("PAYMENTS: OR #${it.ornumber} ${formatter.format(it.createdDate.atZone(ZoneId.systemDefault()))}"),
					null,
					null,
					it.totalpayments * -1)
			)
		}

		// Get Application records

		Map<UUID,BigDecimal> applications = [:]

		billing.billingItemList.findAll { it.status == BillingItemStatus.ACTIVE &&
		it.itemType == BillingItemType.PAYMENTS &&
		it.details.containsKey(BillingItemDetailParam.APPLICATION.name())
		}.each {

			    def PAYMENTRACKERID = it.details[BillingItemDetailParam.PAYTRACKER_ID.name()]
			   if(PAYMENTRACKERID ){

				    if(!applications.containsKey(UUID.fromString(PAYMENTRACKERID)))
						applications[UUID.fromString(PAYMENTRACKERID)] = 0.0

				   applications[UUID.fromString(PAYMENTRACKERID)] = applications[UUID.fromString(PAYMENTRACKERID)] + it.subTotal.abs()
			   }
		}


		applications.each { k,v  ->

			def paymentTracker = paymentTrackerServices.findOne(k)
			items.add(new SoaItems(StringUtils.upperCase("Application: OR #${paymentTracker.ornumber} ${formatter.format(paymentTracker.createdDate.atZone(ZoneId.systemDefault()))}"),
					null,
					null,
					v * -1)
			)

		}

		// ==   ANNOTATION_PAYMENTS_GROUPS
		billing.billingItemList.findAll { it.status == BillingItemStatus.ACTIVE &&
				it.itemType == BillingItemType.ANNOTATION_PAYMENTS_GROUPS
		}.each {
			items.add(new SoaItems(StringUtils.upperCase( it.description),
					null,
					null,
					it.annotationAmount * -1)
			)


		}


		List<AddendumLine> notifications = []

		def addendums = billing.billingItemList.findAll { it.status == BillingItemStatus.ACTIVE &&
				it.itemType == BillingItemType.ANNOTATION_NOTIFICATION_GROUPS
		}


		if(addendums){
			notifications <<  new AddendumLine("Addendum","")
		}

		addendums.each {
			 notifications <<  new AddendumLine(it.description,new DecimalFormat("#,##0.00").format(it.annotationAmount))
		}

		parameters.put("addendum",new JRBeanCollectionDataSource(notifications))


		try {
			def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, new JRBeanCollectionDataSource(items))
			
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
		params.add("Content-Disposition", "inline;filename=SOA-\"" + (billing?.patient?.fullName ?: "") + "\".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)
		
	}
	
	@RequestMapping(value = "/foliodetails", produces = ["application/pdf"])
	ResponseEntity<byte[]> foliodetails(
			@RequestParam UUID billingId
	) {
		
		def res = applicationContext.getResource("classpath:/reports/billing/ledgerfoliodetails2.jasper")
		def logo = applicationContext.getResource("classpath:/reports/logo.png")
		
		def hospitalInfo = hospitalConfigService.hospitalInfo
		
		if (!res.exists()) {
			return ResponseEntity.notFound().build()
		}
		
		def parameters = [:] as Map<String, Object>
		parameters.put("first_case_rate", "0.00")
		parameters.put("second_case_rate", "0.00")
		parameters.put("logo", logo.inputStream)
		
		//parameters.put("soaref", "DRAFT SOA Ref. No.")
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
		
		def billing = billingService.findOne(billingId)
		
		def format = DateTimeFormatter.ofPattern("MM/dd/yyyy")
		
		parameters["pin"] = billing?.patient?.patientNo ?: ""
		parameters["caseno"] = billing?.patientCase?.caseNo ?: ""
		parameters["folio"] = billing?.billingNo ?: ""
		parameters["dateadmitted"] = billing?.patientCase?.admissionDatetime ?
				billing.patientCase.admissionDatetime.atZone(ZoneId.systemDefault()).format(format) : ""
		
		parameters["datedischarged"] = billing?.patientCase?.dischargedDatetime ?
				billing.patientCase.dischargedDatetime.atZone(ZoneId.systemDefault()).format(format) : ""
		
		parameters["roomno"] = billing?.patientCase?.room?.roomNo ?: ""
		parameters["patientname"] = (billing?.patient?.fullName ?: (billing?.otcname ?: "")).toUpperCase()
		
		def os = new ByteArrayOutputStream()
		
		def currentSubtotal = BigDecimal.ZERO
		
		// Rooms and Lodging
		
		def records = [] as List<FolioPrintItems>
		
		def sortedBillingItem = billing?.billingItemList?.toSorted { BillingItem a, BillingItem b -> a.recordNo <=> b.recordNo }
		sortedBillingItem?.findAll {
			it.itemType == BillingItemType.ROOMBOARD &&
					it.status == BillingItemStatus.ACTIVE
		}?.each {
			
			currentSubtotal += ((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO)) * it.qty
			def item = new FolioPrintItems([
					category   : "Room Accommodation",
					date       : it.entryDate.atZone(ZoneId.systemDefault()).format(format),
					reference  : "",
					docno      : it.recordNo ?: "",
					description: it.description,
					qty        : it.qty.toString(),
					price      : new DecimalFormat("#,##0.00").format(((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO))),
					subtotal   : ((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO)) * it.qty,
					runningbal : new DecimalFormat("#,##0.00").format(currentSubtotal)
			
			])
			
			records.add(item)
			
		}
		
		sortedBillingItem?.findAll {
			it.itemType == BillingItemType.MEDICINES &&
					it.status == BillingItemStatus.ACTIVE
		}?.each {
			Item itemI = itemRepository.getOne(UUID.fromString(it.details["ITEMID"]))
			String description = "";
			if(itemI.pnf)
				description = "(PNF)"
			else
				description += "(Non-PNF)"


			currentSubtotal += ((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO)) * it.qty
			def item = new FolioPrintItems([
					category   : "Drugs and Medicines",
					date       : it.entryDate.atZone(ZoneId.systemDefault()).format(format),
					reference  : "",
					docno      : it.recordNo ?: "",
					description: it.description+description,
					qty        : it.qty.toString(),
					price      : new DecimalFormat("#,##0.00").format(((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO))),
					subtotal   : ((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO)) * it.qty,
					runningbal : new DecimalFormat("#,##0.00").format(currentSubtotal),
					pnf			: itemI.pnf,
					nonpnf		: itemI.nonpnf
			
			])
			
			records.add(item)
			
		}
		
		sortedBillingItem?.findAll {
			it.itemType == BillingItemType.DIAGNOSTICS &&
					it.status == BillingItemStatus.ACTIVE
		}?.each {
			
			currentSubtotal += ((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO)) * it.qty
			def item = new FolioPrintItems([
					category   : "Laboratory and Diagnostics",
					date       : it.entryDate.atZone(ZoneId.systemDefault()).format(format),
					reference  : "",
					docno      : it.recordNo ?: "",
					description: it.description,
					qty        : it.qty.toString(),
					price      : new DecimalFormat("#,##0.00").format(((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO))),
					subtotal   : ((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO)) * it.qty,
					runningbal : new DecimalFormat("#,##0.00").format(currentSubtotal)
			
			])
			
			records.add(item)
			
		}
		
		sortedBillingItem?.findAll {
			it.itemType == BillingItemType.ORFEE &&
					it.status == BillingItemStatus.ACTIVE
		}?.each {
			
			currentSubtotal += ((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO)) * it.qty
			def item = new FolioPrintItems([
					category   : "Operating Room",
					date       : it.entryDate.atZone(ZoneId.systemDefault()).format(format),
					reference  : "",
					docno      : it.recordNo ?: "",
					description: it.description,
					qty        : it.qty.toString(),
					price      : new DecimalFormat("#,##0.00").format(((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO))),
					subtotal   : ((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO)) * it.qty,
					runningbal : new DecimalFormat("#,##0.00").format(currentSubtotal)
			
			])
			
			records.add(item)
			
		}

		//cath lab
		sortedBillingItem?.findAll {
			it.itemType == BillingItemType.CATHLAB &&
					it.status == BillingItemStatus.ACTIVE
		}?.each {

			currentSubtotal += ((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO)) * it.qty
			def item = new FolioPrintItems([
					category   : "Catheterization Laboratory",
					date       : it.entryDate.atZone(ZoneId.systemDefault()).format(format),
					reference  : "",
					docno      : it.recordNo ?: "",
					description: it.description,
					qty        : it.qty.toString(),
					price      : new DecimalFormat("#,##0.00").format(((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO))),
					subtotal   : ((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO)) * it.qty,
					runningbal : new DecimalFormat("#,##0.00").format(currentSubtotal)

			])

			records.add(item)

		}
		//end cath lab
		
		sortedBillingItem?.findAll {
			it.itemType == BillingItemType.SUPPLIES &&
					it.status == BillingItemStatus.ACTIVE
		}?.each {
			
			currentSubtotal += ((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO)) * it.qty
			def item = new FolioPrintItems([
					category   : "Supplies",
					date       : it.entryDate.atZone(ZoneId.systemDefault()).format(format),
					reference  : "",
					docno      : it.recordNo ?: "",
					description: it.description,
					qty        : it.qty.toString(),
					price      : new DecimalFormat("#,##0.00").format(((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO))),
					subtotal   : ((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO)) * it.qty,
					runningbal : new DecimalFormat("#,##0.00").format(currentSubtotal)
			
			])
			
			records.add(item)
			
		}

		def soaGroupings = soaGroupingService.getSoaGroupingsbyBillingId(billingId)

		soaGroupings.each {
			String description = it.groupName
			BigDecimal totalGroup = 0.0
			List<String> recNos = []
			it.contents.each { bi->
				if(bi.status == BillingItemStatus.ACTIVE){
					totalGroup += bi.subTotal
					recNos.add(bi.recordNo)
				}
			}
			currentSubtotal += totalGroup
			def itemX = new FolioPrintItems([
					category   : "Others",
					date       : it.createdDate.atZone(ZoneId.systemDefault()).format(format),
					reference  : "",
					docno      : recNos.join(",\n"),
					description: description,
					qty        : "1",
					price      : new DecimalFormat("#,##0.00").format(totalGroup),
					subtotal   : totalGroup,
					runningbal : new DecimalFormat("#,##0.00").format(currentSubtotal)

			])

			records.add(itemX)


		}
		sortedBillingItem?.findAll {
			it.itemType == BillingItemType.OTHERS &&
					it.status == BillingItemStatus.ACTIVE &&
					it.soaGrouping == null
		}?.each {
			
			currentSubtotal += ((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO)) * it.qty
			def item = new FolioPrintItems([
					category   : "Others",
					date       : it.entryDate.atZone(ZoneId.systemDefault()).format(format),
					reference  : "",
					docno      : it.recordNo ?: "",
					description: it.description,
					qty        : it.qty.toString(),
					price      : new DecimalFormat("#,##0.00").format(((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO))),
					subtotal   : ((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO)) * it.qty,
					runningbal : new DecimalFormat("#,##0.00").format(currentSubtotal)
			
			])
			
			records.add(item)
			
		}
		
		sortedBillingItem?.findAll {
			it.itemType == BillingItemType.PF &&
					it.status == BillingItemStatus.ACTIVE
		}?.each {
			
			currentSubtotal += ((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO)) * it.qty
			def item = new FolioPrintItems([
					category   : "Professional Fees",
					date       : it.entryDate.atZone(ZoneId.systemDefault()).format(format),
					reference  : "",
					docno      : it.recordNo ?: "",
					description: it.description,
					qty        : it.qty.toString(),
					price      : new DecimalFormat("#,##0.00").format(((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO))),
					subtotal   : ((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO)) * it.qty,
					runningbal : new DecimalFormat("#,##0.00").format(currentSubtotal)
			
			])
			
			records.add(item)
			
		}
		
		sortedBillingItem?.findAll {
			it.itemType == BillingItemType.DEDUCTIONS &&
					it.status == BillingItemStatus.ACTIVE
		}?.each {
			
			currentSubtotal += ((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO)) * it.qty
			def item = new FolioPrintItems([
					category   : "Deductions (HCI)",
					date       : it.entryDate.atZone(ZoneId.systemDefault()).format(format),
					reference  : "",
					docno      : it.recordNo ?: "",
					description: it.description,
					qty        : it.qty.toString(),
					price      : new DecimalFormat("#,##0.00").format(((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO))),
					subtotal   : ((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO)) * it.qty,
					runningbal : new DecimalFormat("#,##0.00").format(currentSubtotal)
			
			])
			
			records.add(item)
			
		}
		
		sortedBillingItem?.findAll {
			it.itemType == BillingItemType.DEDUCTIONSPF &&
					it.status == BillingItemStatus.ACTIVE
		}?.each {
			
			currentSubtotal += ((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO)) * it.qty
			def item = new FolioPrintItems([
					category   : "Deductions (PF)",
					date       : it.entryDate.atZone(ZoneId.systemDefault()).format(format),
					reference  : "",
					docno      : it.recordNo ?: "",
					description: it.description,
					qty        : it.qty.toString(),
					price      : new DecimalFormat("#,##0.00").format(((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO))),
					subtotal   : ((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO)) * it.qty,
					runningbal : new DecimalFormat("#,##0.00").format(currentSubtotal)
			
			])
			
			records.add(item)
			
		}


		Set<String> ptrackerReapply = []

		List<FolioPrintItems> temporaryPayments = []
		List<FolioPrintItems> temporaryPaymentsApplication = []
		sortedBillingItem?.findAll {
			it.itemType == BillingItemType.PAYMENTS &&
					it.status == BillingItemStatus.ACTIVE
		}?.each {

			Boolean isApplication = false
			if(it.details.containsKey(BillingItemDetailParam.APPLICATION.name())){
				// will catch unique paymentTrackerId
				def PAYMENTRACKERID = it.details[BillingItemDetailParam.PAYTRACKER_ID.name()]
				if(PAYMENTRACKERID ){
					ptrackerReapply << PAYMENTRACKERID
				    isApplication = true
				}
			}

			//currentSubtotal += ((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO)) * it.qty
			def item = new FolioPrintItems([
					category   : "Payments",
					date       : it.entryDate.atZone(ZoneId.systemDefault()).format(format),
					reference  : "",
					docno      : it.recordNo ?: "",
					description: it.description,
					qty        : it.qty.toString(),
					price      :   (isApplication) ? new DecimalFormat("#,##0.00").format(((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO)) * -1 ):
							new DecimalFormat("#,##0.00").format(((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO)) ),
					subtotal   : (isApplication) ? ((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO)) * it.qty * -1 : ((it.debit ?: BigDecimal.ZERO) - (it.credit ?: BigDecimal.ZERO)) * it.qty,
					runningbal : new DecimalFormat("#,##0.00").format(currentSubtotal)
			
			])

			if(isApplication)
				 temporaryPaymentsApplication.add(item)
			 else
			     temporaryPayments.add(item)
		}

		List<FolioPrintItems> advances = []
		ptrackerReapply.each {

			def paymentTracker = paymentTrackerServices.findOne(UUID.fromString(it))

			//currentSubtotal += (paymentTracker.totalpayments * -1)
			def pitem = new FolioPrintItems([
					category   : "Payments",
					date       : paymentTracker.createdDate.atZone(ZoneId.systemDefault()).format(format),
					reference  : "",
					docno      : paymentTracker.ornumber ?: "",
					description: paymentTracker.description,
					qty        : "1",
					price      : new DecimalFormat("#,##0.00").format(paymentTracker.totalpayments * -1),
					subtotal   : paymentTracker.totalpayments * -1,
					runningbal : new DecimalFormat("#,##0.00").format(currentSubtotal)

			])
			advances << pitem
		}




		List<FolioPrintItems> finalPayments = []

		finalPayments.addAll(temporaryPayments)
		finalPayments.addAll(advances)
	//	finalPayments.addAll(temporaryPaymentsApplication)

		finalPayments.each {

			  currentSubtotal += it.subtotal
			  it.runningbal = new DecimalFormat("#,##0.00").format(currentSubtotal)

		}

		records.addAll(finalPayments)

		try {
			def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, new JRBeanCollectionDataSource(records))
			
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
		params.add("Content-Disposition", "inline;filename=FOLIO-\"" + (billing?.patient?.fullName ?: "") + "\".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)
		
	}
	
	// Code ni Adonis
	@RequestMapping(method = RequestMethod.GET, value = ["/api/salesMonitoring/report"])
	ResponseEntity<AnyDocument.Any> donwloadSalesMonitoring(
			@RequestParam String dateStart,
			@RequestParam String dateEnd,
			@RequestParam String type,
			@RequestParam String department,
			@RequestParam String processcode
	) {
		
		List<Salesreportitem> items = jdbcTemplate.query("""
       select *  from billing.salesreport_1(?::date,?::date,?) where
       category ilike concat('%',?,'%') and   process_code ilike  concat('%',?,'%')
""", new RowMapper<Salesreportitem>() {
			@Override
			Salesreportitem mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				return new Salesreportitem(
						rs.getObject("id", UUID.class),
						rs.getString("category"),
						rs.getTimestamp("date").toInstant(),
						rs.getString("ornos"),
						rs.getString("folio"),
						rs.getString("recno"),
						rs.getString("department"),
						rs.getString("service_code"),
						rs.getString("process_code"),
						rs.getString("service"),
						rs.getBigDecimal("gross"),
						rs.getBigDecimal("vatable_sales"),
						rs.getBigDecimal("vat_exempt_sales"),
						rs.getBigDecimal("vat_amount"),
						rs.getString("discounts_availed"),
						rs.getBigDecimal("discounts_total"),
						rs.getBigDecimal("net_sales")
				)
				
			}
		},
				dateStart,
				dateEnd,
				department,
				type,
				processcode
		)
		
		StringBuffer buffer = new StringBuffer()

		DateTimeFormatter formatter =
				DateTimeFormatter.ofPattern("YYYY").withZone(ZoneId.systemDefault())
		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV.withHeader(
				"CAT",
				"DATE",
				"ORNUM",
				"FOLIO",
				"REFERENCE",
				"DEPARTMENT",
				"SERVICE CODE",
				"PROCESS CODE",
				"SERVICE",
				"GROSS",
				"VATABLE SALES",
				"VAT-EXEMPT SALES",
				"VAT AMOUNT",
				"DISCOUNT AVAIL",
				"DISCOUNT",
				"NET SALES"
		))


		try {
			items.each {
				item ->
					csvPrinter.printRecord(
							item.category,
							item.date,
							item.ornos,
							"SOA:" + formatter.format(item.date) + "-" + item.folio,
							item.recno,
							item.department,
							item.service_code,
							item.process_code,
							item.service,
							item.gross,
							item.vatable_sales,
							item.vat_exempt_sales,
							item.vat_amount,
							item.discounts_availed,
							item.discounts_total,
							item.net_sales
					)
			}
			
			LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
			extHeaders.add("Content-Disposition",
					"attachment;filename=Sales_Monitoring_Report_${dateStart.replace("-", "_") + "to" + dateEnd.replace("-", "_")}.csv".toString())
			
			return new ResponseEntity(buffer.toString().getBytes(), extHeaders, HttpStatus.OK)
		}
		catch (e) {
			throw e
		}
		
	}


	// Code ni Adonis
	@RequestMapping(method = RequestMethod.GET, value = ["/api/revenuereconciliation/report"])
	ResponseEntity<AnyDocument.Any> donwloadRevenueReconciliationReport(
			@RequestParam String dateStart,
			@RequestParam String dateEnd
	) {

		List<RevenueAndReconciliationReportDto> items = jdbcTemplate.query("""
		select *,sum(outstanding_balance)  OVER (ORDER BY transaction_date, record_no) as running_balance from billing.revenue_recon_report(?::date,?::date)
		order by transaction_date, record_no;
		""", new RowMapper<RevenueAndReconciliationReportDto>() {
			@Override
			RevenueAndReconciliationReportDto mapRow(ResultSet rs, int rowNum) throws SQLException {

				return new RevenueAndReconciliationReportDto(
						rs.getObject("id", UUID.class),
						rs.getString("folio_no"),
						rs.getString("case_no"),
						rs.getString("soa_no"),
						rs.getString("patient_name"),
						rs.getString("registration_type"),
						rs.getString("admission_date"),
						rs.getString("discharge_date"),
						rs.getString("transaction_date"),
						rs.getString("maker_name"),
						rs.getString("maker_section"),
						rs.getString("maker_department"),
						rs.getString("revenue_center"),
						rs.getString("record_no"),
						rs.getString("item_code"),
						rs.getString("description"),
						rs.getInt("quantity"),
						rs.getBigDecimal("price"),
						rs.getBigDecimal("room_board"),
						rs.getBigDecimal("drugs_medicine"),
						rs.getBigDecimal("laboratory_diagnostic"),
						rs.getBigDecimal("operating_room_fee"),
						rs.getBigDecimal("supplies"),
						rs.getBigDecimal("oxygen"),
						rs.getBigDecimal("infusion_pump"),
						rs.getBigDecimal("miscellaneous_services"),
						rs.getBigDecimal("gross_sales_vat_exclusive"),
						rs.getBigDecimal("vat_revenue"),
						rs.getBigDecimal("vat_exempt"),
						rs.getBigDecimal("vat"),
						rs.getBigDecimal("liability_pf"),
						rs.getBigDecimal("deduction_hci"),
						rs.getBigDecimal("deduction_pf"),
						rs.getString("shift_no"),
						rs.getString("terminal"),
						rs.getString("payment_reference_no"),
						rs.getString("mode_of_payment"),
						rs.getBigDecimal("payment_amount"),
						rs.getString("remarks"),
						rs.getBigDecimal("outstanding_balance"),
						rs.getBigDecimal("running_balance")
				)

			}
		},
				dateStart,
				dateEnd
		)

		StringBuffer buffer = new StringBuffer()

		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV.withHeader(
				"Folio Number",
				"Case Number",
				"SOA Number",
				"Registration Type",
				"Patient Name",
				"Admission date",
				"Discharged Date",
				"Transaction Date",
				"Maker (user) Name",
				"Maker (user) Assigned Section",
				"Maker (user) Assigned Department",
				"Revenue Center",
				"Record Number",
				"Item/Service Code",
				"Description",
				"Quantity",
				"Price",
				"Revenue-Room and Board",
				"Revenue-Drugs and Medicine",
				"Revenue-Laboratory and Diagnostics",
				"Revenue-Operating Room Fee",
				"Revenue-Supplies",
				"Revenue-Oxygen",
				"Revenue-Infusion pump",
				"Revenue-Miscellaneous Services",
				"Total Gross Sales Inclusive of VAT",
				"Vatable Revenue",
				"VAT Exempt Revenue",
				"VAT",
				"Liability-Professional Fees",
				"Deductions-Hospital",
				"Deductions-PF",
				"Payment Shift Number",
				"Cashier Terminal",
				"Payment Reference Number (OR/AR)",
				"Mode of Payment",
				"Amount Paid",
				"Remarks",
				"Outstanding Balance",
				"Running Balance"
			))

		try {
			items.each {
				item ->
					csvPrinter.printRecord(
							item.folio_no,
							item.case_no,
							item.soa_no,
							item.registration_type,
							item.patient_name,
							item.admission_date,
							item.discharge_date,
							item.transaction_date,
							item.maker_name,
							item.maker_section,
							item.maker_department,
							item.revenue_center,
							item.record_no,
							item.item_code,
							item.description,
							item.quantity,
							item.price,
							item.room_board,
							item.drugs_medicine,
							item.laboratory_diagnostic,
							item.operating_room_fee,
							item.supplies,
							item.oxygen,
							item.infusion_pump,
							item.miscellaneous_services,
							item.gross_sales_vat_exclusive,
							item.vat_revenue,
							item.vat_exempt,
							item.vat,
							item.liability_pf,
							item.deduction_hci,
							item.deduction_pf,
							item.shift_no,
							item.terminal,
							item.payment_reference_no,
							item.mode_of_payment,
							item.payment_amount,
							item.remarks,
							item.outstanding_balance,
							item.running_balance
					)
			}

			LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
			extHeaders.add("Content-Disposition",
					"attachment;filename=Revenue_and_Reconciliation_Report_${dateStart.replace("-", "_") + "to" + dateEnd.replace("-", "_")}.csv".toString())

			return new ResponseEntity(buffer.toString().getBytes(), extHeaders, HttpStatus.OK)
		}
		catch (e) {
			throw e
		}

	}
}
