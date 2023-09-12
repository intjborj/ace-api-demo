package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.DepartmentStockRequestItem
import com.hisd3.hismk2.domain.inventory.PurchaseRequestItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DepartmentStockRequestItemRepository extends JpaRepository<DepartmentStockRequestItem, UUID> {
	
	@Query(value = '''Select s from DepartmentStockRequestItem s where s.departmentStockRequest.id=:id''')
	List<DepartmentStockRequestItem> findItemsByRequest(@Param("id") UUID id)

	@Query(value = '''Select s from DepartmentStockRequestItem s where s.departmentStockRequest.id=:id and s.stockIssue is null''')
	List<DepartmentStockRequestItem> getDepRequestItems(@Param("id") UUID id)

	@Query(value = '''Select s from DepartmentStockRequestItem s where s.stockIssueItems.stockIssue.id=:stock_issue_id''')
	List<DepartmentStockRequestItem> findItemsByStockIssueId(@Param("stock_issue_id") UUID stock_issue_id)
	
}
