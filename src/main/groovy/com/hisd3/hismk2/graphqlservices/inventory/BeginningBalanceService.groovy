package com.hisd3.hismk2.graphqlservices.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.inventory.BeginningBalance
import com.hisd3.hismk2.repository.inventory.BeginningBalanceRepository
import com.hisd3.hismk2.rest.InventoryResource
import com.hisd3.hismk2.rest.dto.QueryErrorException
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

@Component
@GraphQLApi
@TypeChecked
class BeginningBalanceService {
	
	@Autowired
	BeginningBalanceRepository beginningBalanceRepository
	
	@Autowired
	InventoryResource inventoryResource
	
	@Autowired
	ObjectMapper objectMapper
	
	@Autowired
	GeneratorService generatorService

	@Autowired
	InventoryLedgerService inventoryLedgerService
	
	@GraphQLQuery(name = "beginningListByItem", description = "List of Beginning Balance by Item")
	List<BeginningBalance> getBeginningById(@GraphQLArgument(name = "item") UUID id) {
		return beginningBalanceRepository.getBeginningById(id).sort { it.createdDate }.reverse(true)
	}
	//
	//MUTATION
	
	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation(name = "beginningBalanceInsert", description = "insert BEG")
	BeginningBalance beginningBalanceInsert(
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		BeginningBalance insert = new BeginningBalance()
		def data
		def beg = objectMapper.convertValue(fields, BeginningBalance)
		try {
//			def check = inventoryResource.getLedger(beg.item.id as String, beg.department.id as String)
//			def checkIfExist = beginningBalanceRepository.getBeginningById(beg.item.id)
//			if (!check) {
//				if (!checkIfExist) {
					insert.refNum = generatorService.getNextValue(GeneratorType.BEGINNING) { Long no ->
						'BEG-' + StringUtils.leftPad(no.toString(), 6, "0")
					}
					insert.dateTrans = beg.dateTrans
					insert.item = beg.item
					insert.department = beg.department
					insert.quantity = beg.quantity
					insert.unitCost = beg.unitCost
					insert.isPosted = false
					insert.isCancel = false
					data = beginningBalanceRepository.save(insert)
//				} else {
//					data = beg
//				}


//			} else {
//				data = beg
//			}
		} catch (Exception e) {
			throw new QueryErrorException("Something was Wrong : " + e)
		}
		return data
	}


}
