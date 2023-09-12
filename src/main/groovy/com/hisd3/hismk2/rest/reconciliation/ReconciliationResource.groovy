package com.hisd3.hismk2.rest.reconciliation

import com.google.gson.Gson
import com.hisd3.hismk2.domain.accounting.ArCreditNote
import com.hisd3.hismk2.domain.accounting.ArCreditNoteItems
import com.hisd3.hismk2.domain.accounting.ArInvoice
import com.hisd3.hismk2.domain.accounting.HeaderLedger
import com.hisd3.hismk2.domain.cashiering.PaymentTrackerDetails
import com.hisd3.hismk2.graphqlservices.accounting.*
import com.hisd3.hismk2.graphqlservices.billing.CompanyAccountServices
import com.hisd3.hismk2.graphqlservices.cashiering.PaymentTrackerDetailsServices
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.rest.dto.*
import com.hisd3.hismk2.security.SecurityUtils
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
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/reconciliation-reports")
class ReconciliationResource {

	@Autowired
	PaymentTrackerDetailsServices paymentTrackerDetailsServices

	@Autowired
	EmployeeRepository employeeRepository


	@RequestMapping(method = RequestMethod.GET, value = ["/check-monitoring"])
	ResponseEntity<AnyDocument.Any> getReconciliationCheckMonitoringCSV(
			@RequestParam String filter,
			@RequestParam String depositoryBank,
			@RequestParam String collectionDate,
			@RequestParam String clearingDate,
			@RequestParam String status
	)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT
		def collectionDateInst = null
		def clearingDateInst = null

		if(collectionDate){
			collectionDateInst = Instant.from(formatter.parse(collectionDate))
		}

		if(clearingDate){
	 		clearingDateInst = Instant.from(formatter.parse(clearingDate))
		}

		UUID depositoryBankUUID = null
		if(depositoryBank)
			depositoryBankUUID = UUID.fromString(depositoryBank)

		List<PaymentTrackerDetails> checks = paymentTrackerDetailsServices.getCashieringCollectionByTypeOnList(filter,depositoryBankUUID,collectionDateInst,clearingDateInst,'CHECK',status)

		StringBuffer buffer = new StringBuffer()

		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV.withHeader(
				'Shift Number',
				'Terminal',
				'Collection Date',
				'Doc Type',
				'Doc No.',
				'Ref. Doc No.',
				'Payee',
				'Mode of Payment',
				'User ID',
				'Payment Description',
				'Amount',
				'Check Number',
				'Check Date',
				'Bank of Check',
				'Depository Bank',
				'CDCTR',
				'Deposit ID',
				'Deposit Date',
				'Clearing Date',
				'Status'
		))

		Map<String,String> userID = [:]

		try {
			checks.each {
				item ->
					String userStr = ""
					if(!userID[item.createdBy]) {
						def user = employeeRepository.findByUsername(item.createdBy).first()
						if(user) {
							userStr = user.employeeId
							userID[item.createdBy] = userStr
						}
					}else {
						userStr = userID[item.createdBy]
					}

					csvPrinter.printRecord(
							item?.paymentTracker?.shift?.shiftno ?: '',
							item?.paymentTracker?.cashierTerminal?.terminalId ?: '',
							item.createdDate.atOffset(ZoneOffset.UTC).plusHours(8).toLocalDate() ?: '',
							item?.paymentTracker?.receiptType ?: '',
							item?.paymentTracker?.ornumber ?: '',
							item?.paymentTracker?.reference ?: '',
							item?.paymentTracker?.payorName ?: '',
							item.type,
							userStr,
							item?.paymentTracker?.transactionCategory ?: '',
							item.amount,
							item?.reference ?: '',
							item?.checkdate ?: '',
							item?.bank ?: '',
							item?.collectionDetail?.bank?.bankname ?: '',
							item?.paymentTracker?.shift?.cdctr?.recno ?: '',
							item?.collectionDetail?.collection?.collectionId ?: '',
							item?.depositDate ?: '',
							item?.cleareddate ?: '',
							item?.status ?: ''
					)
			}

			LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
			extHeaders.add("Content-Disposition",
					"attachment;filename=check-reconciliation.csv".toString())
			return new ResponseEntity(buffer.toString().getBytes(), extHeaders, HttpStatus.OK)
		}
		catch (e) {
			throw e
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = ["/card-monitoring"])
	ResponseEntity<AnyDocument.Any> getReconciliationCardMonitoringCSV(
			@RequestParam String filter,
			@RequestParam String depositoryBank,
			@RequestParam String collectionDate,
			@RequestParam String clearingDate,
			@RequestParam String status
	)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT
		def collectionDateInst = null
		def clearingDateInst = null

		if(collectionDate){
			collectionDateInst = Instant.from(formatter.parse(collectionDate))
		}

		if(clearingDate){
			clearingDateInst = Instant.from(formatter.parse(clearingDate))
		}

		UUID depositoryBankUUID = null
		if(depositoryBank)
			depositoryBankUUID = UUID.fromString(depositoryBank)

		List<PaymentTrackerDetails> checks = paymentTrackerDetailsServices.getCashieringCollectionByTypeOnList(filter,depositoryBankUUID,collectionDateInst,clearingDateInst,'CARD',status)

		StringBuffer buffer = new StringBuffer()

		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV.withHeader(
				'Shift Number',
				'Terminal',
				'Collection Date',
				'Doc Type',
				'Doc No.',
				'Ref. Doc No.',
				'Payee',
				'Mode of Payment',
				'User ID',
				'Payment Description',
				'Amount',
				'Card No',
				'Expiry Date',
				'Bank of Card',
				'Name of Card',
				'Card Type',
				'Authorization Code',
				'POS Terminal ID',
				'Merchant Name/Acquiring Bank',
				'Settlement Date',
				'Status'
		))

		Map<String,String> userID = [:]

		try {
			checks.each {
				item ->
					String userStr = ""
					if(!userID[item.createdBy]) {
						def user = employeeRepository.findByUsername(item.createdBy).first()
						if(user) {
							userStr = user.employeeId
							userID[item.createdBy] = userStr
						}
					}else {
						userStr = userID[item.createdBy]
					}

					csvPrinter.printRecord(
							item?.paymentTracker?.shift?.shiftno ?: '',
							item?.paymentTracker?.cashierTerminal?.terminalId ?: '',
							item.createdDate.atOffset(ZoneOffset.UTC).plusHours(8).toLocalDate() ?: '',
							item?.paymentTracker?.receiptType ?: '',
							item?.paymentTracker?.ornumber ?: '',
							item?.paymentTracker?.reference ?: '',
							item?.paymentTracker?.payorName ?: '',
							item.type,
							userStr,
							item?.paymentTracker?.transactionCategory ?: '',
							item.amount,
							item?.reference ?: '',
							item?.checkdate ?: '',
							item?.bank ?: '',
							item?.nameOfCard ?: '',
							item?.approvalCode ?: '',
							item?.posTerminalId ?: '',
							item?.bankEntity?.bankname ?: '',
							item?.cleareddate ?: '',
							item?.status ?: ''
					)
			}

			LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
			extHeaders.add("Content-Disposition",
					"attachment;filename=card-reconciliation.csv".toString())
			return new ResponseEntity(buffer.toString().getBytes(), extHeaders, HttpStatus.OK)
		}
		catch (e) {
			throw e
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = ["/bank-deposit-monitoring"])
	ResponseEntity<AnyDocument.Any> getReconciliationBankDepositMonitoringCSV(
			@RequestParam String filter,
			@RequestParam String depositoryBank,
			@RequestParam String collectionDate,
			@RequestParam String clearingDate,
			@RequestParam String status
	)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT
		def collectionDateInst = null
		def clearingDateInst = null

		if(collectionDate){
			collectionDateInst = Instant.from(formatter.parse(collectionDate))
		}

		if(clearingDate){
			clearingDateInst = Instant.from(formatter.parse(clearingDate))
		}

		UUID depositoryBankUUID = null
		if(depositoryBank)
			depositoryBankUUID = UUID.fromString(depositoryBank)

		List<PaymentTrackerDetails> checks = paymentTrackerDetailsServices.getCashieringCollectionByTypeOnList(filter,depositoryBankUUID,collectionDateInst,clearingDateInst,'BANKDEPOSIT',status)

		StringBuffer buffer = new StringBuffer()

		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV.withHeader(
				"Shift Number",
				"Terminal",
				"Collection Date",
				"Doc Type",
				"Doc No.",
				"Ref. Doc No.",
				"Payee",
				"Mode of Payment",
				"User ID",
				"Payment Description",
				"Amount",
				"Transfer Ref. No.",
				"Depository Bank",
				"Clearing Date",
				"Status"
		))

		Map<String,String> userID = [:]

		try {
			checks.each {
				item ->
					String userStr = ""
					if(!userID[item.createdBy]) {
						def user = employeeRepository.findByUsername(item.createdBy).first()
						if(user) {
							userStr = user.employeeId
							userID[item.createdBy] = userStr
						}
					}else {
						userStr = userID[item.createdBy]
					}

					csvPrinter.printRecord(
							item?.paymentTracker?.shift?.shiftno ?: '',
							item?.paymentTracker?.cashierTerminal?.terminalId ?: '',
							item.createdDate.atOffset(ZoneOffset.UTC).plusHours(8).toLocalDate() ?: '',
							item?.paymentTracker?.receiptType ?: '',
							item?.paymentTracker?.ornumber ?: '',
							item?.paymentTracker?.reference ?: '',
							item?.paymentTracker?.payorName ?: '',
							item.type,
							userStr,
							item?.paymentTracker?.transactionCategory ?: '',
							item.amount,
							item?.reference ?: '',
							item?.bankEntity?.bankname ?: '',
							item?.cleareddate ?: '',
							item?.status ?: ''
					)
			}

			LinkedMultiValueMap<String, String> extHeaders = new LinkedMultiValueMap<>()
			extHeaders.add("Content-Disposition",
					"attachment;filename=bank-deposit-reconciliation.csv".toString())
			return new ResponseEntity(buffer.toString().getBytes(), extHeaders, HttpStatus.OK)
		}
		catch (e) {
			throw e
		}
	}
}
