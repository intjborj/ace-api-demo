package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.PurchaseRequest
import com.hisd3.hismk2.domain.inventory.PurchaseRequestItem
import com.hisd3.hismk2.domain.inventory.ReceivingReportItem
import com.sun.org.apache.xpath.internal.operations.Bool
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import java.time.Instant

interface PurchaseRequestItemRepository extends JpaRepository<PurchaseRequestItem, UUID> {
	
	@Query(value = "select p from PurchaseRequestItem p where p.purchaseRequest.id = :refPr")
	List<PurchaseRequestItem> getByPrId(@Param("refPr") UUID refPr)
	
	@Query(value = "select p from PurchaseRequestItem p where p.purchaseRequest.id = :refPr and p.refPo is null")
	List<PurchaseRequestItem> getByPrIdAndPoNull(@Param("refPr") UUID refPr)
	
	@Query(value = "select count(p.id) from PurchaseRequestItem p where p.purchaseRequest.id = :refPr and p.refPo is null")
	Integer countGetByPrIdAndPoNull(@Param("refPr") UUID refPr)
	
	@Query(value = "select p from PurchaseRequestItem p where (p.purchaseRequest.prNo in :prNos and p.purchaseRequest.supplier.id = :supplierId) order by p.item.descLong")
	List<PurchaseRequestItem> getPrItemsByMultiplePrNos(@Param("prNos") List<String> prNos, @Param("supplierId") UUID supplierId)
	
	@Query(value = "select p from PurchaseRequestItem p where (p.purchaseRequest.prNo in :prNos) and p.refPo is null order by p.item.descLong")
	List<PurchaseRequestItem> getPrItemsByMultiplePrNos(@Param("prNos") List<String> prNos)
	
	@Query(value = "select p from PurchaseRequestItem p where (p.purchaseRequest.prNo in :prNos) and p.refPo.id = :id order by p.item.descLong")
	List<PurchaseRequestItem> getPrItemsByMultiplePrNosAll(@Param("prNos") List<String> prNos, @Param("id") UUID id)
	
	@Query(value = "select p from PurchaseRequestItem p where p.refPo is null")
	List<PurchaseRequestItem> getPrItemByPoNull()
	
	@Query(value = "select p from PurchaseRequestItem p where p.refPo is null and p.purchaseRequest.status = 'APPROVED'")
	List<PurchaseRequestItem> getPrItemByWherePoIsNotNullandStatusIsApproved()
	
	@Query(value = "select p from PurchaseRequestItem p where p.item.id = :item_id and p.refPo is null and p.purchaseRequest.status = 'APPROVED'")
	List<PurchaseRequestItem> getAllByItemIdWhereStatusIsApproved(@Param("item_id") UUID itemId)
	
	@Query(value = "select p from PurchaseRequestItem p where p.refPo.id = :poId")
	List<PurchaseRequestItem> getPrItemByPoId(@Param("poId") UUID poId)
	
	@Query(value = "select pi from PurchaseRequestItem pi where pi.purchaseRequest.prNo = :prNo")
	List<PurchaseRequestItem> findByPrNo(@Param('prNo') String prNo)

	@Query(value = "select DISTINCT pi.purchaseRequest from PurchaseRequestItem pi where pi.purchaseRequest.isApprove = true and pi.refPo is null")
	List<PurchaseRequest> getPrItemsNotYetPo()
	
	@Query(value = "select DISTINCT pi.purchaseRequest from PurchaseRequestItem pi where pi.purchaseRequest.isApprove = true and pi.purchaseRequest.consignment = :consignment and pi.purchaseRequest.asset = :asset and pi.refPo is null")
	List<PurchaseRequest> getPrItemsNotYetPo(@Param('consignment') Boolean consignment, @Param('asset') Boolean asset)


	@Query(value = '''	Select r from PurchaseRequestItem r
						where
						(r.purchaseRequest.isApprove = true) AND
						r.purchaseRequest.prDateRequested >= :startDate AND
						r.purchaseRequest.prDateRequested <= :endDate AND
						(lower(r.purchaseRequest.prNo) like lower(concat('%',:filter,'%')) OR
						lower(r.item.descLong) like lower(concat('%',:filter,'%')))
					 	ORDER BY r.purchaseRequest.prDateRequested asc ''')
	List<PurchaseRequestItem> getPRItemByDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate, @Param("filter") String filter)

	@Query(value = '''	Select r from PurchaseRequestItem r
						where
						(r.purchaseRequest.isApprove = true) AND
						r.purchaseRequest.prDateRequested >= :startDate AND
						r.purchaseRequest.prDateRequested <= :endDate AND
						r.purchaseRequest.supplier.id = :supplier AND
						(lower(r.purchaseRequest.prNo) like lower(concat('%',:filter,'%')) OR
						lower(r.item.descLong) like lower(concat('%',:filter,'%')) OR
						lower(r.purchaseRequest.supplier.supplierFullname) like lower(concat('%',:filter,'%')))
					 	ORDER BY r.purchaseRequest.prDateRequested asc ''')
	List<PurchaseRequestItem> getPRItemByDateRangeSupplier(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate, @Param("filter") String filter, @Param("supplier") UUID supplier)
}
