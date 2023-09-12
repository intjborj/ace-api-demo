package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.PatientOwnMedicineAdministration
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PatientOwnMedicationAdministrationRepository extends JpaRepository<PatientOwnMedicineAdministration, UUID> {
	
	@Query(value = "Select administration from PatientOwnMedicineAdministration administration where administration.patientOwnMedicine.id = :medication order by administration.entryDateTime desc")
	List<PatientOwnMedicineAdministration> getMedicationAdministrations(@Param("medication") UUID medication)
	
	@Query(value = "Select administration from PatientOwnMedicineAdministration administration where administration.patientOwnMedicine.patientCase.id = :caseId order by administration.entryDateTime desc")
	List<PatientOwnMedicineAdministration> getMedicationAdministrationsByCaseId(@Param("caseId") UUID caseId)
	
}
