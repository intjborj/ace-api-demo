package com.hisd3.hismk2.dao.hl7Integration

import com.hisd3.hismk2.domain.ancillary.AncillaryNote
import com.hisd3.hismk2.domain.pms.Patient
import com.hisd3.hismk2.repository.ancillary.AncillaryNoteRepository
import com.hisd3.hismk2.rest.dto.OrmDto
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@TypeChecked
@Service
@Transactional
class MessageBuilderDao {
	
	@Autowired
	private AncillaryNoteRepository ancillaryNoteRepository
	
	AncillaryNote findById(UUID id) {
		return ancillaryNoteRepository.findById(id).get()
	}
	
	List<AncillaryNote> findByOrderSlipItemId(UUID id) {
		return ancillaryNoteRepository.findByOrderSlipItemId(id)
	}
	
	String messageBuild(UUID patientId) {
		Patient patient = getPatient(patientId)
		OrmDto orm = new OrmDto()
		
	}
	
	Patient getPatient(UUID id) {
		return new Patient()
	}
}
