package com.hisd3.hismk2.graphqlservices.billing

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.dao.price_tier.ItemPriceControlDao
import com.hisd3.hismk2.dao.price_tier.ServicePriceControlDao
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.billing.PriceTierDetail
import com.hisd3.hismk2.domain.billing.PriceTierModifier
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.billing.PriceTierDetailRepository
import com.hisd3.hismk2.repository.billing.PriceTierModifierRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.security.SecurityUtils
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class PriceTierModifiersService {

	@Autowired
	UserRepository userRepository

	@Autowired
	EmployeeRepository employeeRepository

	@Autowired
	PriceTierModifierRepository priceTierModifierRepository

	@Autowired
	PriceTierDetailRepository priceTierDetailRepository
	
	@Autowired
	ItemPriceControlDao itemPriceControlDao
	
	@Autowired
	ServicePriceControlDao servicePriceControlDao
	
	@Autowired
	ObjectMapper objectMapper
	
	@GraphQLQuery(name = "getPriceTierModifiers", description = "Get all price tier modifiers")
	Page<PriceTierModifier> getPriceTierModifiers(
			@GraphQLArgument(name = "type") String type,
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "pageSize") Integer pageSize
	) {
		return priceTierModifierRepository.getPriceTierModifiers(filter, type, new PageRequest(page, pageSize, Sort.Direction.ASC, "fromCost"))
	}

	@GraphQLMutation
	GraphQLRetVal<String> copyPriceTier(
			@GraphQLArgument(name = "tierId") String tierId
	) {

		def tier = priceTierDetailRepository.findById(UUID.fromString(tierId)).get()
		List<PriceTierModifier> tempModifiers = []

		if(tier) {
			def modifiers = priceTierModifierRepository.getPriceTierModifierByTier(tier.id)

			if(modifiers) {
				PriceTierDetail newTier = new PriceTierDetail()
				newTier.tierCode = tier.tierCode + '_COPY'
				newTier.description = tier.description + '_COPY'
				newTier.registryType = tier.registryType
				newTier.accommodationType = tier.accommodationType
				newTier.roomTypes = tier.roomTypes
				newTier.isVatable = tier.isVatable
				newTier.targetAudience = tier.targetAudience
				newTier.forSenior = tier.forSenior

				modifiers.each {
				it->
					PriceTierModifier newMod = new PriceTierModifier()
					newMod.priceTierDetail = newTier
					newMod.categoryType = it.categoryType
					newMod.fromCost = it.fromCost
					newMod.toCost = it.toCost
					newMod.percentageValue = it.percentageValue

					def username = SecurityUtils.currentLogin()
					def user = userRepository.findOneByLogin(username)
					def employee = employeeRepository.findOneByUser(user)

					newMod.employee = employee

					tempModifiers.add(newMod)
				}

				if(tempModifiers) {
					priceTierDetailRepository.save(newTier)
 					priceTierModifierRepository.saveAll(tempModifiers)
				}
			}
		}

		return new GraphQLRetVal<String>("Ok", true)
	}
	
	@GraphQLMutation
	GraphQLRetVal<String> executeMassModifier(
			@GraphQLArgument(name = "type") String type,
			@GraphQLArgument(name = "modifierId") String modifierId
	) {
		def modifier = priceTierModifierRepository.findById(UUID.fromString(modifierId)).get()
		
		if (modifier) {
			def costGroup = modifier.fromCost + '-' + modifier.toCost
			//I could have used != "SERVICES" but this is only to be safe at this point in time.
			if (type == "MEDICINES" || type == "SUPPLIES") {
				itemPriceControlDao.massUpdatePrices(modifier.priceTierDetail.id.toString(), type, costGroup, modifier.percentageValue)
			} else if (type == "SERVICES") {
				def tier = modifier.priceTierDetail
				servicePriceControlDao.massUpdateServicePrices(tier.id.toString(), "", costGroup, modifier.percentageValue)
			}
		}
		
		return new GraphQLRetVal<String>("Ok", true)
	}
}
