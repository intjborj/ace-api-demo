package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.DepartmentStockIssueItems
import com.hisd3.hismk2.domain.inventory.PurchaseRequest
import com.hisd3.hismk2.domain.inventory.ReceivingReport
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import java.time.Instant

interface ReceivingReportRepository extends JpaRepository<ReceivingReport, UUID> {
	
	@Query(value = '''Select r from ReceivingReport r where r.receiveDepartment.id = :id and (lower(r.rrNo) like lower(concat('%',:filter,'%')) or lower(r.supplier.supplierFullname) like lower(concat('%',:filter,'%')))''')
	List<ReceivingReport> findReceivingByDep(@Param("id") UUID id, @Param("filter") String filter)

	@Query(value = '''Select r from ReceivingReport r where r.receiveDepartment.id = :id and 
				(lower(r.rrNo) like lower(concat('%',:filter,'%')) or 
				lower(r.supplier.supplierFullname) like lower(concat('%',:filter,'%'))) and 
				to_date(to_char(r.receiveDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
             	between to_date(:startDate,'YYYY-MM-DD') and  to_date(:endDate,'YYYY-MM-DD')''')
	List<ReceivingReport> getReceivingByDepRange(@Param("id") UUID id, @Param("filter") String filter,
												 @Param("startDate") String startDate, @Param("endDate") String endDate)


	@Query(value = '''Select r from ReceivingReport r where r.receiveDepartment.id = :id and 
				r.consignment = :consignment and
				r.asset = :asset and
				(lower(r.rrNo) like lower(concat('%',:filter,'%')) or 
				lower(r.supplier.supplierFullname) like lower(concat('%',:filter,'%'))) and 
				to_date(to_char(r.receiveDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
             	between to_date(:startDate,'YYYY-MM-DD') and  to_date(:endDate,'YYYY-MM-DD')''',
			countQuery = '''Select count(r) from ReceivingReport r where r.receiveDepartment.id = :id and 
				r.consignment = :consignment and
				r.asset = :asset and
				(lower(r.rrNo) like lower(concat('%',:filter,'%')) or 
				lower(r.supplier.supplierFullname) like lower(concat('%',:filter,'%'))) and 
				to_date(to_char(r.receiveDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
             	between to_date(:startDate,'YYYY-MM-DD') and  to_date(:endDate,'YYYY-MM-DD')''')
	Page<ReceivingReport> getReceivingByDepRangePage(@Param("id") UUID id, @Param("filter") String filter,
													  @Param("startDate") String startDate, @Param("endDate") String endDate,
													 @Param("consignment") Boolean consignment,
													 @Param("asset") Boolean asset,
													  Pageable pageable)
	
	@Query(value = '''Select r from ReceivingReport r where r.isPosted = true and r.receivedType = :type ''')
	List<ReceivingReport> findReceivingPosted(@Param("type") String type)

	@Query(value = ''' Select r from ReceivingReport r where r.rrNo = :rrNo ''')
	ReceivingReport findReceivingBySRR(@Param("rrNo") String rrNo)

	//code ni dons
	@Query(value = '''	Select s from ReceivingReport s
						where
						(s.isVoid = false OR s.isVoid is null) AND
						s.receiveDate >= :startDate AND
						s.receiveDate <= :endDate AND
						(lower(s.rrNo) like lower(concat('%',:filter,'%')) OR
						lower(s.supplier.supplierFullname) like lower(concat('%',:filter,'%')))
					 	ORDER BY s.receiveDate asc ''')
	List<ReceivingReport> getSrrByDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate, @Param("filter") String filter)

	@Query(value = '''	Select s from ReceivingReport s
						where
						(s.isVoid = false OR s.isVoid is null) AND
						s.receiveDate >= :startDate AND
						s.receiveDate <= :endDate AND
						s.supplier.id = :supplier AND
						(lower(s.rrNo) like lower(concat('%',:filter,'%')) OR
						lower(s.supplier.supplierFullname) like lower(concat('%',:filter,'%')))
					 	ORDER BY s.receiveDate asc ''')
	List<ReceivingReport> getSrrByDateRangeSupplier(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate, @Param("filter") String filter, @Param("supplier") UUID supplier)

	@Query(value = """ Select r from ReceivingReport r  where 	
					   lower(r.rrNo) like lower(concat('%',:filter,'%')) 
					   	GROUP BY r.id
    	  		   """,
				countQuery = """ Select count(r) from ReceivingReport r  where 						
					   lower(r.rrNo) like lower(concat('%',:filter,'%')) 
					    GROUP BY r.id
				""")
	List<ReceivingReport> getReceivingNoReport(@Param('filter') String filter)



}
