package com.hisd3.hismk2.graphqlservices.inventoryv2

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.User
import com.hisd3.hismk2.domain.accounting.AccountConfig
import com.hisd3.hismk2.domain.accounting.AccountsPayable
import com.hisd3.hismk2.domain.accounting.Ledger
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.inventory.BeginningTransaction
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.inventory.InventoryLedgerService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.rest.dto.AccountPayableDetialsDto
import com.hisd3.hismk2.rest.dto.BeginningItemDto
import com.hisd3.hismk2.rest.dto.DepDto
import com.hisd3.hismk2.rest.dto.LedgerDto
import com.hisd3.hismk2.rest.dto.QueryErrorException
import com.hisd3.hismk2.rest.dto.RawLedgerDto
import com.hisd3.hismk2.rest.dto.TransTypeDto
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
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.Instant

@Service
@GraphQLApi
@Transactional(rollbackFor = Exception.class)
class ServiceBeginningTransaction extends AbstractDaoService<BeginningTransaction> {

	ServiceBeginningTransaction() {
		super(BeginningTransaction.class)
	}

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	GeneratorService generatorService

	@Autowired
	UserRepository userRepository

	@Autowired
	EmployeeRepository employeeRepository

	@Autowired
	ServiceBeginningDetails serviceBeginningDetails

	@Autowired
	InventoryLedgerService inventoryLedgerService

	@GraphQLQuery(name = "begById")
	BeginningTransaction begById(
			@GraphQLArgument(name = "id") UUID id
	) {
		if(id){
			findOne(id)
		}else{
			return null
		}

	}

	@GraphQLQuery(name = "beginningTransPage")
	Page<BeginningTransaction> findByFilters(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "pageSize") Integer pageSize
	) {

		String query = '''Select b from BeginningTransaction b where
						(lower(b.transNo) like lower(concat('%',:filter,'%')) or
						lower(b.remarksNotes) like lower(concat('%',:filter,'%')))
						and b.department.id=:departmentid '''

		String countQuery = '''Select count(b) from BeginningTransaction b where
						(lower(b.transNo) like lower(concat('%',:filter,'%')) or
						lower(b.remarksNotes) like lower(concat('%',:filter,'%')))
						and b.department.id=:departmentid '''

		Map<String, Object> params = new HashMap<>()
		params.put('departmentid', id)
		params.put('filter', filter)


		query += ''' ORDER BY b.transNo DESC'''

		Page<BeginningTransaction> result = getPageable(query, countQuery, page, pageSize, params)
		return result
	}

	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertBegTrans")
	BeginningTransaction upsertBegTrans(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "items") ArrayList<Map<String, Object>> items,
			@GraphQLArgument(name = "id") UUID id
	) {
		def beg = upsertFromMap(id, fields, { BeginningTransaction entity, boolean forInsert ->
			if (forInsert) {
				entity.transNo = generatorService.getNextValue(GeneratorType.BEGINNING, {
					return "BEG-" + StringUtils.leftPad(it.toString(), 6, "0")
				})
				entity.status = "DRAFT"
			}
		})

		def begDetails = items as ArrayList<BeginningItemDto>
		begDetails.each {
			it ->
				def item = objectMapper.convertValue(it.item, Item.class)
				def begDto = objectMapper.convertValue(it, BeginningItemDto.class)
				def dep = objectMapper.convertValue(it.department, DepDto.class)

				serviceBeginningDetails.upsertBegDetails(begDto, beg, item?.id, dep?.id)
		}

		return beg
	}

	//post inventory for beginning
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "postBeginningBalance")
	GraphQLRetVal<Boolean> postBeginningBalance(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "items") ArrayList<Map<String, Object>> items
	) {
		//get parents

		def beg = findOne(id);
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
							ledgerPhysical: 0,
							ledgerUnitCost: it.unitcost,
							isInclude: true,
					)
					inventoryLedgerService.postInventoryGlobal(item)

					//update beg items to posted
					def begItemId = UUID.fromString(it.id)
					serviceBeginningDetails.updateStatusBegItem(begItemId,true)
				}
			}

			//post to accounting --to do accounting entries

			//end post to account
			//update parent
			beg.posted = true
			beg.status = "POSTED"
			beg.postedBy= SecurityUtils.currentLogin()
			beg.postedLedger= null //to be updated for accounting
			save(beg)
			return new GraphQLRetVal<Boolean>(true,  true,"Items are now posted to ledger")
		} catch (Exception e) {
			return new GraphQLRetVal<Boolean>(false,  false, e?.message)
		}

	}

	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "voidBeginning")
	GraphQLRetVal<Boolean> voidBeginning(
			@GraphQLArgument(name = "id") UUID id
	) {
		//get parents
		def beg = findOne(id);
		def postItems = serviceBeginningDetails.getPostedBeginningItems(id)
		//get items to void
		try {
			if(postItems){
				postItems.each {
					//update beg items to false
					serviceBeginningDetails.updateStatusBegItem(it.id,false)
				}
			}
			//void inventory to inventory ledger
			inventoryLedgerService.voidInventoryGlobal(beg.transNo)
			//void to accounting --to do accounting entries

			//end void to account
			//update parent
			beg.posted = false
			beg.status = "DRAFT"
			beg.postedBy= null
			beg.postedLedger= null //to be updated for accounting
			save(beg)
			return new GraphQLRetVal<Boolean>(true,  true,"Items are now voided to ledger")
		} catch (Exception e) {
			return new GraphQLRetVal<Boolean>(false,  false, e?.message)
		}

	}

}
