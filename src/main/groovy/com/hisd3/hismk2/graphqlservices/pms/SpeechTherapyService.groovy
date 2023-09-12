package com.hisd3.hismk2.graphqlservices.pms

import com.hisd3.hismk2.domain.pms.PhysicalTherapyNote
import com.hisd3.hismk2.domain.pms.SpeechTherapyNote
import com.hisd3.hismk2.repository.pms.SpeechTherapyNoteRepository
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
class SpeechTherapyService {

    @Autowired
    SpeechTherapyNoteRepository speechTherapyNoteRepository

    //============== All Queries ====================

    @GraphQLQuery(name = "speechTherapyNote", description = "Get Speech Therapy Note By Id")
    SpeechTherapyNote findById(@GraphQLArgument(name = "id") UUID id) {
        return speechTherapyNoteRepository.findById(id).get()
    }

    @GraphQLQuery(name = "getSpeechTherapyNotePageable")
    Page<SpeechTherapyNote> getSpeechTherapyNotePageable(
            @GraphQLArgument(name = "id") UUID caseId,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "pageSize") Integer pageSize
    ){
        return speechTherapyNoteRepository.getSpeechTherapyNotePageable(caseId, PageRequest.of(page, pageSize, Sort.Direction.DESC, 'entryDateTime'))
    }

}
