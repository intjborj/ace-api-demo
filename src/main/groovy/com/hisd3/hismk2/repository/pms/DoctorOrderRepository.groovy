package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.DoctorOrder
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import java.time.Instant

interface DoctorOrderRepository extends JpaRepository<DoctorOrder, UUID> {
	@Query(value = 'SELECT d FROM DoctorOrder d WHERE d.parentCase.id = :parentCase AND d.hidden IS NULL ORDER BY d.entryDateTime')
	List<DoctorOrder> getDoctorOrdersByCase(@Param("parentCase") UUID parentCase)

	@Query(value = 'SELECT d FROM DoctorOrder d WHERE d.parentCase.id = :parentCase AND d.hidden IS NULL')
	Page<DoctorOrder> getDoctorOrdersByCasePageable(@Param("parentCase") UUID parentCase, Pageable pageable)
	
	@Query(value = "SELECT d FROM DoctorOrder d WHERE d.parentCase.id = :parentCase AND d.entryDateTime BETWEEN :from AND :to AND (d.hidden IS NULL OR d.hidden IS EMPTY)")
	List<DoctorOrder> getDoctorOrdersByCaseByDateRange(@Param("parentCase") UUID parentCase, @Param('from') Instant from, @Param('to') Instant to)
}
