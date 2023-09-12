package com.hisd3.hismk2.repository.billing

import com.hisd3.hismk2.domain.billing.ServicePriceControl
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ServicePriceControlRepository extends JpaRepository<ServicePriceControl, UUID> {
	
	@Query(value = "Select pc From ServicePriceControl pc where pc.service.id = :serviceId and pc.priceTierDetail.id = :tierId")
	ServicePriceControl getServicePriceControl(@Param("serviceId") UUID serviceId, @Param("tierId") UUID tierId)
	
	@Query(value = "Select pc from ServicePriceControl pc where pc.priceTierDetail.id = :tierId and pc.service.id = :serviceId")
	ServicePriceControl getServiceByIdAndTier(@Param("tierId") UUID tierId, @Param("serviceId") UUID serviceId)
	
	@Query(value = "Select pc from ServicePriceControl pc where pc.priceTierDetail.id = :tierId")
	List<ServicePriceControl> getServiceControlItemsByTier(@Param("tierId") UUID tierId)
	
}
