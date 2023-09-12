package com.hisd3.hismk2.graphqlservices.pms

import com.hisd3.hismk2.domain.pms.OccupationalTherapyNote
import com.hisd3.hismk2.domain.pms.PhysicalTherapyNote
import com.hisd3.hismk2.repository.pms.OccupationalTherapyRepository
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
class OccupationalTherapyService {

    @Autowired
    OccupationalTherapyRepository occupationalTherapyRepository

    //============== All Queries ====================

    @GraphQLQuery(name = "occupationalTherapyNote", description = "Get Occupational Therapy Note By Id")
    OccupationalTherapyNote findById(@GraphQLArgument(name = "id") UUID id) {
        return occupationalTherapyRepository.findById(id).get()
    }

    @GraphQLQuery(name = "getOccupationalTherapyNotePageable")
    Page<OccupationalTherapyNote> getOccupationalTherapyNotePageable(
            @GraphQLArgument(name = "id") UUID caseId,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "pageSize") Integer pageSize
    ){
        return occupationalTherapyRepository.getOccupationalTherapyNotePageable(caseId, PageRequest.of(page, pageSize, Sort.Direction.DESC, 'entryDateTime'))
    }
}
