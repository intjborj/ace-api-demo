package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.DepartmentStockIssue
import com.hisd3.hismk2.domain.inventory.DepartmentStockRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DepartmentStockIssueRepository extends JpaRepository<DepartmentStockIssue, UUID> {
	
	@Query(value = ''' Select s from DepartmentStockIssue s where s.issueFrom.id=:id and lower(s.issueNo) like lower(concat('%',:filter,'%')) ''')
	List<DepartmentStockIssue> findIssueByDep(@Param("id") UUID id, @Param("filter") String filter)

	@Query(value = '''select p from DepartmentStockIssue p where 
			p.issueFrom.id = :id and
			(p.issueNo like concat('%',:filter,'%')) and
			to_date(to_char(p.issueDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
             	between to_date(:startDate,'YYYY-MM-DD') and  to_date(:endDate,'YYYY-MM-DD')''',
			countQuery = '''select count(p) from DepartmentStockIssue p where 
			p.issueFrom.id = :id and
			(p.issueNo like concat('%',:filter,'%')) and
			to_date(to_char(p.issueDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
             	between to_date(:startDate,'YYYY-MM-DD') and  to_date(:endDate,'YYYY-MM-DD')''')
	Page<DepartmentStockIssue> getDepItemIssuance(@Param('filter') String filter,
													@Param('id') UUID id,
													@Param('startDate') String startDate,
													@Param('endDate') String endDate,
													Pageable pageable)

	@Query(value = ''' Select s from DepartmentStockIssue s where s.issueNo = :issueNo ''')
	DepartmentStockIssue findIssueByNo(@Param("issueNo") String issueNo)
	
}
