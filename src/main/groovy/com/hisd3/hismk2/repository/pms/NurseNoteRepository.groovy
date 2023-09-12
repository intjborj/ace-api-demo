package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.NurseNote
import org.javers.spring.annotation.JaversSpringDataAuditable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import java.time.Instant

@JaversSpringDataAuditable
interface NurseNoteRepository extends JpaRepository<NurseNote, UUID> {
	
	@Query(value = "Select nn from NurseNote nn where nn.parentCase.id = :parentCase")
	List<NurseNote> getNurseNotesByCase(@Param("parentCase") UUID parentCase)
	
	@Query(value = "Select nn from NurseNote nn where nn.parentCase.id = :parentCase",
			countQuery = "Select count(nn) from NurseNote nn where nn.parentCase.id = :parentCase"
	)
	Page<NurseNote> getNurseNotesByCasePageable(@Param("parentCase") UUID parentCase, Pageable pageable)
	
	@Query(value = "Select nn from NurseNote nn where nn.parentCase.id = :parentCase and (nn.entryDateTime between :from and :to)")
	List<NurseNote> getNurseNotesByCase(@Param("parentCase") UUID parentCase, @Param("from") Instant from, @Param("to") Instant to)

//	@Query(value = "SELECT c from NurseNote n Where id= :id")
//	List<NurseNote> getAllNurseById(@Param("id") UUID id)
}
