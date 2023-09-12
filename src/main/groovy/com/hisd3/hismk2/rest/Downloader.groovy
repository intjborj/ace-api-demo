package com.hisd3.hismk2.rest

import com.hisd3.hismk2.domain.accounting.Ledger
import com.hisd3.hismk2.graphqlservices.accounting.GeneraLedgerServices
import com.hisd3.hismk2.graphqlservices.accounting.SubAccountSetupService
import com.hisd3.hismk2.graphqlservices.cashiering.PaymentTrackerServices
import com.hisd3.hismk2.services.AES
import com.hisd3.hismk2.services.ReportTabularGeneratorService
import groovy.json.JsonSlurper
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.lang3.ArrayUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import java.nio.charset.Charset
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.concurrent.Callable

@RestController
@RequestMapping("/api")
class Downloader {
	
	@PersistenceContext
	EntityManager entityManager


	@Autowired
	GeneraLedgerServices generaLedgerServices

	@Autowired
	PaymentTrackerServices paymentTrackerServices

	@Autowired
	SubAccountSetupService subAccountSetupService




	@RequestMapping(value= "/getAllChartOfAccountGenerateDownload", method = [RequestMethod.POST] )
	ResponseEntity<byte []> getAllChartOfAccountGenerateDownload(
			@RequestParam  String accountType,
			@RequestParam  String motherAccountCode,
			@RequestParam  String description,
			@RequestParam  String subaccountType,
			@RequestParam  String department,
			@RequestParam(required = false) Boolean excludeMotherAccount
	) {

	  def records = subAccountSetupService.getAllChartOfAccountGenerate(
				accountType,
				motherAccountCode,
				description,
				subaccountType,
				department,
				excludeMotherAccount
		)


		StringBuffer buffer = new StringBuffer()

		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV
				.withHeader("Code","Description"))

		records.each {
			csvPrinter.printRecord(
					it.code,
					it.description
			)
		}

		def data = buffer.toString().getBytes(Charset.defaultCharset())
		def responseHeaders = new HttpHeaders()
		responseHeaders.setContentType(MediaType.TEXT_PLAIN)
		responseHeaders.setContentLength(data.length)
		responseHeaders.add("Content-Disposition", "attachment;filename=coa.csv")

		return new ResponseEntity(data, responseHeaders, HttpStatus.OK)

	}

	@RequestMapping("/downloadCardAndCheck")
	ResponseEntity<byte []> downloadCardAndCheck(
			@RequestParam  String  type,
			@RequestParam  Instant  startDateTime,
			@RequestParam  Instant  endDateTime,
			@RequestParam  String  filter,
			@RequestParam  Boolean  showAll
	){

		StringBuffer buffer = new StringBuffer()

		def records = paymentTrackerServices.getCreditCardsAndCheckDownload(
				type,
				startDateTime,
				endDateTime,
				filter,showAll
		)

		List<String> headers = []

			if(type=="CHECK"){

				headers = ["Date",
						   "Check No",
						   "Amount",
						   "Bank of Check",
						   "Shift No",
						   "CDCTR",
						   "Deposit Id",
						   "Status"]

			}	else {
				headers = ["Date",
						   "Approval Code",
						   "Card No",
						   "Amount",
						   "Bank of Card",
						   "Aquiring Bank",
						   "Shift No",
						   "CDCTR",
						   "Deposit Id",
						   "Status"]
			}




		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV
			    .withHeader(*headers))



		records.each {ptracker ->
			if(type=="CHECK"){
				csvPrinter.printRecord(
						LocalDateTime.ofInstant( ptracker.createdDate, ZoneOffset.systemDefault()).format(
								DateTimeFormatter.ofPattern("MM/dd/yyyy")
						),
						"REF: " + ptracker.reference,
						(ptracker.amount?:0.0).toPlainString(),
						ptracker.bank,
						ptracker.paymentTracker.shift.shiftno,
						ptracker.paymentTracker?.shift?.cdctr?.recno?:"",
						ptracker.paymentTracker?.shift?.cdctr?.collection?.collectionId?:"",
						ptracker.paymentTracker?.shift?.cdctr?.collection?.ledgerHeader != null ? "Deposited":""
				)
			}
			else {
				csvPrinter.printRecord(
						LocalDateTime.ofInstant( ptracker.createdDate, ZoneOffset.systemDefault()).format(
								DateTimeFormatter.ofPattern("MM/dd/yyyy")
						),
						ptracker.approvalCode,
						"REF: " + ptracker.reference,
						(ptracker.amount?:0.0).toPlainString(),
						ptracker.bank,
						ptracker.bankEntity?.bankname,
						ptracker.paymentTracker.shift.shiftno,
						ptracker.paymentTracker?.shift?.cdctr?.recno?:"",
						ptracker.paymentTracker?.shift?.cdctr?.collection?.collectionId?:"",
						ptracker.paymentTracker?.shift?.cdctr?.collection?.ledgerHeader != null ? "Deposited":""
				)

			}
		}

		def data = buffer.toString().getBytes(Charset.defaultCharset())
		def responseHeaders = new HttpHeaders()
		responseHeaders.setContentType(MediaType.TEXT_PLAIN)
		responseHeaders.setContentLength(data.length)
		responseHeaders.add("Content-Disposition", "attachment;filename=${type}-${LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)}.csv")

		return new ResponseEntity(data, responseHeaders, HttpStatus.OK)
	}



	@RequestMapping("/ledgerAllDownload")
	Callable<ResponseEntity<byte []>> ledgerAllDownload(
			@RequestParam(name ="journalType") String journalType,
			@RequestParam(name = "fromDate")  Instant fromDate,
			@RequestParam(name = "toDate")  Instant  toDate,
			@RequestParam(name = "posted", required = false)  Boolean  posted
	){


		return new Callable<ResponseEntity<byte[]>>() {
			@Override
			ResponseEntity<byte[]> call() throws Exception {
				StringBuffer buffer = new StringBuffer()

				def records = generaLedgerServices.ledgerAllDownload(journalType,fromDate,toDate,posted)
				CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV
						.withHeader("Account Code",
								"Account Title",
								"Journal",
								"JV",
								"Date/Time",
								"Reference",
								"Entity",
								"Particulars",
								"Created By",
								"Approved By",
								"Approved Date/Time",
								"Debit",
								"Credit"))

				records.each {

					csvPrinter.printRecord(
							it.journalAccount.code?:"",
							it.journalAccount.description?:"",
							it.header.journalType.name(),
							"${it.header.docType} - ${it.header.docnum}",
							LocalDateTime.ofInstant( it.header.transactionDate, ZoneOffset.systemDefault()).format(
									DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")
							),
							it.header.invoiceSoaReference?:"",
							it.header.entityName?:"",
							it.header.particulars?:"",
							it.header.createdBy,
							it.header.approvedBy?:"",
							(it.header.approvedDatetime ) ? LocalDateTime.ofInstant( it.header.approvedDatetime, ZoneOffset.systemDefault()).format(
									DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")
							): "",
							(it.debit?:0.0).toPlainString(),
							(it.credit?:0.0).toPlainString()

					)
				}

				def data = buffer.toString().getBytes(Charset.defaultCharset())
				def responseHeaders = new HttpHeaders()
				responseHeaders.setContentType(MediaType.TEXT_PLAIN)
				responseHeaders.setContentLength(data.length)
				responseHeaders.add("Content-Disposition", "attachment;filename=transactions-${LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)}.csv")


				return new ResponseEntity(data, responseHeaders, HttpStatus.OK)
			}
		}




	}

	@RequestMapping("/ledgerViewListDownload")
	ResponseEntity<byte []>  ledgerViewListDownload(
			@RequestParam(name ="fiscalId") UUID fiscalId,
			@RequestParam(name = "code")  String code,
			@RequestParam(name = "monthNo")  Integer  monthNo,
			@RequestParam(name = "filter")  String  filter
	){


		StringBuffer buffer = new StringBuffer()
		def records = generaLedgerServices.ledgerViewListForDownload(fiscalId,code,monthNo,filter)

		CSVPrinter csvPrinter = new CSVPrinter(buffer, CSVFormat.POSTGRESQL_CSV
				.withHeader("Account Code",
				"Account Title",
				"Journal",
				"JV",
				"Date/Time",
				"Reference",
				"Entity",
				"Particulars",
				"Created By",
				"Approved By",
				"Approved Date/Time",
				"Debit",
				"Credit"))


		records.each {

			csvPrinter.printRecord(
					it.journalAccount.code?:"",
					it.journalAccount.description?:"",
					it.header.journalType.name(),
					"${it.header.docType} - ${it.header.docnum}",
					LocalDateTime.ofInstant( it.header.transactionDate, ZoneOffset.systemDefault()).format(
							DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")
					),
					it.header.invoiceSoaReference?:"",
					it.header.entityName?:"",
					it.header.particulars?:"",
					it.header.createdBy,
					it.header.approvedBy?:"",
					LocalDateTime.ofInstant( it.header.approvedDatetime, ZoneOffset.systemDefault()).format(
							DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")
					),
					(it.debit?:0.0).toPlainString(),
					(it.credit?:0.0).toPlainString()

			)
		}

		def data = buffer.toString().getBytes(Charset.defaultCharset())
		def responseHeaders = new HttpHeaders()
		responseHeaders.setContentType(MediaType.TEXT_PLAIN)
		responseHeaders.setContentLength(data.length)
	  	responseHeaders.add("Content-Disposition", "attachment;filename=${code}-${Month.of(monthNo).name()}.csv")


		return new ResponseEntity(data, responseHeaders, HttpStatus.OK)

	}


	@RequestMapping("/download")
	ResponseEntity<byte[]> download(
			@RequestParam String d,
			@RequestParam(required = false) Boolean inline
	) {
		
		// JsonOutput.toJson([class: Specialty.name, id: id.toString(),columnName: "attachment","filename":filename])
		
		def detail = AES.decrypt(d.trim())
		
		def jsonSlurper = new JsonSlurper()
		def objDetail = jsonSlurper.parseText(detail)
		
		def className = objDetail["class"]
		def id = objDetail["id"]
		def columnName = objDetail["columnName"]
		def filename = objDetail["filename"] as String
		
		def binaryEntity = entityManager.find(Class.forName(className), UUID.fromString(id))
		
		def binaryColumn = binaryEntity[columnName] as Byte[]
		
		if (!binaryColumn && binaryColumn.length == 0)
			return ResponseEntity.badRequest().build()
		
		def mimeType = URLConnection.guessContentTypeFromName(filename)
		
		def responseHeaders = new HttpHeaders()
		responseHeaders.setContentType(MediaType.valueOf(mimeType))
		responseHeaders.setContentLength(binaryColumn.length)
		
		if (inline)
			responseHeaders.add("Content-Disposition", "inline;filename=${filename}")
		else
			responseHeaders.add("Content-Disposition", "attachment;filename=${filename}")
		
		new ResponseEntity<byte[]>(ArrayUtils.toPrimitive(binaryColumn), responseHeaders, HttpStatus.OK)
	}
	
}
