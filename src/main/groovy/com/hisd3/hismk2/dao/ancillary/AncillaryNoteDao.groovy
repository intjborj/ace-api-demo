package com.hisd3.hismk2.dao.ancillary

import com.hisd3.hismk2.domain.ancillary.AncillaryNote
import com.hisd3.hismk2.repository.ancillary.AncillaryNoteRepository
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@TypeChecked
@Service
@Transactional
class AncillaryNoteDao {
	
	@Autowired
	private AncillaryNoteRepository ancillaryNoteRepository
	
	AncillaryNote findById(UUID id) {
		return ancillaryNoteRepository.findById(id).get()
	}
	
	List<AncillaryNote> findByOrderSlipItemId(UUID id) {
		return ancillaryNoteRepository.findByOrderSlipItemId(id)
	}
}
