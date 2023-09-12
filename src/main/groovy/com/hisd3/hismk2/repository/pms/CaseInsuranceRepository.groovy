package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.CaseInsurance
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CaseInsuranceRepository extends JpaRepository<CaseInsurance, UUID> {
	@Query(value = "Select ci from CaseInsurance ci where ci.parentCase.id = :parentCase")
	List<CaseInsurance> getCaseInsurancesByCase(@Param("parentCase") UUID parentCase)
}
