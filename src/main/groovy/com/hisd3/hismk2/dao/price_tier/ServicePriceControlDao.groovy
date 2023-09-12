package com.hisd3.hismk2.dao.price_tier

import com.hisd3.hismk2.domain.ancillary.Service
import com.hisd3.hismk2.domain.billing.PriceTierDetail
import com.hisd3.hismk2.domain.billing.ServicePriceControl
import com.hisd3.hismk2.repository.ancillary.ServiceRepository
import com.hisd3.hismk2.repository.billing.PriceTierDetailRepository
import com.hisd3.hismk2.repository.billing.PriceTierModifierRepository
import com.hisd3.hismk2.repository.billing.ServicePriceControlRepository
import com.hisd3.hismk2.rest.dto.ServiceCheckDTO
import com.hisd3.hismk2.rest.dto.ServicePriceControlDto
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.transaction.annotation.Transactional

import java.math.RoundingMode

@TypeChecked
@org.springframework.stereotype.Service
@Transactional
class ServicePriceControlDao {
	
	@Autowired
	ServicePriceControlRepository servicePriceControlRepository
	
	@Autowired
	ServiceRepository serviceRepository
	
	@Autowired
	PriceTierDetailRepository priceTierDetailRepository
	
	@Autowired
	PriceTierModifierRepository priceTierModifierRepository
	
	Page<ServicePriceControlDto> getAllPriceControlServices(UUID tierId, UUID deptId, String costGroup, String filter, Pageable pageable) {
		
		Page<Service> allServices
		
		if (costGroup && costGroup.toUpperCase() != 'ALL') {
			def cg = costGroup.split("-")
			
			def from = cg[0]
			def to = cg[1]
			
			from = from as BigDecimal
			to = to as BigDecimal
			
			if (deptId) {
				allServices = serviceRepository.searchlistByDepartmentPageByCostRange(deptId, filter, from, to, pageable)
			} else {
				allServices = serviceRepository.searchlistPageableByCostRange(filter, from, to, pageable)
			}
		} else {
			if (deptId) {
				allServices = serviceRepository.searchlistByDepartmentPage(deptId, filter, pageable)
			} else {
				allServices = serviceRepository.searchlistPageable(filter, pageable)
			}
		}
		
		List<ServicePriceControlDto> allServicePrice = []
		
		if (tierId) {
			PriceTierDetail tier = priceTierDetailRepository.findById(tierId).get()
			
			allServices.each {
				it ->
					ServicePriceControl service = servicePriceControlRepository.getServiceByIdAndTier(
							tierId,
							it.id
					)
					
					if (service) {
						ServicePriceControlDto ipcDto = new ServicePriceControlDto()
						ipcDto.service = it
						
						def unitCost = it.basePrice ?: 0.0
						def basePrice = it.markup || it.markup > 0 ? unitCost + (unitCost * (it.markup / 100)) : unitCost
						ipcDto.basePrice = basePrice ?: 0.0
						def sellPrice = 0.0
						
						if (service.amountValue > 0 && basePrice != 0.0) {
							sellPrice = service.amountValue ?: 0.0
							ipcDto.percentageValue = service.percentageValue ?: 0.0
							ipcDto.amountValue = sellPrice
							ipcDto.addon = ((sellPrice - basePrice) / basePrice) * 100
							ipcDto.margin = sellPrice - it.basePrice
							ipcDto.totalMarkup = ((sellPrice - it.basePrice) / it.basePrice) * 100
							
							ipcDto.locked = service.locked
						} else {
							ipcDto.addon = 0.0
							ipcDto.percentageValue = 0.0
							ipcDto.amountValue = 0.0
							ipcDto.margin = 0.0
							ipcDto.totalMarkup = 0.0
							ipcDto.locked = service.locked
						}
						
						ipcDto.tierDetail = tier
						allServicePrice.add(ipcDto)
					} else {
						ServicePriceControlDto ipcDto = new ServicePriceControlDto()
						ipcDto.service = it
						
						def unitCost = it.basePrice ?: 0.0
						def basePrice = it.markup ? unitCost + (unitCost * (it.markup / 100)) : unitCost
						
						ipcDto.basePrice = basePrice
						ipcDto.percentageValue = 0.0
						ipcDto.amountValue = basePrice
						ipcDto.addon = 0.0
						ipcDto.margin = 0.0
						ipcDto.totalMarkup = 0.0
						ipcDto.locked = false
						
						ipcDto.tierDetail = null
						allServicePrice.add(ipcDto)
					}
			}
		}
		
		Page<ServicePriceControlDto> pages = new PageImpl<>(allServicePrice, pageable, allServices.getTotalElements())
		
		return pages
	}
	
	def updateServicePriceControl(String serviceId, String tierDetail, BigDecimal amountValue, BigDecimal percentageValue) {
		ServicePriceControl priceControl = servicePriceControlRepository.getServicePriceControl(UUID.fromString(serviceId), UUID.fromString(tierDetail))
		
		if (priceControl) {
			if (priceControl.locked == false) {
				priceControl.percentageValue = percentageValue
				priceControl.amountValue = amountValue
				servicePriceControlRepository.save(priceControl)
			}
		} else {
			ServicePriceControl newPriceControl = new ServicePriceControl()
			
			newPriceControl.service = serviceRepository.findById(UUID.fromString(serviceId)).get()
			newPriceControl.priceTierDetail = priceTierDetailRepository.findById(UUID.fromString(tierDetail)).get()
			newPriceControl.percentageValue = percentageValue
			newPriceControl.amountValue = amountValue
			newPriceControl.locked = false
			servicePriceControlRepository.save(newPriceControl)
		}
	}
	
	def updateServicePrices(String serviceId) {
		def service = serviceRepository.findById(UUID.fromString(serviceId)).get()
		
		List<PriceTierDetail> tiers = priceTierDetailRepository.findAll()
		
		if (service && tiers) {
			tiers.each {
				it ->
					def basePrice = service.basePrice ?: 0.0
					
					if (basePrice && basePrice > 0.0) {
						def mod = priceTierModifierRepository.getPriceTierModifierByCost(basePrice, 'SERVICES', it.id).findAll().find()
						
						if (mod) {
							def amountValue = basePrice + (basePrice * (mod.percentageValue / 100))
							updateServicePriceControl(serviceId, it.id.toString(), amountValue, mod.percentageValue)
						}
					}
			}
		}
	}
	
	def toggleLockServicePriceControl(String serviceId, String tierDetail) {
		ServicePriceControl priceControl = servicePriceControlRepository.getServicePriceControl(UUID.fromString(serviceId), UUID.fromString(tierDetail))
		if (priceControl) {
			priceControl.locked = !priceControl.locked
			servicePriceControlRepository.save(priceControl)
		}
	}
	
	def batchUpdateServicePriceControl(String serviceId, String tierDetail, BigDecimal amountValue, BigDecimal percentageValue) {
		ServicePriceControl priceControl = servicePriceControlRepository.getServicePriceControl(UUID.fromString(serviceId), UUID.fromString(tierDetail))
		
		if (priceControl) {
			priceControl.amountValue = amountValue
		} else {
			ServicePriceControl newPriceControl = new ServicePriceControl()
			
			newPriceControl.service = serviceRepository.findById(UUID.fromString(serviceId)).get()
			newPriceControl.priceTierDetail = priceTierDetailRepository.findById(UUID.fromString(tierDetail)).get()
			newPriceControl.percentageValue = percentageValue
			newPriceControl.amountValue = amountValue
		}
	}
	
	List<ServiceCheckDTO> validateServicePriceList(Map<String, Object> fields) {
		def tierId = UUID.fromString(fields.get("tierId") as String)
		def serviceList = fields.get("services") as List<Service>
		List<Service> controlList = []
		List<ServicePriceControl> list = servicePriceControlRepository.getServiceControlItemsByTier(tierId)
		List<ServiceCheckDTO> results = []
		
		list.each {
			it ->
				if (it.amountValue > 0)
					controlList.add(it.service)
		}
		
		serviceList.each {
			it ->
				ServiceCheckDTO dto = new ServiceCheckDTO()
				Service servObj = serviceRepository.findById(UUID.fromString(it.id as String)).get()
				dto.service = servObj
				dto.withPrice = controlList.contains(servObj)
				results.add(dto)
		}
		
		return results
	}
	
	@javax.transaction.Transactional
	List<ServicePriceControl> massUpdateServicePrices(String tierId, String deptId, String costGroup, BigDecimal percentageValue) {
		
		List<Service> allServices = []
		
		if (costGroup && costGroup.toUpperCase() != 'ALL') {
			def cg = costGroup.split("-")
			
			def from = cg[0]
			def to = cg[1]
			
			if (to.toLowerCase() == "up") {
				to = 99999999.0
			}
			
			from = from as BigDecimal
			to = to as BigDecimal
			
			if (deptId) {
				allServices = serviceRepository.searchlistByDepartmentByCostRange(UUID.fromString(deptId), "", from, to).sort { it -> it.createdDate }
			} else {
				allServices = serviceRepository.searchlistByCostRange("", from, to).sort { it -> it.createdDate }
			}
		} else {
			if (deptId) {
				allServices = serviceRepository.searchlistByDepartment(UUID.fromString(deptId), "")
			} else {
				allServices = serviceRepository.searchlist("")
			}
		}
		
		List<ServicePriceControl> allServicePrice = []
		PriceTierDetail tier = priceTierDetailRepository.findById(UUID.fromString(tierId)).get()
		
		allServices.each {
			it ->
				def basePrice = it.basePrice ?: 0.0
				
				if (basePrice && basePrice > 0) {
					if (it.markup) {
						basePrice = basePrice + (basePrice * (it.markup / 100))
					}
					
					basePrice = basePrice + (basePrice * (percentageValue / 100))
					
					ServicePriceControl priceControl = servicePriceControlRepository.getServicePriceControl(it.id, tier.id)
					
					if (priceControl) {
						def isLocked = priceControl.locked
						if (isLocked == false) {
							servicePriceControlRepository.delete(priceControl)
							ServicePriceControl newPriceControl = new ServicePriceControl()
							
							newPriceControl.service = serviceRepository.findById(it.id).get()
							newPriceControl.priceTierDetail = priceTierDetailRepository.findById(tier.id).get()
							newPriceControl.percentageValue = percentageValue
							newPriceControl.amountValue = basePrice.setScale(2, RoundingMode.HALF_EVEN)
							newPriceControl.locked = isLocked
							
							allServicePrice.add(newPriceControl)
						}
					} else {
						ServicePriceControl newPriceControl = new ServicePriceControl()
						
						newPriceControl.service = serviceRepository.findById(it.id).get()
						newPriceControl.priceTierDetail = priceTierDetailRepository.findById(tier.id).get()
						newPriceControl.percentageValue = percentageValue
						newPriceControl.amountValue = basePrice.setScale(2, RoundingMode.HALF_EVEN)
						newPriceControl.locked = false
						
						allServicePrice.add(newPriceControl)
					}
				}
			
		}
		
		return servicePriceControlRepository.saveAll(allServicePrice)
	}
	
}
