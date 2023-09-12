package com.hisd3.hismk2.repository.ancillary

import com.hisd3.hismk2.domain.ancillary.AncillaryNote
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AncillaryNoteRepository extends JpaRepository<AncillaryNote, UUID> {
	
	@Query(value = "Select ancillaryNote from AncillaryNote ancillaryNote where ancillaryNote.pkId = :orderSlipId")
	
	List<AncillaryNote> findByOrderSlipItemId(@Param("orderSlipId") UUID orderSlipId)
}


