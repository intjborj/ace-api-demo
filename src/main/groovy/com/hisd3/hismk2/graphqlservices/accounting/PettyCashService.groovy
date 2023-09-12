package com.hisd3.hismk2.graphqlservices.accounting

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.accounting.JournalType
import com.hisd3.hismk2.domain.accounting.LedgerDocType
import com.hisd3.hismk2.domain.accounting.PettyCash
import com.hisd3.hismk2.domain.accounting.PettyCashItem
import com.hisd3.hismk2.domain.accounting.Reapplication
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.rest.dto.DisbursementApDto
import com.hisd3.hismk2.rest.dto.JournalEntryViewDto
import com.hisd3.hismk2.rest.dto.PCVItemsDto
import com.hisd3.hismk2.rest.dto.PCVOthersDto
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
@GraphQLApi
class PettyCashService extends AbstractDaoService<PettyCash> {

	@Autowired
	GeneratorService generatorService

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	PettyCashItemServices pettyCashItemServices

	@Autowired
	PettyCashOtherServices pettyCashOtherServices

	@Autowired
	IntegrationServices integrationServices

	@Autowired
	LedgerServices ledgerServices

    PettyCashService() {
		super(PettyCash.class)
	}
	
	@GraphQLQuery(name = "pettyCashById")
	PettyCash pettyCashById(
			@GraphQLArgument(name = "id") UUID id
	) {
		findOne(id)
	}

//	@GraphQLQuery(name = "reapplicationList")
//	List<Reapplication> reapplicationList() {
//		createQuery("Select ap from Reapplication ap where ap.status = true").resultList
//	}


	@GraphQLQuery(name = "pettyCashPage")
	Page<PettyCash> pettyCashPage(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "status") Boolean status,
			@GraphQLArgument(name = "start") String start,
			@GraphQLArgument(name = "end") String end,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		String query = '''Select d from PettyCash d where
						( lower(d.payeeName) like lower(concat('%',:filter,'%'))
						or lower(d.pcvNo) like lower(concat('%',:filter,'%')) )
						and to_date(to_char(d.pcvDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
             			between to_date(:start,'YYYY-MM-DD') and  to_date(:end,'YYYY-MM-DD') '''

		String countQuery = '''Select count(d) from PettyCash d where
							( lower(d.payeeName) like lower(concat('%',:filter,'%'))
						or lower(d.pcvNo) like lower(concat('%',:filter,'%')) )
						and to_date(to_char(d.pcvDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
             			between to_date(:start,'YYYY-MM-DD') and  to_date(:end,'YYYY-MM-DD') '''

		Map<String, Object> params = new HashMap<>()
		params.put('filter', filter)
		params.put('start', start)
		params.put('end', end)

		if (status) {
			query += ''' and (d.posted = :status or d.posted is null) '''
			countQuery += ''' and (d.posted = :status or d.posted is null) '''
			params.put("status", !status)
		}


		query += ''' ORDER BY d.pcvNo DESC'''

		getPageable(query, countQuery, page, size, params)
	}


	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertPettyCash")
	PettyCash upsertPettyCash(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields")  Map<String,Object>  fields,
			@GraphQLArgument(name = "items")  ArrayList<Map<String, Object>>  items,
			@GraphQLArgument(name = "others")  ArrayList<Map<String, Object>>  others
	) {
		def parent = upsertFromMap(id, fields, {  PettyCash entity, boolean forInsert ->
			if(forInsert){
				entity.pcvNo = generatorService.getNextValue(GeneratorType.PCVNO, {
					return "PCV-" + StringUtils.leftPad(it.toString(), 6, "0")
				})
				entity.posted = false
				entity.postedLedger = null
				entity.status = "DRAFT"
			}
			entity.amountIssued = entity.amountIssued.round(2)
			entity.amountUsed = entity.amountUsed.round(2)
			entity.amountUnused = entity.amountUnused.round(2)
		})
		//items insert
		def purItems = items as ArrayList<PCVItemsDto>
		purItems.each {
			def dto = objectMapper.convertValue(it, PCVItemsDto.class)
			pettyCashItemServices.upsertPurchaseItems(dto, parent)
		}
		//others insert
		def otherItems = others as ArrayList<PCVOthersDto>
		otherItems.each {
			def dto = objectMapper.convertValue(it, PCVOthersDto.class)
			pettyCashOtherServices.upsertOthers(dto, parent)
		}

		return parent
	}

//
//	//post/void trigger
//	@Transactional(rollbackFor = Exception.class)
//	@GraphQLMutation(name = "postReapplication")
//	Reapplication postReapplication(
//			@GraphQLArgument(name = "id") UUID id,
//			@GraphQLArgument(name = "status") Boolean status
//	) {
//		def parent = findOne(id)
//		if(status){ // reverse
//			def header = ledgerServices.findOne(parent.postedLedger)
//			ledgerServices.reverseEntriesCustom(header, Instant.now())
//			//update AP
//			parent.postedLedger = null
//			parent.status = "DRAFT"
//			parent.posted = false
//			save(parent)
//
//			//update disbursement
//			disbursementServices.updateForReapplicationPost(
//					parent.disbursement.id,
//					parent.discountAmount,
//					parent.ewtAmount,
//					parent.appliedAmount,
//					true
//			)
//
//		}else{
//			postToLedgerAccounting(parent)
//			//update disbursement
//			disbursementServices.updateForReapplicationPost(
//					parent.disbursement.id,
//					parent.discountAmount,
//					parent.ewtAmount,
//					parent.appliedAmount,
//					false
//			)
//
//		}
//
//		return parent
//	}
//
//	//accounting view
//	@GraphQLQuery(name = "reapplyAccountView")
//	List<JournalEntryViewDto> reapplyAccountView(
//			@GraphQLArgument(name = "id") UUID id,
//			@GraphQLArgument(name = "status") Boolean status
//	) {
//		def result = new ArrayList<JournalEntryViewDto>()
//		//ewt rate
//		if(id) { //post
//			def parent = findOne(id)
//			def ewt1 = BigDecimal.ZERO; def ewt2 = BigDecimal.ZERO; def ewt3 = BigDecimal.ZERO;
//			def ewt4 = BigDecimal.ZERO; def ewt5 = BigDecimal.ZERO; def ewt7 = BigDecimal.ZERO;
//			def ewt10 = BigDecimal.ZERO; def ewt15 = BigDecimal.ZERO; def ewt18 = BigDecimal.ZERO;
//			def ewt30 = BigDecimal.ZERO;
//
//			def disburseDetials = disbursementApServices.apReapplication(parent.id)
//			disburseDetials.each {
//				switch (it.ewtRate) {
//					case 1:
//						ewt1 = it.ewtAmount
//						break;
//					case 2:
//						ewt2 = it.ewtAmount
//						break;
//					case 3:
//						ewt3 = it.ewtAmount
//						break;
//					case 4:
//						ewt4 = it.ewtAmount
//						break;
//					case 5:
//						ewt5 = it.ewtAmount
//						break;
//					case 7:
//						ewt7 = it.ewtAmount
//						break;
//					case 10:
//						ewt10 = it.ewtAmount
//						break;
//					case 15:
//						ewt15 = it.ewtAmount
//						break;
//					case 18:
//						ewt18 = it.ewtAmount
//						break;
//					case 30:
//						ewt30 = it.ewtAmount
//						break;
//				}
//			}
//			//ewt rate
//			if(parent.transType?.flagValue){
//				def headerLedger = integrationServices.generateAutoEntries(parent) {it, mul ->
//					it.flagValue = parent.transType?.flagValue
//					def sample = "discAmount"
//
//					def disAmount = parent.appliedAmount - (parent.discountAmount + parent.ewtAmount)
//
//					//debit
//					it.advanceAmount = status ? parent.appliedAmount :parent.appliedAmount * -1
//
//					//credit
//					it.disbursementAmount = status ? disAmount : disAmount * -1
//					it.discAmount = status ? parent.discountAmount : parent.discountAmount * -1
//					//ewt amount
//					it.ewt1Percent = status ? ewt1.round(2) : ewt1.round(2) * -1
//					it.ewt2Percent = status ? ewt2.round(2) : ewt2.round(2) * -1
//					it.ewt3Percent = status ? ewt3.round(2) : ewt3.round(2) * -1
//					it.ewt4Percent = status ? ewt4.round(2) : ewt4.round(2) * -1
//					it.ewt5Percent = status ? ewt5.round(2) : ewt5.round(2) * -1
//					it.ewt7Percent = status ? ewt7.round(2) : ewt7.round(2) * -1
//					it.ewt10Percent = status ? ewt10.round(2) : ewt10.round(2) * -1
//					it.ewt15Percent = status ? ewt15.round(2) : ewt15.round(2) * -1
//					it.ewt18Percent = status ? ewt18.round(2) : ewt18.round(2) * -1
//					it.ewt30Percent = status ? ewt30.round(2) : ewt30.round(2) * -1
//					//end ewt amount
//
//				}
//
//				headerLedger.ledger.each {
//					def list = new JournalEntryViewDto(
//							code: it.journalAccount.code,
//							desc: it.journalAccount.description,
//							debit: it.debit,
//							credit: it.credit
//					)
//					result.add(list)
//				}
//			}else{ //reverse
//				if(parent.postedLedger){
//					def header = ledgerServices.findOne(parent.postedLedger)
//					header.ledger.each {
//						def list = new JournalEntryViewDto(
//								code: it.journalAccount.code,
//								desc: it.journalAccount.description,
//								debit: it.credit,
//								credit: it.debit
//						)
//						result.add(list)
//					}
//				}
//			}
//		}
//		return result
//	}
//
//	//accounting post
//
//	@Transactional(rollbackFor = Exception.class)
//	Reapplication postToLedgerAccounting(Reapplication domain){
//		def yearFormat = DateTimeFormatter.ofPattern("yyyy")
//		def parent = super.save(domain) as Reapplication
//		//ewt rate
//		def ewt1 = BigDecimal.ZERO; def ewt2 = BigDecimal.ZERO; def ewt3 = BigDecimal.ZERO;
//		def ewt4 = BigDecimal.ZERO; def ewt5 = BigDecimal.ZERO; def ewt7 = BigDecimal.ZERO;
//		def ewt10 = BigDecimal.ZERO; def ewt15 = BigDecimal.ZERO; def ewt18 = BigDecimal.ZERO;
//		def ewt30 = BigDecimal.ZERO;
//
//		def parentDetials = disbursementApServices.apReapplication(parent.id)
//		parentDetials.each {
//			switch (it.ewtRate) {
//				case 1:
//					ewt1 = it.ewtAmount
//					break;
//				case 2:
//					ewt2 = it.ewtAmount
//					break;
//				case 3:
//					ewt3 = it.ewtAmount
//					break;
//				case 4:
//					ewt4 = it.ewtAmount
//					break;
//				case 5:
//					ewt5 = it.ewtAmount
//					break;
//				case 7:
//					ewt7 = it.ewtAmount
//					break;
//				case 10:
//					ewt10 = it.ewtAmount
//					break;
//				case 15:
//					ewt15 = it.ewtAmount
//					break;
//				case 18:
//					ewt18 = it.ewtAmount
//					break;
//				case 30:
//					ewt30 = it.ewtAmount
//					break;
//			}
//		}
//		//ewt rate
//
//		def headerLedger = integrationServices.generateAutoEntries(parent) {it, mul ->
//			it.flagValue = parent.transType?.flagValue
//
//			def disAmount = parent.appliedAmount - (parent.discountAmount + parent.ewtAmount)
//
//			//debit
//			it.advanceAmount = parent.appliedAmount //debit normal side
//
//			//credit
//			it.disbursementAmount = disAmount
//			it.discAmount = parent.discountAmount
//
//			it.ewt1Percent = ewt1.round(2)
//			it.ewt2Percent = ewt2.round(2)
//			it.ewt3Percent = ewt3.round(2)
//			it.ewt4Percent = ewt4.round(2)
//			it.ewt5Percent = ewt5.round(2)
//			it.ewt7Percent = ewt7.round(2)
//			it.ewt10Percent = ewt10.round(2)
//			it.ewt15Percent = ewt15.round(2)
//			it.ewt18Percent = ewt18.round(2)
//			it.ewt30Percent = ewt30.round(2)
//			//
//
//		}
//
//		Map<String,String> details = [:]
//
//		parent.details.each { k,v ->
//			details[k] = v
//		}
//
//		details["REAPPLICATION_ID"] = parent.id.toString()
//		details["DISBURSEMENT_ID"] = parent.disbursement.id.toString()
//		details["SUPPLIER_ID"] = parent.supplier.id.toString()
//
//		def pHeader =	ledgerServices.persistHeaderLedger(headerLedger,
//				"${Instant.now().atZone(ZoneId.systemDefault()).format(yearFormat)}-${parent.disbursement.disNo}",
//				"${parent.disbursement.disNo}-${parent.supplier.supplierFullname}",
//				"${parent.disbursement.disNo}-${parent.supplier.supplierFullname}",
//				parent.disbursement.disType.equalsIgnoreCase("CASH") ? LedgerDocType.CS : LedgerDocType.CK, // CS = CASH , CK = CHECK
//				JournalType.DISBURSEMENT,
//				Instant.now(),
//				details)
//		parent.postedLedger = pHeader.id
//		parent.status = "POSTED"
//		parent.posted = true
//
//		if(parent.appliedAmount < 0.0)
//		{
//			pHeader.reversal = true
//			ledgerServices.save(pHeader)
//		}
//		save(parent)
//	}
//
//	//accounting manual
//	@Transactional(rollbackFor = Exception.class)
//	@GraphQLMutation(name = "postReappManual")
//	GraphQLRetVal<Boolean> postReappManual(
//			@GraphQLArgument(name = "id") UUID id,
//			@GraphQLArgument(name = "header")  Map<String,Object>  header,
//			@GraphQLArgument(name = "entries")  List<Map<String,Object>>  entries
//	) {
//		def parent = findOne(id)
//
//		Map<String,String> details = [:]
//
//		parent.details.each { k,v ->
//			details[k] = v
//		}
//
//		details["REAPPLICATION_ID"] = parent.id.toString()
//		details["DISBURSEMENT_ID"] = parent.disbursement.id.toString()
//		details["SUPPLIER_ID"] = parent.supplier.id.toString()
//
//		def result = ledgerServices.addManualJVDynamic(
//				header,
//				entries,
//				parent.disbursement.disType.equalsIgnoreCase("CASH") ? LedgerDocType.CS : LedgerDocType.CK,
//				JournalType.DISBURSEMENT,
//				Instant.now(),
//				details
//		)
//
//		//update parent
//		parent.postedLedger = result.returnId
//		parent.status = "POSTED"
//		parent.posted = true
//		save(parent)
//
//		return result
//	}

}
