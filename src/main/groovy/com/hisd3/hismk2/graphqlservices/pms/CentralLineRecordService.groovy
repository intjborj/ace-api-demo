package com.hisd3.hismk2.graphqlservices.pms

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.CentralLineRecord
import com.hisd3.hismk2.domain.pms.VentilatorRecord
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.repository.pms.CentralLineRecordRepository
import com.hisd3.hismk2.repository.pms.VentilatorRecordItemRepository
import com.hisd3.hismk2.repository.pms.VentilatorRecordRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class CentralLineRecordService {

    @Autowired
    CentralLineRecordRepository centralLineRecordRepository

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    CaseRepository caseRepository

    @GraphQLQuery(name = "getCentralLineRecords", description = "Get all Central Line Records")
    List<CentralLineRecord> searchCentralLineByCase(@GraphQLArgument(name = "id") UUID id) {
        return centralLineRecordRepository.searchCentralLineByCase(id).sort{ it.createdDate }
    }


    @GraphQLMutation
    CentralLineRecord deleteCentralLineRecord(
            @GraphQLArgument(name = "id") UUID id
    )
    {
        if (id) {
            return centralLineRecordRepository.deleteById(id)
        }
    }



    @GraphQLMutation
    CentralLineRecord upsertCentralLineRecord(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "aCase") UUID aCase,
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    )
    {
        Case pCase = null
        caseRepository.findById(aCase).ifPresent{pCase = it}

        if(!pCase) throw new RuntimeException("No case found.")
        if (id) {
            CentralLineRecord centralLineRecordObj = centralLineRecordRepository.findById(id).get()
            centralLineRecordObj.aCase = pCase
            objectMapper.updateValue(centralLineRecordObj, fields)
            return centralLineRecordRepository.save(centralLineRecordObj)
        } else {
            CentralLineRecord centralLineRecordObj = objectMapper.convertValue(fields, CentralLineRecord)
            centralLineRecordObj.aCase = pCase
            def result =  centralLineRecordRepository.save(centralLineRecordObj)
            return result
        }
    }

}
