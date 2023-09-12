package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.*
import com.hisd3.hismk2.domain.cashiering.PaymentTracker
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.billing.CompanyAccountServices
import com.hisd3.hismk2.graphqlservices.cashiering.PaymentTrackerServices
import com.hisd3.hismk2.graphqlservices.inventory.SupplierService
import com.hisd3.hismk2.repository.accounting.ArTransactionDetailsRepository
import com.hisd3.hismk2.repository.accounting.ArTransactionRepository
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

import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Component
@GraphQLApi
class ArLedgerServices extends AbstractDaoService<ArLedger> {

	ArLedgerServices() {
		super(ArLedger.class)
	}

	@Autowired
	EntityObjectMapperService entityObjectMapperService

	@Autowired
	CompanyAccountServices companyAccountServices

	@Autowired
	SupplierRepository supplierRepository


	@Autowired
	GeneratorService generatorService


	@GraphQLQuery(name = "voidArLedgerByAr")
	voidArLedger(@GraphQLArgument(name = "id") UUID id) {
		def arLedger = getLedgerByAR(id)
		if(arLedger){
			arLedger.each {
				it.status = 'inactive'
				save(it)
			}
		}

	}



	@GraphQLQuery(name = "debitArLedgerFromAr")
	debitArLedgerFromAr(@GraphQLArgument(name = "accRec") AccountReceivable accRec) {
		ArLedger arLedger = new ArLedger()
		arLedger.reference = accRec.arNo
		arLedger.ledgerNo = generatorService.getNextValue(GeneratorType.AR_LEDGER_NO, {
			return  StringUtils.leftPad(it.toString(), 6, "0")
		})
		arLedger.accountReceivable = accRec

		def account
		if(accRec.groups[AccReceivableGroupParam.COMPANY_ACCOUNT_ID.name()]){
			def company = companyAccountServices.findOne(accRec.groups[AccReceivableGroupParam.COMPANY_ACCOUNT_ID.name()])
			arLedger.companyAccount = company
			account = company.companyname
			arLedger.debit = accRec.totals
			arLedger.balance = accRec.totals
		}
		if(accRec.groups[AccReceivableGroupParam.PERSONAL_ACCOUNT_ID.name()]){
			def supplier = supplierRepository.findById(accRec.groups[AccReceivableGroupParam.PERSONAL_ACCOUNT_ID.name()]).get()
			arLedger.personalAccount = supplier
			account = supplier.description
			arLedger.debit = accRec.other
			arLedger.balance = accRec.other
		}
		arLedger.description = "${accRec.arNo} - ${account}"
		arLedger.credit = 0
		arLedger.journalLedger = accRec.postedLedger
		arLedger.status = 'active'
		save(arLedger)
	}

	@GraphQLQuery(name = "creditArLedgerFromAr")
	creditArLedgerFromAr(@GraphQLArgument(name = "art") ArTransaction art) {
		ArLedger arLedger = new ArLedger()
		arLedger.reference = art.trackingNo
		arLedger.ledgerNo = generatorService.getNextValue(GeneratorType.AR_LEDGER_NO, {
			return  StringUtils.leftPad(it.toString(), 6, "0")
		})
		arLedger.accountReceivable = art.accountReceivable

		if(art.accountReceivable.groups[AccReceivableGroupParam.COMPANY_ACCOUNT_ID.name()]){
			def company = companyAccountServices.findOne(art.accountReceivable.groups[AccReceivableGroupParam.COMPANY_ACCOUNT_ID.name()])
			arLedger.companyAccount = company
		}
		if(art.accountReceivable.groups[AccReceivableGroupParam.PERSONAL_ACCOUNT_ID.name()]){
			def supplier = supplierRepository.findById(art.accountReceivable.groups[AccReceivableGroupParam.PERSONAL_ACCOUNT_ID.name()]).get()
			arLedger.personalAccount = supplier
		}

		arLedger.description = "${art.trackingNo} - ${art.type}"
		arLedger.debit = 0
		arLedger.credit = art.amount
		arLedger.balance = art.accountReceivable.balance - art.amount
		arLedger.journalLedger = art.postedLedger
		arLedger.status = 'active'
		save(arLedger)
	}

	@GraphQLQuery(name = "debitArLedgerPerAr")
	debitArLedgerPerAr(@GraphQLArgument(name = "art") ArTransactionDetails art,@GraphQLArgument(name = "accRec") AccountReceivableItems accRec) {
		ArLedger arLedger = new ArLedger()
		arLedger.reference = art.arTransaction.trackingNo
		arLedger.ledgerNo = generatorService.getNextValue(GeneratorType.AR_LEDGER_NO, {
			return  StringUtils.leftPad(it.toString(), 6, "0")
		})
		arLedger.accountReceivable = accRec.accountReceivable

		if(accRec.accountReceivable.groups[AccReceivableGroupParam.COMPANY_ACCOUNT_ID.name()]){
			def company = companyAccountServices.findOne(accRec.accountReceivable.groups[AccReceivableGroupParam.COMPANY_ACCOUNT_ID.name()])
			arLedger.companyAccount = company
		}
		if(accRec.accountReceivable.groups[AccReceivableGroupParam.PERSONAL_ACCOUNT_ID.name()]){
			def supplier = supplierRepository.findById(accRec.accountReceivable.groups[AccReceivableGroupParam.PERSONAL_ACCOUNT_ID.name()]).get()
			arLedger.personalAccount = supplier
		}

		arLedger.description = "${accRec.description} - ${art.type}"
		arLedger.debit = accRec.amount
		arLedger.credit = 0
		arLedger.balance = accRec.accountReceivable.balance + accRec.amount
		arLedger.journalLedger = art.arTransaction.postedLedger
		arLedger.status = 'active'
		save(arLedger)
	}

	@GraphQLQuery(name = "creditArLedgerPerAr")
	creditArLedgerPerAr(@GraphQLArgument(name = "art") ArTransactionDetails art,@GraphQLArgument(name = "accRec") AccountReceivableItems accRec) {
		ArLedger arLedger = new ArLedger()
		arLedger.reference = art.arTransaction.trackingNo
		arLedger.ledgerNo = generatorService.getNextValue(GeneratorType.AR_LEDGER_NO, {
			return  StringUtils.leftPad(it.toString(), 6, "0")
		})
		arLedger.accountReceivable = accRec.accountReceivable

		if(accRec.accountReceivable.groups[AccReceivableGroupParam.COMPANY_ACCOUNT_ID.name()]){
			def company = companyAccountServices.findOne(accRec.accountReceivable.groups[AccReceivableGroupParam.COMPANY_ACCOUNT_ID.name()])
			arLedger.companyAccount = company
		}
		if(accRec.accountReceivable.groups[AccReceivableGroupParam.PERSONAL_ACCOUNT_ID.name()]){
			def supplier = supplierRepository.findById(accRec.accountReceivable.groups[AccReceivableGroupParam.PERSONAL_ACCOUNT_ID.name()]).get()
			arLedger.personalAccount = supplier
		}

		arLedger.description = "${accRec.description} - ${art.type}"
		arLedger.debit = 0
		arLedger.credit = art.amount
		arLedger.balance = accRec.accountReceivable.balance - art.amount
		arLedger.journalLedger = art.arTransaction.postedLedger
		arLedger.status = 'active'
		save(arLedger)
	}

	@GraphQLQuery(name = "reverseArLedgerByReference")
	reverseArLedgerByReference(@GraphQLArgument(name = "reference") String reference) {
		def newArLedger = getLedgerReference(reference)

		ArLedger arLedger = new ArLedger()
		arLedger.reference = reference
		arLedger.ledgerNo = generatorService.getNextValue(GeneratorType.AR_LEDGER_NO, {
			return  StringUtils.leftPad(it.toString(), 6, "0")
		})
		arLedger.accountReceivable = newArLedger.accountReceivable
		arLedger.companyAccount = newArLedger.companyAccount
		arLedger.personalAccount = newArLedger.personalAccount
		if(newArLedger.credit > 0){
			arLedger.debit = newArLedger.credit
			arLedger.credit = 0
			arLedger.balance = newArLedger.accountReceivable.totals + newArLedger.credit
		}
		else{
			arLedger.debit = 0
			arLedger.credit = newArLedger.debit
			arLedger.balance = newArLedger.accountReceivable.totals - newArLedger.debit
		}

		arLedger.description = "${newArLedger.reference} - reverse"
//		arLedger.journalLedger = newArLedger.postedLedger
		arLedger.status = 'active'
		save(arLedger)

	}

	@GraphQLQuery(name = "getLedgerReference")
	ArLedger getLedgerReference(@GraphQLArgument(name = "reference") String reference) {
		createQuery("""
                    select b from ArLedger b where b.reference = :reference
            """,
				[
						reference: reference,
				] as Map<String, Object>).singleResult
	}

	@GraphQLQuery(name = "getLedgerByAR")
	List<ArLedger> getLedgerByAR(@GraphQLArgument(name = "accRec") UUID accRec) {
		createQuery("""
                    select b from ArLedger b where b.accountReceivable.id = :accRec
            """,
				[
						accRec: accRec,
				] as Map<String, Object>).resultList
	}

	@GraphQLQuery(name = "arLedgerSave")
	arLedgerSave(@GraphQLArgument(name = "ledger") ArLedger ledger) {
		if(ledger){
			save(ledger)
		}
	}
}
