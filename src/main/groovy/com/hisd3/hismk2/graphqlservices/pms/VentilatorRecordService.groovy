package com.hisd3.hismk2.graphqlservices.pms

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.Vaccination
import com.hisd3.hismk2.domain.pms.VentilatorRecord
import com.hisd3.hismk2.domain.pms.VentilatorRecordItem
import com.hisd3.hismk2.repository.pms.CaseRepository
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
class VentilatorRecordService {
    @Autowired
    VentilatorRecordRepository ventilatorRecordRepository

    @Autowired
    VentilatorRecordItemRepository ventilatorRecordItemRepository

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    CaseRepository caseRepository

    @GraphQLQuery(name = "getVentilatorRecords", description = "Get all Ventilator Records")
    List<VentilatorRecord> searchVentilatorByCase(@GraphQLArgument(name = "id") UUID id) {
        return ventilatorRecordRepository.searchVentilatorByCase(id).sort{ it.createdDate }
    }


    @GraphQLMutation
    VentilatorRecord deleteVentilatorRecord(
            @GraphQLArgument(name = "id") UUID id
    )
    {
        if (id) {
            return ventilatorRecordRepository.deleteById(id)
        }
    }



    @GraphQLMutation
    VentilatorRecord upsertVentilatorRecord(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "aCase") UUID aCase,
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    )
    {
        Case pCase = null
        caseRepository.findById(aCase).ifPresent{pCase = it}

        if(!pCase) throw new RuntimeException("No case found.")
        if (id) {
            VentilatorRecord ventilatorRecordObj = ventilatorRecordRepository.findById(id).get()
            ventilatorRecordObj.aCase = pCase
            objectMapper.updateValue(ventilatorRecordObj, fields)
            return ventilatorRecordRepository.save(ventilatorRecordObj)
        } else {
            VentilatorRecord ventilatorRecordObj = objectMapper.convertValue(fields, VentilatorRecord)
            ventilatorRecordObj.aCase = pCase
            def result =  ventilatorRecordRepository.save(ventilatorRecordObj)
            return result
        }
    }

}
