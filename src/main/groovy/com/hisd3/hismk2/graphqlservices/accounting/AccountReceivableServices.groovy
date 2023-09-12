package com.hisd3.hismk2.graphqlservices.accounting


import com.hisd3.hismk2.domain.accounting.*
import com.hisd3.hismk2.domain.billing.CompanyAccount
import com.hisd3.hismk2.domain.hospital_config.DefaultAdmissionItems
import com.hisd3.hismk2.domain.inventory.Supplier
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.billing.CompanyAccountServices
import com.hisd3.hismk2.graphqlservices.billing.Salesreportitem
import com.hisd3.hismk2.graphqlservices.inventory.SupplierService
import com.hisd3.hismk2.repository.accounting.AccountReceivableItemsRepository
import com.hisd3.hismk2.repository.accounting.AccountReceivableRepository
import com.hisd3.hismk2.repository.accounting.ArTransactionDetailsRepository
import com.hisd3.hismk2.repository.accounting.ArTransactionRepository
import com.hisd3.hismk2.repository.accounting.BillingScheduleRepository
import com.hisd3.hismk2.repository.inventory.SupplierRepository

import com.hisd3.hismk2.rest.dto.EditedJournalEntryDto
import com.hisd3.hismk2.rest.dto.AccRecPaymentTransferDto
import com.hisd3.hismk2.rest.dto.AccRecOtherDto
import com.hisd3.hismk2.rest.dto.AccRecDto
import com.hisd3.hismk2.rest.dto.ARForJournalViewDto
import com.hisd3.hismk2.rest.dto.ArTransferForJournalViewDto
import com.hisd3.hismk2.rest.dto.JournalEntryViewDto
import com.hisd3.hismk2.rest.dto.OtherARForJournalViewDto
import com.hisd3.hismk2.rest.dto.ArArrayDto
import com.hisd3.hismk2.services.EntityObjectMapperService
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.hibernate.resource.transaction.spi.TransactionStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

import javax.transaction.Transactional
import java.sql.ResultSet
import java.sql.SQLException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum AR_INTEGRATION {
	AR_POSTING,
	AR_PAYMENTS,
	AR_RENTAL_ELECTRICITY,
	AR_MANUAL_PAYMENT,
	AR_TRANSFER,
	AR_POSTING_WITH_ADJUSTMENT
}

@Canonical
class ArMemoArrayDto {
	UUID arTransactionType
	UUID accountReceivable
	UUID companyAccount
	UUID personalAccount
	BigDecimal amount
}

@Canonical
class ArJournalAmountDto {
	BigDecimal amount
	BigDecimal hci
	BigDecimal pf
}

@Canonical
class ArYear{
	String transDate
}


@Canonical
class MergeReceivableAccount {
	UUID id
	String accountName
}

@Transactional(rollbackOn = [Exception.class])
@Component
@GraphQLApi
class AccountReceivableServices extends AbstractDaoService<AccountReceivable> {

	AccountReceivableServices() {
		super(AccountReceivable.class)
	}

	@Autowired
	EntityObjectMapperService entityObjectMapperService

	@Autowired
	AccountReceivableRepository accountReceivableRepository

	@Autowired
	AccountReceivableItemsRepository accountReceivableItemsRepository

	@Autowired
	BillingScheduleRepository billingScheduleRepository

	@Autowired
	ArTransactionServices arTransactionServices

	@Autowired
	ArTransferServices arTransferServices

	@Autowired
	AccountReceivableItemsServices accountReceivableItemsServices

	@Autowired
	ArTransactionDetailsServices arTransactionDetailsServices

	@Autowired
	ArTransactionRepository arTransactionRepository

	@Autowired
	ArTransactionDetailsRepository arTransactionDetailsRepository

	@Autowired
	CompanyAccountServices companyAccountServices

	@Autowired
	IntegrationServices integrationServices

	@Autowired
	LedgerServices ledgerServices

	@Autowired
	ArLedgerServices arLedgerServices

	@Autowired
	BillingScheduleServices billingScheduleServices

	@Autowired
	SupplierRepository supplierRepository

	@Autowired
	GeneratorService generatorService

	@Autowired
	Wtx2307Service wtx2307Service

	@Autowired
	SupplierService supplierService

	@Autowired
	ArTransactionTypeServices arTransactionTypeServices

	@GraphQLQuery(name = "accountReceivable", description = "List of ar")
	AccountReceivable accountReceivable(
			@GraphQLArgument(name = "id") UUID id
	) {
		def value = null
		if(id){
			value = findOne(id)
		}
		return value

	}

	@Transactional
	@GraphQLMutation
	AccountReceivable upsertCompanyAR(
			@GraphQLArgument(name = "accRecId") UUID accRecId,
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "billingScheduleId") UUID billingScheduleId

	) {

		if (accRecId) {
			AccountReceivable item = accountReceivableRepository.findById(accRecId).get()
			def status = fields['status'] as String
			if(status.equalsIgnoreCase("voided")){
				def checkTransaction = arTransactionDetailsServices.checkIfARItemIdExist(item)

				if(!checkTransaction){
					entityObjectMapperService.updateFromMap(item, fields)
					if(item.groups[AccReceivableGroupParam.BILLING_SCHEDULE_ID.name()]){
						def billSchedule = billingScheduleServices.findOne(item.groups[AccReceivableGroupParam.BILLING_SCHEDULE_ID.name()])
						billSchedule.status = "draft"
						billingScheduleRepository.save(billSchedule)
					}
					def newSave = accountReceivableRepository.save(item)
					return	newSave
				}
				return null
			}
			else{
				entityObjectMapperService.updateFromMap(item, fields)
				def newSave = accountReceivableRepository.save(item)
				return newSave
			}
		} else {
			def billSchedule = billingScheduleRepository.findById(billingScheduleId).get()
			billSchedule.status = 'posted'
			billingScheduleRepository.save(billSchedule)

			def accountReceivable = new AccountReceivable()

			accountReceivable.arNo = generatorService.getNextValue(GeneratorType.ACCOUNT_RECEIVABLE_NO, {
				return "ARNO-" + StringUtils.leftPad(it.toString(), 6, "0")
			})

			accountReceivable.postedLedger = null
			accountReceivable.transactionDate = billSchedule.transactionDate
			accountReceivable.dueDate = billSchedule.dueDate
			accountReceivable.status = 'active'
			accountReceivable.groups[AccReceivableGroupParam.COMPANY_ACCOUNT_ID.name()] = billSchedule.companyAccount.id
			accountReceivable.groups[AccReceivableGroupParam.BILLING_SCHEDULE_ID.name()] = billSchedule.id
			def newSave = save(accountReceivable)
			newSave.totals = billSchedule.totalAmountWithDebit

			billSchedule.billingScheduleItems.each {
				it ->
					if(!it.isVoided){
						def newBill = new AccountReceivableItems()
						newBill.accountReceivable = newSave

						newBill.recordNo = generatorService.getNextValue(GeneratorType.AR_RECORD_NO, {
							return StringUtils.leftPad(it.toString(), 6, "0")
						})

						newBill.details[AccReceivableItemsDetailParam.PATIENT_ID.name()] = it.billing.patient.id.toString()
						if(it.billingItem){
							newBill.details[AccReceivableItemsDetailParam.BILLING_ITEM_ID.name()] = it.billingItem.id.toString()
							if(it.billingItem.details['PF_EMPLOYEEID']){
								newBill.details[AccReceivableItemsDetailParam.PF_EMPLOYEEID.name()] = it.billingItem.details['PF_EMPLOYEEID'].toString()
							}
						}

						newBill.description = "[${it.billing.billingNo}] ${it.billing.patient.fullName} (${it.type})"
						if(it.debitAdjustment > 0){
							newBill.amount = it.amount
							newBill.debit = it.amount+it.debitAdjustment
						}
						else {
							newBill.amount = it.amount
							newBill.debit = it.amount
						}
						newBill.credit = 0
						newBill.type = it.type
						newBill.status = 'pending'
						accountReceivableItemsRepository.save(newBill)
					}
			}

			if(billSchedule.hci > 0){
				newSave.hci = billSchedule.hci
				newSave.negativeHciAmount = -billSchedule.hci
//				upsertJournalAR(newSave)
			}

			return newSave
		}

	}

	@Transactional
	@GraphQLMutation
	AccountReceivable upsertManualAccountReceivable(
			@GraphQLArgument(name = "companyId") UUID companyId,
			@GraphQLArgument(name = "creditMemoId") UUID creditMemoId,
			@GraphQLArgument(name = "type") String type,
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "entries") ArrayList<Map<String, Object>> entries
	) {
		def accountReceivable = new AccountReceivable()
		if(type == 'transfer'){
			def company = companyAccountServices.findOne(companyId)
			def creditMemo = arTransactionServices.findOne(creditMemoId)
			accountReceivable.arNo = generatorService.getNextValue(GeneratorType.ACCOUNT_RECEIVABLE_NO, {
				return "ARNO-" + StringUtils.leftPad(it.toString(), 6, "0")
			})
			entityObjectMapperService.updateFromMap(accountReceivable,fields)
			accountReceivable.postedLedger = null
			accountReceivable.transactionDate = creditMemo.accountReceivable.transactionDate
			accountReceivable.status = 'active'
			accountReceivable.groups[AccReceivableGroupParam.COMPANY_ACCOUNT_ID.name()] = companyId
			accountReceivable.groups[AccReceivableGroupParam.CREDIT_MEMO_ID.name()] = creditMemo.id
			def newSave = accountReceivableRepository.save(accountReceivable)
			newSave.totals = creditMemo.amount

			creditMemo.arTransactionItems.each {
				it->
					def newARItems = new AccountReceivableItems()
					newARItems.accountReceivable = newSave
					newARItems.description = it.accountReceivableItems.description
					newARItems.details[AccReceivableItemsDetailParam.PARENT_AR_ITEM.name()] = it.accountReceivableItems.id.toString()
					newARItems.details[AccReceivableItemsDetailParam.PATIENT_ID.name()] = it.accountReceivableItems.details[AccReceivableItemsDetailParam.PATIENT_ID.name()]
					newARItems.debit = it.amount
					newARItems.credit = 0
					newARItems.type = it.accountReceivableItems.type
					newARItems.status = 'active'
					accountReceivableItemsRepository.save(newARItems)
			}

			def accRec = super.save(newSave) as AccountReceivable
			Map<String,String> details = [:]
			accRec.details.each { k,v ->
				details[k] = v
			}
			details["ACC_RECEIVABLE_ID"] = accRec.id.toString()
			def yearFormat = DateTimeFormatter.ofPattern("yyyy")

			Map<String, Object> header = new HashMap<String ,Object>()
			header.put("invoiceSoaReference","${accRec.createdDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${accRec.arNo}")
			header.put("entityName","${accRec.arNo}-${company.companyname}")
			header.put("particulars","${accRec.arNo}-${company.companyname}-${accRec.remarks}")
			def pHeader = ledgerServices.addManualJVDynamic(header,entries,LedgerDocType.JV,JournalType.GENERAL,newSave.createdDate,details)
			accRec.postedLedger = pHeader.returnId
			save(accRec)
		}
		return  accountReceivable

	}


	@GraphQLMutation
	AccountReceivable updatePersonalAccRefNo(
			@GraphQLArgument(name = "accRecId") UUID accRecId
	) {
		def accountReceivable = findOne(accRecId)
		def size = accountReceivable.referenceNo.split("-").length
		if(size < 2){
			accountReceivable.referenceNo = generatorService.getNextValue(GeneratorType.BILLING_SCHEDULE_NO, {
				return "BSN-" + StringUtils.leftPad(it.toString(), 6, "0")
			})
			save(accountReceivable)
		}
		return	accountReceivable

	}

	@Transactional
	@GraphQLMutation
	AccountReceivable upsertPersonalAccountAR(
			@GraphQLArgument(name = "accRecId") UUID accRecId,
			@GraphQLArgument(name = "type") String type,
			@GraphQLArgument(name = "personalAccount") UUID personalAccount,
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "fieldsItems") ArrayList<Map<String, Object>> fieldsItems
	) {

		if (accRecId) {
			def personalAccountDetails = supplierRepository.findById(personalAccount).get()
			def accountReceivable = findOne(accRecId)
			entityObjectMapperService.updateFromMap(accountReceivable, fields)

			if(type.equalsIgnoreCase('save')){
				accountReceivable.arNo = generatorService.getNextValue(GeneratorType.ACCOUNT_RECEIVABLE_NO, {
					return "ARNO-" + StringUtils.leftPad(it.toString(), 6, "0")
				})
				accountReceivable.referenceNo = generatorService.getNextValue(GeneratorType.BILLING_SCHEDULE_NO, {
					return "BSN-" + StringUtils.leftPad(it.toString(), 6, "0")
				})
				accountReceivable.status = 'active'
			}
			else{
				accountReceivable.status = 'draft'
			}

			accountReceivable.groups[AccReceivableGroupParam.PERSONAL_ACCOUNT_ID.name()] = personalAccountDetails.id
			accountReceivable.postedLedger = null

			def newSave = save(accountReceivable)

			def totals = 0
			def netAmount = 0

			def rental = 0
			def electricity = 0
			def affiliationFee = 0
			def taxAmount = 0

			fieldsItems.each {
				it ->
					def arItems
					if(it['id']){
						arItems = accountReceivableItemsServices.findOne(UUID.fromString(it['id'].toString()))
					}
					else {
						arItems = new AccountReceivableItems()
					}

					entityObjectMapperService.updateFromMap(arItems,it)
					arItems.accountReceivable = newSave
					arItems.type = "OTHER"
					if(type.equalsIgnoreCase('save')) {
						arItems.status = "active"
					}
					else{
						arItems.status = "draft"
					}
					totals = totals + arItems.amount
					netAmount = netAmount + arItems.debit
					accountReceivableItemsRepository.save(arItems)

					if((it['transType'] as String).equalsIgnoreCase('ACCRUED RENTAL')){
						rental = rental + arItems.amount
					}

					if((it['transType'] as String).equalsIgnoreCase('ACCRUED ELECTRICITY')){
						electricity = electricity + arItems.amount
					}

					if((it['transType'] as String).equalsIgnoreCase('ACCRUED AFFILIATION FEE')){
						affiliationFee = affiliationFee + arItems.amount
					}

					if(arItems.cwt){
						def cwt = arItems.amount * 0.05
						taxAmount = taxAmount + cwt
					}
			}

			if(type.equalsIgnoreCase('save')){
				if(taxAmount > 0){
					Map<String, Object> ewt = new HashMap<>()
					ewt.put('refId',newSave.id)
					ewt.put('refNo',newSave.arNo)
					ewt.put('wtxDate',newSave.createdDate)
					ewt.put('type','AROTHERS') //AP, DIS, RP, AROTHERS
					ewt.put('gross',netAmount) //net of discount
					ewt.put('vatAmount',0) // 0
					ewt.put('netVat',netAmount) // same by gross
					ewt.put('ewtAmount',taxAmount) //ewt amounnt
					wtx2307Service.upsert2307(ewt, null, newSave.groups[AccReceivableGroupParam.PERSONAL_ACCOUNT_ID.name()])
				}

				newSave.electricity = electricity
				newSave.rental = rental
				newSave.affiliationFee = affiliationFee
				newSave.taxAmount = taxAmount
				newSave.totals = totals
				newSave.other = electricity + rental + affiliationFee
//				upsertJournalAR(newSave)
			}

			return newSave
		} else {
			def personalAccountDetails = supplierRepository.findById(personalAccount).get()
			def accountReceivable = new AccountReceivable()
			entityObjectMapperService.updateFromMap(accountReceivable, fields)

			if(type.equalsIgnoreCase('save')){
				accountReceivable.arNo = generatorService.getNextValue(GeneratorType.ACCOUNT_RECEIVABLE_NO, {
					return "ARNO-" + StringUtils.leftPad(it.toString(), 6, "0")
				})
				accountReceivable.referenceNo = generatorService.getNextValue(GeneratorType.BILLING_SCHEDULE_NO, {
					return "BSN-" + StringUtils.leftPad(it.toString(), 6, "0")
				})

				accountReceivable.status = 'active'
			}
			else{
				accountReceivable.status = 'draft'
			}

			accountReceivable.groups[AccReceivableGroupParam.PERSONAL_ACCOUNT_ID.name()] = personalAccountDetails.id
			accountReceivable.postedLedger = null

			def newSave = save(accountReceivable)

			def totals = 0
			def netAmount = 0

			def rental = 0
			def electricity = 0
			def affiliationFee = 0
			def taxAmount = 0

			fieldsItems.each {
				it ->
					def arItems = new AccountReceivableItems()
					entityObjectMapperService.updateFromMap(arItems,it)
					arItems.accountReceivable = newSave
					arItems.type = "OTHER"
					if(type.equalsIgnoreCase('save')) {
						arItems.status = "active"
					}
					else{
						arItems.status = "draft"
					}
					totals = totals + arItems.amount
					netAmount = netAmount + arItems.debit
					accountReceivableItemsRepository.save(arItems)

					if((it['transType'] as String).equalsIgnoreCase('ACCRUED RENTAL')){
						rental = rental + arItems.amount
					}


					if((it['transType'] as String).equalsIgnoreCase('ACCRUED ELECTRICITY')){
						electricity = electricity + arItems.amount
					}

					if((it['transType'] as String).equalsIgnoreCase('ACCRUED AFFILIATION FEE')){
						affiliationFee = affiliationFee + arItems.amount
					}

					if(arItems.cwt){
						def cwt = arItems.amount * 0.05
						taxAmount = taxAmount + cwt
					}
			}

			if(type.equalsIgnoreCase('save')){
				if(taxAmount > 0){
					Map<String, Object> ewt = new HashMap<>()
					ewt.put('refId',newSave.id)
					ewt.put('refNo',newSave.arNo)
					ewt.put('wtxDate',newSave.createdDate)
					ewt.put('type','AROTHERS') //AP, DIS, RP, AROTHERS
					ewt.put('gross',netAmount) //net of discount
					ewt.put('vatAmount',0) // 0
					ewt.put('netVat',netAmount) // same by gross
					ewt.put('ewtAmount',taxAmount) //ewt amounnt
					wtx2307Service.upsert2307(ewt, null, newSave.groups[AccReceivableGroupParam.PERSONAL_ACCOUNT_ID.name()])
				}

				newSave.electricity = electricity
				newSave.rental = rental
				newSave.affiliationFee = affiliationFee
				newSave.taxAmount = taxAmount
				newSave.totals = totals
				newSave.other = electricity + rental + affiliationFee
//				upsertJournalAR(newSave)
			}

			return newSave
		}

	}

	@GraphQLQuery(name = "accountReceivablePerGroupGuarantor")
	Page<AccountReceivable> accountReceivablePerGroupGuarantor(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "type") String type,
			@GraphQLArgument(name = "accounts") ArrayList<UUID> accounts,
			@GraphQLArgument(name = "status") ArrayList<String> status,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {
		if(accounts){
			getPageable(
					"""
              	Select c from AccountReceivable c where c.groups[:type] in (:accounts) and c.status in (:status) 
              	and 
              		(
						lower(c.arNo) like lower(concat('%',:filter,'%')) or 
						lower(c.contactNamePA) like lower(concat('%',:filter,'%')) or 
						lower(c.billingScheduleNo) like lower(concat('%',:filter,'%'))
              		) order by c.arNo desc
				""",
					"""
			 	Select count(c) from AccountReceivable c where c.groups[:type] in (:accounts)  and c.status in (:status) 
			 	and 
              		(
						lower(c.arNo) like lower(concat('%',:filter,'%')) or 
						lower(c.contactNamePA) like lower(concat('%',:filter,'%')) or 
						lower(c.billingScheduleNo) like lower(concat('%',:filter,'%'))
              		)
				""",
					page,
					size,
					[
							filter: filter,
							accounts: accounts,
							status: status,
							type:type
					]
			)
		}
		else{
			getPageable(
					"""
              	Select c from AccountReceivable c where c.groups[:type] is not null and c.status in (:status) 
              	and 
              		(
						lower(c.arNo) like lower(concat('%',:filter,'%')) or 
						lower(c.contactNamePA) like lower(concat('%',:filter,'%')) or 
						lower(c.billingScheduleNo) like lower(concat('%',:filter,'%'))
              		) order by c.arNo desc
				""",
					"""
			 	Select count(c) from AccountReceivable c where c.groups[:type] is not null and c.status in (:status)
			 	and 
              		(
						lower(c.arNo) like lower(concat('%',:filter,'%')) or 
						lower(c.contactNamePA) like lower(concat('%',:filter,'%')) or 
						lower(c.billingScheduleNo) like lower(concat('%',:filter,'%'))
              		)
				""",
					page,
					size,
					[
							filter: filter,
							type:type,
							status: status
					]
			)
		}
	}



	@GraphQLQuery(name = "accountReceivablePerCompany")
	Page<AccountReceivable> getAccountReceivablePerCompany(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "type") String type,
			@GraphQLArgument(name = "companyId") UUID companyId,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		getPageable(
				"""
              	Select c from AccountReceivable c where c.groups[:type] = :companyId  and lower(c.arNo) like lower(concat('%',:filter,'%')) order by c.arNo desc
				""",
				"""
			 	Select count(c) from AccountReceivable c where c.groups[:type] = :companyId and lower(c.arNo) like lower(concat('%',:filter,'%'))
				""",
				page,
				size,
				[
						filter: filter,
						companyId: companyId,
						type:type
				]
		)

	}

	@GraphQLQuery(name = "accountReceivablePerPersonalAccount")
	Page<AccountReceivable> accountReceivablePerPersonalAccount(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "personalAccount") UUID personalAccount,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		getPageable(
				"""
              	Select c from AccountReceivable c where c.groups['PERSONAL_ACCOUNT_ID'] = :personalAccount  and (lower(c.arNo) like lower(concat('%',:filter,'%')) or  lower(c.status) like lower(concat('%',:filter,'%'))) order by c.createdDate desc
				""",
				"""
			 	Select count(c) from AccountReceivable c where c.groups['PERSONAL_ACCOUNT_ID'] = :personalAccount and (lower(c.arNo) like lower(concat('%',:filter,'%')) or lower(c.status) like lower(concat('%',:filter,'%')))
				""",
				page,
				size,
				[
						filter: filter,
						personalAccount: personalAccount,
				]
		)

	}

	@GraphQLQuery(name = "accountReceivableManual")
	Page<AccountReceivable> getAccountReceivablePerPersonalAccount(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		getPageable(
				"""
              	Select c from AccountReceivable c where c.groups['PERSONAL_ACCOUNT_ID'] <> NULL  and (lower(c.arNo) like lower(concat('%',:filter,'%')) or lower(c.status) like lower(concat('%',:filter,'%'))) order by c.arNo desc
				""",
				"""
			 	Select count(c) from AccountReceivable c where c.groups['PERSONAL_ACCOUNT_ID'] <> NULL and (lower(c.arNo) like lower(concat('%',:filter,'%')) or lower(c.status) like lower(concat('%',:filter,'%')))
				""",
				page,
				size,
				[
						filter: filter,
				]
		)

	}

//	@GraphQLQuery(name = "getARTotalBalanceByCompany")
//	BigDecimal getARTotalBalanceByCompany(
//			@GraphQLArgument(name = "companyId") UUID companyId
//	) {
//		def total = accountReceivableRepository.getARSumBalanceByCompany(companyId)
//		return total
//	}

//	@GraphQLQuery(name = "getARTotalMemoByCompany")
//	BigDecimal getARTotalMemoByCompany(
//			@GraphQLArgument(name = "companyId") UUID companyId
//	) {
//		def total = accountReceivableRepository.getARSumMemoByCompany(companyId)
//		return total
//	}
//
//	@GraphQLQuery(name = "getARTotalPaymentByCompany")
//	BigDecimal getARTotalPaymentByCompany(
//			@GraphQLArgument(name = "companyId") UUID companyId
//	) {
//		def total = accountReceivableRepository.getARSumPaymentByCompany(companyId)
//		return total
//	}

	@GraphQLQuery(name = "findAccountReceivablePerId")
	AccountReceivable findAccountReceivablePerId(
			@GraphQLArgument(name = "accountReceivableId") UUID accountReceivableId
	)
	{
		return  accountReceivableRepository.findById(accountReceivableId).get()
	}

	@GraphQLQuery(name = "viewTransactionAccountPosted")
	HeaderLedger viewTransactionAccountPosted(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "type") String type,
			@GraphQLArgument(name = "fieldType") String fieldType,
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "fieldsItems") ArrayList<Map<String, Object>> fieldsItems
	)
	{

		def headerLedger = null
		if(type.equalsIgnoreCase("payment")) {
			def totalHci = 0
			def totalPf = 0
			fieldsItems.each {
				it ->
					def dtoAr = new ARForJournalViewDto()
					entityObjectMapperService.updateFromMap(dtoAr,it)
					def arrItem = accountReceivableItemsServices.findOne(dtoAr.accountReceivableItems)
					if(arrItem.type.equalsIgnoreCase("HCI")){
						totalHci = totalHci + dtoAr.amount
					}

					if(arrItem.type.equalsIgnoreCase("PF")){
						totalPf = totalPf + dtoAr.amount
					}
			}

			if((fieldType?:'').equalsIgnoreCase("personalAccPayments")){
				ArTransaction arTran = new ArTransaction()
				arTran.amount =  fields['amount'] as BigDecimal
				arTran.negativeAmount = arTran.getNegativeAmount()
				headerLedger =	integrationServices.generateAutoEntries(arTran){it, mul ->
					it.flagValue = AR_INTEGRATION.AR_MANUAL_PAYMENT.name()
				}

				Map<String,String> details = [:]

				arTran.details.each { k,v ->
					details[k] = v
				}

				return  headerLedger
			}
			else {
				def companyAccount = companyAccountServices.findOne(id)
				ArTransaction arTran = new ArTransaction()
				arTran.artC = -(totalHci+totalPf)
				arTran.artA = -totalHci
				arTran.artB = totalPf

				headerLedger =	integrationServices.generateAutoEntries(arTran){it, mul ->
					it.flagValue = AR_INTEGRATION.AR_PAYMENTS.name()
					it.companyAccount = companyAccount
				}

				Map<String,String> details = [:]

				arTran.details.each { k,v ->
					details[k] = v
				}

				return  headerLedger

			}
		}
		else if(type.equalsIgnoreCase("voidAccRec")){
			AccountReceivable accRec = findOne(id)
			return ledgerServices.findOne(accRec.postedLedger)
		}
		else if (type.equalsIgnoreCase("voidTransaction")){
			ArTransaction arTrans = arTransactionServices.findOne(id)

			def list =  ledgerServices.findOne(arTrans.postedLedger)
			Set<Ledger> ledger = new HashSet<Ledger>(list.ledger)

			ledger.each {
				it ->
					def d = it.debit
					def c = it.credit
					it.debit = c
					it.credit = d
			}
			return  list
		}
		else if (type.equalsIgnoreCase("arTransfer")){
			ArTransaction arTrans = new ArTransaction()
			def fieldsObject = new ArArrayDto()
			entityObjectMapperService.updateFromMap(fieldsObject,fields)
			headerLedger = integrationServices.generateAutoEntries(arTrans) {it, mul ->
				it.flagValue = AR_INTEGRATION.AR_TRANSFER.name()
//				List<ArTransaction> mulChecks  = []

				def fromArr = new ArTransferForJournalViewDto()
				entityObjectMapperService.updateFromMap(fromArr,fieldsObject.from)
				def fromCompanyAccount = companyAccountServices.findOne(fromArr.company)
				def totalHci = 0
				fieldsItems.each {
					itTrans ->
						def subItem = new ArTransactionDetails()
						entityObjectMapperService.updateFromMap(subItem, itTrans)
						def arItems = accountReceivableItemsServices.findOne(subItem.accountReceivableItems.id)
						if (arItems.type.equalsIgnoreCase("HCI")) {
							totalHci = totalHci + subItem.amount
						}
				}
				it.companyAccount = fromCompanyAccount
				it.amount = totalHci
				it.negativeAmount = -totalHci
			}
			Map<String,String> details = [:]

			arTrans.details.each { k,v ->
				details[k] = v
			}
			return headerLedger
		}
		else if (type.equalsIgnoreCase("voidTransfer")){
			def arTransfer = arTransferServices.findOne(id)
			headerLedger = integrationServices.generateAutoEntries(arTransfer.arTransaction) {it, mul ->
				it.flagValue = AR_INTEGRATION.AR_TRANSFER.name()
				it.companyAccount = arTransfer.companySourceAccount
				it.amount = arTransfer.amount
				it.negativeAmount = -arTransfer.amount
			}
			Map<String,String> details = [:]

			arTransfer.arTransaction.details.each { k,v ->
				details[k] = v
			}
			return headerLedger
		}
		else if (type.equalsIgnoreCase("otherAccountReceivable")) {
			def otherFields = new OtherARForJournalViewDto()
			entityObjectMapperService.updateFromMap(otherFields, fields)
			if (id) {
				def personalAccountDetails = supplierRepository.findById(otherFields.personalAccount).get()
				def accountReceivable = findOne(id)
				entityObjectMapperService.updateFromMap(accountReceivable, fields)

				accountReceivable.groups[AccReceivableGroupParam.PERSONAL_ACCOUNT_ID.name()] = personalAccountDetails.id
				accountReceivable.postedLedger = null

				def totals = 0
				def netAmount = 0

				def rental = 0
				def electricity = 0
				def taxAmount = 0
				def affiliationFee = 0

				fieldsItems.each {
					it ->
						def arItems
						if (it['id']) {
							def result = accountReceivableItemsServices.findOne(UUID.fromString(it['id'].toString()))
							if(result)
								arItems = result
							else
								arItems = new AccountReceivableItems()
						} else {
							arItems = new AccountReceivableItems()
						}

						entityObjectMapperService.updateFromMap(arItems, it)
						arItems.accountReceivable = accountReceivable
						arItems.type = "OTHER"

						totals = totals + arItems.amount
						netAmount = netAmount + arItems.debit

						if ((it['transType'] as String).equalsIgnoreCase('ACCRUED RENTAL')) {
							rental += arItems.amount
						}

						if ((it['transType'] as String).equalsIgnoreCase('ACCRUED ELECTRICITY')) {
							electricity += arItems.amount
						}

						if ((it['transType'] as String).equalsIgnoreCase('ACCRUED AFFILIATION FEE')) {
							affiliationFee += arItems.amount
						}

						if (arItems.cwt) {
							def cwt = arItems.amount * 0.05
							taxAmount += + cwt
						}
				}

				headerLedger = integrationServices.generateAutoEntries(accountReceivable) { it, mul ->
					it.flagValue = AR_INTEGRATION.AR_RENTAL_ELECTRICITY.name()
					it.totals = totals
					it.electricity = electricity
					it.rental = rental
					it.taxAmount = taxAmount
					it.affiliationFee = affiliationFee
					it.receivableAmt = netAmount
				}

				Map<String, String> details = [:]

				accountReceivable.details.each { k, v ->
					details[k] = v
				}

				return headerLedger
			} else {
				def personalAccountDetails = supplierRepository.findById(otherFields.personalAccount).get()
				def accountReceivable = new AccountReceivable()
				entityObjectMapperService.updateFromMap(accountReceivable, fields)

				accountReceivable.groups[AccReceivableGroupParam.PERSONAL_ACCOUNT_ID.name()] = personalAccountDetails.id
				accountReceivable.postedLedger = null

				def totals = 0
				def netAmount = 0

				def rental = 0
				def electricity = 0
				def affiliationFee = 0
				def taxAmount = 0

				fieldsItems.each {
					it ->
						def arItems = new AccountReceivableItems()
						entityObjectMapperService.updateFromMap(arItems, it)
						arItems.accountReceivable = accountReceivable
						arItems.type = "OTHER"

						totals = totals + arItems.amount
						netAmount = netAmount + arItems.debit

						if ((it['transType'] as String).equalsIgnoreCase('ACCRUED RENTAL')) {
							rental += arItems.amount
						}


						if ((it['transType'] as String).equalsIgnoreCase('ACCRUED ELECTRICITY')) {
							electricity += arItems.amount
						}

						if ((it['transType'] as String).equalsIgnoreCase('ACCRUED AFFILIATION FEE')) {
							affiliationFee += arItems.amount
						}

						if (arItems.cwt) {
							def cwt = arItems.amount * 0.05
							taxAmount = taxAmount + cwt
						}
				}

				headerLedger = integrationServices.generateAutoEntries(accountReceivable) { it, mul ->
					it.flagValue = AR_INTEGRATION.AR_RENTAL_ELECTRICITY.name()
					it.totals = totals
					it.electricity = electricity
					it.affiliationFee = affiliationFee
					it.rental = rental
					it.taxAmount = taxAmount
					it.receivableAmt = netAmount
				}

				Map<String, String> details = [:]

				accountReceivable.details.each { k, v ->
					details[k] = v
				}

				return headerLedger

			}
		}
		else if (type.equalsIgnoreCase('memo')){
			ArMemoArrayDto memoDto = new ArMemoArrayDto()
			def upsertFields = entityObjectMapperService.updateFromMap(memoDto,fields)
			def transType = arTransactionTypeServices.findOne(upsertFields.arTransactionType)
			def arTrans  = new ArTransaction()
			def arAcc = findOne(upsertFields.accountReceivable)
			arTrans.amount = upsertFields.amount

			headerLedger = integrationServices.generateAutoEntries(arTrans){it,mul ->
				it.flagValue = transType.integration.flagValue
				it.companyAccount = new CompanyAccount()
				it.personalAccount = new Supplier()
				arTrans.companyAmt = 0
				arTrans.personalAmt = 0

				if(arAcc.groups[AccReceivableGroupParam.COMPANY_ACCOUNT_ID.name()]){
					def comp = companyAccountServices.findOne(arAcc.groups[AccReceivableGroupParam.COMPANY_ACCOUNT_ID.name()])
					if(comp)
						it.companyAccount = comp

					arTrans.companyAmt = -upsertFields.amount
				}
				if(arAcc.groups[AccReceivableGroupParam.PERSONAL_ACCOUNT_ID.name()]){
					def supp = supplierService.getSupplier(arAcc.groups[AccReceivableGroupParam.PERSONAL_ACCOUNT_ID.name()].toString())
					if(supp)
						it.personalAccount = supp

					arTrans.personalAmt = -upsertFields.amount
				}
			}

			Map<String,String> details = [:]

			arTrans.details.each { k,v ->
				details[k] = v
			}

			return  headerLedger
		}
		else{
			def billS = billingScheduleServices.findOne(id)
			AccountReceivable accRec = new AccountReceivable()
			if(billS.hci){
				def companyAccount = companyAccountServices.findOne(billS.companyAccount.id)
				if(billS.hciWithDebit > 0){
					headerLedger =	integrationServices.generateAutoEntries(billS){it, mul ->
						it.flagValue = AR_INTEGRATION.AR_POSTING_WITH_ADJUSTMENT.name()
						it.companyAccount = companyAccount

						it.totalHciWithDebit = billS.hci+billS.hciWithDebit
						it.hci = -billS.hci
					}
				}
				else {
					headerLedger =	integrationServices.generateAutoEntries(accRec){it, mul ->
						it.flagValue = AR_INTEGRATION.AR_POSTING.name()
						it.companyAccount = companyAccount
						it.hci = billS.hci
						it.negativeHciAmount = -billS.hci

					}
				}

				Map<String,String> details = [:]

				accRec.details.each { k,v ->
					details[k] = v
				}
			}



			return  headerLedger
		}


	}

	@GraphQLQuery(name = "viewARTransactionJournal")
	List<JournalEntryViewDto> viewARTransactionJournal(
			@GraphQLArgument(name = "accRecId") UUID accRecId,
			@GraphQLArgument(name = "type") String type,
			@GraphQLArgument(name = "amount") BigDecimal amount,
			@GraphQLArgument(name = "hci") BigDecimal hci,
			@GraphQLArgument(name = "pf") BigDecimal pf
	)
	{
		if(type.equalsIgnoreCase("personalAccPayments")){
			ArTransaction arTran = new ArTransaction()
			arTran.negativeAmount = -amount

			def headerLedger =	integrationServices.generateAutoEntries(arTran){it, mul ->
				it.flagValue = AR_INTEGRATION.AR_MANUAL_PAYMENT.name()
			}

			Map<String,String> details = [:]

			arTran.details.each { k,v ->
				details[k] = v
			}

			List<JournalEntryViewDto> journalDtoList = new ArrayList<JournalEntryViewDto>()
			Set<Ledger> ledger = new HashSet<Ledger>(headerLedger.ledger)
			ledger.each {
				it ->
					def list = new JournalEntryViewDto(
							code:it.journalAccount.code,
							desc:it.journalAccount.description,
							debit:it.debit,
							credit:it.credit
					)
					journalDtoList.add(list)
			}
			return  journalDtoList
		}
		else {
			AccountReceivable accRec = findOne(accRecId)
			def companyAccount = companyAccountServices.findOne(accRec.groups[AccReceivableGroupParam.COMPANY_ACCOUNT_ID.name()])
			ArTransaction arTran = new ArTransaction()
			arTran.artC = -amount
			arTran.artA = -hci
			arTran.artB = pf

			def headerLedger =	integrationServices.generateAutoEntries(arTran){it, mul ->
				it.flagValue = AR_INTEGRATION.AR_PAYMENTS.name()
				it.companyAccount = companyAccount
			}

			Map<String,String> details = [:]

			arTran.details.each { k,v ->
				details[k] = v
			}

			List<JournalEntryViewDto> journalDtoList = new ArrayList<JournalEntryViewDto>()
			Set<Ledger> ledger = new HashSet<Ledger>(headerLedger.ledger)

			ledger.each {
				it ->
					def list = new JournalEntryViewDto(
							code:it.journalAccount.code,
							desc:it.journalAccount.description,
							debit:it.debit,
							credit:it.credit
					)
					journalDtoList.add(list)
			}
			return  journalDtoList
		}
	}

	AccountReceivable upsertJournalAR(AccountReceivable accountReceivable,Instant journalDate){
		def yearFormat = DateTimeFormatter.ofPattern("yyyy")
		def accRec = super.save(accountReceivable) as AccountReceivable

		if(accRec.status == 'voided')
		{
			def header = ledgerServices.findOne(accRec.postedLedger)
			ledgerServices.reverseEntries(header)
			arLedgerServices.voidArLedger(accRec.id)
			return accRec
		}
		else{
			def billScheduleId = accRec.groups[AccReceivableGroupParam.BILLING_SCHEDULE_ID.name()]
			if(billScheduleId){
				def billSchedule = billingScheduleRepository.findById(billScheduleId).get()
				if(billSchedule.hciWithDebit > 0){
					def accountsLabel = null
					def headerLedger =	integrationServices.generateAutoEntries(billSchedule){it, nul ->
						def companyAccount = companyAccountServices.findOne(accRec.groups[AccReceivableGroupParam.COMPANY_ACCOUNT_ID.name()])
						it.flagValue = AR_INTEGRATION.AR_POSTING_WITH_ADJUSTMENT.name()
						it.companyAccount = companyAccount
						it.totalHciWithDebit = it.hci+it.hciWithDebit
						it.hci = -it.hci
						accountsLabel = companyAccount.companyname
					}

					Map<String,String> details = [:]
					accRec.details.each { k,v ->
						details[k] = v
					}

					Date dateTime = accRec.transactionDate

					def transactionDate
					if(journalDate){
						transactionDate = journalDate
					}
					else{
						transactionDate = dateToInstantConverter(dateTime)
					}

					details["ACC_RECEIVABLE_ID"] = accRec.id.toString()
					def pHeader =	ledgerServices.persistHeaderLedger(headerLedger,
							"${transactionDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${accRec.arNo}",
							"${accRec.arNo}-${accountsLabel}",
							"${accRec.arNo}-${accountsLabel}",
							LedgerDocType.JV,
							JournalType.GENERAL,
							transactionDate,
							details)
					accRec.postedLedger = pHeader.id
					//	ADD TO AR LEDGER

					save(accRec)
					arLedgerServices.debitArLedgerFromAr(accRec)
				}
				else {
					def accountsLabel = null
					def headerLedger =	integrationServices.generateAutoEntries(accRec){it, nul ->
						def companyAccount = companyAccountServices.findOne(it.groups[AccReceivableGroupParam.COMPANY_ACCOUNT_ID.name()])
						it.flagValue = AR_INTEGRATION.AR_POSTING.name()
						it.companyAccount = companyAccount
						accountsLabel = companyAccount.companyname

					}

					Map<String,String> details = [:]
					accRec.details.each { k,v ->
						details[k] = v
					}

					Date dateTime = accRec.transactionDate

					def transactionDate
					if(journalDate){
						transactionDate = journalDate
					}
					else{
						transactionDate = dateToInstantConverter(dateTime)
					}

					details["ACC_RECEIVABLE_ID"] = accRec.id.toString()
					def pHeader =	ledgerServices.persistHeaderLedger(headerLedger,
							"${transactionDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${accRec.arNo}",
							"${accRec.arNo}-${accountsLabel}",
							"${accRec.arNo}-${accountsLabel}",
							LedgerDocType.JV,
							JournalType.GENERAL,
							transactionDate,
							details)
					accRec.postedLedger = pHeader.id
					//	ADD TO AR LEDGER
					save(accRec)
					arLedgerServices.debitArLedgerFromAr(accRec)
				}

			}
			else {
				def accountsLabel = null
				if(accRec.electricity > 0 || accRec.rental > 0 || accRec.affiliationFee > 0){

					def headerLedger =	integrationServices.generateAutoEntries(accRec){it, nul ->
						def personal = supplierRepository.findById(it.groups[AccReceivableGroupParam.PERSONAL_ACCOUNT_ID.name()]).get()
						it.flagValue = AR_INTEGRATION.AR_RENTAL_ELECTRICITY.name()
						it.receivableAmt = it.other - it.taxAmount
						accountsLabel = personal.description
					}

					Map<String,String> details = [:]
					accRec.details.each { k,v ->
						details[k] = v
					}

					Date dateTime = accRec.transactionDate

					def transactionDate
					if(journalDate){
						transactionDate = journalDate
					}
					else{
						transactionDate = dateToInstantConverter(dateTime)
					}

					details["ACC_RECEIVABLE_ID"] = accRec.id.toString()
					def pHeader =	ledgerServices.persistHeaderLedger(headerLedger,
							"${transactionDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${accRec.arNo}",
							"${accRec.arNo}-${accountsLabel}",
							"${accRec.arNo}-${accountsLabel}",
							LedgerDocType.JV,
							JournalType.GENERAL,
							transactionDate,
							details)
					accRec.postedLedger = pHeader.id
					//	ADD TO AR LEDGER
					save(accRec)
					arLedgerServices.debitArLedgerFromAr(accRec)
				}
			}

			return  accRec
		}
	}

	@GraphQLQuery(name = "getARCompanyDetailsBySchedule")
	AccountReceivable getARCompanyDetailsBySchedule(@GraphQLArgument(name = "billingScheduleId") UUID billingScheduleId

	) {
		createQuery("""
                    select b from AccountReceivable b where b.groups['BILLING_SCHEDULE_ID'] = :billingScheduleId
            """,
				[
						billingScheduleId: billingScheduleId,
				] as Map<String, Object>).singleResult
	}

	@GraphQLQuery(name = "getARCompanyDetails")
	List<AccountReceivable> getARCompanyDetails(@GraphQLArgument(name = "companyID") UUID companyID

	) {
		createQuery("""
                    select b from AccountReceivable b where b.balance > 0 and  b.groups['COMPANY_ACCOUNT_ID'] = :companyID and b.status <> 'voided' order by b.arNo asc
            """,
				[
						companyID: companyID,
				] as Map<String, Object>).resultList
	}

//	@GraphQLQuery(name = "checkBillingItemPosted")
//	AccountReceivable checkBillingItemPosted(@GraphQLArgument(name = "billingItemId") UUID billingItemId) {
//		def accountReceivableItem = accountReceivableItemsRepository.checkBillingItemPosted(billingItemId)
//		def result = new AccountReceivable()
//		if(accountReceivableItem?.accountReceivable?.id){
//			result = accountReceivableRepository.findById(accountReceivableItem.accountReceivable.id).get()
//		}
//		return result
//	}

	static ChartOfAccountGenerate createSampleCoa(@GraphQLArgument(name = "journal") ChartOfAccountGenerate journal){
		ChartOfAccountGenerate coa = new ChartOfAccountGenerate()
		coa.motherAccount = journal.motherAccount
		if(journal.subAccount){
			coa.subAccount = new CoaComponentContainer(journal.subAccount.code,
					journal.subAccount.id,
					journal.subAccount.description,
					journal.subAccount.domain,
					"")
		}
		else{
			coa.subAccount = new CoaComponentContainer("",null,
					"",
					"",
					"")
		}

		if(journal.subSubAccount){
			coa.subSubAccount = new CoaComponentContainer(journal.subSubAccount.code,
					journal.subSubAccount.id,
					journal.subSubAccount.description,
					journal.subSubAccount.domain,
					"")
		}
		else{
			coa.subSubAccount = new CoaComponentContainer("",null,
					"",
					"",
					"")
		}

		return coa

	}

	@GraphQLQuery(name = "viewARListDetails")
	List<Map<String, List<JournalEntryViewDto>>> viewARListDetails(@GraphQLArgument(name = "id") UUID id){
		List<Map<String, List<JournalEntryViewDto>>> ledgerList = new ArrayList<Map<String, List<JournalEntryViewDto>>>()

		if(id){

			AccountReceivable accRec = findOne(id)

			def arrPostingHeader = ledgerServices.findOne(accRec.postedLedger)
			Map<String, List<JournalEntryViewDto>> arPostingList = new HashMap<String ,List<JournalEntryViewDto>>()
			List<JournalEntryViewDto> postDtoList = new ArrayList<JournalEntryViewDto>()
			Set<Ledger> ledger = new HashSet<Ledger>(arrPostingHeader.ledger)

			ledger.each {
				it ->
					def list = new JournalEntryViewDto(
							code:it.journalAccount.code,
							desc:it.journalAccount.description,
							debit:it.debit,
							credit:it.credit
					)
					postDtoList.add(list)
			}
			arPostingList.put(accRec.arNo,postDtoList)
			ledgerList.add(arPostingList)

			if(accRec.arTransactions){
				accRec.arTransactions.each {
					art ->
						if(art.status.equalsIgnoreCase("ACTIVE")){
							Map<String, List<JournalEntryViewDto>> arTransList = new HashMap<String ,List<JournalEntryViewDto>>()
							List<JournalEntryViewDto> transDtoList = new ArrayList<JournalEntryViewDto>()
							if(art.postedLedger){
								def arrTransHeader = ledgerServices.findOne(art.postedLedger)
								Set<Ledger> artLedger = new HashSet<Ledger>(arrTransHeader.ledger)

								artLedger.each {
									itArr ->
										def list = new JournalEntryViewDto(
												code:itArr.journalAccount.code,
												desc:itArr.journalAccount.description,
												debit:itArr.debit,
												credit:itArr.credit
										)
										transDtoList.add(list)
								}
								arTransList.put(art.trackingNo,transDtoList)
								ledgerList.add(arTransList)
							}
						}
				}
			}
		}

		return  ledgerList
	}

	@Transactional
	@GraphQLMutation
	String autoJournalEntry(
			@GraphQLArgument(name = "type") String type,
			@GraphQLArgument(name = "journalDate") Date journalDate,
			@GraphQLArgument(name = "journalAccounts") ArrayList<Map<String, Object>> journalAccounts,
			@GraphQLArgument(name = "fieldsItems") ArrayList<Map<String, Object>> fieldsItems,
			@GraphQLArgument(name = "dataEntry") Map<String, Object> dataEntry
	) {
		if (type.equalsIgnoreCase('posting') || type.equalsIgnoreCase('voidAccRec')) {
			def auto = new AccRecDto()
			entityObjectMapperService.updateFromMap(auto, dataEntry)
			def newSave = upsertCompanyAR(auto?.accRecId, auto?.fields, auto?.billingScheduleId)

			if(newSave){
				if (journalDate) {
					upsertJournalAR(newSave, journalDate.toInstant())
				} else {
					upsertJournalAR(newSave, null)
				}
				return 'Success'
			}
			else{
				return 'Error'
			}
		}

		if (type.equalsIgnoreCase('otherAccountReceivable')) {
			def auto = new AccRecOtherDto()
			entityObjectMapperService.updateFromMap(auto, dataEntry)
			def newSave = upsertPersonalAccountAR(auto?.accRecId, auto.type, auto.personalAccount, auto.fields, fieldsItems)

			if (auto.type.equalsIgnoreCase('save')) {
				if (journalDate) {
					upsertJournalAR(newSave, journalDate.toInstant())
				} else {
					upsertJournalAR(newSave, null)
				}
			}
			return 'Success'
		}



	}


	@Transactional
	@GraphQLMutation
	AccountReceivable editedJournalEntry(
			@GraphQLArgument(name = "type") String type,
			@GraphQLArgument(name = "journalAccounts") ArrayList<Map<String, Object>> journalAccounts,
			@GraphQLArgument(name = "fieldsItems") ArrayList<Map<String, Object>> fieldsItems,
			@GraphQLArgument(name = "dataEntry") Map<String, Object> dataEntry
	) {
		def debit = 0
		def credit = 0
		journalAccounts.each {
			ja ->
				def journalAuth = new EditedJournalEntryDto()
				entityObjectMapperService.updateFromMap(journalAuth, ja)
				debit = debit + journalAuth.debit
				credit = credit + journalAuth.credit
		}

		if(debit == credit){

			if(type.equalsIgnoreCase('posting')){
				def edited = new AccRecDto()
				entityObjectMapperService.updateFromMap(edited, dataEntry)
				def newSave = upsertCompanyAR(edited.accRecId,edited.fields,edited.billingScheduleId)

				//			JOURNAL ENTRY
				def companyId = newSave.groups[AccReceivableGroupParam.COMPANY_ACCOUNT_ID.name()]
				def company = companyAccountServices.findOne(companyId)
				def transDate = dateToInstantConverter(newSave.transactionDate)
				def ledger = insertAccRecManualLedger(company,null,newSave,journalAccounts,transDate)
				newSave.postedLedger = ledger
				save(newSave)
				return  newSave
			}

			if(type.equalsIgnoreCase('otherAccountReceivable')) {
				def edited = new AccRecOtherDto()
				entityObjectMapperService.updateFromMap(edited, dataEntry)
				def newSave = upsertPersonalAccountAR(edited?.accRecId,edited.type,edited.personalAccount,edited.fields,fieldsItems)

				//			JOURNAL ENTRY
				def personal = supplierRepository.findById(newSave.groups[AccReceivableGroupParam.PERSONAL_ACCOUNT_ID.name()]).get()
				def transDate = dateToInstantConverter(newSave.transactionDate)
				def ledger = insertAccRecManualLedger(null,personal,newSave,journalAccounts,transDate)
				newSave.postedLedger = ledger
				save(newSave)
				return  newSave
			}

			if(type.equalsIgnoreCase('payment') || type.equalsIgnoreCase('arTransfer') || AR_TRANS_TYPE.memo.name() == type) {
				def edited = new AccRecPaymentTransferDto()
				entityObjectMapperService.updateFromMap(edited, dataEntry)
				def newSave = arTransactionServices.addARTransaction(edited.type,edited.fields,fieldsItems,edited.entries)

				//			JOURNAL ENTRY
				if(newSave.companyAccount){
					def company = newSave.companyAccount
					def ledger = insertArTransManualLedger(company,null,newSave,journalAccounts,newSave.createdDate,type)
					newSave.postedLedger = ledger
					arTransactionRepository.save(newSave)
				}
				if(newSave.personalAccount) {
					def supplier = newSave.personalAccount
					def ledger = insertArTransManualLedger(null,supplier,newSave,journalAccounts,newSave.createdDate,type)
					newSave.postedLedger = ledger
					arTransactionRepository.save(newSave)
				}
				AccountReceivable acc = new AccountReceivable()
				return  acc

			}

		}
		else{
			return  null
		}


	}

	@GraphQLMutation
	UUID insertAccRecManualLedger(
			@GraphQLArgument(name = "companyAccount") CompanyAccount companyAccount,
			@GraphQLArgument(name = "supplier") Supplier supplier,
			@GraphQLArgument(name = "accountReceivable") AccountReceivable accountReceivable,
			@GraphQLArgument(name = "journalAccounts") ArrayList<Map<String, Object>> journalAccounts,
			@GraphQLArgument(name = "createdDate") Instant createdDate

	) {

//			JOURNAL ENTRY
		def accRec = super.save(accountReceivable) as AccountReceivable
		Map<String, Object> header = new HashMap<String ,Object>()
		def yearFormat = DateTimeFormatter.ofPattern("yyyy")
		header.put("invoiceSoaReference","${accRec.createdDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${accRec.arNo}")

		if(supplier){
			header.put("entityName","${accRec.arNo}-${supplier.supplierFullname}")
			header.put("particulars","${accRec.arNo}- POSTING")
		}

		if(companyAccount){
			header.put("entityName","${accRec.arNo}-${companyAccount.companyname}")
			header.put("particulars","${accRec.arNo}- POSTING")
		}

		Map<String,String> details = [:]
		accRec.details.each { k,v ->
			details[k] = v
		}
		details["ACC_RECEIVABLE_ID"] = accRec.id.toString()
		def transDate = dateToInstantConverter(accRec.transactionDate)

		def pHeader = ledgerServices.addManualJVDynamic(header,journalAccounts,LedgerDocType.JV,JournalType.GENERAL,transDate,details)
		return  pHeader.returnId
	}


	@GraphQLMutation
	UUID insertArTransManualLedger(
			@GraphQLArgument(name = "companyAccount") CompanyAccount companyAccount,
			@GraphQLArgument(name = "supplier") Supplier supplier,
			@GraphQLArgument(name = "arTransaction") ArTransaction arTransaction,
			@GraphQLArgument(name = "journalAccounts") ArrayList<Map<String, Object>> journalAccounts,
			@GraphQLArgument(name = "createdDate") Instant createdDate,
			@GraphQLArgument(name = "type") String type


	) {

//			JOURNAL ENTRY
		def accTrans = super.save(arTransaction) as ArTransaction
		Map<String, Object> header = new HashMap<String ,Object>()
		def yearFormat = DateTimeFormatter.ofPattern("yyyy")
		header.put("invoiceSoaReference","${accTrans.createdDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${accTrans.trackingNo}")
		if(supplier){
			header.put("entityName","${accTrans.trackingNo}-${supplier.supplierFullname}")
			if(type.equalsIgnoreCase('payment')){
				header.put("particulars","${accTrans.trackingNo}- PAYMENTS")
			}
			else if(type.equalsIgnoreCase('arTransfer')){
				header.put("particulars","${accTrans.trackingNo}- TRANSFER")
			}
			else if(AR_TRANS_TYPE.memo.name() == type){
				header.put("particulars","${accTrans.trackingNo}- CREDIT MEMO")
			}
		}

		if(companyAccount){
			header.put("entityName","${accTrans.trackingNo}-${companyAccount.companyname}")
			if(type.equalsIgnoreCase('payment')){
				header.put("particulars","${accTrans.trackingNo}- PAYMENTS")
			}
			else if(type.equalsIgnoreCase('arTransfer')){
				header.put("particulars","${accTrans.trackingNo}- TRANSFER")
			}
			else if(AR_TRANS_TYPE.memo.name() == type){
				header.put("particulars","${accTrans.trackingNo}- CREDIT MEMO")
			}
		}

		Map<String,String> details = [:]
		accTrans.details.each { k,v ->
			details[k] = v
		}
		details["AR_TRANSACTION_ID"] = accTrans.id.toString()

		BigDecimal jaTotalAmt = 0.00
		journalAccounts.each {
			ja->
				jaTotalAmt += ja['debit'] as BigDecimal
		}

		if(jaTotalAmt == accTrans.amount) {
			def pHeader = ledgerServices.addManualJVDynamic(header, journalAccounts, LedgerDocType.JV, JournalType.GENERAL, createdDate, details)
			return pHeader.returnId
		}
		return null

	}

	Instant dateToInstantConverter(Date dateTime){
		LocalDateTime myDateTime = LocalDateTime.now()
		DateTimeFormatter hourFormat = DateTimeFormatter.ofPattern("HH");
		DateTimeFormatter minutesFormat = DateTimeFormatter.ofPattern("mm");
		SimpleDateFormat yr = new SimpleDateFormat("yyyy")
		SimpleDateFormat mth = new SimpleDateFormat("MM")
		SimpleDateFormat dy = new SimpleDateFormat("dd")

		int year = Integer.parseInt(yr.format(dateTime).toString())
		int month = Integer.parseInt(mth.format(dateTime).toString())
		int date = Integer.parseInt(dy.format(dateTime).toString())
		Integer hour = Integer.parseInt(hourFormat.format(myDateTime))
		Integer minutes = Integer.parseInt(minutesFormat.format(myDateTime))
		Calendar cl = Calendar.getInstance()
		cl.set(year,month-1,date)
		TimeZone philZone = TimeZone.getTimeZone('UTC')
		cl.setTimeZone(philZone)
		return cl.getTime().toInstant()

	}

	@GraphQLQuery(name = "getAccRecYear")
	List<String> getAccRecYear(@GraphQLArgument(name = "companyId") UUID companyId
	) {
		return accountReceivableRepository.getARYearListByCompany(companyId)
	}

	@GraphQLQuery(name= 'getAllReceivableAccounts')
	List<MergeReceivableAccount> getAllReceivableAccounts(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "type") String type,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	)
	{
		List<MergeReceivableAccount> mergeAcc = new ArrayList<MergeReceivableAccount>()
		if(type.equalsIgnoreCase('guarantors')){
			def companyAcc = companyAccountServices.companyAccounts(filter,page,size)
			if(companyAcc.content){
				companyAcc.content.each {
					it ->
						def sub = new MergeReceivableAccount()
						sub.id = it.id
						sub.accountName = it.companyname
						mergeAcc.push(sub)
				}
			}
		}
		else{
			def suppAcc =  supplierService.allSupplierPageable(filter,page,size)
			if(suppAcc.content){
				suppAcc.content.each {
					it ->
						def sub = new MergeReceivableAccount()
						sub.id = it.id
						sub.accountName = it.supplierFullname
						mergeAcc.push(sub)
				}
			}
		}
		return mergeAcc
	}

	@GraphQLQuery(name = "viewTransactionJournalEntry")
	List<JournalEntryViewDto> viewTransactionJournalEntry(@GraphQLArgument(name = "id") UUID id){
		List<JournalEntryViewDto> transDtoList = new ArrayList<JournalEntryViewDto>()

		if(id){

			def arrTransHeader = ledgerServices.findOne(id)
			Set<Ledger> artLedger = new HashSet<Ledger>(arrTransHeader.ledger)

			artLedger.each {
				itArr ->
					def list = new JournalEntryViewDto(
							code:itArr.journalAccount.code,
							desc:itArr.journalAccount.description,
							debit:itArr.debit,
							credit:itArr.credit
					)
					transDtoList.add(list)
			}
		}

		return  transDtoList.sort{it.credit}
	}


	@GraphQLMutation
	AccountReceivable deleteAccountReceivable(
			@GraphQLArgument(name = "id") UUID id
	) {
		def acc = null
		if(id){
			acc = accountReceivableRepository.findById(id).get()
			if(!acc.arNo){
				accountReceivableRepository.delete(acc)
			}
		}
		return acc
	}
}
