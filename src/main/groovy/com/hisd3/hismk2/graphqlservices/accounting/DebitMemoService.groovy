package com.hisd3.hismk2.graphqlservices.accounting

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.accounting.AccountsPayable
import com.hisd3.hismk2.domain.accounting.DebitMemo
import com.hisd3.hismk2.domain.accounting.Disbursement
import com.hisd3.hismk2.domain.accounting.DisbursementAp
import com.hisd3.hismk2.domain.accounting.JournalType
import com.hisd3.hismk2.domain.accounting.Ledger
import com.hisd3.hismk2.domain.accounting.LedgerDocType
import com.hisd3.hismk2.domain.accounting.Reapplication
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.rest.dto.AccountPayableDetialsDto
import com.hisd3.hismk2.rest.dto.DepDto
import com.hisd3.hismk2.rest.dto.DisbursementApDto
import com.hisd3.hismk2.rest.dto.DmDetailsDto
import com.hisd3.hismk2.rest.dto.JournalEntryViewDto
import com.hisd3.hismk2.rest.dto.TransTypeDto
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
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
@GraphQLApi
class DebitMemoService extends AbstractDaoService<DebitMemo> {

	@Autowired
	GeneratorService generatorService

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	AccountsPayableServices accountsPayableServices

	@Autowired
	DepartmentRepository departmentRepository

	@Autowired
	ReapplicationService reapplicationService

	@Autowired
	DisbursementApServices disbursementApServices

	@Autowired
	IntegrationServices integrationServices

	@Autowired
	LedgerServices ledgerServices

	@Autowired
	ApLedgerServices apLedgerServices

	@Autowired
	Wtx2307Service wtx2307Service

	@Autowired
	DebitMemoDetailsServices debitMemoDetailsServices


    DebitMemoService() {
		super(DebitMemo.class)
	}
	
	@GraphQLQuery(name = "debitMemoById")
	DebitMemo debitMemoById(
			@GraphQLArgument(name = "id") UUID id
	) {
		if(id){
			findOne(id)
		}else{
			return null
		}

	}

	@GraphQLQuery(name = "debitMemoFilter", description = "List of DM Pageable")
	Page<DebitMemo> debitMemoFilter(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "supplier") UUID supplier,
			@GraphQLArgument(name = "type") String type,
			@GraphQLArgument(name = "status") Boolean status,
			@GraphQLArgument(name = "start") String start,
			@GraphQLArgument(name = "end") String end,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		String query = '''Select dm from DebitMemo dm where
						(lower(dm.debitNo) like lower(concat('%',:filter,'%')))
						and to_date(to_char(dm.debitDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
						between to_date(:start,'YYYY-MM-DD') and to_date(:end,'YYYY-MM-DD')
             			and dm.debitType = :type'''


		String countQuery = '''Select count(dm) from DebitMemo dm where
							(lower(dm.debitNo) like lower(concat('%',:filter,'%')))
							and to_date(to_char(dm.debitDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
							between to_date(:start,'YYYY-MM-DD') and to_date(:end,'YYYY-MM-DD')
							and dm.debitType = :type'''

		Map<String, Object> params = new HashMap<>()
		params.put('filter', filter)
		params.put('type', type)
		params.put('start', start)
		params.put('end', end)

		if (supplier) {
			query += ''' and (dm.supplier.id = :supplier) '''
			countQuery += ''' and (dm.supplier.id = :supplier) '''
			params.put("supplier", supplier)
		}

		if (status) {
			query += ''' and (dm.posted = :status or dm.posted is null) '''
			countQuery += ''' and (dm.posted = :status or dm.posted is null) '''
			params.put("status", !status)
		}

		query += ''' ORDER BY dm.debitNo DESC'''

		getPageable(query, countQuery, page, size, params)
	}


	//mutations
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertDM") //Debit Advice
	DebitMemo upsertDM(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "items") ArrayList<Map<String, Object>> items,
			@GraphQLArgument(name = "id") UUID id
	) {
		def dm = upsertFromMap(id, fields, { DebitMemo entity, boolean forInsert ->
			if (forInsert) {
				entity.debitNo = generatorService.getNextValue(GeneratorType.DM_NO, {
					return "DM-" + StringUtils.leftPad(it.toString(), 6, "0")
				})
				entity.status = "DRAFT"
				entity.posted = false
			}
		})

		def disAp = items as ArrayList<DisbursementApDto>
		disAp.each {
			def dto = objectMapper.convertValue(it, DisbursementApDto.class)
			disbursementApServices.upsertDisDM(dto, dm)
		}

		return dm
	}

	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertDebitMemo") //Debit Memo
	DebitMemo upsertDebitMemo(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "items") ArrayList<Map<String, Object>> items,
			@GraphQLArgument(name = "details") ArrayList<Map<String, Object>> details,
			@GraphQLArgument(name = "id") UUID id
	) {
		def dm = upsertFromMap(id, fields, { DebitMemo entity, boolean forInsert ->
			if (forInsert) {
				entity.debitNo = generatorService.getNextValue(GeneratorType.DM_NO, {
					return "DM-" + StringUtils.leftPad(it.toString(), 6, "0")
				})
				entity.status = "DRAFT"
				entity.posted = false
			}
		})

		def disAp = items as ArrayList<DisbursementApDto>
		disAp.each {
			def dto = objectMapper.convertValue(it, DisbursementApDto.class)
			disbursementApServices.upsertDisDM(dto, dm)
		}

		def trans = details as ArrayList<DmDetailsDto>
		trans.each {
			def dto = objectMapper.convertValue(it, DmDetailsDto.class)
			debitMemoDetailsServices.upsertDmDetials(dto, dm)
		}

		return dm
	}

	@Transactional(rollbackFor = Exception.class)
	DebitMemo updateDMforRemove(
			UUID id,
			BigDecimal discountAmount,
			BigDecimal ewtAmount,
			BigDecimal appliedAmount
	){
		def up = findOne(id)
		up.discount = up.discount - discountAmount
		up.ewtAmount = up.ewtAmount - ewtAmount
		up.memoAmount = up.memoAmount - (appliedAmount - ewtAmount - discountAmount)
		up.appliedAmount = up.appliedAmount - appliedAmount
		save(up)
	}

	//ACCOUNTS TO VIEW
	@GraphQLQuery(name = "dmAccountView")
	List<JournalEntryViewDto> dmAccountView(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "status") Boolean status
	) {
		def result = new ArrayList<JournalEntryViewDto>()
		//ewt rate
		if(id) {
			def dm = findOne(id)
			def ewt1 = BigDecimal.ZERO; def ewt2 = BigDecimal.ZERO; def ewt3 = BigDecimal.ZERO;
			def ewt4 = BigDecimal.ZERO; def ewt5 = BigDecimal.ZERO; def ewt7 = BigDecimal.ZERO;
			def ewt10 = BigDecimal.ZERO; def ewt15 = BigDecimal.ZERO; def ewt18 = BigDecimal.ZERO;
			def ewt30 = BigDecimal.ZERO;

			def dmDetails = disbursementApServices.apDebitMemo(dm.id)
			def trans = debitMemoDetailsServices.dmDetials(dm.id)

			//ewt rate start here
			dmDetails.each {
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

			if(dm.transType?.flagValue){
				def headerLedger = integrationServices.generateAutoEntries(dm) {it, mul ->
					it.flagValue = dm.transType?.flagValue

					List<DebitMemo> exp  = []

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

					//default department (MMD) b9a940c0-526f-4098-b020-a95dec316e47
					it.department = departmentRepository.findById(UUID.fromString("b9a940c0-526f-4098-b020-a95dec316e47")).get()

					if(dm.debitType.equalsIgnoreCase("DEBIT_MEMO")){
						it.supplierAmount = status ? dm.memoAmount.round(2) * -1 : dm.memoAmount.round(2) //default credit side
						trans.each { a ->
							//=== for multiple  ===//
							exp << new DebitMemo().tap {
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
									//credit default
								} else {
									it[a.transType.source] += status ? a.amount.round(2) : a.amount.round(2) * -1
									//credit default
								}

								mul << exp
							}
							//=== for normal ===//
							if (a.department?.id) {
								it.department = a.department
							}
							if (a.transType.isReverse) {
								it[a.transType.source] += status ? a.amount.round(2) * -1 : a.amount.round(2)
								//credit default
							} else {
								it[a.transType.source] += status ? a.amount.round(2) : a.amount.round(2) * -1
								//credit default
							}
						}
					}else{
						it.supplierAmount = status ? dm.appliedAmount.round(2) * -1 : dm.appliedAmount.round(2) //default credit side
						it.bank = dm.bank
					}

					it.cashOnBank = status ? dm.memoAmount.round(2) * -1 : dm.memoAmount.round(2) //default debit side
					it.discAmount = status ? dm.discount.round(2) : dm.discount.round(2) * -1 //default debit side

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
				if(dm.postedLedger){
					def header = ledgerServices.findOne(dm.postedLedger)
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
		return result.sort{it.credit}
	}


	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "postDM")
	DebitMemo postDM(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "status") Boolean status
	) {
		def dm = findOne(id)
		if(status){
			def header = ledgerServices.findOne(dm.postedLedger)
			ledgerServices.reverseEntriesCustom(header, dm.debitDate)
			//update AP
			dm.postedLedger = null
			dm.status = "DRAFT"
			dm.posted = false
			dm.postedBy = null
			save(dm)
			//remove ap ledger
			apLedgerServices.removeApLedger(dm.debitNo)

			def ap = disbursementApServices.apDebitMemo(id)
			ap.each {
				if(it.ewtAmount > BigDecimal.ZERO){
					wtx2307Service.remove2307(it.payable.id)
				}
				//update ap inag void
				if(dm.debitType.equalsIgnoreCase("DEBIT_ADVICE")){
					accountsPayableServices.updateApForRemoveDM(it.payable.id, it.debitMemo.debitNo, it.appliedAmount, it.posted, "DA")
				}else{
					accountsPayableServices.updateApForRemoveDM(it.payable.id, it.debitMemo.debitNo, dm.memoAmount, it.posted, "DM")
				}
				//update disAp posted = false
				disbursementApServices.updateDisApPosted(it, false)
			}

			//end remover ap ledger
		}else{
			postToLedgerAccounting(dm)

			//add to ap ledger
			Map<String, Object> ledger = new HashMap<>()
			ledger.put('ledgerType', 'DM')
			ledger.put('refNo', dm?.debitNo)
			ledger.put('refId', dm?.id)
			if(dm.debitType.equalsIgnoreCase("DEBIT_MEMO")){
				ledger.put('debit', dm?.memoAmount)
			}else{
				ledger.put('debit', dm?.appliedAmount)
			}
			ledger.put('credit', 0.00)
			apLedgerServices.upsertApLedger(ledger, dm?.supplier?.id, null);
			//end to ap ledger

			//post to ewt if naa
			def ap = disbursementApServices.apDebitMemo(id)
			ap.each {
				if(it.ewtAmount > BigDecimal.ZERO){
					Map<String, Object> ewt = new HashMap<>()
					ewt.put('refId',it.payable.id)
					ewt.put('refNo',it.payable.apNo)
					ewt.put('wtxDate',dm.debitDate)
					ewt.put('type','AP') //AP, AROTHERS
					ewt.put('gross',it.appliedAmount) //net of discount
					ewt.put('vatAmount',it.vatAmount) // 0
					ewt.put('netVat',(it.appliedAmount - it.vatAmount)) // same by gross
					ewt.put('ewtAmount',it.ewtAmount) //ewt amounnt
					wtx2307Service.upsert2307(ewt, null, dm.supplier.id)
				}
			}
			//end

			//update ap balance
			def p = disbursementApServices.apDebitMemo(id)
			p.each {
				if(!it.posted){
					if(dm.debitType.equalsIgnoreCase("DEBIT_ADVICE")){
						accountsPayableServices.updateApFromDM(it.payable.id, it.debitMemo.debitNo, it.appliedAmount, "DA")
					}else{
						accountsPayableServices.updateApFromDM(it.payable.id, it.debitMemo.debitNo, dm.memoAmount, "DM")
					}
				}
				//update disbursement Ap posted
				disbursementApServices.updateDisApPosted(it, true)
			}
			//end
		}
		return dm
	}

	@Transactional(rollbackFor = Exception.class)
	DebitMemo postToLedgerAccounting(DebitMemo dm){
		def yearFormat = DateTimeFormatter.ofPattern("yyyy")
		def debitMemo = super.save(dm) as DebitMemo
		//ewt rate
		def ewt1 = BigDecimal.ZERO; def ewt2 = BigDecimal.ZERO; def ewt3 = BigDecimal.ZERO;
		def ewt4 = BigDecimal.ZERO; def ewt5 = BigDecimal.ZERO; def ewt7 = BigDecimal.ZERO;
		def ewt10 = BigDecimal.ZERO; def ewt15 = BigDecimal.ZERO; def ewt18 = BigDecimal.ZERO;
		def ewt30 = BigDecimal.ZERO;

		def dmDetails = disbursementApServices.apDebitMemo(dm.id)
		def trans = debitMemoDetailsServices.dmDetials(dm.id)
		//ewt rate start here
		dmDetails.each {
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

		def headerLedger = integrationServices.generateAutoEntries(debitMemo) { it, mul ->
			it.flagValue = dm.transType?.flagValue

			List<DebitMemo> exp  = []

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

			//default department (MMD) b9a940c0-526f-4098-b020-a95dec316e47
			it.department = departmentRepository.findById(UUID.fromString("b9a940c0-526f-4098-b020-a95dec316e47")).get()

			if(dm.debitType.equalsIgnoreCase("DEBIT_MEMO")){
				it.supplierAmount = dm.memoAmount.round(2) * -1
				trans.each { a ->
					//=== for multiple ===//
					exp << new DebitMemo().tap {
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
							//credit
						} else {
							it[a.transType.source] += status ? a.amount.round(2) : a.amount.round(2) * -1
							//debit
						}

						mul << exp
					}
					//=== for normal ===//
					if(a.department?.id){
						it.department = a.department
					}
					if(a.transType.isReverse){
						it[a.transType.source] += a.amount.round(2) * -1 //credit
					}else{
						it[a.transType.source] += a.amount.round(2) //debit
					}
				}
			}else{
				it.supplierAmount = dm.appliedAmount.round(2) * -1
				it.bank = dm.bank
			}

			it.cashOnBank = dm.memoAmount.round(2) * -1
			it.discAmount = dm.discount.round(2)

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

		dm.details.each { k,v ->
			details[k] = v
		}

		details["DEBITMEMO_ID"] = dm.id.toString()
		details["SUPPLIER_ID"] = dm.supplier.id.toString()

		def pHeader =	ledgerServices.persistHeaderLedger(headerLedger,
				"${dm.debitDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${dm.debitNo}",
				"${dm.debitNo}-${dm.supplier.supplierFullname}",
				"${dm.debitNo}-${dm.remarksNotes}",
				LedgerDocType.DM,
				JournalType.GENERAL,
				dm.debitDate,
				details)
		dm.postedLedger = pHeader.id
		dm.status = "POSTED"
		dm.posted = true
		dm.postedBy = SecurityUtils.currentLogin()

		save(dm)
	}

	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "postDManual")
	GraphQLRetVal<Boolean> postDManual(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "header")  Map<String,Object>  header,
			@GraphQLArgument(name = "entries")  List<Map<String,Object>>  entries
	) {
		def dm = findOne(id)

		Map<String,String> details = [:]

		dm.details.each { k,v ->
			details[k] = v
		}

		details["DEBITMEMO_ID"] = dm.id.toString()
		details["SUPPLIER_ID"] = dm.supplier.id.toString()

		def result = ledgerServices.addManualJVDynamic(header, entries, LedgerDocType.DM,
				JournalType.GENERAL, dm.debitDate, details)

		//update parent
		dm.postedLedger = result.returnId
		dm.status = "POSTED"
		dm.posted = true
		dm.postedBy = SecurityUtils.currentLogin()
		save(dm)

		//update after post
		//add to ap ledger
		Map<String, Object> ledger = new HashMap<>()
		ledger.put('ledgerType', 'DM')
		ledger.put('refNo', dm?.debitNo)
		ledger.put('refId', dm?.id)
		if(dm.debitType.equalsIgnoreCase("DEBIT_MEMO")){
			ledger.put('debit', dm?.memoAmount)
		}else{
			ledger.put('debit', dm?.appliedAmount)
		}
		ledger.put('credit', 0.00)
		apLedgerServices.upsertApLedger(ledger, dm?.supplier?.id, null);
		//end to ap ledger

		//post to ewt if naa
		def ap = disbursementApServices.apDebitMemo(id)
		ap.each {
			if(it.ewtAmount > BigDecimal.ZERO){
				Map<String, Object> ewt = new HashMap<>()
				ewt.put('refId',it.payable.id)
				ewt.put('refNo',it.payable.apNo)
				ewt.put('wtxDate',dm.debitDate)
				ewt.put('type','AP') //AP, AROTHERS
				ewt.put('gross',it.appliedAmount) //net of discount
				ewt.put('vatAmount',it.vatAmount) // 0
				ewt.put('netVat',(it.appliedAmount - it.vatAmount)) // same by gross
				ewt.put('ewtAmount',it.ewtAmount) //ewt amounnt
				wtx2307Service.upsert2307(ewt, null, dm.supplier.id)
			}
		}
		//end

		//update ap balance
		def p = disbursementApServices.apDebitMemo(id)
		p.each {
			if(!it.posted){
				if(dm.debitType.equalsIgnoreCase("DEBIT_ADVICE")){
					accountsPayableServices.updateApFromDM(it.payable.id, it.debitMemo.debitNo, it.appliedAmount, "DA")
				}else{
					accountsPayableServices.updateApFromDM(it.payable.id, it.debitMemo.debitNo, dm.memoAmount, "DM")
				}
			}
			//update disbursement Ap posted
			disbursementApServices.updateDisApPosted(it, true)
		}
		//end

		return result
	}



}
