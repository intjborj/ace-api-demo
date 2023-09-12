package com.hisd3.hismk2.graphqlservices.ancillary

import com.hisd3.hismk2.dao.ancillary.AncillaryNoteDao
import com.hisd3.hismk2.domain.ancillary.AncillaryNote
import com.hisd3.hismk2.repository.ancillary.AncillaryNoteRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class AncillaryNoteService {
	
	@Autowired
	AncillaryNoteDao ancillaryNoteDao
	
	@Autowired
	AncillaryNoteRepository ancillaryNoteRepository
	
	@GraphQLQuery(name = "AncillaryNotes", description = "Ancillary notes")
	List<AncillaryNote> getAncillaryNotesById(@GraphQLArgument(name = "pkId") UUID pkId) {
		return ancillaryNoteDao.findByOrderSlipItemId(pkId)
	}
	
	@GraphQLQuery(name = "allAncillaryNotes", description = "Ancillary notes")
	List<AncillaryNote> getAllAncillaryNotes() {
		return ancillaryNoteRepository.findAll()
	}
	
	@GraphQLQuery(name = "AncillaryByOrderSlipItem", description = "Ancillary Notes by OrderSlipItem")
	List<AncillaryNote> getAncillaryNotesByOrderSlipItem(@GraphQLArgument(name = "id") String id) {
		return ancillaryNoteDao.findByOrderSlipItemId(UUID.fromString(id))
	}

//    @GraphQLQuery(name = "Service", description = "Get Service By Id")
//    HisService findById(@GraphQLArgument(name = "id") String id) {
//
//        return servicesDao.findById(UUID.fromString(id))
//    }
}
