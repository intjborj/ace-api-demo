package com.hisd3.hismk2.repository.sales

import com.hisd3.hismk2.domain.sales.SalesTransactionItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SalesTransactionItemRepository extends JpaRepository<SalesTransactionItem, UUID> {
	
	@Query(value = '''Select si from SalesTransactionItem si where
            lower(si.item.descLong) like concat('%',:filter,'%') or 
            lower(si.item.genericName) like concat('%',:filter,'%')''')
	
	List<SalesTransactionItem> findAllByFilter(@Param("filter") String filter)
}
