package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.PurchaseRequest
import com.hisd3.hismk2.domain.inventory.SupplierType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, UUID> {
	
	@Query(value = "select pr from PurchaseRequest pr where pr.prNo = :prNo")
	PurchaseRequest getByPrNo(@Param('prNo') String prNo)

	@Query(value = "select pr from PurchaseRequest pr where pr.id = :id")
	PurchaseRequest getPrById(@Param('id') UUID id)
	
	@Query(value = '''Select pr from PurchaseRequest pr where  pr.requestingDepartment.id = :id and (lower(pr.prNo) like lower(concat('%',:filter,'%')))''')
	List<PurchaseRequest> findPrByDepartment(@Param("id") UUID id, @Param("filter") String filter)


	@Query(value = '''Select pr from PurchaseRequest pr where  
						pr.requestingDepartment.id = :id and (lower(pr.prNo) like lower(concat('%',:filter,'%'))) and
						pr.consignment = :consignment and
						pr.asset = :asset and
						to_date(to_char(pr.prDateRequested, 'YYYY-MM-DD'),'YYYY-MM-DD')
             	between to_date(:startDate,'YYYY-MM-DD') and  to_date(:endDate,'YYYY-MM-DD')''')
	List<PurchaseRequest> findPrByDepartmentRange(@Param("id") UUID id, @Param("filter") String filter,
												  @Param("startDate") String startDate,@Param("endDate") String endDate,
												  @Param("consignment") Boolean consignment, @Param("asset") Boolean asset)

	@Query(value = '''Select pr from PurchaseRequest pr where  
						pr.requestingDepartment.id = :id and (lower(pr.prNo) like lower(concat('%',:filter,'%'))) and
						to_date(to_char(pr.prDateRequested, 'YYYY-MM-DD'),'YYYY-MM-DD')
             	between to_date(:startDate,'YYYY-MM-DD') and  to_date(:endDate,'YYYY-MM-DD')''',
			countQuery = '''Select count(pr) from PurchaseRequest pr where  
						pr.requestingDepartment.id = :id and (lower(pr.prNo) like lower(concat('%',:filter,'%'))) and
						to_date(to_char(pr.prDateRequested, 'YYYY-MM-DD'),'YYYY-MM-DD')
             	between to_date(:startDate,'YYYY-MM-DD') and  to_date(:endDate,'YYYY-MM-DD')''')
	Page<PurchaseRequest> findPrByDepartmentRangePage(@Param("id") UUID id, @Param("filter") String filter,
													  @Param("startDate") String startDate,@Param("endDate") String endDate,
													  Pageable pageable)
	
	@Query(value = '''Select pr from PurchaseRequest pr where pr.supplier.id = :id order by pr.prNo''')
	List<PurchaseRequest> findPrBySupplier(@Param("id") UUID id)
	
	@Query(value = '''Select pr from PurchaseRequest pr where pr.supplier is null ''')
	List<PurchaseRequest> findPrWithoutSupplier()
	
}
