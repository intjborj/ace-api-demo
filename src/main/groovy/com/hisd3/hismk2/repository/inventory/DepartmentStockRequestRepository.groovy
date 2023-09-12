package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.DepartmentStockRequest
import com.hisd3.hismk2.domain.inventory.ReturnSupplier
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DepartmentStockRequestRepository extends JpaRepository<DepartmentStockRequest, UUID> {
	
	@Query(value = '''Select s from DepartmentStockRequest s where s.issuingDepartment.id=:dep AND s.status=:status AND lower(s.requestNo) like lower(concat('%',:filter,'%')) ORDER BY s.createdDate desc ''')
	List<DepartmentStockRequest> findIncomingRequest(@Param("dep") UUID dep, @Param("status") Integer status, @Param("filter") String filter)
	
	@Query(value = '''Select s from DepartmentStockRequest s where s.requestingDepartment.id=:dep AND s.status=:status AND lower(s.requestNo) like lower(concat('%',:filter,'%')) ORDER BY s.createdDate desc ''')
	List<DepartmentStockRequest> findOutgoingRequest(@Param("dep") UUID dep, @Param("status") Integer status, @Param("filter") String filter)

//	code ni donss
	@Query(value = '''Select s from DepartmentStockRequest s where s.requestingDepartment.id=:dep AND lower(s.requestNo) like lower(concat('%',:filter,'%')) ORDER BY s.createdDate desc ''')
	List<DepartmentStockRequest> findDepartmentStockRequestByReqDept(@Param("dep") UUID dep, @Param("filter") String filter)
//	code ni donss
	@Query(value = '''Select s from DepartmentStockRequest s where s.issuingDepartment.id=:dep AND lower(s.requestNo) like lower(concat('%',:filter,'%')) AND s.status != 0 ORDER BY s.createdDate desc ''')
	List<DepartmentStockRequest> findDepartmentStockRequestByIssueDept(@Param("dep") UUID dep, @Param("filter") String filter)
//	code ni Dons
	@Query(value = '''Select s from DepartmentStockRequest s where s.issuingDepartment.id=:fromDept AND s.requestingDepartment.id = :toDept AND s.status = :status AND requestType = :type ORDER BY s.createdDate desc ''')
	List<DepartmentStockRequest> getDepartmentStockRequestReqNo(@Param("fromDept") UUID fromDept, @Param("toDept") UUID toDept, @Param("type") String type, @Param("status") Integer status)
	//code ni wilson
	@Query(value = '''select p from DepartmentStockRequest p where 
			p.issuingDepartment.id = :id and
			(p.requestNo like concat('%',:filter,'%')) and
			p.isPosted = true and
			to_date(to_char(p.requestDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
             	between to_date(:startDate,'YYYY-MM-DD') and  to_date(:endDate,'YYYY-MM-DD')''',
			countQuery = '''select count(p) from DepartmentStockRequest p where
			p.issuingDepartment.id = :id and
			(p.requestNo like concat('%',:filter,'%')) and
			p.isPosted = true and
			to_date(to_char(p.requestDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
				between to_date(:startDate,'YYYY-MM-DD') and  to_date(:endDate,'YYYY-MM-DD')''')
	Page<DepartmentStockRequest> getIncomingRequest(@Param('filter') String filter,
												 @Param('id') UUID id,
												 @Param('startDate') String startDate,
												 @Param('endDate') String endDate,
												 Pageable pageable)

	@Query('''select p from DepartmentStockRequest p where 
			p.issuingDepartment.id = :id and
			(p.requestNo like concat('%',:filter,'%')) and
			p.status = 0 and p.isPosted = true''')
	Page<DepartmentStockRequest> getIncomingRequestList(@Param('filter') String filter,
													@Param('id') UUID id,
													Pageable pageable)

	@Query(value = '''select p from DepartmentStockRequest p where 
			p.requestingDepartment.id = :id and
			(p.requestNo like concat('%',:filter,'%')) and
			to_date(to_char(p.requestDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
             	between to_date(:startDate,'YYYY-MM-DD') and  to_date(:endDate,'YYYY-MM-DD')''',
			countQuery = '''select count(p) from DepartmentStockRequest p where 
			p.requestingDepartment.id = :id and
			(p.requestNo like concat('%',:filter,'%')) and
			to_date(to_char(p.requestDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
             	between to_date(:startDate,'YYYY-MM-DD') and  to_date(:endDate,'YYYY-MM-DD')''')
	Page<DepartmentStockRequest> getOutgoingRequest(@Param('filter') String filter,
													@Param('id') UUID id,
													@Param('startDate') String startDate,
													@Param('endDate') String endDate,
													Pageable pageable)

	@Query('''select count(p) from DepartmentStockRequest p where p.issuingDepartment.id = :id and p.status = 0 and p.isPosted = true ''')
	Long getIncomingRequestCount(@Param('id') UUID id)
}
