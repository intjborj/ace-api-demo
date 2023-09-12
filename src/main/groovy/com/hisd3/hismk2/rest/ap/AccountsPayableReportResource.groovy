package com.hisd3.hismk2.rest.ap

import com.google.gson.Gson
import com.hisd3.hismk2.domain.accounting.AccountsPayable
import com.hisd3.hismk2.domain.accounting.HeaderLedger
import com.hisd3.hismk2.domain.accounting.Ledger
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.inventory.SupplierType
import com.hisd3.hismk2.graphqlservices.accounting.AccountsPayableDetialServices
import com.hisd3.hismk2.graphqlservices.accounting.AccountsPayableServices
import com.hisd3.hismk2.graphqlservices.accounting.ApReadersFeeServices
import com.hisd3.hismk2.graphqlservices.accounting.DebitMemoService
import com.hisd3.hismk2.graphqlservices.accounting.DisbursementApServices
import com.hisd3.hismk2.graphqlservices.accounting.DisbursementCheckServices
import com.hisd3.hismk2.graphqlservices.accounting.DisbursementServices
import com.hisd3.hismk2.graphqlservices.accounting.LedgerServices
import com.hisd3.hismk2.graphqlservices.accounting.ReapplicationService
import com.hisd3.hismk2.graphqlservices.accounting.Wtx2307ConsolidatedService
import com.hisd3.hismk2.graphqlservices.accounting.Wtx2307Service
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.inventory.*
import com.hisd3.hismk2.rest.dto.ApAgingDetailedDto
import com.hisd3.hismk2.rest.dto.ApAgingSummaryDto
import com.hisd3.hismk2.security.SecurityUtils
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
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
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm
import org.apache.pdfbox.pdmodel.interactive.form.PDField
import org.apache.xmlbeans.impl.xb.xsdschema.AnyDocument
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import java.nio.charset.Charset
import java.text.DecimalFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

@TypeChecked
@RestController
@RequestMapping('/reports/ap/print')
class AccountsPayableReportResource {
	
	@Autowired
	ApplicationContext applicationContext

	@Autowired
	AccountsPayableServices accountsPayableServices

	@Autowired
	DisbursementServices disbursementServices

	@Autowired
	DisbursementApServices disbursementApServices

	@Autowired
	DisbursementCheckServices disbursementCheckServices

	@Autowired
	AccountsPayableDetialServices accountsPayableDetialServices

	@Autowired
	Wtx2307Service wtx2307Service

	@Autowired
	EmployeeRepository employeeRepository

	@Autowired
	ReapplicationService reapplicationService
	
	@Autowired
	ItemRepository itemRepository
	
	@Autowired
	DepartmentRepository departmentRepository

	@Autowired
	LedgerServices ledgerServices

	@Autowired
	HospitalConfigService hospitalConfigService

	@Autowired
	Wtx2307ConsolidatedService wtx2307ConsolidatedService

	@Autowired
	DebitMemoService debitMemoService

	@Autowired
	ApReadersFeeServices apReadersFeeServices

	@Autowired
	JdbcTemplate jdbcTemplate

	@Autowired
	SupplierTypeRepository supplierTypeRepository

	
	@RequestMapping(value = ['/apv/{id}'], produces = ['application/pdf'])
	ResponseEntity<byte[]> apvReport(@PathVariable('id') UUID id) {

		AccountsPayable ap = accountsPayableServices.apById(id)
		def details = accountsPayableDetialServices.detailsByAp(id)
		def accounts = new HeaderLedger()
		if(ap.postedLedger){
			accounts = ledgerServices.findOne(ap.postedLedger)
		}


		Employee emp = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()

		def res = applicationContext?.getResource("classpath:/reports/ap/apv.jasper")
		def bytearray = new ByteArrayInputStream()
		def os = new ByteArrayOutputStream()
		def parameters = [:] as Map<String, Object>
		def logo = applicationContext?.getResource("classpath:/reports/logo.png")
		def itemsDto = new ArrayList<ApvItemsDto>()
		def je = new ArrayList<JEntriesDto>()

		DateTimeFormatter dateFormat =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())

		def dto = new HeaderDto(
				date: dateFormat.format(ap?.apvDate),
				supplier: ap?.supplier?.supplierFullname,
				refNo: ap?.apNo,
				employee: emp.fullName,
		)

		def gson = new Gson()
		def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
		def dataSource = new JsonDataSource(dataSourceByteArray)

		if (ap) {
			if(ap.apCategory.equalsIgnoreCase("ACCOUNTS PAYABLE")){
				def apv = new ApvItemsDto(
						date: dateFormat.format(ap?.dueDate),
						//refNo: ap?.apNo,
						refNo: ap?.invoiceNo,
						ewt: ap?.ewtAmount,
						vat: ap?.vatAmount,
						description: ap?.remarksNotes ? ap?.remarksNotes : "",
						amount: ap.netOfVat
				)
				itemsDto.add(apv)
			}else{
				details.each {
					def r = it.remarksNotes.split("\\[|]")
					def apv = new ApvItemsDto(
							date: dateFormat.format(ap?.dueDate),
							refNo: it.refNo ? "SOA #"+it.refNo : r ? "SOA #"+r[1] : "",
							ewt: it.ewtAmount,
							vat: it.vatAmount,
							description: it?.remarksNotes ? it?.remarksNotes : "",
							amount: it.amount
					)
					itemsDto.add(apv)
				}
			}

		}

		if(accounts){
			Set<Ledger> ledger = new HashSet<Ledger>(accounts.ledger);
			ledger.each {
				def list = new JEntriesDto(
						date: dateFormat.format(accounts.transactionDate),
						docNo: accounts.docType.name()+'-'+accounts.docnum,
						actCode: it.journalAccount.code,
						actName: it.journalAccount.description,
						debit: it.debit,
						credit: it.credit
				)
				je.add(list)
			}
		}

		if (logo.exists()) {
			parameters.put("logo", logo?.getURL())
		}

		if (itemsDto) {
			parameters.put('items', new JRBeanCollectionDataSource(itemsDto))
		}

		if (je) {
			parameters.put('jeItems', new JRBeanCollectionDataSource(je.sort{it.credit}))
		}

		//printing
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
		//end

		def data = os.toByteArray()
		def params = new LinkedMultiValueMap<String, String>()
		//params.add("Content-Disposition", "inline;filename=Discharge-Instruction-of-\"" + caseDto?.patient?.fullName + "\".pdf")
		params.add("Content-Disposition", "inline;filename=APV-of-\"" + ap?.apNo + "\".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)
	}

	@RequestMapping(value = ['/disbursement/{id}'], produces = ['application/pdf'])
	ResponseEntity<byte[]> disReport(@PathVariable('id') UUID id) {

		def dis = disbursementServices.disbursementById(id)
		def ap = disbursementApServices.apAppByDis(id)
		def checks = disbursementCheckServices.disCheckByParent(id)
		def accounts = new HeaderLedger()
		if(dis.postedLedger){
			accounts = ledgerServices.findOne(dis.postedLedger)
		}

		Employee emp = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()

		def res = applicationContext?.getResource("classpath:/reports/ap/checkcash.jasper")
		def bytearray = new ByteArrayInputStream()
		def os = new ByteArrayOutputStream()
		def parameters = [:] as Map<String, Object>
		def logo = applicationContext?.getResource("classpath:/reports/logo.png")
		def itemsDto = new ArrayList<ApvItemsDto>()
		def je = new ArrayList<JEntriesDto>()
		def banks = new ArrayList<BankDto>()

		DateTimeFormatter dateFormat =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())

		def dto = new HeaderDto(
				date: dateFormat.format(dis?.disDate),
				supplier: dis?.supplier?.supplierFullname,
				refNo: dis?.disNo,
				employee: emp.fullName,
				particulars: "Particular: ${dis.remarksNotes ?: ""}"
		)

		def gson = new Gson()
		def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
		def dataSource = new JsonDataSource(dataSourceByteArray)

		if (ap) {
			ap.each {
				def apv = new ApvItemsDto(
						date: dateFormat.format(it.payable?.apvDate),
						refNo: it.payable?.apNo,
						description: it.payable?.remarksNotes ? it.payable?.remarksNotes : "",
						origAmount: it.payable?.netOfVat,
						amount: it.payable?.appliedAmount,
				)
				itemsDto.add(apv)
			}
		}

		if (checks) {
			checks.each {
				def bank = new BankDto(
						date: dateFormat.format(it.checkDate),
						checkNo: it.checkNo,
						bank: it.bank?.bankname,
						branch: it.bankBranch ? it.bankBranch : "",
						amount: it.amount,
				)
				banks.add(bank)
			}
		}

		if(accounts){
			Set<Ledger> ledger = new HashSet<Ledger>(accounts.ledger);
			ledger.each {
				def list = new JEntriesDto(
						date: dateFormat.format(accounts.transactionDate),
						docNo: accounts.docType.name()+'-'+accounts.docnum,
						actCode: it.journalAccount.code,
						actName: it.journalAccount.description,
						debit: it.debit,
						credit: it.credit
				)
				je.add(list)
			}
		}

		parameters.put("type", dis.disType)

		if (logo.exists()) {
			parameters.put("logo", logo?.getURL())
		}

		if (itemsDto) {
			parameters.put('items', new JRBeanCollectionDataSource(itemsDto))
		}

		if (je) {
			parameters.put('jeItems', new JRBeanCollectionDataSource(je.sort{it.credit}))
		}

		if (banks) {
			parameters.put('banks', new JRBeanCollectionDataSource(banks))
		}

		//printing
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
		//end

		def data = os.toByteArray()
		def params = new LinkedMultiValueMap<String, String>()
		//params.add("Content-Disposition", "inline;filename=Discharge-Instruction-of-\"" + caseDto?.patient?.fullName + "\".pdf")
		params.add("Content-Disposition", "inline;filename=Disbursement-of-\"" + dis?.disNo + "\".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)
	}

	@RequestMapping(value = ['/reapply/{id}'], produces = ['application/pdf'])
	ResponseEntity<byte[]> reapplyReport(@PathVariable('id') UUID id) {

		def dis = reapplicationService.reapplicationById(id)
		def ap = disbursementApServices.apReapplication(id)
		def accounts = new HeaderLedger()
		if(dis.postedLedger){
			accounts = ledgerServices.findOne(dis.postedLedger)
		}

		Employee emp = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()

		def res = applicationContext?.getResource("classpath:/reports/ap/reapply.jasper")
		def bytearray = new ByteArrayInputStream()
		def os = new ByteArrayOutputStream()
		def parameters = [:] as Map<String, Object>
		def logo = applicationContext?.getResource("classpath:/reports/logo.png")
		def itemsDto = new ArrayList<ApvItemsDto>()
		def je = new ArrayList<JEntriesDto>()
		//def banks = new ArrayList<BankDto>()

		DateTimeFormatter dateFormat =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())

		def dto = new HeaderDto(
				date: dateFormat.format(dis?.createdDate),
				supplier: dis?.supplier?.supplierFullname,
				refNo: dis?.disbursement?.disNo,
				employee: emp.fullName,
		)

		def gson = new Gson()
		def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
		def dataSource = new JsonDataSource(dataSourceByteArray)

		if (ap) {
			ap.each {
				def apv = new ApvItemsDto(
						date: dateFormat.format(it.payable?.apvDate),
						refNo: it.payable?.apNo,
						description: it.payable?.remarksNotes ? it.payable?.remarksNotes : "",
						origAmount: it.payable?.netOfVat,
						amount: it.payable?.appliedAmount,
				)
				itemsDto.add(apv)
			}
		}


		if(accounts){
			Set<Ledger> ledger = new HashSet<Ledger>(accounts.ledger);
			ledger.each {
				def list = new JEntriesDto(
						date: dateFormat.format(accounts.transactionDate),
						docNo: accounts.docType.name()+'-'+accounts.docnum,
						actCode: it.journalAccount.code,
						actName: it.journalAccount.description,
						debit: it.debit,
						credit: it.credit
				)
				je.add(list)
			}
		}

		if (logo.exists()) {
			parameters.put("logo", logo?.getURL())
		}

		if (itemsDto) {
			parameters.put('items', new JRBeanCollectionDataSource(itemsDto))
		}

		if (je) {
			parameters.put('jeItems', new JRBeanCollectionDataSource(je.sort{it.credit}))
		}

		//printing
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
		//end

		def data = os.toByteArray()
		def params = new LinkedMultiValueMap<String, String>()
		//params.add("Content-Disposition", "inline;filename=Discharge-Instruction-of-\"" + caseDto?.patient?.fullName + "\".pdf")
		params.add("Content-Disposition", "inline;filename=Reapply-of-\"" + dis?.disbursement?.disNo + "\".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)
	}

	@RequestMapping(value = ['/debit-advice/{id}'], produces = ['application/pdf']) //debit memo and advice
	ResponseEntity<byte[]> debitAdviceReport(@PathVariable('id') UUID id) {

		def dm = debitMemoService.debitMemoById(id)
		def ap = disbursementApServices.apDebitMemo(id)
		def accounts = new HeaderLedger()
		if(dm.postedLedger){
			accounts = ledgerServices.findOne(dm.postedLedger)
		}

		Employee emp = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()

		def res = applicationContext?.getResource("classpath:/reports/ap/debit_advice.jasper")
		def bytearray = new ByteArrayInputStream()
		def os = new ByteArrayOutputStream()
		def parameters = [:] as Map<String, Object>
		def logo = applicationContext?.getResource("classpath:/reports/logo.png")
		def itemsDto = new ArrayList<ApvItemsDto>()
		def je = new ArrayList<JEntriesDto>()

		DateTimeFormatter dateFormat =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())

		def dto = new HeaderDto(
				date: dateFormat.format(dm?.debitDate),
				supplier: dm?.supplier?.supplierFullname,
				refNo: dm?.debitNo,
				particulars: "Particulars: ${dm?.remarksNotes}",
				employee: emp.fullName,
		)

		def gson = new Gson()
		def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
		def dataSource = new JsonDataSource(dataSourceByteArray)

		if (ap) {
			ap.each {
				def apv = new ApvItemsDto(
						date: dateFormat.format(it.payable?.apvDate),
						refNo: it.payable?.apNo,
						description: it.payable?.remarksNotes ? it.payable?.remarksNotes : "",
						origAmount: it.payable?.netOfVat,
						amount: dm.debitType.equalsIgnoreCase("DEBIT_ADVICE") ? it.appliedAmount
						: dm.memoAmount,
				)
				itemsDto.add(apv)
			}
		}


		if(accounts){
			Set<Ledger> ledger = new HashSet<Ledger>(accounts.ledger);
			ledger.each {
				def list = new JEntriesDto(
						date: dateFormat.format(accounts.transactionDate),
						docNo: accounts.docType.name()+'-'+accounts.docnum,
						actCode: it.journalAccount.code,
						actName: it.journalAccount.description,
						debit: it.debit,
						credit: it.credit
				)
				je.add(list)
			}
		}

		parameters.put('title', dm.debitType.equalsIgnoreCase("DEBIT_MEMO") ? "Debit Memo Voucher" : "Debit Advice Voucher")

		if (logo.exists()) {
			parameters.put("logo", logo?.getURL())
		}

		if (itemsDto) {
			parameters.put('items', new JRBeanCollectionDataSource(itemsDto))
		}

		if (je) {
			parameters.put('jeItems', new JRBeanCollectionDataSource(je.sort{it.credit}))
		}

		//printing
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
		//end

		def data = os.toByteArray()
		def params = new LinkedMultiValueMap<String, String>()
		//params.add("Content-Disposition", "inline;filename=Discharge-Instruction-of-\"" + caseDto?.patient?.fullName + "\".pdf")
		params.add("Content-Disposition", "inline;filename=Debit-Memo-of\"" + dm?.debitNo + "\".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)
	}

	@RequestMapping(value = ['/check/{id}/{words}'], produces = ['application/pdf'])
	ResponseEntity<byte[]> printCheck(@PathVariable('id') UUID id, @PathVariable('words') String words) {

		def check = disbursementCheckServices.disCheckById(id)


		def res = applicationContext?.getResource("classpath:/reports/ap/printcheck.jasper")
		def bytearray = new ByteArrayInputStream()
		def os = new ByteArrayOutputStream()
		def parameters = [:] as Map<String, Object>

		DateTimeFormatter dateFormat =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
		//
		def date = dateFormat.format(check.checkDate)
		def md = date.split('/');
		def dto = new PrintCheckDto(
				date: " ${md[0].charAt(0)}  ${md[0].charAt(1)}    ${md[1].charAt(0)}  ${md[1].charAt(1)}     ${md[2].charAt(0)}  ${md[2].charAt(1)}  ${md[2].charAt(2)}  ${md[2].charAt(3)}",
				payee: check.disbursement?.payeeName ? "**"+check.disbursement.payeeName.toUpperCase()+"**" : "",
				amountWords: "**${words}**",
				amount: "**"+new DecimalFormat("#,##0.00").format(check.amount)+"**",
		)

		def gson = new Gson()
		def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
		def dataSource = new JsonDataSource(dataSourceByteArray)


		//printing
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
		//end

		def data = os.toByteArray()
		def params = new LinkedMultiValueMap<String, String>()
		//params.add("Content-Disposition", "inline;filename=Discharge-Instruction-of-\"" + caseDto?.patient?.fullName + "\".pdf")
		params.add("Content-Disposition", "inline;filename=check-of-\"" + check.checkNo + "\".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)
	}


	@RequestMapping(value = ['/2307/{id}'], produces = ['application/pdf'])
	ResponseEntity<byte[]> print2307(@PathVariable('id') UUID id) {
		def hosp = hospitalConfigService.getHospitalInfo()
		def wtx = wtx2307Service.wtxById(id)
		def cf1 = applicationContext.getResource("classpath:/reports/ap/2307_2020-F.pdf") //  16832307_Final_1.pdf
		def pdfdoc = PDDocument.load(cf1.inputStream)

		DateTimeFormatter dateFormat =
				DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault())

		DateTimeFormatter stringFormat =
				DateTimeFormatter.ofPattern("MMddyyyy").withZone(ZoneId.systemDefault())

		LocalDate localDateStart = LocalDate.parse(dateFormat.format(wtx.wtxDate));
		LocalDate localDateEnd = LocalDate.parse(dateFormat.format(wtx.wtxDate));

		def startMonth = localDateStart.with(TemporalAdjusters.firstDayOfMonth())
		def endMonth = localDateEnd.with(TemporalAdjusters.lastDayOfMonth())

		PDAcroForm pDAcroForm = pdfdoc.getDocumentCatalog().getAcroForm()
		def a = 0;
		for(char c: stringFormat.format(startMonth).toCharArray()){
			PDField field = pDAcroForm.getField("period_from_${a}")
			field.setValue("${c}")
			a++
		}

		def b = 0;
		for(char c: stringFormat.format(endMonth).toCharArray()){
			PDField field = pDAcroForm.getField("period_to_${b}")
			field.setValue("${c}")
			b++
		}

		def d = 0;
		for(char c: wtx.supplier.supplierTin.toCharArray()){
			PDField field = pDAcroForm.getField("recipient_tin_${d}")
			field.setValue("${c}")
			d++
		}

		PDField supplierName = pDAcroForm.getField("payees_name")
		supplierName.setValue("${wtx.supplier.supplierFullname}")

		PDField supplierAddress = pDAcroForm.getField("payees_registered_address")
		supplierAddress.setValue("${wtx.supplier.primaryAddress}")

		//payees_zip_0 to 3
		//supplier?.zipcode?.toCharArray()?.forEach{
		//	nameValueMap.put("payees_zip_$i",it.toString())
		//	i++
		//}

		//hospital tin 00872368200000
		def tin = "00872368200000"
		def e = 0;
		for(char c: tin.toCharArray()){
			PDField field = pDAcroForm.getField("agent_tin_${e}")
			field.setValue("${c}")
			e++
		}

		PDField hospName = pDAcroForm.getField("payors_name")
		hospName.setValue("${hosp.hospitalName.toUpperCase()}")

		PDField hospAddress = pDAcroForm.getField("payors_registered_address")
		hospAddress.setValue("${hosp.address}, ${hosp.addressLine2}, ${hosp.city} ${hosp.street}, ${hosp.country}".toUpperCase())

		//numbers
		PDField atc = pDAcroForm.getField("atc_0")
		atc.setValue("${wtx.supplier.atcNo}")

		if(wtx.gross == wtx.netVat){
			PDField amount = pDAcroForm.getField("amt_income_0")
			amount.setValue("${new DecimalFormat("#,##0.00").format(wtx.gross - wtx.vatAmount)}")

			PDField totalAmount = pDAcroForm.getField("amt_income_13")
			totalAmount.setValue("${new DecimalFormat("#,##0.00").format(wtx.gross - wtx.vatAmount)}")
		}else{
			PDField amount = pDAcroForm.getField("amt_income_0")
			amount.setValue("${new DecimalFormat("#,##0.00").format(wtx.gross)}")

			PDField totalAmount = pDAcroForm.getField("amt_income_13")
			totalAmount.setValue("${new DecimalFormat("#,##0.00").format(wtx.gross)}")
		}

		PDField ewt = pDAcroForm.getField("tax_quarter_0")
		ewt.setValue("${new DecimalFormat("#,##0.00").format(wtx.ewtAmount)}")

		PDField totalEwt = pDAcroForm.getField("tax_quarter_13")
		totalEwt.setValue("${new DecimalFormat("#,##0.00").format(wtx.ewtAmount)}")
		//end numbers

		//agent signaturies
		//payor  payor_position
		//def payorName = "BACKY BACAREZA, MD  CORPORATE TREASURER 237-249-824-000"
		//def payorName = "JULIA BERBA MAAPE, MD"
		//def payorName = "AMELIA P. VALDEZ, MD"
		def payorName = "MA. FERMINITA J. BERTULFO, MD  CORPORATE TREASURER 167-012-592-000"
		PDField payor = pDAcroForm.getField("payor")
		payor.setValue("${payorName.toUpperCase()}")

		PDField conforme = pDAcroForm.getField("conforme")
		conforme.setValue("${wtx.supplier.supplierFullname.toUpperCase()}")

		def f = 0;
		for(char c: hosp.zip.toCharArray()){
			PDField field = pDAcroForm.getField("payors_zip_${f}")
			field.setValue("${c}")
			f++
		}

		def outputStream = new ByteArrayOutputStream()
		pdfdoc.save(outputStream)
		pdfdoc.close()

		def data = outputStream.toByteArray()
		def params = new LinkedMultiValueMap<String, String>()
		//params.add("Content-Disposition", "inline;filename=Discharge-Instruction-of-\"" + caseDto?.patient?.fullName + "\".pdf")
		params.add("Content-Disposition", "inline;filename=2307-\"" + wtx.supplier.supplierFullname + "\".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)

	}

	@RequestMapping(value = ['/2307/consolidated/{id}'], produces = ['application/pdf'])
	ResponseEntity<byte[]> printConsolidated2307(@PathVariable('id') UUID id) {
		def hosp = hospitalConfigService.getHospitalInfo()
		def wtx = wtx2307ConsolidatedService.wtxConById(id)
		def wtxItems = wtx2307Service.wtxListByRef(id)
		def cf1 = applicationContext.getResource("classpath:/reports/ap/2307_2020-C.pdf") //  16832307_Final_1.pdf
		def pdfdoc = PDDocument.load(cf1.inputStream)

		DateTimeFormatter dateFormat =
				DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault())

		DateTimeFormatter stringFormat =
				DateTimeFormatter.ofPattern("MMddyyyy").withZone(ZoneId.systemDefault())

		LocalDate startMonth = LocalDate.parse(dateFormat.format(wtx.dateFrom));
		LocalDate endMonth = LocalDate.parse(dateFormat.format(wtx.dateTo));

		PDAcroForm pDAcroForm = pdfdoc.getDocumentCatalog().getAcroForm()
		def a = 0;
		for(char c: stringFormat.format(startMonth).toCharArray()){
			PDField field = pDAcroForm.getField("period_from_${a}")
			field.setValue("${c}")
			a++
		}

		def b = 0;
		for(char c: stringFormat.format(endMonth).toCharArray()){
			PDField field = pDAcroForm.getField("period_to_${b}")
			field.setValue("${c}")
			b++
		}

		def d = 0;
		for(char c: wtx.supplier.supplierTin.toCharArray()){
			PDField field = pDAcroForm.getField("recipient_tin_${d}")
			field.setValue("${c}")
			d++
		}

		PDField supplierName = pDAcroForm.getField("payees_name")
		supplierName.setValue("${wtx.supplier.supplierFullname}")

		PDField supplierAddress = pDAcroForm.getField("payees_registered_address")
		supplierAddress.setValue("${wtx.supplier.primaryAddress}")

		//payees_zip_0 to 3
		//supplier?.zipcode?.toCharArray()?.forEach{
		//	nameValueMap.put("payees_zip_$i",it.toString())
		//	i++
		//}

		//hospital tin 00872368200000
		def tin = "00872368200000"
		def e = 0;
		for(char c: tin.toCharArray()){
			PDField field = pDAcroForm.getField("agent_tin_${e}")
			field.setValue("${c}")
			e++
		}

		PDField hospName = pDAcroForm.getField("payors_name")
		hospName.setValue("${hosp.hospitalName.toUpperCase()}")

		PDField hospAddress = pDAcroForm.getField("payors_registered_address")
		hospAddress.setValue("${hosp.address}, ${hosp.addressLine2}, ${hosp.city} ${hosp.street}, ${hosp.country}".toUpperCase())

		//numbers
		PDField atc = pDAcroForm.getField("atc_0")
		atc.setValue("${wtx.supplier.atcNo}")

		def totalAmount = BigDecimal.ZERO
		def totalEwt = BigDecimal.ZERO

		//items
		def counter = 0
		wtxItems.each {
			if(it.gross == it.netVat){
				def gross = it.gross - it.vatAmount
				PDField amount = pDAcroForm.getField("amt_income_${counter}")
				amount.setValue("${new DecimalFormat("#,##0.00").format(gross)}")
				totalAmount+=gross
			}else{
				def gross = it.gross
				PDField amount = pDAcroForm.getField("amt_income_${counter}")
				amount.setValue("${new DecimalFormat("#,##0.00").format(it.gross)}")
				totalAmount+=gross
			}

			PDField ewt = pDAcroForm.getField("tax_quarter_${counter}")
			ewt.setValue("${new DecimalFormat("#,##0.00").format(it.ewtAmount)}")
			totalEwt+=it.ewtAmount

			counter++
		}

		PDField totalA = pDAcroForm.getField("amt_income_13")
		totalA.setValue("${new DecimalFormat("#,##0.00").format(totalAmount)}")

		PDField totalE = pDAcroForm.getField("tax_quarter_13")
		totalE.setValue("${new DecimalFormat("#,##0.00").format(totalEwt)}")

		//end items

		//agent signaturies
		//def payorName = "BACKY BACAREZA, MD  CORPORATE TREASURER 237-249-824-000"
		//def payorName = "JULIA BERBA MAAPE, MD"
		//def payorName = "AMELIA P. VALDEZ, MD"
		def payorName = "MA. FERMINITA J. BERTULFO, MD  CORPORATE TREASURER 167-012-592-000"
		PDField payor = pDAcroForm.getField("payor")
		payor.setValue("${payorName.toUpperCase()}")

		PDField conforme = pDAcroForm.getField("conforme")
		conforme.setValue("${wtx.supplier.supplierFullname.toUpperCase()}")

		def f = 0;
		for(char c: hosp.zip.toCharArray()){
			PDField field = pDAcroForm.getField("payors_zip_${f}")
			field.setValue("${c}")
			f++
		}

		def outputStream = new ByteArrayOutputStream()
		pdfdoc.save(outputStream)
		pdfdoc.close()

		def data = outputStream.toByteArray()
		def params = new LinkedMultiValueMap<String, String>()
		//params.add("Content-Disposition", "inline;filename=Discharge-Instruction-of-\"" + caseDto?.patient?.fullName + "\".pdf")
		params.add("Content-Disposition", "inline;filename=2307-Consolidated-\"" + wtx.supplier.supplierFullname + "\".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)

	}

	@RequestMapping(value = ['/agingSummary'], produces = ['application/pdf'])
	ResponseEntity<byte[]> printAgingSummary(
			@RequestParam String filter,
			@RequestParam String supplierTypes,
			@RequestParam Boolean posted
	) {
		UUID sup = null; def supType = new SupplierType()
		if(!supplierTypes.equalsIgnoreCase('null')){
			sup = UUID.fromString(supplierTypes)
		}

		if(sup){
			supType = supplierTypeRepository.findById(sup).get()
		}

		def summary = accountsPayableServices.apAgingSummary(filter, sup, posted)

		def res = applicationContext?.getResource("classpath:/reports/ap/agingsummary.jasper")
		def bytearray = new ByteArrayInputStream()
		def os = new ByteArrayOutputStream()
		def parameters = [:] as Map<String, Object>
		def logo = applicationContext?.getResource("classpath:/reports/logo.png")
		def itemsDto = summary
		//def banks = new ArrayList<BankDto>()

		DateTimeFormatter dateFormat =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())

		def dto = new HeaderDto(
				supplier: sup ? "Aging Summary Report - "+supType?.supplierTypeDesc : 'Aging Summary Report',
				//
		)

		def gson = new Gson()
		def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
		def dataSource = new JsonDataSource(dataSourceByteArray)



		if (logo.exists()) {
			parameters.put("logo", logo?.getURL())
		}

		if (itemsDto) {
			parameters.put('items', new JRBeanCollectionDataSource(itemsDto))
		}

		//printing
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
		//end

		def data = os.toByteArray()
		def params = new LinkedMultiValueMap<String, String>()
		//params.add("Content-Disposition", "inline;filename=Discharge-Instruction-of-\"" + caseDto?.patient?.fullName + "\".pdf")
		params.add("Content-Disposition", "inline;filename=Aging-Report-Summary-of-\"" + filter + "\".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)
	}

	@RequestMapping(value = ['/agingDetailed'], produces = ['application/pdf'])
	ResponseEntity<byte[]> printAgingDetailed(
			@RequestParam String filter,
			@RequestParam String supplier,
			@RequestParam String supplierTypes,
			@RequestParam Boolean posted
	) {
		UUID sup = null; UUID supId = null; def supType = new SupplierType()
		if(!supplierTypes.equalsIgnoreCase('null')){
			sup = UUID.fromString(supplierTypes)
		}

		if(!supplier.equalsIgnoreCase('null')){
			supId = UUID.fromString(supplier)
		}

		if(sup){
			supType = supplierTypeRepository.findById(sup).get()
		}

		def detailed = accountsPayableServices.apAgingDetailed(filter,supId, sup, posted)

		def res = applicationContext?.getResource("classpath:/reports/ap/agingdetailed.jasper")
		def bytearray = new ByteArrayInputStream()
		def os = new ByteArrayOutputStream()
		def parameters = [:] as Map<String, Object>
		def logo = applicationContext?.getResource("classpath:/reports/logo.png")
		def itemsDto = new ArrayList<ApAgingDetailedDto>()

		def dto = new HeaderDto(
				supplier: sup ? "Aging Detailed Report - "+supType?.supplierTypeDesc : 'Aging Detailed Report',
				//
		)

		def gson = new Gson()
		def dataSourceByteArray = new ByteArrayInputStream(gson.toJson(dto).bytes)
		def dataSource = new JsonDataSource(dataSourceByteArray)

		if(detailed){
			detailed.each {
				def aging = new ApAgingDetailedDto(
						ap_no: it.ap_no,
						supplier: it.supplier,
						invoice_date: it.invoice_date ? it.invoice_date : it.apv_date,
						due_date: it.due_date,
						invoice_no: it.invoice_no,
						current_amount: it.current_amount,
						day_1_to_31: it.day_1_to_31,
						day_31_to_60: it.day_31_to_60,
						day_61_to_90: it.day_61_to_90,
						day_91_to_120: it.day_91_to_120,
						older: it.older,
						total: it.total
				)
				itemsDto.add(aging)
			}
		}

		if (logo.exists()) {
			parameters.put("logo", logo?.getURL())
		}

		if (itemsDto) {
			parameters.put('items', new JRBeanCollectionDataSource(itemsDto))
		}

		//printing
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
		//end

		def data = os.toByteArray()
		def params = new LinkedMultiValueMap<String, String>()
		//params.add("Content-Disposition", "inline;filename=Discharge-Instruction-of-\"" + caseDto?.patient?.fullName + "\".pdf")
		params.add("Content-Disposition", "inline;filename=Aging-Report-Detailed-of-\"" + filter + "\".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)
	}

	// === download csv === //

	@RequestMapping(method = RequestMethod.GET, value = ["/rf/excel"])
	ResponseEntity<AnyDocument.Any> readersFeeDownload(
			@RequestParam String filter,
			@RequestParam String supplier,
			@RequestParam String dep,
			@RequestParam String start,
			@RequestParam String end,
			@RequestParam Boolean status
	) {
		UUID sup = supplier.equalsIgnoreCase("undefined") ? null : UUID.fromString(supplier)
		UUID department = dep.equalsIgnoreCase("undefined") ? null : UUID.fromString(dep)

		def list = apReadersFeeServices.apReadersList(filter, sup, department, start, end, status)
		StringBuffer buffer = new StringBuffer()

		DateTimeFormatter formatter =
				DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault())
		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV
				.withHeader("DATE","PATIENT","DESCRIPTION","DEPARTMENT","SUPPLIER/PAYEE","BASE PRICE","PRICE TIER","CHARGE PRICE","RF (%)","READERS FEE","TYPE"))

		try {
			list.each {
				item ->
					csvPrinter.printRecord(
							formatter.format(item.transactionDate),
							"[${item.billing?.billingNo}] ${item.billing?.patient?.fullName}",
							item.description,
							item.department?.departmentName,
							item.supplier?.supplierFullname,
							item.service?.basePrice,
							item.pricingTier?.description,
							item.price,
							item.percentage,
							item.rfFee,
							item.registryTypeCharged
					)
			}

			LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
			extHeaders.add("Content-Disposition",
					"attachment;filename=ReadersFee.csv".toString())

			return new ResponseEntity(buffer.toString().getBytes(), extHeaders, HttpStatus.OK)

		}
		catch (e) {
			throw e
		}

	}

	@RequestMapping(method = RequestMethod.GET, value = ["/agingSummary/csv"])
	ResponseEntity<AnyDocument.Any> agingSummaryDownload(
			@RequestParam String filter,
			@RequestParam String supplierTypes,
			@RequestParam Boolean posted
	) {

		String sql = """select supplier_id as id,supplier,supplier_type_id,supplier_type,sum(current_amount) as current_amount,
sum(day_1_to_31) as day_1_to_31,sum(day_31_to_60) as day_31_to_60,sum(day_61_to_90) as day_61_to_90,sum(day_91_to_120) as day_91_to_120,
sum(older) as older,sum(total) as total from accounting.aging_report(?::date) where supplier like '%%' """

		if(posted != null){
			sql+= """ and (posted = ${posted} or posted is null) """
		}

		if(supplierTypes != null && !supplierTypes.equalsIgnoreCase('null')){
			sql+= """ and supplier_type_id = '${supplierTypes}' """
		}

		sql+= """ group by supplier_id,supplier,supplier_type_id,supplier_type order by supplier;"""

		List<ApAgingSummaryDto> items = jdbcTemplate.query(sql,
				new BeanPropertyRowMapper(ApAgingSummaryDto.class),
				filter
		)

		StringBuffer buffer = new StringBuffer()

		//DateTimeFormatter formatter =
		//		DateTimeFormatter.ofPattern("YYYY").withZone(ZoneId.systemDefault())
		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV.withHeader(
				"SUPPLIER",
				"SUPPLIER TYPE",
				"CURRENT",
				"1-30 DAYS",
				"31-60 DAYS",
				"61-90 DAYS",
				"91-120 DAYS",
				"OLDER",
				"TOTAL"))

		try {
			items.each {
				item ->
					csvPrinter.printRecord(
							item.supplier,
							item.supplier_type,
							item.current_amount,
							item.day_1_to_31,
							item.day_31_to_60,
							item.day_61_to_90,
							item.day_91_to_120,
							item.older,
							item.total
					)
			}

			LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
			extHeaders.add("Content-Disposition",
					"attachment;filename=AgingReportSummary_${filter}.csv".toString())

			return new ResponseEntity(buffer.toString().getBytes(), extHeaders, HttpStatus.OK)
		}
		catch (e) {
			throw e
		}

	}

	@RequestMapping(method = RequestMethod.GET, value = ["/agingDetailed/csv"])
	ResponseEntity<AnyDocument.Any> agingDetailedDownload(
			@RequestParam String filter,
			@RequestParam String supplier,
			@RequestParam String supplierTypes,
			@RequestParam Boolean posted
	) {

		String sql = """select * from accounting.aging_report(?::date) where supplier like '%%' """

		if(posted != null){
			sql+= """ and (posted = ${posted} or posted is null) """
		}

		if(supplierTypes != null && !supplierTypes.equalsIgnoreCase('null')){
			sql+= """ and supplier_type_id = '${supplierTypes}' """
		}

		if(supplier != null && !supplier.equalsIgnoreCase('null')){
			sql+= """ and supplier_id = '${supplier}' """
		}

		sql+= """ order by supplier;"""

		List<ApAgingDetailedDto> items = jdbcTemplate.query(sql,
				new BeanPropertyRowMapper(ApAgingDetailedDto.class),
				filter
		)

		StringBuffer buffer = new StringBuffer()

		//DateTimeFormatter formatter =
		//		DateTimeFormatter.ofPattern("YYYY").withZone(ZoneId.systemDefault())
		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV.withHeader(
				"INVOICE DATE",
				"SOURCE",
				"REFERENCE #",
				"SUPPLIER",
				"DUE DATE",
				"CURRENT",
				"1-30 DAYS",
				"31-60 DAYS",
				"61-90 DAYS",
				"91-120 DAYS",
				"OLDER",
				"TOTAL"))

		try {
			items.each {
				item ->
					csvPrinter.printRecord(
							item?.invoice_date ? item.invoice_date : item.apv_date,
							item.ap_no,
							item?.invoice_no,
							item.supplier,
							item.due_date,
							item.current_amount,
							item.day_1_to_31,
							item.day_31_to_60,
							item.day_61_to_90,
							item.day_91_to_120,
							item.older,
							item.total
					)
			}

			LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
			extHeaders.add("Content-Disposition",
					"attachment;filename=AgingReportDetailed_${filter}.csv".toString())

			return new ResponseEntity(buffer.toString().getBytes(), extHeaders, HttpStatus.OK)
		}
		catch (e) {
			throw e
		}

	}
	

}
