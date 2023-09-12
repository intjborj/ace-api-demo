package com.hisd3.hismk2.dao.ancillary

import com.hisd3.hismk2.domain.ancillary.IntegrationConfig
import com.hisd3.hismk2.repository.ancillary.IntegrationConfigRepository
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@TypeChecked
@Service
@Transactional
class IntegrationConfigDao {
	@Autowired
	IntegrationConfigRepository integrationConfigRepository
	
	List<IntegrationConfig> getActiveIntegration() {
		return integrationConfigRepository.getActiveIntegration()
		
	}
}
