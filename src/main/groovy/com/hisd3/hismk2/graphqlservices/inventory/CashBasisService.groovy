package com.hisd3.hismk2.graphqlservices.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.inventory.Inventory
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.inventory.CashBasis
import com.hisd3.hismk2.domain.inventory.CashBasisItem
import com.hisd3.hismk2.repository.inventory.CashBasisItemRepository
import com.hisd3.hismk2.repository.inventory.CashBasisRepository
import com.hisd3.hismk2.repository.inventory.ItemRepository
import com.hisd3.hismk2.rest.dto.CashBasisDto
import com.hisd3.hismk2.rest.dto.CashBasisITemDto
import com.hisd3.hismk2.rest.dto.QueryErrorException
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
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import javax.transaction.Transactional

@Component
@GraphQLApi
@TypeChecked
class CashBasisService {
	
	@Autowired
	CashBasisRepository cashBasisRepository

	@Autowired
	CashBasisItemRepository cashBasisItemRepository

	@Autowired
	ItemRepository itemRepository

	@Autowired
	GeneratorService generatorService

	@Autowired
	private ObjectMapper objectMapper


	@GraphQLQuery(name = "getCashBasisPatientList", description = "List of Cash Basis Patient Pageable")
	Page<CashBasis> getCashBasisPatientList(
			@GraphQLArgument(name = "departmentId") UUID departmentId,
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "pageSize") Integer pageSize
	) {
		return cashBasisRepository.getCashBasisPatientListPageable(departmentId,filter, new PageRequest(page, pageSize, Sort.Direction.ASC, "patient.fullName"))
	}


	@Transactional(rollbackOn = Exception)
	@GraphQLMutation(name = "upsertCashBasis")
	CashBasis upsertCashBasis(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "cashBasis") Map<String, Object> cashBasis,
			@GraphQLArgument(name = "cashBasisItems") ArrayList<Map<String, Object>> cashBasisItems
	) {
			def cashB = new CashBasis()
			def obj = objectMapper.convertValue(cashBasis, CashBasisDto.class)
			def items = cashBasisItems as ArrayList<CashBasisITemDto>

			if(id){
				cashB = cashBasisRepository.findById(id).get()

				items.each {
					it ->
					if(it.id){
						def newCBItems = cashBasisItemRepository.findById(UUID.fromString(it.id)).get()
						newCBItems.price = it.price
						newCBItems.quantity = it.quantity
						cashBasisItemRepository.save(newCBItems)
					}
					else {
						def newCBItems = new CashBasisItem()
						newCBItems.cashBasis = cashB
						newCBItems.item = itemRepository.findById(UUID.fromString(it.item)).get()
						newCBItems.price = it.price
						newCBItems.quantity = it.quantity
						newCBItems.type = it.type
						cashBasisItemRepository.save(newCBItems)
					}
				}

			}
			else {
				cashB.patient = obj.patient
				cashB.cashBasisNo = generatorService.getNextValue(GeneratorType.CASH_BASIS_NO) { Long no ->
					'CB-' + StringUtils.leftPad(no.toString(), 6, "0")
				}
				cashB.patientCase = obj.patientCase
				cashB.department = obj.department
				cashB.status = obj.status

				def cashBSave = cashBasisRepository.save(cashB)

				items.each {
					it->
					def newCBItems = new CashBasisItem()
					newCBItems.cashBasis = cashBSave
					newCBItems.item = itemRepository.findById(UUID.fromString(it.item)).get()
					newCBItems.price = it.price
					newCBItems.quantity = it.quantity
					newCBItems.type = it.type
					cashBasisItemRepository.save(newCBItems)
				}

			}

		return  cashB
	}

	@GraphQLQuery(name = "getCashBasisPatientById", description = "Get Patient Id")
	CashBasis getCashBasisPatientById(@GraphQLArgument(name = "id") UUID id)
	{
		return cashBasisRepository.findById(id).get()
	}
}
