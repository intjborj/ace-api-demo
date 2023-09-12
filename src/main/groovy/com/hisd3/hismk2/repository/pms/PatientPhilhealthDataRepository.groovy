package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.PatientPhilhealthData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PatientPhilhealthDataRepository extends JpaRepository<PatientPhilhealthData, UUID> {
	@Query(value = 'select p from PatientPhilhealthData p where p.parentCase.id = :caseId',
			countQuery = 'select count(p) from PatientPhilhealthData p where p.parentCase.id = :caseId')
	List<PatientPhilhealthData> findByCaseId(@Param('caseId') UUID caseId)
}
