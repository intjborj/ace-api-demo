package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.SpeechTherapyNote
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SpeechTherapyNoteRepository extends JpaRepository<SpeechTherapyNote, UUID> {

    @Query(value = " Select dn from SpeechTherapyNote dn where dn.parentCase.id = :parentCase ",
            countQuery = "Select count(dn) from SpeechTherapyNote dn where dn.parentCase.id = :parentCase"
    )
    Page<SpeechTherapyNote> getSpeechTherapyNotePageable(@Param("parentCase") UUID parentCase, Pageable pageable)
}