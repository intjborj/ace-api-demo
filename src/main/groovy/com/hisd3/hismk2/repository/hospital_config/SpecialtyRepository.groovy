package com.hisd3.hismk2.repository.hospital_config

import com.hisd3.hismk2.domain.hospital_config.Specialty
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SpecialtyRepository extends JpaRepository<Specialty, UUID> {
	
	@Query(value = "Select specialty from Specialty specialty where specialty.deleted = false or specialty.deleted is null ORDER BY description ASC ")
	List<Specialty> availableSpecialties()
}
