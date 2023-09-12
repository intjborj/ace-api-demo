package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.PurchaseOrderItems
import com.hisd3.hismk2.domain.inventory.PurchaseRequestItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import java.time.Instant
import java.time.LocalDateTime

interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItems, UUID> {
	@Query("select pi from PurchaseOrderItems pi where pi.purchaseOrder.id = :poId")
	List<PurchaseOrderItems> findByPurchaseOrderId(@Param("poId") UUID poId)
	
	@Query("select pi from PurchaseOrderItems pi where pi.purchaseOrder.id = :poId and lower(pi.item.descLong) like lower(concat('%',:filter,'%'))")
	Page<PurchaseOrderItems> findByPurchaseOrderIdPageable(@Param("filter") String filter, @Param("poId") UUID poId, Pageable pageable)
	
	//
	@Query("select count(pi.id) from PurchaseOrderItems pi where pi.purchaseOrder.id = :poId and (pi.receivingReport is null or pi.deliveryStatus != 2) ")
	Integer countPOItem(@Param("poId") UUID poId)
	
	@Query(" select pi from PurchaseOrderItems pi where pi.purchaseOrder.id = :poId and (pi.receivingReport is null or pi.deliveryStatus != 2) ")
	List<PurchaseOrderItems> findPoItem(@Param("poId") UUID poId)
	
	@Query(" select pi from PurchaseOrderItems pi where pi.purchaseOrder.id = :poId ")
	List<PurchaseOrderItems> getPoItemsById(@Param("poId") UUID poId)
	//


	@Query("select pi from PurchaseOrderItems pi where pi.receivingReport is null")
	List<PurchaseOrderItems> findPOItemWithOutRec(@Param("poId") UUID poId)


	@Query(value = '''	Select r from PurchaseOrderItems r
						where
						(r.purchaseOrder.status = 'APPROVED' or r.purchaseOrder.status = 'DELIVERED') AND
						r.purchaseOrder.preparedDate >= :startDate AND
						r.purchaseOrder.preparedDate <= :endDate AND
						(lower(r.purchaseOrder.poNumber) like lower(concat('%',:filter,'%')) OR
						lower(r.item.descLong) like lower(concat('%',:filter,'%')))
					 	ORDER BY r.purchaseOrder.preparedDate asc ''')
	List<PurchaseOrderItems> getPOItemByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("filter") String filter)

	@Query(value = '''	Select r from PurchaseOrderItems r
						where
						(r.purchaseOrder.status = 'APPROVED' or r.purchaseOrder.status = 'DELIVERED') AND
						r.purchaseOrder.preparedDate >= :startDate AND
						r.purchaseOrder.preparedDate <= :endDate AND
						r.purchaseOrder.supplier.id = :supplier AND
						(lower(r.purchaseOrder.poNumber) like lower(concat('%',:filter,'%')) OR
						lower(r.item.descLong) like lower(concat('%',:filter,'%')) OR
						lower(r.purchaseOrder.supplier.supplierFullname) like lower(concat('%',:filter,'%')))
					 	ORDER BY r.purchaseOrder.preparedDate asc ''')
	List<PurchaseOrderItems> getPOItemByDateRangeSupplier(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("filter") String filter, @Param("supplier") UUID supplier)
}
