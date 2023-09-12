package com.hisd3.hismk2.dao.census

import com.hisd3.hismk2.repository.pms.CaseRepository
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

@TypeChecked
@Service
@Transactional
class DohDao {
	
	@Autowired
	CaseRepository caseRepository
	
}
