package com.hisd3.hismk2.graphqlservices.pms

import com.hisd3.hismk2.domain.pms.CathLabNote
import com.hisd3.hismk2.repository.pms.CathLabNoteRepository
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
class CathLabNoteService {

    @Autowired
    CathLabNoteRepository cathLabNoteRepository

    //============== All Queries ====================


    @GraphQLQuery(name = "cathlabNurseNote", description = "Get CathLab Nurse Note By Id")
    CathLabNote findById(@GraphQLArgument(name = "id") UUID id) {
        return cathLabNoteRepository.findById(id).get()
    }

    @GraphQLQuery(name = "getCathLabNotePageable")
    Page<CathLabNote> getCathLabNotePageable(
            @GraphQLArgument(name = "id") UUID caseId,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "pageSize") Integer pageSize
    ){
        return cathLabNoteRepository.getCathLabNotePageable(caseId, PageRequest.of(page, pageSize, Sort.Direction.DESC, 'entryDateTime'))
    }
}
