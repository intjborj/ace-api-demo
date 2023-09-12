package com.hisd3.hismk2.graphqlservices.billing

import com.hisd3.hismk2.dao.price_tier.ItemPriceControlDao
import com.hisd3.hismk2.domain.billing.ItemPriceControl
import com.hisd3.hismk2.domain.inventory.PurchaseOrder
import com.hisd3.hismk2.graphqlservices.types.GraphQLResVal
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.rest.dto.ItemCheckDTO
import com.hisd3.hismk2.rest.dto.ItemPriceControlDto
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

import javax.transaction.Transactional

@TypeChecked
@Component
@GraphQLApi
class ItemPriceControlService {
	
	@Autowired
	ItemPriceControlDao itemPriceControlDao
	
	@GraphQLQuery(name = "getAllPriceControlItems")
	Page<ItemPriceControlDto> getAllPriceControlItems(
			@GraphQLArgument(name = "tierId") String tierId,
			@GraphQLArgument(name = "group") String group,
			@GraphQLArgument(name = "costGroup") String costGroup,
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	
	) {
		return itemPriceControlDao.getAllPriceControlItems(tierId, group, costGroup, filter, page, size)
	}
	
	@GraphQLMutation
	List<ItemCheckDTO> itemPriceCheck(@GraphQLArgument(name = "fields") Map<String, Object> fields) {
		itemPriceControlDao.validateItemPriceList(fields)
	}
	
	@GraphQLMutation
	def addPriceControl(
			@GraphQLArgument(name = "itemId") String itemId,
			@GraphQLArgument(name = "tierDetail") String tierDetail,
			@GraphQLArgument(name = "amountValue") String amountValue,
			@GraphQLArgument(name = "percentageValue") String percentageValue
	) {
		itemPriceControlDao.updatePriceControl(itemId, tierDetail, amountValue as BigDecimal, percentageValue as BigDecimal)
	}
	
	@GraphQLMutation
	def toggleLockItemPriceControl(
			@GraphQLArgument(name = "itemId") String itemId,
			@GraphQLArgument(name = "tierDetail") String tierDetail
	) {
		itemPriceControlDao.toggleLockItemPriceControl(itemId, tierDetail)
	}
	
	@GraphQLMutation
	List<ItemPriceControl> massUpdateItemPrices(
			@GraphQLArgument(name = "tierDetail") String tierDetail,
			@GraphQLArgument(name = "group") String group,
			@GraphQLArgument(name = "costGroup") String costGroup,
			@GraphQLArgument(name = "percentageValue") String percentageValue
	) {
		return itemPriceControlDao.massUpdatePrices(tierDetail, group, costGroup, percentageValue as BigDecimal)
	}
	
	@GraphQLMutation
	@Transactional(rollbackOn = Exception)
	GraphQLRetVal<Boolean> updateItemPrices(
			@GraphQLArgument(name = "itemId") String itemId
	) {
		try{
			itemPriceControlDao.updateItemPrices(itemId, null)
			return new GraphQLRetVal<Boolean>(true,true, "Prices successfully generated")
		}catch(e){
			return new GraphQLRetVal<Boolean>(false,false,"Something went wrong. Please contact administrator")
		}
	}
	
}
