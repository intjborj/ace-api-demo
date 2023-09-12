package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.PurchaseOrder
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {
	
	@Query("select p from PurchaseOrder p where p.status='APPROVED' and p.isCompleted = :status")
	List<PurchaseOrder> findPurchaseOrderByStatus(@Param("status") Boolean status)

	@Query("Select e from PurchaseOrder e where e.consignment = :consignment and (e.isCompleted = false or e.isCompleted is null)")
	List<PurchaseOrder> poNotYetCompleted(@Param("consignment") Boolean consignment)

	@Query("Select e from PurchaseOrder e where e.consignment = :consignment and e.isFixedAsset = :asset and (e.isCompleted = false or e.isCompleted is null)")
	List<PurchaseOrder> poNotYetCompleted(@Param("consignment") Boolean consignment, @Param("asset") Boolean asset)

	@Query("Select e from PurchaseOrder e where e.isCompleted = false or e.isCompleted is null")
	List<PurchaseOrder> poNotYetCompleted()
	
	@Query('''select p from PurchaseOrder p where p.prNos like concat('%',:filter,'%') or p.poNumber like concat('%',:filter,'%') or lower(p.supplier.supplierFullname) like lower(concat('%',:filter,'%'))''')
	Page<PurchaseOrder> findPurchaseOrderByPoNoOrPrNos(@Param('filter') String filter, Pageable pageable)

	@Query('''select p from PurchaseOrder p where 
			(p.poNumber like concat('%',:filter,'%') or 
			lower(p.supplier.supplierFullname) like lower(concat('%',:filter,'%')))''')
	Page<PurchaseOrder> getPoList(@Param('filter') String filter, Pageable pageable)

	@Query('''select p from PurchaseOrder p where 
			p.departmentFrom.id = :id and
			p.consignment = :consignment and
			p.isFixedAsset = :asset and
			(p.prNos like concat('%',:filter,'%') or 
			p.poNumber like concat('%',:filter,'%') or 
			lower(p.supplier.supplierFullname) like lower(concat('%',:filter,'%'))) and
			to_date(to_char(p.preparedDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
             	between to_date(:startDate,'YYYY-MM-DD') and  to_date(:endDate,'YYYY-MM-DD')''')
	Page<PurchaseOrder> findPurchaseOrderByPoNoOrPrNosRange(@Param('filter') String filter,
															@Param('id') UUID id,
															@Param('startDate') String startDate,
															@Param('endDate') String endDate,
															@Param('consignment') Boolean consignment,
															@Param('asset') Boolean asset,
															Pageable pageable)

	@Query('''select p from PurchaseOrder p where 
			(p.prNos like concat('%',:filter,'%') or 
			p.poNumber like concat('%',:filter,'%') or 
			lower(p.supplier.supplierFullname) like lower(concat('%',:filter,'%'))) and
			to_date(to_char(p.preparedDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
             	between to_date(:startDate,'YYYY-MM-DD') and  to_date(:endDate,'YYYY-MM-DD')
            and isFixedAsset = true order by p.preparedDate desc, p.poNumber desc ''')
	Page<PurchaseOrder> getFixedAssetPurchaseOrder(@Param('filter') String filter,
															@Param('startDate') String startDate,
															@Param('endDate') String endDate,
															Pageable pageable)
}
