package com.hisd3.hismk2.graphqlservices.inventoryv2

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.inventory.PhysicalTransaction
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.inventory.InventoryLedgerService
import com.hisd3.hismk2.graphqlservices.inventory.PhysicalCountService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.inventory.PhysicalCountRepository
import com.hisd3.hismk2.rest.dto.LedgerDto
import com.hisd3.hismk2.rest.dto.RawLedgerDto
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.Duration
import java.time.Instant

@Service
@GraphQLApi
@Transactional(rollbackFor = Exception.class)
class ServicePhysicalTransaction extends AbstractDaoService<PhysicalTransaction> {

	ServicePhysicalTransaction() {
		super(PhysicalTransaction.class)
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

	@Autowired
	PhysicalCountService physicalCountService

	@Autowired
	PhysicalCountRepository physicalCountRepository


	@GraphQLQuery(name = "physicalTransactionById")
	PhysicalTransaction physicalTransactionById(
			@GraphQLArgument(name = "id") UUID id
	) {
		if(id){
			findOne(id)
		}else{
			return null
		}

	}

	@GraphQLQuery(name = "physicalTransPage")
	Page<PhysicalTransaction> physicalTransPage(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "pageSize") Integer pageSize
	) {

		String query = '''Select p from PhysicalTransaction p where
						(lower(p.transNo) like lower(concat('%',:filter,'%')) or
						lower(p.remarksNotes) like lower(concat('%',:filter,'%')))
						and p.department.id=:departmentid '''

		String countQuery = '''Select count(p) from PhysicalTransaction p where
						(lower(p.transNo) like lower(concat('%',:filter,'%')) or
						lower(p.remarksNotes) like lower(concat('%',:filter,'%')))
						and p.department.id=:departmentid '''

		Map<String, Object> params = new HashMap<>()
		params.put('departmentid', id)
		params.put('filter', filter)


		query += ''' ORDER BY p.transNo DESC'''

		Page<PhysicalTransaction> result = getPageable(query, countQuery, page, pageSize, params)
		return result
	}

	@GraphQLQuery(name = "physicalTransList")
	List<PhysicalTransaction> physicalTransList(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "id") UUID id
	) {

		String query = '''Select p from PhysicalTransaction p where
						(lower(p.transNo) like lower(concat('%',:filter,'%')) or
						lower(p.remarksNotes) like lower(concat('%',:filter,'%')))
						and p.department.id=:departmentid '''


		Map<String, Object> params = new HashMap<>()
		params.put('departmentid', id)
		params.put('filter', filter)


		query += ''' ORDER BY p.transNo DESC'''

		return createQuery(query, params).resultList
	}

	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertPhysicalTrans")
	PhysicalTransaction upsertPhysicalTrans(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "id") UUID id
	) {
		def b = upsertFromMap(id, fields, { PhysicalTransaction entity, boolean forInsert ->
			if (forInsert) {
				entity.transNo = generatorService.getNextValue(GeneratorType.PHY_COUNT, {
					return "PHY-" + StringUtils.leftPad(it.toString(), 6, "0")
				})
				entity.status = "DRAFT"
			}
			entity.transDate = entity.transDate
		})

		if(id){
			physicalCountService.loadPhysicalCountItems(b.id, "UPDATE")
		}

		return b
	}

	//post inventory for Physical Transaction
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "postPhysicalCountTransaction")
	GraphQLRetVal<Boolean> postPhysicalCountTransaction(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "items") ArrayList<Map<String, Object>> items
	) {
		//get parents

		def physical = findOne(id);
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
					def res = inventoryLedgerService.postInventoryGlobal(item)

					//update physical count items to posted
					def phyId = UUID.fromString(it.id)
					physicalCountService.updateStatusPhysicalCount(phyId,true, res.id)
				}
			}

			//post to accounting --to do accounting entries

			//end post to account
			//update parent
			//physical.posted = true
			//physical.status = "POSTED"
			//physical.postedBy= SecurityUtils.currentLogin()
			//physical.postedLedger= null //to be updated for accounting
			//save(physical)
			return new GraphQLRetVal<Boolean>(true,  true,"Items are now posted to ledger")
		} catch (Exception e) {
			throw new Exception("Something was Wrong : " + e)
		}

	}

	@GraphQLMutation(name = "voidPhysical")
	GraphQLRetVal<Boolean> voidPhysical(
			@GraphQLArgument(name = "id") UUID id
	) {
		//get parents
		def phy = findOne(id);
		def postItems = physicalCountRepository.getPhysicalItemsPosted(id)
		//get items to void
		try {
			if(postItems){
				postItems.each {
					//update physical count items to false
					physicalCountService.updateStatusPhysicalCount(it.id,false, null)
				}
			}
			//void inventory to inventory ledger
			inventoryLedgerService.voidInventoryGlobal(phy.transNo)
			//void to accounting --to do accounting entries

			//end void to account
			//update parent
			//phy.posted = false
			//phy.status = "DRAFT"
			//phy.postedBy= null
			//phy.postedLedger= null //to be updated for accounting
			//save(phy)
			return new GraphQLRetVal<Boolean>(true,  true,"Items are now voided to ledger")
		} catch (Exception e) {
			throw new Exception("Something was Wrong : " + e)
		}

	}

	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "updateStatusPhysicalTrans")
	GraphQLRetVal<Boolean> updateStatusPhysicalTrans(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "status") String status
	) {

		try {
			//get parents
			def physical = findOne(id);
			physical.posted = status.equalsIgnoreCase("COMPLETED")
			physical.status = status
			physical.postedBy= status.equalsIgnoreCase("COMPLETED") ? SecurityUtils.currentLogin() : null
			physical.postedLedger= null //to be updated for accounting
			save(physical)

			return new GraphQLRetVal<Boolean>(true,  true,"Physical Count ${physical.transNo} status updated.")
		} catch (Exception e) {
			throw new Exception("Something was Wrong : " + e)
		}

	}

	@GraphQLMutation(name = "voidPhysicalById")
	GraphQLRetVal<Boolean> voidPhysicalById(
			@GraphQLArgument(name = "id") UUID id
	) {
		//get parents
		def phyItem = physicalCountRepository.findById(id).get()
		//get items to void
		try {
			if(phyItem.refLedgerId){
				//void inventory to inventory ledger
				inventoryLedgerService.voidInventoryGlobalById(phyItem.refLedgerId)

				physicalCountService.updateStatusPhysicalCount(phyItem.id,false, null)

				return new GraphQLRetVal<Boolean>(true,  true,"Item are now voided to ledger")
			}else{
				return new GraphQLRetVal<Boolean>(false,  false,"Cannot void physical count. Please contact administrator.")
			}

		} catch (Exception e) {
			throw new Exception("Something was Wrong : " + e)
		}

	}


}
