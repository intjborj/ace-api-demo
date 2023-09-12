package com.hisd3.hismk2.repository.ancillary

import com.hisd3.hismk2.domain.ancillary.IntegrationConfig
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface IntegrationConfigRepository extends JpaRepository<IntegrationConfig, UUID> {
	
	@Query(value = '''Select i from IntegrationConfig i ''')
	List<IntegrationConfig> getActiveIntegration()
}
