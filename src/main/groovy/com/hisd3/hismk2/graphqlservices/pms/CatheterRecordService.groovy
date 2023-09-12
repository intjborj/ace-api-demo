package com.hisd3.hismk2.graphqlservices.pms

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.CatheterRecord
import com.hisd3.hismk2.domain.pms.CentralLineRecord
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.repository.pms.CatheterRecordRepository
import com.hisd3.hismk2.repository.pms.CentralLineRecordRepository
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
class CatheterRecordService {

    @Autowired
    CatheterRecordRepository catheterRecordRepository

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    CaseRepository caseRepository

    @GraphQLQuery(name = "getCatheterRecords", description = "Get all Catheter Records")
    List<CatheterRecord> searchCatheterByCase(@GraphQLArgument(name = "id") UUID id) {
        return catheterRecordRepository.searchCatheterByCase(id).sort{ it.createdDate }
    }


    @GraphQLMutation
    CatheterRecord deleteCatheterRecord(
            @GraphQLArgument(name = "id") UUID id
    )
    {
        if (id) {
            return catheterRecordRepository.deleteById(id)
        }
    }



    @GraphQLMutation
    CatheterRecord upsertCatheterRecord(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "aCase") UUID aCase,
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    )
    {
        Case pCase = null
        caseRepository.findById(aCase).ifPresent{pCase = it}

        if(!pCase) throw new RuntimeException("No case found.")
        if (id) {
            CatheterRecord catheterRecordObj = catheterRecordRepository.findById(id).get()
            catheterRecordObj.aCase = pCase
            objectMapper.updateValue(catheterRecordObj, fields)
            return catheterRecordRepository.save(catheterRecordObj)
        } else {
            CatheterRecord catheterRecordObj = objectMapper.convertValue(fields, CatheterRecord)
            catheterRecordObj.aCase = pCase
            def result =  catheterRecordRepository.save(catheterRecordObj)
            return result
        }
    }

}
