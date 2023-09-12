package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.NeuroVitalSign
import org.javers.spring.annotation.JaversSpringDataAuditable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

@JaversSpringDataAuditable
interface NeuroVitalSignRepository extends JpaRepository<NeuroVitalSign, UUID> {
	@Query(nativeQuery = true, value = 'Select * from pms.neuro_vital_signs neuroVitalSign where neuroVitalSign."case" = :parentCase and (deleted <> true or deleted is null) order by neuroVitalSign.entry_datetime desc')
	List<NeuroVitalSign> getNeuroVitalSignsByCase(@Param("parentCase") UUID parentCase)
	
	@Query(nativeQuery = true, value = 'Select * from pms.neuro_vital_signs neuroVitalSign where neuroVitalSign."case" = :parentCase order by neuroVitalSign.entry_datetime desc limit 1')
	NeuroVitalSign getLatest(@Param("parentCase") UUID parentCase)
}
