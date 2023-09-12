package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.AccountReceivableItems
import com.hisd3.hismk2.domain.accounting.BillingSchedule
import com.hisd3.hismk2.domain.accounting.BillingScheduleItems
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.billing.CompanyAccountServices
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.accounting.BillingScheduleItemsRepository
import com.hisd3.hismk2.repository.accounting.BillingScheduleRepository
import com.hisd3.hismk2.services.EntityObjectMapperService
import com.hisd3.hismk2.services.GeneratorService
import groovy.json.JsonSlurper
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Component

import javax.transaction.Transactional
import java.sql.ResultSet
import java.sql.SQLException

@Canonical
class GuaranteedContentDto {
	String no
	String dischargeDate
	String finalSoa
	String patient
	String billingNo
	String recordNo
	BigDecimal hci = BigDecimal.ZERO
	BigDecimal pf = BigDecimal.ZERO
	String billingId
	String billingItemId
	String description
	String patientId
	String caseId
	String case_no
	String billingSchedItemId
	String billingSchedAmount
	String approvalCode
	String billingSchedType
}

@Canonical
class GuaranteedDto {
	List<GuaranteedContentDto> content
	Integer totalRows
	Integer totalPage
}


@Canonical
class BScheduleEditPage {
	BillingSchedule billingSchedule
	List<BillingScheduleItems> billingScheduleItems
}

@Component
@GraphQLApi
class BillingScheduleItemsServices extends AbstractDaoService<BillingScheduleItems> {

	BillingScheduleItemsServices() {
		super(BillingScheduleItems.class)
	}

	@Autowired
	EntityObjectMapperService entityObjectMapperService

	@Autowired
	BillingScheduleItemsRepository billingScheduleItemRepository

	@Autowired
	BillingScheduleRepository billingScheduleRepository

	@Autowired
	BillingScheduleServices billingScheduleServices

	@Autowired
	JdbcTemplate jdbcTemplate

	@GraphQLQuery(name = "billingScheduleItemsPerCompanyById")
	Page<BillingScheduleItems> getBillingScheduleItemsPerCompanyById(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "billingSchedule") UUID billingSchedule,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		getPageable(
				"""
              	Select c from BillingScheduleItems c  where c.billingSchedule.id = :billingSchedule and (c.isVoided = FALSE OR c.isVoided IS NULL) and (lower(c.billing.billingNo) like lower(concat('%',:filter,'%')) or lower(c.billing.patient.fullName) like lower(concat('%',:filter,'%')) )  order by c.billing.billingNo
				""",
				"""
			 	Select count(c) from BillingScheduleItems c  where c.billingSchedule.id = :billingSchedule  and (c.isVoided = FALSE OR c.isVoided IS NULL) and (lower(c.billing.billingNo) like lower(concat('%',:filter,'%')) or lower(c.billing.patient.fullName) like lower(concat('%',:filter,'%')) )
				""",
				page,
				size,
				[
						billingSchedule: billingSchedule,
						filter         : filter,
				]
		)

	}

	@Transactional
	@GraphQLMutation
	BillingScheduleItems removeBillScheduleItems(
			@GraphQLArgument(name = "id") UUID id
	) {
		def billingScheduleItem = billingScheduleItemRepository.findById(id).get()
		billingScheduleItem.isVoided = true
		billingScheduleItemRepository.save(billingScheduleItem)
		def billingSchedule = billingScheduleRepository.findById(billingScheduleItem.billingSchedule.id).get()
		def currentTotal = billingScheduleItemRepository.getSumBillingSchedule(billingSchedule.id)
		billingSchedule.totalReceivableAmount = currentTotal
		billingScheduleRepository.save(billingSchedule)
		return billingScheduleItem
	}

	@Transactional
	@GraphQLMutation
	BillingScheduleItems updateBillingScheduleItem(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		def returnData = null
		if (id) {
			def newItem = findOne(id)
			entityObjectMapperService.updateFromMap(newItem, fields)
			save(newItem)
			returnData = newItem
		}

		return returnData
	}

	@Transactional
	@GraphQLMutation
	GraphQLRetVal<String> addMultipleBillingScheduleItem(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fieldsItems") ArrayList<Map<String, Object>> fieldsItems
	) {
		def returnData = null
		try {
			if (id) {
				def bill = billingScheduleServices.findOne(id)
				if(bill) {
					if(fieldsItems){
						fieldsItems.each {
							it ->
									def newBill = new BillingScheduleItems()
									newBill.billingSchedule = bill
									entityObjectMapperService.updateFromMap(newBill, it)
									billingScheduleItemRepository.save(newBill)
						}
						return new GraphQLRetVal<String>('success',true,'Successfully updated.')
					}
				}
				return new GraphQLRetVal<String>('error',true,'No parameter.')
			}
			else{
				return new GraphQLRetVal<String>('error',true,'No parameter.')
			}
		}
		catch (e){
			return new GraphQLRetVal<String>('error',true,e.message)
		}
	}

	@GraphQLQuery(name = "billingScheduleItemsPerParent")
	List<BillingScheduleItems> billingScheduleItemsPerParent(@GraphQLArgument(name = "id") UUID id
	) {
		createQuery("""select b from BillingScheduleItems b where  b.billingSchedule.id = :id""",
				[
						id: id,
				] as Map<String, Object>).resultList
	}

	@GraphQLQuery(name = "guaranteedListFiltered")
	GuaranteedDto guaranteedListFiltered(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "pageSize") Integer pageSize,
			@GraphQLArgument(name = "pageNo") Integer pageNo
	) {

		List<GuaranteedDto> gDto = new  ArrayList<GuaranteedDto>()

		gDto = jdbcTemplate.query("""
			select *  from accounting.arguaranteedList(?::uuid,?::integer,?::integer)
			""", new RowMapper<GuaranteedDto>() {
			@Override
			GuaranteedDto mapRow(ResultSet rs, int rowNum) throws SQLException {
				def jsonResult = new JsonSlurper().parseText( rs.getString("contents"))
				List<GuaranteedContentDto> listG = new ArrayList<GuaranteedContentDto>()
				jsonResult.each {
					it ->
						listG.push(new GuaranteedContentDto(
								it['no'] as String,
								it['dischargeDate'] as String,
								it['finalSoa'] as String,
								it['patient'] as String,
								it['billingNo'] as String,
								it['recordNo'] as String,
								new BigDecimal(it['hci'] as String),
								new BigDecimal(it['pf'] as String),
								it['billingId'] as String,
								it['billingItemId'] as String,
								it['description'] as String,
								it['patientId'] as String,
								it['caseId'] as String,
								it['case_no'] as String,
								it['billingSchedItemId'] as String,
								it['billingSchedAmount'] as String,
								it['approvalCode'] as String,
								it['billingSchedType'] as String
						))
				}

				def details = new GuaranteedDto(
						listG,
						rs.getInt("totalrows"),
						rs.getInt("totalpages")
				)
				return details;
			}
		},
				id,
				pageSize,
				pageNo
		)

		return  gDto[0]
	}

	@GraphQLQuery(name="getBSchItemPerBillItem")
	BillingScheduleItems getBSchItemPerBillItem (
			@GraphQLArgument(name = "billingItemId") UUID billingItemId,
			@GraphQLArgument(name = "bSchId") UUID bSchId
	){
		List<BillingScheduleItems> billingScheduleItems
		billingScheduleItems = createQuery("Select bs from BillingScheduleItems bs where bs.billingItem.id = :id and bs.billingSchedule.id = :bSchId and (bs.isVoided = FALSE OR bs.isVoided IS NULL) ", ["id": billingItemId,"bSchId":bSchId]).resultList
		return  billingScheduleItems[0]
	}

	@GraphQLQuery(name="searchBSchItemPerBillItemIdAndAmt")
	List<BillingScheduleItems> searchBSchItemPerBillItemIdAndAmt (
			@GraphQLArgument(name = "billingItemId") UUID billingItemId,
			@GraphQLArgument(name = "compId") UUID compId,
			@GraphQLArgument(name = "amount") BigDecimal amount
	){
		return createQuery("Select bs from BillingScheduleItems bs " +
				" left join BillingSchedule b on b.id = bs.billingSchedule.id and b.companyAccount.id = :compId " +
				" where bs.arTransfer is null and b.status = 'posted' and bs.billingItem.id = :id and bs.amount = :amount and (bs.isVoided = FALSE OR bs.isVoided IS NULL) ",
				[
						"id": billingItemId,
						"compId":compId,
						"amount":amount
				]
		).resultList
	}


	@GraphQLQuery(name = "getBillingScheduleDetails")
	BScheduleEditPage getBillingScheduleDetails(@GraphQLArgument(name = "id") UUID id
	) {
		BScheduleEditPage bScheduleEditPage = new BScheduleEditPage()
		def bs = billingScheduleServices.createQuery("""select b from BillingSchedule b where  b.id = :id""",
				[
						id: id,
				] as Map<String, Object>).singleResult as BillingSchedule
		if(bs)
			bScheduleEditPage.billingSchedule = bs

		def bsi = createQuery("""select b from BillingScheduleItems b where  b.billingSchedule.id = :id and (b.isVoided = FALSE OR b.isVoided IS NULL)""",
				[
						id: id,
				] as Map<String, Object>).resultList.sort{it.billingItem.transactionDate}
		if(bsi)
			bScheduleEditPage.billingScheduleItems = bsi

		return  bScheduleEditPage
	}
}

