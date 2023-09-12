package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.AccountReceivable
import com.hisd3.hismk2.domain.accounting.LedgerDocType
import com.hisd3.hismk2.domain.accounting.BillingSchedule
import com.hisd3.hismk2.domain.accounting.BillingScheduleItems
import com.hisd3.hismk2.domain.accounting.JournalType
import com.hisd3.hismk2.domain.accounting.LedgerDocType
import com.hisd3.hismk2.domain.accounting.dto.CustomBillingItemNativeDto
import com.hisd3.hismk2.domain.billing.BillingItem
import com.hisd3.hismk2.domain.billing.BillingItemStatus
import com.hisd3.hismk2.domain.billing.BillingItemType
import com.hisd3.hismk2.graphqlservices.accounting.transformers.BillingScheduleTransform
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.billing.BillingItemServices
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.accounting.BillingScheduleItemsRepository
import com.hisd3.hismk2.repository.accounting.BillingScheduleRepository
import com.hisd3.hismk2.services.EntityObjectMapperService
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.hibernate.query.NativeQuery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

import javax.persistence.EntityManager
import javax.transaction.Transactional
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Canonical
class dailyReceivableDto {
	Date discharged_date
	String patient_name
	String soa_no
	String folio_no
	BigDecimal hci_amount
	BigDecimal pf_amount
	BigDecimal total_amount
}


@Canonical
class BillingItemWithBsDTOPage {
	List<CustomBillingItemNativeDto> content
	Integer page
	Integer size
	BigInteger totalSize
}

@Transactional(rollbackOn = [Exception.class])
@Component
@GraphQLApi
class BillingScheduleServices extends AbstractDaoService<BillingSchedule> {

	BillingScheduleServices() {
		super(BillingSchedule.class)
	}

	@Autowired
	EntityObjectMapperService entityObjectMapperService

	@Autowired
	BillingScheduleRepository billingScheduleRepository

	@Autowired
	BillingScheduleItemsRepository billingScheduleItemRepository

	@Autowired
	BillingItemServices billingItemServices

	@Autowired
	BillingScheduleItemsServices billingScheduleItemsServices

	@Autowired
	IntegrationServices integrationServices

	@Autowired
	LedgerServices ledgerServices

	@Autowired
	GeneratorService generatorService

	@Autowired
	EntityManager entityManager

	@Transactional
	@GraphQLMutation
	BillingSchedule upsertBillingSchedule(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "fieldsItems") ArrayList<Map<String, Object>> fieldsItems

	) {

		if (id) {
			def item = billingScheduleRepository.findById(id).get()
			List<UUID> listUUID = new ArrayList<UUID>()
			if(fieldsItems){
				fieldsItems.each {
					it ->
						def existing = billingScheduleItemsServices.getBSchItemPerBillItem(UUID.fromString(it['billingItem'].toString()),id)
						if(existing){
							existing.approvalCode = it['approvalCode']
							billingScheduleItemRepository.save(existing)
							listUUID.push(existing.id)
						}
						else{
							def newBill = new BillingScheduleItems()
							newBill.billingSchedule = item
							entityObjectMapperService.updateFromMap(newBill, it as Map<String, Object>)
							def newItem = billingScheduleItemRepository.save(newBill)
							if(newItem.billingItem && newItem.billingItem.arBilled == false) {
								def billingItem = billingItemServices.findOne(newItem.billingItem.id)
								billingItem.arBilled = true
								billingItemServices.save(billingItem)
							}
							listUUID.push(newItem.id)
						}

				}
			}

			List<BillingScheduleItems> bsItem = billingScheduleItemsServices.billingScheduleItemsPerParent(id)
			bsItem.each {
				it ->
					if(!listUUID.contains(it.id)) {
						billingScheduleItemsServices.delete(it)
						if(it.billingItem && !(it.arTransfer)) {
							def billingItem = billingItemServices.findOne(it.billingItem.id)
							billingItem.arBilled = false
							billingItemServices.save(billingItem)
						}
					}
			}

			if(fields){
				entityObjectMapperService.updateFromMap(item,fields)
				billingScheduleRepository.save(item)
			}
			return item

		} else {
			def item = new BillingSchedule()

			item.billingScheduleNo = generatorService.getNextValue(GeneratorType.BILLING_SCHEDULE_NO, {
				return "BSN-" + StringUtils.leftPad(it.toString(), 6, "0")
			})
			entityObjectMapperService.updateFromMap(item, fields)
			def newSave = billingScheduleRepository.save(item)

			fieldsItems.each {
				it ->
					def newBill = new BillingScheduleItems()
					newBill.billingSchedule = newSave
					entityObjectMapperService.updateFromMap(newBill,it)
					if(newBill.billingItem) {
						def billingItem = billingItemServices.findOne(newBill.billingItem.id)
						billingItem.arBilled = true
						billingItemServices.save(billingItem)
					}
					billingScheduleItemRepository.save(newBill)
			}

			return newSave

		}

	}


	@Transactional
	@GraphQLMutation
	BillingSchedule upsertManualBillingSchedule(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "fieldsItems") ArrayList<Map<String, Object>> fieldsItems
	) {

		if (id) {
			def item = billingScheduleRepository.findById(id).get()

			fieldsItems.each {
				it ->
					def newBill = new BillingScheduleItems()
					newBill.billingSchedule = item
					entityObjectMapperService.updateFromMap(newBill,it)
					billingScheduleItemRepository.save(newBill)
			}

			def currentTotal = billingScheduleItemRepository.getSumBillingSchedule(item.id)
			item.totalReceivableAmount = currentTotal
			def newUpdate = billingScheduleRepository.save(item)

			return newUpdate
		} else {
			def item = new BillingSchedule()

			item.billingScheduleNo = generatorService.getNextValue(GeneratorType.BILLING_SCHEDULE_NO, {
				return "BSN-" + StringUtils.leftPad(it.toString(), 6, "0")
			})
			entityObjectMapperService.updateFromMap(item, fields)
			def newSave = billingScheduleRepository.save(item)

			fieldsItems.each {
				it ->
					def newBill = new BillingScheduleItems()
					newBill.billingSchedule = newSave
					entityObjectMapperService.updateFromMap(newBill,it)
					billingScheduleItemRepository.save(newBill)
			}

			return newSave

		}

	}

	@Transactional
	@GraphQLMutation
	GraphQLRetVal<String> updateBillingSchedule(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		try{
			if (id) {
				def item = billingScheduleRepository.findById(id).get()
				if(fields){
					entityObjectMapperService.updateFromMap(item,fields)
					billingScheduleRepository.save(item)
				}
				return new GraphQLRetVal<String>('success',true,"Successfully updated.")
			} else {
				return new GraphQLRetVal<String>('errror',false,"No id parameter.")
			}
		}
		catch (e){
			return new GraphQLRetVal<String>('errror',false,e.message)
		}
	}


	@GraphQLQuery(name = "BillingSchedulePerGroupGuarantor")
	Page<BillingSchedule> BillingSchedulePerGroupGuarantor(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "accounts") ArrayList<UUID> accounts,
			@GraphQLArgument(name = "status") ArrayList<String> status,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {
		if(accounts){
			getPageable(
					"""
              	Select c from BillingSchedule c  where c.companyAccount.id in (:accounts) and c.status in (:status) and lower(c.billingScheduleNo) like lower(concat('%',:filter,'%')) order by c.billingScheduleNo desc
				""",
					"""
			 	Select count(c) from BillingSchedule c  where c.companyAccount.id in (:accounts) and c.status in (:status) and lower(c.billingScheduleNo) like lower(concat('%',:filter,'%'))
				""",
					page,
					size,
					[
							filter: filter,
							accounts: accounts,
							status : status
					]
			)
		}
		else{
			getPageable(
					"""
              	Select c from BillingSchedule c where c.companyAccount.id is not null and c.status in (:status) and lower(c.billingScheduleNo) like lower(concat('%',:filter,'%')) order by c.billingScheduleNo desc
				""",
					"""
			 	Select count(c) from BillingSchedule c where c.companyAccount.id is not null and c.status in (:status) and lower(c.billingScheduleNo) like lower(concat('%',:filter,'%'))
				""",
					page,
					size,
					[
							filter: filter,
							status: status
					]
			)
		}


	}

	@GraphQLQuery(name = "billingSchedulePerCompany")
	Page<BillingSchedule> getBillingSchedulePerCompany(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "companyId") UUID companyId,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		getPageable(
				"""
              	Select c from BillingSchedule c  where c.companyAccount.id = :companyId and lower(c.billingScheduleNo) like lower(concat('%',:filter,'%')) order by c.billingScheduleNo desc
				""",
				"""
			 	Select count(c) from BillingSchedule c  where c.companyAccount.id = :companyId and lower(c.billingScheduleNo) like lower(concat('%',:filter,'%'))
				""",
				page,
				size,
				[
						filter: filter,
						companyId: companyId,
				]
		)

	}

	@GraphQLQuery(name = "getAllOutstandingClaimsByCompany")
	Page<BillingItem> getAllOutstandingClaimsByCompany(
			@GraphQLArgument(name = "companyId") String companyId,
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "patientType") String patientType,
			@GraphQLArgument(name = "billingType") List<String> billingTypes,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {
		//language=HQL
		def result = null

		if(companyId){
			result = billingItemServices.getPageable(
					"""
				 select bi from BillingItem bi
				 LEFT JOIN BillingScheduleItems bs on bi.id = bs.billingItem.id and (bs.isVoided = FALSE OR bs.isVoided IS NULL)
				 where bs.billingItem.id is null and bi.details['COMPANY_ACCOUNT_ID'] = :companyId
				 and 'COMPANY_ACCOUNT_CLAIM_REFERENCE' not in indices(bi.details)
				 and bi.billing.patientCase.registryType = :patientType
				 and 
				 (
					lower(bi.billing.patient.fullName) like concat('%', :filter ,'%')
					or lower(bi.recordNo)  like concat('%',:filter,'%')
				 )
				 and 
				 	bi.itemType in :itemTypes
				 and  
				 	bi.status = :status
				 order by bi.transactionDate desc
				""", """
				select count(bi) from BillingItem bi 
				LEFT JOIN BillingScheduleItems bs on bi.id = bs.billingItem.id and (bs.isVoided = FALSE OR bs.isVoided IS NULL)
			 	where bs.billingItem.id is null and bi.details['COMPANY_ACCOUNT_ID'] = :companyId
				and 'COMPANY_ACCOUNT_CLAIM_REFERENCE' not in indices(bi.details)
				and bi.billing.patientCase.registryType = :patientType
				and 
				(
					lower(bi.billing.patient.fullName) like concat('%', :filter ,'%')
					or lower(bi.recordNo)  like concat('%',:filter,'%')
			  	)
			  	and 
					bi.itemType in :itemTypes
			  	and  
			  		bi.status = :status	
				""",
					page,
					size,
					[
							companyId: companyId,
							patientType     : patientType,
							itemTypes		: billingTypes.collect { BillingItemType.valueOf(it) },
							filter          : filter,
							status          : BillingItemStatus.ACTIVE
					]

			)
		}

		return result

	}

	@GraphQLQuery(name = "getAllOutstandingClaimsByCompany_v2")
	Page<BillingItem> getAllOutstandingClaimsByCompany_v2(
			@GraphQLArgument(name = "companyId") String companyId,
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "patientType") String patientType,
			@GraphQLArgument(name = "billingType") List<String> billingTypes,
			@GraphQLArgument(name = "filterDate") String filterDate,
			@GraphQLArgument(name = "dateType") String dateType,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {
		//language=HQL
		def result = null

		if(companyId){
			if(dateType.equalsIgnoreCase('admission')){
				result = billingItemServices.getPageable(
						"""
				 select bi from BillingItem bi
				 LEFT JOIN BillingScheduleItems bs on bi.id = bs.billingItem.id and (bs.isVoided = FALSE OR bs.isVoided IS NULL)
				 where bs.billingItem.id is null and bi.details['COMPANY_ACCOUNT_ID'] = :companyId
				 and 'COMPANY_ACCOUNT_CLAIM_REFERENCE' not in indices(bi.details)
				 and bi.billing.patientCase.registryType = :patientType
				 and 
				 (
					lower(bi.billing.patient.fullName) like concat('%', :filter ,'%')
					or lower(bi.recordNo)  like concat('%',:filter,'%')
					or lower(bi.billing.patientCase.caseNo)  like concat('%',:filter,'%')
				 )
				 and
					to_char(bi.billing.patientCase.admissionDatetime, 'YYYY-MM-DD') = :filterDate
				 and 
				 	bi.itemType in :itemTypes
				 and  
				 	bi.status = :status
				 order by bi.billing.patientCase.admissionDatetime desc
				""", """
				select count(bi) from BillingItem bi 
				LEFT JOIN BillingScheduleItems bs on bi.id = bs.billingItem.id and (bs.isVoided = FALSE OR bs.isVoided IS NULL)
			 	where bs.billingItem.id is null and bi.details['COMPANY_ACCOUNT_ID'] = :companyId
				and 'COMPANY_ACCOUNT_CLAIM_REFERENCE' not in indices(bi.details)
				and bi.billing.patientCase.registryType = :patientType
				and 
				(
					lower(bi.billing.patient.fullName) like concat('%', :filter ,'%')
					or lower(bi.recordNo)  like concat('%',:filter,'%')
					or lower(bi.billing.patientCase.caseNo)  like concat('%',:filter,'%')
			  	)
			  	and 
					to_char(bi.billing.patientCase.admissionDatetime, 'YYYY-MM-DD') = :filterDate
			  	and
					bi.itemType in :itemTypes
			  	and  
			  		bi.status = :status	
				""",
						page,
						size,
						[
								companyId: companyId,
								patientType     : patientType,
								itemTypes		: billingTypes.collect { BillingItemType.valueOf(it) },
								filter          : filter,
								filterDate      : filterDate,
								status          : BillingItemStatus.ACTIVE
						]

				)
			}
			else if(dateType.equalsIgnoreCase('discharged')){
				result = billingItemServices.getPageable(
						"""
				 select bi from BillingItem bi
				 LEFT JOIN fetch  BillingScheduleItems bs on bi.id = bs.billingItem.id and (bs.isVoided = FALSE OR bs.isVoided IS NULL)
				 where bs.billingItem.id is null and bi.details['COMPANY_ACCOUNT_ID'] = :companyId
				 and 'COMPANY_ACCOUNT_CLAIM_REFERENCE' not in indices(bi.details)
				 and bi.billing.patientCase.registryType = :patientType
				 and 
				 (
					lower(bi.billing.patient.fullName) like concat('%', :filter ,'%')
					or lower(bi.recordNo)  like concat('%',:filter,'%')
					or lower(bi.billing.patientCase.caseNo)  like concat('%',:filter,'%')
				 )
				 and 
				 	bi.itemType in :itemTypes
				 and 
					to_char(bi.billing.patientCase.dischargedDatetime, 'YYYY-MM-DD') = :filterDate
				 and  
				 	bi.status = :status
				 order by bi.billing.patientCase.dischargedDatetime desc
				""", """
				select count(bi) from BillingItem bi 
				LEFT JOIN fetch BillingScheduleItems bs on bi.id = bs.billingItem.id and (bs.isVoided = FALSE OR bs.isVoided IS NULL)
			 	where bs.billingItem.id is null and bi.details['COMPANY_ACCOUNT_ID'] = :companyId
				and 'COMPANY_ACCOUNT_CLAIM_REFERENCE' not in indices(bi.details)
				and bi.billing.patientCase.registryType = :patientType
				and 
				(
					lower(bi.billing.patient.fullName) like concat('%', :filter ,'%')
					or lower(bi.recordNo)  like concat('%',:filter,'%')
					or lower(bi.billing.patientCase.caseNo)  like concat('%',:filter,'%')
			  	)
			  	and 
					bi.itemType in :itemTypes
				and 
					to_char(bi.billing.patientCase.dischargedDatetime, 'YYYY-MM-DD') = :filterDate
			  	and  
			  		bi.status = :status	
				""",
						page,
						size,
						[
								companyId: companyId,
								patientType     : patientType,
								itemTypes		: billingTypes.collect { BillingItemType.valueOf(it) },
								filter          : filter,
								filterDate      : filterDate,
								status          : BillingItemStatus.ACTIVE
						]

				)
			}
			else {
				result = billingItemServices.getPageable(
						"""
				 select bi from BillingItem bi
				 LEFT JOIN fetch  BillingScheduleItems bs on bi.id = bs.billingItem.id and (bs.isVoided = FALSE OR bs.isVoided IS NULL)
				 where bs.billingItem.id is null and bi.details['COMPANY_ACCOUNT_ID'] = :companyId
				 and 'COMPANY_ACCOUNT_CLAIM_REFERENCE' not in indices(bi.details)
				 and bi.billing.patientCase.registryType = :patientType
				 and 
				 (
					lower(bi.billing.patient.fullName) like concat('%', :filter ,'%')
					or lower(bi.recordNo)  like concat('%',:filter,'%')
					or lower(bi.billing.patientCase.caseNo)  like concat('%',:filter,'%')
				 )
				 and 
				 	bi.itemType in :itemTypes
				 and  
				 	bi.status = :status
				 order by bi.transactionDate desc
				""", """
				select count(bi) from BillingItem bi 
				LEFT JOIN fetch BillingScheduleItems bs on bi.id = bs.billingItem.id and (bs.isVoided = FALSE OR bs.isVoided IS NULL)
			 	where bs.billingItem.id is null and bi.details['COMPANY_ACCOUNT_ID'] = :companyId
				and 'COMPANY_ACCOUNT_CLAIM_REFERENCE' not in indices(bi.details)
				and bi.billing.patientCase.registryType = :patientType
				and 
				(
					lower(bi.billing.patient.fullName) like concat('%', :filter ,'%')
					or lower(bi.recordNo)  like concat('%',:filter,'%')
					or lower(bi.billing.patientCase.caseNo)  like concat('%',:filter,'%')
			  	)
			  	and 
					bi.itemType in :itemTypes
				and 
			  		bi.status = :status	
				""",
						page,
						size,
						[
								companyId: companyId,
								patientType     : patientType,
								itemTypes		: billingTypes.collect { BillingItemType.valueOf(it) },
								filter          : filter,
								status          : BillingItemStatus.ACTIVE
						]

				)
			}
		}

		return result

	}



	@GraphQLQuery(name = "billingItemWithBSchNativeQuery")
	BillingItemWithBsDTOPage billingItemWithBSchNativeQuery(
			@GraphQLArgument(name = "account") String account,
			@GraphQLArgument(name = "itemTypes") List<String> itemTypes,
			@GraphQLArgument(name = "patientType") List<String> patientType,
			@GraphQLArgument(name = "dateType") String dateType,
			@GraphQLArgument(name = "dateStart") String dateStart,
			@GraphQLArgument(name = "dateEnd") String dateEnd,
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "pageSize") Integer pageSize){

		BillingItemWithBsDTOPage billingItemWithBsDTOPage = new BillingItemWithBsDTOPage()
		billingItemWithBsDTOPage.content = entityManager.createNativeQuery("""
			select 
				cast("rowNum" as int) as "rowNum" ,
				cast("id" as varchar) as "id",
				"transactionDate" as "transactionDate",
				"recordNo" as "recordNo",
				"itemType" as "itemType",
				"description" as "description",
				cast("billing" as varchar) as "billing",
				"credit" as "credit",
				cast("fullCount" as int)  as "fullCount"
			from
				(select 
					ROW_NUMBER () OVER (ORDER BY bi.record_no asc) as "rowNum",
					cast(bi.id as varchar) as "id",
					bi.record_no as "recordNo",
					bi.item_type as "itemType",
					bi.description as "description",
					to_char(date(bi.transaction_date + interval '8 hour'),'YYYY-MM-DD') as "transactionDate",
					jsonb_build_object(
					'id',cast(b.id as varchar),
					'billingNo',b.billing_no,
					'patient',jsonb_build_object(
						'id',cast(p.id as varchar),
						'fullName',CONCAT(p.last_name ,', ',p.first_name),
						'lastName',p.last_name,
						'firstName',p.first_name
					),
					'patientCase', jsonb_build_object(
						'id',cast(c.id as varchar),
						'caseNo',cast(c.case_no as varchar) ,
						'dischargedDatetime',to_char(date(c.discharged_datetime + interval '8 hour'),'YYYY-MM-DD'),
						'admissionDatetime',to_char(date(c.admission_datetime + interval '8 hour'),'YYYY-MM-DD'),
						'registryType',c.registry_type,
						'createdDate', c.created_date
					) 
					) as "billing",
					bi.credit as "credit",
					count(*) OVER() AS "fullCount"
					from billing.billing_item bi 
					left join billing.billing b on b.id = bi.billing 
					left join billing.billing_item_details bid on bi.id = bid.billingitem  and bid.field_name = 'COMPANY_ACCOUNT_ID' and cast(bid.field_value as uuid) = cast(:account as uuid)
					left join pms.patients p on p.id = b.patient 
					left join pms.cases c on c.id = b.patient_case 
					left join accounting.billing_schedule_items bsi on bsi.billing_item_id = bi.id and bsi.is_voided is not true
				where 
					bid.id is not null and
					bsi.billing_item_id is null
					and
						c.registry_type in (:patientType)
					and
						bi.item_type in (:itemTypes)
					and 
					bi.status = 'ACTIVE'
					and
						case 
						when :dateType = 'DISCHARGED' then to_char(date(c.discharged_datetime + interval '8 hour'),'YYYY-MM-DD') between to_char(cast(:dateStart as date),'YYYY-MM-DD') and to_char(cast(:dateEnd as date),'YYYY-MM-DD')
						when :dateType = 'ADMISSION' then to_char(date(c.admission_datetime + interval '8 hour'),'YYYY-MM-DD') between to_char(cast(:dateStart as date),'YYYY-MM-DD') and to_char(cast(:dateEnd as date),'YYYY-MM-DD')
						when :dateType = 'TRANSACTION_DATE' then to_char(date(bi.transaction_date + interval '8 hour'),'YYYY-MM-DD') between to_char(cast(:dateStart as date),'YYYY-MM-DD') and to_char(cast(:dateEnd as date),'YYYY-MM-DD')
						else c.created_date is not null 
						end
					and (lower(concat(p.last_name,', ',p.first_name)) like lower(concat('%',:search,'%')))
				) as temptable
			where
			case when :pageSize > 0 then  "rowNum" > :page*:pageSize  and "rowNum" <= (:page*:pageSize+:pageSize) else "rowNum" <= "fullCount"  end
			""")
				.setParameter('search',search)
				.setParameter('account',account)
				.setParameter('itemTypes',itemTypes)
				.setParameter('patientType',patientType)
				.setParameter('dateType',dateType)
				.setParameter('dateStart',dateStart)
				.setParameter('dateEnd',dateEnd)
				.setParameter('page',page)
				.setParameter('pageSize',pageSize)
				.unwrap(NativeQuery.class)
				.setResultTransformer(new BillingScheduleTransform())
				.getResultList();
		billingItemWithBsDTOPage.totalSize = billingItemWithBsDTOPage.content[0] ?  billingItemWithBsDTOPage.content[0].fullCount : 0
		billingItemWithBsDTOPage.page = page
		billingItemWithBsDTOPage.size = pageSize
		return billingItemWithBsDTOPage
	}


}
