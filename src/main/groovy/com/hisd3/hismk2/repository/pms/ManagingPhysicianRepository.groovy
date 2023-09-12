package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.ManagingPhysician
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ManagingPhysicianRepository extends JpaRepository<ManagingPhysician, UUID> {
	
	@Query(value = "Select mp from ManagingPhysician mp where mp.parentCase.id = :parentCase and mp.position not like 'STAFF' ")
	List<ManagingPhysician> getManagingPhysiciansByCase(@Param("parentCase") UUID parentCase)
	
	@Query(value = "Select mp from ManagingPhysician mp where mp.parentCase.id = :parentCase and mp.position = 'STAFF' ")
	List<ManagingPhysician> getManagingStaffByCase(@Param("parentCase") UUID parentCase)

	@Query(value = "Select mp from ManagingPhysician mp where mp.parentCase.id = :parentCase and mp.employee.id = :employeeId")
	List<ManagingPhysician> getManagingStaffByCase(@Param("parentCase") UUID parentCase, @Param("employeeId") UUID employeeId)
	
	@Query(value = "Select mp from ManagingPhysician mp where mp.parentCase.id = :parentCase and mp.position = 'PHYSICIAN' and mp.employee.id = :employeeId")
	List<ManagingPhysician> getPhysician(@Param("parentCase") UUID parentCase, @Param("employeeId") UUID employeeId)

}
