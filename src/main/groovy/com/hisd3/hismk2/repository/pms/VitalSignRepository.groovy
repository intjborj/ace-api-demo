package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.VitalSign
import org.javers.spring.annotation.JaversSpringDataAuditable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import java.time.Instant

@JaversSpringDataAuditable
interface VitalSignRepository extends JpaRepository<VitalSign, UUID> {
	@Query(nativeQuery = true, value = 'Select * from pms.vital_signs vitalSign where vitalSign."case" = :parentCase and (deleted <> true or deleted is null) order by vitalSign.entry_datetime desc')
	List<VitalSign> getVitalSignsByCase(@Param("parentCase") UUID parentCase)
	
	@Query(nativeQuery = true, value = 'Select * from pms.vital_signs vitalSign where vitalSign."case" = :parentCase order by vitalSign.entry_datetime desc limit 1')
	VitalSign getLatest(@Param("parentCase") UUID parentCase)
	
	@Query(value = "select vitalSign from VitalSign vitalSign where vitalSign.parentCase.id = :parentCase and vitalSign.entryDateTime between :from and :to")
	List<VitalSign> getVitalSignFromTo(@Param("parentCase") UUID caseId, @Param('from') Instant from, @Param('to') Instant to)

	@Query(value = "select vitalSign from VitalSign vitalSign where vitalSign.note = 'initial' and vitalSign.parentCase.id = :parentCase")
	VitalSign isInitialExists(@Param("parentCase") UUID parentCase)
}
