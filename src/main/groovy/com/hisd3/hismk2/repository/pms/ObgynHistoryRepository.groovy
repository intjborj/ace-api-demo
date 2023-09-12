package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.ObgynHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ObgynHistoryRepository extends JpaRepository<ObgynHistory, UUID> {
	@Query(value = "Select ob from ObgynHistory ob where ob.aCase.id = :caseId")
	List<ObgynHistory> getAllByCaseAndId(@Param("caseId") UUID caseId)
}
