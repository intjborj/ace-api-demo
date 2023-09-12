package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.FileAttachment
import groovy.transform.TypeChecked
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

@TypeChecked

interface FileAttachmentRepository extends JpaRepository<FileAttachment, UUID> {
	
	@Query(value = "Select i from FileAttachment i where i.patientCase.id = :parentCase")
	List<FileAttachment> casePhotosByCase(@Param("parentCase") UUID parentCase)
	
	@Query(
			value = "Select f from FileAttachment f where f.patient.id =:id"
	)
	List<FileAttachment> findByPid(@Param("id") UUID id)
	
}
