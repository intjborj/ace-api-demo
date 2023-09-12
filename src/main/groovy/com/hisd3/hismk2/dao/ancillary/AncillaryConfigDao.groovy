package com.hisd3.hismk2.dao.ancillary

import com.hisd3.hismk2.domain.ancillary.AncillaryConfig
import com.hisd3.hismk2.repository.ancillary.AncillaryConfigRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLQuery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@TypeChecked
@Service
@Transactional
class AncillaryConfigDao {
	@Autowired
	AncillaryConfigRepository ancillaryConfigRepository
	
	@GraphQLQuery(name = "config", description = "Get All Config")
	List<AncillaryConfig> findAll() {
		ancillaryConfigRepository.findAll()
	}
	
	List<AncillaryConfig> getConfigByName(String name) {
		return ancillaryConfigRepository.getConfigByEntityName(name)
	}
	
	List<AncillaryConfig> getConfigByDepartment(String name) {
		return ancillaryConfigRepository.getConfigByEntityName(name)
	}
}
