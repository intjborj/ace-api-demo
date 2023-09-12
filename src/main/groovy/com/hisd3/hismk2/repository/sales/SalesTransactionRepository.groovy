package com.hisd3.hismk2.repository.sales

import com.hisd3.hismk2.domain.sales.SalesTransaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SalesTransactionRepository extends JpaRepository<SalesTransaction, UUID> {
	
	@Query(value = '''Select s from SalesTransaction s where (
            lower(s.customerName) like concat('%',:filter,'%') or 
            lower(s.cashierName) like concat('%',:filter,'%') )and 
			lower(s.status) like concat('%',:status,'%') 
			''')
	
	List<SalesTransaction> findAllByFilterAndStatus(@Param("filter") String filter, @Param("status") String status)
	
	@Query(value = '''Select s from SalesTransaction s where
            lower(s.status) like concat('%',:status,'%')''')
	
	List<SalesTransaction> findAllByStatus(@Param("status") String status)
}
