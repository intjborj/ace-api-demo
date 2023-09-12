package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.AR_TRANS_TYPE
import com.hisd3.hismk2.domain.accounting.AccReceivableGroupParam
import com.hisd3.hismk2.domain.accounting.AccReceivableItemsDetailParam
import com.hisd3.hismk2.domain.accounting.AccountReceivable
import com.hisd3.hismk2.domain.accounting.AccountReceivableItems
import com.hisd3.hismk2.domain.accounting.ArLedger
import com.hisd3.hismk2.domain.accounting.ArPaymentTrackerTransaction
import com.hisd3.hismk2.domain.accounting.ArTransaction
import com.hisd3.hismk2.domain.accounting.ArTransactionDetails
import com.hisd3.hismk2.domain.accounting.ArTransfer
import com.hisd3.hismk2.domain.accounting.JournalType
import com.hisd3.hismk2.domain.accounting.LedgerDocType
import com.hisd3.hismk2.domain.billing.BillingItemStatus
import com.hisd3.hismk2.domain.billing.BillingItemType
import com.hisd3.hismk2.domain.billing.CompanyAccount
import com.hisd3.hismk2.domain.cashiering.PaymentTracker
import com.hisd3.hismk2.domain.inventory.Supplier
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.billing.BillingItemServices
import com.hisd3.hismk2.graphqlservices.billing.CompanyAccountServices
import com.hisd3.hismk2.graphqlservices.cashiering.PaymentTrackerServices
import com.hisd3.hismk2.graphqlservices.hrm.EmployeeService
import com.hisd3.hismk2.graphqlservices.inventory.SupplierService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.accounting.AccountReceivableItemsRepository
import com.hisd3.hismk2.repository.accounting.AccountReceivableRepository
import com.hisd3.hismk2.repository.accounting.ArTransactionDetailsRepository
import com.hisd3.hismk2.repository.accounting.ArTransactionRepository
import com.hisd3.hismk2.repository.accounting.BillingScheduleItemsRepository
import com.hisd3.hismk2.repository.accounting.BillingScheduleRepository
import com.hisd3.hismk2.repository.inventory.SupplierRepository
import com.hisd3.hismk2.rest.dto.ARForJournalViewDto
import com.hisd3.hismk2.rest.dto.AccRecPaymentTransferDto
import com.hisd3.hismk2.rest.dto.ArArrayDto
import com.hisd3.hismk2.rest.dto.ArTransferForJournalViewDto
import com.hisd3.hismk2.rest.dto.QueryErrorException
import com.hisd3.hismk2.services.EntityObjectMapperService
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Component
@GraphQLApi
class ArTransactionServices extends AbstractDaoService<ArTransaction> {

	ArTransactionServices() {
		super(ArTransaction.class)
	}

	@Autowired
	EntityObjectMapperService entityObjectMapperService

	@Autowired
	IntegrationServices integrationServices

	@Autowired
	AccountReceivableItemsServices accountReceivableItemsServices

	@Autowired
	AccountReceivableItemsRepository accountReceivableItemsRepository

	@Autowired
	CompanyAccountServices companyAccountServices

	@Autowired
	PaymentTrackerServices paymentTrackerServices

	@Autowired
	BillingItemServices billingItemServices

	@Autowired
	ArTransactionRepository arTransactionRepository

	@Autowired
	ArTransactionDetailsRepository arTransactionDetailsRepository

	@Autowired
	ArLedgerServices arLedgerServices

	@Autowired
	ArTransferServices arTransferServices

	@Autowired
	LedgerServices ledgerServices

	@Autowired
	EmployeeService employeeService

	@Autowired
	GeneratorService generatorService

	@Autowired
	ArPaymentTrackerServices arPaymentTrackerServices

	@Autowired
	ArPaymentTrackerTransServices arPaymentTrackerTransServices

	@Autowired
	SupplierRepository supplierRepository

	@Autowired
	ArTransactionTypeServices arTransactionTypeServices

	@Autowired
	SupplierService supplierService

	@Autowired
	AccountReceivableServices accountReceivableServices

	@GraphQLQuery(name = "findPaymentTrackerById")
	PaymentTracker findPaymentTrackerById(
			@GraphQLArgument(name = "id") UUID id
	) {
		if(id){
			paymentTrackerServices.findOne(id)
		}
		else{
			return new PaymentTracker()
		}
	}

	@GraphQLQuery(name = "findORForAccountReceivable")
	Page<PaymentTracker> findORForAccountReceivable(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		paymentTrackerServices.getPageable(
				"""
              	Select c from PaymentTracker c  where lower(c.ornumber) like lower(concat('%',:filter,'%')) order by c.ornumber
				""",
				"""
			 	Select count(c) from PaymentTracker c where lower(c.ornumber) like lower(concat('%',:filter,'%'))
				""",
				page,
				size,
				[
						filter: filter,
				]
		)

	}


	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation
	ArTransaction addARTransaction(
			@GraphQLArgument(name = "type") String type,
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "fieldsItems") ArrayList<Map<String, Object>> fieldsItems,
			@GraphQLArgument(name = "entries") ArrayList<Map<String, Object>> entries
	) {
		//			SAVE AR PAYMENT IN TRANSACTION
		def item = new ArTransaction()

		if (type == 'memo') {
			item.trackingNo = generatorService.getNextValue(GeneratorType.ArMEMO, {
				return "CM-" + StringUtils.leftPad(it.toString(), 6, "0")
			})
		}
		else if (type == 'transfer') {
			item.trackingNo = generatorService.getNextValue(GeneratorType.ArTRANSFER, {
				return "TRANS-" + StringUtils.leftPad(it.toString(), 6, "0")
			})

		}
		else {
			item.trackingNo = generatorService.getNextValue(GeneratorType.ArPAY, {
				return "ART-" + StringUtils.leftPad(it.toString(), 6, "0")
			})
		}
		item.status = "ACTIVE"

		entityObjectMapperService.updateFromMap(item,fields)


		def newSave = save(item)

		if (type == 'memo') {
			if (newSave.accountReceivable.groups[AccReceivableGroupParam.COMPANY_ACCOUNT_ID.name()]) {
				def comp = companyAccountServices.findOne(newSave.accountReceivable.groups[AccReceivableGroupParam.COMPANY_ACCOUNT_ID.name()])
				if (comp)
					newSave.companyAccount = comp
			}

			if (newSave.accountReceivable.groups[AccReceivableGroupParam.PERSONAL_ACCOUNT_ID.name()]) {
				def supplier = supplierService.getSupplier(newSave.accountReceivable.groups[AccReceivableGroupParam.PERSONAL_ACCOUNT_ID.name()].toString())
				if (supplier)
					newSave.personalAccount = supplier
			}
			newSave = save(newSave)
		}

		if(type.equalsIgnoreCase('companyPayments') || type.equalsIgnoreCase('personalAccPayments'))
		{
			def paymentTracker = arPaymentTrackerServices.findORIfExistARPT(newSave.paymentTracker.id)
			if(paymentTracker){
				paymentTracker.credit = paymentTracker.credit + newSave.amount
				arPaymentTrackerServices.saveArPaymentTracker(paymentTracker)

				ArPaymentTrackerTransaction apTT = new ArPaymentTrackerTransaction()
				apTT.arPaymentTracker = paymentTracker
				apTT.accountReceivable = newSave.accountReceivable
				apTT.arTransaction = newSave
				apTT.amount = newSave.amount
				apTT.status = 'active'
				arPaymentTrackerTransServices.saveArPaymentTrackerTrans(apTT)
			}
		}

		//		SAVE AR TRANSACTION DETAILS
		def totalHci = 0
		def totalPf = 0
		fieldsItems.each {
			it ->
				//				Insert New AR Transaction
				def subItem = new ArTransactionDetails()
				entityObjectMapperService.updateFromMap(subItem,it)
				if (type.equalsIgnoreCase('memo') || type.equalsIgnoreCase('transfer')) {
					subItem.type = type
				}
				else{
					subItem.type = 'payments'
				}
				subItem.arTransaction = newSave
				def transDetails = arTransactionDetailsRepository.save(subItem)

				//				Update Account Receivable Items
				def arItems = accountReceivableItemsServices.findOne(transDetails.accountReceivableItems.id)
				if(arItems.type.equalsIgnoreCase("HCI")){
					totalHci = totalHci + subItem.amount
				}

				if(arItems.type.equalsIgnoreCase("PF")){
					totalPf = totalPf + subItem.amount
				}

				if(type.equalsIgnoreCase('transfer')){
					arItems.debit = arItems.debit - transDetails.amount
				}
				else {
					arItems.credit = arItems.credit + transDetails.amount
					arLedgerServices.creditArLedgerPerAr(transDetails,arItems)
				}


				accountReceivableItemsRepository.save(arItems)

//					ADD TO TRANSFER TABLE
				if (type == 'transfer') {
					def fieldsObject = new ArArrayDto()
					entityObjectMapperService.updateFromMap(fieldsObject,fields)

					def from = new ArTransferForJournalViewDto()
					entityObjectMapperService.updateFromMap(from,fieldsObject.from)
					def companySourceAccount = companyAccountServices.findOne(from.company)
//
					def to = new ArTransferForJournalViewDto()
					entityObjectMapperService.updateFromMap(to,fieldsObject.to)
					def companyAccount = companyAccountServices.findOne(to.company)

					def arTransfer = new ArTransfer()
					arTransfer.companyAccount = companyAccount
					arTransfer.companySourceAccount = companySourceAccount
					def billingItemId = UUID.fromString(subItem.accountReceivableItems.details[AccReceivableItemsDetailParam.BILLING_ITEM_ID.name()])
					def billingItem = billingItemServices.findOne(billingItemId)
					if(billingItem){
						arTransfer.billingItem = billingItem
						if(billingItem.billing){
							arTransfer.billing = billingItem.billing
						}
					}
					arTransfer.arTransaction = newSave
					arTransfer.arTransactionDetails = transDetails
					arTransfer.amount = subItem.amount
					arTransfer.status = 'active'
					arTransfer.reference = fieldsObject.reference
					arTransferServices.upsertTransfer(arTransfer)

					// Add to SOA
					if(billingItem){
						def pfEmpId = transDetails.accountReceivableItems.details[AccReceivableItemsDetailParam.PF_EMPLOYEEID.name()]
						def extraLabel = ""
						if(pfEmpId){
							def doctors = employeeService.findById(UUID.fromString(pfEmpId))
							extraLabel = doctors?.fullName ? "${ doctors.fullName } (PF)" : "(PF)"
						}
						else {
							extraLabel = "(${subItem.accountReceivableItems.type})"
						}

						DateTimeFormatter soaDate = DateTimeFormatter.ofPattern("MM/dd/yyyy");
						if(!(companySourceAccount.companyname.equalsIgnoreCase('PROMISSORY NOTE'))){
							def billingAnnotationsSource = billingItemServices.addAnnotations(billingItem.billing.id,
									BillingItemType.valueOf('ANNOTATION_PAYMENTS_GROUPS'),
									"${billingItem.description} (WRONG ENTRY) ${arTransfer.createdDate.atZone(ZoneId.systemDefault()).format(soaDate).toString()}",
									-transDetails.amount
							)
							subItem.billingItemSource = billingAnnotationsSource
						}

						if(!(companyAccount.companyname.equalsIgnoreCase('PROMISSORY NOTE'))){
							def billingAnnotations = billingItemServices.addAnnotations(billingItem.billing.id,
									BillingItemType.valueOf('ANNOTATION_PAYMENTS_GROUPS'),
									"PAYMENTS: ${companyAccount.companyname} - ${extraLabel} ${arTransfer.createdDate.atZone(ZoneId.systemDefault()).format(soaDate).toString()}",
									transDetails.amount
							)
							subItem.billingItemRef = billingAnnotations
						}
						arTransactionDetailsRepository.save(subItem)

					}
				}
		}

		newSave.totalHci = totalHci
		newSave.totalPf = totalPf
		if(type.equalsIgnoreCase('transfer') || type.equalsIgnoreCase('memo')){
			arLedgerServices.creditArLedgerFromAr(newSave)
		}
		return newSave



	}

	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation
	ArTransaction removeARTransaction(
			@GraphQLArgument(name = "arTransId") UUID arTransId
	) {

		def arTrans = arTransactionRepository.findById(arTransId).get()
		if(arTrans.status == "ACTIVE"){
			arTrans.arTransactionItems.each {
				def arItems = accountReceivableItemsServices.findOne(it.accountReceivableItems.id)
				arItems.credit = arItems.credit - it.amount
				accountReceivableItemsRepository.save(arItems)
				it.isVoided = true
				if(it.billingItemRef){
					if(it.billingItemRef.status == BillingItemStatus.ACTIVE){
						billingItemServices.toggleBillingItem(it.billingItemRef.id.toString())
					}
				}
				arTransactionDetailsRepository.save(it)
			}

			if(arTrans.type.equalsIgnoreCase('payments'))
			{
				//	Ar Payment tracker Trans
				def arPTT = arPaymentTrackerTransServices.findArPTTByTransId(arTrans.id)
				arPTT.status = 'inactive'
				arPaymentTrackerTransServices.saveArPaymentTrackerTrans(arPTT)
				//	Ar Payment tracker
				def arPT = arPaymentTrackerServices.findOne(arPTT.arPaymentTracker.id)
				arPT.credit = arPT.credit - arPTT.amount
				arPaymentTrackerServices.saveArPaymentTracker(arPT)
				arLedgerServices.reverseArLedgerByReference(arTrans.trackingNo)
			}

			arTrans.status = "INACTIVE"
			arTransactionRepository.save(arTrans)

			def header = ledgerServices.findOne(arTrans.postedLedger)
			ledgerServices.reverseEntries(header)
		}

		return  arTrans
	}

	@GraphQLQuery(name = "viewARTransactionByType")
	Page<ArTransaction> viewARTransactionByType(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "type") String type,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		getPageable(
				"""
					Select c from ArTransaction c where c.type = :type and c.status = 'ACTIVE' and lower(c.trackingNo) like lower(concat('%',:filter,'%')) order by c.trackingNo
					""",
				"""
					Select count(c) from ArTransaction c where c.type = :type and c.status = 'ACTIVE' AND lower(c.trackingNo) like lower(concat('%',:filter,'%'))
					""",
				page,
				size,
				[
						filter: filter,
						type: type,
				]
		)

	}


	@GraphQLQuery(name = "ageReceivableArByDate")
	ArTransaction ageReceivableArByDate(@GraphQLArgument(name = "accountReceivable") UUID accountReceivable,
										@GraphQLArgument(name = "dateStart") String dateStart,
										@GraphQLArgument(name = "dateEnd") String dateEnd
	) {
		createQuery("""select b from ArTransaction b where  b.accountReceivable.id = :accountReceivable and to_date(to_char(b.createdDate, 'YYYY-MM-DD'),'YYYY-MM-DD') between to_date(:dateStart,'YYYY-MM-DD') and to_date(:dateEnd,'YYYY-MM-DD') order by b.createdDate desc""",
				[
						accountReceivable: accountReceivable,
						dateStart: dateStart,
						dateEnd: dateEnd
				] as Map<String, Object>).setMaxResults(1).singleResult
	}

	@GraphQLQuery(name = "findARTransactionById")
	ArTransaction findARTransactionById(@GraphQLArgument(name = "id") UUID id) {
		return findOne(id)
	}

	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLQuery(name = "voidTransactionById")
	voidTransactionById(@GraphQLArgument(name = "id") UUID id) {
		def arTrans = findOne(id)
		if(arTrans.status == "ACTIVE"){
			arTrans.arTransactionItems.each {
				def arItems = accountReceivableItemsServices.findOne(it.accountReceivableItems.id)
				if(it.type == 'transfer'){
					arItems.debit = arItems.debit + it.amount
				}
				else {
					arItems.credit = arItems.credit - it.amount
				}
				accountReceivableItemsRepository.save(arItems)
				it.isVoided = true
				arTransactionDetailsRepository.save(it)
			}


			arTrans.status = "INACTIVE"
			arTransactionRepository.save(arTrans)


			def header = ledgerServices.findOne(arTrans.postedLedger)
			ledgerServices.reverseEntries(header)
		}

		return  arTrans
	}


//	Use 2021
	@GraphQLQuery(name = "getMemoAndPaymentList")
	List<ArTransaction> getMemoAndPaymentList(@GraphQLArgument(name = "id") UUID id
	) {

		if (id) {
			def query =  createQuery("""select b from ArTransaction b where  b.accountReceivable.id = :id and b.type in ('memo','payments') and b.status = 'ACTIVE' order by b.trackingNo desc""",
					[
							id: id,
					] as Map<String, Object>).resultList
			if(query){
				return  query
			}
			return  new ArrayList<ArTransaction>()
		}
		else{
			return new ArrayList<ArTransaction>()
		}
	}

	@GraphQLQuery(name = "paymentsTransactionByARId")
	List<ArTransaction> paymentsTransactionByARId(@GraphQLArgument(name = "id") UUID id
	) {
		createQuery("""select b from ArTransaction b where  b.accountReceivable.id = :id and b.type = 'payments' order by b.trackingNo desc""",
				[
						id: id,
				] as Map<String, Object>).resultList
	}

//  New update 06-03-21

	@GraphQLQuery(name = "receivableGuarantorPaymentList")
	Page<ArTransaction> receivableGuarantorPaymentList(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "accounts") ArrayList<UUID> accounts,
			@GraphQLArgument(name = "status") ArrayList<String> status,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {
		if(accounts){
			getPageable(
					"""
					Select c from ArTransaction c where c.companyAccount.id in (:accounts) and c.type = 'payments' and c.status in (:status) and lower(c.trackingNo) like lower(concat('%',:filter,'%')) order by c.trackingNo desc
					""",
					"""
					Select count(c) from ArTransaction c where c.companyAccount.id in (:accounts) and c.type = 'payments' and c.status in (:status) AND lower(c.trackingNo) like lower(concat('%',:filter,'%'))
					""",
					page,
					size,
					[
							filter: filter,
							status: status,
							accounts: accounts
					]
			)
		}
		else{
			getPageable(
					"""
					Select c from ArTransaction c where c.companyAccount.id is not null and  c.type = 'payments' and c.status in (:status) and lower(c.trackingNo) like lower(concat('%',:filter,'%')) order by c.trackingNo desc
					""",
					"""
					Select count(c) from ArTransaction c where c.companyAccount.id is not null and  c.type = 'payments' and c.status in (:status) AND lower(c.trackingNo) like lower(concat('%',:filter,'%'))
					""",
					page,
					size,
					[
							filter: filter,
							status: status
					]
			)
		}
	}

	@GraphQLQuery(name = "receivablePersonalPaymentList")
	Page<ArTransaction> receivablePersonalPaymentList(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "accounts") ArrayList<UUID> accounts,
			@GraphQLArgument(name = "status") ArrayList<String> status,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {
		if(accounts){
			getPageable(
					"""
					Select c from ArTransaction c where c.personalAccount.id in (:accounts) and c.type = 'payments' and c.status in (:status) and lower(c.trackingNo) like lower(concat('%',:filter,'%')) order by c.trackingNo desc
					""",
					"""
					Select count(c) from ArTransaction c where c.personalAccount.id in (:accounts) and c.type = 'payments' and c.status in (:status) AND lower(c.trackingNo) like lower(concat('%',:filter,'%'))
					""",
					page,
					size,
					[
							filter: filter,
							status: status,
							accounts: accounts
					]
			)
		}
		else{
			getPageable(
					"""
					Select c from ArTransaction c where c.personalAccount.id is not null and  c.type = 'payments' and c.status in (:status) and lower(c.trackingNo) like lower(concat('%',:filter,'%')) order by c.trackingNo desc
					""",
					"""
					Select count(c) from ArTransaction c where c.personalAccount.id is not null and  c.type = 'payments' and c.status in (:status) AND lower(c.trackingNo) like lower(concat('%',:filter,'%'))
					""",
					page,
					size,
					[
							filter: filter,
							status: status
					]
			)
		}
	}

	@Transactional
	@GraphQLMutation
	ArTransaction transactionProcess(
			@GraphQLArgument(name = "type") String type,
			@GraphQLArgument(name = "journalDate") java.util.Date journalDate,
			@GraphQLArgument(name = "fieldsItems") ArrayList<Map<String, Object>> fieldsItems,
			@GraphQLArgument(name = "dataEntry") Map<String, Object> dataEntry
	){
		if(type.equalsIgnoreCase('payment') || type.equalsIgnoreCase('arTransfer') || type.equalsIgnoreCase('memo')) {
			def auto = new AccRecPaymentTransferDto()
			entityObjectMapperService.updateFromMap(auto, dataEntry)
			def newSave = addARTransaction(auto.type,auto.fields,fieldsItems,auto.entries)
			def yearFormat = DateTimeFormatter.ofPattern("yyyy")

			if ((auto.type.equalsIgnoreCase('transfer')) && (newSave.totalHci > 0)) {
				def fieldsObject = new ArArrayDto()
				entityObjectMapperService.updateFromMap(fieldsObject, auto.fields)

				def companyAcc = new ArTransferForJournalViewDto()
				entityObjectMapperService.updateFromMap(companyAcc, fieldsObject.from)
				def companyAccount = companyAccountServices.findOne(companyAcc.company)

				def companyAccTo = new ArTransferForJournalViewDto()
				entityObjectMapperService.updateFromMap(companyAccTo, fieldsObject.to)
				def companyAccountTo = companyAccountServices.findOne(companyAccTo.company)

				def headerLedger = integrationServices.generateAutoEntries(newSave) { it, mul ->
					it.flagValue = AR_INTEGRATION.AR_TRANSFER.name()
					it.companyAccount = companyAccount
					it.amount = it.totalHci
					it.negativeAmount = -it.totalHci
				}
				Map<String, String> details = [:]

				newSave.details.each { k, v ->
					details[k] = v
				}

				def transactionDate
				if(journalDate){
					transactionDate = journalDate.toInstant()
				}
				else{
					transactionDate = newSave.createdDate
				}

				def pHeader = ledgerServices.persistHeaderLedger(headerLedger,
						"${transactionDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${newSave.trackingNo?:''}",
						"${newSave.trackingNo}-${companyAccount.companyname}",
						"${companyAccount.companyname}- TRANSFER TO -${companyAccountTo.companyname}",
						LedgerDocType.JV,
						JournalType.GENERAL,
						transactionDate,
						details)
				newSave.postedLedger = pHeader.id
				arTransactionRepository.save(newSave)
			}
			else if (auto.type.equalsIgnoreCase('personalAccPayments')) {

				def mapFields = new ArMemoArrayDto()
				entityObjectMapperService.updateFromMap(mapFields, auto.fields)
				def personalAccount = supplierRepository.findById(mapFields.personalAccount).get()
				def headerLedger =	integrationServices.generateAutoEntries(newSave){it, mul ->
					it.flagValue = AR_INTEGRATION.AR_MANUAL_PAYMENT.name()
					it.negativeAmount = -it.amount
				}

				Map<String,String> details = [:]

				newSave.details.each { k,v ->
					details[k] = v
				}

				def transactionDate
				if(journalDate){
					transactionDate = journalDate.toInstant()
				}
				else{
					transactionDate = newSave.createdDate
				}

				def pHeader =	ledgerServices.persistHeaderLedger(headerLedger,
						"${transactionDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${newSave.trackingNo?:''}",
						"${newSave.trackingNo}-${personalAccount.supplierFullname}",
						"${newSave.trackingNo}- PAYMENTS",
						LedgerDocType.JV,
						JournalType.GENERAL,
						transactionDate,
						details)
				newSave.postedLedger = pHeader.id
				arTransactionRepository.save(newSave)
			}
			else if(auto.type.equalsIgnoreCase('memo'))
			{
				ArMemoArrayDto memoDto = new ArMemoArrayDto()
				def upsertFields = entityObjectMapperService.updateFromMap(memoDto,auto.fields)
				def transType = arTransactionTypeServices.findOne(upsertFields.arTransactionType)

				def accName = ""
				newSave.companyAmt = 0

				if(newSave.companyAccount){
					accName = newSave.companyAccount.companyname
				}

				def headerLedger = integrationServices.generateAutoEntries(newSave){it,mul ->
					it.flagValue = transType.integration.flagValue
				}

				Map<String,String> details = [:]

				newSave.details.each { k,v ->
					details[k] = v
				}

				def transactionDate
				if(journalDate){
					transactionDate = journalDate.toInstant()
				}
				else{
					transactionDate = newSave.createdDate
				}

				def pHeader =	ledgerServices.persistHeaderLedger(headerLedger,
						"${transactionDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${newSave.accountReceivable.arNo}",
						"${newSave.accountReceivable.arNo}-${accName}",
						"${newSave.trackingNo}- CREDIT MEMO",
						LedgerDocType.JV,
						JournalType.GENERAL,
						transactionDate,
						details)
				newSave.postedLedger = pHeader.id
				arTransactionRepository.save(newSave)
			}
			else {
				def mapFields = new ArMemoArrayDto()
				entityObjectMapperService.updateFromMap(mapFields, auto.fields)
				def companyAccount = companyAccountServices.findOne(mapFields.companyAccount)
				def headerLedger =	integrationServices.generateAutoEntries(newSave){it, mul ->
					it.flagValue = AR_INTEGRATION.AR_PAYMENTS.name()
					it.companyAccount = companyAccount
					it.artC = -it.amount
					it.artA = -it.totalHci
					it.artB = it.totalPf
				}

				Map<String,String> details = [:]

				newSave.details.each { k,v ->
					details[k] = v
				}

				def transactionDate
				if(journalDate){
					transactionDate = journalDate.toInstant()
				}
				else{
					transactionDate = newSave.createdDate
				}

				def pHeader =	ledgerServices.persistHeaderLedger(headerLedger,
						"${transactionDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${newSave.trackingNo}",
						"${newSave.trackingNo}-${companyAccount.companyname}",
						"${newSave.trackingNo}- PAYMENTS",
						LedgerDocType.JV,
						JournalType.GENERAL,
						transactionDate,
						details)
				newSave.postedLedger = pHeader.id
				arTransactionRepository.save(newSave)
			}
			return  newSave
		}
		else if (type.equalsIgnoreCase('voidTransaction')) {
			def auto = new AccRecPaymentTransferDto()
			entityObjectMapperService.updateFromMap(auto, dataEntry)
			def newSave = removeARTransaction(auto.arTransId)
			return newSave
		}

		else if (type.equalsIgnoreCase('voidTransfer')) {
			def auto = new AccRecPaymentTransferDto()
			entityObjectMapperService.updateFromMap(auto, dataEntry)
			def newSave = arTransferServices.voidTransfer(auto.arTransId)
			return newSave.arTransaction
		}

	}



	@Transactional
	GraphQLRetVal<ArTransaction> processCreditMemo(
			Map<String, Object> fields,
			ArrayList<Map<String, Object>> fieldsItems

	){
		try{
			//AR TRANSACTION
			ArTransaction arT = new ArTransaction()
			arT.trackingNo = generatorService.getNextValue(GeneratorType.ArMEMO, {
				return "CM-" + StringUtils.leftPad(it.toString(), 6, "0")
			})
			arT.status = "ACTIVE"
			entityObjectMapperService.updateFromMap(arT,fields)
			def newSave = save(arT)

			//--AR TRANSACTION DETAILS
			def totalHci = 0
			def totalPf = 0
			fieldsItems.each {
				it ->
					//--NEW AR TRANSACTION
					ArTransactionDetails arTSub = new ArTransactionDetails()
					entityObjectMapperService.updateFromMap(arTSub,it)
					arTSub.type = AR_TRANS_TYPE.memo.name()
					arTSub.arTransaction = newSave
					def newArTSub = arTransactionDetailsRepository.save(arTSub)

					//--UPDATE AR ITEMS
					def arItems = accountReceivableItemsServices.findOne(newArTSub.accountReceivableItems.id)
					if(arItems.type.equalsIgnoreCase("HCI")){
						totalHci = totalHci + newArTSub.amount
					}

					if(arItems.type.equalsIgnoreCase("PF")){
						totalPf = totalPf + newArTSub.amount
					}

					arItems.credit = arItems.credit + newArTSub.amount
					arLedgerServices.creditArLedgerPerAr(newArTSub,arItems)
					accountReceivableItemsRepository.save(arItems)
			}

			newSave.totalHci = totalHci
			newSave.totalPf = totalPf
			arLedgerServices.creditArLedgerFromAr(newSave)
			new GraphQLRetVal<ArTransaction>(arT,true,"Successfully saved.")
		}
		catch (e){
			new GraphQLRetVal<ArTransaction>(null,false,e.message)
		}

	}


	@Transactional
	@GraphQLMutation
	GraphQLRetVal<ArTransaction> processARTransactions(
			String type,
		 	Map<String, Object> fields,
			ArrayList<Map<String, Object>> fieldsItems
	){
		try{
			//		AR MEMO
			if(AR_TRANS_TYPE.memo.name() == type){
				def art = processCreditMemo(fields,fieldsItems)
				if(art.payload)
					return new GraphQLRetVal<ArTransaction>(art.payload,true,'Successfully added.')
			}

		}
		catch (e){
			return new GraphQLRetVal<ArTransaction>(null,false,e.message)
		}

	}
}
