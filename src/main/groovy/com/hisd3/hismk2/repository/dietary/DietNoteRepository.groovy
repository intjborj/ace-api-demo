package com.hisd3.hismk2.repository.dietary


import com.hisd3.hismk2.domain.dietary.DietNotes
import com.hisd3.hismk2.domain.pms.NurseNote
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DietNoteRepository  extends JpaRepository<DietNotes, UUID> {

    @Query(value = """  Select dn from DietNotes dn where dn.caseId.id = :caseId """,
            countQuery = """ Select count(dn) from DietNotes dn where dn.caseId.id = :caseId """
    )
    Page<DietNotes> getDietNotesByCasePageable(@Param("caseId") UUID caseId, Pageable pageable)
}