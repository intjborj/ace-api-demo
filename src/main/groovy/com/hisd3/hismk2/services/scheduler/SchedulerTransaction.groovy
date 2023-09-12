package com.hisd3.hismk2.services.scheduler

import com.hisd3.hismk2.domain.accounting.HeaderLedger
import com.hisd3.hismk2.domain.billing.Billing
import com.hisd3.hismk2.domain.billing.BillingItem
import com.hisd3.hismk2.domain.billing.BillingItemDetailParam
import com.hisd3.hismk2.domain.billing.BillingItemStatus
import com.hisd3.hismk2.domain.billing.BillingItemType
import com.hisd3.hismk2.domain.cashiering.PaymentTracker
import com.hisd3.hismk2.domain.hospital_config.OperationalConfiguration
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.graphqlservices.accounting.LedgerServices
import com.hisd3.hismk2.graphqlservices.billing.BillingItemServices
import com.hisd3.hismk2.graphqlservices.billing.BillingService
import com.hisd3.hismk2.graphqlservices.cashiering.PaymentTrackerServices
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.repository.hospital_config.OperationalConfigurationRepository
import com.hisd3.hismk2.repository.pms.CaseRepository
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.BooleanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Service
@Slf4j
class SchedulerTransaction {

	@Autowired
	CaseRepository caseRepository

	@Autowired
	BillingItemServices billingItemServices

	@Autowired
	BillingService billingService

	@Autowired
	JdbcTemplate jdbcTemplate

	@Autowired
	HospitalConfigService hospitalConfigService

	@PersistenceContext
	EntityManager entityManager

	@Autowired
	PaymentTrackerServices paymentTrackerServices

	@Autowired
	LedgerServices ledgerServices

	@Autowired
	OperationalConfigurationRepository operationalConfigurationRepository

	@Transactional
	def autochargeRooms() {

		def activeCases = caseRepository.getActiveCasesForRoomCharge()

		//def roomInMultiplier = hospitalConfigService.operationalConfig?.roomInDeduction?:0.0




		def formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
		activeCases.each {
			patientCase ->

				def room = patientCase.room

				if (room) {

					//def deductRoomIn = 0.0
					//def roomInStr = ""

					//if(patientCase.roomIn){
					//	deductRoomIn = (room.price * roomInMultiplier).setScale(2, RoundingMode.HALF_EVEN)
					//	roomInStr = "RM-IN"
					//}
					def activeBilling = billingService.activeBilling(patientCase)

					if (activeBilling && BooleanUtils.isNotTrue(activeBilling.locked)) {
						def result = billingItemServices.addBillingItem(activeBilling.id,
								"ROOMBOARD",
								[
										[
												"roomDeptId"     : room?.department?.id?.toString()?:"",
												"roomno"     : room.roomNo,
												"bedno"      : room.bedNo,
												"price"      : room.price,
												"quantity"   : 1,
												"description": "${LocalDate.now().format(formatter)}"

										]
								]
						)

						if (result.success) {
							log.info("Add Room: ${patientCase.patient.fullName}-${patientCase.caseNo} ")
						} else {
							log.warn("Add Room: ${patientCase.patient.fullName}-${patientCase.caseNo} error ${result.message}")
						}

					}
				}
		}
	}


	@Transactional
	def cleanNotifications() {
		jdbcTemplate.execute("delete  from notifications   where   DATE_PART('day',now() -  date_notified  ) > 7;")
		jdbcTemplate.execute("VACUUM  (VERBOSE, ANALYZE) notifications;")
	}

	@Transactional
	def autoCloseOPD() {

		List<OperationalConfiguration> opConfig = operationalConfigurationRepository.findAll()

				List<Case> opdCases = caseRepository.getAllActiveCasesByRegistry()

				opdCases.each {
					it ->
						def billing = billingService.activeBilling(it)

						if (billing) {
							// has active Billing ...
							if (billing.balance < 1) {
								billing.status = "INACTIVE"
								if (opConfig.size() > 0 && opConfig[0].autolockOpd){
										it.locked = true
									}
								billingService.save(billing)

								it.status = 'CLOSED'
								caseRepository.save(it)

								log.info("Folio ${billing.billingNo} and Case no ${it.caseNo} closed")

							}
						} else {
							// has no active Billing... meaning Folio is closed but Case is still open
							it.status = 'CLOSED'
							if (opConfig.size() > 0 && opConfig[0].autolockOpd){
								it.locked = true
							}
							caseRepository.save(it)

							log.info("no Folio and Case no ${it.caseNo} closed")
						}

				}

	}


	@Transactional
	void recompClosedFolio() {

		int counter = 0
		def allBilling = jdbcTemplate.queryForList("select id,billing_no from billing.billing where status ='INACTIVE' and locked = true ")

		log.info("recompClosedFolio")
		allBilling.each {
			log.info("Folio  =  ${it.getOrDefault("billing_no","")}")
			billingService.recompPayments( it.get("id") as UUID)
			counter++
		}
		log.info("recompClosedFolio ${counter}")
	}


	// curl --user "scheduler:password"   -X POST http://localhost:8080/api/correctAutoEntries
	@Transactional(rollbackOn = Exception.class)
	void correctAutoEntries(){

		/*
		   1.) Identify Folios from Jan. 1, 2021 ( i think priority ang Payments)
		   2.) Correct Payments Auto Entries
		          - All Billing Payments are Canceled and Recreated
		          - Will be created from payment trackers
		          - Pls check for ledgerHeader

		   3.) Correct Items with Advance Payments... advance payment will be corrected to be put into folios...

          Find or for  journal_account ->> 'code' = '200180-1010-1020'



			select * from cashiering.payment_tracker pt where pt.ledger_header in
			(Select header.id  from accounting.ledger ld left join accounting.header_ledger header on header.id=ld."header"
			where
			 ld.journal_account ->> 'code' = '200180-1010-1020')

			 // As Per Checking there is no advance deposits for Month of January
			// himoon lng na kung if naa detect


           update t_user set password= '$2a$10$6EPQ5.IH1mfOMuHLgHY9WuhVjB21BElsQ48TzPa99vvl1PdRWATD.'


		 */



  def _currentYearFolios  = entityManager.createQuery(""" from Billing b where b.entryDateTime >:entryDateTime  and
 b.patientCase.registryType='IPD'  order by b.billingNo
				""", Billing.class)
		.setParameter("entryDateTime",LocalDateTime.of(2021,1,1,1,0,0,0)
		.toInstant(ZoneOffset.UTC))
		.resultList

		//_currentYearFolios.each {
		//	println("${it.id} - ${it.billingNo} - ${it.patientCase.patient.fullName} - ${it.status}")
		//}
		println("Start Processing ${_currentYearFolios.size()}")
		_currentYearFolios.eachWithIndex { billing,index ->

			//def billing = billing // billingService.findOne(UUID.fromString("e2643156-892c-4ae6-ba5b-79f341ffc381"))
			println("${index+1}/${_currentYearFolios.size()}  ${billing.id} - ${billing.billingNo} - ${billing.patientCase.patient.fullName} - ${billing.status}")

			reverseWrongEntries(billing)

			processBacklogPayments(billing)
			processARClearing(billing)

			if(billing.status=="INACTIVE")
			{
				entityManager.flush()
				entityManager.refresh(billing)
				billingService.reapplyPaymentsV2(billing)
			}
		}
		println("End Processing")



		//def activeFolios = _currentYearFolios.findAll { it.status == "ACTIVE"}
		//def closedFolios = _currentYearFolios.findAll { it.status == "INACTIVE"}


	}


	void reverseWrongEntries(Billing billing){

		/*
		    Cancel all Items with    /
		    REAPPLICATION OF PAYMENTS
			PAYMENT DEBIT ADJUSTMENT

		 */

		/*
		   Cancel
		   Company Accounts Entries
		 */


		List<HeaderLedger> forRev = entityManager.createQuery("""Select hl from HeaderLedger hl where hl.details['BILLING_ID']=:billingId and hl.particulars in ('REAPPLICATION OF PAYMENTS','PAYMENT DEBIT ADJUSTMENT ')    """)
		.setParameter("billingId",billing.id.toString())
		.resultList


		forRev.each {
			//println("=" + it.particulars)
			ledgerServices.reverseEntriesCustom(it,it.transactionDate)
		}


	}
	// this will delete billing items payments and recreate as progress billing
	// Test case for inactive patients
	void processBacklogPayments(Billing billing){


		/*
		   1.) Cancel all Billing Items
		   2.) Get Payment Trackers
		   3.) Ledgers from payment trackers are cleared. Marked with Invalidated
		   4.)

		 */

	  def activePayments= billing.billingItemList.findAll {
			  it.status == BillingItemStatus.ACTIVE &&
				it.itemType == BillingItemType.PAYMENTS
		}

		activePayments.each {
			 it.status = BillingItemStatus.CANCELED
			billingItemServices.save(it)
		}


		List<PaymentTracker> payments = entityManager.createQuery("from PaymentTracker pt where pt.billingid=:billingid and coalesce(pt.voided,false) = false ")
		.setParameter("billingid",billing.id)
		.resultList



		payments.each {  PaymentTracker ptracker ->
			def headerLedger = entityManager.find(HeaderLedger.class,ptracker.ledgerHeader)

			/*headerLedger.ledger.clear()
			headerLedger.particulars = "[CANCELED ENTRY]" + headerLedger.particulars
			ledgerServices.save(headerLedger)
			*/

			ledgerServices.reverseEntriesCustom(headerLedger,headerLedger.transactionDate)
		}

		payments.each { PaymentTracker ptracker ->

			List<BillingItem> hciPayments = []

			hciPayments << billingService.addPayment(
					billing.id,
					ptracker.totalpayments,
					null,
					ptracker,
					null,
					false,
					true,
					ptracker.createdDate
			)

			paymentTrackerServices.postToAccounting(ptracker, [], hciPayments)
		}
	}


	// for inactive folios. After fixing payments
	void processARClearing (Billing billing){

		// reversing AR Entries
		def arDeductions = billing.billingItemList.findAll {
			it.status == BillingItemStatus.ACTIVE &&
					it.itemType == BillingItemType.DEDUCTIONS &&
					!it.details.containsKey(BillingItemDetailParam.PF_EMPLOYEEID.name()) &&
					it.postedledger != null
		}

		arDeductions.each {
			def postedLedger = it.postedledger
			if(postedLedger)
				ledgerServices.reverseEntriesCustom(ledgerServices.findOne(postedLedger),it.transactionDate)

			it.postedledger = null
			billingItemServices.save(it)
		}

		if(arDeductions){
			entityManager.refresh(billing)
			billingService.postAccountsReceibleFromCompanyAccounts(billing)
		}

	}

}
