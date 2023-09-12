package com.hisd3.hismk2.graphqlservices.pms

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.pms.Case
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
class VentilatorRecordItemService {
    @Autowired
    VentilatorRecordItemRepository ventilatorRecordItemRepository

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    VentilatorRecordRepository ventilatorRecordRepository


    @GraphQLQuery(name = "getVentilatorRecordItems", description = "Get all Ventilator Record Items")
    List<VentilatorRecordItem> searchVentilatorItemByVR(@GraphQLArgument(name = "id") UUID id) {
        return ventilatorRecordItemRepository.searchVentilatorItemByVR(id)
    }

    @GraphQLMutation
    VentilatorRecordItem upsertVentilatorRecordItem(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "ventilatorRecord") UUID ventilatorRecord,
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    )
    {
        VentilatorRecord vRecord = null
        ventilatorRecordRepository.findById(ventilatorRecord).ifPresent{vRecord = it}

        if(!vRecord) throw new RuntimeException("No ventilator record found.")
        if (id) {
            VentilatorRecordItem ventilatorRecordItemObj = ventilatorRecordItemRepository.findById(id).get()
            ventilatorRecordItemObj.ventilatorRecord = vRecord
            objectMapper.updateValue(ventilatorRecordItemObj, fields)
            return ventilatorRecordItemRepository.save(ventilatorRecordItemObj)
        } else {
            VentilatorRecordItem ventilatorRecordItemObj = objectMapper.convertValue(fields, VentilatorRecordItem)
            ventilatorRecordItemObj.ventilatorRecord = vRecord
            def result =  ventilatorRecordItemRepository.save(ventilatorRecordItemObj)
            return result
        }
    }


    @GraphQLMutation
    VentilatorRecordItem deleteVentilatorRecordItem(
            @GraphQLArgument(name = "id") UUID id
    )
    {
        if (id) {
            return ventilatorRecordItemRepository.deleteById(id)
        }
    }

}
