package com.hisd3.hismk2.graphqlservices.inventory
import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.accounting.HeaderLedger
import com.hisd3.hismk2.domain.accounting.Integration
import com.hisd3.hismk2.domain.accounting.IntegrationItem
import com.hisd3.hismk2.domain.accounting.JournalType
import com.hisd3.hismk2.domain.accounting.Ledger
import com.hisd3.hismk2.domain.accounting.LedgerDocType
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.inventory.AccountingCategory
import com.hisd3.hismk2.domain.inventory.DepartmentStockIssue
import com.hisd3.hismk2.domain.inventory.DepartmentStockIssueItems
import com.hisd3.hismk2.domain.inventory.DepartmentStockRequest
import com.hisd3.hismk2.domain.inventory.DepartmentStockRequestItem
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.inventory.ReceivingReport
import com.hisd3.hismk2.domain.inventory.ReturnSupplier
import com.hisd3.hismk2.graphqlservices.accounting.IntegrationServices
import com.hisd3.hismk2.graphqlservices.accounting.LedgerServices
import com.hisd3.hismk2.graphqlservices.accounting.TransactionTypeService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.inventory.DepartmentStockIssueItemRepository
import com.hisd3.hismk2.repository.inventory.DepartmentStockIssueRepository
import com.hisd3.hismk2.repository.inventory.DepartmentStockRequestItemRepository
import com.hisd3.hismk2.repository.inventory.DepartmentStockRequestRepository
import com.hisd3.hismk2.repository.inventory.ItemRepository
import com.hisd3.hismk2.rest.InventoryResource
import com.hisd3.hismk2.rest.dto.IssuedItems
import com.hisd3.hismk2.rest.dto.JournalEntryViewDto
import com.hisd3.hismk2.rest.dto.LedgerDto
import com.hisd3.hismk2.rest.dto.QueryErrorException
import com.hisd3.hismk2.rest.dto.RawLedgerDto
import com.hisd3.hismk2.rest.dto.StockIssueDto
import com.hisd3.hismk2.rest.dto.UpdateStockRequestDto
import com.hisd3.hismk2.rest.dto.UpdateStockRequestItem
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import com.sun.org.apache.xpath.internal.operations.Bool
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

import javax.transaction.Transactional
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


enum INV_INTEGRATION {
	INV_STOCK_ISSUE
}

class InvStockIssueJMap {
	Map<String, InvStockIssueJMapValues> stockIssueJMapValuesMap = [:]
}
class InvStockIssueJMapValues {
	BigDecimal medicine = 0.00
	BigDecimal medicalSupp = 0.00
	BigDecimal supplies = 0.00
	BigDecimal negativeMedicinesAmt = 0.00
	BigDecimal negativeMedicalSupp = 0.00
	BigDecimal negativeSuppliesAmt = 0.00
	BigDecimal expenseMedicines = 0.00
	BigDecimal expenseSupplies = 0.00
	BigDecimal expenseMedicalSupp = 0.00
}

@Component
@GraphQLApi
@TypeChecked
class DepartmentStockIssueService {
	
	@Autowired
	DepartmentStockIssueRepository departmentStockIssueRepository
	
	@Autowired
	private ObjectMapper objectMapper
	
	@Autowired
	GeneratorService generatorService
	
	@Autowired
	ItemRepository itemRepository
	
	@Autowired
	EmployeeRepository employeeRepository
	
	@Autowired
	DepartmentRepository departmentRepository
	
	@Autowired
	InventoryResource inventoryResource
	
	@Autowired
	DepartmentStockIssueItemRepository departmentStockIssueItemRepository

	@Autowired
	DepartmentStockRequestRepository departmentStockRequestRepository

	@Autowired
	DepartmentStockRequestItemRepository departmentStockRequestItemRepository

	@Autowired
	IntegrationServices integrationServices

	@Autowired
	LedgerServices ledgerServices

	@Autowired
	TransactionTypeService transactionTypeService

	@Autowired
	InventoryLedgerService inventoryLedgerService

	//
	@GraphQLQuery(name = "depIssuanceById")
	DepartmentStockIssue depIssuanceById(@GraphQLArgument(name = "id") UUID id) {
		if(id){
			return departmentStockIssueRepository.findById(id).get()
		}else {
			return null;
		}

	}

	@GraphQLQuery(name = "departmentStockIssueList", description = "List of Departmental Stock Issue")
	List<DepartmentStockIssue> getDepartmentStockIssue() {
		return departmentStockIssueRepository.findAll().sort { it.createdDate }.reverse(true)
	}
	
	@GraphQLQuery(name = "departmentStockIssueByDep", description = "List of Departmental Stock Issue By Department")
	List<DepartmentStockIssue> getDepartmentStockIssueByDep(@GraphQLArgument(name = "id") UUID id, @GraphQLArgument(name = "filter") String filter) {
		return departmentStockIssueRepository.findIssueByDep(id, filter).sort { it.createdDate }.reverse(true)
	}

	@GraphQLQuery(name = "getDepItemIssuance", description = "get incoming request list by dep")
	Page<DepartmentStockIssue> getDepItemIssuance(@GraphQLArgument(name = "id") UUID id,
													@GraphQLArgument(name = "filter") String filter,
													@GraphQLArgument(name = "startDate") String startDate,
													@GraphQLArgument(name = "endDate") String endDate,
													@GraphQLArgument(name = "page") Integer page,
													@GraphQLArgument(name = "pageSize") Integer pageSize) {
		return departmentStockIssueRepository.getDepItemIssuance(filter,
				id, startDate, endDate, new PageRequest(page, pageSize, Sort.Direction.DESC, "issueNo"))
	}
	
	// mutation //
	@Transactional(rollbackOn = Exception)
	@GraphQLMutation(name = "upsertItemIssuance")
	DepartmentStockIssue upsertItemIssuance(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "issueItems") ArrayList<Map<String, Object>> issueItems
	) {
		def issue = new DepartmentStockIssue() //new for default
		def obj = objectMapper.convertValue(fields, StockIssueDto.class)
		def items = issueItems as ArrayList<IssuedItems>
		if (id) {//update
			issue = departmentStockIssueRepository.findById(id).get()
			issue.issueDate = obj.issued_date
			issue.issueTo = departmentRepository.findById(UUID.fromString(obj.issue_to)).get()
			issue.issued_by = employeeRepository.findById(UUID.fromString(obj.issued_by)).get()
			issue.claimed_by = employeeRepository.findById(UUID.fromString(obj.claimed_by)).get()
			issue.issueType = obj.issue_type
			
			items.each {
				it ->
					def item = objectMapper.convertValue(it.item, Item)
					if (it.isNew) {
						def list = new DepartmentStockIssueItems()
						list.stockIssue = issue
						list.item = itemRepository.findById(item.id).get()
						list.issueQty = it.issueQty
						list.unitCost = inventoryResource.getLastUnitPrice(item.id as String)
						list.isPosted = it.isPosted
						departmentStockIssueItemRepository.save(list)
					} else {
						def up = departmentStockIssueItemRepository.findById(UUID.fromString(it.id)).get()
						up.issueQty = it.issueQty
						up.isPosted = it.isPosted
						departmentStockIssueItemRepository.save(up)
					}
				
			}
			departmentStockIssueRepository.save(issue)
		} else {//insert
			issue = new DepartmentStockIssue()
			issue.issueNo = generatorService.getNextValue(GeneratorType.ISSUE_NO) { Long no ->
				'DSI-' + StringUtils.leftPad(no.toString(), 6, "0")
			}
			issue.issueDate = obj.issued_date
			issue.issueFrom = departmentRepository.findById(UUID.fromString(obj.issued_from)).get()
			issue.issueTo = departmentRepository.findById(UUID.fromString(obj.issue_to)).get()
			issue.issueType = obj.issue_type
			issue.issued_by = employeeRepository.findById(UUID.fromString(obj.issued_by)).get()
			issue.claimed_by = employeeRepository.findById(UUID.fromString(obj.claimed_by)).get()
			issue.isCancel = false
			issue.isPosted = false
			def issueSave = departmentStockIssueRepository.save(issue)
			
			items.each {
				it ->
					def item = objectMapper.convertValue(it.item, Item)
					def list = new DepartmentStockIssueItems()
					list.stockIssue = issueSave
					list.item = itemRepository.findById(item.id).get()
					list.issueQty = it.issueQty
					list.unitCost = inventoryResource.getLastUnitPrice(item.id as String)
					list.isPosted = it.isPosted
					departmentStockIssueItemRepository.save(list)
			}
		}
		return issue
	}


	//update ni WIlson
	@Transactional(rollbackOn = Exception)
	@GraphQLMutation(name = "upsertDepIssuance")
	DepartmentStockIssue upsertDepIssuance(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "issueItems") ArrayList<Map<String, Object>> issueItems
	) {
		def issue = new DepartmentStockIssue() //new for default
		def obj = objectMapper.convertValue(fields, StockIssueDto.class)
		def items = issueItems as ArrayList<IssuedItems>
		def req = null;
		if (id) {//update
			issue = departmentStockIssueRepository.findById(id).get()
			issue.issueDate = obj.issued_date
			issue.issueTo = departmentRepository.findById(UUID.fromString(obj.issue_to)).get()
			issue.claimed_by = employeeRepository.findById(UUID.fromString(obj.claimed_by)).get()
			issue.issueType = obj.issue_type
			if(obj.request){
				req = departmentStockRequestRepository.findById(UUID.fromString(obj.request)).get()
				issue.request = req
				issue.requestNo = req.requestNo

				//update request parent
				req.preparedBy = issue.issued_by
				req.dispensedBy = issue.issued_by
				req.claimedBy = employeeRepository.findById(UUID.fromString(obj.claimed_by)).get()
				departmentStockRequestRepository.save(req)
			}
			if(obj.acc_type){
				issue.accType = UUID.fromString(obj.acc_type)
			}

			items.each {
				it ->
					def item = objectMapper.convertValue(it.item, Item)
					if (it.isNew) {
						def list = new DepartmentStockIssueItems()
						list.stockIssue = issue
						list.item = itemRepository.findById(item.id).get()
						list.issueQty = it.issueQty
						list.requestedQty = it.requestedQty
						list.unitCost = inventoryResource.getLastUnitPrice(item.id as String)
						list.remarks = it.remarks
						list.isPosted = it.isPosted
						departmentStockIssueItemRepository.save(list)
					} else {
						def up = departmentStockIssueItemRepository.findById(UUID.fromString(it.id)).get()
						up.issueQty = it.issueQty
						up.remarks = it.remarks
						up.isPosted = it.isPosted
						departmentStockIssueItemRepository.save(up)
						//update request item remarks and prepared quantity
						if(up.requestItem){
							def reqItem = departmentStockRequestItemRepository.findById(up.requestItem).get()
							reqItem.preparedQty = it.issueQty
							reqItem.status = it.remarks
							departmentStockRequestItemRepository.save(reqItem)
						}
					}
			}

			departmentStockIssueRepository.save(issue)
		} else {//insert
			issue.issueNo = generatorService.getNextValue(GeneratorType.ISSUE_NO) { Long no ->
				'DSI-' + StringUtils.leftPad(no.toString(), 6, "0")
			}
			issue.issueDate = obj.issued_date
			issue.issueFrom = departmentRepository.findById(UUID.fromString(obj.issued_from)).get()
			issue.issueTo = departmentRepository.findById(UUID.fromString(obj.issue_to)).get()
			issue.issueType = obj.issue_type
			issue.issued_by = employeeRepository.findById(UUID.fromString(obj.issued_by)).get()
			issue.claimed_by = employeeRepository.findById(UUID.fromString(obj.claimed_by)).get()
			if(obj.request){
				req = departmentStockRequestRepository.findById(UUID.fromString(obj.request)).get()
				issue.request = req
				issue.requestNo = req.requestNo
			}
			if(obj.acc_type){
				issue.accType = UUID.fromString(obj.acc_type)
			}
			issue.isCancel = false
			issue.isPosted = false
			def issueSave = departmentStockIssueRepository.save(issue)

			items.each {
				it ->
					def item = objectMapper.convertValue(it.item, Item)
					def list = new DepartmentStockIssueItems()
					list.stockIssue = issueSave
					list.item = itemRepository.findById(item.id).get()
					list.requestedQty = it.requestedQty
					list.issueQty = it.issueQty
					list.unitCost = inventoryResource.getLastUnitPrice(item.id as String)
					list.remarks = it.remarks
					list.isPosted = it.isPosted
					departmentStockIssueItemRepository.save(list)
			}
		}
		return issue
	}

	//update ni WIlson
	@Transactional(rollbackOn = Exception)
	@GraphQLMutation(name = "upsertDepIssuanceByRequest")
	DepartmentStockIssue upsertDepIssuanceByRequest(
			@GraphQLArgument(name = "reqId") UUID reqId,
			@GraphQLArgument(name = "issueItems") ArrayList<Map<String, Object>> issueItems
	) {
		def issue = new DepartmentStockIssue() //new for default
		def req = departmentStockRequestRepository.findById(reqId).get()
		Employee currentEmp = employeeRepository.findByUsername(SecurityUtils.currentLogin()).first()
		def items = issueItems as ArrayList<DepartmentStockRequestItem>
		if (reqId) {//update
			issue.issueNo = generatorService.getNextValue(GeneratorType.ISSUE_NO) { Long no ->
				'DSI-' + StringUtils.leftPad(no.toString(), 6, "0")
			}
			issue.issueDate = Instant.now()
			issue.issueFrom = req.issuingDepartment
			issue.issueTo = req.requestingDepartment
			issue.issueType = req.requestType
			issue.issued_by = currentEmp
			issue.claimed_by = null

			issue.request = req
			issue.requestNo = req.requestNo

			issue.accType = null
			issue.isCancel = false
			issue.isPosted = false
			def issueSave = departmentStockIssueRepository.save(issue)

			items.each {
				it ->
					def reqItem = objectMapper.convertValue(it, DepartmentStockRequestItem.class)

					def list = new DepartmentStockIssueItems()
					list.stockIssue = issueSave
					list.item = reqItem.item
					list.requestedQty = reqItem.quantity_requested
					list.issueQty = reqItem.preparedQty
					list.unitCost = reqItem.unit_cost
					list.remarks = reqItem.status
					list.requestItem = reqItem.id
					list.isPosted = false
					def saveItem = departmentStockIssueItemRepository.save(list)

					//update request item
					def upsertReqItem = departmentStockRequestItemRepository.findById(reqItem.id).get()
					upsertReqItem.preparedQty = reqItem.preparedQty
					upsertReqItem.status = reqItem.status
					upsertReqItem.stockIssueItems = saveItem
					upsertReqItem.stockIssue = issueSave
					departmentStockRequestItemRepository.save(upsertReqItem)

			}

			//save request
			req.status = 1
			req.dispensedBy = currentEmp
			req.stockIssue = issueSave
			departmentStockRequestRepository.save(req)
		}
		return issue
	}

	//post return
	//post inventory for return to supplier
	@Transactional(rollbackOn = Exception.class)
	@GraphQLMutation(name = "postIssuanceItems")
	GraphQLRetVal<Boolean> postIssuanceItems(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "items") ArrayList<Map<String, Object>> items
	) {
		//get parents
		def parent = departmentStockIssueRepository.findById(id).get()
		//get items to post
		def postItems =  items as ArrayList<RawLedgerDto>
		try {
			if(postItems){
				postItems.each {

					def source = objectMapper.convertValue(it.source, Department)
					def dest = objectMapper.convertValue(it.destination, Department)
					Boolean isIn = it.typeId.equalsIgnoreCase("7250e64a-de1b-4015-80fb-e15f9f6762ab")
					def item = new LedgerDto(
							sourceDep: source,
							destinationDep: dest,
							documentTypes: UUID.fromString(it.typeId),
							item: UUID.fromString(it.itemId),
							referenceNo: it.ledgerNo,
							ledgerDate: Instant.parse(it.date),
							ledgerQtyIn: isIn ? it.qty : 0,
							ledgerQtyOut: isIn ? 0 : it.qty,
							ledgerPhysical: it.physical,
							ledgerUnitCost: it.unitcost,
							isInclude: true,
					)
					inventoryLedgerService.postInventoryGlobal(item)

					//update issue item to posted
					def issueItem = departmentStockIssueItemRepository.findById(UUID.fromString(it.id)).get()
					issueItem.isPosted = true
					departmentStockIssueItemRepository.save(issueItem)
				}
			}

			//post to accounting --to do accounting entries
			def transType = transactionTypeService.transTypeById(parent.accType)
			if(integrationServices.getIntegrationByDomainAndTagValue(DepartmentStockIssue.class.name, transType.flagValue)){
				this.saveToJournalEntryDepartmentStockIssue(parent)
			}
			//end post to account

			return new GraphQLRetVal<Boolean>(true,  true,"Items are now posted to ledger")
		} catch (Exception e) {
			throw new Exception("Something was Wrong : " + e)
		}

	}

	//update status void
	@GraphQLMutation(name = "voidIssuanceItems")
	GraphQLRetVal<Boolean> voidIssuanceItems(
			@GraphQLArgument(name = "id") UUID id
	) {
		//get parents
		def parent = departmentStockIssueRepository.findById(id).get()
		def postItems = departmentStockIssueItemRepository.getIssuancePostedItems(id).sort { it.item.descLong }
		//get items to void
		try {
			//reverse accounting entry
			if(parent.postedLedger){
				def header = ledgerServices.findOne(parent.postedLedger)
				ledgerServices.reverseEntriesCustom(header, parent.issueDate)
			}

			if(postItems){
				postItems.each {
					//update mp items to false
					def update = it
					update.isPosted = false
					departmentStockIssueItemRepository.save(update)
				}
			}
			//void inventory to inventory ledger
			inventoryLedgerService.voidInventoryGlobal(parent.issueNo)
			//void to accounting --to do accounting entries

			//end void to account
			//update parent
			parent.isPosted = false
			parent.isCancel = true
			parent.postedLedger = null //to be updated for accounting
			departmentStockIssueRepository.save(parent)
			return new GraphQLRetVal<Boolean>(true,  true,"Items are now voided to ledger")
		} catch (Exception e) {
			throw new Exception("Something was Wrong : " + e)
		}

	}

	//accounting view
	@GraphQLQuery(name = "issuanceJournalView")
	List<JournalEntryViewDto> issuanceJournalView(
			@GraphQLArgument(name = "id") UUID id
	){
		def result = new ArrayList<JournalEntryViewDto>()
		def parent = departmentStockIssueRepository.findById(id).get()
		def items = departmentStockIssueItemRepository.getIssuanceItemsForPosting(id).sort { it.item.descLong }
		def transType = transactionTypeService.transTypeById(parent.accType)


		if(!parent.postedLedger && transType){
			Integration match = integrationServices.getIntegrationByDomainAndTagValue(parent.domain,transType.flagValue)

			def headerLedger = integrationServices.generateAutoEntries(parent){
				it, mul ->
					it.flagValue = transType.flagValue

					Map<AccountingCategory, BigDecimal> accounts = [:]
					Map<AccountingCategory, BigDecimal> accountsExpense = [:]

					//loop stock out/in items
					items.each { a ->
						if(!accounts.containsKey(a.item.accountingCategory)){
							accounts[a.item.accountingCategory] = 0.0
						}
						def sum = a.issueQty * a.unitCost
						accounts[a.item.accountingCategory] =  accounts[a.item.accountingCategory] + sum
					}

					//loop stock out/in items
					items.each { a ->
						if(!accountsExpense.containsKey(a.item.accountingExpenseCategory)){
							accountsExpense[a.item.accountingExpenseCategory] = 0.0
						}
						def sum = a.issueQty * a.unitCost
						accountsExpense[a.item.accountingExpenseCategory] =  accountsExpense[a.item.accountingExpenseCategory] + sum
					}

					//initialize
					Map<String, List<DepartmentStockIssue>> finalAcc  = [:]
					match.integrationItems.findAll { BooleanUtils.isTrue(it.multiple) }.eachWithIndex { IntegrationItem entry, int i ->
						if(!finalAcc.containsKey(entry.sourceColumn)){
							finalAcc[entry.sourceColumn] = []
						}
					}
					//end init

					//multiple
					//loop accounts stockOut
					accounts.each {k, v ->
						if(v > 0){
							String sourceColumn = "negative_${k.sourceColumn}";
							finalAcc[sourceColumn]  << new DepartmentStockIssue().tap {
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
								//init negative
								it.negative_asset_a = BigDecimal.ZERO;it.negative_asset_b = BigDecimal.ZERO;
								it.negative_asset_c = BigDecimal.ZERO;it.negative_asset_d = BigDecimal.ZERO;
								it.negative_asset_e = BigDecimal.ZERO;it.negative_asset_f = BigDecimal.ZERO;
								it.negative_asset_g = BigDecimal.ZERO;it.negative_asset_h = BigDecimal.ZERO;
								it.negative_asset_i = BigDecimal.ZERO;it.negative_asset_j = BigDecimal.ZERO;
								it.negative_asset_k = BigDecimal.ZERO;it.negative_asset_l = BigDecimal.ZERO;
								it.negative_asset_m = BigDecimal.ZERO;it.negative_asset_n = BigDecimal.ZERO;
								it.negative_asset_o = BigDecimal.ZERO;it.negative_asset_p = BigDecimal.ZERO;
								it.negative_asset_q = BigDecimal.ZERO;it.negative_asset_r = BigDecimal.ZERO;
								it.negative_asset_s = BigDecimal.ZERO;it.negative_asset_t = BigDecimal.ZERO;
								it.negative_asset_u = BigDecimal.ZERO;it.negative_asset_v = BigDecimal.ZERO;
								it.negative_asset_w = BigDecimal.ZERO;it.negative_asset_x = BigDecimal.ZERO;
								it.negative_asset_y = BigDecimal.ZERO;it.negative_asset_z = BigDecimal.ZERO;
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

								it.issueFrom = parent.issueFrom
								it.category = k
								it[sourceColumn] = v * -1; //credit
							}
						}
					}


					if(parent.issueType.equalsIgnoreCase("Expense")){
						//loop accounts expense
						accountsExpense.each {k, v ->
							if(v > 0){
								finalAcc[k.sourceColumn] << new DepartmentStockIssue().tap {
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
									//init negative
									it.negative_asset_a = BigDecimal.ZERO;it.negative_asset_b = BigDecimal.ZERO;
									it.negative_asset_c = BigDecimal.ZERO;it.negative_asset_d = BigDecimal.ZERO;
									it.negative_asset_e = BigDecimal.ZERO;it.negative_asset_f = BigDecimal.ZERO;
									it.negative_asset_g = BigDecimal.ZERO;it.negative_asset_h = BigDecimal.ZERO;
									it.negative_asset_i = BigDecimal.ZERO;it.negative_asset_j = BigDecimal.ZERO;
									it.negative_asset_k = BigDecimal.ZERO;it.negative_asset_l = BigDecimal.ZERO;
									it.negative_asset_m = BigDecimal.ZERO;it.negative_asset_n = BigDecimal.ZERO;
									it.negative_asset_o = BigDecimal.ZERO;it.negative_asset_p = BigDecimal.ZERO;
									it.negative_asset_q = BigDecimal.ZERO;it.negative_asset_r = BigDecimal.ZERO;
									it.negative_asset_s = BigDecimal.ZERO;it.negative_asset_t = BigDecimal.ZERO;
									it.negative_asset_u = BigDecimal.ZERO;it.negative_asset_v = BigDecimal.ZERO;
									it.negative_asset_w = BigDecimal.ZERO;it.negative_asset_x = BigDecimal.ZERO;
									it.negative_asset_y = BigDecimal.ZERO;it.negative_asset_z = BigDecimal.ZERO;
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

									it.issueTo = parent.issueTo
									it.category = k
									it[k.sourceColumn] = v;
									//println("it[k.sourceColumn] => "+ k.sourceColumn + " = "+ v)
								}
							}
						}
					}else{
						//loop accounts stockIn
						accounts.each {k, v ->
							if(v > 0){
								finalAcc[k.sourceColumn] << new DepartmentStockIssue().tap {
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
									//init negative
									it.negative_asset_a = BigDecimal.ZERO;it.negative_asset_b = BigDecimal.ZERO;
									it.negative_asset_c = BigDecimal.ZERO;it.negative_asset_d = BigDecimal.ZERO;
									it.negative_asset_e = BigDecimal.ZERO;it.negative_asset_f = BigDecimal.ZERO;
									it.negative_asset_g = BigDecimal.ZERO;it.negative_asset_h = BigDecimal.ZERO;
									it.negative_asset_i = BigDecimal.ZERO;it.negative_asset_j = BigDecimal.ZERO;
									it.negative_asset_k = BigDecimal.ZERO;it.negative_asset_l = BigDecimal.ZERO;
									it.negative_asset_m = BigDecimal.ZERO;it.negative_asset_n = BigDecimal.ZERO;
									it.negative_asset_o = BigDecimal.ZERO;it.negative_asset_p = BigDecimal.ZERO;
									it.negative_asset_q = BigDecimal.ZERO;it.negative_asset_r = BigDecimal.ZERO;
									it.negative_asset_s = BigDecimal.ZERO;it.negative_asset_t = BigDecimal.ZERO;
									it.negative_asset_u = BigDecimal.ZERO;it.negative_asset_v = BigDecimal.ZERO;
									it.negative_asset_w = BigDecimal.ZERO;it.negative_asset_x = BigDecimal.ZERO;
									it.negative_asset_y = BigDecimal.ZERO;it.negative_asset_z = BigDecimal.ZERO;
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

									it.issueTo = parent.issueTo
									it.category = k
									it[k.sourceColumn] = v; //debit
								}
							}
						}
					}
					//loop final
					finalAcc.each {key, itemsAcc ->
						mul << itemsAcc
					}
					// not multiple --goes here
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

	//save to journal entry
	@Transactional(rollbackOn = Exception.class)
	DepartmentStockIssue saveToJournalEntryDepartmentStockIssue(DepartmentStockIssue departmentStockIssue){
		def id = departmentStockIssue.id
		def parent = departmentStockIssue
		def items = departmentStockIssueItemRepository.getIssuanceItemsForPosting(id).sort { it.item.descLong }
		def transType = transactionTypeService.transTypeById(parent.accType)
		def yearFormat = DateTimeFormatter.ofPattern("yyyy")
		if(transType){

			Integration match = integrationServices.getIntegrationByDomainAndTagValue(parent.domain,transType.flagValue)

			def headerLedger = integrationServices.generateAutoEntries(parent){
				it, mul ->
					it.flagValue = transType.flagValue

					Map<AccountingCategory, BigDecimal> accounts = [:]
					Map<AccountingCategory, BigDecimal> accountsExpense = [:]

					//loop stock out/in items
					items.each { a ->
						if(!accounts.containsKey(a.item.accountingCategory)){
							accounts[a.item.accountingCategory] = 0.0
						}
						def sum = a.issueQty * a.unitCost
						accounts[a.item.accountingCategory] =  accounts[a.item.accountingCategory] + sum
					}

					//loop stock out/in items
					items.each { a ->
						if(!accountsExpense.containsKey(a.item.accountingExpenseCategory)){
							accountsExpense[a.item.accountingExpenseCategory] = 0.0
						}
						def sum = a.issueQty * a.unitCost
						accountsExpense[a.item.accountingExpenseCategory] =  accountsExpense[a.item.accountingExpenseCategory] + sum
					}

					//initialize
					Map<String, List<DepartmentStockIssue>> finalAcc  = [:]
					match.integrationItems.findAll { BooleanUtils.isTrue(it.multiple) }.eachWithIndex { IntegrationItem entry, int i ->
						if(!finalAcc.containsKey(entry.sourceColumn)){
							finalAcc[entry.sourceColumn] = []
						}
					}
					//end init

					//multiple
					//loop accounts stockOut
					accounts.each {k, v ->
						if(v > 0){
							String sourceColumn = "negative_${k.sourceColumn}";
							finalAcc[sourceColumn]  << new DepartmentStockIssue().tap {
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
								//init negative
								it.negative_asset_a = BigDecimal.ZERO;it.negative_asset_b = BigDecimal.ZERO;
								it.negative_asset_c = BigDecimal.ZERO;it.negative_asset_d = BigDecimal.ZERO;
								it.negative_asset_e = BigDecimal.ZERO;it.negative_asset_f = BigDecimal.ZERO;
								it.negative_asset_g = BigDecimal.ZERO;it.negative_asset_h = BigDecimal.ZERO;
								it.negative_asset_i = BigDecimal.ZERO;it.negative_asset_j = BigDecimal.ZERO;
								it.negative_asset_k = BigDecimal.ZERO;it.negative_asset_l = BigDecimal.ZERO;
								it.negative_asset_m = BigDecimal.ZERO;it.negative_asset_n = BigDecimal.ZERO;
								it.negative_asset_o = BigDecimal.ZERO;it.negative_asset_p = BigDecimal.ZERO;
								it.negative_asset_q = BigDecimal.ZERO;it.negative_asset_r = BigDecimal.ZERO;
								it.negative_asset_s = BigDecimal.ZERO;it.negative_asset_t = BigDecimal.ZERO;
								it.negative_asset_u = BigDecimal.ZERO;it.negative_asset_v = BigDecimal.ZERO;
								it.negative_asset_w = BigDecimal.ZERO;it.negative_asset_x = BigDecimal.ZERO;
								it.negative_asset_y = BigDecimal.ZERO;it.negative_asset_z = BigDecimal.ZERO;
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

								it.issueFrom = parent.issueFrom
								it.category = k
								it[sourceColumn] = v * -1; //credit
							}
						}
					}


					if(parent.issueType.equalsIgnoreCase("Expense")){
						//loop accounts expense
						accountsExpense.each {k, v ->
							if(v > 0){
								finalAcc[k.sourceColumn] << new DepartmentStockIssue().tap {
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
									//init negative
									it.negative_asset_a = BigDecimal.ZERO;it.negative_asset_b = BigDecimal.ZERO;
									it.negative_asset_c = BigDecimal.ZERO;it.negative_asset_d = BigDecimal.ZERO;
									it.negative_asset_e = BigDecimal.ZERO;it.negative_asset_f = BigDecimal.ZERO;
									it.negative_asset_g = BigDecimal.ZERO;it.negative_asset_h = BigDecimal.ZERO;
									it.negative_asset_i = BigDecimal.ZERO;it.negative_asset_j = BigDecimal.ZERO;
									it.negative_asset_k = BigDecimal.ZERO;it.negative_asset_l = BigDecimal.ZERO;
									it.negative_asset_m = BigDecimal.ZERO;it.negative_asset_n = BigDecimal.ZERO;
									it.negative_asset_o = BigDecimal.ZERO;it.negative_asset_p = BigDecimal.ZERO;
									it.negative_asset_q = BigDecimal.ZERO;it.negative_asset_r = BigDecimal.ZERO;
									it.negative_asset_s = BigDecimal.ZERO;it.negative_asset_t = BigDecimal.ZERO;
									it.negative_asset_u = BigDecimal.ZERO;it.negative_asset_v = BigDecimal.ZERO;
									it.negative_asset_w = BigDecimal.ZERO;it.negative_asset_x = BigDecimal.ZERO;
									it.negative_asset_y = BigDecimal.ZERO;it.negative_asset_z = BigDecimal.ZERO;
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

									it.issueTo = parent.issueTo
									it.category = k
									it[k.sourceColumn] = v;
									//println("it[k.sourceColumn] => "+ k.sourceColumn + " = "+ v)
								}
							}
						}
					}else{
						//loop accounts stockIn
						accounts.each {k, v ->
							if(v > 0){
								finalAcc[k.sourceColumn] << new DepartmentStockIssue().tap {
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
									//init negative
									it.negative_asset_a = BigDecimal.ZERO;it.negative_asset_b = BigDecimal.ZERO;
									it.negative_asset_c = BigDecimal.ZERO;it.negative_asset_d = BigDecimal.ZERO;
									it.negative_asset_e = BigDecimal.ZERO;it.negative_asset_f = BigDecimal.ZERO;
									it.negative_asset_g = BigDecimal.ZERO;it.negative_asset_h = BigDecimal.ZERO;
									it.negative_asset_i = BigDecimal.ZERO;it.negative_asset_j = BigDecimal.ZERO;
									it.negative_asset_k = BigDecimal.ZERO;it.negative_asset_l = BigDecimal.ZERO;
									it.negative_asset_m = BigDecimal.ZERO;it.negative_asset_n = BigDecimal.ZERO;
									it.negative_asset_o = BigDecimal.ZERO;it.negative_asset_p = BigDecimal.ZERO;
									it.negative_asset_q = BigDecimal.ZERO;it.negative_asset_r = BigDecimal.ZERO;
									it.negative_asset_s = BigDecimal.ZERO;it.negative_asset_t = BigDecimal.ZERO;
									it.negative_asset_u = BigDecimal.ZERO;it.negative_asset_v = BigDecimal.ZERO;
									it.negative_asset_w = BigDecimal.ZERO;it.negative_asset_x = BigDecimal.ZERO;
									it.negative_asset_y = BigDecimal.ZERO;it.negative_asset_z = BigDecimal.ZERO;
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

									it.issueTo = parent.issueTo
									it.category = k
									it[k.sourceColumn] = v; //debit
								}
							}
						}
					}
					//loop final
					finalAcc.each {key, itemsAcc ->
						mul << itemsAcc
					}
					// not multiple --goes here
			}

			Map<String,String> details = [:]

			parent.details.each { k,v ->
				details[k] = v
			}

			details["ISSUANCE_ID"] = parent.id.toString()
			details["ISSUE_TO_ID"] = parent.issueTo.id.toString()
			details["ISSUE_FROM_ID"] = parent.issueFrom.id.toString()
			details["ISSUE_TO"] = parent.issueTo.departmentName
			details["ISSUE_FROM"] = parent.issueFrom.departmentName

			def pHeader =	ledgerServices.persistHeaderLedger(headerLedger,
					"${parent.issueDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${parent.issueNo}",
					"${parent.issueNo}-${parent.issueTo?.departmentName}",
					"${parent.issueNo}-${parent.issueType.equalsIgnoreCase("Expense") ? "EXPENSE" : "ISSUANCE"}-${parent.issueTo?.departmentName}",
					parent.issueType.equalsIgnoreCase("Expense") ? LedgerDocType.EI : LedgerDocType.SI,
					JournalType.GENERAL,
					parent.createdDate,
					details)

			//update parent
			parent.isPosted = true
			parent.postedLedger = pHeader.id
			departmentStockIssueRepository.save(parent)

		}
		return parent
	}

	//	code ni dons
	@Transactional(rollbackOn = Exception)
	@GraphQLMutation(name = "upsertItemIssuanceFromItemReq")
	DepartmentStockIssue mutateItemIssuanceFromItemReq(
			@GraphQLArgument(name = "issueId") UUID issueId,
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "issueItems") ArrayList<Map<String, Object>> issueItems,
			@GraphQLArgument(name = "requestItems") ArrayList<Map<String, Object>> requestItems,
			@GraphQLArgument(name = "deletedIssueItems") ArrayList<String> deletedIssueItems,
			@GraphQLArgument(name = "resetRequestNo") ArrayList<String> resetRequestNo
	) {
		def issue = new DepartmentStockIssue() //new for default
		def obj = objectMapper.convertValue(fields, StockIssueDto.class)
		def items = issueItems as ArrayList<IssuedItems>

		if (issueId) {//update
			issue = departmentStockIssueRepository.findById(issueId).get()
			issue.issueDate = obj.issued_date
			issue.issueTo = departmentRepository.findById(UUID.fromString(obj.issue_to)).get()
			issue.issued_by = employeeRepository.findById(UUID.fromString(obj.issued_by)).get()
			issue.claimed_by = employeeRepository.findById(UUID.fromString(obj.claimed_by)).get()
			issue.requestNo = obj.request_no

			def issueSave = departmentStockIssueRepository.save(issue)

			items.each {
				it ->
					def item = objectMapper.convertValue(it.item, Item)
					def list = departmentStockIssueItemRepository.getExistingStockIssueItemByItem(issueId,item.id)
					if(list){
						list.stockIssue = issueSave
						list.issueQty = it.issueQty
						departmentStockIssueItemRepository.save(list)
					}
					else{
						list = new DepartmentStockIssueItems()
						list.stockIssue = issueSave
						list.item = itemRepository.findById(item.id).get()
						list.issueQty = it.issueQty
						list.unitCost = inventoryResource.getLastUnitPrice(item.id as String)
						list.isPosted = it.isPosted
						departmentStockIssueItemRepository.save(list)
					}

			}

			//			update department item request table
			def reqItems = requestItems as ArrayList<UpdateStockRequestDto>
			reqItems.each {
				newIt ->
					def request = departmentStockRequestRepository.findById(UUID.fromString(newIt.stockRequestId)).get()
					request.stockIssue = issueSave
					request.preparedBy = employeeRepository.findById(UUID.fromString(obj.issued_by)).get()
					request.claimedBy = employeeRepository.findById(UUID.fromString(obj.claimed_by)).get()
					request.status = 2
					def subRequestItems = newIt.requestItems as ArrayList<UpdateStockRequestItem>

					subRequestItems.each {
						subIt ->
							def requestItem = departmentStockRequestItemRepository.findById(UUID.fromString(subIt.stockRequestItemId)).get()
							def stockIssueItem = departmentStockIssueItemRepository.getExistingStockIssueItemByItem(issueSave.id,requestItem.item.id)
							requestItem.preparedQty = subIt.preparedQty
							requestItem.status = subIt.status
							requestItem.stockIssueItems = stockIssueItem

							departmentStockRequestItemRepository.save(requestItem)

					}
					departmentStockRequestRepository.save(request)
			}

			if(resetRequestNo){
				resetRequestNo.each {
					resetId ->
						def resetRequest = departmentStockRequestRepository.findById(UUID.fromString(resetId)).get()
						resetRequest.stockIssue = null
						resetRequest.preparedBy = null
						resetRequest.claimedBy = null
						resetRequest.status = 1

						def resetRequestItem = departmentStockRequestItemRepository.findItemsByRequest(resetRequest.id)
						resetRequestItem.each {
							resetItem ->
								resetItem.preparedQty = 0
								resetItem.status = null
								resetItem.stockIssueItems = null
								departmentStockRequestItemRepository.save(resetItem)
						}
						departmentStockRequestRepository.save(resetRequest)
				}
			}

		} else {//insert
//			insert new department issue request
			issue.issueNo = generatorService.getNextValue(GeneratorType.ISSUE_NO) { Long no ->
				'DSI-' + StringUtils.leftPad(no.toString(), 6, "0")
			}
			issue.issueDate = obj.issued_date
			issue.issueFrom = departmentRepository.findById(UUID.fromString(obj.issued_from)).get()
			issue.issueTo = departmentRepository.findById(UUID.fromString(obj.issue_to)).get()
			issue.issueType = obj.issue_type
			issue.issued_by = employeeRepository.findById(UUID.fromString(obj.issued_by)).get()
			issue.claimed_by = employeeRepository.findById(UUID.fromString(obj.claimed_by)).get()
			issue.isCancel = false
			issue.isPosted = false
			issue.requestNo = obj.request_no

			def issueSave = departmentStockIssueRepository.save(issue)

			items.each {
				it ->
					def item = objectMapper.convertValue(it.item, Item)
					def list = new DepartmentStockIssueItems()
					list.stockIssue = issueSave
					list.item = itemRepository.findById(item.id).get()
					list.issueQty = it.issueQty
					list.unitCost = inventoryResource.getLastUnitPrice(item.id as String)
					list.isPosted = it.isPosted
					departmentStockIssueItemRepository.save(list)
			}

			//			update department item request table
			def reqItems = requestItems as ArrayList<UpdateStockRequestDto>
			reqItems.each {
				newIt ->
					def request = departmentStockRequestRepository.findById(UUID.fromString(newIt.stockRequestId)).get()
					request.stockIssue = issueSave
					request.preparedBy = employeeRepository.findById(UUID.fromString(obj.issued_by)).get()
					request.claimedBy = employeeRepository.findById(UUID.fromString(obj.claimed_by)).get()
					request.status = 2
					def subRequestItems = newIt.requestItems as ArrayList<UpdateStockRequestItem>

					subRequestItems.each {
						subIt ->
							def requestItem = departmentStockRequestItemRepository.findById(UUID.fromString(subIt.stockRequestItemId)).get()
							def stockIssueItem = departmentStockIssueItemRepository.getExistingStockIssueItemByItem(issueSave.id,requestItem.item.id)
							requestItem.preparedQty = subIt.preparedQty
							requestItem.status = subIt.status
							requestItem.stockIssueItems = stockIssueItem

							departmentStockRequestItemRepository.save(requestItem)

					}
					departmentStockRequestRepository.save(request)
			}

		}

		return issue

	}

	//redo issuance
	@GraphQLMutation(name = "redoIssuance")
	GraphQLRetVal<Boolean> redoIssuance(
			@GraphQLArgument(name = "id") UUID id
	) {
		//get parents
		def parent = departmentStockIssueRepository.findById(id).get()
		//get items to void
		try {
			parent.isCancel = false
			parent.isPosted = false
			departmentStockIssueRepository.save(parent)
			return new GraphQLRetVal<Boolean>(true,  true,"Issuance Successfully Updated. You can now edit and post the issuance again.")
		} catch (Exception e) {
			throw new Exception("Something was Wrong : " + e)
		}

	}
}
