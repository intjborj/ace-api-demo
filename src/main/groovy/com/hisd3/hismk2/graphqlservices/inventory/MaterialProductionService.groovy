package com.hisd3.hismk2.graphqlservices.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.IntegrationTemplate
import com.hisd3.hismk2.domain.SubAccountHolder
import com.hisd3.hismk2.domain.accounting.Bank
import com.hisd3.hismk2.domain.accounting.Disbursement
import com.hisd3.hismk2.domain.accounting.Integration
import com.hisd3.hismk2.domain.accounting.IntegrationItem
import com.hisd3.hismk2.domain.accounting.JournalType
import com.hisd3.hismk2.domain.accounting.Ledger
import com.hisd3.hismk2.domain.accounting.LedgerDocType
import com.hisd3.hismk2.domain.cashiering.CollectionDetailType
import com.hisd3.hismk2.domain.inventory.AccountingCategory
import com.hisd3.hismk2.domain.inventory.DepartmentStockIssue
import com.hisd3.hismk2.domain.inventory.InventoryLedger
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.inventory.MaterialProduction
import com.hisd3.hismk2.domain.inventory.MaterialProductionItem
import com.hisd3.hismk2.domain.inventory.ReceivingReport
import com.hisd3.hismk2.graphqlservices.accounting.IntegrationServices
import com.hisd3.hismk2.graphqlservices.accounting.LedgerServices
import com.hisd3.hismk2.graphqlservices.accounting.TransactionTypeService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.inventory.AccountingCategoryRepository
import com.hisd3.hismk2.repository.inventory.DocumentTypeRepository
import com.hisd3.hismk2.repository.inventory.InventoryLedgerRepository
import com.hisd3.hismk2.repository.inventory.MaterialProductionItemRepository
import com.hisd3.hismk2.repository.inventory.MaterialProductionRepository
import com.hisd3.hismk2.rest.dto.JournalEntryViewDto
import com.hisd3.hismk2.rest.dto.LedgerDto
import com.hisd3.hismk2.rest.dto.MaterialProdItemDto
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
import org.apache.commons.lang3.BooleanUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Component
@GraphQLApi
@TypeChecked
class MaterialProductionService {

	@Autowired
	MaterialProductionRepository materialProductionRepository

	@Autowired
	MaterialProductionItemRepository materialProductionItemRepository

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	InventoryLedgerService inventoryLedgerService

	@Autowired
	GeneratorService generatorService

	@Autowired
	DocumentTypeRepository documentTypeRepository

	@Autowired
	InventoryLedgerRepository inventoryLedgerRepository

	@Autowired
	IntegrationServices integrationServices

	@Autowired
	LedgerServices ledgerServices

	@Autowired
	TransactionTypeService transactionTypeService

	@Autowired
	AccountingCategoryRepository accountingCategoryRepository

	@GraphQLQuery(name = 'get_material_production', description = 'object for material production')
	MaterialProduction getMaterialProduction(@GraphQLArgument(name = 'id') UUID id) {
		if(id){
			return materialProductionRepository.findById(id).get()
		}else{
			return null;
		}

	}

	@GraphQLQuery(name = 'get_material_production_items', description = 'object for material production')
	Page<MaterialProductionItem> getByMaterialProduction(
			@GraphQLArgument(name = 'id') UUID id,
			@GraphQLArgument(name = 'page') Integer page,
			@GraphQLArgument(name = 'pageSize') Integer pageSize
	) {
		return materialProductionItemRepository.getByMaterialProduction(id, PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, "item.descLong")))
	}

	@GraphQLQuery(name = 'getAllMaterialProductionItems', description = 'object for material production')
	List<MaterialProductionItem> getByMaterialProduction(
			@GraphQLArgument(name = 'id') UUID id
	) {
		return materialProductionItemRepository.getAllMaterialProductionItems(id)
	}

	@GraphQLQuery(name = 'material_production_list', description = 'list of all material production with pagination')
	Page<MaterialProduction> getAllMaterialProduciton(
			@GraphQLArgument(name = "pageSize") Integer pageSize,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "itemId") UUID itemId

	) {

		return materialProductionRepository.getAllMaterialProduction(itemId, PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, 'mpNo')))
	}

	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "material_production_mutation", description = "")
	MaterialProduction postMaterialProduction(@GraphQLArgument(name = 'fields') Map<String, Object> fields, @GraphQLArgument(name = 'items') List<Map<String, Object>> items) {
		if (fields.get('id') != null) {
			def materialProduction = materialProductionRepository.findById(UUID.fromString(fields.get('id').toString())).get()
			def list = new ArrayList<Map<String, Object>>()

			materialProduction.quantity = fields.get('qty') as Integer
			materialProduction.item = objectMapper.convertValue(fields.get('item'), Item)
			materialProduction.department = objectMapper.convertValue(fields.get('department'), Department)
			materialProduction.unitCost = fields.get('unit_cost') as BigDecimal

			def afterSaveMaterialProduction = materialProductionRepository.save(materialProduction)

			if (afterSaveMaterialProduction) {
				def item = new HashMap<String, Object>()

				item.put('type', 'output')
				item.put('department', materialProduction.department.id)
				item.put('itemId', afterSaveMaterialProduction.item.id)
				item.put('reference_no', afterSaveMaterialProduction.mpNo)
				item.put('qty', afterSaveMaterialProduction.quantity)
				item.put('cost', afterSaveMaterialProduction.unitCost)

				list.add(item)
			}

			items.eachWithIndex { Map<String, Object> entry, int i ->
				if (entry.get('id') != null) {
					def materialProductionItem = materialProductionItemRepository.findById(UUID.fromString(entry.get('id').toString())).get()
					if (entry.get('item') != null) {
						materialProductionItem.item = objectMapper.convertValue(entry.get('item'), Item) as Item
					}
					materialProductionItem.qty = entry.get('qty') as Integer
					materialProductionItem.materialProduction = afterSaveMaterialProduction
					def afterSaveMaterialProdItem = materialProductionItemRepository.save(materialProductionItem)

					if (afterSaveMaterialProdItem) {

						def item = new HashMap<String, Object>()

						item.put('type', 'source')
						item.put('department', materialProduction.department.id)
						item.put('itemId', afterSaveMaterialProdItem.item.id)
						item.put('reference_no', afterSaveMaterialProduction.mpNo)
						item.put('qty', afterSaveMaterialProdItem.qty)
						item.put('cost', entry.get('lastUnitCost'))

						list.add(item)

					}
				} else {
					def materialProductionItem = new MaterialProductionItem()
					if (entry.get('item') != null) {
						materialProductionItem.item = objectMapper.convertValue(entry.get('item'), Item) as Item

					}
					materialProductionItem.qty = entry.get('qty') as Integer
					materialProductionItem.materialProduction = afterSaveMaterialProduction
					def afterSaveMaterialProdItem = materialProductionItemRepository.save(materialProductionItem)

					if (afterSaveMaterialProdItem) {

						def item = new HashMap<String, Object>()

						item.put('type', 'source')
						item.put('department', materialProduction.department.id)
						item.put('itemId', afterSaveMaterialProdItem.item.id)
						item.put('reference_no', afterSaveMaterialProduction.mpNo)
						item.put('qty', afterSaveMaterialProdItem.qty)
						item.put('cost', entry.get('lastUnitCost'))

						list.add(item)

					}
				}

			}

			if (fields.get('postToLedger') as Boolean) {
				inventoryLedgerService.insertLedgerMaterialProd(list)
				afterSaveMaterialProduction.status = "POSTED"
				materialProductionRepository.save(afterSaveMaterialProduction)
			}

			return afterSaveMaterialProduction
		} else {
			def materialProduction = new MaterialProduction()
			def list = new ArrayList<Map<String, Object>>()

			materialProduction.quantity = fields.get('qty') as Integer
			materialProduction.item = objectMapper.convertValue(fields.get('item'), Item)
			materialProduction.department = objectMapper.convertValue(fields.get('department'), Department)
			materialProduction.unitCost = fields.get('unit_cost') as BigDecimal
			materialProduction.mpNo = generatorService.getNextValue(GeneratorType.MP_NO) { Long no ->
				StringUtils.leftPad(no.toString(), 6, "0")
			}
			def afterSaveMaterialProduction = materialProductionRepository.save(materialProduction)

			if (afterSaveMaterialProduction) {
				def item = new HashMap<String, Object>()

				item.put('type', 'output')
				item.put('department', materialProduction.department.id)
				item.put('itemId', afterSaveMaterialProduction.item.id)
				item.put('reference_no', afterSaveMaterialProduction.mpNo)
				item.put('qty', afterSaveMaterialProduction.quantity)
				item.put('cost', afterSaveMaterialProduction.unitCost)

				list.add(item)
			}

			items.eachWithIndex { Map<String, Object> entry, int i ->
				def materialProductionItem = new MaterialProductionItem()
				materialProductionItem.item = objectMapper.convertValue(entry.get('item'), Item)
				materialProductionItem.qty = entry.get('qty') as Integer
				materialProductionItem.materialProduction = afterSaveMaterialProduction
				def afterSaveMaterialProdItem = materialProductionItemRepository.save(materialProductionItem)

				if (afterSaveMaterialProdItem) {

					def item = new HashMap<String, Object>()

					item.put('type', 'source')
					item.put('department', materialProduction.department.id)
					item.put('itemId', afterSaveMaterialProdItem.item.id)
					item.put('reference_no', afterSaveMaterialProduction.mpNo)
					item.put('qty', afterSaveMaterialProdItem.qty)
					item.put('cost', entry.get('lastUnitCost'))

					list.add(item)

				}
			}

			//inventoryLedgerService.insertLedgerMaterialProd(list)

			return afterSaveMaterialProduction
		}
	}

	@GraphQLMutation(name = "void_material_production_mutation")
	MaterialProduction voidMaterialProduction(@GraphQLArgument(name = 'fields') Map<String, Object> fields, @GraphQLArgument(name = 'items') List<Map<String, Object>> items) {
		if (fields.get('id') != null) {
			def materialProduction = materialProductionRepository.findById(UUID.fromString(fields.get('id').toString())).get()
			def list = new ArrayList<Map<String, Object>>()

			materialProduction.quantity = fields.get('qty') as Integer
			materialProduction.item = objectMapper.convertValue(fields.get('item'), Item)
			materialProduction.department = objectMapper.convertValue(fields.get('department'), Department)
			materialProduction.unitCost = fields.get('unit_cost') as BigDecimal

			def afterSaveMaterialProduction = materialProductionRepository.save(materialProduction)

			if (afterSaveMaterialProduction) {
				def item = new HashMap<String, Object>()

				item.put('type', 'output')
				item.put('department', materialProduction.department.id)
				item.put('itemId', afterSaveMaterialProduction.item.id)
				item.put('reference_no', afterSaveMaterialProduction.mpNo)
				item.put('qty', afterSaveMaterialProduction.quantity * -1)
				item.put('cost', afterSaveMaterialProduction.unitCost)

				list.add(item)
			}

			items.eachWithIndex { Map<String, Object> entry, int i ->
				if (entry.get('id') != null) {
					def materialProductionItem = materialProductionItemRepository.findById(UUID.fromString(entry.get('id').toString())).get()
					if (entry.get('item') != null) {
						materialProductionItem.item = objectMapper.convertValue(entry.get('item'), Item) as Item
					}
					materialProductionItem.qty = entry.get('qty') as Integer
					materialProductionItem.materialProduction = afterSaveMaterialProduction
					def afterSaveMaterialProdItem = materialProductionItemRepository.save(materialProductionItem)

					if (afterSaveMaterialProdItem) {

						def item = new HashMap<String, Object>()

						item.put('type', 'source')
						item.put('department', materialProduction.department.id)
						item.put('itemId', afterSaveMaterialProdItem.item.id)
						item.put('reference_no', afterSaveMaterialProduction.mpNo)
						item.put('qty', afterSaveMaterialProdItem.qty * -1)
						item.put('cost', entry.get('lastUnitCost'))

						list.add(item)

					}
				}

			}

			inventoryLedgerService.insertLedgerMaterialProd(list)
			afterSaveMaterialProduction.status = "VOIDED"
			materialProductionRepository.save(afterSaveMaterialProduction)

			return afterSaveMaterialProduction
		}
	}

	// -- mutation ni Wilson -- //
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "upsertMaterialProduction", description = "insert BEG")
	MaterialProduction upsertMaterialProduction(
			@GraphQLArgument(name = "parentId") UUID parentId,
			@GraphQLArgument(name = "otherValue") Map<String, Object> otherValue,
			@GraphQLArgument(name = "output") ArrayList<Map<String, Object>> output,
			@GraphQLArgument(name = "source") ArrayList<Map<String, Object>> source
	) {
		MaterialProduction upsert = new MaterialProduction()
		def obj = objectMapper.convertValue(otherValue, MaterialProduction)
		def philDate = obj.dateTransaction.plus(Duration.ofHours(8))
		def outputItems = output as ArrayList<MaterialProdItemDto>
		def sourceItems = source as ArrayList<MaterialProdItemDto>
		try { //try clause
			if (parentId) { //update
				upsert = materialProductionRepository.findById(parentId).get()
				upsert.dateTransaction = philDate
				upsert.department = obj.department
				upsert.description = obj.description

				outputItems.each {
					it ->
						def item = objectMapper.convertValue(it.item, Item)
						def upsertItems = new MaterialProductionItem()
						if (it.isNew) {
							upsertItems.materialProduction = upsert
							upsertItems.item = item
							upsertItems.qty = it.qty
							upsertItems.unitCost = it.unitCost
							upsertItems.type = 'output'
							materialProductionItemRepository.save(upsertItems)
						} else {
							upsertItems = materialProductionItemRepository.findById(UUID.fromString(it.id)).get()
							upsertItems.qty = it.qty
							materialProductionItemRepository.save(upsertItems)
						}
				}

				sourceItems.each {
					it ->
						def item = objectMapper.convertValue(it.item, Item)
						def upsertItems = new MaterialProductionItem()
						if (it.isNew) {
							upsertItems.materialProduction = upsert
							upsertItems.item = item
							upsertItems.qty = it.qty
							upsertItems.unitCost = it.unitCost
							upsertItems.type = 'source'
							materialProductionItemRepository.save(upsertItems)
						} else {
							upsertItems = materialProductionItemRepository.findById(UUID.fromString(it.id)).get()
							upsertItems.qty = it.qty
							materialProductionItemRepository.save(upsertItems)
						}
				}

			} else { //insert
				upsert.mpNo = generatorService.getNextValue(GeneratorType.MP_NO) { Long no ->
					'MP-' + StringUtils.leftPad(no.toString(), 6, "0")
				}
				upsert.dateTransaction = obj.dateTransaction
				upsert.department = obj.department
				upsert.description = obj.description
				upsert.producedBy = obj.producedBy
				upsert.status = "NEW"
				upsert.isPosted = false
				def afterSave = materialProductionRepository.save(upsert)

				//insert sources
				outputItems.each {
					it ->
						def item = objectMapper.convertValue(it.item, Item)
						def upsertItems = new MaterialProductionItem()
						if (it.isNew) {
							upsertItems.materialProduction = afterSave
							upsertItems.item = item
							upsertItems.qty = it.qty
							upsertItems.unitCost = it.unitCost
							upsertItems.type = 'output'
							materialProductionItemRepository.save(upsertItems)
						} else {
							upsertItems = materialProductionItemRepository.findById(UUID.fromString(it.id)).get()
							upsertItems.qty = it.qty
							materialProductionItemRepository.save(upsertItems)
						}
				}

				sourceItems.each {
					it ->
						def item = objectMapper.convertValue(it.item, Item)
						def upsertItems = new MaterialProductionItem()
						if (it.isNew) {
							upsertItems.materialProduction = afterSave
							upsertItems.item = item
							upsertItems.qty = it.qty
							upsertItems.unitCost = it.unitCost
							upsertItems.type = 'source'
							materialProductionItemRepository.save(upsertItems)
						} else {
							upsertItems = materialProductionItemRepository.findById(UUID.fromString(it.id)).get()
							upsertItems.qty = it.qty
							materialProductionItemRepository.save(upsertItems)
						}
				}
			}
		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		return upsert
	}

	//update mutation ni wilson
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "upsertMP", description = "insert MP")
	MaterialProduction upsertMP(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "items") ArrayList<Map<String, Object>> items
	) {
		MaterialProduction upsert = new MaterialProduction()
		def obj = objectMapper.convertValue(fields, MaterialProduction)
		def mpItems = items as ArrayList<MaterialProdItemDto>
		try { //try clause
			if (id) { //update
				upsert = materialProductionRepository.findById(id).get()
				upsert.dateTransaction =  obj.dateTransaction
				upsert.department = obj.department
				upsert.description = obj.description
				upsert.accType = obj.accType

				mpItems.each {
					it ->
						def item = objectMapper.convertValue(it.item, Item)
						def upsertItems = new MaterialProductionItem()
						if (it.isNew) {
							upsertItems.materialProduction = upsert
							upsertItems.item = item
							upsertItems.qty = it.qty
							upsertItems.unitCost = it.unitCost
							upsertItems.type = it.type
							upsertItems.isPosted = false
							materialProductionItemRepository.save(upsertItems)
						} else {
							upsertItems = materialProductionItemRepository.findById(UUID.fromString(it.id)).get()
							upsertItems.qty = it.qty
							upsertItems.unitCost = it.unitCost
							materialProductionItemRepository.save(upsertItems)
						}
				}


			} else { //insert
				upsert.mpNo = generatorService.getNextValue(GeneratorType.MP_NO) { Long no ->
					'MP-' + StringUtils.leftPad(no.toString(), 6, "0")
				}
				upsert.dateTransaction = obj.dateTransaction
				upsert.department = obj.department
				upsert.description = obj.description
				upsert.status = "NEW"
				upsert.producedBy = obj.producedBy
				upsert.accType = obj.accType
				upsert.isPosted = false
				def afterSave = materialProductionRepository.save(upsert)

				//insert sources
				mpItems.each {
					it ->
						def item = objectMapper.convertValue(it.item, Item)
						def upsertItems = new MaterialProductionItem()
						upsertItems.materialProduction = afterSave
						upsertItems.item = item
						upsertItems.qty = it.qty
						upsertItems.unitCost = it.unitCost
						upsertItems.type = it.type
						upsertItems.isPosted = false
						materialProductionItemRepository.save(upsertItems)
				}
			}
		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		return upsert
	}

	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "removeMpItem")
	MaterialProductionItem removeMpItem(
			@GraphQLArgument(name = "id") UUID id
	) {
		MaterialProductionItem upsert = new MaterialProductionItem()
		try { //try clause
			if (id) { //update
				upsert = materialProductionItemRepository.findById(id).get()
				materialProductionItemRepository.delete(upsert)
			}
		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		return upsert
	}

	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "postMaterialProd", description = "insert Material Prod")
	InventoryLedger postMaterialProd(
			@GraphQLArgument(name = "parentId") UUID parentId,
			@GraphQLArgument(name = "transDate") String transDate
	) {
		InventoryLedger upsert = new InventoryLedger()

		def source = materialProductionItemRepository.getAllMaterialProductionItemsByType(parentId,'source')
		def output = materialProductionItemRepository.getAllMaterialProductionItemsByType(parentId,'output')
		def parent = materialProductionRepository.findById(parentId).get()
		def docType = "27d236bb-c023-44dc-beac-18ddfe1daf79"

		//out source
		source.each {
			it ->
				def upsertSource = new InventoryLedger()
				LocalTime localTime = LocalTime.now()
				String date = transDate + " " + localTime
				//date time
				upsertSource.sourceDep = it.materialProduction.department
				upsertSource.destinationDep = it.materialProduction.department
				upsertSource.documentTypes = documentTypeRepository.findById(UUID.fromString(docType)).get()
				upsertSource.item = it.item
				upsertSource.referenceNo = it.materialProduction.mpNo
				upsertSource.ledgerDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")).atZone(ZoneId.of("Asia/Manila")).toInstant().plus(Duration.ofHours(8))
				upsertSource.ledgerQtyIn = 0
				upsertSource.ledgerQtyOut = it.qty
				upsertSource.ledgerPhysical = 0
				upsertSource.ledgerUnitCost = it.unitCost
				upsertSource.isInclude = true
				inventoryLedgerRepository.save(upsertSource)
		}

		//in
		output.each {
			it ->
				def upsertOutput = new InventoryLedger()
				LocalTime localTime = LocalTime.now()
				String date = transDate + " " + localTime
				//date time
				upsertOutput.sourceDep = it.materialProduction.department
				upsertOutput.destinationDep = it.materialProduction.department
				upsertOutput.documentTypes = documentTypeRepository.findById(UUID.fromString(docType)).get()
				upsertOutput.item = it.item
				upsertOutput.referenceNo = it.materialProduction.mpNo
				upsertOutput.ledgerDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")).atZone(ZoneId.of("Asia/Manila")).toInstant().plus(Duration.ofHours(8))
				upsertOutput.ledgerQtyIn = it.qty
				upsertOutput.ledgerQtyOut = 0
				upsertOutput.ledgerPhysical = 0
				upsertOutput.ledgerUnitCost = it.unitCost
				upsertOutput.isInclude = true
				inventoryLedgerRepository.save(upsertOutput)
		}


		//update parent
		parent.isPosted = true
		parent.status = "Posted"
		materialProductionRepository.save(parent)

		return upsert
	}

	@GraphQLMutation(name = "voidMaterialProd", description = "insert Material Prod")
	InventoryLedger voidMaterialProd(
			@GraphQLArgument(name = "parentId") UUID parentId,
			@GraphQLArgument(name = "transDate") String transDate
	) {
		def parent = materialProductionRepository.findById(parentId).get()
		def listLedger = inventoryLedgerRepository.getLedgerByRefNo(parent.mpNo)
		def upsert = new InventoryLedger()
		try {
			listLedger.each {
				it->
					upsert = it
					upsert.isInclude = false
					inventoryLedgerRepository.save(upsert)
			}

		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}

		//update parent
		parent.isPosted = false
		parent.status = "Voided"
		materialProductionRepository.save(parent)

		return upsert
	}

//	ADONIS CODE
	@GraphQLQuery(name = 'filterableMaterialProductionByDept', description = 'list of all material production with pagination by dept')
	Page<MaterialProduction> filterableMaterialProductionByDept(
			@GraphQLArgument(name = "pageSize") Integer pageSize,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "deptID") UUID deptID,
			@GraphQLArgument(name = "filter") String filter
	) {
		return materialProductionRepository.filterableMaterialProductionByDept(deptID, filter, PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, 'mpNo')))
	}

	//wilson code
	@GraphQLQuery(name = 'filterableMaterialProductionByDeptPage', description = 'list of all material production with pagination by dept')
	Page<MaterialProduction> filterableMaterialProductionByDeptPage(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "startDate") String startDate,
			@GraphQLArgument(name = "endDate") String endDate,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "pageSize") Integer pageSize
	) {
		return materialProductionRepository.filterableMaterialProductionByDeptPage(filter,
				id, startDate, endDate, new PageRequest(page, pageSize, Sort.Direction.DESC, "mpNo"))
	}

	//wilson
	//post inventory for material production
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "postMaterialProdTransaction")
	GraphQLRetVal<Boolean> postMaterialProdTransaction(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "items") ArrayList<Map<String, Object>> items
	) {
		//get parents
		def material = materialProductionRepository.findById(id).get()
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
							ledgerQtyIn: it.type.equalsIgnoreCase("MPO") ? it.qty : 0,
							ledgerQtyOut: it.type.equalsIgnoreCase("MPO") ? 0: it.qty,
							ledgerPhysical: it.physical,
							ledgerUnitCost: it.unitcost,
							isInclude: true,
					)
					inventoryLedgerService.postInventoryGlobal(item)

					//update mp item to posted
					def mpItem =materialProductionItemRepository.findById(UUID.fromString(it.id)).get()
					mpItem.isPosted = true
					materialProductionItemRepository.save(mpItem)
				}
			}

			//post to accounting --to do accounting entries
			def transType = transactionTypeService.transTypeById(material.accType)
			if(integrationServices.getIntegrationByDomainAndTagValue(MaterialProduction.class.name, transType.flagValue)){
				this.saveToJournalEntryMaterialProduction(material)
			}
			//end post to account

			return new GraphQLRetVal<Boolean>(true,  true,"Items are now posted to ledger")
		} catch (Exception e) {
			throw new Exception("Something was Wrong : " + e)
		}

	}

	@GraphQLMutation(name = "voidMaterialProdTransaction")
	GraphQLRetVal<Boolean> voidMaterialProdTransaction(
			@GraphQLArgument(name = "id") UUID id
	) {
		//get parents
		def material = materialProductionRepository.findById(id).get()
		def postItems = materialProductionItemRepository.getAllMaterialProductionItemsPosted(id)
		//get items to void
		try {
			//void to accounting --to do accounting entries
			//reverse accounting entry
			if(material.postedLedger){
				def header = ledgerServices.findOne(material.postedLedger)
				ledgerServices.reverseEntriesCustom(header, material.dateTransaction)
			}
			//end void to account

			if(postItems){
				postItems.each {
					//update mp items to false
					def update = it
					update.isPosted = false
					materialProductionItemRepository.save(update)
				}
			}
			//void inventory to inventory ledger
			inventoryLedgerService.voidInventoryGlobal(material.mpNo)

			//update parent
			material.isPosted = false
			material.status = "Voided"
			material.postedLedger= null //to be updated for accounting
			materialProductionRepository.save(material)
			return new GraphQLRetVal<Boolean>(true,  true,"Items are now voided to ledger")
		} catch (Exception e) {
			throw new Exception("Something was Wrong : " + e)
		}

	}

	//view jopurnal entry for material production
	@GraphQLQuery(name = "materialAccountView")
	List<JournalEntryViewDto> materialAccountView(
			@GraphQLArgument(name = "id") UUID id
	){
		def result = new ArrayList<JournalEntryViewDto>()
		def material = materialProductionRepository.findById(id).get()
		def sItems = materialProductionItemRepository.getAllMaterialProductionItems(id)

		def transType = transactionTypeService.transTypeById(material.accType)
		if(transType){

			Integration match = integrationServices.getIntegrationByDomainAndTagValue(material.domain, transType.flagValue)

			def headerLedger =	integrationServices.generateAutoEntries(material){
				it, mul ->
					it.flagValue = transType.flagValue

					Map<AccountingCategory, BigDecimal> source  = [:]
					Map<AccountingCategory, BigDecimal> output  = [:]

					it.diff_value = BigDecimal.ZERO;
					it.source_value = BigDecimal.ZERO;
					it.output_value = BigDecimal.ZERO;

					//initialize
					Map<String, List<MaterialProduction>> finalAcc  = [:]
					match.integrationItems.findAll { BooleanUtils.isTrue(it.multiple) }.eachWithIndex { IntegrationItem entry, int i ->
						if(!finalAcc.containsKey(entry.sourceColumn)){
							finalAcc[entry.sourceColumn] = []
						}
					}
					//end init

					BigDecimal sourceCost = BigDecimal.ZERO; BigDecimal outputCost = BigDecimal.ZERO;

					sItems.each {a ->
						//=== for multiple  ===//
						if(a.item.accountingCategory){
							BigDecimal cost = a.unitCost * a.qty
							if(!source.containsKey(a.item.accountingCategory))
								source[a.item.accountingCategory] = 0.0

							if(!output.containsKey(a.item.accountingCategory))
								output[a.item.accountingCategory] = 0.0

							if(a.type.equalsIgnoreCase("source")){
								source[a.item.accountingCategory] =  source[a.item.accountingCategory] + cost
								sourceCost = sourceCost + cost
							}else{
								output[a.item.accountingCategory] =  output[a.item.accountingCategory] + cost
								outputCost = outputCost + cost
							}
						}
					}

					source.each {k, v ->
						String sourceColumn = "negative_${k.sourceColumn}";
						if(v > 0){
							finalAcc[sourceColumn] << new MaterialProduction().tap {
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

								it.assignDepartment = material.department
								it.accountingCategory = k
								it[sourceColumn] = v * -1;
							}
						}
					}


					output.each {k, v ->
						if(v > 0){
							finalAcc[k.sourceColumn] << new MaterialProduction().tap {
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

								it.assignDepartment = material.department
								it.accountingCategory = k
								it[k.sourceColumn] = v;
							}
						}
					}

					//loop multiples
					finalAcc.each { key, items ->
						mul << items
					}

					//not multiple
					it.assignDepartment = material.department
					it.diff_value = outputCost - sourceCost

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

	//save to accounting journal
	@Transactional(rollbackFor = Exception.class)
	MaterialProduction saveToJournalEntryMaterialProduction(MaterialProduction materialProduction){
		def id = materialProduction.id
		def material = materialProduction
		def sItems = materialProductionItemRepository.getAllMaterialProductionItems(id)
		def yearFormat = DateTimeFormatter.ofPattern("yyyy")
		def transType = transactionTypeService.transTypeById(material.accType)
		if(transType){

			Integration match = integrationServices.getIntegrationByDomainAndTagValue(material.domain, transType.flagValue)

			def headerLedger =	integrationServices.generateAutoEntries(material){
				it, mul ->
					it.flagValue = transType.flagValue

					Map<AccountingCategory, BigDecimal> source  = [:]
					Map<AccountingCategory, BigDecimal> output  = [:]

					it.diff_value = BigDecimal.ZERO;
					it.source_value = BigDecimal.ZERO;
					it.output_value = BigDecimal.ZERO;

					//initialize
					Map<String, List<MaterialProduction>> finalAcc  = [:]
					match.integrationItems.findAll { BooleanUtils.isTrue(it.multiple) }.eachWithIndex { IntegrationItem entry, int i ->
						if(!finalAcc.containsKey(entry.sourceColumn)){
							finalAcc[entry.sourceColumn] = []
						}
					}
					//end init

					BigDecimal sourceCost = BigDecimal.ZERO; BigDecimal outputCost = BigDecimal.ZERO;

					sItems.each {a ->
						//=== for multiple  ===//
						if(a.item.accountingCategory){
							BigDecimal cost = a.unitCost * a.qty
							if(!source.containsKey(a.item.accountingCategory))
								source[a.item.accountingCategory] = 0.0

							if(!output.containsKey(a.item.accountingCategory))
								output[a.item.accountingCategory] = 0.0

							if(a.type.equalsIgnoreCase("source")){
								source[a.item.accountingCategory] =  source[a.item.accountingCategory] + cost
								sourceCost = sourceCost + cost
							}else{
								output[a.item.accountingCategory] =  output[a.item.accountingCategory] + cost
								outputCost = outputCost + cost
							}
						}
					}

					source.each {k, v ->
						String sourceColumn = "negative_${k.sourceColumn}";
						if(v > 0){
							finalAcc[sourceColumn] << new MaterialProduction().tap {
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

								it.assignDepartment = material.department
								it.accountingCategory = k
								it[sourceColumn] = v * -1;
							}
						}
					}


					output.each {k, v ->
						if(v > 0){
							finalAcc[k.sourceColumn] << new MaterialProduction().tap {
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

								it.assignDepartment = material.department
								it.accountingCategory = k
								it[k.sourceColumn] = v;
							}
						}
					}

					//loop multiples
					finalAcc.each { key, items ->
						mul << items
					}

					//not multiple
					it.assignDepartment = material.department
					it.diff_value = outputCost - sourceCost

			}
			Map<String,String> details = [:]

			material.details.each { k,v ->
				details[k] = v
			}

			details["MP_ID"] = material.id.toString()
			details["MP_DEPARTMENT_ID"] = material.department.id.toString()
			details["MP_DEPARTMENT_NAME"] = material.department.departmentName

			def pHeader =	ledgerServices.persistHeaderLedger(headerLedger,
					"${material.dateTransaction.atZone(ZoneId.systemDefault()).format(yearFormat)}-${material.mpNo}",
					"${material.mpNo}-${material.department?.departmentName}",
					"${material.mpNo}-${material.description.toUpperCase()}-${material.department?.departmentName}",
					LedgerDocType.PA,
					JournalType.GENERAL,
					material.createdDate,
					details)

			//update parent
			material.isPosted = true
			material.postedLedger = pHeader.id
			material.status = "Posted"
			materialProductionRepository.save(material)

		}
		return material
	}

}
