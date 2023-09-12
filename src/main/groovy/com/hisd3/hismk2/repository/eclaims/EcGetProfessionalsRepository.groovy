package com.hisd3.hismk2.repository.eclaims

import com.hisd3.hismk2.domain.eclaims.EclaimsIntegrationAccount
import com.hisd3.hismk2.domain.hrm.Employee
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface EcGetProfessionalsRepository extends JpaRepository<Employee, UUID> {
	
	@Query(value = '''Select es from Employee es where es.id in :ids''')
	List<Employee> findByPFId(@Param("ids") UUID[] ids)
}
