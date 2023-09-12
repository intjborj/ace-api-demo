package com.hisd3.hismk2.repository.dietary

import com.hisd3.hismk2.domain.dietary.Diet
import com.hisd3.hismk2.domain.dietary.DietLog
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DietLogRepository extends JpaRepository<DietLog, UUID> {
	@Query(value = "select d from DietLog  d where d.aCase.id = :caseId order by d.createdDate desc ")
	Page<DietLog> findByCaseId(@Param("caseId") UUID caseId, Pageable pageable)
	
	@Query(nativeQuery = true, value = 'Select * from dietary.patient_diet_log dietLog where dietLog."case" = :parentCase order by dietLog.date_added desc limit 1')
	DietLog getLatest(@Param("parentCase") UUID parentCase)
}
