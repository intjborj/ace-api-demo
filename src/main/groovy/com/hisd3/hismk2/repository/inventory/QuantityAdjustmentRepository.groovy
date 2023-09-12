package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.QuantityAdjustment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface QuantityAdjustmentRepository extends JpaRepository<QuantityAdjustment, UUID> {
	
	@Query(value = "select q from QuantityAdjustment q where q.item.id = :id")
	List<QuantityAdjustment> getAdjustById(@Param('id') UUID id)
	
}
