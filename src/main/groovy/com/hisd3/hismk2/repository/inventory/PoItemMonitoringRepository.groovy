package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.PODeliveryMonitoring
import com.hisd3.hismk2.domain.inventory.PurchaseOrder
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PoItemMonitoringRepository extends JpaRepository<PODeliveryMonitoring, UUID> {
	
	@Query("select p from PODeliveryMonitoring p where p.receivingReport.id = :receivingId")
	List<PODeliveryMonitoring> findDeliveryMonitoringItems(@Param("receivingId") UUID receivingId)



}
