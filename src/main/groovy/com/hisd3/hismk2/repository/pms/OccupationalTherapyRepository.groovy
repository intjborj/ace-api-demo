package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.OccupationalTherapyNote
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface OccupationalTherapyRepository extends JpaRepository<OccupationalTherapyNote, UUID> {

    @Query(value = " Select dn from OccupationalTherapyNote dn where dn.parentCase.id = :parentCase ",
            countQuery = "Select count(dn) from OccupationalTherapyNote dn where dn.parentCase.id = :parentCase"
    )
    Page<OccupationalTherapyNote> getOccupationalTherapyNotePageable(@Param("parentCase") UUID parentCase, Pageable pageable)

}