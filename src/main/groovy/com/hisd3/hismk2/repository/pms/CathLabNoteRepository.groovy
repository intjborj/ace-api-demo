package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.CathLabNote
import com.hisd3.hismk2.domain.pms.NurseNote
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import java.time.Instant

interface CathLabNoteRepository extends JpaRepository<CathLabNote, UUID> {

    @Query(value = " Select dn from CathLabNote dn where dn.parentCase.id = :parentCase ",
            countQuery = "Select count(dn) from PhysicalTherapyNote dn where dn.parentCase.id = :parentCase"
    )
    Page<CathLabNote> getCathLabNotePageable (@Param("parentCase") UUID parentCase, Pageable pageable)

    @Query(value = "Select cl from CathLabNote cl where cl.parentCase.id = :parentCase")
    List<CathLabNote> getCathlabNotesByCaseId (@Param("parentCase") UUID parentCase)



}