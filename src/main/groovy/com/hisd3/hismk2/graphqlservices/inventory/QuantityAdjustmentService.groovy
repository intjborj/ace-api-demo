package com.hisd3.hismk2.graphqlservices.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.accounting.Ledger
import com.hisd3.hismk2.domain.inventory.AccountingCategory
import com.hisd3.hismk2.domain.inventory.MaterialProduction
import com.hisd3.hismk2.domain.inventory.QuantityAdjustment
import com.hisd3.hismk2.domain.inventory.QuantityAdjustmentType
import com.hisd3.hismk2.graphqlservices.accounting.IntegrationServices
import com.hisd3.hismk2.graphqlservices.accounting.LedgerServices
import com.hisd3.hismk2.graphqlservices.accounting.TransactionTypeService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.inventory.AccountingCategoryRepository
import com.hisd3.hismk2.repository.inventory.QuantityAdjustmentRepository
import com.hisd3.hismk2.rest.dto.JournalEntryViewDto
import com.hisd3.hismk2.rest.dto.LedgerDto
import com.hisd3.hismk2.rest.dto.QueryErrorException
import com.hisd3.hismk2.rest.dto.RawLedgerDto
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import java.time.Duration
import java.time.Instant

@Component
@GraphQLApi
@TypeChecked
class QuantityAdjustmentService {
	
	@Autowired
	QuantityAdjustmentRepository quantityAdjustmentRepository
	
	@Autowired
	ObjectMapper objectMapper
	
	@Autowired
	InventoryLedgerService inventoryLedgerService
	
	@Autowired
	GeneratorService generatorService

	@Autowired
	QuantityAdjustmentTypeService quantityAdjustmentTypeService

	@Autowired
	IntegrationServices integrationServices

	@Autowired
	LedgerServices ledgerServices

	@Autowired
	AccountingCategoryRepository accountingCategoryRepository

	@GraphQLQuery(name = "quantityListByItem", description = "List of Quantity Adjustment by Item")
	List<QuantityAdjustment> getAdjustById(@GraphQLArgument(name = "item") UUID id) {
		return quantityAdjustmentRepository.getAdjustById(id).sort { it.createdDate }.reverse(true)
	}
	
	//
	//MUTATION
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "quantityAdjustmentInsert", description = "insert adj")
	QuantityAdjustment quantityAdjustmentInsert(
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		QuantityAdjustment insert = new QuantityAdjustment()
		def data
		def adj = objectMapper.convertValue(fields, QuantityAdjustment)
		try {
			insert.refNum = generatorService.getNextValue(GeneratorType.QTY_ADJ) { Long no ->
				'ADJ-' + StringUtils.leftPad(no.toString(), 6, "0")
			}
			insert.dateTrans = adj.dateTrans
			insert.item = adj.item
			insert.department = adj.department
			insert.quantity = adj.quantity
			insert.unit_cost = adj.unit_cost
			insert.isPosted = false
			insert.isCancel = false
			insert.quantityAdjustmentType = adj.quantityAdjustmentType
			insert.remarks = adj.remarks

			data = quantityAdjustmentRepository.save(insert)
			
		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		
		return data
	}

	//update qty and unitCost
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "upsertAdjQtyUnitCost")
	QuantityAdjustment upsertAdjQtyUnitCost(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "qty") Integer qty,
			@GraphQLArgument(name = "unitCost") BigDecimal unitCost
	) {
		QuantityAdjustment insert = quantityAdjustmentRepository.findById(id).get()
		try {

			insert.quantity = qty
			insert.unit_cost = unitCost

			quantityAdjustmentRepository.save(insert)

		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}

		return insert
	}

	//post Adjustment
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "postQtyAdjustment")
	GraphQLRetVal<Boolean> postQtyAdjustment(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "items") ArrayList<Map<String, Object>> items
	) {
		//get parents
		def adj = quantityAdjustmentRepository.findById(id).get()
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
							ledgerQtyIn: it.qty,
							ledgerQtyOut: 0,
							ledgerPhysical: it.physical,
							ledgerUnitCost: it.unitcost,
							isInclude: true,
					)
					inventoryLedgerService.postInventoryGlobal(item)
				}
			}

			//post to accounting --to do accounting entries

			//end post to account
			//update parent
			adj.isPosted = true
			adj.isCancel = false
			adj.postedBy= SecurityUtils.currentLogin()
			adj.postedLedger= null //to be updated for accounting
			quantityAdjustmentRepository.save(adj)
			return new GraphQLRetVal<Boolean>(true,  true,"Items are now posted to ledger")
		} catch (Exception e) {
			throw new Exception("Something was Wrong : " + e)
		}

	}

	@GraphQLMutation(name = "voidQtyAdj")
	GraphQLRetVal<Boolean> voidQtyAdj(
			@GraphQLArgument(name = "id") UUID id
	) {
		//get parents
		def adj = quantityAdjustmentRepository.findById(id).get()
		//get items to void
		try {
			//void inventory to inventory ledger
			inventoryLedgerService.voidInventoryGlobal(adj.refNum)
			//void to accounting --to do accounting entries

			//end void to account
			//update parent
			adj.isPosted = false
			adj.isCancel = true
			adj.postedBy= null
			adj.postedLedger= null //to be updated for accounting
			quantityAdjustmentRepository.save(adj)
			return new GraphQLRetVal<Boolean>(true,  true,"Items are now voided to ledger")
		} catch (Exception e) {
			throw new Exception("Something was Wrong : " + e)
		}

	}

	@GraphQLQuery(name = "adjQuantityAccountView")
	List<JournalEntryViewDto> adjQuantityAccountView(
			@GraphQLArgument(name = "id") UUID id
	){
		def result = new ArrayList<JournalEntryViewDto>()
		def adjItem = quantityAdjustmentRepository.findById(id).get()

		def flagValue = adjItem.quantityAdjustmentType.flagValue
		def source = adjItem.quantityAdjustmentType.sourceValue
		def reverse = adjItem.quantityAdjustmentType.reverse

		if(flagValue){
			def headerLedger =	integrationServices.generateAutoEntries(adjItem){
				it, mul ->

					it.adj_1 = BigDecimal.ZERO; it.adj_2 = BigDecimal.ZERO; it.adj_3 = BigDecimal.ZERO;
					it.adj_4 = BigDecimal.ZERO; it.adj_5 = BigDecimal.ZERO; it.adj_6 = BigDecimal.ZERO;
					it.adj_7 = BigDecimal.ZERO; it.adj_8 = BigDecimal.ZERO; it.adj_9 = BigDecimal.ZERO;
					it.adj_10 = BigDecimal.ZERO; it.adj_11 = BigDecimal.ZERO; it.adj_12 = BigDecimal.ZERO;
					it.adj_13 = BigDecimal.ZERO; it.adj_14 = BigDecimal.ZERO; it.adj_15 = BigDecimal.ZERO;
					it.adj_16 = BigDecimal.ZERO; it.adj_17 = BigDecimal.ZERO; it.adj_18 = BigDecimal.ZERO;
					it.adj_19 = BigDecimal.ZERO; it.adj_20 = BigDecimal.ZERO; it.adj_21 = BigDecimal.ZERO;
					it.adj_22 = BigDecimal.ZERO; it.adj_23 = BigDecimal.ZERO; it.adj_24 = BigDecimal.ZERO;
					it.adj_25 = BigDecimal.ZERO; it.adj_26 = BigDecimal.ZERO; it.adj_27 = BigDecimal.ZERO;
					it.adj_28 = BigDecimal.ZERO; it.adj_29 = BigDecimal.ZERO; it.adj_30 = BigDecimal.ZERO;
					it.adj_31 = BigDecimal.ZERO; it.adj_32 = BigDecimal.ZERO; it.adj_33 = BigDecimal.ZERO;
					it.adj_34 = BigDecimal.ZERO; it.adj_35 = BigDecimal.ZERO; it.adj_36 = BigDecimal.ZERO;
					it.adj_37 = BigDecimal.ZERO; it.adj_38 = BigDecimal.ZERO; it.adj_39 = BigDecimal.ZERO;
					it.adj_40 = BigDecimal.ZERO; it.adj_41 = BigDecimal.ZERO; it.adj_42 = BigDecimal.ZERO;
					it.adj_43 = BigDecimal.ZERO; it.adj_44 = BigDecimal.ZERO; it.adj_45 = BigDecimal.ZERO;
					it.adj_46 = BigDecimal.ZERO; it.adj_47 = BigDecimal.ZERO; it.adj_48 = BigDecimal.ZERO;
					it.adj_49 = BigDecimal.ZERO; it.adj_50 = BigDecimal.ZERO; it.adj_51 = BigDecimal.ZERO;
					it.adj_52 = BigDecimal.ZERO; it.adj_53 = BigDecimal.ZERO; it.adj_54 = BigDecimal.ZERO;
					it.adj_55 = BigDecimal.ZERO; it.adj_56 = BigDecimal.ZERO; it.adj_57 = BigDecimal.ZERO;
					it.adj_58 = BigDecimal.ZERO; it.adj_59 = BigDecimal.ZERO; it.adj_60 = BigDecimal.ZERO;
					it.adj_61 = BigDecimal.ZERO; it.adj_62 = BigDecimal.ZERO; it.adj_63 = BigDecimal.ZERO;
					it.adj_64 = BigDecimal.ZERO; it.adj_65 = BigDecimal.ZERO; it.adj_66 = BigDecimal.ZERO;
					it.adj_67 = BigDecimal.ZERO; it.adj_68 = BigDecimal.ZERO; it.adj_69 = BigDecimal.ZERO;
					it.adj_70 = BigDecimal.ZERO; it.adj_71 = BigDecimal.ZERO; it.adj_72 = BigDecimal.ZERO;
					it.adj_73 = BigDecimal.ZERO; it.adj_74 = BigDecimal.ZERO; it.adj_75 = BigDecimal.ZERO;
					it.adj_76 = BigDecimal.ZERO; it.adj_77 = BigDecimal.ZERO; it.adj_78 = BigDecimal.ZERO;
					it.adj_79 = BigDecimal.ZERO; it.adj_80 = BigDecimal.ZERO; it.adj_81 = BigDecimal.ZERO;
					it.adj_82 = BigDecimal.ZERO; it.adj_83 = BigDecimal.ZERO; it.adj_84 = BigDecimal.ZERO;
					it.adj_85 = BigDecimal.ZERO; it.adj_86 = BigDecimal.ZERO; it.adj_87 = BigDecimal.ZERO;
					it.adj_88 = BigDecimal.ZERO; it.adj_89 = BigDecimal.ZERO; it.adj_90 = BigDecimal.ZERO;
					it.adj_91 = BigDecimal.ZERO; it.adj_92 = BigDecimal.ZERO; it.adj_93 = BigDecimal.ZERO;
					it.adj_94 = BigDecimal.ZERO; it.adj_95 = BigDecimal.ZERO; it.adj_96 = BigDecimal.ZERO;
					it.adj_97 = BigDecimal.ZERO; it.adj_98 = BigDecimal.ZERO; it.adj_99 = BigDecimal.ZERO;
					it.adj_100 = BigDecimal.ZERO;

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

					BigDecimal cost = adjItem.unit_cost * adjItem.quantity
					def cat = adjItem.item.accountingCategory
					it.flagValue = flagValue

					//not multiple
					it.department = adjItem.department
					it.accountingCategory = adjItem.item.accountingCategory
					it[cat.sourceColumn] = cost
					it[source] = reverse ? cost * -1 : cost

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
		}
		return result.sort{it.credit}
	}
	
}
