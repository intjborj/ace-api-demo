package com.hisd3.hismk2.repository.eclaims

import com.hisd3.hismk2.domain.eclaims.EclaimsIntegrationAccount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface EcAccountRepository extends JpaRepository<EclaimsIntegrationAccount, UUID> {
	
	@Query(value = '''Select es from EclaimsIntegrationAccount es where es.employee.id=:id ''')
	EclaimsIntegrationAccount findByEmployee(@Param("id") UUID id)
}
