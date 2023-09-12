package com.hisd3.hismk2.repository.eclaims

import com.hisd3.hismk2.domain.eclaims.EclaimsIntegrationSetting
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface EcProviderRepository extends JpaRepository<EclaimsIntegrationSetting, UUID> {
	
	@Query(value = '''Select es from EclaimsIntegrationSetting es where es.provider=:provider ''')
	EclaimsIntegrationSetting findByProvider(@Param("provider") String provider)
}
