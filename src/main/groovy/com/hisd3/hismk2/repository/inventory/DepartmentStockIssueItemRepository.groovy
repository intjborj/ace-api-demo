package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.DepartmentStockIssueItems
import com.hisd3.hismk2.domain.inventory.DepartmentStockRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import java.time.Instant

interface DepartmentStockIssueItemRepository extends JpaRepository<DepartmentStockIssueItems, UUID> {
	
	@Query(value = '''Select s from DepartmentStockIssueItems s where s.stockIssue.id=:id''')
	List<DepartmentStockIssueItems> findItemsByIssue(@Param("id") UUID id)
	
	@Query(value = '''	Select s from DepartmentStockIssueItems s
						where
						s.stockIssue.issueType = :issueType AND
						s.stockIssue.issueDate >= :startDate AND
						s.stockIssue.issueDate <= :endDate AND
						s.stockIssue.issueFrom.id = :expenseFrom AND
						(lower(s.item.descLong) like lower(concat('%',:filter,'%')) OR
						lower(s.item.item_category.categoryDescription) like lower(concat('%',:filter,'%')) OR
						lower(s.stockIssue.issueNo) like lower(concat('%',:filter,'%')))
					 	ORDER BY s.stockIssue.issueDate asc ''')
	List<DepartmentStockIssueItems> getItemExpense(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate, @Param("issueType") String issueType, @Param("filter") String filter, @Param("expenseFrom") UUID expenseFrom)

	@Query(value = '''Select s from DepartmentStockIssueItems s where s.stockIssue.id=:stockIssueId AND s.item.id = :itemId''')
	DepartmentStockIssueItems getExistingStockIssueItemByItem(@Param("stockIssueId") UUID stockIssueId, @Param("itemId") UUID itemId)

	//code ni wilson
	@Query(value = '''Select s from DepartmentStockIssueItems s where s.stockIssue.id=:id and s.issueQty > 0''')
	List<DepartmentStockIssueItems> getIssuanceItemsForPosting(@Param("id") UUID id)

	@Query(value = '''Select s from DepartmentStockIssueItems s where s.stockIssue.id=:id and s.isPosted = true''')
	List<DepartmentStockIssueItems> getIssuancePostedItems(@Param("id") UUID id)

}
