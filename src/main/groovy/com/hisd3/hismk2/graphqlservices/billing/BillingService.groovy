package com.hisd3.hismk2.graphqlservices.billing

import com.hisd3.hismk2.dao.price_tier.PriceTierDetailDao
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.IntegrationTemplate
import com.hisd3.hismk2.domain.accounting.JournalType
import com.hisd3.hismk2.domain.accounting.LedgerDocType
import com.hisd3.hismk2.domain.accounting.LedgerHeaderDetailParam
import com.hisd3.hismk2.domain.billing.*
import com.hisd3.hismk2.domain.cashiering.PaymentTracker
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.graphqlservices.accounting.IntegrationServices
import com.hisd3.hismk2.graphqlservices.accounting.LedgerServices
import com.hisd3.hismk2.graphqlservices.ancillary.OrderslipService
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoServiceJavers
import com.hisd3.hismk2.graphqlservices.cashiering.PaymentTrackerServices
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.graphqlservices.versioning.Hisd3EntityVersionInfoHistory
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.ancillary.OrderSlipItemRepository
import com.hisd3.hismk2.repository.ancillary.ServiceRepository
import com.hisd3.hismk2.repository.billing.PriceTierDetailRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.philhealth.ICDCodesRepository
import com.hisd3.hismk2.repository.philhealth.RVSCodesRepository
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.repository.pms.PatientRepository
import com.hisd3.hismk2.repository.pms.TransferRepository
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.EntityObjectMapperService
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import com.hisd3.hismk2.services.PercentageUtils
import graphql.GraphQL
import groovy.json.JsonSlurper
import groovy.transform.Canonical
import groovy.util.logging.Slf4j
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLContext
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.BooleanUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.math.NumberUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

import javax.transaction.Transactional
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// ============ return types ========
class BillingOperationResult {
	@GraphQLQuery
	String message

	@GraphQLQuery
	boolean operationOk

	@GraphQLQuery
	UUID entityIdRef
}

enum DISCOUNTS_INTEGRATION {
	DISCOUNTS_DEDUCT
}
enum PAYMENTS_ADJUSTMENTS {
	REAPPLICATION_OF_PAYMENTS
}

@Canonical
class Balances {

	String description
	BigDecimal balance
	String type
	UUID employeeid
	UUID billingItemId
}

class DeductionsTableData {

	@GraphQLQuery
	List<BillingItem> rooms = []
	@GraphQLQuery
	List<BillingItem> meds = []
	@GraphQLQuery
	List<BillingItem> labs = []
	@GraphQLQuery
	List<BillingItem> or = []
	@GraphQLQuery
	List<BillingItem> supplies = []
	@GraphQLQuery
	List<BillingItem> misc = []

	@GraphQLQuery
	List<BillingItem> deductions = []

	@GraphQLQuery
	List<BillingItem> pfCharges = []

	@GraphQLQuery
	List<BillingItem> pfDeductions = []


}

@Canonical
class PHICCaseRate {
	String tier = ""
	String type = ""
	String code = "", description = ""
	BigDecimal hospitalShare = 0.0
	BigDecimal pfShare = 0.0

}


@Canonical
class PaymentComponent{
	String ornumber
	UUID billingItemId
	String recordNo
	String description
	BigDecimal amountShare
}


@Slf4j
@Service
@GraphQLApi
@Transactional(rollbackOn = Exception.class)
class BillingService extends AbstractDaoServiceJavers<Billing> {

	BillingService() {
		super(Billing.class)
	}


	@GraphQLQuery(name="billingSnapshotHistory")
	List<Hisd3EntityVersionInfoHistory> billingSnapshotHistory(@GraphQLArgument(name = "billingId") UUID billingId) {
		return getSnapshotHistory(getBillingById(billingId))
	}


	@Autowired
	ServiceRepository serviceRepository

	@Autowired
	BillingService billingService

	@Autowired
	PriceTierDetailDao priceTierDetailDao

	@Autowired
	EntityObjectMapperService entityObjectMapperService

	@Autowired
	PatientRepository patientRepository

	@Autowired
	CaseRepository caseRepository

	@Autowired
	GeneratorService generatorService

	@Autowired
	DiscountsService discountsService

	@Autowired
	UserRepository userRepository

	@Autowired
	EmployeeRepository employeeRepository

	@Autowired
	PriceTierDetailRepository priceTierDetailRepository

	@Autowired
	BillingItemServices billingItemServices

	@Autowired
	CompanyAccountServices companyAccountServices

	@Autowired
	PackageServices packageServices

	@Autowired
	HospitalConfigService hospitalConfigService

	@Autowired
	OrderslipService orderslipService

	@Autowired
	ICDCodesRepository icdCodesRepository

	@Autowired
	RVSCodesRepository rvsCodesRepository

	@Autowired
	OrderSlipItemRepository orderSlipItemRepository

	@Autowired
	PaymentTrackerServices paymentTrackerServices


	@Autowired
	DepartmentRepository departmentRepository

	@Autowired
	IntegrationServices integrationServices

	@Autowired
	LedgerServices ledgerServices

	@Autowired
	TransferRepository transferRepository

	@Value('${accounting.autopostjournal}')
	Boolean auto_post_journal


	@GraphQLQuery(name = "getPaymentComponents")
	List<PaymentComponent> getPaymentComponents(@GraphQLArgument(name = "billingItemId") UUID billingItemId
	) {

		List<PaymentComponent> shares = []

		def billingItem = billingItemServices.findOne(billingItemId)

		def orNumber = billingItem.details[BillingItemDetailParam.ORNUMBER.name()]
		if(StringUtils.isNotBlank(orNumber)){

			def list = billingItemServices.getBillingItemsByOr(orNumber)

			list.each {
				 bi ->


					 if(bi.details.containsKey(BillingItemDetailParam.PF_EMPLOYEEID.name())){
						 //this is PF Payment
						 shares << new PaymentComponent(orNumber,
								 bi.id,
								 "",
								 bi.description,
								 bi.subTotal * -1
						 )
					 }
					 else {

					     def ids = bi.amountdetails.collect {
							 entry ->
								 String targetBilling = entry.key
								 UUID.fromString(targetBilling)
						 }



						 def bis = billingItemServices.billingItemByIds(ids)


						 bis.each {
							    bit->

									def amountShare = bi.amountdetails[bit.id.toString()]

									shares << new PaymentComponent(orNumber,
											bit.id,
											bit.recordNo,
											bit.description,
											amountShare
									)
						 }

					 }


			}

		}
		shares
	}

	/*
	  This will be called when folio will be successfully closed
	 */
	Boolean recompPayments(UUID billingId) {

		def billing  = findOne(billingId)

		if(billing.status == "INACTIVE" && billing.locked){
			// get all payments

		def allPayments =	billing.billingItemList.findAll {
				 it.itemType == BillingItemType.PAYMENTS &&
						 it.status == BillingItemStatus.ACTIVE //&& BooleanUtils.isNotTrue(it.paymentRecomp) // always recomp
			}.toSorted {
			a,b ->
				b.transactionDate <=> a.transactionDate
		 }


			// recomp all payments
			/*
			  Notes:
			    1.) Pf payments are already Recomped
			    2.) Voided Payments are ignored
			 */

			List<BillingItem> chargesForProcess = []
			def pfCharges = billing.billingItemList.findAll {
				it.status == BillingItemStatus.ACTIVE && it.itemType == BillingItemType.PF
			}




			def hciCharges = billing.billingItemList.findAll {
				it.status == BillingItemStatus.ACTIVE && !(it.itemType in [BillingItemType.PF,BillingItemType.DEDUCTIONSPF,
				BillingItemType.DEDUCTIONS,BillingItemType.PAYMENTS])
			}

			def  hciDeductions = billing.billingItemList.findAll {
				it.status == BillingItemStatus.ACTIVE && it.itemType == BillingItemType.DEDUCTIONS
			}


			hciCharges.each {
				def totalCharges = it.subTotal
				def totalDeduct = 0.0
				def  itemBalance = 0.0

				def matchDeduct = hciDeductions.findAll { it.amountdetails.containsKey(it.id.toString())}

				matchDeduct.each {ded->
					totalDeduct += ded.amountdetails[it.id.toString()]
				}

				itemBalance = totalCharges + totalDeduct
				it.tmpBalance = itemBalance.setScale(2,RoundingMode.HALF_EVEN)

				chargesForProcess << it
			}


			// Separate Recomp for PF Fees



			List<BillingItem> hciPayments = []

			for(pmt in allPayments){

				boolean  isPFPayment = false
				pfCharges.each {
					def employeeId = it.details[BillingItemDetailParam.PF_EMPLOYEEID.name()]

					if (StringUtils.isNotBlank(employeeId)) {
						// is PF Charges will
						if(pmt.amountdetails.containsKey(employeeId))
						{
							isPFPayment = true
							pmt.pfPaymentRecomp = true
						}

					}
				}

				if(isPFPayment){
					billingService.save(pmt)


				}
				if(!isPFPayment){
					hciPayments << pmt
				}

			}






			//def formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")
			for(pmt in hciPayments){

				//println("======Start ${pmt.description} with Payment ${new DecimalFormat("#,##0.00").format(pmt.subTotal)}= Paid ${pmt.transactionDate.atZone(ZoneId.systemDefault()).format(formatter)}=====")

				BigDecimal amountPaidBalance = pmt.subTotal * -1 // this is credit so its a negative no.

				 List<Tuple2<BillingItem,BigDecimal>> mapSuccessForThisPayment = []


			    def filteredForProcessBalance =	chargesForProcess.findAll { it.tmpBalance > 0}

				for(charges in filteredForProcessBalance){
						 // theres still balance in payment
						 if(amountPaidBalance > 0){
							 if(amountPaidBalance >= charges.tmpBalance)
							 {
								 // deduct tmpBalanc
								 amountPaidBalance -= charges.tmpBalance
								 // record as success
								 mapSuccessForThisPayment.add(new Tuple2<BillingItem, BigDecimal>(charges,charges.tmpBalance))
								 charges.tmpBalance = 0
							 }
							 else {
								 charges.tmpBalance -= amountPaidBalance
								 mapSuccessForThisPayment.add(new Tuple2<BillingItem, BigDecimal>(charges,amountPaidBalance))
								 amountPaidBalance = 0
							 }
						 }
						 else {
							 break
						 }

				}

				//println("======Printing ${pmt.description}  Shares======")

				/*BigDecimal recompPerPayment = 0.0
				mapSuccessForThisPayment.each {
					//println("${it.first.recordNo} - ${it.first.description}  =  ${new DecimalFormat("#,##0.00").format(it.second)}")

					println(String.format("%10s - %-50.50s = %15s",it.first.recordNo,it.first.description,new DecimalFormat("#,##0.00").format(it.second)))
					recompPerPayment += it.second

				}
				println("***********Total Per Payment ${new DecimalFormat("#,##0.00").format(recompPerPayment)}  Shares***********")
				println("***********End Printing ${pmt.description}  Shares***********")
				println()
				println()
				println()*/

				mapSuccessForThisPayment.each {
					 def bi = it.first
					 def amt  = it.second
					 pmt.amountdetails[bi.id.toString()] = amt
				}
				pmt.paymentRecomp = true
				billingItemServices.save(pmt)
			}
		}
	}
	@GraphQLQuery(name = "getItemsThatAreCashBasis")
	List<BillingItem> getItemsThatAreCashBasis(@GraphQLArgument(name = "billingId") UUID billingId
	) {

		List<BillingItem> result = []
		def billing = findOne(billingId)

		def cashBasis = orderSlipItemRepository.getCashBasisItems(billing)

		cashBasis.each {
			if (it.billing_item) {
				it.billing_item.orderSlipItemNo = it.itemNo
				result << it.billing_item
			}
		}

		return result
	}

	@GraphQLQuery(name = "getCaseRatesFromBilling")
	List<PHICCaseRate> getCaseRatesFromBilling(@GraphQLArgument(name = "billingId") UUID billingId
	) {

		List<PHICCaseRate> result = []
		def billing = findOne(billingId)
		def jsonSlurper = new JsonSlurper()
		if (StringUtils.isNotBlank(billing?.patientCase?.primaryDx)) {

			def primaryJson = jsonSlurper.parseText(billing?.patientCase?.primaryDx) as Map<String, Object>

			PHICCaseRate primary = new PHICCaseRate()
			primary.tier = "PRIMARY"

			if (primaryJson.containsKey("diagnosisCode")) {
				primary.type = "ICD"
				primary.code = primaryJson["diagnosisCode"]
			}

			if (primaryJson.containsKey("rvsCode")) {
				primary.type = "RVS"
				primary.code = primaryJson["rvsCode"]

			}

			// get the Description and Amount
			primary.description = primaryJson["longName"]

			if (StringUtils.isNotBlank(primary.type)) {
				if (primary.type == "ICD") {
					def icd = icdCodesRepository.searchICDCodesOnly(primary.code).find()
					if (icd) {
						primary.hospitalShare = icd.primaryHospShare1
						primary.pfShare = icd.primaryProfShare1
					}
				} else {
					def rvs = rvsCodesRepository.searchRVSCodesOnly(primary.code).find()


					if (rvs) {
						primary.hospitalShare = rvs.primaryHospShare1
						primary.pfShare = rvs.primaryProfShare1
					}
				}

		//START//  THIS CODE WILL OVERRIDE THE CODE ABOVE

				try {

					primary.hospitalShare = BigDecimal.valueOf(primaryJson["primaryHospShare1"])
					primary.pfShare = BigDecimal.valueOf(primaryJson["primaryProfShare1"])
				}
				catch (Exception e)
				{
					e.printStackTrace()
				}

		//END//  THIS CODE WILL OVERRIDE THE CODE ABOVE
				result << primary
			}

		}

		if (StringUtils.isNotBlank(billing?.patientCase?.secondaryDx)) {

			def secondaryJson = jsonSlurper.parseText(billing?.patientCase?.secondaryDx) as Map<String, Object>

			PHICCaseRate secondary = new PHICCaseRate()
			secondary.tier = "SECONDARY"

			if (secondaryJson.containsKey("diagnosisCode")) {
				secondary.type = "ICD"
				secondary.code = secondaryJson["diagnosisCode"]
			}

			if (secondaryJson.containsKey("rvsCode")) {
				secondary.type = "RVS"
				secondary.code = secondaryJson["rvsCode"]

			}

			// get the Description and Amount
			secondary.description = secondaryJson["longName"]

			if (StringUtils.isNotBlank(secondary.type)) {
				if (secondary.type == "ICD") {

					def icd = icdCodesRepository.searchICDCodesOnly(secondary.code).find()
					if (icd) {
						secondary.hospitalShare = icd.secondaryHospShare
						secondary.pfShare = icd.secondaryProfShare
					}
				} else {
					def rvs = rvsCodesRepository.searchRVSCodesOnly(secondary.code).find()

					if (rvs) {

						secondary.hospitalShare = rvs.secondaryHospShare
						secondary.pfShare = rvs.secondaryProfShare
					}
				}

				result << secondary
			}

		}

		result
	}

	def processVoidORPayment(UUID billingId, PaymentTracker paymentTracker, String remarks) {



		def activePayments = billingItemServices.getBillingItemsAll(billingId, [BillingItemType.PAYMENTS.name()], "").findAll {
			it.status == BillingItemStatus.ACTIVE
		}

		//  1.) Cancel Pf payments witch are ACTIVE with match BillingItemDetailParam.PAYTRACKER_ID
		activePayments.findAll {
			StringUtils.equalsIgnoreCase(it.details.getOrDefault(BillingItemDetailParam.PAYTRACKER_ID.name(), "").toString(),
					paymentTracker.id.toString())
		}.each {
			bi ->

				billingItemServices.cancelBillingItem(
						bi.id,
						[(BillingItemDetailParam.VOIDTYPE.name()): remarks]
				)
		}

		/*// 2.) If Billing has a REAPP cancel all Payments with key BillingItemDetailParam.REAPPLICATION
		activePayments.findAll {
			it.details.containsKey(BillingItemDetailParam.REAPPLICATION.name())
		}.each {
			bi ->

				billingItemServices.cancelBillingItem(
						bi.id,
						[(BillingItemDetailParam.VOIDTYPE.name()): remarks]
				)
		}*/

		def billing = findOne(billingId)

		billing.status = "ACTIVE"

		save(billing)
	}

	@GraphQLMutation
	GraphQLRetVal<Billing> reOpenFolio(
			@GraphQLArgument(name = "billingId") UUID billingId

	) {
		def billing = findOne(billingId)

		billing.status = "ACTIVE"
		billing.locked = true

		/*// resetting recomp
		billing.billingItemList.findAll {
			BooleanUtils.isTrue(it.paymentRecomp)
		}.each {
			it.paymentRecomp = false
		}


	 	billing.billingItemList.findAll {
			it.itemType == BillingItemType.PAYMENTS &&
					it.status == BillingItemStatus.ACTIVE  && BooleanUtils.isNotTrue(it.paymentRecomp)
		}.each {
			it.paymentRecomp = false
			it.pfPaymentRecomp = false


		}
*/
		billing = save(billing)





		return new GraphQLRetVal<Billing>(billing, true, "Folio Successfully Re-Opened")

	}

	@GraphQLMutation
	GraphQLRetVal<Billing> finalizedSoa(
			@GraphQLArgument(name = "billingId") UUID billingId

	) {
		def billing = findOne(billingId)
		billing.finalizedSoa = true
		if(billing.patientCase){
			billing.finalSoa =billing?.patientCase.registryType + "-" +  generatorService.getNextSoaNumber(billing?.patientCase.registryType){
				StringUtils.leftPad(it +"" ,6,"0")
			}
		}
		save(billing)
		return new GraphQLRetVal<Billing>(billing, true, "SOA Successfully Finalized")

	}

	@GraphQLMutation
	GraphQLRetVal<Billing> allowprogress(
			@GraphQLArgument(name = "billingId") UUID billingId

	) {
		def billing = findOne(billingId)
		billing.overrideProgressPayment = !billing.overrideProgressPayment;

		save(billing)

		return new GraphQLRetVal<Billing>(billing, true, "Folio Successfully allows progress payment")
	}

	@GraphQLMutation
	GraphQLRetVal<Billing> closeFolio(@GraphQLArgument(name = "billingId") UUID billingId) {

		def billing = findOne(billingId)

		if (billing?.patientCase?.registryType == "IPD") {
			 if(BooleanUtils.isNotTrue(billing.finalizedSoa))
			  return new GraphQLRetVal<Billing>(billing, false, "In-Patient Folios are required to get Finalized")
		}


		if (billing.balance > 0.50) {
			return new GraphQLRetVal<Billing>(billing, false, "There is a Balance for this Folio")
		}


		billing.status = "INACTIVE"
		billing.locked = true
		billing = save(billing)

		if(auto_post_journal) {
			applyDiscount(billing)
			//reapplyPayments(billing)

			if(billingService.isAllowedProgressPayment(billing) || billing?.patientCase?.registryType=="IPD")
			reapplyPaymentsV2(billing)

			postAccountsReceibleFromCompanyAccounts(billing)
		}

		return new GraphQLRetVal<Billing>(billing, true, "Folio Successfully Closed")
	}

	def postAccountsReceibleFromCompanyAccounts(Billing billing){
		def yearFormat = DateTimeFormatter.ofPattern("yyyy")

		def arDeductions = billing.billingItemList.findAll {
			it.status == BillingItemStatus.ACTIVE &&
					it.itemType == BillingItemType.DEDUCTIONS &&
					!it.details.containsKey(BillingItemDetailParam.PF_EMPLOYEEID.name()) &&
					it.postedledger == null
		}


		arDeductions.each {arDeduct->

			def companyAccountId = arDeduct.details[BillingItemDetailParam.COMPANY_ACCOUNT_ID.name()]



			if(companyAccountId){
				Map<String,String> mDetails = [:]
				arDeduct.details.each { k,v ->
					mDetails[k] = v
				}
				mDetails[LedgerHeaderDetailParam.BILLING_ID.name()] = billing.id.toString()
				Map<String,BigDecimal> registryType = [:]

				registryType["ERD"] = 0.0
				registryType["OPD"] = 0.0
				registryType["IPD"] = 0.0
				registryType["OTC"] = 0.0

				def companyAccountIdEntity = companyAccountServices.findOne(UUID.fromString(companyAccountId))
				arDeduct.amountdetails.each { k, v ->
					def sourceItem = billingItemServices.findOne(UUID.fromString(k))
					registryType[sourceItem.registryTypeCharged] = registryType[sourceItem.registryTypeCharged] + v
				}

				def headerLedger =	integrationServices.generateAutoEntries(arDeduct){ template,multiple->
					template.flagValue = "AR_DEDUCT"
					template.arInPatient = registryType["IPD"].abs() * -1
					template.arOutPatient = registryType["OPD"].abs() * -1
					template.arOtcPatient = registryType["OTC"].abs() * -1
					template.arErPatient = registryType["ERD"].abs() * -1

					template.companyAccount = companyAccountIdEntity

					if(companyAccountIdEntity.directToAr){

						template.arDeductionTotalDirect = template.subTotal * -1 // this are negative in subtotal
						template.arDeductionTotalClearing = 0.0
					}
					else{
						template.arDeductionTotalDirect = 0.0
						template.arDeductionTotalClearing = template.subTotal * -1 // this are negative in subtotal
					}
				}

				def pHeader =ledgerServices.persistHeaderLedger(headerLedger,
						"${billing.createdDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${billing.billingNo}",
						"${billing.billingNo}-${billing.patient?.fullName?:(billing?.otcname?:"")}",
						"${arDeduct.recordNo}-${arDeduct.description}",
						LedgerDocType.AJ,
						JournalType.GENERAL,
						arDeduct.transactionDate,
						mDetails
						)

				arDeduct.postedledger = pHeader.id

				billingItemServices.save(arDeduct)
			}

		}

	}


	def reapplyPaymentsV2(Billing billing){

		def payments = billing.billingItemList.findAll {
			it.status == BillingItemStatus.ACTIVE &&
					it.itemType == BillingItemType.PAYMENTS &&
					!it.details.containsKey(BillingItemDetailParam.PF_EMPLOYEEID.name()) &&
					!it.reappliedDatetime &&
					it.isProgress
		}

		def posted = billing.billingItemList.findAll {
			it.status == BillingItemStatus.ACTIVE &&
					it.itemType == BillingItemType.PAYMENTS &&
					!it.details.containsKey(BillingItemDetailParam.PF_EMPLOYEEID.name()) &&
					!it.isProgress
		}

		Map<String,BigDecimal> paymentShare = [:]
		posted.each {
			it.amountdetails.each { k, v->
				if(!paymentShare.containsKey(k)){
					paymentShare[k] = v
				}
				else
					paymentShare[k] = paymentShare[k] + v
			}

		}

		if(payments.size() > 0){

			def totalProgressPayments = 0.0
			def pfBalancesAmount = 0.0
			List<BillingItem> pfPayments = []




			payments.each {
				totalProgressPayments += (it.subTotal) * -1
			}

			// Billing Progress payments Canceled Billing Payments for ReApplication
			billingService.addPayment(
					billing.id,
					totalProgressPayments * -1,
					null,
					null,
					"RE-APPLICATION OF PAYMENTS"
			)


			def pfBalances = billingService.balances(billing.id).findAll { it.employeeid }
			pfBalances.each {
				pfBalancesAmount += it.balance

				if(it.balance > 0){
					billingService.addPayment(
							billing.id,
							it.balance,
							it.employeeid,
							null
					)
				}

			}

			def totalHospitalPayments = totalProgressPayments - pfBalancesAmount

			def reapplicationItem =	billingService.addPayment(
					billing.id,
					totalHospitalPayments,
					null,
					null
			)



			Map<String,BigDecimal> registryTypeHospital = [:]
			registryTypeHospital["ERD"] = 0.0
			registryTypeHospital["OPD"] = 0.0
			registryTypeHospital["IPD"] = 0.0
			registryTypeHospital["OTC"] = 0.0

			List<BillingItem> realBalance = []


			def previousDeductions = billing.billingItemList.findAll {
				it.itemType == BillingItemType.DEDUCTIONS &&
						it.status == BillingItemStatus.ACTIVE
			}

			billing.billingItemList.findAll {
				it.status == BillingItemStatus.ACTIVE  && it.itemType in [BillingItemType.ROOMBOARD,
																		  BillingItemType.MEDICINES,
																		  BillingItemType.DIAGNOSTICS,
																		  BillingItemType.ORFEE,
																		  BillingItemType.SUPPLIES,
																		  BillingItemType.OTHERS]
			}.each {  bItem ->
				def subtotal = bItem.subTotal

				if(paymentShare.containsKey(bItem.id.toString())){
					subtotal = subtotal - paymentShare[bItem.id.toString()]
				}

				if(!previousDeductions){
					bItem.tmpBalance = subtotal
					realBalance << bItem
				} else {


					def totalDeduct = 0.0
					previousDeductions.each {
						prevdeduct ->
							if (prevdeduct.amountdetails.containsKey(bItem.id.toString()))
								totalDeduct = totalDeduct + prevdeduct.amountdetails[bItem.id.toString()]
					}

					bItem.tmpBalance = subtotal - totalDeduct
					realBalance << bItem

				}

			}

			def realHospitalBalance = 0.0
			realBalance.each {
				realHospitalBalance += it.tmpBalance
			//	println("${it.description} = ${it.tmpBalance.toPlainString()}" )
				def tmpBalance = it.tmpBalance
				def rType = it.registryTypeCharged
				registryTypeHospital[rType] = registryTypeHospital[rType] + tmpBalance
			}


			def forCreditToAr = realHospitalBalance + pfBalancesAmount


			def headerLedger =	integrationServices.generateAutoEntries(new IntegrationTemplate()){ template,multiple->
				template.flagValue = "REAPPLICATION_OF_PAYMENTS_MOD"

				template.value_a = forCreditToAr * -1
				template.value_b = registryTypeHospital["IPD"] * -1
				template.value_c = registryTypeHospital["OPD"] * -1
				template.value_d = registryTypeHospital["OTC"] * -1
				template.value_e = registryTypeHospital["ERD"] * -1
				template.value_f = pfBalancesAmount
			}

			def yearFormat = DateTimeFormatter.ofPattern("yyyy")

			ledgerServices.persistHeaderLedger(headerLedger,
					"${billing.createdDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${billing.billingNo}",
					"${billing.billingNo}-${billing.patient.fullName}",
					"PAYMENT REAPPLICATION",
					LedgerDocType.AJ,
					JournalType.GENERAL,
					Instant.now(),
					[(LedgerHeaderDetailParam.BILLING_ID.name()):billing.id.toString()])
			payments.each {
				it.reappliedDatetime = Instant.now()
				billingItemServices.save(it)
			}
		}

	}

	@Deprecated
	def reapplyPayments(Billing billing){

		// pls. check this as to initiate reapplication


		def payments = billing.billingItemList.findAll {
			 it.status == BillingItemStatus.ACTIVE &&
					 it.itemType == BillingItemType.PAYMENTS &&
					 !it.details.containsKey(BillingItemDetailParam.PF_EMPLOYEEID.name())

		}

		if(payments.size()>1){
			// this needs to be more than 1 payments...

			Map<String,BigDecimal> registryType = [:]

			registryType["ERD"] = 0.0
			registryType["OPD"] = 0.0
			registryType["IPD"] = 0.0
			registryType["OTC"] = 0.0


			def totalPaymentsHCI = 0.0
			def totalRealPayments = 0.0
			payments.each {
				BigDecimal recognized = 0
				   it.amountdetails.each { k, v ->
					   def sourceItem = billingItemServices.findOne(UUID.fromString(k))
					    registryType[sourceItem.registryTypeCharged] = registryType[sourceItem.registryTypeCharged] + v
					    recognized += v
				   }

				// will only recognized payments starting Nov. 21
				totalPaymentsHCI += recognized
				totalRealPayments += it.subTotal.abs() // credit
			}
			// no need to be deleted... just need to adjust amount_details


			def unrecognized = (totalRealPayments - totalPaymentsHCI).abs()

			def headerLedger =	integrationServices.generateAutoEntries(new IntegrationTemplate()){ template,multiple->


				template.flagValue = "REAPPLICATION_OF_PAYMENTS"
				template.value_a = registryType["IPD"] * 1
				template.value_b = registryType["OPD"] * 1
				template.value_c = registryType["OTC"] * 1
				template.value_d = registryType["ERD"] * 1

				template.value_e = totalPaymentsHCI * 1
			}
			def yearFormat = DateTimeFormatter.ofPattern("yyyy")


			ledgerServices.persistHeaderLedger(headerLedger,
					"${billing.createdDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${billing.billingNo}",
					"${billing.billingNo}-${billing.patient.fullName}",
					"PAYMENT DEBIT ADJUSTMENT ${unrecognized > 0.0  ? "w/ Unposted Payments Prior 11/21/2020 ${new DecimalFormat("#,###.00").format(unrecognized)}":""}",
					LedgerDocType.AJ,
					JournalType.GENERAL,
					Instant.now(),
					[(LedgerHeaderDetailParam.BILLING_ID.name()):billing.id.toString()])


			// ============now make adjustment=======================

			List<BillingItem> realBalance = []


			def previousDeductions = billing.billingItemList.findAll {
				it.itemType == BillingItemType.DEDUCTIONS &&
						it.status == BillingItemStatus.ACTIVE
			}

			billing.billingItemList.findAll {
				it.status == BillingItemStatus.ACTIVE  && it.itemType in [BillingItemType.ROOMBOARD,
																		  BillingItemType.MEDICINES,
																		  BillingItemType.DIAGNOSTICS,
																		  BillingItemType.ORFEE,
																		  BillingItemType.SUPPLIES,
																		  BillingItemType.OTHERS]
			}.each {  bItem ->
				def subtotal = bItem.subTotal

				if(!previousDeductions){
					bItem.tmpBalance = subtotal
					realBalance << bItem
				} else {
					previousDeductions.each {
						prevdeduct ->

							if (prevdeduct.amountdetails.containsKey(bItem.id.toString()))
								subtotal -= prevdeduct.amountdetails[bItem.id.toString()]

							if(!(subtotal == 0.0)){
								bItem.tmpBalance = subtotal
								realBalance << bItem
							}
					}
				}

			}


			payments.each {
				  it.amountdetails.clear()

				billingItemServices.save(it)
			}



		    Map<BillingItem,Map<String,BigDecimal>> tmpAmount = [:]


			payments.each {pmt->

				BigDecimal amountPaidBalance = pmt.subTotal * -1  // its a credit
				List<Tuple2<BillingItem,BigDecimal>> mapSuccessForThisPayment = []


				for(charges in realBalance){

					if(amountPaidBalance > 0){
						if(amountPaidBalance >= charges.tmpBalance)
						{
							// deduct tmpBalanc
							amountPaidBalance -= charges.tmpBalance
							// record as success
							mapSuccessForThisPayment.add(new Tuple2<BillingItem, BigDecimal>(charges,charges.tmpBalance))
							charges.tmpBalance = 0
						}
						else {
							charges.tmpBalance -= amountPaidBalance
							mapSuccessForThisPayment.add(new Tuple2<BillingItem, BigDecimal>(charges,amountPaidBalance))
							amountPaidBalance = 0
						}
					}
					else {
						break
					}
				}


				Map<String,BigDecimal> amts = [:]

				mapSuccessForThisPayment.each {

					//println("====================")
					def bi = it.first
					def amt  = it.second

					if(amt != 0.0){
						pmt.amountdetails[bi.id.toString()] = amt
						//println("${bi.recordNo} : ${amt}")
						amts[bi.id.toString()] = amt
					}


				}
				tmpAmount[pmt] =amts
				billingItemServices.save(pmt)
			}




			// ====== Apply new changes =====



			registryType = [:]

			registryType["ERD"] = 0.0
			registryType["OPD"] = 0.0
			registryType["IPD"] = 0.0
			registryType["OTC"] = 0.0

			totalPaymentsHCI = 0.0
			tmpAmount.each { it, amts->

				BigDecimal totalHCIPaymentsCredit = 0
				amts.each { k, v ->
					def sourceItem = billingItemServices.findOne(UUID.fromString(k))
					registryType[sourceItem.registryTypeCharged] = registryType[sourceItem.registryTypeCharged] + v
					totalHCIPaymentsCredit += v
				}

				// will only recognized paid
				totalPaymentsHCI += totalHCIPaymentsCredit
			}


			  headerLedger = integrationServices.generateAutoEntries(new IntegrationTemplate()){ template,multiple->

			    template.flagValue = "REAPPLICATION_OF_PAYMENTS"
				template.value_a = registryType["IPD"] * -1
				template.value_b = registryType["OPD"] * -1
				template.value_c = registryType["OTC"] * -1
				template.value_d = registryType["ERD"] * -1

				template.value_e = totalPaymentsHCI * -1
			}


			ledgerServices.persistHeaderLedger(headerLedger,
					"${billing.createdDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${billing.billingNo}",
					"${billing.billingNo}-${billing.patient.fullName}",
					"REAPPLICATION OF PAYMENTS",
					LedgerDocType.AJ,
					JournalType.GENERAL,
					Instant.now(),
					[(LedgerHeaderDetailParam.BILLING_ID.name()):billing.id.toString()])



		}

	}
	def applyDiscount(Billing billing){

		// get active discount from Billing
        billing.billingItemList.findAll {
			it.status == BillingItemStatus.ACTIVE &&
					it.itemType == BillingItemType.DEDUCTIONS &&
					it.postedledger == null
		}.each {


			Map<String,String> mDetails = [:]

			it.details.each { k,v ->
				mDetails[k] = v
			}


			if(it.details.containsKey(BillingItemDetailParam.DISCOUNT_ID.name())){
				//DISCOUNTS_DEDUCT
			def headerLedger =	integrationServices.generateAutoEntries(it) { bItem, multiple ->

				def discount = discountsService.findOne(UUID.fromString(bItem.details[BillingItemDetailParam.DISCOUNT_ID.name()]))


				bItem.flagValue = "DISCOUNTS_DEDUCT"

				// debit department
				Map<UUID, BigDecimal> departmentMap = [:]

				Map<String, BigDecimal> registryType = [:]

				registryType["ERD"] = 0.0
				registryType["OPD"] = 0.0
				registryType["IPD"] = 0.0
				registryType["OTC"] = 0.0

				// get details
				bItem.amountdetails.each { k, v ->
					BillingItem bTarget = billing.billingItemList.find { it.id == UUID.fromString(k) }
					if (bTarget) {
						if (!departmentMap.containsKey(bTarget.department.id))
							departmentMap[bTarget.department.id] = 0.0

						departmentMap[bTarget.department.id] = (departmentMap[bTarget.department.id] + v)

						if (!registryType.containsKey(bTarget.registryTypeCharged))
							registryType[bTarget.registryTypeCharged] = 0.0

						registryType[bTarget.registryTypeCharged] = registryType[bTarget.registryTypeCharged] + v
					}
				}


				bItem.vatAmountdetails.each { k, v ->
					BillingItem bTarget = billing.billingItemList.find { it.id == UUID.fromString(k) }
					if (bTarget) {
						if (!registryType.containsKey(bTarget.registryTypeCharged))
							registryType[bTarget.registryTypeCharged] = 0.0

						registryType[bTarget.registryTypeCharged] = registryType[bTarget.registryTypeCharged] + v
					}
				}



				List<BillingItem> debits = []

				departmentMap.each { k, v ->
					debits << new BillingItem().tap {
						it.discount = discount
						it.discountDepartment = departmentRepository.findById(k).get()
						it.discountAmount = v
					}
				}



				multiple << debits


				bItem.discountAmountArOTC = registryType["OTC"] * -1
				bItem.discountAmountArER = registryType["ERD"] * -1
				bItem.discountAmountArOPD = registryType["OPD"] * -1
				bItem.discountAmountArIP = registryType["IPD"] * -1


				bItem.discountAmountVat = 0.00
				bItem.vatAmountdetails.each {
					k, v ->
						bItem.discountAmountVat += v
				}

				bItem.discountAmountVat = bItem.discountAmountVat * -1 // a deduction

			}

				def yearFormat = DateTimeFormatter.ofPattern("yyyy")

				def pHeader =	ledgerServices.persistHeaderLedger(headerLedger,
						"${it.billing.createdDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${it.billing.billingNo}",
						"${it.billing.billingNo}-${it.billing?.patient?.fullName?:it.billing?.otcname}",
						"${it.recordNo}-${it.description}",
						LedgerDocType.CM, // DEDUCTION ARE CREDIT MEMOS
						JournalType.SALES,
						it.transactionDate,
						mDetails)
				it.postedledger = pHeader.id


				billingItemServices.save(it)


			}

		}



	}

	@GraphQLQuery(name = "balances")
	List<Balances> balances(@GraphQLArgument(name = "billingId") UUID billingId) {

		List<Balances> result = []

		def billing = findOne(billingId)

		def hospitalTotal = 0.0
		billing.billingItemList.findAll {
			it.itemType in [BillingItemType.ROOMBOARD,
			                BillingItemType.MEDICINES,
			                BillingItemType.DIAGNOSTICS,
			                BillingItemType.ORFEE,
			                BillingItemType.SUPPLIES,
			                BillingItemType.OTHERS] && it.status == BillingItemStatus.ACTIVE
		}.each {
			hospitalTotal += it.subTotal
		}

		def hospitalDeduction = 0.0
		billing.billingItemList.findAll {
			it.itemType in [BillingItemType.DEDUCTIONS] && it.status == BillingItemStatus.ACTIVE
		}.each {
			hospitalDeduction += it.subTotal
		}

		def hospitalPayments = 0.0

		billing.billingItemList.findAll {
			it.itemType in [BillingItemType.PAYMENTS] &&
					it.status == BillingItemStatus.ACTIVE &&
					!it.details.containsKey(BillingItemDetailParam.PF_EMPLOYEEID.name())
		}.each {
			hospitalPayments += it.subTotal
		}

		result.add(new Balances("HOSPITAL", (hospitalTotal + hospitalDeduction + hospitalPayments).setScale(2, RoundingMode.HALF_EVEN), "HCI"))

		billing.billingItemList.findAll {
			it.itemType in [BillingItemType.PF] && it.status == BillingItemStatus.ACTIVE
		}.each { pf ->

			def pfTotal = pf.subTotal
			def employeeid = pf.details[BillingItemDetailParam.PF_EMPLOYEEID.name()]

			def pfDeduction = 0.0

			billing.billingItemList.findAll {
				it.itemType in [BillingItemType.DEDUCTIONSPF] && it.status == BillingItemStatus.ACTIVE &&
						it.details[BillingItemDetailParam.PF_EMPLOYEEID.name()] == employeeid
			}.each { ded ->
				pfDeduction += ded.subTotal
			}

			def pfPayments = 0.0
			billing.billingItemList.findAll {
				it.itemType in [BillingItemType.PAYMENTS] && it.status == BillingItemStatus.ACTIVE &&
						it.details[BillingItemDetailParam.PF_EMPLOYEEID.name()] == employeeid
			}.each { payment ->
				pfPayments += payment.subTotal
			}

			result.add(new Balances(pf.description, (pfTotal + pfDeduction + pfPayments).setScale(2, RoundingMode.HALF_EVEN), "PF", UUID.fromString(employeeid), pf.id))

		}
		result.add(new Balances("TOTAL BALANCE", (billing.balance ?: 0.0).setScale(2, RoundingMode.HALF_EVEN), "TOTAL BALANCE"))

		result
	}

	@GraphQLQuery(name = "deductionsTable")
	DeductionsTableData deductionsTable(@GraphQLArgument(name = "billingId") UUID billingId) {
	  return	deductionsTableImplement(billingId,true)
	}


	DeductionsTableData deductionsTableImplement(UUID billingId, Boolean includePF=false){

		def result = new DeductionsTableData()
		def billing = findOne(billingId)

		billing?.billingItemList?.findAll {
			it.itemType == BillingItemType.ROOMBOARD &&
					it.status == BillingItemStatus.ACTIVE
		}?.sort(false) {
			a, b ->
				a.createdDate <=> b.createdDate
		}?.each {
			result.rooms.add(it)
		}

		billing?.billingItemList?.findAll {
			it.itemType == BillingItemType.MEDICINES &&
					it.status == BillingItemStatus.ACTIVE
		}?.sort(false) {
			a, b ->
				a.createdDate <=> b.createdDate
		}?.each {
			result.meds.add(it)
		}

		billing?.billingItemList?.findAll {
			it.itemType == BillingItemType.DIAGNOSTICS &&
					it.status == BillingItemStatus.ACTIVE
		}?.sort(false) {
			a, b ->
				a.createdDate <=> b.createdDate
		}?.each {
			result.labs.add(it)
		}

		billing?.billingItemList?.findAll {
			it.itemType == BillingItemType.ORFEE &&
					it.status == BillingItemStatus.ACTIVE
		}?.sort(false) {
			a, b ->
				a.createdDate <=> b.createdDate
		}?.each {
			result.or.add(it)
		}

		billing?.billingItemList?.findAll {
			it.itemType == BillingItemType.SUPPLIES &&
					it.status == BillingItemStatus.ACTIVE
		}?.sort(false) {
			a, b ->
				a.createdDate <=> b.createdDate
		}?.each {
			result.supplies.add(it)
		}

		billing?.billingItemList?.findAll {
			it.itemType == BillingItemType.OTHERS &&
					it.status == BillingItemStatus.ACTIVE
		}?.sort(false) {
			a, b ->
				a.createdDate <=> b.createdDate
		}?.sort(false) {
			a, b ->
				a.createdDate <=> b.createdDate
		}?.each {
			result.misc.add(it)
		}


		if(includePF){
			// Loading PF
			billing?.billingItemList?.findAll {
				it.itemType == BillingItemType.PF &&
						it.status == BillingItemStatus.ACTIVE
			}?.sort(false) {
				a, b ->
					a.createdDate <=> b.createdDate
			}?.sort(false) {
				a, b ->
					a.createdDate <=> b.createdDate
			}?.each {
				result.pfCharges.add(it)
			}
		}




		billing?.billingItemList?.findAll {
			it.itemType == BillingItemType.DEDUCTIONS &&
					it.status == BillingItemStatus.ACTIVE
		}?.sort(false) {
			a, b ->
				a.createdDate <=> b.createdDate
		}?.each {
			result.deductions.add(it)
		}


		if(includePF){
			// deductionspf

			billing?.billingItemList?.findAll {
				it.itemType == BillingItemType.DEDUCTIONSPF &&
						it.status == BillingItemStatus.ACTIVE
			}?.sort(false) {
				a, b ->
					a.createdDate <=> b.createdDate
			}?.each {
				result.pfDeductions.add(it)
			}
		}



		result
	}
	@GraphQLQuery(name = "getPatientsForCreditLimit")
	Page<Billing> getPatientsForCreditLimit(@GraphQLArgument(name = "filter") String filter, // patient name of patient case
	                                        @GraphQLArgument(name = "page") Integer page,
	                                        @GraphQLArgument(name = "size") Integer size) {

		def defaultCreditLimit = hospitalConfigService.operationalConfig.defaultCreditLimit ?: BigDecimal.ZERO

		getPageable("""
  from Billing b where b.status = 'ACTIVE' and
  (
     lower(b.patientCase.patient.fullName) like lower(concat('%',:filter,'%')) or
     lower(b.patientCase.caseNo) like lower(concat('%',:filter,'%'))
  )
  and  b.patientCase.registryType = 'IPD'
  and  (b.balance / COALESCE(b.patientCase.creditLimit,:defaultCreditLimit)) > 0.80
   order by patientCase.patient.fullName
""",
				"""
Select count(b) from Billing b where b.status = 'ACTIVE' and
   (
     lower(b.patientCase.patient.fullName) like lower(concat('%',:filter,'%')) or
     lower(b.patientCase.caseNo) like lower(concat('%',:filter,'%'))
  )
  and  b.patientCase.registryType = 'IPD'
  and  (b.balance / COALESCE(b.patientCase.creditLimit,:defaultCreditLimit)) > 0.80
  
""",
				page,
				size,
				["filter"            : filter,
				 "defaultCreditLimit": defaultCreditLimit])

	}

	@GraphQLQuery(name = "getPatientsForDischarge")
	Page<Billing> getPatientsForDischarge(@GraphQLArgument(name = "filter") String filter, // patient name of patient case
	                                      @GraphQLArgument(name = "page") Integer page,
	                                      @GraphQLArgument(name = "size") Integer size) {

		getPageable("""
  from Billing b where b.status = 'ACTIVE' and
  (
     lower(b.patientCase.patient.fullName) like lower(concat('%',:filter,'%')) or
     lower(b.patientCase.caseNo) like lower(concat('%',:filter,'%'))
  )
  and  b.patientCase.registryType = 'IPD'
  and  (b.patientCase.mayGoHomeDatetime is not null or b.patientCase.dischargedDatetime is not null)
   order by patientCase.patient.fullName
""",
				"""
Select count(b) from Billing b where b.status = 'ACTIVE' and
  (
     lower(b.patientCase.patient.fullName) like lower(concat('%',:filter,'%')) or
     lower(b.patientCase.caseNo) like lower(concat('%',:filter,'%'))
  )
  and  b.patientCase.registryType = 'IPD'
  and  (b.patientCase.mayGoHomeDatetime is not null or b.patientCase.dischargedDatetime is not null)
  
""",
				page,
				size,
				[filter: filter])

	}

	@GraphQLQuery(name = "getFolioOTCByPage")
	Page<Billing> getFolioOTCByPage(@GraphQLArgument(name = "page") Integer page, // zero based
	                                @GraphQLArgument(name = "pageSize") Integer pageSize,
	                                @GraphQLArgument(name = "filter") String filter,
	                                @GraphQLArgument(name = "active") Boolean active
	) {

		if (active) {

			//language=HQL
			getPageable("""
		   select b from Billing b where
          (     lower(b.billingNo) like   lower(concat('%',:filter,'%'))
                or
                lower(b.otcname) like   lower(concat('%',:filter,'%'))
                )
                and b.status=:status and b.patient is  null order by b.billingNo desc
                
      """,
					"""
            select count(b) from Billing b where
						  (     lower(b.billingNo) like  lower(concat('%',:filter,'%'))
								or
								lower(b.otcname) like  lower(concat('%',:filter,'%'))
						  ) and b.status=:status and b.patient is  null
      """, page,
					pageSize,
					[
							filter: filter,
							status: "ACTIVE"
					]
			)
		} else {
			//language=HQL
			getPageable("""
		   select b from Billing b where
          (     lower(b.billingNo) like  lower(concat('%',:filter,'%'))
                or
                lower(b.otcname) like   lower(concat('%',:filter,'%'))
                ) and b.patient is   null   order by b.billingNo
                
      """,
					"""
            select count(b) from Billing b where
			  (     lower(b.billingNo) like  lower(concat('%',:filter,'%'))
                       or
                    lower(b.otcname) like   lower(concat('%',:filter,'%'))
             ) and b.patient is   null
      """, page,
					pageSize,
					[
							filter: filter
					]
			)
		}

	}
	@GraphQLQuery(name = "getFolioByPageWithRegistryType")
	Page<Billing> getFolioByPageWithRegistryType(@GraphQLArgument(name = "page") Integer page, // zero based
								 @GraphQLArgument(name = "pageSize") Integer pageSize,
								 @GraphQLArgument(name = "filter") String filter,
								 @GraphQLArgument(name = "registryType") String registryType,
								 @GraphQLArgument(name = "active") Boolean active
	) {

		if (active) {
			getPageable("""
		   select b from Billing b where
          (     lower(b.billingNo) like  lower(concat('%',:filter,'%'))
                or
                lower(b.patient.fullName) like   lower(concat('%',:filter,'%'))
                )
                and (b.patientCase.registryType=:registryType or :registryType = 'ALL')
                and b.status=:status and b.patient is not null 
                order by b.billingNo
                
      """,
					"""
            select count(b) from Billing b where
						  (     lower(b.billingNo) like   lower(concat('%',:filter,'%'))
								or
								lower(b.patient.fullName) like   lower(concat('%',:filter,'%'))
						  ) 
						    and (b.patientCase.registryType=:registryType or :registryType = 'ALL')
						    and b.status=:status and b.patient is not null
      """, page,
					pageSize,
					[
							filter: filter,
							status: "ACTIVE",
							registryType:registryType
					]
			)
		} else {
			getPageable("""
		   select b from Billing b where
          (     lower(b.billingNo) like   lower(concat('%',:filter,'%'))
                or
                lower(b.patient.fullName) like   lower(concat('%',:filter,'%'))
                )
                 and (b.patientCase.registryType=:registryType or :registryType = 'ALL')
                 and b.patient is not null  
                 order by b.billingNo 
      """,
					"""
            select count(b) from Billing b where
						  (     lower(b.billingNo) like  lower(concat('%',:filter,'%'))
								or
								lower(b.patient.fullName) like   lower(concat('%',:filter,'%')) and b.patient is not null
						  )
						   and (b.patientCase.registryType=:registryType or :registryType = 'ALL')
						   and b.patient is not null
      """, page,
					pageSize,
					[
							filter: filter,
							registryType:registryType
					]
			)
		}

	}


	@GraphQLQuery(name = "getFolioByPage")
	Page<Billing> getFolioByPage(@GraphQLArgument(name = "page") Integer page, // zero based
	                             @GraphQLArgument(name = "pageSize") Integer pageSize,
	                             @GraphQLArgument(name = "filter") String filter,
	                             @GraphQLArgument(name = "active") Boolean active
	) {

		if (active) {
			getPageable("""
		   select b from Billing b where
          (     lower(b.billingNo) like  lower(concat('%',:filter,'%'))
                or
                lower(b.patient.fullName) like   lower(concat('%',:filter,'%'))
                )
                and b.status=:status and b.patient is not null order by b.billingNo
                
      """,
					"""
            select count(b) from Billing b where
						  (     lower(b.billingNo) like   lower(concat('%',:filter,'%'))
								or
								lower(b.patient.fullName) like   lower(concat('%',:filter,'%'))
						  ) and b.status=:status and b.patient is not null
      """, page,
					pageSize,
					[
							filter: filter,
							status: "ACTIVE"
					]
			)
		} else {
			getPageable("""
		   select b from Billing b where
          (     lower(b.billingNo) like   lower(concat('%',:filter,'%'))
                or
                lower(b.patient.fullName) like   lower(concat('%',:filter,'%'))
                ) and b.patient is not null   order by b.billingNo
                
      """,
					"""
            select count(b) from Billing b where
						  (     lower(b.billingNo) like  lower(concat('%',:filter,'%'))
								or
								lower(b.patient.fullName) like   lower(concat('%',:filter,'%')) and b.patient is not null
						  )
      """, page,
					pageSize,
					[
							filter: filter
					]
			)
		}

	}

	@GraphQLQuery(name = "packageinBilling")
	List<Package> getPackageinBilling(@GraphQLArgument(name = "billingId") UUID billingId
	) {

		List<Package> packages = []

		def billing = findOne(billingId)

		billing.billingItemList.findAll { it.status == BillingItemStatus.ACTIVE }.findAll {
			it.apackage
		}.each {
			addp ->
				def match = packages.find { it.id == addp.apackage.id }

				if (!match)
					packages.add(addp.apackage)
		}

		packages

	}

	@GraphQLQuery
	List<Billing> getBillingByPatient(@GraphQLArgument(name = "patientId") UUID patientId) {
		createQuery("select b from Billing b where b.patient.id = :patientid",
				[patientid: patientId]).resultList
	}

	@GraphQLQuery
	Billing getBillingById(@GraphQLArgument(name = "billingId") UUID billingId) {
		findOne(billingId)
	}

	@GraphQLQuery
	List<BillingItem> getBillingItemsByBill(@GraphQLArgument(name = "billingId") UUID billingId) {
		findOne(billingId).billingItemList
	}

// ============= Billing  Graphql Services  Authored by : Albert Oclarit

	@GraphQLQuery(name = "getPriceTier")
	PriceTierDetail getPriceTier(@GraphQLArgument(name = "billingId") UUID billingId
	) {

		def billing = findOne(billingId)

		if (billing.pricetiermanual) {

			def t = priceTierDetailRepository.findById(billing.pricetiermanual).get()

			return t
		}

		priceTierDetailDao.getDetail(billing.patientCase.id)

	}

	@GraphQLQuery(name = "getPriceTierByCaseId")
	PriceTierDetail getPriceTierByCaseId(@GraphQLArgument(name = "caseId") UUID caseId
	) {

		def billing = billingService.findByPatientCase(caseId).find()

		if (billing && billing.pricetiermanual)
			return priceTierDetailRepository.findById(billing.pricetiermanual).get()

		priceTierDetailDao.getDetail(caseId)
	}

	@GraphQLQuery
	Page<com.hisd3.hismk2.domain.ancillary.Service> getBillingServices(@GraphQLArgument(name = "caseId") UUID caseId,
	                                                                   @GraphQLArgument(name = "filter") String filter,
	                                                                   @GraphQLArgument(name = "tag") String tag, // comma separated
	                                                                   @GraphQLArgument(name = "page") Integer page,
	                                                                   @GraphQLArgument(name = "size") Integer size

	) {
		def username = SecurityUtils.currentLogin()
		def user = userRepository.findOneByLogin(username)
		def emp = employeeRepository.findOneByUser(user)
		def department = emp.departmentOfDuty
		PriceTierDetail tier = null
		//  caseId is reused as ids for Billing and Case
		// priority is billing
		def billing = billingService.findOne(caseId)
		if (!billing) {
			//log.warn("Billing is not Detected Trying Through Case")
			// this is not a billing object but as a case.... Prioritizing active Billing object which will rarely happened
			// This is to accomodate Ancillary pricing detection
			Case patientCase = caseRepository.findById(caseId).get()
			billing = billingService.activeBilling(patientCase)
		}

		if (billing?.pricetiermanual) {
			//log.warn("This is override Price Tier")
			tier = priceTierDetailRepository.findById(billing.pricetiermanual).get()
		}

		if (!tier) {
			//log.warn("Price Tier based from Case")
			if (billing)
				tier = priceTierDetailDao.getDetail(billing.patientCase.id)
		}

		if (!billing)
			log.warn("Billing not found")

		/*def services = serviceRepository.getServicesForTaggedServices(filter,
				tag,
				new PageRequest(page, size, Sort.Direction.ASC, "serviceName")
		)*/

        if(billingService.isCreditLimitReached(billing))
        {
            def services = serviceRepository.getServicesForTaggedServicesDeptCreditLimit(filter,
                    tag,
                    department,
                    new PageRequest(page, size, Sort.Direction.ASC, "serviceName")
            )
            services.each {
                if (tier) {
                    it.calculatedAmount = priceTierDetailDao.getServicePrice(tier.id, it.id)
                } else {
                    //log.warn("Error Tier not detected")
                }
            }

            services
        }
        else
        {
            def services = serviceRepository.getServicesForTaggedServicesDept(filter,
                    tag,
                    department,
                    new PageRequest(page, size, Sort.Direction.ASC, "serviceName")
            )
            services.each {
                if (tier) {
                    it.calculatedAmount = priceTierDetailDao.getServicePrice(tier.id, it.id)
                } else {
                    //log.warn("Error Tier not detected")
                }
            }

            services
        }

	}

	// ====================== Mutations ========================

	@GraphQLMutation
	def cancelPackage(
			@GraphQLArgument(name = "billingId") UUID billingId,
			@GraphQLArgument(name = "packageId") UUID packageId) {

		def billing = findOne(billingId)

		billing.billingItemList.findAll {
			it.status == BillingItemStatus.ACTIVE && it.apackage && it.apackage.id == packageId
		}.each {
			it.status = BillingItemStatus.CANCELED
			billingItemServices.save(it)
		}

		true
	}

	@GraphQLMutation
	Billing editBilling(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		def result = upsertFromMap(id, fields)

		if (fields.containsKey("locked")) {

			Boolean locked = Boolean.valueOf(fields.get("locked") as String)
			if (!locked) {

				// cancel all deductions
				result.billingItemList.findAll {
					it.status == BillingItemStatus.ACTIVE && !it.apackage &&
							(it.itemType == BillingItemType.DEDUCTIONSPF
									|| it.itemType == BillingItemType.DEDUCTIONS)
				}.each {

					it.status = BillingItemStatus.CANCELED
					it.tempCanceled = true
					billingItemServices.save(it)
				}

				// reset PF Payments

				result.billingItemList.findAll {
					it.status == BillingItemStatus.ACTIVE &&
							it.itemType == BillingItemType.PF
				}.each {
					pf ->

						String pfffn = pf.details[BillingItemDetailParam.PF_NET.name()]
						String pfffw = pf.details[BillingItemDetailParam.PF_WTX_AMT.name()]
						def pfNet = pfffn?new BigDecimal(pfffn):BigDecimal.ZERO
						def pfWtxAmt = pfffw?new BigDecimal(pfffw):BigDecimal.ZERO

						def pfWtx = pfNet + pfWtxAmt

						if (pf.details.containsKey("PF_VAT_RATE")) { // always true
							def pfVatRate = new BigDecimal(pf.details["PF_VAT_RATE"])

							if (pfVatRate > 0) {

								// check if PF_VAT_APPLIED
								def PF_VAT_APPLIED = pf.details[BillingItemDetailParam.PF_VAT_APPLIED.name()]

								if(StringUtils.equalsIgnoreCase(PF_VAT_APPLIED,"YES")){
									def nextValue = PercentageUtils.increasePercentageValue(pfWtx, pfVatRate, true).setScale(4, RoundingMode.HALF_EVEN)
									def addendum = (nextValue - pfWtx).setScale(2, RoundingMode.HALF_EVEN)

									pf.debit = nextValue
									pf.details[BillingItemDetailParam.PF_VAT_AMT.name()] = addendum.toPlainString()

								}
								else {

									def nextValue = PercentageUtils.increasePercentageValue(pfWtx, pfVatRate, true).setScale(4, RoundingMode.HALF_EVEN)
									def addendum = (nextValue - pfWtx).setScale(2, RoundingMode.HALF_EVEN)

									pf.debit = pfWtx // stay the same
									pf.details[BillingItemDetailParam.PF_VAT_AMT.name()] = addendum.toPlainString()

								}


								billingItemServices.save(pf)
							} else {
								pf.debit = pfWtx

								billingItemServices.save(pf)
							}

						}

				}

			}
		}

		result
	}

	@GraphQLMutation(name = "changeBillingStatus")
	GraphQLRetVal<List<Billing>> changeBillingStatus(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		GraphQLRetVal<List<Billing>> graphQLRetVal = new GraphQLRetVal<List<Billing>>()
		graphQLRetVal.message = ""
		graphQLRetVal.success = false

		Billing billing1 = findOne(id)
		List<Billing> activeStatus = getActiveBillingByCase(billing1.patientCase)
		if ((!activeStatus) && (fields.containsKey("status"))) {
			entityObjectMapperService.updateFromMap(billing1, fields)
			save(billing1)
			graphQLRetVal.message = "Successfully reopen"
			graphQLRetVal.success = true
		}
		else {
			graphQLRetVal.payload = activeStatus
		}

		return graphQLRetVal
	}

	@GraphQLQuery(name = "getAllPriceTier")
	List<PriceTierDetail> getAllPriceTier() {
		priceTierDetailRepository.findAll().toSorted {
			it.tierCode
		}
	}

	@GraphQLQuery(name = "getAllPriceTierForOTC")
	List<PriceTierDetail> getAllPriceTierForOTC() {
		priceTierDetailRepository.findAll().findAll{
			it.octUse
		}.toSorted {
			it.tierCode
		}
	}

	@GraphQLMutation
	Billing createOtcBilling(
			@GraphQLArgument(name = "patientName") String patientName,
			@GraphQLArgument(name = "priceTierId") UUID priceTierId) {

		def newBilling = new Billing()
		newBilling.pricetiermanual = priceTierId
		newBilling.otcname = StringUtils.upperCase(patientName)
		newBilling.entryDateTime = Instant.now()
		newBilling.status = BillingItemStatus.ACTIVE
		newBilling.billingNo = generatorService.getNextValue(GeneratorType.BILLING_NO) { Long no ->
			StringUtils.leftPad(no.toString(), 5, "0")
		}

		save(newBilling)

	}

	@GraphQLMutation
	Billing createBilling(
			@GraphQLArgument(name = "patientId") UUID patientId,
			@GraphQLArgument(name = "caseId") UUID caseId) {

		def newBilling = new Billing()
		def patientDto = patientRepository.findById(patientId).get()
		newBilling.patient = patientDto
		newBilling.patientCase = caseRepository.findById(caseId).get()
		newBilling.entryDateTime = Instant.now()
		newBilling.status = BillingItemStatus.ACTIVE
		newBilling.billingNo = generatorService.getNextValue(GeneratorType.BILLING_NO) { Long no ->
			StringUtils.leftPad(no.toString(), 5, "0")
		}

		save(newBilling)

	}

	@GraphQLQuery(name = "isCreditLimitReached", description = "Detect if Credit limit is reached")
	Boolean isCreditLimitReached(@GraphQLContext Billing billing) {
		def defaultCreditLimit = hospitalConfigService.getOperationalConfig()?.defaultCreditLimit ?: 0.0
		return ((billing?.balance ?: BigDecimal.ZERO).setScale(2, RoundingMode.HALF_EVEN)) > ((billing?.patientCase?.creditLimit ?: defaultCreditLimit).setScale(2, RoundingMode.HALF_EVEN))
	}

	@GraphQLQuery(name = "isAllowedProgressPayment")
	Boolean isAllowedProgressPayment(@GraphQLContext Billing billing) {

		String registryType = billing?.patientCase?.registryType

		if(registryType == "IPD"){

			if(billing.finalizedSoa)
			{
				return BooleanUtils.isTrue(billing.overrideProgressPayment)
			}

			return  true
		}


		return  BooleanUtils.isTrue(billing.overrideProgressPayment)


	}


	@GraphQLQuery(name = "creditLimit", description = "Detect if Credit limit is reached")
	BigDecimal creditLimit(@GraphQLContext Billing billing) {
		def defaultCreditLimit = hospitalConfigService.getOperationalConfig()?.defaultCreditLimit ?: 0.0
		billing?.patientCase?.creditLimit ?: defaultCreditLimit
	}

	@GraphQLQuery(name = "getBilling")
	Billing getBilling(@GraphQLArgument(name = "billingId") UUID billingId) {
		findOne(billingId)
	}


	@GraphQLQuery(name = "activeBilling", description = "Get all First Active billing record")
	Billing activeBilling(@GraphQLContext Case aCase) {
		getActiveBillingByCase(aCase).find()
	}

	@GraphQLQuery(name = "activeBillingList", description = "Get all All Active billing record and still open")
	List<Billing> activeBillingList(@GraphQLArgument(name = "caseId") UUID caseId) {

		def patientCase = caseRepository.findById(caseId).get()
		getActiveOpenBillingByCase(patientCase)
	}


	List<Billing> getActiveBillingByCase(Case patientCase) {

		createQuery(" select b from Billing b where b.patientCase = :patientCase and b.status = 'ACTIVE' order by b.billingNo desc",
				[patientCase: patientCase]).resultList

	}

	List<Billing> getActiveOpenBillingByCase(Case patientCase) {

		createQuery(" select b from Billing b where b.patientCase = :patientCase and b.status = 'ACTIVE' and (b.locked is null or b.locked = false) order by b.billingNo desc",
				[patientCase: patientCase]).resultList

	}

	Billing findByFolioNo(String folioNo) {
		createQuery("select b from Billing b where b.billingNo = :billingNo", [billingNo: folioNo])
				.resultList.
				find()

	}


	List<Billing> findByPatientCase(UUID caseId) {

		def patientCase = caseRepository.findById(caseId).get()

		createQuery("select b from Billing b where b.patientCase = :patientCase", [patientCase: patientCase]).resultList
	}

	// dont worry about this ... this is prevented in the Billing module frontend
	@GraphQLMutation
	Boolean addPackage(
			@GraphQLArgument(name = "billingId") UUID billingId,
			@GraphQLArgument(name = "packageId") UUID packageId,
			@GraphQLArgument(name = "requestingPhysician") UUID requestingPhysician

	) {

		def billing = findOne(billingId)

		def pkg = packageServices.findOne(packageId)

		if (billing.patientCase) {

			def username = SecurityUtils.currentLogin()
			def user = userRepository.findOneByLogin(username)
			def emp = employeeRepository.findOneByUser(user)
			def department = emp.departmentOfDuty

			def doctor = employeeRepository.findById(requestingPhysician).get()

			def requested = [] as List<com.hisd3.hismk2.domain.ancillary.Service>
			//======== For Services ============
			pkg.items.findAll {
				it.service
			}.each {

				requested.add(it.service)

			}

			if (requested) {
				def items = orderslipService.addOrderslipFromPackage(billing.patientCase,
						department,
						requested,
						doctor
				)

				List<UUID> orderSlipItems = []

				items.each {
					orderSlipItems.add(it.id)
				}

				billingItemServices.addBillingItem(
						billing.patientCase.id,
						orderSlipItems,
						pkg
				)
			}

			//======== For Medicines ============

			/*
			 def quantity = it.getOrDefault("quantity", 0) as Integer
                def itemId = it.getOrDefault("itemId", 0) as String
                def targetDepartment = it.getOrDefault("targetDepartment", "") as String
			 */

			def medicines = [] as List<Item>
			//======== For Services ============
			pkg.items.findAll {
				it.item && it.item.isMedicine
			}.each {
				medicines.add(it.item)
			}

			if (medicines) {

				List<Map<String, Object>> forAddition = []
				medicines.each {
					med ->

						forAddition.add(
								[
										quantity        : 1,
										itemId          : med.id.toString(),
										targetDepartment: department.id.toString()
								]
						)
				}

				billingItemServices.addBillingItem(billing.id,
						BillingItemType.MEDICINES,
						forAddition,
						pkg
				)

			}

			def supplies = [] as List<Item>
			//======== For Services ============
			pkg.items.findAll {
				it.item && !it.item.isMedicine
			}.each {
				supplies.add(it.item)
			}

			if (supplies) {

				List<Map<String, Object>> forAddition = []
				supplies.each {
					supply ->

						forAddition.add(
								[
										quantity        : 1,
										itemId          : supply.id.toString(),
										targetDepartment: department.id.toString()
								]
						)
				}

				billingItemServices.addBillingItem(billing.id,
						BillingItemType.SUPPLIES,
						forAddition,
						pkg
				)
			}

			// Now Compute Deduction

			def totalDeduction = 0.0
			def packagePrice = pkg.packagePrice
			def totalBill = 0.0
			def balance = 0.0

			def biItems = billingItemServices.getPackageBillingItem(billing, pkg)

			biItems.each {
				totalBill += it.subTotal
			}

			if (totalBill > packagePrice && pkg.discountTarget) {
				// process deductions
				// deductions wont be processed when totalBill is less than the Package Price aron dili gansi ang hospital

				def differenceForDiscount = totalBill - packagePrice

				totalDeduction = processFixedDiscountDeductHCI(billing, pkg.discountTarget, biItems.collect { it.id },
						"inclusions", differenceForDiscount, "${pkg.code} ${pkg.description}", pkg)

				balance = totalBill - totalDeduction
			}

			if (balance > 0.0 && pkg.companyAccountSubsidy) {

				processEntityDeductHCI(
						billing,
						pkg.companyAccountSubsidy,
						biItems.collect { it.id },
						"inclusions",
						balance,
						"${pkg.code} ${pkg.description}",
						pkg

				)

			}

		}

		true
	}

	BillingItem addPayment(UUID billingId, BigDecimal amount, UUID employeeId, PaymentTracker paymentTracker, String customDescription = null,boolean application =false,boolean  progressPayment= false, Instant overrideDateTime=null) {

		def billing = findOne(billingId)

		def username = SecurityUtils.currentLogin()
		def user = userRepository.findOneByLogin(username)
		def emp = employeeRepository.findOneByUser(user)
		def department = emp.departmentOfDuty

		def billingItem = new BillingItem()

		if(overrideDateTime)
			billingItem.transactionDate = overrideDateTime

		billingItem.debit = 0
		billingItem.credit = 0
		billingItem.qty = 1
		billingItem.forPosting = true
		billingItem.billing = billing
		billingItem.department = department
		billingItem.itemType = BillingItemType.PAYMENTS

		billingItem.recordNo = generatorService.getNextValue(GeneratorType.BILLING_RECORD_NO) { Long next ->
			StringUtils.leftPad(next.toString(), 5, '0')
		}
		billingItem.status = BillingItemStatus.ACTIVE

		if (amount > 0)
			billingItem.credit = amount
		else
			billingItem.debit = amount * -1

		if (employeeId)
			billingItem.amountdetails = [(employeeId.toString()): amount] as Map<String, BigDecimal>

		if (customDescription) {
			billingItem.description = customDescription
		} else {

			if (paymentTracker) {
				if (employeeId) {
					def doctor = employeeRepository.findById(employeeId).get()
					billingItem.description = "PAYMENT #${paymentTracker.ornumber} [${doctor.fullName}]"
				} else {
					billingItem.description = "PAYMENT #${paymentTracker.ornumber} [HCI]"

				}
			} else {

				if (employeeId) {
					def doctor = employeeRepository.findById(employeeId).get()
					billingItem.description = "PF REAPPLICATION : [${doctor.fullName}]"
				} else {
					billingItem.description = "HCI REAPPLICATION"
				}

				if(customDescription){
					billingItem.description = customDescription ?: "Specify a Description"
				}

			}

		}


		if(progressPayment)
		{
			billingItem.description = "PROGRESS " + billingItem.description
			billingItem.isProgress = true
		}

		// compute Per Item Charges
		// Will be reapplied later on

		if(!progressPayment){
			Map<BillingItem,BigDecimal> hciItems = [:]
			billing.billingItemList.findAll {
				it.status == BillingItemStatus.ACTIVE  && it.itemType in [BillingItemType.ROOMBOARD,
																		  BillingItemType.MEDICINES,
																		  BillingItemType.DIAGNOSTICS,
																		  BillingItemType.ORFEE,
																		  BillingItemType.SUPPLIES,
																		  BillingItemType.OTHERS]
			}.each {
				hciItems.put(it,it.subTotal)
			}


			def amountToBeProcessedHCI = PercentageUtils.prorateFromValues(
					amount,
					hciItems,
					true,
					true // only recognized whats need to be recognized

			)
			amountToBeProcessedHCI.each {
				k,v ->
					billingItem.amountdetails[k.id.toString()]= v
			}
		}

		billingItem.details = [:]
		if (paymentTracker) {
			if(application){
				billingItem.details[BillingItemDetailParam.APPLICATION.name()] = "YES"
			}
			billingItem.details[BillingItemDetailParam.PAYTRACKER_ID.name()] = paymentTracker.id.toString()
			billingItem.details[BillingItemDetailParam.ORNUMBER.name()] = paymentTracker.ornumber
		} else {
			billingItem.details[BillingItemDetailParam.REAPPLICATION.name()] = "YES"
		}
		if (employeeId)
			billingItem.details[BillingItemDetailParam.PF_EMPLOYEEID.name()] = employeeId.toString()
		billingItemServices.save(billingItem)
	}

	@GraphQLMutation
	Boolean addEntityDeduction(
			@GraphQLArgument(name = "billingId") UUID billingId,
			@GraphQLArgument(name = "remarks") String remarks,
			@GraphQLArgument(name = "payload") Map<String, Object> payload,
			@GraphQLArgument(name = "approvalCode") String approvalCode = ''
	) {

		remarks = StringUtils.upperCase(remarks)
		approvalCode = StringUtils.upperCase(approvalCode)
		/*

		entity = compayAccountId
		customCategory =map
					HCI
					[EmployeeIds]
	    customCategoryNetPF
	                [EmployeeIds]
	                 when true means that deduction is applied to balance
        selectedItemsHci
                    [ array of billingitem id]
        modeHci
		 */

		def billing = findOne(billingId)
		def entity = payload["entity"] as String
		def companyAccount = companyAccountServices.findOne(UUID.fromString(entity))

		String modeHci = payload["modeHci"] as String

		def selectedItemsHci = [] as List<UUID>
		(payload["selectedItemsHci"] as List<String>).each {
			selectedItemsHci.add(UUID.fromString(it))
		}

		def customCategory = payload["customCategory"] as Map<String, BigDecimal>
		def customCategoryNetPF = payload["customCategoryNetPF"] as Map<String, Boolean>

		// process the HCI Bill

		def hciDeduction = 0.0

		if (customCategory.containsKey("HCI"))
			hciDeduction = new BigDecimal(customCategory["HCI"])

		if (hciDeduction > 0) {

			processEntityDeductHCI(
					billing,
					companyAccount,
					selectedItemsHci,
					modeHci,
					hciDeduction,
					remarks,
					null,
					approvalCode
			)

		}

		// process the PF Bill
		//inclusions

		customCategory.keySet().findAll { it != "HCI" }.each {

			def employeeid = it
			def amountPFDeduct = new BigDecimal(StringUtils.defaultString(customCategory[employeeid] as String, "0.0"))

			def doctor = employeeRepository.findById(UUID.fromString(employeeid)).get()

			if (amountPFDeduct > 0.0) {
				if (customCategoryNetPF[employeeid]) {
					// this will deduct to balance

					def username = SecurityUtils.currentLogin()
					def user = userRepository.findOneByLogin(username)
					def emp = employeeRepository.findOneByUser(user)
					def department = emp.departmentOfDuty

					def billingItem = new BillingItem()
					billingItem.debit = 0
					billingItem.credit = 0
					billingItem.qty = 1
					billingItem.forPosting = true
					billingItem.billing = billing
					billingItem.department = department
					billingItem.itemType = BillingItemType.DEDUCTIONSPF

					billingItem.recordNo = generatorService.getNextValue(GeneratorType.BILLING_RECORD_NO) { Long next ->
						StringUtils.leftPad(next.toString(), 5, '0')
					}
					billingItem.status = BillingItemStatus.ACTIVE

					billingItem.credit = amountPFDeduct
					billingItem.amountdetails = [(employeeid): amountPFDeduct] as Map<String, BigDecimal>

					billingItem.description = "${companyAccount.companyname} ${approvalCode ? "[${approvalCode}]" : ""} ${remarks ? "[${remarks}]" : ""} - ${doctor.fullName} (PF)"
					billingItem.approvalCode = approvalCode ? approvalCode : ''
					// this is not a discount type
					billingItem.details = [:]
					billingItem.details[BillingItemDetailParam.COMPANY_ACCOUNT_ID.name()] = companyAccount.id.toString()
					billingItem.details[BillingItemDetailParam.PF_EMPLOYEEID.name()] = employeeid
					billingItem.details[BillingItemDetailParam.PF_DEDUCT_MODE.name()] = "FIXEDAMOUNT"

					billingItemServices.save(billingItem)

				} else {

					// find the BillingItem for the employee

					def pf = billing.billingItemList.find {
						it.itemType == BillingItemType.PF &&
								it.details[BillingItemDetailParam.PF_EMPLOYEEID.name()] == employeeid &&
								it.status == BillingItemStatus.ACTIVE
					}

					if (pf) {
						processDeductionPF(
								billing,
								amountPFDeduct,
								[pf.id],
								"inclusions",
								false,
								false,
								companyAccount.companyname + "${approvalCode ? " [${approvalCode}]" : ""} ${remarks ? "[${remarks}]" : ""}",
								[(BillingItemDetailParam.COMPANY_ACCOUNT_ID.name()): companyAccount.id.toString()],
								false,
								[:],
								approvalCode
						)
					}
				}
			}

		}

		true
	}

	BigDecimal processFixedDiscountDeductHCI(
			Billing billing,
			Discount discount,
			List<UUID> idsHandle,
			String mode,
			BigDecimal amountDeduction,
			String remarks,
			Package aPackage = null
	) {

		def previousDeductions = billing.billingItemList.findAll {
			it.itemType == BillingItemType.DEDUCTIONS &&
					it.status == BillingItemStatus.ACTIVE
		}
		def tobeProcessed = [] as List<BillingItem>

		if (idsHandle) {

			if (mode == "exceptions") {

				billing.billingItemList.findAll {
					(
							it.itemType == BillingItemType.ROOMBOARD
									||
									it.itemType == BillingItemType.MEDICINES
									||
									it.itemType == BillingItemType.DIAGNOSTICS
									||
									it.itemType == BillingItemType.ORFEE
									||
									it.itemType == BillingItemType.SUPPLIES
									||
									it.itemType == BillingItemType.OTHERS
					) &&
							it.status == BillingItemStatus.ACTIVE && !idsHandle.contains(it.id)
				}.each {
					tobeProcessed.add(it)
				}
			}

			if (mode == "inclusions") {

				billing.billingItemList.findAll {
					(
							it.itemType == BillingItemType.ROOMBOARD
									||
									it.itemType == BillingItemType.MEDICINES
									||
									it.itemType == BillingItemType.DIAGNOSTICS
									||
									it.itemType == BillingItemType.ORFEE
									||
									it.itemType == BillingItemType.SUPPLIES
									||
									it.itemType == BillingItemType.OTHERS
					) &&
							it.status == BillingItemStatus.ACTIVE && idsHandle.contains(it.id)
				}.each {
					tobeProcessed.add(it)
				}
			}

		} else {

			billing.billingItemList.findAll {
				(
						it.itemType == BillingItemType.ROOMBOARD
								||
								it.itemType == BillingItemType.MEDICINES
								||
								it.itemType == BillingItemType.DIAGNOSTICS
								||
								it.itemType == BillingItemType.ORFEE
								||
								it.itemType == BillingItemType.SUPPLIES
								||
								it.itemType == BillingItemType.OTHERS
				) &&
						it.status == BillingItemStatus.ACTIVE
			}.each {
				tobeProcessed.add(it)
			}

		}

		def itemBalance = [:] as Map<UUID, BigDecimal>

		tobeProcessed.each {
			process ->

				def subtotal = (process.subTotal - process.vatOutputTax)

				previousDeductions.each {
					prevdeduct ->

						if (prevdeduct.amountdetails.containsKey(process.id.toString()))
							subtotal -= prevdeduct.amountdetails[process.id.toString()]
				}

				// process balance
				itemBalance.put(process.id, subtotal)

		}

		def amountToBeProcessedHCI = PercentageUtils.prorateFromValues(
				amountDeduction,
				itemBalance,
				true,
				true // only recognized whats need to be recognized

		)

		def totalDeduction = 0.0

		amountToBeProcessedHCI.each {
			t, v ->

				totalDeduction += v
		}

		if (totalDeduction > 0) {

			def username = SecurityUtils.currentLogin()
			def user = userRepository.findOneByLogin(username)
			def emp = employeeRepository.findOneByUser(user)
			def department = emp.departmentOfDuty

			def billingItem = new BillingItem()
			if (aPackage)
				billingItem.apackage = aPackage
			billingItem.debit = 0
			billingItem.credit = 0
			billingItem.qty = 1
			billingItem.forPosting = true
			billingItem.billing = billing
			billingItem.department = department
			billingItem.itemType = BillingItemType.DEDUCTIONS

			billingItem.recordNo = generatorService.getNextValue(GeneratorType.BILLING_RECORD_NO) { Long next ->
				StringUtils.leftPad(next.toString(), 5, '0')
			}
			billingItem.status = BillingItemStatus.ACTIVE

			billingItem.credit = totalDeduction
			billingItem.amountdetails = [:]

			amountToBeProcessedHCI.each {
				t, u ->
					billingItem.amountdetails.put(t.toString(), u)
			}

			billingItem.description = "${discount.discount} ${remarks ? "[${remarks}]" : ""}(HCI)"
			billingItem.details[BillingItemDetailParam.DISCOUNT_ID.name()] = discount.id.toString()
			billingItem.details[BillingItemDetailParam.DISCOUNT_TYPE.name()] = discount.type.name()
			billingItem.details[BillingItemDetailParam.DISCOUNT_MULTIPLIER.name()] = discount.type == DiscountType.FIXED ?
					discount.value.toPlainString() : "{}"

			if (discount.fromInitial)
				billingItem.details[BillingItemDetailParam.FROM_INITIAL.name()] = "1"

			billingItemServices.save(billingItem)

		}

		return totalDeduction

	}

	def processEntityDeductHCI(
			Billing billing,
			CompanyAccount companyAccount,
			List<UUID> idsHandle,
			String mode,
			BigDecimal amountDeduction,
			String remarks,
			Package aPackage = null,
			String approvalCode = ''
	) {

		/*
	 ROOMBOARD,
	MEDICINES,
	DIAGNOSTICS,
	ORFEE,
	SUPPLIES,
	OTHERS,
		 */

		def previousDeductions = billing.billingItemList.findAll {
			it.itemType == BillingItemType.DEDUCTIONS &&
					it.status == BillingItemStatus.ACTIVE
		}
		def tobeProcessed = [] as List<BillingItem>

		if (idsHandle) {

			if (mode == "exceptions") {

				billing.billingItemList.findAll {
					(
							it.itemType == BillingItemType.ROOMBOARD
									||
									it.itemType == BillingItemType.MEDICINES
									||
									it.itemType == BillingItemType.DIAGNOSTICS
									||
									it.itemType == BillingItemType.ORFEE
									||
									it.itemType == BillingItemType.SUPPLIES
									||
									it.itemType == BillingItemType.OTHERS
					) &&
							it.status == BillingItemStatus.ACTIVE && !idsHandle.contains(it.id)
				}.each {
					tobeProcessed.add(it)
				}
			}

			if (mode == "inclusions") {

				billing.billingItemList.findAll {
					(
							it.itemType == BillingItemType.ROOMBOARD
									||
									it.itemType == BillingItemType.MEDICINES
									||
									it.itemType == BillingItemType.DIAGNOSTICS
									||
									it.itemType == BillingItemType.ORFEE
									||
									it.itemType == BillingItemType.SUPPLIES
									||
									it.itemType == BillingItemType.OTHERS
					) &&
							it.status == BillingItemStatus.ACTIVE && idsHandle.contains(it.id)
				}.each {
					tobeProcessed.add(it)
				}
			}

		} else {

			billing.billingItemList.findAll {
				(
						it.itemType == BillingItemType.ROOMBOARD
								||
								it.itemType == BillingItemType.MEDICINES
								||
								it.itemType == BillingItemType.DIAGNOSTICS
								||
								it.itemType == BillingItemType.ORFEE
								||
								it.itemType == BillingItemType.SUPPLIES
								||
								it.itemType == BillingItemType.OTHERS
				) &&
						it.status == BillingItemStatus.ACTIVE
			}.each {
				tobeProcessed.add(it)
			}

		}

		def itemBalance = [:] as Map<UUID, BigDecimal>

		tobeProcessed.each {
			process ->

				def subtotal = process.subTotal

				previousDeductions.each {
					prevdeduct ->

						if (prevdeduct.amountdetails.containsKey(process.id.toString()))
							subtotal -= prevdeduct.amountdetails[process.id.toString()]
				}

				// process balance
				itemBalance.put(process.id, subtotal)

		}

		def amountToBeProcessedHCI = PercentageUtils.prorateFromValues(
				amountDeduction,
				itemBalance,
				true,
				true // only recognized whats need to be recognized

		)

		def totalDeduction = 0.0

		amountToBeProcessedHCI.each {
			t, v ->

				totalDeduction += v
		}

		if (totalDeduction > 0) {

			def username = SecurityUtils.currentLogin()
			def user = userRepository.findOneByLogin(username)
			def emp = employeeRepository.findOneByUser(user)
			def department = emp.departmentOfDuty

			def billingItem = new BillingItem()
			if (aPackage)
				billingItem.apackage = aPackage
			billingItem.debit = 0
			billingItem.credit = 0
			billingItem.qty = 1
			billingItem.forPosting = true
			billingItem.billing = billing
			billingItem.department = department
			billingItem.itemType = BillingItemType.DEDUCTIONS

			billingItem.recordNo = generatorService.getNextValue(GeneratorType.BILLING_RECORD_NO) { Long next ->
				StringUtils.leftPad(next.toString(), 5, '0')
			}
			billingItem.status = BillingItemStatus.ACTIVE

			billingItem.credit = totalDeduction
			billingItem.amountdetails = [:]

			amountToBeProcessedHCI.each {
				t, u ->
					billingItem.amountdetails.put(t.toString(), u)
			}

			billingItem.description = "${companyAccount.companyname} ${approvalCode ? "[${approvalCode}]" : ""} ${remarks ? "[${remarks}]" : ""}(HCI)"
			billingItem.approvalCode = approvalCode
			billingItem.details[BillingItemDetailParam.COMPANY_ACCOUNT_ID.name()] = companyAccount.id.toString()
			billingItemServices.save(billingItem)

		}

	}

//=====================================================================================================================

	@GraphQLMutation
	Boolean addDiscount(
			@GraphQLArgument(name = "discountId") UUID discountId,
			@GraphQLArgument(name = "billingId") UUID billingId,
			@GraphQLArgument(name = "mode") String mode,
			@GraphQLArgument(name = "payload") Map<String, Object> payload

	)

	{

		/*
		 payload
		    selectedItemsHci  []
		    selectedItemsPf []
		    modeHci  exceptions || inclusions
		    modePf  exceptions || inclusions
		    customCategory  map of BillingItemType
            mode   [percentage,fixed]
		 */

		def discount = discountsService.findOne(discountId)
		def billing = findOne(billingId)

		//println(payload.dump())

		def selectedItemsHci = [] as List<UUID>
		(payload["selectedItemsHci"] as List<String>).each {
			selectedItemsHci.add(UUID.fromString(it))
		}

		def selectedItemsPf = [] as List<UUID>
		def selectedItemsPfValueMap = [:] as Map<UUID, BigDecimal>
		(payload["selectedItemsPf"] as List<String>).each {

			String id = it
			if (StringUtils.contains(id, "|")) {
				String[] parts = StringUtils.split(id, "|")
				id = parts[0]
				BigDecimal value = new BigDecimal(parts[1])
				selectedItemsPfValueMap[UUID.fromString(id)] = value
			}

			selectedItemsPf.add(UUID.fromString(id))
		}

		String modeHci = payload["modeHci"] as String
		String modePf = payload["modePf"] as String
		def customCategory = payload["customCategory"] as Map<String, BigDecimal>

		def amountDetails = [:] as Map<String, BigDecimal>
		def vatAmountDetails = [:] as Map<String, BigDecimal>
		// Apply to HCI Transactions
		if (discount.applyhci) {

			if (discount.type == DiscountType.FIXED) {
				// Fix Discount

				/*
                                    BillingItemType.ROOMBOARD,
                                    BillingItemType.MEDICINES,
                                    BillingItemType.DIAGNOSTICS,
                                    BillingItemType.ORFEE,
                                    BillingItemType.SUPPLIES,
                                    BillingItemType.OTHERS

                 */



				processDiscountHCI(discount.value, billing, BillingItemType.ROOMBOARD,
						selectedItemsHci,
						modeHci,
						discount.fromInitial
				).each {
					key, value ->

						amountDetails.put(key.toString(), value)
				}




				processDiscountHCI(discount.value, billing, BillingItemType.MEDICINES,
						selectedItemsHci,
						modeHci,
						discount.fromInitial
				).each {
					key, value ->

						amountDetails.put(key.toString(), value)
				}

				processDiscountVAT(discount.value, billing, BillingItemType.MEDICINES,
						selectedItemsHci,
						modeHci,
						discount.fromInitial
				).each {
					key, value ->

						vatAmountDetails.put(key.toString(), value)
				}


				processDiscountHCI(discount.value, billing, BillingItemType.DIAGNOSTICS,
						selectedItemsHci,
						modeHci,
						discount.fromInitial
				).each {
					key, value ->

						amountDetails.put(key.toString(), value)
				}




				processDiscountHCI(discount.value, billing, BillingItemType.ORFEE,
						selectedItemsHci,
						modeHci,
						discount.fromInitial
				).each {
					key, value ->

						amountDetails.put(key.toString(), value)
				}

				processDiscountHCI(discount.value, billing, BillingItemType.SUPPLIES,
						selectedItemsHci,
						modeHci,
						discount.fromInitial
				).each {
					key, value ->

						amountDetails.put(key.toString(), value)
				}

				processDiscountVAT(discount.value, billing, BillingItemType.SUPPLIES,
						selectedItemsHci,
						modeHci,
						discount.fromInitial
				).each {
					key, value ->

						vatAmountDetails.put(key.toString(), value)
				}



				processDiscountHCI(discount.value, billing, BillingItemType.OTHERS,
						selectedItemsHci,
						modeHci,
						discount.fromInitial
				).each {
					key, value ->

						amountDetails.put(key.toString(), value)
				}


				processDiscountVAT(discount.value, billing, BillingItemType.OTHERS,
						selectedItemsHci,
						modeHci,
						discount.fromInitial
				).each {
					key, value ->

						vatAmountDetails.put(key.toString(), value)
				}


			} else {
				// Custom

				// consider mode   percentage  fixed
				// this is the only part where mode does matter

				customCategory.each {
					k, v ->

						if (v > 0) {

							if (BillingItemType.valueOf(k) != BillingItemType.PF) {

								processDiscountHCI(v, billing, BillingItemType.valueOf(k),
										selectedItemsHci,
										modeHci,
										discount.fromInitial,
										mode
								).each {
									key, value ->

										amountDetails.put(key.toString(), value)
								}
							}

						}
				}

			}

			def totalDeduction = BigDecimal.ZERO
			def totalDeductionVat = BigDecimal.ZERO

			amountDetails.each {
				t, v ->

					totalDeduction += v
			}

			vatAmountDetails.each {
				t, v ->

					totalDeductionVat += v
			}

			//println(totalDeduction)

			if (totalDeduction > 0) {

				def username = SecurityUtils.currentLogin()
				def user = userRepository.findOneByLogin(username)
				def emp = employeeRepository.findOneByUser(user)
				def department = emp.departmentOfDuty

				def billingItem = new BillingItem()
				billingItem.debit = 0
				billingItem.credit = 0
				billingItem.qty = 1
				billingItem.forPosting = true
				billingItem.billing = billing
				billingItem.department = department
				billingItem.itemType = BillingItemType.DEDUCTIONS

				billingItem.recordNo = generatorService.getNextValue(GeneratorType.BILLING_RECORD_NO) { Long next ->
					StringUtils.leftPad(next.toString(), 5, '0')
				}
				billingItem.status = BillingItemStatus.ACTIVE

				billingItem.credit = totalDeduction+totalDeductionVat
				billingItem.amountdetails = amountDetails
				billingItem.vatAmountdetails = vatAmountDetails

				billingItem.description = "${discount.discount} (HCI)"
				billingItem.details[BillingItemDetailParam.DISCOUNT_ID.name()] = discount.id.toString()
				billingItem.details[BillingItemDetailParam.DISCOUNT_TYPE.name()] = discount.type.name()
				billingItem.details[BillingItemDetailParam.DISCOUNT_MULTIPLIER.name()] = discount.type == DiscountType.FIXED ?
						discount.value.toPlainString() : customCategory.inspect()

				if (discount.fromInitial)
					billingItem.details[BillingItemDetailParam.FROM_INITIAL.name()] = "1"

				billingItemServices.save(billingItem)

			}

		}

		// Apply to PF
		if (discount.applypf) {

			if (discount.type == DiscountType.FIXED) {
				processDeductionPF(billing, discount.value,
						selectedItemsPf,
						modePf,
						discount.fromInitial,
						true,
						discount.discount,
						[

								(BillingItemDetailParam.DISCOUNT_ID.name())        : discount.id.toString(),
								(BillingItemDetailParam.DISCOUNT_TYPE.name())      : discount.type.name(),
								(BillingItemDetailParam.DISCOUNT_MULTIPLIER.name()): discount.type == DiscountType.FIXED ?
										discount.value.toPlainString() : customCategory.inspect()
						],
						discount.vat
				)

			} else {

				// its custom...
				// However it is ignored then theres no value specified

				if (mode == "percentage") {

					// This is now ignored .. No Custom that is percentage
					// all their amounts depend on selectedItemsPfValueMap so  discount.value is not used
					/*processDeductionPF(billing,discount.value,
							selectedItemsPfValueMap.collect { it.key},
							modePf,
							discount.fromInitial,
							true,
							discount.discount,
							[

									(BillingItemDetailParam.DISCOUNT_ID.name()):discount.id.toString(),
									(BillingItemDetailParam.DISCOUNT_TYPE.name()):discount.type.name(),
									(BillingItemDetailParam.DISCOUNT_MULTIPLIER.name()):discount.type == DiscountType.FIXED ?
											discount.value.toPlainString(): customCategory.inspect()
							],
							discount.vat,
							selectedItemsPfValueMap
					)*/
				} else {
					// fixed
					// all their amounts depend on selectedItemsPfValueMap so  discount.value is not used
					processDeductionPF(billing, discount.value,
							selectedItemsPf,
							modePf,
							discount.fromInitial,
							false,
							discount.discount,
							[

									(BillingItemDetailParam.DISCOUNT_ID.name())        : discount.id.toString(),
									(BillingItemDetailParam.DISCOUNT_TYPE.name())      : discount.type.name(),
									(BillingItemDetailParam.DISCOUNT_MULTIPLIER.name()): discount.type == DiscountType.FIXED ?
											discount.value.toPlainString() : customCategory.inspect()
							],
							discount.vat,
							selectedItemsPfValueMap
					)

				}

				/*if(customCategory.containsKey("PF")){

					def value = customCategory["PF"]
					if(value > 0){

						processDeductionPF(billing,value,
								selectedItemsPf as List,
								modePf,
								discount.fromInitial,
								true,
								discount.discount,
								[

										(BillingItemDetailParam.DISCOUNT_ID.name()):discount.id.toString(),
										(BillingItemDetailParam.DISCOUNT_TYPE.name()):discount.type.name(),
										(BillingItemDetailParam.DISCOUNT_MULTIPLIER.name()):discount.type == DiscountType.FIXED ?
												discount.value.toPlainString(): customCategory.inspect()
								],
								discount.vat
						)

					}

				}*/
			}

		}

		true

	}

	// ================================= Utilities ======================

	def processDeductionPF(Billing billing,
	                       BigDecimal multiplierOrAmount,
	                       List<UUID> idsHandle,
	                       String mode,
	                       Boolean fromInitial,
	                       Boolean multiplier,
	                       String description,
	                       Map<String, String> details,
	                       Boolean isDeductVat,
	                       Map<UUID, BigDecimal> selectedItemsPfValueMap = [:],
						   String approvalCode = ''
	) {

		def tobeProcessed = [] as List<BillingItem>

		if (idsHandle) {

			if (mode == "exceptions") {

				billing.billingItemList.findAll {
					it.itemType == BillingItemType.PF &&
							it.status == BillingItemStatus.ACTIVE && !idsHandle.contains(it.id)
				}.each {
					tobeProcessed.add(it)
				}
			}

			if (mode == "inclusions") {

				billing.billingItemList.findAll {
					it.itemType == BillingItemType.PF &&
							it.status == BillingItemStatus.ACTIVE && idsHandle.contains(it.id)
				}.each {
					tobeProcessed.add(it)
				}
			}

		} else {

			billing.billingItemList.findAll {
				it.itemType == BillingItemType.PF &&
						it.status == BillingItemStatus.ACTIVE
			}.each {
				tobeProcessed.add(it)
			}

		}

		def username = SecurityUtils.currentLogin()
		def user = userRepository.findOneByLogin(username)
		def emp = employeeRepository.findOneByUser(user)
		def department = emp.departmentOfDuty

		tobeProcessed.each {
			pf ->
				boolean hasProcesscedVAT = isDeductVat

				def employeeid = pf.details[BillingItemDetailParam.PF_EMPLOYEEID.name()]



				def doctor = employeeRepository.findById(UUID.fromString(employeeid)).get()

				/*
		PF_NET
		PF_WTX_RATE
		PF_WTX_AMT,
		PF_VAT_RATE,
		PF_VAT_AMT,
		PF_EMPLOYEEID
		 */

				boolean proceed = true

				/*if (isDeductVat) {
					def vatRate = new BigDecimal(pf.details["PF_VAT_RATE"])
					if (vatRate <= 0)
						proceed = false

				}*/

				if (proceed) {

					def pfNet = new BigDecimal(pf.details[BillingItemDetailParam.PF_NET.name()])
					def pfWtxAmt = new BigDecimal(pf.details[BillingItemDetailParam.PF_WTX_AMT.name()])

					// tracked
					def pfWtx = (pfNet?:0.0) + (pfWtxAmt?:0.0)
					def billingItem = new BillingItem()
					if (!multiplier) {
						// Fix Amount

						billingItem.debit = 0
						billingItem.credit = 0
						billingItem.qty = 1
						billingItem.forPosting = true
						billingItem.billing = billing
						billingItem.department = department
						billingItem.itemType = BillingItemType.DEDUCTIONSPF

						billingItem.recordNo = generatorService.getNextValue(GeneratorType.BILLING_RECORD_NO) { Long next ->
							StringUtils.leftPad(next.toString(), 5, '0')
						}
						billingItem.status = BillingItemStatus.ACTIVE

						BigDecimal valueToUse = multiplierOrAmount?:0.0
						if (selectedItemsPfValueMap.containsKey(pf.id)) {
							valueToUse = selectedItemsPfValueMap.get(pf.id, BigDecimal.ZERO)
						}

						billingItem.credit = valueToUse

						pfWtx += valueToUse

						billingItem.amountdetails = [(pf.id.toString()): valueToUse] as Map<String, BigDecimal>

						billingItem.description = "${description} - ${doctor.fullName} (PF)"

						// this is not a discount type
						billingItem.details = [:]

						details.each {
							k, v ->
								billingItem.details.put(k, v)
						}
						if (fromInitial)
							billingItem.details[BillingItemDetailParam.FROM_INITIAL.name()] = "1"

						billingItem.details[BillingItemDetailParam.PF_EMPLOYEEID.name()] = employeeid.toString()
						billingItem.details[BillingItemDetailParam.PF_DEDUCT_MODE.name()] = "FIXEDAMOUNT"

						if (isDeductVat)
							billingItem.details[BillingItemDetailParam.PF_DEDUCT_IS_VAT.name()] = "1"

					} else {

						BigDecimal valueToUse = multiplierOrAmount
						if (selectedItemsPfValueMap.containsKey(pf.id)) {
							//valueToUse = selectedItemsPfValueMap.get(pf.id,BigDecimal.ZERO)

							// Percentage is not allowed in Discount Custom Mode.
						} else {

							def nextValue = PercentageUtils.increasePercentageValue(pfWtx, valueToUse, fromInitial).setScale(4, RoundingMode.HALF_EVEN)
							def addendum = (nextValue - pfWtx).setScale(2, RoundingMode.HALF_EVEN)

							billingItem.debit = 0
							billingItem.credit = 0
							billingItem.qty = 1
							billingItem.forPosting = true
							billingItem.billing = billing
							billingItem.department = department
							billingItem.itemType = BillingItemType.DEDUCTIONSPF

							billingItem.recordNo = generatorService.getNextValue(GeneratorType.BILLING_RECORD_NO) { Long next ->
								StringUtils.leftPad(next.toString(), 5, '0')
							}
							billingItem.status = BillingItemStatus.ACTIVE

							billingItem.credit = addendum

							pfWtx = nextValue

							billingItem.amountdetails = [(pf.id.toString()): valueToUse] as Map<String, BigDecimal>

							billingItem.description = "${description} - ${doctor.fullName} (PF)"

							billingItem.details = [:]

							details.each {
								k, v ->
									billingItem.details.put(k, v)
							}
							if (fromInitial)
								billingItem.details[BillingItemDetailParam.FROM_INITIAL.name()] = "1"

							billingItem.details[BillingItemDetailParam.PF_EMPLOYEEID.name()] = employeeid.toString()
							billingItem.details[BillingItemDetailParam.PF_DEDUCT_MODE.name()] = "PERCENTAGE"

							if (isDeductVat)
								billingItem.details[BillingItemDetailParam.PF_DEDUCT_IS_VAT.name()] = "1"
						}

					}

					// Custom Discount will not go on top for PF

					if (!selectedItemsPfValueMap.containsKey(pf.id)) {

						// Now modify the previous deductions

						def sorted = billing.billingItemList.findAll {
							it.itemType == BillingItemType.DEDUCTIONSPF && it.status == BillingItemStatus.ACTIVE &&
									StringUtils.equalsIgnoreCase(it.details[BillingItemDetailParam.PF_EMPLOYEEID.name()], employeeid)
						}.sort(false) {
							a, b ->

								b.createdDate <=> a.createdDate
						}
						sorted.each {
							prevPfDed ->

								if (prevPfDed.details.containsKey(BillingItemDetailParam.PF_DEDUCT_IS_VAT.name()))
									hasProcesscedVAT = true

								if (prevPfDed.details[BillingItemDetailParam.PF_DEDUCT_MODE.name()] == "FIXEDAMOUNT") {
									// ignore coz this is not a discount type
									pfWtx += prevPfDed.credit
								} else {

									boolean oldFromInitialValue = prevPfDed.details.containsKey(BillingItemDetailParam.FROM_INITIAL.name())
									BigDecimal multiplierValue = 0

									String discountType = prevPfDed.details["DISCOUNT_TYPE"]

									if (discountType == "FIXED") {
										multiplierValue = new BigDecimal(prevPfDed.details["DISCOUNT_MULTIPLIER"])
									} else {
										def customConfig = new JsonSlurper().parseText(prevPfDed.details["DISCOUNT_MULTIPLIER"]) as Map<String, Object>

										if (customConfig.containsKey("PF")) {

											multiplierValue = new BigDecimal(customConfig["PF"])
										}

									}

									if (multiplierValue > 0) {

										def nextValue = PercentageUtils.increasePercentageValue(pfWtx, multiplierValue, oldFromInitialValue).setScale(4, RoundingMode.HALF_EVEN)
										def addendum = (nextValue - pfWtx).setScale(2, RoundingMode.HALF_EVEN)

										prevPfDed.credit = addendum

										prevPfDed.amountdetails[pf.id.toString()] = addendum
										prevPfDed.approvalCode = approvalCode ? approvalCode : ''
										billingItemServices.save(prevPfDed)
										pfWtx = nextValue

									}

								}

						}

						//	println(billingItem.dump())
						billingItem.approvalCode = approvalCode ? approvalCode : ''
						billingItemServices.save(billingItem)

						//if (!hasProcesscedVAT) {

							if (pf.details.containsKey("PF_VAT_RATE")) { // always true
								def pfVatRate = new BigDecimal(pf.details["PF_VAT_RATE"])

								if (pfVatRate > 0) {

									// check if PF_VAT_APPLIED
								       def PF_VAT_APPLIED = pf.details[BillingItemDetailParam.PF_VAT_APPLIED.name()]

									if(StringUtils.equalsIgnoreCase(PF_VAT_APPLIED,"YES")){

										def nextValue = PercentageUtils.increasePercentageValue(pfWtx, pfVatRate, true).setScale(4, RoundingMode.HALF_EVEN)
										def addendum = (nextValue - pfWtx).setScale(2, RoundingMode.HALF_EVEN)

										pf.debit = nextValue
										pf.details[BillingItemDetailParam.PF_VAT_AMT.name()] = addendum.toPlainString()


									}
									else {
										def nextValue = PercentageUtils.increasePercentageValue(pfWtx, pfVatRate, true).setScale(4, RoundingMode.HALF_EVEN)
										def addendum = (nextValue - pfWtx).setScale(2, RoundingMode.HALF_EVEN)

										pf.debit = pfWtx // stay the same
										pf.details[BillingItemDetailParam.PF_VAT_AMT.name()] = addendum.toPlainString()
									}
									pf.approvalCode = approvalCode ? approvalCode : ''
									billingItemServices.save(pf)
								} else {
									pf.debit = pfWtx
									pf.approvalCode = approvalCode ? approvalCode : ''
									billingItemServices.save(pf)
								}

							}

						/*} else {

							// update pf sa new gross

							pf.debit = pfWtx
							billingItemServices.save(pf)

						}
						*/
					} else {

						// just save custom fixed pf discount
						billingItem.approvalCode = approvalCode ? approvalCode : ''
						billingItemServices.save(billingItem)
					}

				}

		}

		true
	}

/*	Map<UUID, BigDecimal> processDiscountHCI(BigDecimal multiplier,
	                                         Billing billing,
	                                         List<UUID> idsHandle,
	                                         String mode,
	                                         Boolean fromInitial
	) {
		def results = [:] as Map<UUID, BigDecimal>

		def previousDeductions = billing.billingItemList.findAll {
			it.itemType == BillingItemType.DEDUCTIONS &&
					it.status == BillingItemStatus.ACTIVE
		}
		def tobeProcessed = [] as List<BillingItem>

		if (idsHandle) {

			if (mode == "exceptions") {

				billing.billingItemList.findAll {
					it.itemType == billingItemType &
							it.status == BillingItemStatus.ACTIVE && !idsHandle.contains(it.id)
				}.each {
					tobeProcessed.add(it)
				}
			}

			if (mode == "inclusions") {

				billing.billingItemList.findAll {
					it.status == BillingItemStatus.ACTIVE && idsHandle.contains(it.id)
				}.each {
					tobeProcessed.add(it)
				}
			}

		} else {

			billing.billingItemList.findAll {
				i
				it.status == BillingItemStatus.ACTIVE
			}.each {
				tobeProcessed.add(it)
			}

		}

		tobeProcessed.each {
			process ->

				def subtotal = process.subTotal

				previousDeductions.each {
					prevdeduct ->

						if (prevdeduct.amountdetails.containsKey(process.id.toString()))
							subtotal -= prevdeduct.amountdetails[process.id.toString()]
				}

				// process the balance

				if (subtotal > 0) {
					def ded = PercentageUtils.deductionPercentageValue(subtotal, multiplier, fromInitial).setScale(4, RoundingMode.HALF_EVEN)
//(subtotal * multiplier).setScale(4, RoundingMode.HALF_EVEN)
					results[process.id] = ded
				}
		}

		results
	}*/

	Map<UUID, BigDecimal> processDiscountVAT(BigDecimal multiplier,
											 Billing billing,
											 BillingItemType billingItemType,
											 List<UUID> idsHandle,
											 String mode,
											 Boolean fromInitial,
											 String customMode = "percentage"
	) {

		def results = [:] as Map<UUID, BigDecimal>

		def previousDeductions = billing.billingItemList.findAll {
			it.itemType == BillingItemType.DEDUCTIONS &&
					it.status == BillingItemStatus.ACTIVE
		}
		def tobeProcessed = [] as List<BillingItem>

		if (idsHandle) {

			if (mode == "exceptions") {

				billing.billingItemList.findAll {
					it.itemType == billingItemType &&
							it.status == BillingItemStatus.ACTIVE && !idsHandle.contains(it.id)
				}.each {
					tobeProcessed.add(it)
				}
			}

			if (mode == "inclusions") {

				billing.billingItemList.findAll {
					it.itemType == billingItemType &&
							it.status == BillingItemStatus.ACTIVE && idsHandle.contains(it.id)
				}.each {
					tobeProcessed.add(it)
				}
			}

		} else {

			billing.billingItemList.findAll {
				it.itemType == billingItemType &&
						it.status == BillingItemStatus.ACTIVE
			}.each {
				tobeProcessed.add(it)
			}

		}

		if (customMode == "percentage") {

			// multiplier is percentage
			tobeProcessed.each {
				process ->

					def subtotal = (process.vatOutputTax)

					previousDeductions.each {
						prevdeduct ->

							if (prevdeduct.vatAmountdetails.containsKey(process.id.toString()))
								subtotal -= prevdeduct.vatAmountdetails[process.id.toString()]
					}

					// process the balance


					def ded = PercentageUtils.deductionPercentageValue(subtotal, multiplier, fromInitial).setScale(4, RoundingMode.HALF_EVEN)//(subtotal * multiplier).setScale(4, RoundingMode.HALF_EVEN)
					results[process.id] = ded

			}
		} else {

			// multiplier is amount

			def itemBalance = [:] as Map<UUID, BigDecimal>

			tobeProcessed.each {
				process ->

					def subtotal = ( process.vatOutputTax)

					previousDeductions.each {
						prevdeduct ->

							if (prevdeduct.vatAmountdetails.containsKey(process.id.toString()))
								subtotal -= prevdeduct.vatAmountdetails[process.id.toString()]
					}

					// process the balance
					itemBalance.put(process.id, subtotal)

			}

			def amountToBeProcessedHCI = PercentageUtils.prorateFromValues(
					multiplier,
					itemBalance,
					true,
					false // only recognized whats need to be recognized

			)

			amountToBeProcessedHCI.each {
				k, v ->
					results[k] = v
			}

		}

		results

	}


	Map<UUID, BigDecimal> processDiscountHCI(BigDecimal multiplier,
	                                         Billing billing,
	                                         BillingItemType billingItemType,
	                                         List<UUID> idsHandle,
	                                         String mode,
	                                         Boolean fromInitial,
	                                         String customMode = "percentage"
	) {

		def results = [:] as Map<UUID, BigDecimal>

		def previousDeductions = billing.billingItemList.findAll {
			it.itemType == BillingItemType.DEDUCTIONS &&
					it.status == BillingItemStatus.ACTIVE
		}
		def tobeProcessed = [] as List<BillingItem>

		if (idsHandle) {

			if (mode == "exceptions") {

				billing.billingItemList.findAll {
					it.itemType == billingItemType &&
							it.status == BillingItemStatus.ACTIVE && !idsHandle.contains(it.id)
				}.each {
					tobeProcessed.add(it)
				}
			}

			if (mode == "inclusions") {

				billing.billingItemList.findAll {
					it.itemType == billingItemType &&
							it.status == BillingItemStatus.ACTIVE && idsHandle.contains(it.id)
				}.each {
					tobeProcessed.add(it)
				}
			}

		} else {

			billing.billingItemList.findAll {
				it.itemType == billingItemType &&
						it.status == BillingItemStatus.ACTIVE
			}.each {
				tobeProcessed.add(it)
			}

		}

		if (customMode == "percentage") {

			// multiplier is percentage
			tobeProcessed.each {
				process ->

					def subtotal = (process.subTotal - process.vatOutputTax)

					previousDeductions.each {
						prevdeduct ->

							if (prevdeduct.amountdetails.containsKey(process.id.toString()))
								subtotal -= prevdeduct.amountdetails[process.id.toString()]
					}

					// process the balance

					// if(subtotal > 0){
					def ded = PercentageUtils.deductionPercentageValue(subtotal, multiplier, fromInitial).setScale(4, RoundingMode.HALF_EVEN)//(subtotal * multiplier).setScale(4, RoundingMode.HALF_EVEN)
					results[process.id] = ded
					// }
			}
		} else {

			// multiplier is amount

			def itemBalance = [:] as Map<UUID, BigDecimal>

			tobeProcessed.each {
				process ->

					def subtotal = (process.subTotal - process.vatOutputTax)

					previousDeductions.each {
						prevdeduct ->

							if (prevdeduct.amountdetails.containsKey(process.id.toString()))
								subtotal -= prevdeduct.amountdetails[process.id.toString()]
					}

					// process the balance
					itemBalance.put(process.id, subtotal)

			}

			def amountToBeProcessedHCI = PercentageUtils.prorateFromValues(
					multiplier,
					itemBalance,
					true,
					false // only recognized whats need to be recognized

			)

			amountToBeProcessedHCI.each {
				k, v ->
					results[k] = v
			}

		}

		results

	}

	//================= code ni adonis ================
	//get OR numbers base on billing
	@GraphQLQuery(name = "paymentTracker", description = "List of Or Number based on Billing")
	List<PaymentTracker> paymentTracker(@GraphQLContext Billing billing) {
		return paymentTrackerServices.getPaymentsByBillingId(billing.id)
	}


	/*
	  Return the Department of Room during a specific time
	 */
	Department whereIsPatientAtDateTransaction(Case aCase,Instant dateTransaction){

	   def transfers = transferRepository.getTransfersByCaseWithRooms(aCase.id)

	  	 if(transfers){
			 def sortedDesc = transfers.toSorted {
				   a,b ->
					   b.entryDateTime <=> a.entryDateTime
			 }

			 for(i in sortedDesc){
				 if(dateTransaction >= i.entryDateTime){
					 return i.room.department
				 }
			 }
		 }

		// it has no rooms at this point
		return null

	}

	List<Billing> billingAdmissionChargeNew(UUID caseId) {
		def patientCase = caseRepository.findById(caseId).get()
 		createQuery("select b from Billing b where b.patientCase = :patientCase and b.status = 'ACTIVE' order by b.billingNo desc", [patientCase: patientCase]).resultList
	}
}




