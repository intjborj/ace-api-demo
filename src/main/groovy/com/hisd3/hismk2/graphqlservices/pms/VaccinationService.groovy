package com.hisd3.hismk2.graphqlservices.pms

import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.Vaccination
import com.hisd3.hismk2.repository.pms.VaccinationRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class VaccinationService {

    @Autowired
    VaccinationRepository    vaccinationRepository

//============== All Queries ====================

    @GraphQLQuery(name = "vaccination", description = "Get all Vaccination")
    List<Vaccination> findAll() {
        return vaccinationRepository.listAll()
    }

    @GraphQLQuery(name = "vaccinationSearchByPatient", description = "Get Vaccination by Patient")
    List<Vaccination> searchByPatient(@GraphQLArgument(name = "id") UUID id) {
        def results = vaccinationRepository.searchByPatient(id).sort{it.createdDate}
        results.reverse(true)
    }

}
