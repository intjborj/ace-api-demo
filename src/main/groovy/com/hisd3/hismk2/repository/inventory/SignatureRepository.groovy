package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.Signature
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SignatureRepository extends JpaRepository<Signature, UUID> {

	@Query(value = "Select s from Signature s where s.department.id = :departmentId and s.currentUsers = true and s.signatureType = :type")
	Signature findActiveSignatureDept(@Param("departmentId") UUID departmentId,@Param("type") String type)

	@Query(value = "Select s from Signature s where s.department.id = :departmentId and s.sequence = :sequence and s.signatureType = :type")
	Signature findSignatureSequenceDept(@Param("departmentId") UUID departmentId,@Param("sequence") Integer sequence,@Param("type") String type)

	@Query(value = "Select count(s) from Signature s where s.department.id = :departmentId and s.signatureType = :type")
	Integer countNumberofSignature(@Param("departmentId") UUID departmentId,@Param("type") String type)

}
