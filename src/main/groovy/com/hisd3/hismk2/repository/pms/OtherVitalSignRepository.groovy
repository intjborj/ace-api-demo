package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.OtherVitalSign
import org.javers.spring.annotation.JaversSpringDataAuditable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

@JaversSpringDataAuditable
interface OtherVitalSignRepository extends JpaRepository<OtherVitalSign, UUID> {
    @Query(nativeQuery = true, value = 'Select * from pms.other_vital_signs otherVitalSign where otherVitalSign."case" = :parentCase and (deleted <> true or deleted is null) order by otherVitalSign.entry_datetime desc')
    List<OtherVitalSign> getOtherVitalSignsByCase(@Param("parentCase") UUID parentCase)
}
