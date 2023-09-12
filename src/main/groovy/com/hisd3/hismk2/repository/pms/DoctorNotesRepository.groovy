package com.hisd3.hismk2.repository.pms


import com.hisd3.hismk2.domain.pms.DoctorNote
import com.hisd3.hismk2.domain.pms.NurseNote
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import java.time.Instant

interface DoctorNotesRepository extends JpaRepository<DoctorNote, UUID> {

    @Query(value = " Select dn from DoctorNote dn where dn.parentCase.id = :parentCase ",
            countQuery = "Select count(dn) from DoctorNote dn where dn.parentCase.id = :parentCase"
    )
    Page<DoctorNote> getDoctorNotePageable(@Param("parentCase") UUID parentCase, Pageable pageable)

    @Query(value = "Select nn from DoctorNote nn where nn.parentCase.id = :parentCase and (nn.entryDateTime between :from and :to)")
    List<DoctorNote> getDoctorNotesByCase(@Param("parentCase") UUID parentCase, @Param("from") Instant from, @Param("to") Instant to)
}