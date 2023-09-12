package com.hisd3.hismk2.graphqlservices.pms

import com.hisd3.hismk2.domain.pms.DoctorNote
import com.hisd3.hismk2.domain.pms.PhysicalTherapyNote
import com.hisd3.hismk2.repository.pms.PhysicalTherapyNoteRepository
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
class PhysicalTherapyNoteService {

    @Autowired
    PhysicalTherapyNoteRepository physicalTherapyNoteRepository

    //============== All Queries ====================

    @GraphQLQuery(name = "physicalTherapyNote", description = "Get Physical Therapy Note By Id")
    PhysicalTherapyNote findById(@GraphQLArgument(name = "id") UUID id) {
        return physicalTherapyNoteRepository.findById(id).get()
    }

    @GraphQLQuery(name = "getPhysicalTherapyNotePageable")
    Page<PhysicalTherapyNote> getPhysicalTherapyNotePageable(
            @GraphQLArgument(name = "id") UUID caseId,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "pageSize") Integer pageSize
    ){
        return physicalTherapyNoteRepository.getPhysicalTherapyNotePageable(caseId, PageRequest.of(page, pageSize, Sort.Direction.DESC, 'entryDateTime'))
    }
}
