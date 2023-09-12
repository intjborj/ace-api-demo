package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.PhysicalCountView
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import java.time.Instant

interface PhysicalCountViewRepository extends JpaRepository<PhysicalCountView, UUID> {
	
	@Query(value = "select q from PhysicalCountView q where q.dateTrans = :transDate AND q.department.id = :departmentID ")
	List<PhysicalCountView> getPhysicalCountViewByDept(@Param('departmentID') UUID departmentID, @Param('transDate') Instant transDate)
	
}
