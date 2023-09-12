package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.ReturnSupplierItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ReturnSupplierItemRepository extends JpaRepository<ReturnSupplierItem, UUID> {
	
	@Query(value = "Select r from ReturnSupplierItem r where r.returnSupplier.id = :id")
	List<ReturnSupplierItem> findItemsByReturnSupplierId(@Param("id") UUID id)
}
