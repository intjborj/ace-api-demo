package com.hisd3.hismk2.rest

import com.hisd3.hismk2.domain.billing.BillingItemType
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.pms.O2Administration
import com.hisd3.hismk2.graphqlservices.billing.BillingItemServices
import com.hisd3.hismk2.graphqlservices.billing.BillingService
import com.hisd3.hismk2.repository.inventory.ItemRepository
import com.hisd3.hismk2.repository.pms.O2AdministrationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import java.math.RoundingMode
import java.time.temporal.ChronoUnit

@RestController
class OxygenAdministrationResource {
	@Autowired
	O2AdministrationRepository o2AdministrationRepository
	
	@Autowired
	ItemRepository itemRepository
	
	@Autowired
	BillingService billingService
	
	@Autowired
	BillingItemServices billingItemServices
	
	@RequestMapping("/api/billo2administration")
	String billo2administration(@RequestParam("id") UUID id) {
		O2Administration o2 = o2AdministrationRepository.getOne(id)
		if (o2.startDate != null && o2.endDate != null) {
			BigDecimal totalHours = (ChronoUnit.MILLIS.between(o2.startDate, o2.endDate) / 3600000).setScale(2, RoundingMode.CEILING)
			
			Item itemz = itemRepository.filterAllGasItems("").first()
			
			def activeBilling = billingService.activeBilling(o2.patientCase)
			// if no activing billing... no charges are made
			if (activeBilling && !activeBilling.locked) {
				o2
				def item = [
						"quantity"        : 1,
						"itemId"          : itemz.id,
						"targetDepartment": o2.patientCase.department.id.toString(),
						"flowrate"        : o2.flowrate,
						"totalAmount"     : totalHours.multiply(o2.flowrate.pricePerHour).setScale(2, RoundingMode.CEILING),
						"totalHours"      : totalHours
				
				]
				
				def values = []
				values << item
				
				billingItemServices.addBillingItem(
						activeBilling.id,
						BillingItemType.OXYGEN,
						values
				)
				o2.billed = true
				o2AdministrationRepository.save(o2)
			} else {
				throw new IllegalArgumentException("No Active Billing or Billing Record is now locked")
			}
		} else
			throw new IllegalArgumentException("Start or End date is blank")
		
		return id.toString()
	}
}
