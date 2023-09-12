package com.hisd3.hismk2.repository.clinic

import com.hisd3.hismk2.domain.clinic.Clinic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ClinicRepository extends JpaRepository<Clinic, UUID> {
	@Query(value = "select c from Clinic c")
	List<Clinic> findAllClinics()
	
	@Query(value = "select c from Clinic c where lower(clinicName) like lower(concat('%',:name,'%')) ")
	List<Clinic> findClinicsByName(@Param("name") String name)
}
