package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.DischargeClearance
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DischargeClearanceRepository extends JpaRepository<DischargeClearance, UUID> {
	@Query(value = "Select dc from DischargeClearance dc where dc.parentCase.id = :parentCase order by dc.createdDate desc")
	List<DischargeClearance> getDischargeClearanceByCase(@Param("parentCase") UUID parentCase)
}
