package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.PatientOwnMedicine
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PatientOwnMedicineRepository extends JpaRepository<PatientOwnMedicine, UUID> {
	
	@Query(value = "Select medication from PatientOwnMedicine medication where medication.patientCase.id = :caseId")
	List<PatientOwnMedicine> getPatientOwnMedicationsByCase(@Param("caseId") UUID caseId)
}
