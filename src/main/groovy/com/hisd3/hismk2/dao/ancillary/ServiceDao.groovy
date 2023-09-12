package com.hisd3.hismk2.dao.ancillary

import com.hisd3.hismk2.domain.ancillary.Service as HisService
import com.hisd3.hismk2.repository.ancillary.ServiceRepository
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@TypeChecked
@Service
@Transactional
class ServiceDao {
	
	@Autowired
	private ServiceRepository servicesRepository
	
	@PersistenceContext
	EntityManager entityManager
	
	List<HisService> findAll() {
		return servicesRepository.listAll()
	}
	
	HisService findById(UUID id) {
		return servicesRepository.findById(id).get()
	}
	
	List<HisService> searchHisServices(String filter) {
		return servicesRepository.searchlist(filter)
	}
	
	List<HisService> searchHisServicesByDepartment(UUID department, String filter) {
		return servicesRepository.searchlistByDepartment(department, filter)
	}
	
	HisService save(HisService serviceitem) {
		servicesRepository.save(serviceitem)
	}
}
