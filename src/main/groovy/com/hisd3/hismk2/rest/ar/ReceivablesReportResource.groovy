package com.hisd3.hismk2.rest.ar

import com.google.gson.Gson
import com.hisd3.hismk2.domain.accounting.AccReceivableGroupParam
import com.hisd3.hismk2.domain.accounting.ArCreditNote
import com.hisd3.hismk2.domain.accounting.ArCreditNoteItems
import com.hisd3.hismk2.domain.accounting.ArInvoice
import com.hisd3.hismk2.domain.accounting.BsPhilClaims
import com.hisd3.hismk2.domain.accounting.HeaderLedger
import com.hisd3.hismk2.domain.annotations.UpperCase
import com.hisd3.hismk2.domain.billing.BillingItemType
import com.hisd3.hismk2.domain.billing.CompanyAccount
import com.hisd3.hismk2.graphqlservices.accounting.*
import com.hisd3.hismk2.graphqlservices.billing.CompanyAccountServices
import com.hisd3.hismk2.graphqlservices.billing.dto.InvestorPaymentLedgerRunningBalanceDto
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.repository.accounting.AccountReceivableCompanyRepository
import com.hisd3.hismk2.repository.accounting.AccountReceivableRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.inventory.SupplierRepository
import com.hisd3.hismk2.rest.dto.ARCreditNoteFieldsDto
import com.hisd3.hismk2.rest.dto.ARInvoiceBankDto
import com.hisd3.hismk2.rest.dto.ARInvoiceDto
import com.hisd3.hismk2.rest.dto.ARInvoiceFieldsDto
import com.hisd3.hismk2.rest.dto.ArCreditNoteItemsDTO
import com.hisd3.hismk2.rest.dto.ArCreditNoteJournalEntryDTO
import com.hisd3.hismk2.rest.dto.BillingScheduleDto
import com.hisd3.hismk2.rest.dto.BillingScheduleFieldsDto
import com.hisd3.hismk2.rest.dto.BillingScheduleSignDto
import com.hisd3.hismk2.rest.dto.ManualBillingDto
import com.hisd3.hismk2.security.SecurityUtils
import groovy.json.JsonSlurper
import groovy.transform.Canonical
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
import java.math.RoundingMode
import java.sql.ResultSet
import java.sql.SQLException
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit


@RestController
@RequestMapping("/arreports")
class ReceivableReportResource {
	
	@Autowired
	CompanyAccountServices companyAccountServices

	@Autowired
	EmployeeRepository employeeRepository

	@Autowired
	ApplicationContext applicationContext

	@Autowired
	HospitalConfigService hospitalConfigService

	@Autowired
	JdbcTemplate jdbcTemplate

	@Autowired
	EntityManager entityManager

	@Autowired
	ArInvoiceItemServices arInvoiceItemServices

	@Autowired
	ArInvoiceServices arInvoiceServices

	@Autowired
	ArCreditNoteService arCreditNoteService

	@Autowired
	ArCreditNoteItemServices arCreditNoteItemServices

	@Autowired
	LedgerServices ledgerServices

	@RequestMapping(value = "/arinvoice", produces = ["application/pdf"])
	ResponseEntity<byte[]> arinvoice(
			@RequestParam UUID id
	) {
		ArInvoice invoice = arInvoiceServices.findOne(id)

		def res = applicationContext.getResource("classpath:/reports/ar/arinvoice.jasper")
		def os = new ByteArrayOutputStream()
		def parameters = [:] as Map<String, Object>

		def logo = applicationContext?.getResource("classpath:/reports/logo.png")
		def maker = employeeRepository.findByUsername(invoice.createdBy).first()

		SimpleDateFormat invoiceDateFormat = new SimpleDateFormat("MM/dd/yyyy")
		SimpleDateFormat dueDateFormat = new SimpleDateFormat("dd MMM yyyy")
		SimpleDateFormat labelDateFormat2 = new SimpleDateFormat("MMMM, yyyy")


		if (logo.exists()) {
			parameters.put("logo", logo.inputStream)
		}

		List<ARInvoiceBankDto> bankList = []
		bankList.push(new ARInvoiceBankDto(
				"Landbank of the Philippines (LBP)",
				"Tagbilaran Branch",
				"06-1212-0293"
		))
		bankList.push(new ARInvoiceBankDto(
				"Bank of the Philippine Islands (BPI)",
				"Tagbilaran Main",
				"001203-3180-45"
		))
		bankList.push(new ARInvoiceBankDto(
				"BDO",
				"Tagbilaran Branch",
				"002958014077"
		))

		List<ARInvoiceDto> dtoList = arInvoiceItemServices.getARInvoiceItemPerPatientGroupById(id)

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy")
		LocalDateTime now = LocalDateTime.now()

		if (dtoList) {
			parameters.put('invoice_items', new JRBeanCollectionDataSource(dtoList))
		}
		parameters.put('bank_accounts', new JRBeanCollectionDataSource(bankList))

		def dto = new ARInvoiceFieldsDto(
				customer_account_number:"Customer Account Number: ${invoice?.arCustomer?.accountNo?:''}",
				customer_name:"Customer Account Name: ${invoice.arCustomer.customerName}",
				customer_address:"Customer Address: ${invoice.arCustomer.address}",
				invoice_date: "Invoice Date: ${invoiceDateFormat.format(invoice.invoiceDate)}",
				invoice_intro: "Please find below the list of the patients who availed our Hospital Services :",
				invoice_number:"Invoice Number: ${invoice.invoiceNo}",
				due_date:"Due Date:${dueDateFormat.format(invoice.dueDate)}",
				prepared_by:"${StringUtils.upperCase(maker.fullName)} ${dtf.format(now)}",
				noted_by:"JOY CRISTINE Q. ESCABUSA ${dtf.format(now)}"
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
		params.add("Content-Disposition", "inline;filename=invoice-" + invoice.invoiceNo + ".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)

	}

	@RequestMapping(value = "/arcreditnote", produces = ["application/pdf"])
	ResponseEntity<byte[]> arcreditnote(
			@RequestParam UUID id
	) {
		ArCreditNote creditNote = arCreditNoteService.findOne(id)


		def res = applicationContext.getResource("classpath:/reports/ar/arcreditnote.jasper")
		def os = new ByteArrayOutputStream()
		def parameters = [:] as Map<String, Object>

		def logo = applicationContext?.getResource("classpath:/reports/logo.png")
		def maker = employeeRepository.findByUsername(creditNote.createdBy).first()

		SimpleDateFormat invoiceDateFormat = new SimpleDateFormat("MM/dd/yyyy")
		def formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

		if (logo.exists()) {
			parameters.put("logo", logo.inputStream)
		}

		List<ArCreditNoteJournalEntryDTO> journalEntryDTOS = []
		HeaderLedger headerLedger = ledgerServices.findOne(creditNote.ledgerId)
		headerLedger.ledger.each {
			it ->
				journalEntryDTOS.push(
					new ArCreditNoteJournalEntryDTO(
						headerLedger.transactionDate.atZone(ZoneId.systemDefault()).format(formatter),
						creditNote.creditNoteNo,
						it.journalAccount.code,
						it.journalAccount.description,
						it.debit,
						it.credit
					)
				)
		}

		List<ArCreditNoteItemsDTO> dtoList = []
		List<ArCreditNoteItems> items = arCreditNoteItemServices.findCreditNoteItemsByCNId(id)
		items.each {
			cn ->
				dtoList.push(
						new ArCreditNoteItemsDTO(
								cn.reference,
								cn.description,
								cn.itemName,
								cn.arInvoiceItem.invoiceNo,
								cn.totalAmountDue
						)
				)
		}


		if (dtoList) {
			parameters.put('credit_note_items', new JRBeanCollectionDataSource(dtoList))
		}
		def a = journalEntryDTOS.sort{dtoSort -> dtoSort.debit}.reverse(true)
		parameters.put('journal_entry', new JRBeanCollectionDataSource(a))

		def dto = new ARCreditNoteFieldsDto(
				customer_account_number:"Customer Account Number: ${creditNote?.arCustomer?.accountNo?:''}",
				customer_name:"Customer Account Name: ${creditNote.arCustomer.customerName}",
				customer_address:"Customer Address: ${creditNote.arCustomer.address}",
				cn_date: "Credit Note Date: ${invoiceDateFormat.format(creditNote.creditNoteDate)}",
				cn_number:"Credit Note Number: ${creditNote.creditNoteNo}",
				prepared_by:"${StringUtils.upperCase(maker.fullName)}",
				noted_by:"MA. ELISA S. CASTRODES",
				audited_by: "JUVY O. CAGA"
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
		params.add("Content-Disposition", "inline;filename=credit-note-" + creditNote.creditNoteNo + ".pdf")
		return new ResponseEntity(data, params, HttpStatus.OK)

	}
}
