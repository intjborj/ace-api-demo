package com.hisd3.hismk2.graphqlservices.pms

import com.hisd3.hismk2.domain.pms.NurseNote
import com.hisd3.hismk2.repository.pms.NurseNoteRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class NurseNoteService {
	
	@Autowired
	private NurseNoteRepository nurseNoteRepository
	
	//============== All Queries ====================
	
	@GraphQLQuery(name = "nurseNotes", description = "Get all NurseNotes")
	List<NurseNote> findAll() {
		return nurseNoteRepository.findAll().sort { it.entryDateTime }
	}
	
	@GraphQLQuery(name = "nurseNote", description = "Get NurseNote By Id")
	NurseNote findById(@GraphQLArgument(name = "id") UUID id) {
		return nurseNoteRepository.findById(id).get()
	}
	
	@GraphQLQuery(name = "nurseNotesByCase", description = "Get all NurseNotes by Case Id")
	List<NurseNote> getNurseNotesByCase(@GraphQLArgument(name = "caseId") UUID caseId) {
		return nurseNoteRepository.getNurseNotesByCase(caseId).sort { it.entryDateTime }
	}
	
	@GraphQLQuery(name = "nurseNotesByCasePageable")
	Page<NurseNote> getByPatientPageable(
			@GraphQLArgument(name = "id") UUID caseId,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "pageSize") Integer pageSize
	) {
		return nurseNoteRepository.getNurseNotesByCasePageable(caseId, PageRequest.of(page, pageSize, Sort.Direction.DESC, 'entryDateTime'))
	}

	@GraphQLQuery(name ="nurseNotesById")
	NurseNoteService nurseNoteService(
		@GraphQLArgument(name="id") UUID id
	){
		findById(id) as NurseNoteService
	}

}
