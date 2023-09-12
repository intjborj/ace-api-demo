package com.hisd3.hismk2.dao.census

import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.billing.PriceTierDetail
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.Transfer
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.repository.pms.TransferRepository
import com.hisd3.hismk2.rest.dto.DepartmentCensusDto
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.data.domain.Page
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

import javax.transaction.Transactional
import java.sql.Timestamp
import java.time.Instant

@TypeChecked
@Service
@Transactional
class CaseDao extends AbstractDaoService<Case> {
	CaseDao() {
		super(Case.class)
	}
	@Autowired
	CaseRepository caseRepository

	@Autowired
	TransferRepository transferRepository

	@Autowired
	DepartmentRepository departmentRepository

	@Autowired
	JdbcTemplate jdbcTemplate

	List<Case> getAllCase() {
		
		return caseRepository.getAllPatientCase()
	}
	
	List<Case> getAllCasesToday() {
		
		return caseRepository.getAllCaseByDate(Instant.now())
	}
	
	List<Case> getAllCasesByRegistryType(String allcasesbyregistrytype, String from, String to) {
		return caseRepository.getAllCasesByRegistryType(allcasesbyregistrytype)
	}
	
	List<Case> getAllCaseByEntryDatetime(String registryType, String from, String to) {
		
		Timestamp sFromStamp = Timestamp.valueOf(from)
		Timestamp sToStamp = Timestamp.valueOf(to)
		
		Instant sFrom = sFromStamp.toInstant()
		Instant sTo = sToStamp.toInstant()
		
		return caseRepository.getAllCaseByEntryDatetime(registryType, sFrom, sTo)
	}
	
	List<Case> getAllAccommodationType(String accommodation) {
		return caseRepository.getAllAccommodationType(accommodation)
	}
	
	List<Case> getAllInPatients() {
		return caseRepository.getAllInPatients()
	}

	List<DepartmentCensusDto> getCensus(String filter, Integer year) {

		List<Department> departmentList = departmentRepository.getPatientsDepartments()
		return []
	}
}
