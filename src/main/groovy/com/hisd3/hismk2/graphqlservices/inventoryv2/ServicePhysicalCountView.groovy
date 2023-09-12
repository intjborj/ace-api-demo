package com.hisd3.hismk2.graphqlservices.inventoryv2

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.inventory.PhysicalCount
import com.hisd3.hismk2.domain.inventory.PhysicalCountView
import com.hisd3.hismk2.domain.inventory.PhysicalTransaction
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.inventory.InventoryLedgerService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
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
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.Instant

@Service
@GraphQLApi
@Transactional(rollbackFor = Exception.class)
class ServicePhysicalCountView extends AbstractDaoService<PhysicalCountView> {

	ServicePhysicalCountView() {
		super(PhysicalCountView.class)
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

	@GraphQLQuery(name = "getPhyItemCountById")
	PhysicalCountView getPhyItemCountById(
			@GraphQLArgument(name = "id") UUID id
	){
		if(id){
			return findOne(id)
		}else{
			return null
		}

	}

	@GraphQLQuery(name = "getPhysicalCountByCode")
	PhysicalCountView getPhysicalCountByCode(
			@GraphQLArgument(name = "code") String code
	) {

		String query = '''Select p from PhysicalCountView p where p.sku=:code'''

		Map<String, Object> params = new HashMap<>()
		params.put('code', code)

		def result = createQuery(query, params).resultList
		if(result){
			return result.first()
		}else{
			return null
		}


	}

	@GraphQLQuery(name = "physicalCountViewPage")
	Page<PhysicalCountView> physicalTransPage(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "tag") String tag,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "pageSize") Integer pageSize
	) {

		String query = '''Select p from PhysicalCountView p where
						(lower(p.descLong) like lower(concat('%',:filter,'%')) or
						lower(p.sku) like lower(concat('%',:filter,'%')))
						and p.physicalTransaction.id=:phyId '''

		String countQuery = '''Select count(p) from PhysicalCountView p where
						(lower(p.descLong) like lower(concat('%',:filter,'%')) or
						lower(p.sku) like lower(concat('%',:filter,'%')))
						and p.physicalTransaction.id=:phyId '''

		Map<String, Object> params = new HashMap<>()
		params.put('phyId', id)
		params.put('filter', filter)

		if(tag.equalsIgnoreCase("WITH_VARIANCE")){
			query += ''' and (p.variance != 0 and p.unitCost > 0)'''
			countQuery += ''' and (p.variance != 0 and p.unitCost > 0)'''
		}else if(tag.equalsIgnoreCase("ZERO_VARIANCE")){
			query += ''' and (p.variance = 0 and p.unitCost > 0)'''
			countQuery += ''' and (p.variance = 0 and p.unitCost > 0)'''
		}else if(tag.equalsIgnoreCase("NO_COST")){
			query += ''' and (p.unitCost <= 0)'''
			countQuery += ''' and (p.unitCost <= 0)'''
		}


		query += ''' ORDER BY p.descLong ASC'''

		Page<PhysicalCountView> result = getPageable(query, countQuery, page, pageSize, params)
		return result
	}

	@GraphQLQuery(name = "countPhysicalItems")
	Long countPhysicalItems(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "tag") String tag
	) {

		String query = '''Select count(p) from PhysicalCountView p where p.physicalTransaction.id=:phyId '''

		Map<String, Object> params = new HashMap<>()
		params.put('phyId', id)

		if(tag.equalsIgnoreCase("WITH_VARIANCE")){
			query += ''' and (p.variance != 0 and p.unitCost > 0)'''
		}else if(tag.equalsIgnoreCase("ZERO_VARIANCE")){
			query += ''' and (p.variance = 0 and p.unitCost > 0)'''
		}else if(tag.equalsIgnoreCase("NO_COST")){
			query += ''' and (p.unitCost <= 0)'''
		}

		def count = getCount(query, params)
		return count

	}

	@GraphQLQuery(name = "postPhysicalCountItems")
	List<PhysicalCountView> postPhysicalCountItems(
			@GraphQLArgument(name = "id") UUID id
	) {

		String query = '''Select p from PhysicalCountView p where p.physicalTransaction.id=:phyId 
		and p.variance = 0 and p.unitCost > 0 and (p.isPosted = false or p.isPosted is null)'''

		Map<String, Object> params = new HashMap<>()
		params.put('phyId', id)

		def result = createQuery(query, params).resultList.sort{it.descLong}
		return result

	}

	@GraphQLQuery(name = "getPhysicalItemById")
	List<PhysicalCountView> getPhysicalItemById(
			@GraphQLArgument(name = "id") UUID id
	) {

		String query = '''Select p from PhysicalCountView p where p.physicalTransaction.id=:phyId '''

		Map<String, Object> params = new HashMap<>()
		params.put('phyId', id)

		def result = createQuery(query, params).resultList.sort{it.descLong}
		return result

	}

	@GraphQLQuery(name = "getPhysicalItem")
	PhysicalCountView getPhysicalItem(
			@GraphQLArgument(name = "id") UUID id
	) {

		findOne(id)

	}



}
