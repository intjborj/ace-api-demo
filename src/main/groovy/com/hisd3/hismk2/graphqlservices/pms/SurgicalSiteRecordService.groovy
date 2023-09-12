package com.hisd3.hismk2.graphqlservices.pms

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.CatheterRecord
import com.hisd3.hismk2.domain.pms.SurgicalSiteRecord
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.repository.pms.CatheterRecordRepository
import com.hisd3.hismk2.repository.pms.SurgicalSiteRecordRepository
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
class SurgicalSiteRecordService {

    @Autowired
    SurgicalSiteRecordRepository surgicalSiteRecordRepository

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    CaseRepository caseRepository

    @GraphQLQuery(name = "getSurgicalSiteRecords", description = "Get all Surgical Site Records")
    List<SurgicalSiteRecord> getSurgicalSiteRecords(@GraphQLArgument(name = "id") UUID id) {
        return surgicalSiteRecordRepository.searchSurgicalSiteByCase(id).sort{ it.createdDate }
    }


    @GraphQLMutation
    SurgicalSiteRecord deleteSurgicalSiteRecord(
            @GraphQLArgument(name = "id") UUID id
    )
    {
        if (id) {
            return surgicalSiteRecordRepository.deleteById(id)
        }
    }



    @GraphQLMutation
    SurgicalSiteRecord upsertSurgicalSiteRecord(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "aCase") UUID aCase,
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    )
    {
        Case pCase = null
        caseRepository.findById(aCase).ifPresent{pCase = it}

        if(!pCase) throw new RuntimeException("No case found.")
        if (id) {
            SurgicalSiteRecord surgicalSiteRecordObj = surgicalSiteRecordRepository.findById(id).get()
            surgicalSiteRecordObj.aCase = pCase
            objectMapper.updateValue(surgicalSiteRecordObj, fields)
            return surgicalSiteRecordRepository.save(surgicalSiteRecordObj)
        } else {
            SurgicalSiteRecord surgicalSiteRecordObj = objectMapper.convertValue(fields, SurgicalSiteRecord)
            surgicalSiteRecordObj.aCase = pCase
            def result =  surgicalSiteRecordRepository.save(surgicalSiteRecordObj)
            return result
        }
    }

}
