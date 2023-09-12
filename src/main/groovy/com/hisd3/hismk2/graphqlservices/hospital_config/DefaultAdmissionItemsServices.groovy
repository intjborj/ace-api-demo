package com.hisd3.hismk2.graphqlservices.hospital_config

import com.hisd3.hismk2.domain.accounting.*
import com.hisd3.hismk2.domain.billing.Billing
import com.hisd3.hismk2.domain.billing.BillingItem
import com.hisd3.hismk2.domain.billing.BillingItemType
import com.hisd3.hismk2.domain.billing.PriceTierDetail
import com.hisd3.hismk2.domain.hospital_config.DefaultAdmissionItems
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.graphqlservices.accounting.*
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.billing.BillingItemServices
import com.hisd3.hismk2.graphqlservices.billing.BillingService
import com.hisd3.hismk2.graphqlservices.exceptions.BillingException
import com.hisd3.hismk2.graphqlservices.pms.CaseService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.accounting.AccountReceivableItemsRepository
import com.hisd3.hismk2.repository.accounting.ArTransactionDetailsRepository
import com.hisd3.hismk2.repository.hospital_config.DefaultAdmissionItemsRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.inventory.InventoryRepository
import com.hisd3.hismk2.repository.inventory.SupplierRepository
import com.hisd3.hismk2.rest.dto.JournalEntryViewDto
import com.hisd3.hismk2.rest.dto.QueryErrorException
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.EntityObjectMapperService
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional


@Canonical
class ToBillingItem{

	UUID inventoryId
	BigDecimal quantity

}

@Component
@GraphQLApi
class DefaultAdmissionItemsServices extends AbstractDaoService<DefaultAdmissionItems> {

	DefaultAdmissionItemsServices() {
		super(DefaultAdmissionItems.class)
	}

	@Autowired
	EntityObjectMapperService entityObjectMapperService

	@Autowired
	DefaultAdmissionItemsRepository defaultAdmissionItemsRepository

	@Autowired
	InventoryRepository inventoryRepository

	@Autowired
	BillingItemServices billingItemServices

	@Autowired
	BillingService billingService

	@Autowired
	JdbcTemplate jdbcTemplate

	@Autowired
	GeneratorService generatorService


	@GraphQLQuery(name = "getAllDefaultAdmissionItems")
	Page<DefaultAdmissionItems> getAllDefaultAdmissionItems(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {
		//language=HQL
		getPageable(
				"""
			 	select da from DefaultAdmissionItems da where 
				(
					lower(da.item.descLong) like concat('%', :filter ,'%')
			  	)
				order by da.createdDate desc
				""", """
				select count(da) from DefaultAdmissionItems da where
				(
					lower(da.item.descLong) like concat('%', :filter ,'%')
			  	)
				""",
				page,
				size,
				[
						filter   : filter
				]

		)

	}

	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation
	DefaultAdmissionItems upsertDefaultAdmissionItems(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		def newSave = null
		if(id){
			def defaultItems = findOne(id)
			entityObjectMapperService.updateFromMap(defaultItems, fields)
			def inv = inventoryRepository.findById(defaultItems.inventory).get()
			if(inv){
				defaultItems.item = inv.item
			}
			newSave = save(defaultItems)
		}
		else {
			def defaultItems = new DefaultAdmissionItems()
			entityObjectMapperService.updateFromMap(defaultItems, fields)
			def inv = inventoryRepository.findById(defaultItems.inventory).get()
			if(inv){
				defaultItems.item = inv.item
			}
			newSave = save(defaultItems)
		}
		return newSave
	}

	@GraphQLMutation
	DefaultAdmissionItems deleteDefaultAdmissionItems(
			@GraphQLArgument(name = "id") UUID id
	) {
		def defaultItems = null
		if(id){

			defaultItems = defaultAdmissionItemsRepository.findById(id).get()
			defaultAdmissionItemsRepository.delete(defaultItems)
		}
		return defaultItems
	}

	static def autoChargeItem(UUID inventoryId, UUID itemId, BigDecimal quantity, String itemType, Boolean isMedicine ) {
		def billingItem = new HashMap<String, Object>()
		billingItem.inventoryId = inventoryId
		billingItem.itemId = itemId
		billingItem.quantity = quantity

		if (itemType.equalsIgnoreCase("Medicine") && isMedicine) {
			return billingItem as List<Map<String, Object>>
		}
		if (itemType.equalsIgnoreCase("Supplies") && !isMedicine) {
			return billingItem as List<Map<String, Object>>
		}
	}


	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "addDefaultAdmission")
	GraphQLRetVal<Boolean> addDefaultAdmission(
			@GraphQLArgument(name = "caseId") UUID caseId
	){
		try{
			Billing billing1 = billingService.billingAdmissionChargeNew(caseId)[0]
			if(billing1 && !billing1.admissionCharge && !billing1.patientCase.refusedKitBag  && billing1.patientCase.registryType.equalsIgnoreCase("IPD")) {

				List<Map<String, Object>> supplies = new ArrayList<Map<String, Object>>()
				List<Map<String, Object>> medicine = new ArrayList<Map<String, Object>>()

				def suppItem = findAll()
				suppItem.each {
					it ->
						if(billing1.patientCase.admissionKit){
							def billingItem = new HashMap<String, Object>()
							billingItem.inventoryId = it.inventory
							billingItem.quantity = it.quantity
							billingItem.itemId = it.item.id
							if (billing1.patientCase.admissionKit.equalsIgnoreCase(it.admissionKitType)) {
								if (it.item.isMedicine) {
									medicine.push(billingItem)
								}
								else {
									supplies.push(billingItem)
								}
							}
						}
						else{
							if(!(it.admissionKitType)) {
								def billingItem = new HashMap<String, Object>()
								billingItem.inventoryId = it.inventory
								billingItem.quantity = it.quantity
								billingItem.itemId = it.item.id
								if (it.item.isMedicine) {
									medicine.push(billingItem)
								}
								else {
									supplies.push(billingItem)
								}
							}
						}
				}


				if (medicine) {
					billingItemServices.addBillingItem(billing1.id, "MEDICINES", medicine)
				}

				if (supplies) {
					billingItemServices.addBillingItem(billing1.id, "SUPPLIES", supplies)
				}

				billing1.admissionCharge = true
				billingService.save(billing1)
				return new GraphQLRetVal<Boolean>(true,true,'Success')

			}
			else
				return new GraphQLRetVal<Boolean>(false,false,'Conditions not met.')

		} catch(e){
			return new GraphQLRetVal<Boolean>(false,false,e.message)
		}
	}

	@GraphQLQuery(name="findAdmissionKitByType")
	List<DefaultAdmissionItems> findAdmissionKitByType(
			@GraphQLArgument(name="type") String type
	){

		def result = createQuery("""Select d from DefaultAdmissionItems d where d.admissionKitType = :type """,[type:type]).resultList
		if(result){
			return  result as List<DefaultAdmissionItems>
		}
		return new ArrayList<DefaultAdmissionItems>()
	}

	@GraphQLMutation(name ="additionAdmissionKit")
	GraphQLRetVal<Boolean> additionAdmissionKit(@GraphQLArgument(name="caseId") UUID caseId,@GraphQLArgument(name="type") String type){

		if(caseId) {
			Billing billing1 = billingService.billingAdmissionChargeNew(caseId)[0]
			if(billing1 && billing1.admissionCharge && !type.equalsIgnoreCase('REFUSE KITBAG')  && billing1.patientCase.registryType.equalsIgnoreCase("IPD") &&
					type && billing1.patientCase && billing1.patientCase.admissionKit != type
			){
				def items = findAdmissionKitByType(type)

					List<Map<String, Object>> supplies = new ArrayList<Map<String, Object>>()
					List<Map<String, Object>> medicine = new ArrayList<Map<String, Object>>()

					if (items) {
						items.each {
							it ->
								def billingItem = new HashMap<String, Object>()
								billingItem.inventoryId = it.inventory
								billingItem.quantity = it.quantity
								billingItem.itemId = it.item.id
								if (!it.item.isMedicine) {
									supplies.push(billingItem)
								}
								if (it.item.isMedicine) {
									medicine.push(billingItem)
								}
						}
					}

					if (medicine) {
						billingItemServices.addBillingItem(billing1.id, "MEDICINES", medicine)
					}
					if (supplies) {
						billingItemServices.addBillingItem(billing1.id, "SUPPLIES", supplies)
					}

					return new GraphQLRetVal<Boolean>(true, true, 'Success')

				}
			return new GraphQLRetVal<Boolean>(false, false, 'Invalid Request')
		}
		return new GraphQLRetVal<Boolean>(false, false, 'Invalid Request')
	}
}
