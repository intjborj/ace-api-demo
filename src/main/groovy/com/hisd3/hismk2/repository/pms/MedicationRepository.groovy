package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.Medication
import org.javers.spring.annotation.JaversSpringDataAuditable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

@JaversSpringDataAuditable
interface MedicationRepository extends JpaRepository<Medication, UUID> {

	@Query(value = "Select medication from Medication medication where medication.parentCase.id = :parentCase")
	List<Medication> getMedicationsByCase(@Param("parentCase") UUID parentCase)

	@Query(value = "SELECT m FROM Medication m WHERE m.id = :medicine")
	Medication getMedicationByItemId(@Param("medicine") UUID medicine)

	@Query(value = "Select medication from Medication medication where medication.medicine.id = :medicine")
	List<Medication> getMedicationsByItemId(@Param("medicine") UUID medicine)

	@Query(value = "Select m from Medication m where m.parentCase.id = :parentCase and m.type = :type and (m.route is not 'NEBULIZATION' or m.route is null)")
	List<Medication> getMedicationsByCaseAndType(@Param("parentCase") UUID parentCase, @Param("type") String type)

	@Query(value = "Select m from Medication m where m.parentCase.id = :parentCase and m.route = 'NEBULIZATION'")
	List<Medication> getMedicationsByCaseAndTypeNebulization(@Param("parentCase") UUID parentCase)

	@Query(value = "Select medication from Medication medication where medication.parentCase.id = :parentCase and (medication.type = 'STAT' or medication.type = 'PRN')")
	List<Medication> getMedicationsByCaseAndTypeStatPrn(@Param("parentCase") UUID parentCase)

	@Query(value = "Select medication from Medication medication where medication.parentCase.id = :parentCase and medication.medicine.fluid = true and (medication.type != 'STANDING')")
	List<Medication> getMedicationsByCaseForIV(@Param("parentCase") UUID parentCase)

	@Query(value = "Select medication from Medication medication where medication.doctorsOrderItemId = :doItemId ")
	List<Medication> getMedicationsWhereDoItemId(@Param("doItemId") UUID doItemId)

}
