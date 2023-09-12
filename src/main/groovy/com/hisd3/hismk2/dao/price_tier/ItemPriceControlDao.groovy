package com.hisd3.hismk2.dao.price_tier

import com.hisd3.hismk2.domain.billing.ItemPriceControl
import com.hisd3.hismk2.domain.billing.PriceTierDetail
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.repository.billing.ItemPriceControlRepository
import com.hisd3.hismk2.repository.billing.PriceTierDetailRepository
import com.hisd3.hismk2.repository.billing.PriceTierModifierRepository
import com.hisd3.hismk2.repository.inventory.ItemRepository
import com.hisd3.hismk2.rest.dto.ItemCheckDTO
import com.hisd3.hismk2.rest.dto.ItemPriceControlDto
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

class ItemPriceCheckDTO {
	UUID item
	UUID tier
}

@TypeChecked
@Service
@Transactional
class ItemPriceControlDao {
	
	@Autowired
	ItemPriceControlRepository itemPriceControlRepository
	
	@Autowired
	ItemRepository itemRepository
	
	@Autowired
	PriceTierDetailRepository priceTierDetailRepository
	
	@Autowired
	PriceTierModifierRepository priceTierModifierRepository
	
	Page<ItemPriceControlDto> getAllPriceControlItems(String tierId, String group, String costGroup, String filter, Integer page, Integer size) {
		
		Page<Item> allItems
		Pageable pageable = new PageRequest(page, size, Sort.Direction.ASC, "descLong")
		if (costGroup && costGroup.toUpperCase() != "ALL") {
			def cg = costGroup.split("-")
			
			def from = cg[0]
			def to = cg[1]
			
			if (to.toLowerCase() == "up") {
				to = 99999999.0
			}
			
			from = from as BigDecimal
			to = to as BigDecimal
			
			if (group == "MEDICINES")
				allItems = itemRepository.itemsByFilterAndCostRangePagedMedicines(filter, new PageRequest(page, size, Sort.Direction.ASC, "descLong"), from, to)
			else if (group == "SUPPLIES")
				allItems = itemRepository.itemsByFilterAndCostRangePagedNonMedicines(filter, new PageRequest(page, size, Sort.Direction.ASC, "descLong"), from, to)
			else if (group == "MEDICAL SUPPLIES")
				allItems = itemRepository.itemsByFilterAndCostRangePagedMedicalSupplies(filter, new PageRequest(page, size, Sort.Direction.ASC, "descLong"), from, to)
			else
				allItems = itemRepository.itemsByFilterAndCostRangePaged(filter, new PageRequest(page, size, Sort.Direction.ASC, "descLong"), from, to)
		} else {
			if (group == "MEDICINES")
				allItems = itemRepository.itemsByFilterPagedMedicines(filter, new PageRequest(page, size, Sort.Direction.ASC, "descLong"))
			else if (group == "SUPPLIES")
				allItems = itemRepository.itemsByFilterPagedNonMedicines(filter, new PageRequest(page, size, Sort.Direction.ASC, "descLong"))
			else if (group == "MEDICAL SUPPLIES")
				allItems = itemRepository.itemsByFilterPagedMedicalSupplies(filter, new PageRequest(page, size, Sort.Direction.ASC, "descLong"))
			else
				allItems = itemRepository.itemsByFilterPaged(filter, new PageRequest(page, size, Sort.Direction.ASC, "descLong"))
		}
		
		List<ItemPriceControlDto> allItemPrice = []
		
		if (tierId) {
			PriceTierDetail tier = priceTierDetailRepository.findById(UUID.fromString(tierId)).get()
			
			allItems.each {
				it ->
					ItemPriceControl item = itemPriceControlRepository.getItemByIdAndTier(
							UUID.fromString(tierId),
							it.id
					)
					
					if (it.active) {
						if (item) {
							ItemPriceControlDto ipcDto = new ItemPriceControlDto()
							ipcDto.item = it
							
							def unitCost = it.actualUnitCost ?: 0.0
							def basePrice = it.item_markup ? unitCost + (unitCost * (it.item_markup / 100)) : unitCost
							ipcDto.basePrice = basePrice ?: 0.0
							def sellPrice = 0.0
							
							if (item.amountValue > 0 && basePrice != 0.0) {
								if (tier.isVatable && (it.vatable && tier.vatRate)) {
									sellPrice = item.amountValue + (item.amountValue * (tier.vatRate / 100)) ?: 0.0
									ipcDto.percentageValue = item.percentageValue ?: 0.0
									ipcDto.amountValue = sellPrice
								} else {
									sellPrice = item.amountValue ?: 0.0
									ipcDto.percentageValue = item.percentageValue ?: 0.0
									ipcDto.amountValue = sellPrice
								}
								
								ipcDto.addon = ((sellPrice - basePrice) / basePrice) * 100
								ipcDto.margin = sellPrice - it.actualUnitCost
								ipcDto.totalMarkup = ((sellPrice - it.actualUnitCost) / it.actualUnitCost) * 100
								ipcDto.locked = item.locked
							} else {
								ipcDto.addon = 0.0
								ipcDto.percentageValue = 0.0
								ipcDto.amountValue = 0.0
								ipcDto.margin = 0.0
								ipcDto.totalMarkup = 0.0
								ipcDto.locked = item.locked
							}
							
							ipcDto.tierDetail = tier
							allItemPrice.add(ipcDto)
						} else {
							ItemPriceControlDto ipcDto = new ItemPriceControlDto()
							ipcDto.item = it
							
							def unitCost = it.actualUnitCost ?: 0.0
							def basePrice = it.item_markup ? unitCost + (unitCost * (it.item_markup / 100)) : unitCost
							
							ipcDto.basePrice = basePrice
							ipcDto.percentageValue = 0.0
							ipcDto.amountValue = basePrice
							ipcDto.addon = 0.0
							ipcDto.margin = 0.0
							ipcDto.totalMarkup = 0.0
							ipcDto.locked = false
							
							ipcDto.tierDetail = null
							
							allItemPrice.add(ipcDto)
						}
					}
			}
		}
		
		Page<ItemPriceControlDto> pages = new PageImpl<>(allItemPrice, pageable, allItems.getTotalElements())
		return pages
		
	}
	
	List<ItemCheckDTO> validateItemPriceList(Map<String, Object> fields) {
		def tierId = UUID.fromString(fields.get("tierId") as String)
		def itemList = fields.get("items") as List<Item>
		List<Item> controlList = []
		List<ItemPriceControl> list = itemPriceControlRepository.getItemControlItemsByTier(tierId)
		List<ItemCheckDTO> results = []
		
		list.each {
			it ->
				if (it.amountValue > 0)
					controlList.add(it.item)
		}
		
		itemList.each {
			it ->
				ItemCheckDTO dto = new ItemCheckDTO()
				Item itemObj = itemRepository.findById(UUID.fromString(it.id as String)).get()
				dto.item = itemObj
				dto.withPrice = controlList.contains(itemObj)
				results.add(dto)
		}
		
		return results
	}
	
	def updatePriceControl(String itemId, String tierDetail, BigDecimal amountValue, BigDecimal percentageValue) {
		ItemPriceControl priceControl = itemPriceControlRepository.getItemPriceControl(UUID.fromString(tierDetail), UUID.fromString(itemId))
		
		if (priceControl) {
			if (priceControl.locked == false) {
				priceControl.percentageValue = percentageValue ?: 0.0
				priceControl.amountValue = amountValue
				itemPriceControlRepository.save(priceControl)
			}
		} else {
			ItemPriceControl newPriceControl = new ItemPriceControl()
			
			newPriceControl.item = itemRepository.findById(UUID.fromString(itemId)).get()
			newPriceControl.priceTierDetail = priceTierDetailRepository.findById(UUID.fromString(tierDetail)).get()
			newPriceControl.percentageValue = percentageValue ?: 0.0
			newPriceControl.amountValue = amountValue
			newPriceControl.locked = false
			
			itemPriceControlRepository.save(newPriceControl)
		}
		
	}
	
	def toggleLockItemPriceControl(String itemId, String tierDetail) {
		ItemPriceControl priceControl = itemPriceControlRepository.getItemPriceControl(UUID.fromString(tierDetail), UUID.fromString(itemId))
		if (priceControl) {
			priceControl.locked = !priceControl.locked
			itemPriceControlRepository.save(priceControl)
		}
	}
	
	def updateItemPrices(String itemId, String group) {
		def item = itemRepository.findById(UUID.fromString(itemId)).get()
		def itemGroup = group ? group : (item.isMedicine ? 'MEDICINES' : 'SUPPLIES')
		
		List<PriceTierDetail> tiers = priceTierDetailRepository.findAll()
		
		if (item && tiers) {
			tiers.each {
				it ->
					def unitCost = item.actualUnitCost ?: 0.0
					def basePrice = item.item_markup ? unitCost + (unitCost * (item.item_markup / 100)) : unitCost
					
					if (basePrice && basePrice > 0.0) {
						def mod = priceTierModifierRepository.getPriceTierModifierByCost(basePrice, itemGroup, it.id).findAll().find()
						
						if (mod) {
							def amountValue = basePrice + (basePrice * (mod.percentageValue / 100))
							updatePriceControl(itemId, it.id.toString(), amountValue, mod.percentageValue)
						}
					}
			}
		}
	}
	
	List<ItemPriceControl> massUpdatePrices(String tierId, String group, String costGroup, BigDecimal percentageValue) {
		List<Item> allItems = []
		
		if (costGroup && costGroup.toUpperCase() != 'ALL') {
			def cg = costGroup.split("-")
			
			def from = cg[0]
			def to = cg[1]
			
			from = from as BigDecimal
			to = to as BigDecimal
			
			if (group == "MEDICINES")
				allItems = itemRepository.findAllMedicinesByCostRange(from, to).sort { it -> it.createdDate }
			else if (group == "SUPPLIES")
				allItems = itemRepository.findAllSuppliesByCostRange(from, to).sort { it -> it.createdDate }
			else
				allItems = itemRepository.itemsByFilterByCostRange("", from, to).sort { it -> it.createdDate }
		} else {
			if (group == "MEDICINES")
				allItems = itemRepository.findAllMedicines().sort { it -> it.createdDate }
			else if (group == "SUPPLIES")
				allItems = itemRepository.findAllSupplies().sort { it -> it.createdDate }
			else
				allItems = itemRepository.itemsByFilter("").sort { it -> it.createdDate }
		}
		
		List<ItemPriceControl> allItemPrice = []
		
		PriceTierDetail tier = priceTierDetailRepository.findById(UUID.fromString(tierId)).get()
		
		allItems.each {
			it ->
				if (it.active) {
					def basePrice = it.actualUnitCost ?: 0.0
					
					if (it.item_markup) {
						basePrice = basePrice + (basePrice * (it.item_markup / 100))
					}
					
					basePrice = basePrice + (basePrice * (percentageValue / 100))
					
					ItemPriceControl priceControl = itemPriceControlRepository.getItemPriceControl(tier.id, it.id)
					
					if (priceControl) {
						def isLocked = priceControl.locked
						if (isLocked == false) {
							itemPriceControlRepository.delete(priceControl)
							ItemPriceControl newPriceControl = new ItemPriceControl()
							
							newPriceControl.item = itemRepository.findById(it.id).get()
							newPriceControl.priceTierDetail = priceTierDetailRepository.findById(tier.id).get()
							newPriceControl.percentageValue = percentageValue ?: 0.0
							newPriceControl.amountValue = basePrice
							newPriceControl.locked = isLocked
							
							allItemPrice.add(newPriceControl)
						}
					} else {
						ItemPriceControl newPriceControl = new ItemPriceControl()
						
						newPriceControl.item = itemRepository.findById(it.id).get()
						newPriceControl.priceTierDetail = priceTierDetailRepository.findById(tier.id).get()
						newPriceControl.percentageValue = percentageValue ?: 0.0
						newPriceControl.amountValue = basePrice
						newPriceControl.locked = false
						
						allItemPrice.add(newPriceControl)
					}
				}
		}
		
		return itemPriceControlRepository.saveAll(allItemPrice)
	}
	
}
