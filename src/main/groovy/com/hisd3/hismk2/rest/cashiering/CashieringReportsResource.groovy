package com.hisd3.hismk2.rest.cashiering

import com.google.gson.Gson
import com.hisd3.hismk2.domain.cashiering.PaymentTracker
import com.hisd3.hismk2.domain.cashiering.PaymentTrackerDetails
import com.hisd3.hismk2.domain.cashiering.PaymentType
import com.hisd3.hismk2.domain.cashiering.ReceiptType
import com.hisd3.hismk2.domain.cashiering.dto.CollectionReportCsvDownloadDto
import com.hisd3.hismk2.graphqlservices.billing.BillingService
import com.hisd3.hismk2.graphqlservices.billing.InvestorPaymentLedgerService
import com.hisd3.hismk2.graphqlservices.billing.InvestorsService
import com.hisd3.hismk2.graphqlservices.billing.SubscriptionService
import com.hisd3.hismk2.graphqlservices.cashiering.CdctrServices
import com.hisd3.hismk2.graphqlservices.cashiering.ChequeEncashmentServices
import com.hisd3.hismk2.graphqlservices.cashiering.PaymentTrackerDetailsServices
import com.hisd3.hismk2.graphqlservices.cashiering.ShiftingServices
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.billing.SubscriptionRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.rest.dto.BillingScheduleFieldsDto
import com.hisd3.hismk2.security.SecurityUtils
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
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.BooleanUtils
import org.apache.commons.lang3.StringUtils
import org.apache.pdfbox.io.MemoryUsageSetting
import org.apache.pdfbox.multipdf.PDFMergerUtility
import org.apache.xmlbeans.impl.xb.xsdschema.AnyDocument
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.Callable

@Canonical
class CashDto {
	String denomination
	int noofpieces = 0
	BigDecimal total
	
}

@Canonical
class CheckCC {
	String bank = ""
	String type = ""
	String chnumber = ""
	BigDecimal amount = BigDecimal.ZERO
	
}

@Canonical
class DCTRItems {
	String description = ""
	BigDecimal totalpayments = BigDecimal.ZERO
	BigDecimal descriptionAmount = BigDecimal.ZERO
	String paymentType = ""
	String details = ""
	String receiptTypeStr = ""
	String category = ""
	Integer order = 0
}


@Canonical
class DailySalesTRItems {
	String doc_date = ""
	String doc_type = ""
	String doc_no = ""
	String doc_ref_no = ""
	String payee = ""
	String description = ""
	String mode_of_payment = ""
	BigDecimal amount = BigDecimal.ZERO
	String user_id = ""
}

@Canonical
class CheckEncashmentItems {
	String bank_of_check
	String check_no
	String doc_date
	String doc_no
	String check_cashier
	BigDecimal amount = BigDecimal.ZERO
}

@Canonical
class DailySalesFieldsDTO {
	String shiftno = ""
	String shiftstart = ""
	String shiftend = ""
	String status = ""
	String terminal = ""
	String terminalremarks = ""
}

@Canonical
class AcknowledgementFieldsDto {
	String description = ""
}

@RestController
@RequestMapping("/cashieringreports")
class CashieringReportsResource {

	@Autowired
	ApplicationContext applicationContext

	@Autowired
	HospitalConfigService hospitalConfigService

	@Autowired
	ShiftingServices shiftingServices

	@Autowired
	UserRepository userRepository

	@Autowired
	EmployeeRepository employeeRepository

	@Autowired
	ChequeEncashmentServices chequeEncashmentServices

	@Autowired
	CdctrServices cdctrServices

	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate

	@Autowired
	BillingService billingService

	@Autowired
	InvestorPaymentLedgerService investorPaymentLedgerService

	@Autowired
	PaymentTrackerDetailsServices paymentTrackerDetailsServices


	@RequestMapping(value = "/printcdctr", produces = ["application/pdf"])
	ResponseEntity<byte[]> printcdctr(
			@RequestParam UUID cdctrId
	) {

		def cdctr = cdctrServices.findOne(cdctrId)

		def res = applicationContext.getResource("classpath:/reports/cashier/printcdctr.jasper")
		def os = new ByteArrayOutputStream()
		def logo = applicationContext.getResource("classpath:/reports/logo.png")

		if (!res.exists()) {
			return ResponseEntity.notFound().build()
		}
		def hospitalInfo = hospitalConfigService.hospitalInfo
		def parameters = [:] as Map<String, Object>

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

		List<CashDto> cash = []

		cash.add(new CashDto("1000", 0, BigDecimal.ZERO))
		cash.add(new CashDto("500", 0, BigDecimal.ZERO))
		cash.add(new CashDto("100", 0, BigDecimal.ZERO))
		cash.add(new CashDto("50", 0, BigDecimal.ZERO))
		cash.add(new CashDto("20", 0, BigDecimal.ZERO))
		cash.add(new CashDto("10", 0, BigDecimal.ZERO))
		cash.add(new CashDto("5", 0, BigDecimal.ZERO))
		cash.add(new CashDto("1", 0, BigDecimal.ZERO))
		cash.add(new CashDto(".25", 0, BigDecimal.ZERO))
		cash.add(new CashDto(".1", 0, BigDecimal.ZERO))
		cash.add(new CashDto(".05", 0, BigDecimal.ZERO))

		def dataSourceTable = new JRBeanCollectionDataSource(cash)

		parameters.put("tablesource", dataSourceTable)

		parameters.put("recno", cdctr.recno)

		List<CheckCC> checkscc = []

		cdctr.shiftings.each {

			it.payments.each {
				PaymentTracker pt = it

				pt.paymentDetails.each {
					pd ->

						if (pd.type == PaymentType.BANKDEPOSIT ||
								pd.type == PaymentType.CARD ||
								pd.type == PaymentType.CHECK
						) {
							checkscc << new CheckCC(pd.bank ?: (pd.bankEntity?.bankname ?: "N/A"), pd.type.name(), pd.reference, pd.amount)
						}
				}

			}

			def encashmentShift = chequeEncashmentServices.cheqEncashmentByOneShiftId(it.id)
			if(encashmentShift)
				encashmentShift.each {
					ce ->
						if(ce.shifting.id == it.id) {
							checkscc << new CheckCC(ce.bank ? ce.bank?.bankname : "N/A", PaymentType.CHECK.name(), ce.chequeNo, ce.amount)
						}
				}

		}


		def username = SecurityUtils.currentLogin()
		def user = userRepository.findOneByLogin(username)
		def emp = employeeRepository.findOneByUser(user)

		parameters.put("preparedby", emp.fullName)
		parameters.put("preparedbyempno", emp.employeeNo)

		try {
			def jrprint = JasperFillManager.fillReport(res.inputStream, parameters, new JRBeanCollectionDataSource(checkscc))

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

		def ut = new PDFMergerUtility()
		def outputStream = new ByteArrayOutputStream()

		def inputStream = new ByteArrayInputStream(os.toByteArray())
		ut.addSource(inputStream)
		IOUtils.closeQuietly(os)

		ut.destinationStream = outputStream

		cdctr.shiftings.each {

			def shift = printSalesReportPerShift(it.id)
			def is = new ByteArrayInputStream(shift.body)
			ut.addSource(is)
			IOUtils.closeQuietly(is)

		}

		ut.mergeDocuments(MemoryUsageSetting.setupTempFileOnly())

		def data = outputStream.toByteArray()
		def params = new LinkedMultiValueMap<String, String>()
		params.add("Content-Disposition", "inline;filename=ConsolidatedDailyCollection-${cdctr.recno}.pdf".toString())
		return new ResponseEntity(data, params, HttpStatus.OK)
	}

	static DCTRItems addDCTRItems(PaymentTrackerDetails ptd, String category,cards,checks,deposits,Integer order=0,Boolean voided=false){
		if(ptd.type == PaymentType.CASH) {
			return new DCTRItems(
					"     - CASH ${(ptd.reference ?: "")}",
					null,
					ptd.amount,
					"",
					"",
					"",
					category,
					order
			)
		}
		if(ptd.type == PaymentType.CHECK) {
			if(!voided)
				checks << ptd

			return new DCTRItems(
					"     - CHECK: ${(ptd.reference ?: "")}",
					null,
					ptd.amount,
					"",
					"",
					"",
					category,
					order
			)
		}

		if(ptd.type == PaymentType.CARD) {
			if(!voided)
				cards << ptd

			return new DCTRItems(
					"     - CARD: ${(ptd.reference ?: "")}",
					null,
					ptd.amount,
					"",
					"",
					"",
					category,
					order
			)
		}

		if(ptd.type == PaymentType.BANKDEPOSIT) {
			if(!voided)
				deposits << ptd

			return new DCTRItems(
					"     - BANKDEPOSIT: ${(ptd.reference ?: "")}",
					null,
					ptd.amount,
					"",
					"",
					"",
					category,
					order
			)
		}
	}


	@RequestMapping(value = "/printSalesReportForShift", produces = ["application/pdf"])
	ResponseEntity<byte[]> printSalesReportForShift(
			@RequestParam UUID shiftId
	) {

		def shift = shiftingServices.findOne(shiftId)
		def encashmentShift = chequeEncashmentServices.cheqEncashmentByOneShiftId(shiftId)

		def res = applicationContext.getResource("classpath:/reports/cashier/printdctr.jasper")
		def os = new ByteArrayOutputStream()
		//def logo = applicationContext.getResource("classpath:/reports/logo.jpg")
		def logo = applicationContext?.getResource("classpath:/reports/logo.png")

		if (!res.exists()) {
			return ResponseEntity.notFound().build()
		}
		def hospitalInfo = hospitalConfigService.hospitalInfo
		def parameters = [:] as Map<String, Object>

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

		List<CashDto> cash = []

		cash.add(new CashDto("1000", 0, BigDecimal.ZERO))
		cash.add(new CashDto("500", 0, BigDecimal.ZERO))
		cash.add(new CashDto("100", 0, BigDecimal.ZERO))
		cash.add(new CashDto("50", 0, BigDecimal.ZERO))
		cash.add(new CashDto("20", 0, BigDecimal.ZERO))
		cash.add(new CashDto("10", 0, BigDecimal.ZERO))
		cash.add(new CashDto("5", 0, BigDecimal.ZERO))
		cash.add(new CashDto("1", 0, BigDecimal.ZERO))
		cash.add(new CashDto(".25", 0, BigDecimal.ZERO))
		cash.add(new CashDto(".1", 0, BigDecimal.ZERO))
		cash.add(new CashDto(".05", 0, BigDecimal.ZERO))

		def dataSourceTable = new JRBeanCollectionDataSource(cash)

		parameters.put("tablesource", dataSourceTable)

		// shift
		def formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")
		parameters.put("title", "Daily Collection Report")
		parameters.put("shiftno", shift.shiftno ?: "")
		parameters.put("terminal", shift.terminal?.terminalId ?: "")
		parameters.put("terminalremarks", shift.terminal?.remarks ?: "")
		parameters.put("shiftstart", shift.startshift ? formatter.format(shift.startshift.atZone(ZoneId.systemDefault())) : "")
		parameters.put("shiftend", shift.endshift ? formatter.format(shift.endshift.atZone(ZoneId.systemDefault())) : "")

		parameters.put("status", shift.active ? "OPEN" : "CLOSED")

		// Fill Report Items

		List<DCTRItems> items = []
		List<PaymentTrackerDetails> cards = []
		List<PaymentTrackerDetails> checks = []
		List<PaymentTrackerDetails> deposits = []

		// Calculate Total Amount
		def totalchecks = 0.0
		def totalchecksEncashment = 0.0
		def totalbankdeposit = 0.0
		def totalcards = 0.0
		def totalcash = 0.0
		def totalchange = 0.0


		def sortedActiveOr = shift.payments.findAll { PaymentTracker a -> BooleanUtils.isNotTrue(a.voided) && a.receiptType == ReceiptType.OR }.toSorted {
			PaymentTracker a, PaymentTracker b ->
				a.ornumber <=> b.ornumber
		}

		Integer order = 0
		sortedActiveOr.each {
			PaymentTracker pt ->

				items << new DCTRItems(
						StringUtils.upperCase(pt.description),
						pt.totalpayments,
						null,
						"",
						"",
						"${pt.receiptType.name()}: ${pt.ornumber} [${pt.createdBy}]",
						"Official Receipts",
						order++
				)

				pt.paymentDetails.each {
					ptd ->
						def dCTRItems = addDCTRItems(ptd,"Official Receipts",cards,checks,deposits,order++)
						if(ptd.type == PaymentType.CASH)
							totalcash += ptd.amount

						if(dCTRItems)
							items << dCTRItems
				}

				totalchange += pt.change
		}

		def sortedVoidedOr = shift.payments.findAll { PaymentTracker a -> BooleanUtils.isTrue(a.voided) }.toSorted {
			PaymentTracker a, PaymentTracker b ->
				a.ornumber <=> b.ornumber
		}

		sortedVoidedOr.each {
			PaymentTracker pt ->
				items << new DCTRItems(
						StringUtils.upperCase(pt.description),
						pt.totalpayments,
						null,
						"",
						"",
						"${pt.receiptType.name()}: ${pt.ornumber} [${pt.createdBy}]",
						"Voided Official Receipts",
						order++
				)

				pt.paymentDetails.each {
					ptd ->
						def dCTRItems = addDCTRItems(ptd,"Voided Official Receipts",cards,checks,deposits,order++,true)
						if(dCTRItems)
							items << dCTRItems

				}
		}

		// AR Payments
		def sortedActiveAr = shift.payments.findAll { PaymentTracker a -> BooleanUtils.isNotTrue(a.voided) && a.receiptType == ReceiptType.AR }.toSorted {
			PaymentTracker a, PaymentTracker b ->
				a.ornumber <=> b.ornumber
		}

		sortedActiveAr.each {
			PaymentTracker pt ->

				items << new DCTRItems(
						StringUtils.upperCase(pt.description),
						pt.totalpayments,
						null,
						"",
						"",
						"${pt.receiptType.name()}: ${pt.ornumber} [${pt.createdBy}]",
						"Acknowledgement Receipts",
						order++
				)

				pt.paymentDetails.each {
					ptd ->
						def dCTRItems = addDCTRItems(ptd,"Acknowledgement Receipts",cards,checks,deposits,order++)

						if(ptd.type == PaymentType.CASH)
							totalcash += ptd.amount

						if(dCTRItems)
							items << dCTRItems
				}

				totalchange += pt.change
		}

		// Details of Checks and Card
		// Cards
		cards.each {
			card ->
				totalcards += card.amount

				items << new DCTRItems(
						StringUtils.upperCase(card.paymentTracker.description)+" ${(card.bankEntity ? [card.bankEntity.bankname] : "")}",
						card.amount,
						null,
						"",
						"",
						card.reference ? "REFNUM: ${card.reference}" : ""+"[${card.paymentTracker.createdBy}]",
						"Cards",
						order++

				)
		}

		// Checks
		checks.each {
			check ->
				totalchecks += check.amount

				items << new DCTRItems(
						StringUtils.upperCase(check.paymentTracker.description)+" [${check.bank}]",
						check.amount,
						null,
						"",
						"",
						check.reference ? "REFNUM: ${check.reference}" : ""+"[${check.paymentTracker.createdBy}]",
						"Checks",
						order++

				)
		}

		// Deposits
		deposits.each {
			deposit ->
				totalbankdeposit += deposit.amount

				items << new DCTRItems(
						StringUtils.upperCase(deposit.paymentTracker.description)+" ${(deposit.bankEntity ? [deposit.bankEntity.bankname] : "")}",
						deposit.amount,
						null,
						"",
						"",
						deposit.reference ? "REFNUM: ${deposit.reference}" : ""+"[${deposit.paymentTracker.createdBy}]",
						"Bank Deposits",
						order++

				)
		}

		encashmentShift.each {
			ce ->

				if(ce.returnedShifting) {
					if (ce.shifting.id == shiftId && ce.returnedShifting.id != shiftId) {
						totalchecksEncashment += ce.amount
						def recordNo = ce.recordNo.replaceFirst('^0+(?!$)', "")
						items << new DCTRItems(
								"[${recordNo}]-${ce.bank.bankname}",
								ce.amount,
								null,
								"",
								"",
								"${PaymentType.CHECK.name()}: ${ce.chequeNo ?: ""} [${ce.createdBy}]",
								"Checks Encashment",
								order++
						)
					}

					if (ce.returnedShifting.id == shiftId && ce.shifting.id != shiftId) {
						totalcash += ce.amount
						def recordNo = ce.recordNo.replaceFirst('^0+(?!$)', "")
						items << new DCTRItems(
								"[${recordNo}] - CASH",
								ce.amount,
								null,
								"",
								"",
								"${PaymentType.CHECK.name()}: ${ce.chequeNo ?: ""} [RETURNED] [${ce.createdBy}]",
								"Checks Encashment",
								order++
						)
					}
				}
				else {
					if(ce.shifting.id == shiftId) {
						totalchecksEncashment += ce.amount
						def recordNo = ce.recordNo.replaceFirst('^0+(?!$)', "")
						items << new DCTRItems(
								"[${recordNo}]-${ce.bank.bankname}",
								ce.amount,
								null,
								"",
								"",
								"${PaymentType.CHECK.name()}: ${ce.chequeNo ?: ""} [${ce.createdBy}]",
								"Checks Encashment",
								order++
						)
					}
				}
		}

		parameters.put("totalchecks", totalchecks)
		parameters.put("totalCheckEncashment", totalchecksEncashment)
		parameters.put("totalbankdeposit", totalbankdeposit)
		parameters.put("totalcards", totalcards)
		parameters.put("totalhardcash", totalcash + totalchange)
		parameters.put("netRemainingCash", (totalcash + totalchange) - totalchecksEncashment)

		BigDecimal amountReceived = totalchecks + totalcards + (totalcash + totalchange) + totalbankdeposit
		parameters.put("totalamountreceived", amountReceived)

		def username = SecurityUtils.currentLogin()
		def user = userRepository.findOneByLogin(username)
		def emp = employeeRepository.findOneByUser(user)

		parameters.put("preparedby", emp.fullName)
		parameters.put("preparedbyempno", emp.employeeNo)

		items = items.sort{ s -> s.order}
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
		params.add("Content-Disposition", "inline;filename=DailyCollection-${shift.shiftno}.pdf".toString())
		return new ResponseEntity(data, params, HttpStatus.OK)
		
	}



	def DailySalesTRItems addDailySalesTRItems(Map<String,BigDecimal> receiptsObj,PaymentTracker pt,PaymentTrackerDetails ptd,cards,checks,deposits,totalCalculations,userID,Boolean voided=false){
		def formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")

		String doc_ref_no = ""
		if(pt.billingid){
			def billing = billingService.findOne(pt.billingid)
			doc_ref_no = billing.billingNo
		}

		if(pt.investorId){
			def investorLedger = investorPaymentLedgerService.findByPaymentTrackerId(pt.id)
			if(investorLedger.size() > 0) {
				investorLedger.eachWithIndex  {
					it, index ->
						if (investorLedger.size() == index + 1)
							doc_ref_no = it.subscription.subscriptionCode
						else
							doc_ref_no += "${it.subscription.subscriptionCode} ,"
				}
			}
		}

		if(pt.transactionCategory) {
			if (pt.transactionCategory.equalsIgnoreCase("SA"))
				doc_ref_no = pt?.reference ?: ''
			if (pt.transactionCategory.equalsIgnoreCase("PN"))
				doc_ref_no = pt?.reference ?: ''
		}

		String empID = ""
		if(!userID[ptd.createdBy]) {
			def user = employeeRepository.findByUsername(ptd.createdBy).first()
			if(user) {
				userID[ptd.createdBy] = user.employeeId
				empID = userID[ptd.createdBy]
			}
		}
		else {
			empID = userID[ptd.createdBy]
		}


		DailySalesTRItems item = new DailySalesTRItems(
				formatter.format(pt.createdDate.atZone(ZoneId.systemDefault())),
				pt.receiptType as String,
				pt.ornumber,
				doc_ref_no as String,
				pt.payorName?:pt.description,
				pt.transactionCategory?:'',
				ptd.type == PaymentType.BANKDEPOSIT ? "BANK DEPOSIT" : ptd.type.name(),
				ptd.amount,
				empID)



		if(!voided) {
			if(pt.transactionCategory) {
				if (receiptsObj[pt.transactionCategory]) {
					BigDecimal total = receiptsObj[pt.transactionCategory]
					total += ptd.amount
					receiptsObj[pt.transactionCategory] = total
				}
				else
					receiptsObj[pt.transactionCategory] = ptd.amount
			}

			DailySalesTRItems separateItems = new DailySalesTRItems(
					item.doc_date,
					item.doc_type,
					item.doc_no,
					item.doc_ref_no,
					item.payee,
					ptd.bankEntity?.bankname ?: '',
					ptd.reference,
					item.amount,
					item.user_id
			)

			switch (ptd.type.toString()) {
				case PaymentType.CASH.name() :
					totalCalculations[ptd.type.toString()] += separateItems.amount
					break;
				case PaymentType.CHECK.name() :
					totalCalculations[ptd.type.toString()] += separateItems.amount
					separateItems.description = ptd.bank
					separateItems.doc_date = ptd.checkdate
					checks << separateItems
					break;
				case PaymentType.CARD.name() :
					totalCalculations[ptd.type.toString()] += separateItems.amount
					separateItems.doc_ref_no = ptd.reference
					separateItems.mode_of_payment = ptd.type
					cards << separateItems
					break;
				case PaymentType.BANKDEPOSIT.name() :
					totalCalculations[ptd.type.toString()] += separateItems.amount
					separateItems.doc_date = ptd.checkdate
					deposits << separateItems
					break;
				case PaymentType.EWALLET.name() :
					totalCalculations[ptd.type.toString()] += separateItems.amount
					separateItems.doc_ref_no = ptd.reference
					separateItems.mode_of_payment = ptd.type
					cards << separateItems
					break;
				default:
					break;
			}
		}

		return item
	}

	@RequestMapping(value = "/printSalesReportPerShift", produces = ["application/pdf"])
	ResponseEntity<byte[]> printSalesReportPerShift(
			@RequestParam UUID shiftId
	) {

		def shift = shiftingServices.findOne(shiftId)
		def encashmentShift = chequeEncashmentServices.cheqEncashmentByOneShiftId(shiftId)

		def res = applicationContext.getResource("classpath:/reports/cashier/printdailycollection.jasper")
		def os = new ByteArrayOutputStream()
		//def logo = applicationContext.getResource("classpath:/reports/logo.jpg")
		def logo = applicationContext?.getResource("classpath:/reports/logo.png")

		if (!res.exists()) {
			return ResponseEntity.notFound().build()
		}
		def hospitalInfo = hospitalConfigService.hospitalInfo
		def parameters = [:] as Map<String, Object>

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

		List<CashDto> cash = []

		cash.add(new CashDto("1000", 0, BigDecimal.ZERO))
		cash.add(new CashDto("500", 0, BigDecimal.ZERO))
		cash.add(new CashDto("200", 0, BigDecimal.ZERO))
		cash.add(new CashDto("100", 0, BigDecimal.ZERO))
		cash.add(new CashDto("50", 0, BigDecimal.ZERO))
		cash.add(new CashDto("20", 0, BigDecimal.ZERO))
		cash.add(new CashDto("10", 0, BigDecimal.ZERO))
		cash.add(new CashDto("5", 0, BigDecimal.ZERO))
		cash.add(new CashDto("1", 0, BigDecimal.ZERO))
		cash.add(new CashDto(".25", 0, BigDecimal.ZERO))
		cash.add(new CashDto(".1", 0, BigDecimal.ZERO))
		cash.add(new CashDto(".05", 0, BigDecimal.ZERO))

		def dataSourceTable = new JRBeanCollectionDataSource(cash)

		parameters.put("tablesource", dataSourceTable)

		// shift
		def formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")
		def encashmentformatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
		parameters.put("title", "Daily Collection Report")


		// Fill Report Items

		List<DailySalesTRItems> items = []
		List<DailySalesTRItems> voidedItems = []
		List<DailySalesTRItems> cards = []
		List<DailySalesTRItems> checks = []
		List<DailySalesTRItems> deposits = []
		List<CheckEncashmentItems> encashmentItems = []
		List<CheckEncashmentItems> returnedEncashmentItems = []



		// Calculate Total Amount
		Map<String,BigDecimal> totalCalculations = [:]
		totalCalculations['CHECK'] = 0.0
		totalCalculations['BANKDEPOSIT'] = 0.0
		totalCalculations['CASH'] = 0.0
		totalCalculations['CARD'] = 0.0
		totalCalculations['EWALLET'] = 0.0
		totalCalculations['totalchange'] = 0.0
		totalCalculations['totalchecksEncashment'] = 0.0

		Map<String,BigDecimal> receiptsObj = [:]

		Map<String,String> userID = [:]

		// Receipts
		(shift.payments.findAll { PaymentTracker a -> BooleanUtils.isNotTrue(a.voided) }.toSorted {
			PaymentTracker a, PaymentTracker b ->
				Integer.valueOf(a.ornumber) <=> Integer.valueOf(b.ornumber)
		}).each {
			pt ->
				pt.paymentDetails.each {
					ptd ->
						def dCTRItems = addDailySalesTRItems(receiptsObj,pt,ptd,cards,checks,deposits,totalCalculations,userID)
						if(dCTRItems)
							items << dCTRItems
				}
				totalCalculations['totalchange'] += pt.change
		}

		// Voided Receipts
		(shift.payments.findAll { PaymentTracker a -> BooleanUtils.isTrue(a.voided) }.toSorted {
			PaymentTracker a, PaymentTracker b ->
				Integer.valueOf(a.ornumber) <=> Integer.valueOf(b.ornumber)
		}).each {
			pt ->
				pt.paymentDetails.each {
					ptd ->
						def dCTRItems = addDailySalesTRItems(receiptsObj,pt,ptd,cards,checks,deposits,totalCalculations,userID,true)
						if(dCTRItems)
							voidedItems << dCTRItems
				}
		}

		encashmentShift.each {
			ce ->
				def userCreatedBy = userRepository.findOneByLogin(ce.createdBy)
				def createdEmp = employeeRepository.findOneByUser(userCreatedBy)
				def recordNo = ce.recordNo.replaceFirst('^0+(?!$)', "")

				if(ce.returnedShifting) {
					if (ce.shifting.id == shiftId && ce.returnedShifting.id != shiftId) {
						totalCalculations['totalchecksEncashment'] += ce.amount
						encashmentItems << new CheckEncashmentItems(
								ce.bank.bankname,
								ce.chequeNo ?: "",
								encashmentformatter.format(ce.chequeDate.atZone(ZoneId.systemDefault())),
								recordNo,
								createdEmp.fullName,
								ce.amount
						)
					}

					if (ce.returnedShifting.id == shiftId && ce.shifting.id != shiftId) {
						totalCalculations['CASH'] += ce.amount
						returnedEncashmentItems << new CheckEncashmentItems(
								ce.bank.bankname,
								ce.chequeNo ?: "",
								encashmentformatter.format(ce.transactionDate.atZone(ZoneId.systemDefault())),
								recordNo,
								createdEmp.fullName,
								ce.amount
						)
					}
				}
				else {
					if(ce.shifting.id == shiftId) {
						totalCalculations['totalchecksEncashment'] += ce.amount
						encashmentItems << new CheckEncashmentItems(
								ce.bank.bankname,
								ce.chequeNo ?: "",
								encashmentformatter.format(ce.transactionDate.atZone(ZoneId.systemDefault())),
								recordNo,
								createdEmp.fullName,
								ce.amount
						)
					}
				}
		}


		parameters.put("totalchecks", totalCalculations['CHECK'])
		parameters.put("totalbankdeposit", totalCalculations['BANKDEPOSIT'])
		parameters.put("total_e_wallet", totalCalculations['EWALLET'])
		parameters.put("totalcards", totalCalculations['CARD'])
		parameters.put("totalhardcash", totalCalculations['CASH'] + totalCalculations['totalchange'])
		parameters.put("totalCheckEncashment", totalCalculations['totalchecksEncashment'])
		parameters.put("netRemainingCash", (totalCalculations['CASH'] + totalCalculations['totalchange']) - totalCalculations['totalchecksEncashment'])

		BigDecimal amountReceived = totalCalculations['EWALLET'] + totalCalculations['CHECK'] + totalCalculations['CARD'] + (totalCalculations['CASH'] + totalCalculations['totalchange']) + totalCalculations['BANKDEPOSIT']
		parameters.put("totalamountreceived", amountReceived)

		def username = shift.createdBy
		def user = userRepository.findOneByLogin(username)
		def emp = employeeRepository.findOneByUser(user)

		parameters.put("preparedby", emp.fullName)
		parameters.put("preparedbyempno", emp.employeeNo)

		def dto = new DailySalesFieldsDTO(
				shiftno: shift.shiftno ?: "",
				shiftstart: shift.startshift ? formatter.format(shift.startshift.atZone(ZoneId.systemDefault())) : "",
				shiftend: shift.endshift ? formatter.format(shift.endshift.atZone(ZoneId.systemDefault())) : "",
				status: shift.active ? "OPEN" : "CLOSED",
				terminal:shift.terminal?.terminalId ?: "",
				terminalremarks: shift.terminal?.remarks ?: "",
		)

		def gson = new Gson()
		def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
		def dataSource = new JsonDataSource(dataSourceByteArray)


		List<Map<String,Object>> receipts_summary_items = []
		receiptsObj.each {key , value ->
			Map<String,Object> summaryItems = [:]
			summaryItems['label'] = key
			summaryItems['amount'] = value
			receipts_summary_items.push(summaryItems)
		}

		cards = cards.sort { s1, s2 -> Integer.valueOf(s1.doc_no) <=> Integer.valueOf(s2.doc_no)}
		if(cards.size() > 0)
			parameters.put('receipts_card_items', new JRBeanCollectionDataSource(cards))

		checks = checks.sort { s1, s2 -> Integer.valueOf(s1.doc_no) <=> Integer.valueOf(s2.doc_no)}
		if(checks.size() > 0)
			parameters.put('receipts_checks_items', new JRBeanCollectionDataSource(checks))

		deposits = deposits.sort { s1, s2 -> Integer.valueOf(s1.doc_no) <=> Integer.valueOf(s2.doc_no)}
		if(deposits.size() > 0)
			parameters.put('receipts_bank_deposit_items', new JRBeanCollectionDataSource(deposits))

		encashmentItems = encashmentItems.sort { s1, s2 -> Integer.valueOf(s1.doc_no) <=> Integer.valueOf(s2.doc_no)}
		if(encashmentItems.size() > 0)
			parameters.put('check_encashments', new JRBeanCollectionDataSource(encashmentItems))

		returnedEncashmentItems = returnedEncashmentItems.sort { s1, s2 -> Integer.valueOf(s1.doc_no) <=> Integer.valueOf(s2.doc_no)}
		if(returnedEncashmentItems.size() > 0)
			parameters.put('returned_check_encashment', new JRBeanCollectionDataSource(returnedEncashmentItems))

		voidedItems = voidedItems.sort { s1, s2 -> Integer.valueOf(s1.doc_no) <=> Integer.valueOf(s2.doc_no)}
		if(voidedItems.size() > 0)
			parameters.put('receipts_voided_items', new JRBeanCollectionDataSource(voidedItems))

		receipts_summary_items = receipts_summary_items.sort { s1, s2 -> s1['label'] as String <=> s2['label'] as String}
		if(receipts_summary_items.size() > 0)
			parameters.put('receipts_summary_items', new JRBeanCollectionDataSource(receipts_summary_items))

		items = items.sort { s1, s2 -> Integer.valueOf(s1.doc_no) <=> Integer.valueOf(s2.doc_no)}
		if(items.size() > 0)
			parameters.put('receipts_items', new JRBeanCollectionDataSource(items))

		List<AcknowledgementFieldsDto> acknowledge_items = []
		acknowledge_items.push(new AcknowledgementFieldsDto('Overage (Shortage)'))
		acknowledge_items.push(new AcknowledgementFieldsDto('Less: Cash on Hand Accountability'))
		acknowledge_items.push(new AcknowledgementFieldsDto('Cash on Hand Turn-over'))
		parameters.put('acknowledgement_items',new JRBeanCollectionDataSource(acknowledge_items))

		try {
			def jrprint = JasperFillManager.fillReport(res.inputStream, parameters,dataSource)

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
		params.add("Content-Disposition", "inline;filename=DailyCollection-${shift.shiftno}.pdf".toString())
		return new ResponseEntity(data, params, HttpStatus.OK)

	}


	@RequestMapping(method = RequestMethod.GET, value = "/downloadSalesReportForShift")
	Callable<ResponseEntity<byte []>> downloadSalesReportForShift(
			@RequestParam UUID shiftId
	) {
		return new Callable<ResponseEntity<byte[]>>() {
			@Override
			ResponseEntity<byte[]> call() throws Exception {
				def encashmentShift = chequeEncashmentServices.cheqEncashmentByOneShiftId(shiftId)


				StringBuffer buffer = new StringBuffer()

				CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV.withHeader(
						"Allied Care Experts (ACE) Medical Center-Bohol, Inc."))
				csvPrinter.printRecord('COLLECTION REPORT')
				csvPrinter.printRecord('')
				csvPrinter.printRecord('Shift Number', 'Date SFT Start', 'Date SFT End','SFT Status','Terminal ID','OR Number','AR Number','Name of Payee','Transaction Description','Mode of Payment','Card Number','Check Number','Bank Deposit Number','Amount')

				def recordsRaw = namedParameterJdbcTemplate.queryForList(
						"""
				select  
				s.shiftno,
				to_char(date(s.startshift+interval'8 hours'),'YYYY-MM-DD') as "sftStart",
				to_char(date(s.endshift+interval'8 hours'),'YYYY-MM-DD') as "sftEnd",
				case when s.active then 'OPEN' else 'CLOSED' end as "sftStatus",
				concat(c.terminal_id,' ',c.remarks) as "terminalId",
				pt.receipt_type,
				pt.ornumber,
				split_part(pt.description,'-','2') as "payee",
				bi.description,
				ptd."type",
				ptd.reference,
				ptd.amount
				from cashiering.payment_tracker_details ptd 
				left join cashiering.payment_tracker pt on pt.id  = ptd.payment_tracker
				left join cashiering.shifting s on s.id = pt.shiftid
				left join cashiering.cashierterminals c on c.id  = s.cashier 
				left join billing.billing_item_details bid on cast(bid.field_value as UUID) = pt.id  and bid.field_name = 'PAYTRACKER_ID'
				left join billing.billing_item bi on bi.id = bid.billingitem 
				where (pt.voided is null or pt.voided is false )
				and pt.shiftid = :shiftId	
				"""
				,
				[
						shiftId:shiftId
				])

				recordsRaw.each {

					String shiftNumber = it.get("shiftno","") as String
					String dateSFTStart = it.get("sftStart","") as String
					String dateSFTEnd = it.get("sftEnd","") as String
					String SFTStatus = it.get("sftStatus","") as String
					String terminalID = it.get("terminalId","") as String
					String orNumber = ''
					String arNumber = ''

					String receiptNo = it.get("ornumber","") as String
					String receipt_type = it.get("receipt_type","") as String

					switch (receipt_type){
						case 'OR':
							orNumber = receiptNo;
							break;
						case 'AR':
							arNumber = receiptNo;
							break;
						default:
							break;
					}


					String nameOfPayee = it.get("payee","") as String
					String transactionDescription = it.get("shiftno","") as String
					String modeOfPayment =  it.get("type","") as String
					String cardNumber = ''
					String checkNumber = ''
					String depositNumber = ''

					String reference = it.get("reference","")
					switch (modeOfPayment){
						case 'CARD':
							cardNumber = reference;
							break;
						case 'CHECK':
							checkNumber = reference;
							break;
						case 'BANKDEPOSIT':
							depositNumber = reference;
							break;
						default:
							break;
					}

					String amount =  it.get("amount","") as String
					csvPrinter.printRecord(shiftNumber, dateSFTStart, dateSFTEnd,SFTStatus,terminalID,orNumber,arNumber,nameOfPayee,transactionDescription,modeOfPayment,cardNumber,checkNumber,depositNumber,amount)

				}

				LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
				extHeaders.add("Content-Disposition",
						"attachment;filename=collection-report.csv".toString())
				return new ResponseEntity(buffer.toString().getBytes(), extHeaders, HttpStatus.OK)
			}
		}
	}


	@RequestMapping(method = RequestMethod.GET, value = "/csvDownloadsDailyCollectionReports")
	Callable<ResponseEntity<byte[]>> csvDownloadsDailyCollectionReports(
			@RequestParam String filter,
			@RequestParam String filterType,
			@RequestParam String terminalId,
			@RequestParam String collectionStartDate,
			@RequestParam String collectionEndDate,
			@RequestParam String paymentType,
			@RequestParam String transactionCategory
	) {
		return new Callable<ResponseEntity<byte[]>>() {
			@Override
			ResponseEntity<byte[]> call() throws Exception {
				def terminalIdUUID = terminalId ? UUID.fromString(terminalId) : null
				def transactions = paymentTrackerDetailsServices.getAllCashieringCollectionReport(
						filter,terminalIdUUID,collectionStartDate,collectionEndDate,paymentType,transactionCategory,filterType)
				StringBuffer buffer = new StringBuffer()

				BigDecimal investor = 0.00
				BigDecimal ipd = 0.00
				BigDecimal opd = 0.00
				BigDecimal otc = 0.00
				BigDecimal erd = 0.00
				BigDecimal totalCheckEncashment = 0.00
				BigDecimal totalChecks = 0.00
				BigDecimal totalCard = 0.00
				BigDecimal totalCash = 0.00
				BigDecimal totalEWallet = 0.00
				BigDecimal totalBankDeposit = 0.00
				BigDecimal remainingHardCash = 0.00

				CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV.withHeader(
						"Allied Care Experts (ACE) Medical Center-Bohol, Inc."))
				csvPrinter.printRecord('COLLECTION REPORT')
				csvPrinter.printRecord('')

				csvPrinter.printRecord('Shift Number', 'Date SFT Start', 'Date SFT End','Terminal ID', 'Collection Date','Doc Type','Doc No.','Ref.','Name of Payee','Payment Description','Mode of Payment','User ID','Amount')

				transactions.each {

					String shiftNumber = it.shiftNo as String
					String dateSFTStart = it.startShift as String
					String dateSFTEnd = it.endShift as String
					String terminalID = it.terminalId as String
					String collDate = it.collectionDate as String
					String docType = it.docType
					String docNo = it.docNo
					String reference = it.reference
					String nameOfPayee = it.payee as String
					String transactionDescription = it.paymentDescription as String
					String modeOfPayment =  it.type as String
					String userID = it.userId
					BigDecimal amount =  it.amount

					if(transactionDescription.equalsIgnoreCase('INVESTOR')) {
						investor += amount

						if(reference){
							reference = reference.replace("[", "")
									.replace("]", "")
									.replaceAll("\\s", "")
									.replaceAll('"', "")
							reference = reference.toString()
						}
					}
					if(transactionDescription.equalsIgnoreCase('OTC'))
						otc += amount
					if(transactionDescription.equalsIgnoreCase('ERD'))
						erd += amount
					if(transactionDescription.equalsIgnoreCase('IPD') || transactionDescription.equalsIgnoreCase('IPD-PP'))
						ipd += amount
					if(transactionDescription.equalsIgnoreCase('OPD') || transactionDescription.equalsIgnoreCase('OPD-PP'))
						opd += amount

					if(modeOfPayment.equalsIgnoreCase('CHECK'))
						totalChecks += amount
					if(modeOfPayment.equalsIgnoreCase('CASH'))
						totalCash += amount
					if(modeOfPayment.equalsIgnoreCase('CARD'))
						totalCard += amount
					if(modeOfPayment.equalsIgnoreCase('BANKDEPOSIT'))
						totalBankDeposit += amount
					if(modeOfPayment.equalsIgnoreCase('EWALLET'))
						totalEWallet += amount

					remainingHardCash = totalCash - totalCheckEncashment

					csvPrinter.printRecord(shiftNumber, dateSFTStart, dateSFTEnd,terminalID,collDate,docType,docNo,reference,nameOfPayee,transactionDescription,modeOfPayment,userID,amount)

				}

				csvPrinter.printRecord('')
				csvPrinter.printRecord('SUMMARY:')
				csvPrinter.printRecord('INVESTOR',investor,'Total Checks:',totalChecks,'','','','Total Hard Cash',totalCash)
				csvPrinter.printRecord('IPD',ipd,'Total Card:',totalCard,'','','','Total Check Encashment',totalCheckEncashment)
				csvPrinter.printRecord('OPD',opd,'Total Bank Deposits:',totalBankDeposit,'','','','Net Remaining Hard Cash',remainingHardCash)
				csvPrinter.printRecord('OTC',otc,'Total E-Wallet:',totalEWallet)
				csvPrinter.printRecord('')


				LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
				extHeaders.add("Content-Disposition",
						"attachment;filename=collection-report.csv".toString())
				return new ResponseEntity(buffer.toString().getBytes(), extHeaders, HttpStatus.OK)
			}
		}
	}
	
}
