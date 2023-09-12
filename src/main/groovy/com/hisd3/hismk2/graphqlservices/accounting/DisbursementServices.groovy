package com.hisd3.hismk2.graphqlservices.accounting

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.accounting.Disbursement
import com.hisd3.hismk2.domain.accounting.JournalType
import com.hisd3.hismk2.domain.accounting.Ledger
import com.hisd3.hismk2.domain.accounting.LedgerDocType
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.cashiering.CashieringService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.inventory.ReceivingReportRepository
import com.hisd3.hismk2.rest.dto.*
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
@GraphQLApi
class DisbursementServices extends AbstractDaoService<Disbursement> {

	@Autowired
	GeneratorService generatorService

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	AccountsPayableDetialServices accountsPayableDetialServices

	@Autowired
	ApLedgerServices apLedgerServices

	@Autowired
	JdbcTemplate jdbcTemplate

	@Autowired
	ReceivingReportRepository receivingReportRepository

	@Autowired
	IntegrationServices integrationServices

	@Autowired
	LedgerServices ledgerServices

	@Autowired
	DisbursementCheckServices disbursementCheckServices

	@Autowired
	DisbursementApServices disbursementApServices

	@Autowired
	CashieringService cashieringService

	@Autowired
	DisbursementExpenseServices disbursementExpenseServices

	@Autowired
	DisbursementWtxServices disbursementWtxServices

	@Autowired
	Wtx2307Service wtx2307Service

	@Autowired
	AccountsPayableServices accountsPayableServices

	@Autowired
	DepartmentRepository departmentRepository

    DisbursementServices() {
		super(Disbursement.class)
	}
	
	@GraphQLQuery(name = "disbursementById")
	Disbursement disbursementById(
			@GraphQLArgument(name = "id") UUID id
	) {
		findOne(id)
	}

	@GraphQLQuery(name = "disbursementFilterPosted", description = "List of AP Pageable By Supplier")
	Page<Disbursement> apListBySupplierFilter(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "supplier") UUID supplier,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		String query = '''Select d from Disbursement d where
							d.posted = true and
						( lower(d.disNo) like lower(concat('%',:filter,'%')) )'''

		String countQuery = '''Select count(d) from Disbursement d where
							d.posted = true and
							( lower(d.disNo) like lower(concat('%',:filter,'%')) ) '''

		Map<String, Object> params = new HashMap<>()
		params.put('filter', filter)

		if (supplier) {
			query += ''' and (d.supplier.id = :supplier) '''
			countQuery += ''' and (d.supplier.id = :supplier) '''
			params.put("supplier", supplier)
		}

		query += ''' ORDER BY d.disNo DESC'''

		getPageable(query, countQuery, page, size, params)
	}

	@GraphQLQuery(name = "disbursementFilter", description = "List of Disbursement Pageable")
	Page<Disbursement> disbursementFilter(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "supplier") UUID supplier,
			@GraphQLArgument(name = "status") Boolean status,
			@GraphQLArgument(name = "start") String start,
			@GraphQLArgument(name = "end") String end,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		String query = '''Select d from Disbursement d where
						( lower(d.disNo) like lower(concat('%',:filter,'%')) )
						and to_date(to_char(d.disDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
             			between to_date(:start,'YYYY-MM-DD') and  to_date(:end,'YYYY-MM-DD')'''

		String countQuery = '''Select count(d) from Disbursement d where
							( lower(d.disNo) like lower(concat('%',:filter,'%')) )
							and to_date(to_char(d.disDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
             			between to_date(:start,'YYYY-MM-DD') and  to_date(:end,'YYYY-MM-DD')'''

		Map<String, Object> params = new HashMap<>()
		params.put('filter', filter)
		params.put('start', start)
		params.put('end', end)

		if (supplier) {
			query += ''' and (d.supplier.id = :supplier) '''
			countQuery += ''' and (d.supplier.id = :supplier) '''
			params.put("supplier", supplier)
		}

		if (status) {
			query += ''' and (d.posted = :status or d.posted is null) '''
			countQuery += ''' and (d.posted = :status or d.posted is null) '''
			params.put("status", !status)
		}

		query += ''' ORDER BY d.disNo DESC'''

		getPageable(query, countQuery, page, size, params)
	}

	//mutations
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertDisbursement")
	Disbursement upsertDisbursement(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "checks") ArrayList<Map<String, Object>> checks,
			@GraphQLArgument(name = "ap") ArrayList<Map<String, Object>> ap,
			@GraphQLArgument(name = "expense") ArrayList<Map<String, Object>> expense,
			@GraphQLArgument(name = "wtx") ArrayList<Map<String, Object>> wtx,
			@GraphQLArgument(name = "id") UUID id
	) {
		def disCat = 'CK';

		def dis = upsertFromMap(id, fields, { Disbursement entity, boolean forInsert ->
			if (forInsert) {
				def type = fields['disType'] as String
				if(type.equalsIgnoreCase("CASH")){
					disCat = 'CS'
				}
				entity.disNo = generatorService.getNextValue(GeneratorType.DISNO, {
					return "${disCat}-" + StringUtils.leftPad(it.toString(), 6, "0")
				})
				//round numbers to 2 decimal
				entity.cash = entity.cash.round(2)
				entity.checks = entity.checks.round(2)
				entity.discountAmount = entity.discountAmount.round(2)
				entity.ewtAmount = entity.ewtAmount.round(2)
				entity.voucherAmount = entity.voucherAmount.round(2)
				entity.appliedAmount = entity.appliedAmount.round(2)

				entity.status = "DRAFT"
				entity.posted = false

			}else{
				//round numbers to 2 decimal
				entity.cash = entity.cash.round(2)
				entity.checks = entity.checks.round(2)
				entity.discountAmount = entity.discountAmount.round(2)
				entity.ewtAmount = entity.ewtAmount.round(2)
				entity.voucherAmount = entity.voucherAmount.round(2)
				entity.appliedAmount = entity.appliedAmount.round(2)
			}
		})

		def disChecks = checks as ArrayList<DisbursementDto>
		//save details here
		disChecks.each{
			def disDto = objectMapper.convertValue(it, DisbursementDto.class)
			disbursementCheckServices.upsertCheck(disDto, dis)
		}
		//end save details here
		//save ap application
		def disAp = ap as ArrayList<DisbursementApDto>
		disAp.each {
			def dto = objectMapper.convertValue(it, DisbursementApDto.class)
			disbursementApServices.upsertDisAp(dto, dis)
		}
		//end save ap application

		//save save expense disbursement
		def disEx = expense as ArrayList<DisbursementExpDto>
		disEx.each {
			def dto = objectMapper.convertValue(it, DisbursementExpDto.class)
			disbursementExpenseServices.upsertExp(dto, dis)
		}
		//end expense disbursement

		//save save disbursement wtx
		def disWtx = wtx as ArrayList<DisbursementWtxDto>
		disWtx.each {
			def dto = objectMapper.convertValue(it, DisbursementWtxDto.class)
			disbursementWtxServices.upsertWtx(dto, dis)
		}
		//end disbursement wtx

		return dis
	}


	@GraphQLQuery(name = "disAccountView")
	List<JournalEntryViewDto> disAccountView(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "status") Boolean status
	) {
		def result = new ArrayList<JournalEntryViewDto>()
		//ewt rate
		if(id) {
			def disburse = findOne(id)
			def ewt1 = BigDecimal.ZERO; def ewt2 = BigDecimal.ZERO; def ewt3 = BigDecimal.ZERO;
			def ewt4 = BigDecimal.ZERO; def ewt5 = BigDecimal.ZERO; def ewt7 = BigDecimal.ZERO;
			def ewt10 = BigDecimal.ZERO; def ewt15 = BigDecimal.ZERO; def ewt18 = BigDecimal.ZERO;
			def ewt30 = BigDecimal.ZERO;

			def disburseDetials = disbursementApServices.apAppByDis(disburse.id)
			def expenseEwt = disbursementWtxServices.disWtxByParent(disburse.id)
			def expense = disbursementExpenseServices.disExpByParent(disburse.id)
			def checks = disbursementCheckServices.disCheckByParent(disburse.id)
			//ewt rate start here
			disburseDetials.each {
				switch (it.ewtRate) {
					case 1:
						ewt1+=it.ewtAmount
						break;
					case 2:
						ewt2+=it.ewtAmount
						break;
					case 3:
						ewt3+=it.ewtAmount
						break;
					case 4:
						ewt4+=it.ewtAmount
						break;
					case 5:
						ewt5+=it.ewtAmount
						break;
					case 7:
						ewt7+=it.ewtAmount
						break;
					case 10:
						ewt10+=it.ewtAmount
						break;
					case 15:
						ewt15+=it.ewtAmount
						break;
					case 18:
						ewt18+=it.ewtAmount
						break;
					case 30:
						ewt30+=it.ewtAmount
						break;
				}
			}
			//expense ewt
			expenseEwt.each {
				switch (it.ewtRate) {
					case 1:
						ewt1+=it.ewtAmount
						break;
					case 2:
						ewt2+=it.ewtAmount
						break;
					case 3:
						ewt3+=it.ewtAmount
						break;
					case 4:
						ewt4+=it.ewtAmount
						break;
					case 5:
						ewt5+=it.ewtAmount
						break;
					case 7:
						ewt7+=it.ewtAmount
						break;
					case 10:
						ewt10+=it.ewtAmount
						break;
					case 15:
						ewt15+=it.ewtAmount
						break;
					case 18:
						ewt18+=it.ewtAmount
						break;
					case 30:
						ewt30+=it.ewtAmount
						break;
				}
			}
			//ewt rate end here

			if(disburse.transType?.flagValue){
				def headerLedger = integrationServices.generateAutoEntries(disburse) {it, mul ->
					it.flagValue = disburse.transType?.flagValue
					List<Disbursement> mulChecks  = []
					List<Disbursement> exp  = []

					// disbursement bank multiple // cash on bank
					if(disburse.disType.equalsIgnoreCase("CHECK")) {
						checks.each { dc ->
							mulChecks << new Disbursement().tap {
								//debit normal side use negative to make it to credit
								it.bank = dc.bank
								it.cashOnBank = status ? dc.amount.round(2) * -1 : dc.amount.round(2)
							}
						}
					}
					mul << mulChecks

					//cash on hand
					//there's an issue on null
					it.cashOnHand = status ? disburse.cash.round(2) * -1 : disburse.cash.round(2)
					it.terminal = disburse.terminal ? disburse.terminal: cashieringService.findOneById(UUID.fromString("b6ebb547-d2d8-4b7c-9bc8-0243a93fd179"))

					//end disbursement bank multiple
					//initialize value_*
					it.value_a = BigDecimal.ZERO;it.value_b = BigDecimal.ZERO;it.value_c = BigDecimal.ZERO;it.value_d = BigDecimal.ZERO;
					it.value_e = BigDecimal.ZERO;it.value_f = BigDecimal.ZERO;it.value_g = BigDecimal.ZERO;it.value_h = BigDecimal.ZERO;
					it.value_i = BigDecimal.ZERO;it.value_j = BigDecimal.ZERO;it.value_k = BigDecimal.ZERO;it.value_l = BigDecimal.ZERO;
					it.value_m = BigDecimal.ZERO;it.value_n = BigDecimal.ZERO;it.value_o = BigDecimal.ZERO;it.value_p = BigDecimal.ZERO;
					it.value_q = BigDecimal.ZERO;it.value_r = BigDecimal.ZERO;it.value_s = BigDecimal.ZERO;it.value_t = BigDecimal.ZERO;
					it.value_u = BigDecimal.ZERO;it.value_v = BigDecimal.ZERO;it.value_w = BigDecimal.ZERO;it.value_x = BigDecimal.ZERO;
					it.value_y = BigDecimal.ZERO;it.value_z = BigDecimal.ZERO;

					it.value_z1 = BigDecimal.ZERO;it.value_z2 = BigDecimal.ZERO;it.value_z3 = BigDecimal.ZERO;it.value_z4 = BigDecimal.ZERO;
					it.value_z5 = BigDecimal.ZERO;it.value_z6 = BigDecimal.ZERO;it.value_z7 = BigDecimal.ZERO;it.value_z8 = BigDecimal.ZERO;
					it.value_z9 = BigDecimal.ZERO;it.value_z10 = BigDecimal.ZERO;it.value_z11 = BigDecimal.ZERO;it.value_z12 = BigDecimal.ZERO;
					it.value_z13 = BigDecimal.ZERO;it.value_z14 = BigDecimal.ZERO;it.value_z15 = BigDecimal.ZERO;it.value_z16 = BigDecimal.ZERO;
					it.value_z17 = BigDecimal.ZERO;it.value_z18 = BigDecimal.ZERO;it.value_z19 = BigDecimal.ZERO;it.value_z20 = BigDecimal.ZERO;
					it.value_z21 = BigDecimal.ZERO;it.value_z22 = BigDecimal.ZERO;it.value_z23 = BigDecimal.ZERO;it.value_z24 = BigDecimal.ZERO;
					it.value_z25 = BigDecimal.ZERO;it.value_z26 = BigDecimal.ZERO;

					it.discAmount = status ? disburse.discountAmount.round(2) : disburse.discountAmount.round(2) * -1

					//default department (MMD) b9a940c0-526f-4098-b020-a95dec316e47
					it.department = departmentRepository.findById(UUID.fromString("b9a940c0-526f-4098-b020-a95dec316e47")).get()

					if(disburse.paymentCategory.equalsIgnoreCase("EXPENSE")){
						expense.each {a ->
							//=== for multiple  ===//
							exp << new Disbursement().tap {
								//default department (MMD) b9a940c0-526f-4098-b020-a95dec316e47
								it.department = departmentRepository.findById(UUID.fromString("b9a940c0-526f-4098-b020-a95dec316e47")).get()

								it.value_a = BigDecimal.ZERO;it.value_b = BigDecimal.ZERO;it.value_c = BigDecimal.ZERO;it.value_d = BigDecimal.ZERO;
								it.value_e = BigDecimal.ZERO;it.value_f = BigDecimal.ZERO;it.value_g = BigDecimal.ZERO;it.value_h = BigDecimal.ZERO;
								it.value_i = BigDecimal.ZERO;it.value_j = BigDecimal.ZERO;it.value_k = BigDecimal.ZERO;it.value_l = BigDecimal.ZERO;
								it.value_m = BigDecimal.ZERO;it.value_n = BigDecimal.ZERO;it.value_o = BigDecimal.ZERO;it.value_p = BigDecimal.ZERO;
								it.value_q = BigDecimal.ZERO;it.value_r = BigDecimal.ZERO;it.value_s = BigDecimal.ZERO;it.value_t = BigDecimal.ZERO;
								it.value_u = BigDecimal.ZERO;it.value_v = BigDecimal.ZERO;it.value_w = BigDecimal.ZERO;it.value_x = BigDecimal.ZERO;
								it.value_y = BigDecimal.ZERO;it.value_z = BigDecimal.ZERO;

								it.value_z1 = BigDecimal.ZERO;it.value_z2 = BigDecimal.ZERO;it.value_z3 = BigDecimal.ZERO;it.value_z4 = BigDecimal.ZERO;
								it.value_z5 = BigDecimal.ZERO;it.value_z6 = BigDecimal.ZERO;it.value_z7 = BigDecimal.ZERO;it.value_z8 = BigDecimal.ZERO;
								it.value_z9 = BigDecimal.ZERO;it.value_z10 = BigDecimal.ZERO;it.value_z11 = BigDecimal.ZERO;it.value_z12 = BigDecimal.ZERO;
								it.value_z13 = BigDecimal.ZERO;it.value_z14 = BigDecimal.ZERO;it.value_z15 = BigDecimal.ZERO;it.value_z16 = BigDecimal.ZERO;
								it.value_z17 = BigDecimal.ZERO;it.value_z18 = BigDecimal.ZERO;it.value_z19 = BigDecimal.ZERO;it.value_z20 = BigDecimal.ZERO;
								it.value_z21 = BigDecimal.ZERO;it.value_z22 = BigDecimal.ZERO;it.value_z23 = BigDecimal.ZERO;it.value_z24 = BigDecimal.ZERO;
								it.value_z25 = BigDecimal.ZERO;it.value_z26 = BigDecimal.ZERO;

								if (a.department?.id) {
									it.department = a.department
								}
								if (a.transType.isReverse) {
									it[a.transType.source] += status ? a.amount.round(2) * -1 : a.amount.round(2)
								} else {
									it[a.transType.source] += status ? a.amount.round(2) : a.amount.round(2) * -1
								}

								mul << exp
							}

							//=== for normal ===//
							if (a.department?.id) {
								it.department = a.department
							}
							if (a.transType.isReverse) {
								it[a.transType.source] += status ? a.amount.round(2) * -1 : a.amount.round(2)
							} else {
								it[a.transType.source] += status ? a.amount.round(2) : a.amount.round(2) * -1
							}

						}
						//make it zero for expense
						it.supplierAmount = BigDecimal.ZERO
						it.advancesSupplier = BigDecimal.ZERO
					}else{
						if(disburse.isAdvance){
							// debit normal side no need to negative
							it.advancesSupplier = status ? disburse.voucherAmount.round(2) : disburse.voucherAmount.round(2)  * -1
							it.supplierAmount = BigDecimal.ZERO
						}else{
							// credit normal side make it negative to debit
							it.supplierAmount = status ? (disburse.voucherAmount.round(2) * -1) : disburse.voucherAmount.round(2)
							it.advancesSupplier = BigDecimal.ZERO
						}
					}

					it.ewt1Percent = status ? ewt1.round(2) : ewt1.round(2) * -1
					it.ewt2Percent = status ? ewt2.round(2) : ewt2.round(2) * -1
					it.ewt3Percent = status ? ewt3.round(2) : ewt3.round(2) * -1
					it.ewt4Percent = status ? ewt4.round(2) : ewt4.round(2) * -1
					it.ewt5Percent = status ? ewt5.round(2) : ewt5.round(2) * -1
					it.ewt7Percent = status ? ewt7.round(2) : ewt7.round(2) * -1
					it.ewt10Percent = status ? ewt10.round(2) : ewt10.round(2) * -1
					it.ewt15Percent = status ? ewt15.round(2) : ewt15.round(2) * -1
					it.ewt18Percent = status ? ewt18.round(2) : ewt18.round(2) * -1
					it.ewt30Percent = status ? ewt30.round(2) : ewt30.round(2) * -1
					//

				}

				Set<Ledger> ledger = new HashSet<Ledger>(headerLedger.ledger);
				ledger.each {
					def list = new JournalEntryViewDto(
							code: it.journalAccount.code,
							desc: it.journalAccount.description,
							debit: it.debit,
							credit: it.credit
					)
					result.add(list)
				}
			}else{
				if(disburse.postedLedger){
					def header = ledgerServices.findOne(disburse.postedLedger)
					Set<Ledger> ledger = new HashSet<Ledger>(header.ledger);
					ledger.each {
						def list = new JournalEntryViewDto(
								code: it.journalAccount.code,
								desc: it.journalAccount.description,
								debit: it.credit,
								credit: it.debit
						)
						result.add(list)
					}
				}
			}
		}
		return result.sort{it.debit}.reverse(true)
	}


	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "postDisbursement")
	Disbursement postDisbursement(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "status") Boolean status
	) {
		def disCat = 'CK';
		def dis = findOne(id)
		if(dis.disType.equalsIgnoreCase("CASH")){
			disCat = 'CS'
		}
		if(status){
			def header = ledgerServices.findOne(dis.postedLedger)
			ledgerServices.reverseEntriesCustom(header, dis.disDate)
			//update AP
			dis.postedLedger = null
			dis.status = "DRAFT"
			dis.posted = false
			dis.postedBy = null
			save(dis)
			//remove ap ledger
			if(dis.paymentCategory.equalsIgnoreCase("PAYABLE")){
				apLedgerServices.removeApLedger(dis.disNo)
			}

			def ap = disbursementApServices.apByDis(id)
			ap.each {
				if(it.ewtAmount > BigDecimal.ZERO){
					wtx2307Service.remove2307(it.payable.id)
				}
				//update ap inag void
				accountsPayableServices.updateApForRemove(it.payable.id, it.disbursement.disNo, it.appliedAmount, it.posted)
				//update disAp posted = false
				disbursementApServices.updateDisApPosted(it, false)
			}

			def expenseEwt = disbursementWtxServices.disWtxByParent(id)
			expenseEwt.each {
				if(it.ewtAmount > BigDecimal.ZERO){
					wtx2307Service.remove2307(dis.id)
				}
			}

			//end remover ap ledger
		}else{
			postToLedgerAccounting(dis)

			if(dis.paymentCategory.equalsIgnoreCase("PAYABLE")){
				//add to ap ledger
				Map<String, Object> ledger = new HashMap<>()
				ledger.put('ledgerType', disCat)
				ledger.put('refNo', dis?.disNo)
				ledger.put('refId', dis?.id)
				ledger.put('debit', dis?.voucherAmount)
				ledger.put('credit', 0.00)
				apLedgerServices.upsertApLedger(ledger, dis?.supplier?.id, null);
				//end to ap ledger
			}

			//post to ewt if naa
			def ap = disbursementApServices.apAppByDis(id)
			ap.each {
				if(it.ewtAmount > BigDecimal.ZERO){
					Map<String, Object> ewt = new HashMap<>()
					ewt.put('refId',it.payable.id)
					ewt.put('refNo',it.payable.apNo)
					ewt.put('wtxDate',dis.disDate)
					ewt.put('type','AP') //AP, AROTHERS
					ewt.put('gross',it.appliedAmount) //net of discount
					ewt.put('vatAmount',it.vatAmount) // 0
					ewt.put('netVat', (it.appliedAmount - it.vatAmount)) // same by gross
					ewt.put('ewtAmount',it.ewtAmount) //ewt amounnt
					wtx2307Service.upsert2307(ewt, null, dis.supplier.id)
				}
			}
			//end
			//expense ewt
			def expenseEwt = disbursementWtxServices.disWtxByParent(id)
			expenseEwt.each {
				if(it.ewtAmount > BigDecimal.ZERO){
					Map<String, Object> ewt = new HashMap<>()
					ewt.put('refId',dis.id)
					ewt.put('refNo',dis.disNo)
					ewt.put('wtxDate',dis.disDate)
					ewt.put('type',disCat) //AP, AROTHERS, CK, CS
					ewt.put('gross',dis.voucherAmount) //net of discount
					ewt.put('vatAmount',0) // 0
					ewt.put('netVat',dis.voucherAmount) // same by gross
					ewt.put('ewtAmount',it.ewtAmount) //ewt amounnt
					wtx2307Service.upsert2307(ewt, null, dis.supplier.id)
				}
			}
			//end expense

			//update ap balance
			def p = disbursementApServices.apByDis(id)
			p.each {
				if(!it.posted){
					accountsPayableServices.updateAp(it.payable.id, it.disbursement.disNo, it.appliedAmount)
				}
				//update disbursement Ap posted
				disbursementApServices.updateDisApPosted(it, true)
			}
			//end
		}
		return dis
	}

	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "postDsManual")
	GraphQLRetVal<Boolean> postDsManual(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "header")  Map<String,Object>  header,
			@GraphQLArgument(name = "entries")  List<Map<String,Object>>  entries
	) {
		def dis = findOne(id)

		def disCat = 'CK';
		if(dis.disType.equalsIgnoreCase("CASH")){
			disCat = 'CS'
		}

		Map<String,String> details = [:]

		dis.details.each { k,v ->
			details[k] = v
		}

		details["DISBURSEMENT_ID"] = dis.id.toString()
		details["SUPPLIER_ID"] = dis.supplier.id.toString()

		def result = ledgerServices.addManualJVDynamic(header, entries, dis.disType.equalsIgnoreCase("CASH") ? LedgerDocType.CS : LedgerDocType.CK,
				JournalType.DISBURSEMENT, dis.disDate, details)

		//update parent
		dis.postedLedger = result.returnId
		dis.status = "POSTED"
		dis.posted = true
		dis.postedBy = SecurityUtils.currentLogin()
		save(dis)

		//update
		if(dis.paymentCategory.equalsIgnoreCase("PAYABLE")){
			//add to ap ledger
			Map<String, Object> ledger = new HashMap<>()
			ledger.put('ledgerType', disCat)
			ledger.put('refNo', dis?.disNo)
			ledger.put('refId', dis?.id)
			ledger.put('debit', dis?.voucherAmount)
			ledger.put('credit', 0.00)
			apLedgerServices.upsertApLedger(ledger, dis?.supplier?.id, null);
			//end to ap ledger
		}

		//post to ewt if naa
		def ap = disbursementApServices.apAppByDis(id)
		ap.each {
			if(it.ewtAmount > BigDecimal.ZERO){
				Map<String, Object> ewt = new HashMap<>()
				ewt.put('refId',it.payable.id)
				ewt.put('refNo',it.payable.apNo)
				ewt.put('wtxDate',dis.disDate)
				ewt.put('type','AP') //AP, AROTHERS
				ewt.put('gross',it.appliedAmount) //net of discount
				ewt.put('vatAmount',it.vatAmount) // 0
				ewt.put('netVat', (it.appliedAmount - it.vatAmount)) // same by gross
				ewt.put('ewtAmount',it.ewtAmount) //ewt amounnt
				wtx2307Service.upsert2307(ewt, null, dis.supplier.id)
			}
		}
		//end
		//expense ewt
		def expenseEwt = disbursementWtxServices.disWtxByParent(id)
		expenseEwt.each {
			if(it.ewtAmount > BigDecimal.ZERO){
				Map<String, Object> ewt = new HashMap<>()
				ewt.put('refId',dis.id)
				ewt.put('refNo',dis.disNo)
				ewt.put('wtxDate',dis.disDate)
				ewt.put('type',disCat) //AP, AROTHERS, CK, CS
				ewt.put('gross',dis.voucherAmount) //net of discount
				ewt.put('vatAmount',0) // 0
				ewt.put('netVat',dis.voucherAmount) // same by gross
				ewt.put('ewtAmount',it.ewtAmount) //ewt amounnt
				wtx2307Service.upsert2307(ewt, null, dis.supplier.id)
			}
		}
		//end expense

		//update ap balance
		def p = disbursementApServices.apByDis(id)
		p.each {
			if(!it.posted){
				accountsPayableServices.updateAp(it.payable.id, it.disbursement.disNo, it.appliedAmount)
			}
			//update disbursement Ap posted
			disbursementApServices.updateDisApPosted(it, true)
		}
		//end

		return result
	}


	//save to accounting in post
	@Transactional(rollbackFor = Exception.class)
	Disbursement postToLedgerAccounting(Disbursement disbursement){
		def yearFormat = DateTimeFormatter.ofPattern("yyyy")
		def disburse = super.save(disbursement) as Disbursement
		//ewt rate
		def ewt1 = BigDecimal.ZERO; def ewt2 = BigDecimal.ZERO; def ewt3 = BigDecimal.ZERO;
		def ewt4 = BigDecimal.ZERO; def ewt5 = BigDecimal.ZERO; def ewt7 = BigDecimal.ZERO;
		def ewt10 = BigDecimal.ZERO; def ewt15 = BigDecimal.ZERO; def ewt18 = BigDecimal.ZERO;
		def ewt30 = BigDecimal.ZERO;

		def disburseDetials = disbursementApServices.apAppByDis(disburse.id)
		def expenseEwt = disbursementWtxServices.disWtxByParent(disburse.id)
		def expense = disbursementExpenseServices.disExpByParent(disburse.id)
		def checks = disbursementCheckServices.disCheckByParent(disburse.id)
		disburseDetials.each {
			switch (it.ewtRate) {
				case 1:
					ewt1+=it.ewtAmount
					break;
				case 2:
					ewt2+=it.ewtAmount
					break;
				case 3:
					ewt3+=it.ewtAmount
					break;
				case 4:
					ewt4+=it.ewtAmount
					break;
				case 5:
					ewt5+=it.ewtAmount
					break;
				case 7:
					ewt7+=it.ewtAmount
					break;
				case 10:
					ewt10+=it.ewtAmount
					break;
				case 15:
					ewt15+=it.ewtAmount
					break;
				case 18:
					ewt18+=it.ewtAmount
					break;
				case 30:
					ewt30+=it.ewtAmount
					break;
			}
		}
		//expense ewt
		expenseEwt.each {
			switch (it.ewtRate) {
				case 1:
					ewt1+=it.ewtAmount
					break;
				case 2:
					ewt2+=it.ewtAmount
					break;
				case 3:
					ewt3+=it.ewtAmount
					break;
				case 4:
					ewt4+=it.ewtAmount
					break;
				case 5:
					ewt5+=it.ewtAmount
					break;
				case 7:
					ewt7+=it.ewtAmount
					break;
				case 10:
					ewt10+=it.ewtAmount
					break;
				case 15:
					ewt15+=it.ewtAmount
					break;
				case 18:
					ewt18+=it.ewtAmount
					break;
				case 30:
					ewt30+=it.ewtAmount
					break;
			}
		}
		//ewt rate

		def headerLedger = integrationServices.generateAutoEntries(disburse) {it, mul ->
			it.flagValue = disburse.transType?.flagValue
			List<Disbursement> mulChecks  = []
			List<Disbursement> exp  = []

			// disbursement bank multiple // cash on bank
			if(disburse.disType.equalsIgnoreCase("CHECK")) {
				checks.each { dc ->
					mulChecks << new Disbursement().tap {
						//debit normal side use negative to make it to credit
						it.bank = dc.bank
						it.cashOnBank = dc.amount.round(2) * -1
					}
				}
			}
			mul << mulChecks

			//end disbursement bank multiple

			//cash on hand
			//there's an issue on null
			it.cashOnHand = disburse.cash.round(2) * -1
			it.terminal = disburse.terminal ? disburse.terminal: cashieringService.findOneById(UUID.fromString("b6ebb547-d2d8-4b7c-9bc8-0243a93fd179")) // need to change if not present into database

			//initialize value_*
			it.value_a = BigDecimal.ZERO;it.value_b = BigDecimal.ZERO;it.value_c = BigDecimal.ZERO;it.value_d = BigDecimal.ZERO;
			it.value_e = BigDecimal.ZERO;it.value_f = BigDecimal.ZERO;it.value_g = BigDecimal.ZERO;it.value_h = BigDecimal.ZERO;
			it.value_i = BigDecimal.ZERO;it.value_j = BigDecimal.ZERO;it.value_k = BigDecimal.ZERO;it.value_l = BigDecimal.ZERO;
			it.value_m = BigDecimal.ZERO;it.value_n = BigDecimal.ZERO;it.value_o = BigDecimal.ZERO;it.value_p = BigDecimal.ZERO;
			it.value_q = BigDecimal.ZERO;it.value_r = BigDecimal.ZERO;it.value_s = BigDecimal.ZERO;it.value_t = BigDecimal.ZERO;
			it.value_u = BigDecimal.ZERO;it.value_v = BigDecimal.ZERO;it.value_w = BigDecimal.ZERO;it.value_x = BigDecimal.ZERO;
			it.value_y = BigDecimal.ZERO;it.value_z = BigDecimal.ZERO;

			it.value_z1 = BigDecimal.ZERO;it.value_z2 = BigDecimal.ZERO;it.value_z3 = BigDecimal.ZERO;it.value_z4 = BigDecimal.ZERO;
			it.value_z5 = BigDecimal.ZERO;it.value_z6 = BigDecimal.ZERO;it.value_z7 = BigDecimal.ZERO;it.value_z8 = BigDecimal.ZERO;
			it.value_z9 = BigDecimal.ZERO;it.value_z10 = BigDecimal.ZERO;it.value_z11 = BigDecimal.ZERO;it.value_z12 = BigDecimal.ZERO;
			it.value_z13 = BigDecimal.ZERO;it.value_z14 = BigDecimal.ZERO;it.value_z15 = BigDecimal.ZERO;it.value_z16 = BigDecimal.ZERO;
			it.value_z17 = BigDecimal.ZERO;it.value_z18 = BigDecimal.ZERO;it.value_z19 = BigDecimal.ZERO;it.value_z20 = BigDecimal.ZERO;
			it.value_z21 = BigDecimal.ZERO;it.value_z22 = BigDecimal.ZERO;it.value_z23 = BigDecimal.ZERO;it.value_z24 = BigDecimal.ZERO;
			it.value_z25 = BigDecimal.ZERO;it.value_z26 = BigDecimal.ZERO;

			it.discAmount = disburse.discountAmount.round(2)

			//default department (MMD) b9a940c0-526f-4098-b020-a95dec316e47
			it.department = departmentRepository.findById(UUID.fromString("b9a940c0-526f-4098-b020-a95dec316e47")).get()

			if(disburse.paymentCategory.equalsIgnoreCase("EXPENSE")){
				expense.each {a ->
					//=== for multiple  ===//
					exp << new Disbursement().tap {
						//default department (MMD) b9a940c0-526f-4098-b020-a95dec316e47
						it.department = departmentRepository.findById(UUID.fromString("b9a940c0-526f-4098-b020-a95dec316e47")).get()

						it.value_a = BigDecimal.ZERO;it.value_b = BigDecimal.ZERO;it.value_c = BigDecimal.ZERO;it.value_d = BigDecimal.ZERO;
						it.value_e = BigDecimal.ZERO;it.value_f = BigDecimal.ZERO;it.value_g = BigDecimal.ZERO;it.value_h = BigDecimal.ZERO;
						it.value_i = BigDecimal.ZERO;it.value_j = BigDecimal.ZERO;it.value_k = BigDecimal.ZERO;it.value_l = BigDecimal.ZERO;
						it.value_m = BigDecimal.ZERO;it.value_n = BigDecimal.ZERO;it.value_o = BigDecimal.ZERO;it.value_p = BigDecimal.ZERO;
						it.value_q = BigDecimal.ZERO;it.value_r = BigDecimal.ZERO;it.value_s = BigDecimal.ZERO;it.value_t = BigDecimal.ZERO;
						it.value_u = BigDecimal.ZERO;it.value_v = BigDecimal.ZERO;it.value_w = BigDecimal.ZERO;it.value_x = BigDecimal.ZERO;
						it.value_y = BigDecimal.ZERO;it.value_z = BigDecimal.ZERO;

						it.value_z1 = BigDecimal.ZERO;it.value_z2 = BigDecimal.ZERO;it.value_z3 = BigDecimal.ZERO;it.value_z4 = BigDecimal.ZERO;
						it.value_z5 = BigDecimal.ZERO;it.value_z6 = BigDecimal.ZERO;it.value_z7 = BigDecimal.ZERO;it.value_z8 = BigDecimal.ZERO;
						it.value_z9 = BigDecimal.ZERO;it.value_z10 = BigDecimal.ZERO;it.value_z11 = BigDecimal.ZERO;it.value_z12 = BigDecimal.ZERO;
						it.value_z13 = BigDecimal.ZERO;it.value_z14 = BigDecimal.ZERO;it.value_z15 = BigDecimal.ZERO;it.value_z16 = BigDecimal.ZERO;
						it.value_z17 = BigDecimal.ZERO;it.value_z18 = BigDecimal.ZERO;it.value_z19 = BigDecimal.ZERO;it.value_z20 = BigDecimal.ZERO;
						it.value_z21 = BigDecimal.ZERO;it.value_z22 = BigDecimal.ZERO;it.value_z23 = BigDecimal.ZERO;it.value_z24 = BigDecimal.ZERO;
						it.value_z25 = BigDecimal.ZERO;it.value_z26 = BigDecimal.ZERO;

						if(a.department?.id){
							it.department = a.department
						}
						if(a.transType.isReverse){
							it[a.transType.source] += a.amount.round(2) * -1
						}else{
							it[a.transType.source] += a.amount.round(2)
						}

						mul << exp
					}

					//=== for normal ===//
					if(a.department?.id){
						it.department = a.department
					}
					if(a.transType.isReverse){
						it[a.transType.source] += a.amount.round(2) * -1
					}else{
						it[a.transType.source] += a.amount.round(2)
					}
				}

				//make it zero for expense
				it.supplierAmount = BigDecimal.ZERO
				it.advancesSupplier = BigDecimal.ZERO
			}else{
				if(disburse.isAdvance){
					// debit normal side no need to negative
					it.advancesSupplier =disburse.voucherAmount.round(2)
					it.supplierAmount = BigDecimal.ZERO
				}else{
					// credit normal side make it negative to debit
					it.supplierAmount = disburse.voucherAmount.round(2) * -1
					it.advancesSupplier = BigDecimal.ZERO
				}
			}


			it.ewt1Percent = ewt1.round(2)
			it.ewt2Percent = ewt2.round(2)
			it.ewt3Percent = ewt3.round(2)
			it.ewt4Percent = ewt4.round(2)
			it.ewt5Percent = ewt5.round(2)
			it.ewt7Percent = ewt7.round(2)
			it.ewt10Percent = ewt10.round(2)
			it.ewt15Percent = ewt15.round(2)
			it.ewt18Percent = ewt18.round(2)
			it.ewt30Percent = ewt30.round(2)
			//

		}

		Map<String,String> details = [:]

		disburse.details.each { k,v ->
			details[k] = v
		}

		details["DISBURSEMENT_ID"] = disburse.id.toString()
		details["SUPPLIER_ID"] = disburse.supplier.id.toString()

		def pHeader =	ledgerServices.persistHeaderLedger(headerLedger,
				"${disburse.disDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${disburse.disNo}",
				"${disburse.disNo}-${disburse.supplier.supplierFullname}",
				"${disburse.disNo}-${disburse.remarksNotes}",
				disburse.disType.equalsIgnoreCase("CASH") ? LedgerDocType.CS : LedgerDocType.CK, // CS = CASH , CK = CHECK
				JournalType.DISBURSEMENT,
				disburse.disDate,
				details)
		disburse.postedLedger = pHeader.id
		disburse.status = "POSTED"
		disburse.posted = true
		disburse.postedBy = SecurityUtils.currentLogin()

//		if(disburse.supplierAmount < 0.0)
//		{
//			pHeader.reversal = true
//			ledgerServices.save(pHeader)
//		}
		save(disburse)
	}

	@Transactional(rollbackFor = Exception.class)
	Disbursement updateForRelease(UUID id){
		def up = findOne(id)
		up.isRelease = true
		save(up)
	}

	@Transactional(rollbackFor = Exception.class)
	Disbursement updateForReapplicationPost(UUID id,
	BigDecimal disc, BigDecimal ewt, BigDecimal amount, Boolean isVoid){

		def up = findOne(id)
		if(isVoid){
			up.discountAmount = up.discountAmount - disc
			up.ewtAmount = up.ewtAmount - ewt
			up.appliedAmount = up.appliedAmount - amount
			up.voucherAmount = up.voucherAmount - (disc + ewt)
			save(up)
		}else{
			up.discountAmount = up.discountAmount + disc
			up.ewtAmount = up.ewtAmount + ewt
			up.appliedAmount = up.appliedAmount + amount
			up.voucherAmount = up.voucherAmount + (disc + ewt)
			save(up)
		}

	}

	@Transactional(rollbackFor = Exception.class)
	Disbursement updateRemove(UUID id, String type, BigDecimal value){
		def up = findOne(id)
		if(type.equalsIgnoreCase("EX")){
			up.appliedAmount = up.appliedAmount - value
		}else if(type.equalsIgnoreCase("CK")){
			up.checks = up.checks - value
			up.voucherAmount = up.voucherAmount - value
		}else if(type.equalsIgnoreCase("WTX")){
			up.ewtAmount = up.ewtAmount - value
			up.voucherAmount = up.voucherAmount - value
		}
		save(up)
	}

	@Transactional(rollbackFor = Exception.class)
	Disbursement updateRemoveAp(
			UUID id,
			BigDecimal discountAmount,
			BigDecimal ewtAmount,
			BigDecimal appliedAmount
	){
		def up = findOne(id)
		up.discountAmount = up.discountAmount - discountAmount
		up.ewtAmount = up.ewtAmount - ewtAmount
		up.voucherAmount = up.voucherAmount - (ewtAmount + discountAmount)
		up.appliedAmount = up.appliedAmount - appliedAmount
		save(up)
	}


}
