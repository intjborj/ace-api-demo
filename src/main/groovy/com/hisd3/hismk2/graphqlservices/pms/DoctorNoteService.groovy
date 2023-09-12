package com.hisd3.hismk2.graphqlservices.pms

import com.hisd3.hismk2.domain.pms.DoctorNote
import com.hisd3.hismk2.domain.pms.NurseNote
import com.hisd3.hismk2.repository.pms.DoctorNotesRepository
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
class DoctorNoteService {

    @Autowired
    DoctorNotesRepository doctorNotesRepository

    //============== All Queries ====================

    @GraphQLQuery(name = "doctorNote", description = "Get DoctorNote By Id")
    DoctorNote findById(@GraphQLArgument(name = "id") UUID id) {
        return doctorNotesRepository.findById(id).get()
    }

    @GraphQLQuery(name = "doctorNotesByCasePageable")
    Page<DoctorNote> getPatientPageable(
            @GraphQLArgument(name = "id") UUID caseId,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "pageSize") Integer pageSize
    ){
        return doctorNotesRepository.getDoctorNotePageable(caseId, PageRequest.of(page, pageSize, Sort.Direction.DESC, 'entryDateTime'))
    }
}
