package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.DoctorOrderProgressNote
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DoctorOrderProgressNoteRepository extends JpaRepository<DoctorOrderProgressNote, UUID> {
	
	@Query(nativeQuery = true, value = 'Select * from pms.doctor_order_progress_notes doctorOrderProgressNote where doctorOrderProgressNote.doctor_order = :doctorOrder order by doctorOrderProgressNote.entry_datetime desc')
	List<DoctorOrderProgressNote> getDoctorOrderProgressNotesByDoctorOrder(@Param("doctorOrder") UUID doctorOrder)
}
