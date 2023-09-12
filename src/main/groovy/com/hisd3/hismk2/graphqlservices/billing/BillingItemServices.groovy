package com.hisd3.hismk2.graphqlservices.billing

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.hisd3.hismk2.dao.price_tier.PriceTierDetailDao
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.accounting.AccountReceivableItems
import com.hisd3.hismk2.domain.accounting.Disbursement
import com.hisd3.hismk2.domain.accounting.JournalType
import com.hisd3.hismk2.domain.accounting.LedgerDocType
import com.hisd3.hismk2.domain.accounting.LedgerHeaderDetailParam
import com.hisd3.hismk2.domain.ancillary.OrderSlipItem
import com.hisd3.hismk2.domain.ancillary.OrderSlipItemPackageContent
import com.hisd3.hismk2.domain.ancillary.PackageContent
import com.hisd3.hismk2.domain.ancillary.RfFees
import com.hisd3.hismk2.domain.ancillary.ServiceTypes
import com.hisd3.hismk2.domain.billing.*
import com.hisd3.hismk2.domain.inventory.AccountingCategory
import com.hisd3.hismk2.domain.inventory.CashBasisItem
import com.hisd3.hismk2.domain.inventory.DepartmentItem
import com.hisd3.hismk2.domain.inventory.Inventory
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.inventory.ReceivingReport
import com.hisd3.hismk2.domain.pms.FlowRate
import com.hisd3.hismk2.graphqlservices.accounting.IntegrationServices
import com.hisd3.hismk2.graphqlservices.accounting.LedgerServices
import com.hisd3.hismk2.graphqlservices.ancillary.OrderSlipItemPackageContentService
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.cashiering.PaymentTrackerServices
import com.hisd3.hismk2.graphqlservices.exceptions.BillingException
import com.hisd3.hismk2.graphqlservices.hospital_config.HospitalConfigService
import com.hisd3.hismk2.graphqlservices.inventory.InventoryLedgerService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.ancillary.OrderSlipItemRepository
import com.hisd3.hismk2.repository.ancillary.PackageContentRepository
import com.hisd3.hismk2.repository.ancillary.RfFeesRepository
import com.hisd3.hismk2.repository.ancillary.ServiceRepository
import com.hisd3.hismk2.repository.billing.PriceTierDetailRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.inventory.CashBasisItemRepository
import com.hisd3.hismk2.repository.inventory.DepartmentItemRepository
import com.hisd3.hismk2.repository.inventory.InventoryRepository
import com.hisd3.hismk2.repository.inventory.ItemRepository
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.rest.InventoryResource
import com.hisd3.hismk2.rest.dto.AmountDetailsDto
import com.hisd3.hismk2.rest.dto.ItemServiceDto
import com.hisd3.hismk2.rest.dto.PrItems
import com.hisd3.hismk2.rest.dto.SalesReportDetailedDto
import com.hisd3.hismk2.rest.dto.StockCard
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import com.hisd3.hismk2.services.InventoryLedgService
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLContext
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.ArrayUtils
import org.apache.commons.lang3.BooleanUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.math.NumberUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional
import java.lang.reflect.Type
import java.math.RoundingMode
import java.sql.ResultSet
import java.sql.SQLException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Canonical
class ReturnItemInfo {
	UUID id
	String description
	Integer qty

}

@Canonical
class Salesreportitem {
	UUID id
	String category
	Instant date
	String ornos
	String folio
	String recno
	String department
	String service_code
	String process_code
	String service
	BigDecimal gross
	BigDecimal vatable_sales
	BigDecimal vat_exempt_sales
	BigDecimal vat_amount
	String discounts_availed
	BigDecimal discounts_total
	BigDecimal net_sales

}

@Canonical
class SalesreportitemTotals {

	BigDecimal gross
	BigDecimal discounts_total
	BigDecimal net_sales
}

class RfDetails {
	String rfTableId
	String doctorsId
	String serviceId
	String percentage
	String rfValue
}

// Predefined HardCode Integration Rules

enum SALES_INTEGRATION{
	  IP_MEDS ,
	  IP_SUPPLIES,
	  IP_SERVICES,
	  IP_OXYGEN,
	  IP_ROOM,

	  OPD_MEDS,
	  OPD_SUPPLIES,
	  OPD_SERVICES,
	  OPD_OXYGEN,
	  OPD_ROOM,

	  ER_MEDS,
	  ER_SUPPLIES,
	  ER_SERVICES,
	  ER_ROOM,
	  ER_OXYGEN,

	  OTC_MEDS,
	  OTC_SUPPLIES,
	  OTC_SERVICES,

	  OTC_NONVAT_MEDS,
	  OTC_NONVAT_SUPPLIES,
	  OTC_NONVAT_SERVICES,

}




// ALL COMPANY ACCOUNT DEDUCTIONS ARE POSTED ONLY DURING RECOGNITION


@Transactional(rollbackOn = [Exception.class])
@Service
@GraphQLApi
class BillingItemServices extends AbstractDaoService<BillingItem> {
	BillingItemServices() {
		super(BillingItem.class)
	}

	@Value('${accounting.autopostjournal}')
	Boolean auto_post_journal

	@Value('${accounting.enable_costing}')
	Boolean enable_costing


	@Autowired
	BillingService billingService

	@Autowired
	PriceTierDetailDao priceTierDetailDao

	@Autowired
	UserRepository userRepository

	@Autowired
	EmployeeRepository employeeRepository

	@Autowired
	GeneratorService generatorService

	@Autowired
	CaseRepository caseRepository

	@Autowired
	OrderSlipItemRepository orderSlipItemRepository

	@Autowired
	InventoryLedgerService inventoryLedgerService

	@Autowired
	InventoryLedgService inventoryLedgService

	@Autowired
	InventoryRepository inventoryRepository

	@Autowired
	DepartmentRepository departmentRepository

	@Autowired
	ServiceRepository serviceRepository

	@Autowired
	ItemRepository itemRepository

	@Autowired
	CashBasisItemRepository cashBasisItemRepository

	@Autowired
	PriceTierDetailRepository priceTierDetailRepository

	@Autowired
	JdbcTemplate jdbcTemplate

	@Autowired
	PaymentTrackerServices paymentTrackerServices

	@Autowired
	PackageContentRepository packageContentRepository

	@Autowired
	RfFeesRepository rfFeesRepository
	@Autowired
	IntegrationServices integrationServices

	@Autowired
	LedgerServices ledgerServices


	@Autowired
	InventoryResource inventoryResource

	@Autowired
	HospitalConfigService hospitalConfigService

	@PersistenceContext
	EntityManager entityManager

	@Autowired
	DepartmentItemRepository departmentItemRepository

	@Autowired
	OrderSlipItemPackageContentService orderSlipItemPackageContentService


	@GraphQLQuery(name = "billingItemsEligibleForMiscGrouping")
	List<BillingItem> billingItemsEligibleForMiscGrouping(@GraphQLArgument(name = "billingId") UUID billingId,
														  @GraphQLArgument(name = "filter") String filter){


		def billing = billingService.findOne(billingId)
		def items  = createQuery("""from BillingItem bi where bi.soaGrouping is null and bi.itemType=:itemType and bi.status = :status and
						(
						   lower(bi.recordNo) like lower(concat('%',:filter,'%'))
						   or
						   lower(bi.description) like lower(concat('%',:filter,'%'))
						)
						and billing=:billing
   """,
				[
						itemType:BillingItemType.OTHERS,
						status: BillingItemStatus.ACTIVE,
						filter:filter,
						billing: billing
				])
		.resultList

		items.toSorted({a,b ->
			a.recordNo <=> b.recordNo

		})
	}

	List<BillingItem> getBillingItemsByOr( String ornumber
	) {
		 createQuery("from BillingItem bi where bi.details['ORNUMBER'] = :ornumber and bi.status=:status",
		 [ornumber:ornumber,
		  status:BillingItemStatus.ACTIVE])
		.resultList
	}

	 BillingItem getBillingItemsByRecno( String recno ) {
		createQuery("from BillingItem bi where bi.recordNo=:recno",
				[recno:recno])
				.resultList.find()
	}



	@GraphQLQuery(name = "billingItemsDetails")
	Map<String, String> billingItemsDetails(@GraphQLArgument(name = "billingItemId") UUID billingItemId
	) {
		def bi = findOne(billingItemId)
		bi.details
	}

	@GraphQLQuery(name = "billingItemById")
	BillingItem billingItemById(@GraphQLArgument(name = "billingItemId") UUID billingItemId
	) {
		findOne(billingItemId)
	}


	List<BillingItem> billingItemByIds( List<UUID> billingItemIds
	) {
		 createQuery("from BillingItem bi where bi.id in (:billingItemIds)",[
				 billingItemIds:billingItemIds
		 ]).resultList
	}


	@GraphQLQuery(name = "hospitalItems")
	Page<BillingItem> hospitalItems(@GraphQLArgument(name = "billingId") UUID billingItemId,
	                                @GraphQLArgument(name = "page") Integer page,
	                                @GraphQLArgument(name = "size") Integer size,
	                                @GraphQLArgument(name = "filter") String filter
	) {

		getPageable("""
                    select b from BillingItem b where b.billing.id = :billingId and b.itemType in :itemTypes
                    and (lower(b.description) like concat('%',:filter,'%') or lower(b.recordNo) like concat('%',:filter,'%'))
                    and b.status=:status
                    order by b.createdDate
            """,
				"""
             select count(b) from BillingItem b where b.billing.id = :billingId and b.itemType in :itemTypes
                   and (lower(b.description) like concat('%',:filter,'%') or lower(b.recordNo) like concat('%',:filter,'%'))
                   and b.status=:status
                """,
				page,
				size,
				[
						billingId: billingItemId,
						itemTypes: [
								BillingItemType.ROOMBOARD,
								BillingItemType.MEDICINES,
								BillingItemType.DIAGNOSTICS,
								BillingItemType.ORFEE,
								BillingItemType.SUPPLIES,
								BillingItemType.OTHERS

						],
						filter   : filter,
						status   : BillingItemStatus.ACTIVE
				] as Map<String, Object>)

	}

	@GraphQLQuery(name = "billingItemByTypes")
	Page<BillingItem> getBillingItemsByTypes(@GraphQLArgument(name = "billingId") UUID billingItemId,
	                                         @GraphQLArgument(name = "page") Integer page,
	                                         @GraphQLArgument(name = "size") Integer size,
	                                         @GraphQLArgument(name = "filter") String filter,
	                                         @GraphQLArgument(name = "billingType") List<String> billingTypes
	) {

		getPageable("""
                    select b from BillingItem b where b.billing.id = :billingId and b.itemType in :itemTypes
                    and (lower(b.description) like lower(concat('%',:filter,'%')) or lower(b.item) like lower(concat('%',:filter,'%')))
                    and b.status=:status
                    order by b.createdDate
            """,
				"""
             select count(b) from BillingItem b where b.billing.id = :billingId and b.itemType in :itemTypes
                   and (lower(b.description) like lower(concat('%',:filter,'%')) or lower(b.item) like lower(concat('%',:filter,'%')))
                   and b.status=:status
                """,
				page,
				size,
				[
						billingId: billingItemId,
						itemTypes: billingTypes.collect { BillingItemType.valueOf(it) },
						filter   : filter,
						status   : BillingItemStatus.ACTIVE
				] as Map<String, Object>)

	}

	@GraphQLQuery(name = "pfItems")
	Page<BillingItem> pfItems(@GraphQLArgument(name = "billingId") UUID billingItemId,
	                          @GraphQLArgument(name = "page") Integer page,
	                          @GraphQLArgument(name = "size") Integer size,
	                          @GraphQLArgument(name = "filter") String filter
	) {

		getPageable("""
                    select b from BillingItem b where b.billing.id = :billingId and b.itemType in :itemTypes
                    and (lower(b.description) like concat('%',:filter,'%') or lower(b.recordNo) like concat('%',:filter,'%'))
                    and b.status=:status
                     order by b.createdDate
            """,
				"""
             select count(b) from BillingItem b where b.billing.id = :billingId and b.itemType in :itemTypes
                   and (lower(b.description) like concat('%',:filter,'%') or lower(b.recordNo) like concat('%',:filter,'%'))
                   and b.status=:status
                """,
				page,
				size,
				[
						billingId: billingItemId,
						itemTypes: [
								BillingItemType.PF,

						],
						filter   : filter,
						status   : BillingItemStatus.ACTIVE
				] as Map<String, Object>)

	}

	@GraphQLQuery
	List<BillingItem> getBillingItemsAll(@GraphQLArgument(name = "billingId") UUID billingId,
	                                     @GraphQLArgument(name = "billingItemType") List<String> billingItemType,
	                                     @GraphQLArgument(name = "filter") String filter
	) {

		createQuery("""
                    select b from BillingItem b where b.billing.id = :billingId and b.itemType in :itemTypes
                    and (lower(b.description) like concat('%',:filter,'%') or lower(b.recordNo) like concat('%',:filter,'%'))  order by b.createdDate
            """,
				[
						billingId: billingId,
						itemTypes: billingItemType.collect { BillingItemType.valueOf(it) },
						filter   : filter
				] as Map<String, Object>).resultList

	}


	@GraphQLQuery(name="getBillingItemsAllNoType")
	List<BillingItem> getBillingItemsAllNoType(@GraphQLArgument(name = "billingId") UUID billingId
	) {

		createQuery("""
                    select b from BillingItem b where b.billing.id = :billingId and b.status=:status  order by b.recordNo
            """,
				[
						billingId: billingId,
						status   : BillingItemStatus.ACTIVE
				] as Map<String, Object>).resultList

	}



	@GraphQLQuery
	Page<BillingItem> getBillingItems(@GraphQLArgument(name = "billingId") UUID billingId,
	                                  @GraphQLArgument(name = "billingItemType") List<String> billingItemType,
	                                  @GraphQLArgument(name = "page") Integer page,
	                                  @GraphQLArgument(name = "size") Integer size,
	                                  @GraphQLArgument(name = "filter") String filter
	) {
		Page<BillingItem> pagez = getPageable("""
                    select b from BillingItem b where b.billing.id = :billingId and b.itemType in :itemTypes
                    and (lower(b.description) like lower(concat('%',:filter,'%')) or lower(b.recordNo) like lower(concat('%',:filter,'%')))  order by b.createdDate desc
            """,
				"""
             select count(b) from BillingItem b where b.billing.id = :billingId and b.itemType in :itemTypes
                   and (lower(b.description) like lower(concat('%',:filter,'%')) or lower(b.recordNo) like lower(concat('%',:filter,'%')))
                """,
				page,
				size,
				[
						billingId: billingId,
						itemTypes: billingItemType.collect { BillingItemType.valueOf(it) },
						filter   : filter
				] as Map<String, Object>)

		if(billingItemType.size()==1 && billingItemType.get(0)=="MEDICINES")
		{

			for(BillingItem ii in pagez.getContent())
			{

				Item item = itemRepository.getOne(UUID.fromString(ii.details["ITEMID"]))
				ii.pnf = item.pnf
			}
			return pagez;
		}

		return pagez

	}

	// Depreciated
	/*@GraphQLMutation(name = "adjustQuantity")
	Boolean adjustQuantity(
			@GraphQLArgument(name = "billingItemId") UUID billingItemId,
			@GraphQLArgument(name = "quantityDeduct") Integer quantityDeduct
	) {
		def bi = findOne(billingItemId)

		bi.qty -= quantityDeduct
		save(bi)

		true
	}*/

	@GraphQLMutation(name = "deleteSupportingDoc")
	Boolean deleteSupportingDoc(
			@GraphQLArgument(name = "billingItemId") UUID billingItemId,
			@GraphQLArgument(name = "supportingDocId") UUID supportingDocId
	) {

		def bi = findOne(billingItemId)

		def match = bi.supportingDocs.find {
			it.id == supportingDocId
		}

		if (match)
			bi.supportingDocs.remove(match)

		save(bi)

		true
	}

	@GraphQLMutation(name = "addSupportingDoc")
	SupportingDoc addSupportingDoc(
			@GraphQLArgument(name = "billingItemId") UUID billingItemId,
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "attachment") MultipartFile attachment
	) {

		def bi = findOne(billingItemId)
		def supportingDoc = new SupportingDoc()
		supportingDoc.billingItem = bi
		updateFromMap(supportingDoc, fields)

		if (attachment && !attachment.empty) {
			supportingDoc.filename = attachment.originalFilename
			supportingDoc.attachment = ArrayUtils.toObject(IOUtils.toByteArray(attachment.inputStream))
		}

		bi.supportingDocs.add(supportingDoc)
		save(bi)

		supportingDoc

	}

	@GraphQLMutation
	BillingItem toggleBillingItem(@GraphQLArgument(name = "billingItemId") String billingItemId) {

		def billingItem = findOne(UUID.fromString(billingItemId))

		if (billingItem.status == BillingItemStatus.CANCELED)
			billingItem.status = BillingItemStatus.ACTIVE
		else
			billingItem.status = BillingItemStatus.CANCELED

		save(billingItem)

	}

	@GraphQLMutation
	GraphQLRetVal<String> addPFBillingItem(@GraphQLArgument(name = "billingId") UUID billingId,
	                                       @GraphQLArgument(name = "fields") Map<String, Object> fields) {

		// not handling Exception
		addBillingItem(billingId, BillingItemType.PF, [fields])

		return new GraphQLRetVal<String>("OK", true)
	}

	@GraphQLMutation
	GraphQLRetVal<String> addBillingItem(@GraphQLArgument(name = "billingId") UUID billingId,
	                                     @GraphQLArgument(name = "billingItemType") String billingItemType,
	                                     @GraphQLArgument(name = "billingItems") List<Map<String, Object>> billingItemList) {

		try {
			addBillingItem(billingId, BillingItemType.valueOf(billingItemType), billingItemList)
		} catch (BillingException billingException) {

			return new GraphQLRetVal<String>("ERROR", false, billingException.message)
		}

		return new GraphQLRetVal<String>("OK", true)
	}

	@GraphQLMutation
	GraphQLRetVal<String> addBillingItemFromOrderSlipItems(@GraphQLArgument(name = "caseId") UUID caseId,
	                                                       @GraphQLArgument(name = "orderListItems") List<UUID> orderListItems) {

		try {
			addBillingItem(caseId, orderListItems)
		} catch (BillingException billingException) {
			return new GraphQLRetVal<String>("ERROR", false, billingException.message)
		}

		return new GraphQLRetVal<String>("OK", true)
	}

	@GraphQLMutation
	BillingOperationResult cancelBillingItem(@GraphQLArgument(name = "billingItemId") UUID billingItemId,
	                                         @GraphQLArgument(name = "details") Map<String, String> details) {
		_cancelBillingItem(billingItemId, details)
	}

	@GraphQLQuery(name = "getItemReturnInfo")
	ReturnItemInfo getItemReturnInfo(
			@GraphQLArgument(name = "billingItemId") UUID billingItemId
	) {

		def target = findOne(billingItemId)

		def sumQuantity = getCount("Select sum(b.qty) from BillingItem b where b.billing=:billing and b.canceledref=:canceledref and b.status='ACTIVE'",
				[
						billing    : target.billing,
						canceledref: target.id

				] as Map<String, Object>)

		long remaining = target.qty - (sumQuantity ?: 0)

		return new ReturnItemInfo(target.id, target.description, remaining.toInteger())

	}

	@GraphQLMutation
	Boolean transferItem(@GraphQLArgument(name = "billingIdTarget") UUID billingIdTarget,
									   @GraphQLArgument(name = "itemsId") List<UUID> itemsId) {

		def billing = billingService.getBillingById(billingIdTarget)

		def items = billingItemByIds(itemsId)

		 items.each {
			  it.billing = billing
			 save(it)

			 // check for payments
			 if(it.itemType == BillingItemType.PAYMENTS){

			   def paymentTrackerId = 	 it.details[BillingItemDetailParam.PAYTRACKER_ID.name()]
				   if(paymentTrackerId){
					   def paymentTracker = paymentTrackerServices.findOne(UUID.fromString(paymentTrackerId))
					   if(paymentTracker){

						   paymentTracker.billingid = billing.id

						   paymentTrackerServices.save(paymentTracker)
					   }
				   }
			 }
		 }


		true
	}
	@GraphQLMutation
	BillingOperationResult returnItems(@GraphQLArgument(name = "billingItemId") UUID billingItemId,
	                                   @GraphQLArgument(name = "countReturned") Integer countReturned) {
		//,@GraphQLArgument(name = "details") Map<String, String> details
		_returnItems(billingItemId, countReturned, [:])
	}

	BillingOperationResult _returnItems(UUID billingItemId,
	                                    int countReturned,
	                                    Map<String, String> details) {

		def target = findOne(billingItemId)

		// existing ACTIVE Returned
		def sumQuantity = getCount("Select sum(b.qty) from BillingItem b where b.billing=:billing and b.canceledref=:canceledref and b.status='ACTIVE'",
				[
						billing    : target.billing,
						canceledref: target.id

				] as Map<String, Object>)

		def remaining = target.qty - (sumQuantity ?: 0)
		def checkExcess = remaining - countReturned

		if (checkExcess < 0) {
			return new BillingOperationResult().tap {
				it.operationOk = false
				it.message = "Item returned [${countReturned}] is greater than remaining [${remaining}]"
			}
		}

		def itemId = target.details.getOrDefault(BillingItemDetailParam.ITEMID.name(), "")

		if (!itemId) {
			return new BillingOperationResult().tap {
				it.operationOk = false
				it.message = "Cannot Return. ITEMID cannot be identified"
			}

		}

		Map<String, Object> detail = [:]
		List<Map<String, Object>> params = []

		detail["returnedBillingItemId"] = target.id.toString()
		detail["itemId"] = itemId
		detail["quantity"] = countReturned

		params << detail

		// returns just ignore

		try {

			addBillingItem(
					target.billing.id,
					target.itemType,
					params
			)
		} catch (BillingException billingException) {

			return new BillingOperationResult().tap {
				it.operationOk = true
				it.message = billingException.message
			}
		}

		return new BillingOperationResult().tap {
			it.operationOk = true
			it.message = "Item successully added"
		}

	}

	BillingOperationResult _cancelBillingItem(UUID billingItemId,
	                                          Map<String, String> details, boolean repricing = false) {

		// load target billing Item
		def target = findOne(billingItemId)

		if (target.status == BillingItemStatus.CANCELED) {
			return new BillingOperationResult().tap {
				it.operationOk = false
				it.message = "Cannot canceled an already [Canceled] Item"
			}

		}

		// Cannot cancel an item with active returns
		// canceledref

		if (!repricing) {

			def count = getCount("Select count(b) from BillingItem b where b.billing=:billing and b.canceledref=:canceledref and b.status='ACTIVE'",
					[
							billing    : target.billing,
							canceledref: target.id

					] as Map<String, Object>)

			if (count > 0) {
				return new BillingOperationResult().tap {
					it.operationOk = false
					it.message = "Cannot canceled a Billing Item with [Return Item] records. If you want to cancel this item, please Cancel  all dependent records or Just Return all remaining Items"
				}
			}
			if (target.itemType == BillingItemType.MEDICINES ||
					target.itemType == BillingItemType.SUPPLIES) {

				String itemid = null

				if (target.details.containsKey(BillingItemDetailParam.INVENTORYID.name())) {
					def inventoryId = target.details[BillingItemDetailParam.INVENTORYID.name()].toString()
					def inventory = inventoryRepository.findById(UUID.fromString(inventoryId)).get()

					if (inventory)
						itemid = inventory.item.id.toString()

				} else {
					itemid = target.details[BillingItemDetailParam.ITEMID.name()].toString()
				}

				// what if canceled is a return?

				if (itemid) {
					inventoryLedgService.InventoryCharge(
							target.department.id,
							UUID.fromString(itemid),
							target.recordNo,
							target.canceledref ? "cs" : "rcs",
							target.qty,
							target.billing.id,
							target.id)
				}

			}
			if (target.itemType == BillingItemType.DIAGNOSTICS) {
				// look for orderslip reference
				def items = orderSlipItemRepository.getByBillingItem(target)

				boolean cannotDelete = false
				items.each {
					if (it.status == "COMPLETED")
						cannotDelete = true
				}

				if (cannotDelete) {
					return new BillingOperationResult().tap {
						it.operationOk = false
						it.message = "Cannot canceled a Billing Item with Diagnostic Procedure already done. Please remove [COMPLETED] status first"
					}
				}

				items.each {

					it.posted = false
					it.billing_item = null

					orderSlipItemRepository.save(it)
				}
			}
			if (target.itemType == BillingItemType.CATHLAB) {
				// look for orderslip reference
				def items = orderSlipItemRepository.getByBillingItem(target)

				boolean cannotDelete = false
				items.each {
					if (it.status == "COMPLETED")
						cannotDelete = true
				}

				if (cannotDelete) {
					return new BillingOperationResult().tap {
						it.operationOk = false
						it.message = "Cannot canceled a Billing Item with Diagnostic Procedure already done. Please remove [COMPLETED] status first"
					}
				}

				items.each {

					it.posted = false
					it.billing_item = null
					//cancel ref package
					orderSlipItemPackageContentService.updateBillingPackageList(it.id, target.recordNo)

					orderSlipItemRepository.save(it)
				}
			}
		}

		target.status = BillingItemStatus.CANCELED

		details.each { t, u ->
			target.details[t] = u
		}

		target.tempCanceled = true
		save(target)

		//TODO: Accounting Entry reversal

		//=====================

		return new BillingOperationResult().tap {
			it.operationOk = true
			it.message = "OK"
			it.entityIdRef = billingItemId
		}
	}

	// From Package
	def addBillingItem(UUID caseId, List<UUID> uuids, Package aPackage = null) throws BillingException {
		def username = SecurityUtils.currentLogin()
		def user = userRepository.findOneByLogin(username)
		def emp = employeeRepository.findOneByUser(user)
		def department = emp.departmentOfDuty

		def patientCase = caseRepository.findById(caseId).get()
		def billing = billingService.getActiveBillingByCase(patientCase).find()
		//  New Active Billing auto from Ancillary
		if (!billing) {
			billing = billingService.createBilling(patientCase.patient.id, patientCase.id)
		}

		if (billing.locked) {
			throw new BillingException("Billing Folio is Locked")
		}

		Boolean hasLifeSupportService = false;
		Boolean creditLimitReached = false;
		List<OrderSlipItem> orderSlipItemsCredit = []
		List<OrderSlipItem> orderSlipItemsCash = []
		uuids.each { UUID oid ->
			def orderSlipItem = orderSlipItemRepository.findById(oid).get()
			if(hasLifeSupportService==false)
				hasLifeSupportService = orderSlipItem.service?.isLifeSupport?true:false


			if (orderSlipItem.billing_item == null ){
				if (orderSlipItem.transaction_type == "CASH")
					orderSlipItemsCash << orderSlipItem
				else
					orderSlipItemsCredit << orderSlipItem
			}
		}

		boolean creditLimit = false;

		if(billing.patientCase.registryType == "IPD" || billing.patientCase.registryType == "ERD") {
			creditLimit = billingService.isCreditLimitReached(billing)

			if (orderSlipItemsCash.size() == 0) {
				if (creditLimit) {
					creditLimitReached = true;
					if(hasLifeSupportService==false)
							throw new BillingException("Billing Folio credit is reached")
				}
			}
		}

		List<OrderSlipItem> forProcessing = []

		if (!creditLimit) {
			orderSlipItemsCredit.each {
				if (!it.posted)
					forProcessing << it
			}
		}

		if(creditLimitReached && hasLifeSupportService)
		{
			orderSlipItemsCredit.each {
				if (!it.posted && it.service.isLifeSupport)
					forProcessing << it
			}
		}

		orderSlipItemsCash.each {
			if (!it.posted)
				forProcessing << it
		}

		PriceTierDetail pricingTier = null

		// priority Manual
		if (billing.pricetiermanual)
			pricingTier = priceTierDetailRepository.findById(billing.pricetiermanual).get()

		if (!pricingTier) {
			pricingTier = priceTierDetailDao.getDetail(caseId)
		}

		if (billing && !billing.locked) {

			forProcessing.each { orderSlipItem ->

				def service = orderSlipItem.service
				if((creditLimitReached && hasLifeSupportService) || creditLimitReached==false)
				{
					def billingItemDto = new BillingItem()

					if (aPackage)
						billingItemDto.apackage = aPackage
					billingItemDto.debit = 0.0
					billingItemDto.credit = 0.0
					billingItemDto.forPosting = true

					if (pricingTier)
						billingItemDto.priceTierDetail = pricingTier

					billingItemDto.billing = billing

					if(service.revenueToUser){
						billingItemDto.department = department
					}else {
						billingItemDto.department = service?.department?:department
					}
					def group = service?.department?.groupCategory ?: ""
					if(group.equalsIgnoreCase("CATHLAB")){
						billingItemDto.itemType = BillingItemType.CATHLAB
					}else if (group.equalsIgnoreCase("OPERATING_ROOM")){
						billingItemDto.itemType = BillingItemType.ORFEE
					}else if(group.equalsIgnoreCase("OTHERS")){
						billingItemDto.itemType = BillingItemType.OTHERS
					}else{
						billingItemDto.itemType = BillingItemType.DIAGNOSTICS
					}
					billingItemDto.recordNo = generatorService.getNextValue(GeneratorType.BILLING_RECORD_NO) { Long next ->
						StringUtils.leftPad(next.toString(), 5, '0')
					}
					billingItemDto.status = BillingItemStatus.ACTIVE

					billingItemDto.qty = 1
					if (pricingTier)
						billingItemDto.debit = priceTierDetailDao.getServicePrice(pricingTier.id, service.id)

					billingItemDto.description = "${service.serviceName}" // [#${orderSlipItem.itemNo}]
					billingItemDto.item = orderSlipItem.itemNo
					billingItemDto.details[BillingItemDetailParam.SERVICEID.name()] = service.id.toString()
					billingItemDto.details[BillingItemDetailParam.ORDERSLIPITEM.name()] = orderSlipItem.id.toString()


					billingItemDto = save(billingItemDto)

					//Add Reader's Fee
					if(orderSlipItem.reader){
						List<RfFees> rfFees = rfFeesRepository.searchMatch(orderSlipItem.service.id,orderSlipItem.reader.id)
						if(rfFees.size() > 0){
							billingItemDto.rfFee = billingItemDto.debit * (rfFees[0].rfPercentage / 100)

							def o = new RfDetails()
							o.rfTableId = rfFees[0].id
							o.doctorsId = orderSlipItem.reader.id
							o.serviceId = orderSlipItem.service.id
							o.percentage = rfFees[0].rfPercentage
							billingItemDto.rfDetails = new JsonBuilder( o ).toString()
						}
						else{
//							def percentage = (orderSlipItem.service.readersFee /orderSlipItem.service.basePrice)
							billingItemDto.rfFee = orderSlipItem.service.readersFee

							def o = new RfDetails()
							o.doctorsId = orderSlipItem.reader.id
							o.serviceId = orderSlipItem.service.id
							billingItemDto.rfDetails = new JsonBuilder( o ).toString()
						}
						billingItemDto = save(billingItemDto)
					}else{

						billingItemDto.rfFee = orderSlipItem.service.readersFee

						def o = new RfDetails()

					o.serviceId = orderSlipItem.service.id
					try{
						o.percentage = (orderSlipItem.service.readersFee / orderSlipItem.service.basePrice) *100
						billingItemDto.rfDetails = new JsonBuilder( o ).toString()
					}
					catch (Exception e)
					{
						e.printStackTrace()
					}


						billingItemDto = save(billingItemDto)
					}

					orderSlipItem.posted = true
					orderSlipItem.billing_item = billingItemDto

					orderSlipItemRepository.save(orderSlipItem)


					// Services with inventory items
					if(orderSlipItem.service.serviceType == ServiceTypes.PACKAGE){

						List<OrderSlipItemPackageContent> itemsForCharge =  orderSlipItemPackageContentService.orderSlipItemPackageByParent(orderSlipItem.id)
						itemsForCharge.each{
							packItem ->
								//update packageItem to reference billing item
								orderSlipItemPackageContentService.updateBillingPackageItem(packItem, billingItemDto.id)
								//add to stock card
								inventoryLedgService.InventoryCharge(
										packItem.department.id,
										packItem.item.id,
										billingItemDto.recordNo,
										"dm",
										packItem.qty,
										billingItemDto.billing.id,
										billingItemDto.id)
						}

					}
				}
			}
			//if(creditLimitReached && hasLifeSupportService)
			//	throw new BillingException("Billing Folio credit is reached. Life support services billed.")

		}

	}

	def addAnnotations(UUID billingId,
	    BillingItemType annotationType,
		String description,
		BigDecimal amountValue
	){

		if(annotationType in [BillingItemType.ANNOTATION_PAYMENTS_GROUPS,
							  BillingItemType.ANNOTATION_NOTIFICATION_GROUPS
		]){
			def username = SecurityUtils.currentLogin()
			def user = userRepository.findOneByLogin(username)
			def emp = employeeRepository.findOneByUser(user)
			def department = emp.departmentOfDuty

			def billing = billingService.findOne(billingId)

			def billingItem = new BillingItem()
			billingItem.description = description
			billingItem.debit = 0.0
			billingItem.credit = 0.0
			billingItem.annotationAmount = amountValue
			billingItem.forPosting = false

			billingItem.billing = billing
			billingItem.department = department
			billingItem.itemType = annotationType
			billingItem.recordNo = generatorService.getNextValue(GeneratorType.BILLING_RECORD_NO) { Long next ->
				StringUtils.leftPad(next.toString(), 5, '0')
			}
			billingItem.status = BillingItemStatus.ACTIVE
			billingItem.qty = 1
			billingItem.details[BillingItemDetailParam.ANNOTATIONS.name()] = "YES"

			save(billingItem)
		}

	}

	@GraphQLQuery(name = "expandDetails")
	List<Map<String,Object>> expandDetails (@GraphQLContext com.hisd3.hismk2.domain.billing.CashBasisItem cashBasisItem) {

		List<Map<String,String>> results = []
		BigDecimal totalAmount = 0.00
		Billing billing = cashBasisItem.billing
		PriceTierDetail pricingTier = null

		// priority manual override
		if (billing.pricetiermanual)
			pricingTier = priceTierDetailRepository.findById(billing.pricetiermanual).get()

		if (!pricingTier) {
			if (billing.patientCase) {
				pricingTier = priceTierDetailDao.getDetail(billing.patientCase.id)

			}
		}

		def jsonSlurper = new JsonSlurper()
		def data =(List<Map<String,Object>>) jsonSlurper.parseText(cashBasisItem.data)     // List<Map<String,String>>


		data.forEach {

			Map<String,String> expand = [:]
			def inventoryId = it.inventoryId as String
			def index = it.index
			def quantity = NumberUtils.toInt(it.quantity,0)
			Item item = null
			DepartmentItem inventory = departmentItemRepository.findById(UUID.fromString(inventoryId)).get()
			item = inventory?.item

			def unitPrice = priceTierDetailDao.getItemPrice(pricingTier.id, item.id)
			def subTotal = unitPrice * quantity
			totalAmount += subTotal

			expand.index = index
			expand.quantity = quantity
			expand.unitPrice = unitPrice
			expand.inventoryId = inventoryId
			expand.itemName =  "[${item.itemCode}] ${item.descLong}".toString()
			expand.subTotal = subTotal

			results <<  expand
		}

		results
	}


	@GraphQLMutation
	GraphQLRetVal<String> addBillingItemCashBasis(@GraphQLArgument(name = "billingId") UUID billingId,
												   @GraphQLArgument(name = "billingItemType") String billingItemType,
												   @GraphQLArgument(name = "dataInput")  String dataInput){

		def billing = billingService.findOne(billingId)
		def username = SecurityUtils.currentLogin()
		def user = userRepository.findOneByLogin(username)
		def emp = employeeRepository.findOneByUser(user)
		def department = emp.departmentOfDuty
		def cashBasis = new com.hisd3.hismk2.domain.billing.CashBasisItem()


		cashBasis.billing = billing
		cashBasis.departmentId = department.id
		cashBasis.type = billingItemType
		cashBasis.data = dataInput
		cashBasis.code = generatorService.getNextValue(GeneratorType.CASHBASIS){
			return StringUtils.leftPad(it + "",5,"0")
		}


		billing.cashBasisItems.add(cashBasis)
		billingService.save(billing)

		return  new GraphQLRetVal<String>().tap {
			it.success = true
			it.message =  cashBasis.code
		}

	}

	// generic
	def addBillingItem(UUID billingId,
	                   BillingItemType billingItemType,
	                   List<Map<String, Object>> dataInput,
	                   Package aPackage = null,
			           Department departmentOverride = null, // Meds and Supplies only
			           String userOverride = null, // Meds and Supplies only
					   boolean bypassCreditLimit = false // Bypass by patient medication (Wilson update)
	) throws BillingException {

		def billing = billingService.findOne(billingId)

		if (billing.locked) {
			throw new BillingException("Billing Folio is Locked")
		}
        boolean creditLimitReached = false
        List<Map<String, Object>> lifeSupportDataInput = new ArrayList<>()
		List<Map<String, Object>> returnedDataInput = new ArrayList<>()
        //check if lifesupport items on charging


		if(userOverride == null){
			if (billingItemType != BillingItemType.PF && billingItemType != BillingItemType.OXYGEN) {
				if (billingService.isCreditLimitReached(billing)) {
                    creditLimitReached = true
                    dataInput.each {
                        fields ->
                            String serviceId = fields.getOrDefault("serviceid",null)
							def returnedBillingItemId = fields.getOrDefault("returnedBillingItemId", "") as String
                            if(serviceId!=null && serviceRepository.getOne(UUID.fromString(serviceId)).isLifeSupport)
                            {
                                lifeSupportDataInput.add(fields)
                            }
							//add condition for return supplies and medicines
							if(returnedBillingItemId){
								returnedDataInput.add(fields)
							}
                    }
					def userTest = SecurityUtils.currentLogin()
					//add condition for return supplies and medicines
					if (!StringUtils.equalsIgnoreCase(userTest, "scheduler") && lifeSupportDataInput.size()<1 && returnedDataInput.size()<1 && !bypassCreditLimit) {
						// scheduler user is exempted
						throw new BillingException("Billing Folio credit is reached")
					}
				}
			}
		}


		PriceTierDetail pricingTier = null

		// priority manual override
		if (billing.pricetiermanual)
			pricingTier = priceTierDetailRepository.findById(billing.pricetiermanual).get()

		if (!pricingTier) {
			if (billing.patientCase) {
				pricingTier = priceTierDetailDao.getDetail(billing.patientCase.id)

			}
		}

		def username = SecurityUtils.currentLogin()
		def user = userRepository.findOneByLogin(username)
		def emp = employeeRepository.findOneByUser(user)
		def department = departmentOverride ?: emp.departmentOfDuty

		if (billingItemType == BillingItemType.PF) {

			dataInput.each {
				fields ->

					def billingItem = new BillingItem()
					billingItem.debit = 0.0
					billingItem.credit = 0.0
					billingItem.forPosting = false
					/*if (pricingTier)
						billingItem.priceTierDetail = pricingTier*/
					billingItem.billing = billing
					billingItem.department = department
					billingItem.itemType = BillingItemType.PF
					billingItem.recordNo = generatorService.getNextValue(GeneratorType.BILLING_RECORD_NO) { Long next ->
						StringUtils.leftPad(next.toString(), 5, '0')
					}
					billingItem.status = BillingItemStatus.ACTIVE

					def net = fields.getOrDefault("net", "") as String
					def wtx = fields.getOrDefault("wtx", "") as String
					def vat = fields.getOrDefault("vat", "") as String
					def employeeid = fields.getOrDefault("employeeid", "") as String

					def doctor = employeeRepository.findById(UUID.fromString(employeeid)).get()

					billingItem.qty = 1

					def netAmt = new BigDecimal(net)
					def wtxDecimal = new BigDecimal(wtx)
					def vatDecimal = new BigDecimal(vat)

					def tmp = netAmt
					def wtxAmt = BigDecimal.ZERO
					def vatAmt = BigDecimal.ZERO

					if (wtxDecimal > BigDecimal.ZERO) {
						tmp = tmp / (BigDecimal.ONE - wtxDecimal).setScale(2, RoundingMode.HALF_EVEN)
						wtxAmt = (tmp - netAmt).setScale(2, RoundingMode.HALF_EVEN)
					}

					if (vatDecimal > BigDecimal.ZERO) {
							def orig = tmp
							tmp = tmp * (BigDecimal.ONE + vatDecimal)
							vatAmt = (tmp - orig).setScale(2, RoundingMode.HALF_EVEN)
						}


					def vatExempt = fields.getOrDefault("VAT_EXEMPT","").toString()

					if(StringUtils.equalsIgnoreCase(vatExempt,"YES")){
						billingItem.debit = (netAmt + wtxAmt).setScale(2, RoundingMode.HALF_EVEN)
						billingItem.details[BillingItemDetailParam.PF_VAT_APPLIED.name()] = "NO"
					}
					else {
						billingItem.debit = (netAmt + wtxAmt + vatAmt).setScale(2, RoundingMode.HALF_EVEN)
						billingItem.details[BillingItemDetailParam.PF_VAT_APPLIED.name()] = "YES"

					}



					def nonVat = vatAmt <= 0
					billingItem.description = "PF ${nonVat ? "NV" : ""} [${doctor?.fullName}]"

					billingItem.details[BillingItemDetailParam.PF_NET.name()] = net
					billingItem.details[BillingItemDetailParam.PF_WTX_RATE.name()] = wtx
					billingItem.details[BillingItemDetailParam.PF_WTX_AMT.name()] = wtxAmt.toPlainString()
					billingItem.details[BillingItemDetailParam.PF_VAT_RATE.name()] = vat
					billingItem.details[BillingItemDetailParam.PF_VAT_AMT.name()] = vatAmt.toPlainString()
					billingItem.details[BillingItemDetailParam.PF_EMPLOYEEID.name()] = employeeid

					save(billingItem)
					//TODO: Accounting Entry

			}

		}

		if (billingItemType == BillingItemType.ROOMBOARD) {

			// roomno , bedno, price, quantity, description
			dataInput.each {

				def billingItemDto = new BillingItem()
				billingItemDto.debit = 0.0
				billingItemDto.credit = 0.0
				billingItemDto.forPosting = true
				if (pricingTier)
					billingItemDto.priceTierDetail = pricingTier
				billingItemDto.billing = billing
				billingItemDto.department = department
				billingItemDto.itemType = billingItemType
				billingItemDto.recordNo = generatorService.getNextValue(GeneratorType.BILLING_RECORD_NO) { Long next ->
					StringUtils.leftPad(next.toString(), 5, '0')
				}
				billingItemDto.status = BillingItemStatus.ACTIVE

				def roomDeptId = it.getOrDefault("roomDeptId", "") as String
				def roomno = it.getOrDefault("roomno", "") as String
				def bedno = it.getOrDefault("bedno", "") as String
				def price = it.getOrDefault("price", BigDecimal.ZERO) as BigDecimal
				def quantity = it.getOrDefault("quantity", 0) as Integer
				def description = it.getOrDefault("description", "") as String


				if(StringUtils.isNotBlank(roomDeptId))
					{
						billingItemDto.roomAndBoard = departmentRepository.findById(UUID.fromString(roomDeptId)).get()
						billingItemDto.department = billingItemDto.roomAndBoard
					}


				def roomInMultiplier = hospitalConfigService.operationalConfig?.roomInDeduction?:0.0

				def deductRoomIn = 0.0
				def roomInStr = ""

				if(billing?.patientCase?.roomIn){
					deductRoomIn = (price * roomInMultiplier).setScale(2, RoundingMode.HALF_EVEN)
					roomInStr = "RM-IN"
				}


				billingItemDto.qty = quantity
				billingItemDto.debit = price - deductRoomIn
				billingItemDto.description = "ROOM & BOARD [${roomno}:${bedno}]-${description} ${roomInStr}"

				billingItemDto.details[BillingItemDetailParam.ROOMNO.name()] = roomno
				billingItemDto.details[BillingItemDetailParam.BEDNO.name()] = bedno

				save(billingItemDto)

				//TODO: Accounting Entry

			}

		}

		if (billingItemType == BillingItemType.ORFEE || billingItemType == BillingItemType.OTHERS) {

			// serviceid, quantity

            for (it in dataInput) {
                def quantity = it.getOrDefault("quantity", 0) as Integer
                def serviceid = it.getOrDefault("serviceid", 0) as String

                def service = serviceRepository.findById(UUID.fromString(serviceid)).get()

                if (creditLimitReached && (service.isLifeSupport == null || service.isLifeSupport == false))
                    break;
                def billingItemDto = new BillingItem()
                billingItemDto.debit = 0.0
                billingItemDto.credit = 0.0
                billingItemDto.forPosting = true
                if (pricingTier)
                    billingItemDto.priceTierDetail = pricingTier
                billingItemDto.billing = billing


                if (service.revenueToUser) {
                    billingItemDto.department = department
                } else {
                    billingItemDto.department = service?.department ?: department
                }


                billingItemDto.itemType = billingItemType
                billingItemDto.recordNo = generatorService.getNextValue(GeneratorType.BILLING_RECORD_NO) { Long next ->
                    StringUtils.leftPad(next.toString(), 5, '0')
                }
                billingItemDto.status = BillingItemStatus.ACTIVE

                billingItemDto.qty = quantity
                if (pricingTier)
                    billingItemDto.debit = priceTierDetailDao.getServicePrice(pricingTier.id, service.id)

                billingItemDto.description = service.serviceName

                billingItemDto.details[BillingItemDetailParam.SERVICEID.name()] = service.id.toString()

                save(billingItemDto)

                //TODO: Accounting Entry

            }

		}

		if (billingItemType == BillingItemType.MEDICINES) {

			// itemId, quantity
			dataInput.each {

				def quantity = it.getOrDefault("quantity", 0) as Integer
				def itemId = it.getOrDefault("itemId", "") as String
				def targetDepartment = it.getOrDefault("targetDepartment", "") as String
				def returnedBillingItemId = it.getOrDefault("returnedBillingItemId", "") as String
				def cashBasisItemId = it.getOrDefault("cashBasisItemId",null) as UUID

				Department tdept = department
				if (targetDepartment) {
					tdept = departmentRepository.findById(UUID.fromString(targetDepartment)).get()

				}

				Item item = null

				if (itemId) {
					item = itemRepository.findById(UUID.fromString(itemId)).get()
				}

				Inventory inventory = null
				if (!item) {
					// From Billing
					def inventoryId = it.getOrDefault("inventoryId", "") as String
					inventory = inventoryRepository.findById(UUID.fromString(inventoryId)).get()

					item = inventory?.item

				}

				def billingItemDto = new BillingItem()

				if(userOverride)
					billingItemDto.cashBasisUser = userOverride
				if (aPackage)
					billingItemDto.apackage = aPackage

				billingItemDto.debit = 0.0
				billingItemDto.credit = 0.0
				billingItemDto.forPosting = true
				if (pricingTier)
					billingItemDto.priceTierDetail = pricingTier
				billingItemDto.billing = billing
				billingItemDto.department = tdept
				billingItemDto.itemType = billingItemType
				billingItemDto.recordNo = generatorService.getNextValue(GeneratorType.BILLING_RECORD_NO) { Long next ->
					StringUtils.leftPad(next.toString(), 5, '0')
				}
				billingItemDto.status = BillingItemStatus.ACTIVE

				billingItemDto.qty = quantity

				if (!returnedBillingItemId) {
					if (pricingTier)
						billingItemDto.debit = priceTierDetailDao.getItemPrice(pricingTier.id, item.id)
					billingItemDto.description = "[${item.itemCode}] ${item.descLong}"
				} else {
					def returnedBilling = findOne(UUID.fromString(returnedBillingItemId))
					billingItemDto.credit = returnedBilling.debit
					billingItemDto.description = "RETURNED ${returnedBilling.recordNo} - ${returnedBilling.description}"
					billingItemDto.canceledref = returnedBilling.id
					billingItemDto.cogsPerItem = returnedBilling.cogsPerItem
				}

				billingItemDto.details[BillingItemDetailParam.ITEMID.name()] = item.id.toString()

				if (inventory)
					billingItemDto.details[BillingItemDetailParam.INVENTORYID.name()] = inventory.id.toString()
				billingItemDto = save(billingItemDto)
				if(cashBasisItemId!=null)
				{
					CashBasisItem cashBasisItem = cashBasisItemRepository.getOne(cashBasisItemId)

				}
				// Inventory entries
				//$type data ("cs" or "rcs") cs = "Charge Slip"; rcs = "Reverse Charge Slip"

				// if false .. inventory deduction is during preparation

				// gas is ignored in code
				if (inventory || billingItemDto.canceledref) // from Billing Module
					inventoryLedgService.InventoryCharge(
							tdept.id,
							item.id,
							billingItemDto.recordNo,
							billingItemDto.canceledref ? "rcs" : "cs",
							quantity,
							billingItemDto.billing.id,
							billingItemDto.id)

				//TODO: Accounting Entry

			}

		}
		if (billingItemType == BillingItemType.OXYGEN) {

			// itemId, quantity
			dataInput.each {

				def quantity = it.getOrDefault("quantity", 0) as Integer
				def totalAmount = it.getOrDefault("totalAmount", 0) as BigDecimal
				def flowRate = it.getOrDefault("flowrate", null) as FlowRate
				def itemId = it.getOrDefault("itemId", "") as String
				def targetDepartment = it.getOrDefault("targetDepartment", "") as String
				def returnedBillingItemId = it.getOrDefault("returnedBillingItemId", "") as String
				def totalHours = it.getOrDefault("totalHours", "") as BigDecimal

				Department tdept = department
				if (targetDepartment) {
					tdept = departmentRepository.findById(UUID.fromString(targetDepartment)).get()

				}

				Item item = null

				if (itemId) {
					item = itemRepository.findById(UUID.fromString(itemId)).get()
				}

				Inventory inventory = null
				if (!item) {
					// From Billing
					def inventoryId = it.getOrDefault("inventoryId", "") as String
					inventory = inventoryRepository.findById(UUID.fromString(inventoryId)).get()

					item = inventory?.item

				}

				def billingItemDto = new BillingItem()
				if (aPackage)
					billingItemDto.apackage = aPackage

				billingItemDto.debit = 0.0
				billingItemDto.credit = 0.0
				billingItemDto.forPosting = true

				billingItemDto.billing = billing
				//oxygen based on room
				billingItemDto.department = billing?.patientCase?.room?.department?:tdept
				billingItemDto.roomAndBoard = billingItemDto.department



				billingItemDto.itemType = BillingItemType.SUPPLIES
				billingItemDto.recordNo = generatorService.getNextValue(GeneratorType.BILLING_RECORD_NO) { Long next ->
					StringUtils.leftPad(next.toString(), 5, '0')
				}
				billingItemDto.status = BillingItemStatus.ACTIVE

				billingItemDto.qty = quantity

				if (!returnedBillingItemId) {
					billingItemDto.debit = totalAmount?:0.0
					billingItemDto.description = "[${item.itemCode}] ${item.descLong}(${flowRate.description}) ${totalHours}hours@${flowRate.pricePerHour}/hr"
				} else {
					def returnedBilling = findOne(UUID.fromString(returnedBillingItemId))
					billingItemDto.credit = returnedBilling.debit
					billingItemDto.description = "RETURNED ${returnedBilling.recordNo} - ${returnedBilling.description}"
					billingItemDto.canceledref = returnedBilling.id
					billingItemDto.cogsPerItem = returnedBilling.cogsPerItem
				}

				billingItemDto.details[BillingItemDetailParam.ITEMID.name()] = item.id.toString()

				if (inventory)
					billingItemDto.details[BillingItemDetailParam.INVENTORYID.name()] = inventory.id.toString()
				billingItemDto = save(billingItemDto)

				// Inventory entries
				//$type data ("cs" or "rcs") cs = "Charge Slip"; rcs = "Reverse Charge Slip"

				// if false .. inventory deduction is during preparation

				// gas is ignored in code
				if (inventory || billingItemDto.canceledref) // from Billing Module
					inventoryLedgService.InventoryCharge(
							tdept.id,
							item.id,
							billingItemDto.recordNo,
							billingItemDto.canceledref ? "rcs" : "cs",
							quantity,
							billingItemDto.billing.id,
							billingItemDto.id)

				//TODO: Accounting Entry

			}

		}
		if (billingItemType == BillingItemType.SUPPLIES) {

			// serviceid, quantity
			dataInput.each {

				def quantity = it.getOrDefault("quantity", 0) as Integer
				def inventoryId = it.getOrDefault("inventoryId", "") as String
				def itemId = it.getOrDefault("itemId", "") as String
				def returnedBillingItemId = it.getOrDefault("returnedBillingItemId", "") as String

				Item item = null
				Inventory inventory = null
				if (itemId) {
					item = itemRepository.findById(UUID.fromString(itemId)).get()

				}

				if (inventoryId) {
					inventory = inventoryRepository.findById(UUID.fromString(inventoryId)).get()
					if (!item) {
						item = inventory.item
					}
				}

				if (!returnedBillingItemId) {
					if (BooleanUtils.isNotTrue(inventory?.item?.gas)) {
						//if (quantity > inventory.onHand)
						//	throw new Exception("Quantity is greater than onHand")
					}
				}

				def billingItemDto = new BillingItem()

				if(userOverride)
					billingItemDto.cashBasisUser = userOverride

				if (aPackage)
					billingItemDto.apackage = aPackage

				billingItemDto.debit = 0.0
				billingItemDto.credit = 0.0
				billingItemDto.forPosting = true
				if (pricingTier)
					billingItemDto.priceTierDetail = pricingTier
				billingItemDto.billing = billing
				billingItemDto.department = department
				billingItemDto.itemType = billingItemType
				billingItemDto.recordNo = generatorService.getNextValue(GeneratorType.BILLING_RECORD_NO) { Long next ->
					StringUtils.leftPad(next.toString(), 5, '0')
				}
				billingItemDto.status = BillingItemStatus.ACTIVE

				billingItemDto.qty = quantity

				if (!returnedBillingItemId) {
					if (pricingTier)
						billingItemDto.debit = priceTierDetailDao.getItemPrice(pricingTier.id, inventory.item.id)
					billingItemDto.description = "[${item.itemCode}] ${item.descLong}"
				} else {
					def returnedBilling = findOne(UUID.fromString(returnedBillingItemId))
					billingItemDto.credit = returnedBilling.debit
					billingItemDto.description = "RETURNED ${returnedBilling.recordNo} - ${returnedBilling.description}"
					billingItemDto.canceledref = returnedBilling.id
					billingItemDto.cogsPerItem = returnedBilling.cogsPerItem

				}

				billingItemDto.details[BillingItemDetailParam.ITEMID.name()] = item?.id?.toString()

				if (inventory)
					billingItemDto.details[BillingItemDetailParam.INVENTORYID.name()] = inventory.id.toString()

				billingItemDto = save(billingItemDto)

				// Inventory entries
				//$type data ("cs" or "rcs") cs = "Charge Slip"; rcs = "Reverse Charge Slip"

				// gas is ignored in code
				inventoryLedgService.InventoryCharge(
						department.id,
						item.id,
						billingItemDto.recordNo,
						billingItemDto.canceledref ? "rcs" : "cs",
						quantity,
						billingItemDto.billing.id,
						billingItemDto.id)

				//TODO: Accounting Entry
			}

		}

	}


	@Deprecated // for removal
	def addBillingItemForCashBasis(UUID billingId,
	                   BillingItemType billingItemType,
	                   List<Map<String, Object>> dataInput,
	                   Package aPackage = null) throws BillingException {

		def billing = billingService.findOne(billingId)

		if (billing.locked) {
			throw new BillingException("Billing Folio is Locked")
		}



		PriceTierDetail pricingTier = null

		// priority manual override
		if (billing.pricetiermanual)
			pricingTier = priceTierDetailRepository.findById(billing.pricetiermanual).get()

		if (!pricingTier) {
			if (billing.patientCase) {
				pricingTier = priceTierDetailDao.getDetail(billing.patientCase.id)

			}
		}

		def username = SecurityUtils.currentLogin()
		def user = userRepository.findOneByLogin(username)
		def emp = employeeRepository.findOneByUser(user)
		def department = emp.departmentOfDuty

		if (billingItemType == BillingItemType.MEDICINES) {

			// itemId, quantity
			dataInput.each {

				def quantity = it.getOrDefault("quantity", 0) as Integer
				def itemId = it.getOrDefault("itemId", "") as String
				def targetDepartment = it.getOrDefault("targetDepartment", "") as String
				def returnedBillingItemId = it.getOrDefault("returnedBillingItemId", "") as String
				def cashBasisItemId = it.getOrDefault("cashBasisItemId",null) as UUID

				Department tdept = department
				if (targetDepartment) {
					tdept = departmentRepository.findById(UUID.fromString(targetDepartment)).get()

				}

				Item item = null

				if (itemId) {
					item = itemRepository.findById(UUID.fromString(itemId)).get()
				}

				Inventory inventory = null
				if (!item) {
					// From Billing
					def inventoryId = it.getOrDefault("inventoryId", "") as String
					inventory = inventoryRepository.findById(UUID.fromString(inventoryId)).get()

					item = inventory?.item

				}

				def billingItemDto = new BillingItem()
				if (aPackage)
					billingItemDto.apackage = aPackage

				billingItemDto.debit = 0.0
				billingItemDto.credit = 0.0
				billingItemDto.forPosting = true
				if (pricingTier)
					billingItemDto.priceTierDetail = pricingTier
				billingItemDto.billing = billing
				billingItemDto.department = tdept
				billingItemDto.itemType = billingItemType
				billingItemDto.recordNo = generatorService.getNextValue(GeneratorType.BILLING_RECORD_NO) { Long next ->
					StringUtils.leftPad(next.toString(), 5, '0')
				}
				billingItemDto.status = BillingItemStatus.ACTIVE

				billingItemDto.qty = quantity

				if (!returnedBillingItemId) {
					if (pricingTier)
						billingItemDto.debit = priceTierDetailDao.getItemPrice(pricingTier.id, item.id)
					billingItemDto.description = "[${item.itemCode}] ${item.descLong}"
				} else {
					def returnedBilling = findOne(UUID.fromString(returnedBillingItemId))
					billingItemDto.credit = returnedBilling.debit
					billingItemDto.description = "RETURNED ${returnedBilling.recordNo} - ${returnedBilling.description}"
					billingItemDto.canceledref = returnedBilling.id
					billingItemDto.cogsPerItem = returnedBilling.cogsPerItem
				}

				billingItemDto.details[BillingItemDetailParam.ITEMID.name()] = item.id.toString()

				if (inventory)
					billingItemDto.details[BillingItemDetailParam.INVENTORYID.name()] = inventory.id.toString()
				billingItemDto = save(billingItemDto)
				if(cashBasisItemId!=null)
				{
					def cashBasisItem = cashBasisItemRepository.getOne(cashBasisItemId)
					cashBasisItem.billingItemNo = billingItemDto.id
					cashBasisItemRepository.save(cashBasisItem)

				}
				// Inventory entries
				//$type data ("cs" or "rcs") cs = "Charge Slip"; rcs = "Reverse Charge Slip"

				// if false .. inventory deduction is during preparation

				// gas is ignored in code
				if (inventory || billingItemDto.canceledref) // from Billing Module
					inventoryLedgService.InventoryCharge(
							tdept.id,
							item.id,
							billingItemDto.recordNo,
							billingItemDto.canceledref ? "rcs" : "cs",
							quantity,
							billingItemDto.billing.id,
							billingItemDto.id)

				//TODO: Accounting Entry

			}

		}

		if (billingItemType == BillingItemType.SUPPLIES) {

			// serviceid, quantity
			dataInput.each {

				def quantity = it.getOrDefault("quantity", 0) as Integer
				def inventoryId = it.getOrDefault("inventoryId", "") as String
				def itemId = it.getOrDefault("itemId", "") as String
				def returnedBillingItemId = it.getOrDefault("returnedBillingItemId", "") as String
				def cashBasisItemId = it.getOrDefault("cashBasisItemId",null) as UUID
				Item item = null
				Inventory inventory = null
				if (itemId) {
					item = itemRepository.findById(UUID.fromString(itemId)).get()

				}

				if (inventoryId) {
					inventory = inventoryRepository.findById(UUID.fromString(inventoryId)).get()
					if (!item) {
						item = inventory.item
					}
				}

				if (!returnedBillingItemId) {
					if (BooleanUtils.isNotTrue(inventory?.item?.gas)) {
						//if (quantity > inventory.onHand)
						//	throw new Exception("Quantity is greater than onHand")
					}
				}

				def billingItemDto = new BillingItem()

				if (aPackage)
					billingItemDto.apackage = aPackage

				billingItemDto.debit = 0.0
				billingItemDto.credit = 0.0
				billingItemDto.forPosting = true
				if (pricingTier)
					billingItemDto.priceTierDetail = pricingTier
				billingItemDto.billing = billing
				billingItemDto.department = department
				billingItemDto.itemType = billingItemType
				billingItemDto.recordNo = generatorService.getNextValue(GeneratorType.BILLING_RECORD_NO) { Long next ->
					StringUtils.leftPad(next.toString(), 5, '0')
				}
				billingItemDto.status = BillingItemStatus.ACTIVE

				billingItemDto.qty = quantity

				if (!returnedBillingItemId) {
					if (pricingTier)
						billingItemDto.debit = priceTierDetailDao.getItemPrice(pricingTier.id, inventory.item.id)
					billingItemDto.description = "[${item.itemCode}] ${item.descLong}"
				} else {
					def returnedBilling = findOne(UUID.fromString(returnedBillingItemId))
					billingItemDto.credit = returnedBilling.debit
					billingItemDto.description = "RETURNED ${returnedBilling.recordNo} - ${returnedBilling.description}"
					billingItemDto.canceledref = returnedBilling.id
					billingItemDto.cogsPerItem = returnedBilling.cogsPerItem

				}

				billingItemDto.details[BillingItemDetailParam.ITEMID.name()] = item?.id?.toString()

				if (inventory)
					billingItemDto.details[BillingItemDetailParam.INVENTORYID.name()] = inventory.id.toString()

				billingItemDto = save(billingItemDto)
				if(cashBasisItemId!=null)
				{
					def cashBasisItem = cashBasisItemRepository.getOne(cashBasisItemId)
					cashBasisItem.billingItemNo = billingItemDto.id
					cashBasisItemRepository.save(cashBasisItem)

				}
				// Inventory entries
				//$type data ("cs" or "rcs") cs = "Charge Slip"; rcs = "Reverse Charge Slip"

				// gas is ignored in code
				inventoryLedgService.InventoryCharge(
						department.id,
						item.id,
						billingItemDto.recordNo,
						billingItemDto.canceledref ? "rcs" : "cs",
						quantity,
						billingItemDto.billing.id,
						billingItemDto.id)

				//TODO: Accounting Entry
			}

		}

	}

	// additionalUtils for Packages

	List<BillingItem> getPackageBillingItem(Billing billing, Package apackage) {

		createQuery("Select bi from BillingItem bi where bi.billing=:billing and bi.apackage=:apackage and  bi.status='ACTIVE' ",
				[
						"billing" : billing,
						"apackage": apackage
				] as Map<String, Object>).resultList

	}

	@GraphQLMutation
	@Transactional
	Billing overridePriceTier(
			@GraphQLArgument(name = "priceTierId") UUID priceTierId,
			@GraphQLArgument(name = "billingId") UUID billingId) {



		def pricetier = priceTierDetailRepository.findById(priceTierId).get()
		def billing = billingService.findOne(billingId)
		billing.pricetiermanual = priceTierId
		billingService.save(billing)
		try{
			def forCancelling = billing.billingItemList.findAll {
				it.status == BillingItemStatus.ACTIVE &&
						(
								it.itemType == BillingItemType.ROOMBOARD
										|| it.itemType == BillingItemType.MEDICINES
										|| it.itemType == BillingItemType.DIAGNOSTICS
										|| it.itemType == BillingItemType.ORFEE
										|| it.itemType == BillingItemType.SUPPLIES
										|| it.itemType == BillingItemType.OTHERS
										|| it.itemType == BillingItemType.CATHLAB
						) /*&&
					it.priceTierDetail.id != pricetier.id*/
			}.toSorted {
				it.recordNo
			}

			forCancelling.each {
				item ->

					_cancelBillingItem(item.id,
							["reason": "Change Tier to " + pricetier.tierCode],
							true
					)

			}

			forCancelling.each {
				item ->

					def newItem = new BillingItem()

					item.amountdetails.each {
						k, v ->
							newItem.amountdetails.put(k, v)
					}

					newItem.apackage = item.apackage
					newItem.billing = item.billing
					newItem.canceledref = item.canceledref
					newItem.registryTypeCharged = item.registryTypeCharged

					newItem.department = item.department
					newItem.description = item.description

					item.details.each {
						k, v ->
							newItem.details.put(k, v)
					}

					newItem.forPosting = item.forPosting

					newItem.item = item.item
					newItem.itemType = item.itemType
					newItem.postedledger = item.postedledger

					newItem.qty = item.qty
					newItem.status = BillingItemStatus.ACTIVE

					item.supportingDocs.each {
						i ->
							newItem.supportingDocs.add(i)
					}

					newItem.recordNo = generatorService.getNextValue(GeneratorType.BILLING_RECORD_NO) { Long next ->
						StringUtils.leftPad(next.toString(), 5, '0')
					}

					//TODO: Change Price Tier

					newItem.priceTierDetail = pricetier

					if (item.credit > BigDecimal.ZERO) {

						if (newItem.itemType == BillingItemType.ROOMBOARD) {
							newItem.credit = item.credit
						} else if (
						newItem.itemType == BillingItemType.MEDICINES ||
								newItem.itemType == BillingItemType.SUPPLIES
						) {

							if (pricetier) {
								def itemId = newItem.details[BillingItemDetailParam.ITEMID.name()] as String
								if (itemId) {
									def inventoryItem = itemRepository.findById(UUID.fromString(itemId)).get()

									if (inventoryItem)
										newItem.credit = priceTierDetailDao.getItemPrice(pricetier.id, inventoryItem.id)
								}
							}

						} else {

							// Service
							if (pricetier) {
								def serviceId = newItem.details[BillingItemDetailParam.SERVICEID.name()] as String

								if (serviceId) {
									def service = serviceRepository.findById(UUID.fromString(serviceId)).get()
									newItem.credit = priceTierDetailDao.getServicePrice(pricetier.id, service.id)
								}

							}

						}
					}

					if (item.debit > BigDecimal.ZERO) {

						if (newItem.itemType == BillingItemType.ROOMBOARD) {
							newItem.debit = item.debit
						} else if (
						newItem.itemType == BillingItemType.MEDICINES ||
								newItem.itemType == BillingItemType.SUPPLIES
						) {

							if (pricetier) {
								def itemId = newItem.details[BillingItemDetailParam.ITEMID.name()] as String
								if (itemId) {
									def inventoryItem = itemRepository.findById(UUID.fromString(itemId)).get()

									if (inventoryItem)
										newItem.debit = priceTierDetailDao.getItemPrice(pricetier.id, inventoryItem.id)
								}
							}

						} else {

							// Service
							if (pricetier) {
								def serviceId = newItem.details[BillingItemDetailParam.SERVICEID.name()] as String

								if (serviceId) {
									def service = serviceRepository.findById(UUID.fromString(serviceId)).get()
									newItem.debit = priceTierDetailDao.getServicePrice(pricetier.id, service.id)
								}

								//added copy rf details and rf fee

								//end rf

							}
						}
					}
					newItem.rfDetails = item.rfDetails
					newItem.rfFee = item.rfFee
					if(item.rfDetails){
						//Type collectionType = new TypeToken<Collection<RfDetails>>() {}.getType()
						RfDetails rf_details = (RfDetails) new Gson().fromJson(item.rfDetails, RfDetails.class)
						if(rf_details.rfTableId){
							newItem.rfFee = newItem.debit * (new BigDecimal(rf_details.percentage) / 100.00)
						}
					}
					newItem.transactionDate = item.transactionDate
					newItem.recomputeDatetime = Instant.now()
					def newBillItem = save(newItem)

					// assign new id to orderSlipItem
					def itemList = orderSlipItemRepository.getByBillingItem(item)
					itemList.each {
						def upsert = it
						upsert.billing_item = newBillItem
						orderSlipItemRepository.save(upsert)
					}

					//TODO: Accounting Entry

			}

			// Unreliable ang PriceTier
			// Update PF Charges
/*		billing.billingItemList.findAll {
			 it.itemType == BillingItemType.PF && it.status == BillingItemStatus.ACTIVE
		} .each {


			if(pricetier.forSenior){
				def PF_VAT_APPLIED = it.details[BillingItemDetailParam.PF_VAT_APPLIED.name()]
				def vatAMount = new BigDecimal(it.details[BillingItemDetailParam.PF_VAT_AMT.name()])
				if(StringUtils.equalsIgnoreCase(PF_VAT_APPLIED,"YES")){
					it.debit -= vatAMount
					it.details[BillingItemDetailParam.PF_VAT_APPLIED.name()] = "NO"
					save(it)
				}

			}
			else {
				def PF_VAT_APPLIED = it.details[BillingItemDetailParam.PF_VAT_APPLIED.name()]
				def vatAMount = new BigDecimal(it.details[BillingItemDetailParam.PF_VAT_AMT.name()])
				if(!StringUtils.equalsIgnoreCase(PF_VAT_APPLIED,"YES")){

					it.debit += vatAMount
					it.details[BillingItemDetailParam.PF_VAT_APPLIED.name()] = "YES"
					save(it)
				}

			}
			save(it)
		}*/


		}
		catch (Exception e)
		{
			e.printStackTrace()
			throw new IllegalArgumentException(e.message)
		}


		billing
	}

	// Not used for now because of pagination issue
	@GraphQLQuery(name = "getBillingItemSales2")
	Page<Salesreportitem> getBillingItemSales2(@GraphQLArgument(name = "page") Integer page,
	                                           @GraphQLArgument(name = "size") Integer size,
	                                           @GraphQLArgument(name = "dateStart") String dateStart,
	                                           @GraphQLArgument(name = "dateEnd") String dateEnd,
	                                           @GraphQLArgument(name = "type") String type,
	                                           @GraphQLArgument(name = "department") String department, // department uuid
	                                           @GraphQLArgument(name = "processcode") String processcode // or itemcode
	) {
		// offset ? limit ?
		List<Salesreportitem> items = jdbcTemplate.query("""
       select *  from billing.salesreport_1(?::date,?::date,?) where
       category ilike concat('%',?,'%') and   process_code ilike  concat('%',?,'%')
      
""", new RowMapper<Salesreportitem>() {
			@Override
			Salesreportitem mapRow(ResultSet rs, int rowNum) throws SQLException {

				return new Salesreportitem(
						rs.getObject("id", UUID.class),
						rs.getString("category"),
						rs.getTimestamp("date").toInstant(),
						rs.getString("ornos"),
						rs.getString("folio"),
						rs.getString("recno"),
						rs.getString("department"),
						rs.getString("process_code"),
						rs.getString("service"),
						rs.getBigDecimal("gross"),
						rs.getString("discounts_availed"),
						rs.getBigDecimal("discounts_total"),
						rs.getBigDecimal("net_sales")
				)

			}
		},
				dateStart,
				dateEnd,
				department,
				type,
				processcode
		)

		/*
		,
			 page * size,
			 size
		 */


		new PageImpl<Salesreportitem>(items, PageRequest.of(page, size), 0)
	}

	@GraphQLQuery(name = "getBillingItemSales2Totals")
	SalesreportitemTotals getBillingItemSales2Totals(
			@GraphQLArgument(name = "dateStart") String dateStart,
			@GraphQLArgument(name = "dateEnd") String dateEnd,
			@GraphQLArgument(name = "type") String type,
			@GraphQLArgument(name = "department") String department,
			@GraphQLArgument(name = "processcode") String processcode
	) {

		List<SalesreportitemTotals> items = jdbcTemplate.query("""
  
 select sum(gross) as gross, sum(discounts_total) as discounts_total ,sum(net_sales) as net_sales  from billing.salesreport_1(?::date,?::date,?) where
       category ilike concat('%',?,'%')  and   process_code ilike  concat('%',?,'%')
""", new RowMapper<SalesreportitemTotals>() {
			@Override
			SalesreportitemTotals mapRow(ResultSet rs, int rowNum) throws SQLException {

				return new SalesreportitemTotals(
						rs.getBigDecimal("gross"),
						rs.getBigDecimal("discounts_total"),
						rs.getBigDecimal("net_sales")
				)

			}
		},
				dateStart,
				dateEnd,
				department,
				type,
				processcode

		)

		items.find()
	}

//	=====================CODE NI ADONIS================================================

//	and b.id = '9b42e2d7-c430-4237-99ea-10447f365480'
	@Deprecated
	@GraphQLQuery(name = "getBillingItemSales")
	Page<BillingItem> getBillingItemSales(@GraphQLArgument(name = "page") Integer page,
	                                      @GraphQLArgument(name = "size") Integer size,
	                                      @GraphQLArgument(name = "dateStart") String dateStart,
	                                      @GraphQLArgument(name = "dateEnd") String dateEnd,
	                                      @GraphQLArgument(name = "type") String type
	) {

		String query = '''select b from BillingItem b where b.itemType in :itemTypes
						and b.status=:status and to_date(to_char(b.transactionDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
						between to_date(:dateStart,'YYYY-MM-DD') and  to_date(:dateEnd,'YYYY-MM-DD') and KEY(b.details) in :details'''

		String countQuery = ''' select count(b) from BillingItem b where
             			b.itemType in :itemTypes and to_date(to_char(b.transactionDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
             			between to_date(:dateStart,'YYYY-MM-DD') and  to_date(:dateEnd,'YYYY-MM-DD')
                   		and b.status=:status and KEY(b.details) in :details'''

		Map<String, Object> params = new HashMap<>()
		params.put('dateStart', dateStart)
		params.put('dateEnd', dateEnd)
		params.put('itemTypes', [
				BillingItemType.MEDICINES,
				BillingItemType.ROOMBOARD,
				BillingItemType.DEDUCTIONS,
				BillingItemType.SUPPLIES,
				BillingItemType.OTHERS,
				BillingItemType.DIAGNOSTICS
		])
		params.put('details', [
				BillingItemDetailParam.DISCOUNT_ID.name().toString(),
				BillingItemDetailParam.SERVICEID.name().toString(),
				BillingItemDetailParam.ROOMNO.name().toString(),
				BillingItemDetailParam.ITEMID.name().toString()
		])
		params.put('status', BillingItemStatus.ACTIVE)

		if (type) {
			if (type.equalsIgnoreCase("ALL")) {
				query += ''' ORDER BY b.transactionDate asc'''
			} else if (type.equalsIgnoreCase("OTC")) {
				query += ''' and b.billing.patientCase is null ORDER BY b.transactionDate asc'''
				countQuery += ''' and b.billing.patientCase is null'''
			} else {
				query += ''' and b.billing.patientCase.registryType = :type ORDER BY b.transactionDate asc'''
				countQuery += ''' and b.billing.patientCase.registryType = :type'''
				params.put("type", type)
			}
		}

		return getPageable(query, countQuery, page, size, params)
	}

	@GraphQLQuery(name = "getBillingItemSalesList")
	List<BillingItem> getBillingItemSalesList(
			@GraphQLArgument(name = "dateStart") String dateStart,
			@GraphQLArgument(name = "dateEnd") String dateEnd,
			@GraphQLArgument(name = "type") String type
	) {
		def query = ''' select b from BillingItem b where b.itemType in :itemTypes
						and b.status=:status and to_date(to_char(b.transactionDate, 'YYYY-MM-DD'),'YYYY-MM-DD') between to_date(:dateStart,'YYYY-MM-DD') and  to_date(:dateEnd,'YYYY-MM-DD')
						and KEY(b.details) in :details '''

		Map<String, Object> params = new HashMap<>()
		params.put('dateStart', dateStart)
		params.put('dateEnd', dateEnd)
		params.put('itemTypes', [
				BillingItemType.MEDICINES,
				BillingItemType.ROOMBOARD,
				BillingItemType.SUPPLIES,
				//BillingItemType.DEDUCTIONS,
				BillingItemType.OTHERS,
				BillingItemType.DIAGNOSTICS
		])
		params.put('details', [
				BillingItemDetailParam.DISCOUNT_ID.name().toString(),
				BillingItemDetailParam.SERVICEID.name().toString(),
				BillingItemDetailParam.ROOMNO.name().toString(),
				BillingItemDetailParam.ITEMID.name().toString()
		])
		params.put('status', BillingItemStatus.ACTIVE)

		if (type) {
			if (type.equalsIgnoreCase("ALL")) {
				query += ''' ORDER BY b.transactionDate asc'''
			} else if (type.equalsIgnoreCase("OTC")) {
				query += ''' and b.billing.patientCase is null ORDER BY b.transactionDate asc'''
			} else {
				query += ''' and b.billing.patientCase.registryType = :type ORDER BY b.transactionDate asc'''
				params.put('type', type)
			}
		}
		createQuery(query, params).resultList
	}

	@GraphQLQuery(name = "discountDetails", description = "List of item details based on Deductions")
	List<AmountDetailsDto> discountDetails(@GraphQLContext BillingItem billingItem) {
		def result = new ArrayList<AmountDetailsDto>()
		billingItem.amountdetails.each {
			index, value ->
				def details = new AmountDetailsDto(
						billingItem: this.billingItemById(UUID.fromString(index)),
						discountAmount: value
				)
				result.add(details)
		}
		return result
	}

	@GraphQLQuery(name = "serviceDetials", description = "List of Service/item details")
	ItemServiceDto serviceDetials(@GraphQLContext BillingItem billingItem) {
		def result = new ItemServiceDto()
		billingItem.details.each {
			index, value ->
				if (index.equalsIgnoreCase("SERVICEID")) {
					def service = serviceRepository.findById(UUID.fromString(value)).get()
					result = new ItemServiceDto(
							id: UUID.fromString(value),
							desc: service.serviceName,
							proccessCode: service.serviceCode
					)
				} else if (index.equalsIgnoreCase("ITEMID")) {
					def item = itemRepository.findById(UUID.fromString(value)).get()
					result = new ItemServiceDto(
							id: UUID.fromString(value),
							desc: item.descLong,
							proccessCode: item.itemCode
					)
				}
		}
		return result
	}

	@GraphQLQuery(name = "totalGross")
	BigDecimal totalGross(
			@GraphQLArgument(name = "dateStart") String dateStart,
			@GraphQLArgument(name = "dateEnd") String dateEnd,
			@GraphQLArgument(name = "type") String type
	) {
		def query = ''' select sum(coalesce (b.debit, 0) * b.qty) from BillingItem b where b.itemType in :itemTypes
						and b.status=:status and to_date(to_char(b.transactionDate, 'YYYY-MM-DD'),'YYYY-MM-DD') between to_date(:dateStart,'YYYY-MM-DD') and  to_date(:dateEnd,'YYYY-MM-DD')
						and KEY(b.details) in :details '''

		Map<String, Object> params = new HashMap<>()
		params.put('dateStart', dateStart)
		params.put('dateEnd', dateEnd)
		params.put('itemTypes', [
				BillingItemType.MEDICINES,
				BillingItemType.ROOMBOARD,
				BillingItemType.DEDUCTIONS,
				BillingItemType.SUPPLIES,
				BillingItemType.OTHERS,
				BillingItemType.DIAGNOSTICS
		])
		params.put('details', [
				BillingItemDetailParam.DISCOUNT_ID.name().toString(),
				BillingItemDetailParam.SERVICEID.name().toString(),
				BillingItemDetailParam.ROOMNO.name().toString(),
				BillingItemDetailParam.ITEMID.name().toString()
		])
		params.put('status', BillingItemStatus.ACTIVE)

		if (type) {
			if (type.equalsIgnoreCase("OTC")) {
				query += '''and b.billing.patientCase is null'''
			} else if (!type.equalsIgnoreCase("ALL")) {
				query += '''and b.billing.patientCase.registryType = :type'''
				params.put('type', type)
			}
		}
		BigDecimal total = getSum(query, params)
		return total
	}

	@GraphQLQuery(name = "totalDiscount")
	BigDecimal totalDiscount(
			@GraphQLArgument(name = "dateStart") String dateStart,
			@GraphQLArgument(name = "dateEnd") String dateEnd,
			@GraphQLArgument(name = "type") String type
	) {
		def query = ''' select sum(coalesce (b.credit , 0) * b.qty) from BillingItem b where b.itemType in :itemTypes
						and b.status=:status and to_date(to_char(b.transactionDate, 'YYYY-MM-DD'),'YYYY-MM-DD') between to_date(:dateStart,'YYYY-MM-DD') and  to_date(:dateEnd,'YYYY-MM-DD')
						and KEY(b.details) in :details '''
		Map<String, Object> params = new HashMap<>()
		params.put('dateStart', dateStart)
		params.put('dateEnd', dateEnd)
		params.put('itemTypes', [
				BillingItemType.MEDICINES,
				BillingItemType.ROOMBOARD,
				BillingItemType.DEDUCTIONS,
				BillingItemType.SUPPLIES,
				BillingItemType.OTHERS,
				BillingItemType.DIAGNOSTICS
		])
		params.put('details', [
				BillingItemDetailParam.DISCOUNT_ID.name().toString(),
				BillingItemDetailParam.SERVICEID.name().toString(),
				BillingItemDetailParam.ROOMNO.name().toString(),
				BillingItemDetailParam.ITEMID.name().toString()
		])
		params.put('status', BillingItemStatus.ACTIVE)

		if (type) {
			if (type.equalsIgnoreCase("OTC")) {
				query += '''and b.billing.patientCase is null'''
			} else if (!type.equalsIgnoreCase("ALL")) {
				query += '''and b.billing.patientCase.registryType = :type'''
				params.put('type', type)
			}
		}
		BigDecimal total = getSum(query, params)
		return total
	}

	@GraphQLQuery(name = "totalNetSales")
	BigDecimal totalNetSales(
			@GraphQLArgument(name = "dateStart") String dateStart,
			@GraphQLArgument(name = "dateEnd") String dateEnd,
			@GraphQLArgument(name = "type") String type
	) {
		def query = ''' select sum((coalesce (b.debit, 0) - coalesce (b.credit , 0)) * b.qty) from BillingItem b where b.itemType in :itemTypes
						and b.status=:status and to_date(to_char(b.transactionDate, 'YYYY-MM-DD'),'YYYY-MM-DD') between to_date(:dateStart,'YYYY-MM-DD') and  to_date(:dateEnd,'YYYY-MM-DD')
						and KEY(b.details) in :details '''
		Map<String, Object> params = new HashMap<>()
		params.put('dateStart', dateStart)
		params.put('dateEnd', dateEnd)
		params.put('itemTypes', [
				BillingItemType.MEDICINES,
				BillingItemType.ROOMBOARD,
				BillingItemType.DEDUCTIONS,
				BillingItemType.SUPPLIES,
				BillingItemType.OTHERS,
				BillingItemType.DIAGNOSTICS
		])
		params.put('details', [
				BillingItemDetailParam.DISCOUNT_ID.name().toString(),
				BillingItemDetailParam.SERVICEID.name().toString(),
				BillingItemDetailParam.ROOMNO.name().toString(),
				BillingItemDetailParam.ITEMID.name().toString()
		])
		params.put('status', BillingItemStatus.ACTIVE)

		if (type) {
			if (type.equalsIgnoreCase("OTC")) {
				query += '''and b.billing.patientCase is null'''
			} else if (!type.equalsIgnoreCase("ALL")) {
				query += '''and b.billing.patientCase.registryType = :type'''
				params.put('type', type)
			}
		}
		BigDecimal total = getSum(query, params)
		return total
	}

	@Override
	BillingItem save(BillingItem billingItem) {

		Boolean newEntity = billingItem.id == null

		if(newEntity){
			billingItem.registryTypeCharged = billingItem?.billing?.patientCase?.registryType?:"OTC"
		}

		def tmpcancel  = billingItem.tempCanceled

		def bItem = super.save(billingItem) as BillingItem


		if(auto_post_journal && ! (bItem.itemType in [BillingItemType.ANNOTATION_PAYMENTS_GROUPS,BillingItemType.ANNOTATION_NOTIFICATION_GROUPS])){

			def yearFormat = DateTimeFormatter.ofPattern("yyyy")

			if(newEntity && !(billingItem.itemType in  [BillingItemType.PF,BillingItemType.PAYMENTS, BillingItemType.DEDUCTIONS,BillingItemType.DEDUCTIONSPF])){
				// In-Patient

				Map<String,String> details = [:]
				// IPD ERD OPD

				    details.put(LedgerHeaderDetailParam.QUANTITY.name(),bItem.qty.toString())
				    if(bItem.priceTierDetail)
				    details.put(LedgerHeaderDetailParam.PRICETIERDETAILID.name(),bItem.priceTierDetail.id.toString())
				    if(bItem.registryTypeCharged)
				    details.put(LedgerHeaderDetailParam.REGISTRATIONTYPE.name(),bItem.registryTypeCharged)




					// Services
					if(bItem.itemType in [BillingItemType.ORFEE ,BillingItemType.DIAGNOSTICS , BillingItemType.OTHERS, BillingItemType.CATHLAB]){

						// OTC Not applied here
						def headerLedger =	integrationServices.generateAutoEntries(bItem){it, multipleData->


							if(bItem.registryTypeCharged == "IPD")
								it.flagValue = SALES_INTEGRATION.IP_SERVICES.name()

							if(bItem.registryTypeCharged == "ERD")
								it.flagValue = SALES_INTEGRATION.ER_SERVICES.name()

							if(bItem.registryTypeCharged == "OPD")
								it.flagValue = SALES_INTEGRATION.OPD_SERVICES.name()

							if(bItem.registryTypeCharged == "OTC")
							{
								def priceTier = bItem.priceTierDetail

								if(priceTier.isVatable) {
									// this is a VAT Sales
									it.flagValue = SALES_INTEGRATION.OTC_SERVICES.name()
									details.put("VATABLE","YES")
									def output = ((bItem.debit - bItem.credit) / (1 + (priceTier.vatRate/ 100.0)).setScale(2,RoundingMode.HALF_EVEN))
									output *= (priceTier.vatRate/ 100.0) * bItem.qty
									output = output.setScale(2,RoundingMode.HALF_EVEN)
									it.vatOutputTax = output
								}
								else {
									details.put("VATABLE","NO")
									it.flagValue = SALES_INTEGRATION.OTC_NONVAT_SERVICES.name()
								}

							}


							// override flagValue from Service
						  def serviceId  =  	bItem.details[BillingItemDetailParam.SERVICEID.name()]
							if(serviceId){
								def service = serviceRepository.findById(UUID.fromString(serviceId)).get()
								 if(service){
									 if(StringUtils.isNotBlank(service.flagValue)){
										 it.flagValue = bItem.registryTypeCharged + "-" + service.flagValue
									 }
								 }
							}

							if(BooleanUtils.isTrue(enable_costing)){
								def orderSlipItem = bItem.details[BillingItemDetailParam.ORDERSLIPITEM.name()]
								if(orderSlipItem){
									def orderSlip = orderSlipItemRepository.findById(UUID.fromString(orderSlipItem)).get()
									// Services with inventory items
									if(orderSlip.service.serviceType == ServiceTypes.PACKAGE){
										List<OrderSlipItemPackageContent> itemsForCharge =  orderSlipItemPackageContentService.orderSlipItemPackageByParent(orderSlip.id)

										List<BillingItem> finalAccounts  = []
										Map<AccountingCategory, BigDecimal> normalAcc  = [:]
										Map<AccountingCategory, BigDecimal> consignmentAcc  = [:]

										//loop
										itemsForCharge.each{
											packItem ->
												if(packItem.item.consignment){
													if(!consignmentAcc.containsKey(packItem.item.accountingCategory)){
														consignmentAcc[packItem.item.accountingCategory] = 0.0
													}
													consignmentAcc[packItem.item.accountingCategory] =  consignmentAcc[packItem.item.accountingCategory] + packItem.unitCost
												}else{
													if(!normalAcc.containsKey(packItem.item.accountingCategory)){
														normalAcc[packItem.item.accountingCategory] = 0.0
													}
													normalAcc[packItem.item.accountingCategory] =  normalAcc[packItem.item.accountingCategory] + packItem.unitCost
												}
										}

										//loop normal
										normalAcc.each {acc,value ->
											finalAccounts << new BillingItem().tap {na ->
												na.costDept = it.department
												na.itemCategory = acc
												na.dmPackage = value
												na.dmInventory = BigDecimal.ZERO;
												na.dmConsignment = BigDecimal.ZERO;
												na.dmConsignmentPayable = BigDecimal.ZERO;
											}
										}
										multipleData << finalAccounts
										//credit
										normalAcc.each {acc,value ->
											finalAccounts << new BillingItem().tap {na ->
												na.costDept = it.department
												na.itemCategory = acc
												na.dmPackage = BigDecimal.ZERO;
												na.dmInventory = value * -1.0;
												na.dmConsignment = BigDecimal.ZERO;
												na.dmConsignmentPayable = BigDecimal.ZERO;
											}
										}
										multipleData << finalAccounts

										//loop consignment
										consignmentAcc.each {acc,value ->
											finalAccounts << new BillingItem().tap {na ->
												na.costDept = it.department
												na.itemCategory = acc
												na.dmConsignment = value
												na.dmPackage = BigDecimal.ZERO;
												na.dmInventory = BigDecimal.ZERO;
												na.dmConsignmentPayable = BigDecimal.ZERO;
											}
										}
										multipleData << finalAccounts
										//credit
										consignmentAcc.each {acc,value ->
											finalAccounts << new BillingItem().tap {na ->
												na.costDept = it.department
												na.itemCategory = acc
												na.dmConsignmentPayable = value
												na.dmConsignment = BigDecimal.ZERO;
												na.dmInventory = BigDecimal.ZERO;
												na.dmPackage = BigDecimal.ZERO;
											}
										}

										multipleData << finalAccounts
									}
								}
							}


							it.income = (it.subTotal )
							it.income -= it.vatOutputTax

						}


						bItem.details.each { k,v ->
							details[k] = v
						}
						details["BILLING_ITEM_ID"] = bItem.id.toString()


					def pHeader =	ledgerServices.persistHeaderLedger(headerLedger,
								"${bItem.billing.createdDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${bItem.billing.billingNo}",
								"${bItem.billing.billingNo}-${bItem.billing?.patient?.fullName?:bItem?.billing?.otcname}",
								"${bItem.recordNo}-${bItem.description}",
								LedgerDocType.DM,
								JournalType.SALES,
								bItem?.recomputeDatetime?:bItem.transactionDate,
								details)
						bItem.postedledger = pHeader.id

						if(bItem.subTotal < 0.0)
						{
							pHeader.reversal = true
							ledgerServices.save(pHeader)
						}
						save(bItem)
					}


					if(bItem.itemType in [BillingItemType.SUPPLIES, BillingItemType.MEDICINES]){

						// OTC Applied HERE

						def headerLedger =	integrationServices.generateAutoEntries(bItem){it, multipleData->

							// Revenue Recognition
							it.revenueDept = it.department
							it.inventoryDept = it.department
							it.costDept = it.department
							it.itemCategory = null

							/*
							Note:

							1.) All users can charge even Billers

							 */


							Department userDepartment = it.department
							// ======== Special Case for Ace Bohol  WARDS REVENUE is to CSSR and PHARMACY========
							if(StringUtils.trim(userDepartment.groupCategory) =="NURSING"){
								if(it.itemType == BillingItemType.SUPPLIES){
								Department deptSupplies = departmentRepository.findOneByRevenueTag("SUPPLIES")
									it.revenueDept = deptSupplies
									it.costDept = deptSupplies
									it.inventoryDept = deptSupplies
								}

								if(it.itemType == BillingItemType.MEDICINES){
									Department medicinesDept = departmentRepository.findOneByRevenueTag("MEDICINES")
									it.revenueDept = medicinesDept
									it.costDept = medicinesDept
									it.inventoryDept = medicinesDept
								}
							}






							if(bItem.registryTypeCharged == "IPD"){
								if(bItem.itemType == BillingItemType.SUPPLIES)
									it.flagValue = SALES_INTEGRATION.IP_SUPPLIES.name()
								else
									it.flagValue = SALES_INTEGRATION.IP_MEDS.name()
							}


							if(bItem.registryTypeCharged == "ERD"){
								if(bItem.itemType == BillingItemType.SUPPLIES)
									it.flagValue = SALES_INTEGRATION.ER_SUPPLIES.name()
								else
									it.flagValue = SALES_INTEGRATION.ER_MEDS.name()
							}


							if(bItem.registryTypeCharged == "OPD"){
								if(bItem.itemType == BillingItemType.SUPPLIES)
									it.flagValue = SALES_INTEGRATION.OPD_SUPPLIES.name()
								else
									it.flagValue = SALES_INTEGRATION.OPD_MEDS.name()
							}




							String itemId = it.details[BillingItemDetailParam.ITEMID.name()]
							if(StringUtils.isNotBlank(itemId)){
								Item item = itemRepository.findById(UUID.fromString(itemId)).get()
								println("item.accountingCategory => " + item.accountingCategory.description)
								//item category sub account
								it.itemCategory = item.accountingCategory

								// override oxygen
								if(item.gas){

									if(it.roomAndBoard == null)
										it.roomAndBoard = bItem.billing.patientCase.room?bItem.billing.patientCase.room.department:bItem.billing.patientCase.department

									if(bItem.registryTypeCharged == "OPD"){
										it.flagValue =SALES_INTEGRATION.OPD_OXYGEN.name()
									}
									if(bItem.registryTypeCharged == "IPD"){
										it.flagValue =SALES_INTEGRATION.IP_OXYGEN.name()
									}
									if(bItem.registryTypeCharged == "ERD"){
										it.flagValue =SALES_INTEGRATION.ER_OXYGEN.name()
									}
								}
								else {

									if(bItem.registryTypeCharged == "OTC"){
										def priceTier = bItem.priceTierDetail

										if(priceTier.isVatable && item.vatable){
											// this is a VAT Sales

											details.put("VATABLE","YES")

											if(bItem.itemType == BillingItemType.SUPPLIES)
											{
												it.flagValue = SALES_INTEGRATION.OTC_SUPPLIES.name()

												// compute VAT
												def output = ((bItem.debit - bItem.credit) / (1 + (priceTier.vatRate/ 100.0)).setScale(2,RoundingMode.HALF_EVEN))
												output *= (priceTier.vatRate/ 100.0) * bItem.qty
												output = output.setScale(2,RoundingMode.HALF_EVEN)
												it.vatOutputTax = output

											}
											else
											{
												it.flagValue = SALES_INTEGRATION.OTC_MEDS.name()

												def output = ((bItem.debit - bItem.credit) / (1 + (priceTier.vatRate/ 100.0)).setScale(2,RoundingMode.HALF_EVEN))
												output *= (priceTier.vatRate/ 100.0) * bItem.qty
												output = output.setScale(2,RoundingMode.HALF_EVEN)
												it.vatOutputTax = output

											}
										}
										else {
											// this is a non vat sale

											details.put("VATABLE","NO")

											if(bItem.itemType == BillingItemType.SUPPLIES)
												it.flagValue = SALES_INTEGRATION.OTC_NONVAT_SUPPLIES.name()
											else
												it.flagValue = SALES_INTEGRATION.OTC_NONVAT_MEDS.name()
										}

									}
								}

								//
							}

							if(bItem.subTotal < 0.0){
								// its a return
								if(bItem.cogsPerItem  && bItem.cogsPerItem > 0.0){
									details.put(LedgerHeaderDetailParam.COGS_PER_ITEM.name(),bItem.cogsPerItem.toPlainString())
									it.costOfSale = bItem.cogsPerItem * (bItem.qty * -1)
									it.inventoryDeduct = it.costOfSale * -1.0

									it.income = (it.subTotal )

									it.income -= it.vatOutputTax



								}
								else {

									it.costOfSale=0.0
									it.inventoryDeduct = 0.0
									it.income = (it.subTotal )
									it.income -= it.vatOutputTax

								}


							}
							else{
								def cost=inventoryResource.getLastUnitPrice(itemId).abs()


								it.cogsPerItem = cost
								details.put(LedgerHeaderDetailParam.COGS_PER_ITEM.name(),cost.toPlainString())


								it.costOfSale = cost * bItem.qty
								it.inventoryDeduct =it.costOfSale * -1.0
								it.income = (it.subTotal )
                                it.income -= it.vatOutputTax
							}

							if(!BooleanUtils.isTrue(enable_costing)){
								it.costOfSale = 0.0
								it.inventoryDeduct =  0.0
							}





						}




						bItem.details.each { k,v ->
							details[k] = v
						}
						details["BILLING_ITEM_ID"] = bItem.id.toString()

					def pHeader =ledgerServices.persistHeaderLedger(headerLedger,
								"${bItem.billing.createdDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${bItem.billing.billingNo}",
								"${bItem.billing.billingNo}-${bItem.billing?.patient?.fullName?:(bItem.billing?.otcname)}",
								"${bItem.recordNo}-${bItem.description}",
								LedgerDocType.DM,
								JournalType.SALES,
						    	bItem?.recomputeDatetime?:bItem.transactionDate,
								details)

						bItem.postedledger = pHeader.id

						if(bItem.subTotal < 0.0)
						{
							pHeader.reversal = true
							ledgerServices.save(pHeader)
						}
						save(bItem)
					}

				if(bItem.itemType in [BillingItemType.ROOMBOARD]){
					def headerLedger =	integrationServices.generateAutoEntries(bItem){it, multipleData->
						if(bItem.registryTypeCharged == "IPD")
							it.flagValue = SALES_INTEGRATION.IP_ROOM.name()

						if(bItem.registryTypeCharged == "ERD")
							it.flagValue = SALES_INTEGRATION.ER_ROOM.name()

						if(bItem.registryTypeCharged == "OPD")
							it.flagValue = SALES_INTEGRATION.OPD_ROOM.name()



						// Revenue for that department
						if(it.roomAndBoard == null)
						 it.roomAndBoard = bItem.billing.patientCase.room.department

						it.income = (it.subTotal )

					}




					bItem.details.each { k,v ->
						details[k] = v
					}
					details["BILLING_ITEM_ID"] = bItem.id.toString()

					def pHeader =	ledgerServices.persistHeaderLedger(headerLedger,
							"${bItem.billing.createdDate.atZone(ZoneId.systemDefault()).format(yearFormat)}-${bItem.billing.billingNo}",
							"${bItem.billing.billingNo}-${bItem.billing.patient.fullName}",
							"${bItem.recordNo}-${bItem.description}",
							LedgerDocType.DM,
							JournalType.SALES,
							bItem?.recomputeDatetime?:bItem.transactionDate,
							details)
					bItem.postedledger = pHeader.id

					if(bItem.subTotal < 0.0)
					{
						pHeader.reversal = true
						ledgerServices.save(pHeader)
					}
					save(bItem)
				}

			}else if(tmpcancel  && !(billingItem.itemType in  [BillingItemType.PF,BillingItemType.DEDUCTIONSPF,BillingItemType.PAYMENTS])){
				// when billing charges is Canceled. Reversal are treated normally with a negative subtotal
				// this is reversing entries
				if(bItem.postedledger)
				{
					def header = ledgerServices.findOne(bItem.postedledger)
					ledgerServices.reverseEntries(header)
				}

			}


		}


		bItem

	}

	//update for ap process PF
	@GraphQLMutation(name = "updatePfProcess")
	BillingItem updatePfProcess(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "status") Boolean status
	) {
		def item = findOne(id)
		item.apProcess = status
		save(item)

		//update ap delete
		return item
	}

	//show sales report detailed
	@GraphQLQuery(name = "listSalesReportDetailed")
	List<SalesReportDetailedDto> listSalesReportDetailed(
			@GraphQLArgument(name = "type") String type,
			@GraphQLArgument(name = "start") String start,
			@GraphQLArgument(name = "end") String end
	) {
		String sql = "SELECT * from billing.detailed_sales_report('${type}', '${start}', '${end}');"
		List<SalesReportDetailedDto> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper(SalesReportDetailedDto.class))
		return list
	}

	//get billing item by record no
	BillingItem getBillingItemByRecordNo(String refNo) {
		createQuery("Select bi from BillingItem bi where bi.recordNo = :refNo", ["refNo" : refNo] as Map<String, Object>).resultList.find()
	}

	@GraphQLQuery
	Page<BillingItem> getBillingItemsDeductions(
			@GraphQLArgument(name = "patientId") UUID patientId,
			@GraphQLArgument(name = "billingId") UUID billingId,
			@GraphQLArgument(name = "companyId") String companyId,
			@GraphQLArgument(name = "admissionDate") String admissionDate,
			@GraphQLArgument(name = "dischargedDate") String dischargedDate,
		  	@GraphQLArgument(name = "billingItemType") List<String> billingItemType,
		  	@GraphQLArgument(name = "page") Integer page,
		  	@GraphQLArgument(name = "size") Integer size,
		  	@GraphQLArgument(name = "filter") String filter
	) {
		String strQuery = """ from BillingItem b where """
		Map<String,Object> param = [
			companyId: companyId,
			itemTypes: billingItemType.collect { BillingItemType.valueOf(it) },
			filter   : filter
		]

		if(billingId) {
			strQuery += """ b.billing.id = :billingId and"""
			param['billingId'] = billingId
		}

		if(patientId && !billingId) {
			strQuery += """ b.billing.patient.id = :patientId and"""
			param['patientId'] = patientId
		}

		if(admissionDate){
			strQuery += """ to_char(b.billing.patientCase.admissionDatetime, 'YYYY-MM-DD') = :admissionDate and"""
			param['admissionDate'] = admissionDate
		}

		if(dischargedDate){
			strQuery += """ to_char(b.billing.patientCase.dischargedDatetime, 'YYYY-MM-DD') = :dischargedDate and"""
			param['dischargedDate'] = dischargedDate
		}

	 	getPageable("""
                    select b  ${strQuery} b.itemType in :itemTypes and b.details['COMPANY_ACCOUNT_ID'] = :companyId and (b.arBilled is NULL or b.arBilled = false)
                    and (lower(b.description) like lower(concat('%',:filter,'%')) or lower(b.recordNo) like lower(concat('%',:filter,'%')))  order by b.createdDate desc
            """,
				"""
             		select count(b) ${strQuery} b.itemType in :itemTypes and b.details['COMPANY_ACCOUNT_ID'] = :companyId and (b.arBilled is NULL or b.arBilled = false)
                    and (lower(b.description) like lower(concat('%',:filter,'%')) or lower(b.recordNo) like lower(concat('%',:filter,'%'))) 
                """,
				page,
				size,
				param)
	}

  @GraphQLQuery
	Page<BillingItem> getBillingItemsClaims(
			@GraphQLArgument(name = "companyId") String companyId,
			@GraphQLArgument(name = "billingItemType") List<String> billingItemType,
			@GraphQLArgument(name = "registryType") String registryType,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size,
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "filterType") String filterType,
			@GraphQLArgument(name = "dateType") String dateType,
			@GraphQLArgument(name = "filterDate") String filterDate
	) {
		String strQuery = """from BillingItem b where"""
		Map<String,Object> param = [
				companyId: companyId,
				itemTypes: billingItemType.collect { BillingItemType.valueOf(it) },
				filter   : filter,
				registryType : registryType
		]

		switch (filterType){
			case 'FOLIO_NO':
				strQuery += """ (lower(b.billing.billingNo) like lower(concat('%',:filter,'%'))) and"""
				break;
			case 'PATIENT':
				strQuery += """ (lower(b.billing.patient.fullName) like lower(concat('%',:filter,'%'))) and"""
				break;
			case 'DESCRIPTION':
				strQuery += """ (lower(b.description) like lower(concat('%',:filter,'%'))) and"""
				break;
			default:
				strQuery += """ (lower(b.recordNo) like lower(concat('%',:filter,'%'))) and"""
				break;
		}

		switch (dateType) {
			case 'ADMISSION_DATE':
				param['filterDate'] = filterDate
				strQuery += """ to_char(b.billing.patientCase.admissionDatetime, 'YYYY-MM-DD') = :filterDate and"""
				break;
			case 'DISCHARGED_DATE':
				param['filterDate'] = filterDate
				strQuery += """ to_char(b.billing.patientCase.dischargedDatetime, 'YYYY-MM-DD') = :filterDate and"""
				break;
			case 'TRANSACTION_DATE':
				param['filterDate'] = filterDate
				strQuery += """ to_char(b.transactionDate, 'YYYY-MM-DD') = :filterDate and"""
				break;
			default:
				strQuery += """"""
				break;
		}


		getPageable("""
                    select b  ${strQuery} b.itemType in :itemTypes and b.details['COMPANY_ACCOUNT_ID'] = :companyId and (b.arBilled is NULL or b.arBilled = false)
					and b.billing.patientCase.registryType = :registryType
					and b.status = 'ACTIVE'
				 	order by b.createdDate desc
            """,
				"""
             		select count(b) ${strQuery} b.itemType in :itemTypes and b.details['COMPANY_ACCOUNT_ID'] = :companyId and (b.arBilled is NULL or b.arBilled = false)
					and b.billing.patientCase.registryType = :registryType
					and b.status = 'ACTIVE'
                """,
				page,
				size,
				param)
	}
}
