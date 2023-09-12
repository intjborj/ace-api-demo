package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.PhysicalTherapyNote
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import java.time.Instant

interface PhysicalTherapyNoteRepository extends JpaRepository<PhysicalTherapyNote, UUID> {

    @Query(value = " Select dn from PhysicalTherapyNote dn where dn.parentCase.id = :parentCase ",
            countQuery = "Select count(dn) from PhysicalTherapyNote dn where dn.parentCase.id = :parentCase"
    )
    Page<PhysicalTherapyNote> getPhysicalTherapyNotePageable(@Param("parentCase") UUID parentCase, Pageable pageable)

    @Query(value = "Select nn from PhysicalTherapyNote nn where nn.parentCase.id = :parentCase and (nn.entryDateTime between :from and :to)")
    List<PhysicalTherapyNote> getPhysicalTherapyNoteByCase(@Param("parentCase") UUID parentCase, @Param("from") Instant from, @Param("to") Instant to)
}