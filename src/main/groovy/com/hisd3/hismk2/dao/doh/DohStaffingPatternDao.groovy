package com.hisd3.hismk2.dao.doh

import com.hisd3.hismk2.domain.Authority
import com.hisd3.hismk2.domain.User
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.rest.dto.DohStaffingPatternDto
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@TypeChecked
@Service
@Transactional
class DohStaffingPatternDao {
	
	@Autowired
	private UserRepository userRepository
	
	@PersistenceContext
	EntityManager entityManager
	
	List<Authority> getStaffingPattern(User user) {
		def mergedUser = entityManager.merge(user)
		mergedUser.authorities.size()
		return mergedUser.authorities as List
	}
	
	DohStaffingPatternDto getStaff() {
//		Integer professiondesignation
//		Integer specialtyboardcertified
//		Integer fulltime40permanent
//		Integer fulltime40contractual
//		Integer parttimepermanent
//		Integer parttimecontractual
//		Integer activerotatingaffiliate
//		Integer outsourced
	
	}
}
