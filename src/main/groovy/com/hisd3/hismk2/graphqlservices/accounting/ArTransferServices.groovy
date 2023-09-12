package com.hisd3.hismk2.graphqlservices.accounting


import com.hisd3.hismk2.domain.accounting.AccReceivableGroupParam
import com.hisd3.hismk2.domain.accounting.AccountReceivable
import com.hisd3.hismk2.domain.accounting.AccountReceivableItems
import com.hisd3.hismk2.domain.accounting.ArLedger
import com.hisd3.hismk2.domain.accounting.ArTransaction
import com.hisd3.hismk2.domain.accounting.ArTransactionDetails
import com.hisd3.hismk2.domain.accounting.ArTransfer
import com.hisd3.hismk2.domain.accounting.BillingScheduleItems
import com.hisd3.hismk2.domain.accounting.JournalType
import com.hisd3.hismk2.domain.accounting.LedgerDocType
import com.hisd3.hismk2.domain.billing.BillingItem
import com.hisd3.hismk2.domain.billing.BillingItemStatus
import com.hisd3.hismk2.domain.billing.BillingItemType
import com.hisd3.hismk2.domain.inventory.Inventory
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.billing.BillingItemServices
import com.hisd3.hismk2.graphqlservices.billing.CompanyAccountServices
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.accounting.AccountReceivableItemsRepository
import com.hisd3.hismk2.repository.accounting.ArTransactionDetailsRepository
import com.hisd3.hismk2.repository.inventory.SupplierRepository
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

import java.time.Duration
import java.time.Instant

@Component
@GraphQLApi
class ArTransferServices extends AbstractDaoService<ArTransfer> {

	ArTransferServices() {
		super(ArTransfer.class)
	}

	@Autowired
	EntityObjectMapperService entityObjectMapperService

	@Autowired
	ArTransactionDetailsServices arTransactionDetailsServices

	@Autowired
	ArTransactionDetailsRepository arTransactionDetailsRepository

	@Autowired
	AccountReceivableItemsServices accountReceivableItemsServices

	@Autowired
	AccountReceivableItemsRepository accountReceivableItemsRepository

	@Autowired
	BillingItemServices billingItemServices

	@Autowired
	ArLedgerServices arLedgerServices

	@Autowired
	IntegrationServices integrationServices

	@Autowired
	LedgerServices ledgerServices

	@Autowired
	BillingScheduleItemsServices billingScheduleItemsServices

	@Autowired
	GeneratorService generatorService

	@GraphQLQuery(name = "getAllArTransfer")
	Page<ArTransfer> getAllArTransfer(
			@GraphQLArgument(name = "companyId") UUID companyId,
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {
		//language=HQL
		getPageable(
				"""
			 	select ar from ArTransfer ar LEFT JOIN BillingScheduleItems bs on ar.id = bs.arTransfer.id and (bs.isVoided = FALSE OR bs.isVoided IS NULL)
			 	where bs.billingItem.id is null and ar.companyAccount.id = :companyId
			 	and  ar.status = 'active' and 
				(
					lower(ar.billing.patient.fullName) like concat('%', :filter ,'%')
			  	)
				order by ar.createdDate desc
				""", """
				select count(ar) from ArTransfer ar LEFT JOIN BillingScheduleItems bs on ar.id = bs.arTransfer.id and (bs.isVoided = FALSE OR bs.isVoided IS NULL)
			 	where bs.billingItem.id is null and ar.companyAccount.id = :companyId 
			 	and ar.status = 'active' and 
				(
					lower(ar.billing.patient.fullName) like concat('%', :filter ,'%')
			  	)
				""",
				page,
				size,
				[
						companyId: companyId,
						filter   : filter
				]

		)

	}

	@GraphQLQuery(name = "showAllArTransfer")
	Page<ArTransfer> showAllArTransfer(
			@GraphQLArgument(name = "sourceAcc") ArrayList<UUID> sourceAcc,
			@GraphQLArgument(name = "destinationAcc") ArrayList<UUID> destinationAcc,
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "status") String status,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		Map<String, Object> params = new HashMap<>()
		params.put('filter',filter)
		params.put('status',BillingItemStatus.ACTIVE)

		String query = """
			 	select ar from ArTransfer ar where 
			 	ar.arTransactionDetails.billingItemRef.status = :status and
				(
					lower(ar.billing.patient.fullName) like concat('%', :filter ,'%') or
					lower(ar.arTransaction.trackingNo) like concat('%', :filter ,'%')
			  	)
				"""
		String countQuery = """
				select count(ar) from ArTransfer ar where 
				ar.arTransactionDetails.billingItemRef.status = :status and
				(
					lower(ar.billing.patient.fullName) like concat('%', :filter ,'%') or
					lower(ar.arTransaction.trackingNo) like concat('%', :filter ,'%')
			  	)
				"""

		if (sourceAcc) {
			query += ''' and (ar.companySourceAccount.id in (:sourceAcc))'''
			countQuery += ''' and (ar.companySourceAccount.id in (:sourceAcc))'''
			params.put("sourceAcc", sourceAcc)
		}

		if(destinationAcc){
			query += ''' and (ar.companyAccount.id in (:destinationAcc))'''
			countQuery += ''' and (ar.companyAccount.id in (:destinationAcc))'''
			params.put("destinationAcc", destinationAcc)
		}

		if(status){
			switch (status){
				case('BILLED'):
					query += ''' and size(ar.billingScheduleItems) >= 1'''
					countQuery += ''' and size(ar.billingScheduleItems)  >= 1'''
				break;
				default:
					query += ''' and size(ar.billingScheduleItems) < 1'''
					countQuery += ''' and size(ar.billingScheduleItems)  < 1'''
				break;
			}
		}

		query += """ order by ar.createdDate desc"""

		getPageable(
				query, countQuery,
				page,
				size,
				params
		)

	}

	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation
	ArTransfer voidTransfer(
			@GraphQLArgument(name = "arTransId") UUID arTransId
	) {

		def transfer = findOne(arTransId)
		if(transfer.status.equalsIgnoreCase('active')) {
			// TRANSACTION DETAILS
			ArTransactionDetails arDetails = transfer.arTransactionDetails
			arDetails.isVoided = true
			arTransactionDetailsRepository.save(arDetails)

			//		RECEIVABLE ITEMS
			AccountReceivableItems accRec = transfer.arTransactionDetails.accountReceivableItems
			accRec.debit = accRec.debit + transfer.amount
			accountReceivableItemsRepository.save(accRec)

			// AR LEDGER
			ArLedger arLedger = new ArLedger()
			arLedger.reference = accRec.accountReceivable.arNo
			arLedger.ledgerNo = generatorService.getNextValue(GeneratorType.AR_LEDGER_NO, {
				return StringUtils.leftPad(it.toString(), 6, "0")
			})
			arLedger.accountReceivable = accRec.accountReceivable
			def account
			arLedger.companyAccount = transfer.companySourceAccount
			account = transfer.companySourceAccount.companyname
			arLedger.debit = transfer.amount
			arLedger.credit = 0
			arLedger.balance = accRec.accountReceivable.totals + transfer.amount
			arLedger.description = "${accRec.accountReceivable.arNo} - ${account}"
			arLedger.status = 'active'
			def ledger = arLedgerServices.arLedgerSave(arLedger)

			// JOURNAL ENTRY
			def header = integrationServices.generateAutoEntries(transfer.arTransaction) { it, mul ->
				it.flagValue = AR_INTEGRATION.AR_TRANSFER.name()
				it.companyAccount = transfer.companySourceAccount
				it.amount = -transfer.amount
				it.negativeAmount = transfer.amount
			}
			Map<String, String> details = [:]

			transfer.arTransaction.details.each { k, v ->
				details[k] = v
			}

			def pHeader = ledgerServices.persistHeaderLedger(header,
					"${transfer.arTransaction.trackingNo}",
					"${transfer.arTransaction.trackingNo}-${transfer.companySourceAccount.companyname}-reverse",
					"${transfer.arTransaction.trackingNo}-${transfer.companySourceAccount.companyname}-reverse",
					LedgerDocType.JV,
					JournalType.GENERAL,
					ledger.createdDate,
					details)
			arLedger.journalLedger = pHeader.id
			arLedgerServices.arLedgerSave(arLedger)

			// BILLING ITEM
			def billingItemSource = transfer.arTransactionDetails.billingItemSource
			if(billingItemSource){
				billingItemServices.toggleBillingItem(billingItemSource.id.toString())
			}
			def billingItem = transfer.arTransactionDetails.billingItemRef
			if(billingItem){
				billingItemServices.toggleBillingItem(billingItem.id.toString())
			}
			// TRANSFER
			transfer.status = 'inactive'
			save(transfer)
		}
		return transfer

	}

	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation
	ArTransfer upsertTransfer(
			@GraphQLArgument(name = "arTransfer") ArTransfer arTransfer
	) {
		def newSave = null
		if(arTransfer){
			newSave = save(arTransfer)
		}
		return newSave
	}

	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation
	ArTransfer updateTransferBillingItem(
			@GraphQLArgument(name = "id") UUID id
	) {
		def transfer = findOne(id)
		if(id){
			billingItemServices.toggleBillingItem(transfer.arTransactionDetails.billingItemRef.id.toString())
		}
		return  transfer
	}

	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation
	GraphQLRetVal<String> reSyncTransferToBillingSchedule(
			@GraphQLArgument(name = "id") UUID id
	){
		ArTransfer arTransfer = findOne(id)

		List<BillingScheduleItems> billingScheduleItems = billingScheduleItemsServices.searchBSchItemPerBillItemIdAndAmt(arTransfer.billingItem.id,arTransfer.companyAccount.id,arTransfer.amount)

		if(billingScheduleItems.size() > 0){
			if(billingScheduleItems.size() == 1) {
				billingScheduleItems.each {
					it->
						it.arTransfer = arTransfer
						billingScheduleItemsServices.save(it)
				}

				return new GraphQLRetVal<String>("UPDATED", true)
			}
			else{
				return new GraphQLRetVal<String>("ERROR", false)
			}
		}
		else{
			return new GraphQLRetVal<String>("OK", true)
		}
	}
}
