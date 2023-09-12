package com.hisd3.hismk2.graphqlservices.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.dao.inventory.ReceivingDao
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.accounting.Integration
import com.hisd3.hismk2.domain.accounting.IntegrationItem
import com.hisd3.hismk2.domain.accounting.JournalType
import com.hisd3.hismk2.domain.accounting.Ledger
import com.hisd3.hismk2.domain.accounting.LedgerDocType
import com.hisd3.hismk2.domain.inventory.AccountingCategory
import com.hisd3.hismk2.domain.inventory.InventoryLedger
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.inventory.PurchaseOrder
import com.hisd3.hismk2.domain.inventory.ReceivingReport
import com.hisd3.hismk2.domain.inventory.ReturnSupplier
import com.hisd3.hismk2.domain.inventory.ReturnSupplierItem
import com.hisd3.hismk2.graphqlservices.accounting.IntegrationServices
import com.hisd3.hismk2.graphqlservices.accounting.LedgerServices
import com.hisd3.hismk2.graphqlservices.accounting.TransactionTypeService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.inventory.ReturnSupplierItemRepository
import com.hisd3.hismk2.repository.inventory.ReturnSupplierRepository
import com.hisd3.hismk2.rest.dto.JournalEntryViewDto
import com.hisd3.hismk2.rest.dto.LedgerDto
import com.hisd3.hismk2.rest.dto.QueryErrorException
import com.hisd3.hismk2.rest.dto.RawLedgerDto
import com.hisd3.hismk2.rest.dto.ReturnSupplierItemDto
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.BooleanUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@TypeChecked
@Component
@GraphQLApi
class ReturnSupplierService {
	
	@Autowired
	ReturnSupplierRepository returnSupplierRepository
	
	@Autowired
	ReturnSupplierItemRepository returnSupplierItemRepository
	
	@Autowired
	ReceivingDao receivingDao
	
	@Autowired
	ObjectMapper objectMapper
	
	@Autowired
	GeneratorService generatorService

	@Autowired
	IntegrationServices integrationServices

	@Autowired
	LedgerServices ledgerServices

	@Autowired
	TransactionTypeService transactionTypeService

	@Autowired
	InventoryLedgerService inventoryLedgerService
	
	@GraphQLQuery(name = "returnSupplierByDep", description = "get return supplier list by dep")
	List<ReturnSupplier> getReturnSupplierByDep(@GraphQLArgument(name = "id") UUID id, @GraphQLArgument(name = "filter") String filter) {
		return returnSupplierRepository.findReturnSupplierByDep(id, filter).sort { it.createdDate }.reverse(true)
	}

	@GraphQLQuery(name = "returnSupplierByDepPage", description = "get return supplier list by dep")
	Page<ReturnSupplier> returnSupplierByDepPage(@GraphQLArgument(name = "id") UUID id,
									@GraphQLArgument(name = "filter") String filter,
									@GraphQLArgument(name = "startDate") String startDate,
									@GraphQLArgument(name = "endDate") String endDate,
									@GraphQLArgument(name = "page") Integer page,
									@GraphQLArgument(name = "pageSize") Integer pageSize) {
		return returnSupplierRepository.returnSupplierByDepPage(filter,
				id, startDate, endDate, new PageRequest(page, pageSize, Sort.Direction.DESC, "returnDate"))
	}
	
	@GraphQLQuery(name = "returnSupplierItemById", description = "get return supplier list Item by Id")
	List<ReturnSupplierItem> getReturnSupplierItemById(@GraphQLArgument(name = "id") UUID id) {
		return returnSupplierItemRepository.findItemsByReturnSupplierId(id).sort { it.item.descLong }
	}

	@GraphQLQuery(name = "returnSupplierById")
	ReturnSupplier returnSupplierById(@GraphQLArgument(name = "id") UUID id) {
		if(id){
			return returnSupplierRepository.findById(id).get()
		}else{
			return null;
		}

	}
	
	//MUTATION
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "returnSupplierInsert", description = "insert return supplier")
	ReturnSupplier returnSupplierInsert(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "items") ArrayList<Map<String, Object>> items,
			@GraphQLArgument(name = "id") String id
	
	) {
		ReturnSupplier upsert = new ReturnSupplier()
		def ret = objectMapper.convertValue(fields, ReturnSupplier)
		def retItems = items as ArrayList<ReturnSupplierItemDto>
		try {
			if (id) {
				upsert = returnSupplierRepository.findById(UUID.fromString(id)).get()
				upsert.returnDate = ret.returnDate
				upsert.refSrr = ret.refSrr
				upsert.receivedRefNo = ret.receivedRefNo
				upsert.receivedRefDate = ret.receivedRefDate
				upsert.department = ret.department
				upsert.supplier = ret.supplier
				upsert.received_by = ret.received_by
				upsert.accType = ret.accType
				
				if (retItems.size()) {
					retItems.each {
						def item = objectMapper.convertValue(it.item, Item.class)
						if(it.isNew){
							ReturnSupplierItem ins = new ReturnSupplierItem()
							ins.returnSupplier = upsert
							ins.item = item
							ins.returnQty = it.returnQty
							ins.returnUnitCost = it.returnUnitCost
							ins.return_remarks = it.return_remarks
							ins.originalQty = 0
							ins.isPosted = it.isPosted
							returnSupplierItemRepository.save(ins)
						}else {
							ReturnSupplierItem ins = returnSupplierItemRepository.findById(UUID.fromString(it.id)).get()
							ins.returnSupplier = upsert
							ins.item = item
							ins.returnQty = it.returnQty
							ins.returnUnitCost = it.returnUnitCost
							ins.return_remarks = it.return_remarks
							ins.originalQty = 0
							ins.isPosted = it.isPosted
							returnSupplierItemRepository.save(ins)
						}
					}
				}
				returnSupplierRepository.save(upsert)
			} else {
				upsert.rtsNo = generatorService.getNextValue(GeneratorType.RET_SUP) { Long no ->
					'RTS-' + StringUtils.leftPad(no.toString(), 6, "0")
				}
				upsert.returnDate = ret.returnDate
				upsert.refSrr = ret.refSrr
				upsert.receivedRefNo = ret.receivedRefNo
				upsert.receivedRefDate = ret.receivedRefDate
				upsert.department = ret.department
				upsert.supplier = ret.supplier
				upsert.received_by = ret.received_by
				upsert.returnBy = ret.returnBy
				upsert.returnUser = ret.returnUser
				upsert.accType = ret.accType
				upsert.isPosted = false
				upsert.isVoid = false
				def afterSave = returnSupplierRepository.save(upsert)
				
				if (retItems.size()) {
					retItems.each {
						def item = objectMapper.convertValue(it.item, Item.class)
						ReturnSupplierItem ins = new ReturnSupplierItem()
						ins.returnSupplier = afterSave
						ins.item = item
						ins.returnQty = it.returnQty
						ins.returnUnitCost = it.returnUnitCost
						ins.return_remarks = it.return_remarks
						ins.originalQty = 0
						ins.isPosted = it.isPosted
						returnSupplierItemRepository.save(ins)
					}
				}
			}
			
		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		
		return upsert
	}

	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "removeReturnSupItem", description = "insert return supplier")
	ReturnSupplierItem removeReturnSupItem(
			@GraphQLArgument(name = "id") UUID id
	) {
		def up = returnSupplierItemRepository.findById(id).get()
		returnSupplierItemRepository.delete(up)
		return up
	}

	//post return
	//post inventory for return to supplier
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "postReturnSupplier")
	GraphQLRetVal<Boolean> postReturnSupplier(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "items") ArrayList<Map<String, Object>> items
	) {
		//get parents
		def parent = returnSupplierRepository.findById(id).get()
		//get items to post
		def postItems =  items as ArrayList<RawLedgerDto>
		try {
			if(postItems){
				postItems.each {

					def source = objectMapper.convertValue(it.source, Department)
					def dest = objectMapper.convertValue(it.destination, Department)

					def item = new LedgerDto(
							sourceDep: source,
							destinationDep: dest,
							documentTypes: UUID.fromString(it.typeId),
							item: UUID.fromString(it.itemId),
							referenceNo: it.ledgerNo,
							ledgerDate: Instant.parse(it.date),
							ledgerQtyIn: 0,
							ledgerQtyOut: it.qty,
							ledgerPhysical: it.physical,
							ledgerUnitCost: it.unitcost,
							isInclude: true,
					)
					inventoryLedgerService.postInventoryGlobal(item)

					//update return item to posted
					def retItem = returnSupplierItemRepository.findById(UUID.fromString(it.id)).get()
					retItem.isPosted = true
					returnSupplierItemRepository.save(retItem)
				}
			}

			//post to accounting --to do accounting entries
			def transType = transactionTypeService.transTypeById(parent.accType)
			if(integrationServices.getIntegrationByDomainAndTagValue(ReturnSupplier.class.name, transType.flagValue)){
				this.saveToJournalEntryReturnSupplier(parent)
			}
			//end post to account

			return new GraphQLRetVal<Boolean>(true,  true,"Items are now posted to ledger")
		} catch (Exception e) {
			throw new Exception("Something was Wrong : " + e)
		}

	}

	//new return journal entries view
	@GraphQLQuery(name = "returnJournalView")
	List<JournalEntryViewDto> returnJournalView(
			@GraphQLArgument(name = "id") UUID id
	){
		def result = new ArrayList<JournalEntryViewDto>()
		def parent = returnSupplierRepository.findById(id).get()
		def items = this.getReturnSupplierItemById(id)
		def transType = transactionTypeService.transTypeById(parent.accType)

		if(!parent.postedLedger && transType){
			Integration match = integrationServices.getIntegrationByDomainAndTagValue(parent.domain, transType.flagValue)

			def headerLedger = integrationServices.generateAutoEntries(parent){
				it, mul ->
					it.flagValue = transType.flagValue

					Map<AccountingCategory, BigDecimal> accounts  = [:]

					//initialize
					Map<String, List<ReturnSupplier>> finalAcc  = [:]
					match.integrationItems.findAll { BooleanUtils.isTrue(it.multiple) }.eachWithIndex { IntegrationItem entry, int i ->
						if(!finalAcc.containsKey(entry.sourceColumn)){
							finalAcc[entry.sourceColumn] = []
						}
					}
					//end init

					BigDecimal supAmount = BigDecimal.ZERO

					//loop items
					items.each { a ->
						if(!accounts.containsKey(a.item.accountingCategory))
							accounts[a.item.accountingCategory] = 0.0

						def sum = a.returnQty * a.returnUnitCost
						accounts[a.item.accountingCategory] =  accounts[a.item.accountingCategory] + sum
						supAmount = supAmount + sum
					}

					//loop accounts
					accounts.each {k, v ->
						if(v > 0){
							finalAcc[k.sourceColumn] << new ReturnSupplier().tap {
								it.cost = BigDecimal.ZERO;
								it.supplierAmount = BigDecimal.ZERO;
								//init asset
								it.asset_a = BigDecimal.ZERO;it.asset_b = BigDecimal.ZERO;it.asset_c = BigDecimal.ZERO;
								it.asset_d = BigDecimal.ZERO;it.asset_e = BigDecimal.ZERO;it.asset_f = BigDecimal.ZERO;
								it.asset_g = BigDecimal.ZERO;it.asset_h = BigDecimal.ZERO;it.asset_i = BigDecimal.ZERO;
								it.asset_j = BigDecimal.ZERO;it.asset_k = BigDecimal.ZERO;it.asset_l = BigDecimal.ZERO;
								it.asset_m = BigDecimal.ZERO;it.asset_n = BigDecimal.ZERO;it.asset_o = BigDecimal.ZERO;
								it.asset_p = BigDecimal.ZERO;it.asset_q = BigDecimal.ZERO;it.asset_r = BigDecimal.ZERO;
								it.asset_s = BigDecimal.ZERO;it.asset_t = BigDecimal.ZERO;it.asset_u = BigDecimal.ZERO;
								it.asset_v = BigDecimal.ZERO;it.asset_w = BigDecimal.ZERO;it.asset_x = BigDecimal.ZERO;
								it.asset_y = BigDecimal.ZERO;it.asset_z = BigDecimal.ZERO;
								//init expense
								it.expense_a = BigDecimal.ZERO;it.expense_b = BigDecimal.ZERO;it.expense_c = BigDecimal.ZERO;
								it.expense_d = BigDecimal.ZERO;it.expense_e = BigDecimal.ZERO;it.expense_f = BigDecimal.ZERO;
								it.expense_g = BigDecimal.ZERO;it.expense_h = BigDecimal.ZERO;it.expense_i = BigDecimal.ZERO;
								it.expense_j = BigDecimal.ZERO;it.expense_k = BigDecimal.ZERO;it.expense_l = BigDecimal.ZERO;
								it.expense_m = BigDecimal.ZERO;it.expense_n = BigDecimal.ZERO;it.expense_o = BigDecimal.ZERO;
								it.expense_p = BigDecimal.ZERO;it.expense_q = BigDecimal.ZERO;it.expense_r = BigDecimal.ZERO;
								it.expense_s = BigDecimal.ZERO;it.expense_t = BigDecimal.ZERO;it.expense_u = BigDecimal.ZERO;
								it.expense_v = BigDecimal.ZERO;it.expense_w = BigDecimal.ZERO;it.expense_x = BigDecimal.ZERO;
								it.expense_y = BigDecimal.ZERO;it.expense_z = BigDecimal.ZERO;

								it.department =  parent.department
								it.category = k
								it[k.sourceColumn] = v * -1; //debit normal pero e credit
								//println("k.sourceColumn => " + k.sourceColumn + " - " + k.categoryDescription)
							}
						}
					}

					//loop multiples
					finalAcc.each { key, accountsRec ->
						mul << accountsRec
					}

					// Revenue for that department
					it.department = parent.department
					it.supplierAmount = supAmount * -1 //credit normal pero e debit
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
			if(parent.postedLedger) {
				def header = ledgerServices.findOne(parent.postedLedger)
				Set<Ledger> ledger = new HashSet<Ledger>(header.ledger);
				ledger.each {
					def list = new JournalEntryViewDto(
							code: it.journalAccount.code,
							desc: it.journalAccount.description,
							debit: it.debit,
							credit: it.credit
					)
					result.add(list)
				}
			}
		}
		return result.sort{it.credit}
	}

	//save to accounting journal
	@Transactional(rollbackFor = QueryErrorException.class)
	ReturnSupplier saveToJournalEntryReturnSupplier(ReturnSupplier returnSupplier){
		def yearFormat = DateTimeFormatter.ofPattern("yyyy")
		def id = returnSupplier.id
		def parent = returnSupplier
		def items = this.getReturnSupplierItemById(id)
		def transType = transactionTypeService.transTypeById(parent.accType)

		if(transType){
			Integration match = integrationServices.getIntegrationByDomainAndTagValue(parent.domain, transType.flagValue)

			def headerLedger = integrationServices.generateAutoEntries(parent){
				it, mul ->
					it.flagValue = transType.flagValue

					Map<AccountingCategory, BigDecimal> accounts  = [:]

					//initialize
					Map<String, List<ReturnSupplier>> finalAcc  = [:]
					match.integrationItems.findAll { BooleanUtils.isTrue(it.multiple) }.eachWithIndex { IntegrationItem entry, int i ->
						if(!finalAcc.containsKey(entry.sourceColumn)){
							finalAcc[entry.sourceColumn] = []
						}
					}
					//end init

					BigDecimal supAmount = BigDecimal.ZERO

					//loop items
					items.each { a ->
						if(!accounts.containsKey(a.item.accountingCategory))
							accounts[a.item.accountingCategory] = 0.0

						def sum = a.returnQty * a.returnUnitCost
						accounts[a.item.accountingCategory] =  accounts[a.item.accountingCategory] + sum
						supAmount = supAmount + sum
					}

					//loop accounts
					accounts.each {k, v ->
						if(v > 0){
							finalAcc[k.sourceColumn] << new ReturnSupplier().tap {
								it.cost = BigDecimal.ZERO;
								it.supplierAmount = BigDecimal.ZERO;
								//init asset
								it.asset_a = BigDecimal.ZERO;it.asset_b = BigDecimal.ZERO;it.asset_c = BigDecimal.ZERO;
								it.asset_d = BigDecimal.ZERO;it.asset_e = BigDecimal.ZERO;it.asset_f = BigDecimal.ZERO;
								it.asset_g = BigDecimal.ZERO;it.asset_h = BigDecimal.ZERO;it.asset_i = BigDecimal.ZERO;
								it.asset_j = BigDecimal.ZERO;it.asset_k = BigDecimal.ZERO;it.asset_l = BigDecimal.ZERO;
								it.asset_m = BigDecimal.ZERO;it.asset_n = BigDecimal.ZERO;it.asset_o = BigDecimal.ZERO;
								it.asset_p = BigDecimal.ZERO;it.asset_q = BigDecimal.ZERO;it.asset_r = BigDecimal.ZERO;
								it.asset_s = BigDecimal.ZERO;it.asset_t = BigDecimal.ZERO;it.asset_u = BigDecimal.ZERO;
								it.asset_v = BigDecimal.ZERO;it.asset_w = BigDecimal.ZERO;it.asset_x = BigDecimal.ZERO;
								it.asset_y = BigDecimal.ZERO;it.asset_z = BigDecimal.ZERO;
								//init expense
								it.expense_a = BigDecimal.ZERO;it.expense_b = BigDecimal.ZERO;it.expense_c = BigDecimal.ZERO;
								it.expense_d = BigDecimal.ZERO;it.expense_e = BigDecimal.ZERO;it.expense_f = BigDecimal.ZERO;
								it.expense_g = BigDecimal.ZERO;it.expense_h = BigDecimal.ZERO;it.expense_i = BigDecimal.ZERO;
								it.expense_j = BigDecimal.ZERO;it.expense_k = BigDecimal.ZERO;it.expense_l = BigDecimal.ZERO;
								it.expense_m = BigDecimal.ZERO;it.expense_n = BigDecimal.ZERO;it.expense_o = BigDecimal.ZERO;
								it.expense_p = BigDecimal.ZERO;it.expense_q = BigDecimal.ZERO;it.expense_r = BigDecimal.ZERO;
								it.expense_s = BigDecimal.ZERO;it.expense_t = BigDecimal.ZERO;it.expense_u = BigDecimal.ZERO;
								it.expense_v = BigDecimal.ZERO;it.expense_w = BigDecimal.ZERO;it.expense_x = BigDecimal.ZERO;
								it.expense_y = BigDecimal.ZERO;it.expense_z = BigDecimal.ZERO;

								it.department =  parent.department
								it.category = k
								it[k.sourceColumn] = v * -1; //debit normal pero e credit
								//println("k.sourceColumn => " + k.sourceColumn + " - " + k.categoryDescription)
							}
						}
					}

					//loop multiples
					finalAcc.each { key, accountsRec ->
						mul << accountsRec
					}

					// Revenue for that department
					it.department = parent.department
					it.supplierAmount = supAmount * -1 //credit normal pero e debit
			}

			Map<String,String> details = [:]

			parent.details.each { k,v ->
				details[k] = v
			}

			details["RTS_ID"] = parent.id.toString()
			details["SUPPLIER_ID"] = parent.supplier.id.toString()

			def pHeader =	ledgerServices.persistHeaderLedger(headerLedger,
					"${parent.returnDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${parent.rtsNo}",
					"${parent.rtsNo}-${parent.supplier?.supplierFullname}",
					"${parent.rtsNo}-${parent.supplier?.supplierFullname}",
					LedgerDocType.RT,
					JournalType.PURCHASES_PAYABLES,
					parent.createdDate,
					details)

			//update parent
			parent.isPosted = true
			parent.postedLedger = pHeader.id
			returnSupplierRepository.save(parent)

		}
		return parent
	}

	//update status void
	@GraphQLMutation(name = "voidReturnSupplier")
	GraphQLRetVal<Boolean> voidReturnSupplier(
			@GraphQLArgument(name = "id") UUID id
	) {
		//get parents
		def parent = returnSupplierRepository.findById(id).get()
		def postItems = returnSupplierItemRepository.findItemsByReturnSupplierId(id).sort { it.item.descLong }
		//get items to void
		try {
			//reverse accounting entry
			if(parent.postedLedger){
				def header = ledgerServices.findOne(parent.postedLedger)
				ledgerServices.reverseEntriesCustom(header, parent.returnDate)
			}

			if(postItems){
				postItems.each {
					//update mp items to false
					def update = it
					update.isPosted = false
					returnSupplierItemRepository.save(update)
				}
			}
			//void inventory to inventory ledger
			inventoryLedgerService.voidInventoryGlobal(parent.rtsNo)
			//void to accounting --to do accounting entries

			//end void to account
			//update parent
			parent.isPosted = false
			parent.isVoid = true
			parent.postedLedger = null //to be updated for accounting
			returnSupplierRepository.save(parent)
			return new GraphQLRetVal<Boolean>(true,  true,"Items are now voided to ledger")
		} catch (Exception e) {
			throw new Exception("Something was Wrong : " + e)
		}

	}

}
