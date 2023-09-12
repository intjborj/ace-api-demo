package com.hisd3.hismk2.graphqlservices.pms

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.OtherVitalSign
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.pms.CaseRepository
import com.hisd3.hismk2.repository.pms.OtherVitalSignRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.sql.Timestamp

@TypeChecked
@Component
@GraphQLApi
class OtherVitalSignService {

    @Autowired
    private OtherVitalSignRepository otherVitalSignRepository

    @Autowired
    private EmployeeRepository employeeRepository

    @Autowired
    private CaseRepository caseRepository

    @Autowired
    ObjectMapper objectMapper

    //============== All Queries ====================

    @GraphQLQuery(name = "otherVitalSigns", description = "Get all OtherVitalSigns")
    List<OtherVitalSign> findAll() {
        return otherVitalSignRepository.findAll().sort { it.entryDateTime }
    }

    @GraphQLQuery(name = "otherVitalSign", description = "Get OtherVitalSign By Id")
    OtherVitalSign findById(@GraphQLArgument(name = "id") UUID id) {
        return otherVitalSignRepository.findById(id).get()
    }

    @GraphQLQuery(name = "otherVitalSignsByCase", description = "Get all OtherVitalSigns by Case Id")
    List<OtherVitalSign> getOtherVitalSignsByCase(@GraphQLArgument(name = "caseId") UUID caseId) {
        return otherVitalSignRepository.getOtherVitalSignsByCase(caseId)
    }

    @GraphQLMutation
    def addOtherVitals(
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    ) {
        OtherVitalSign otherVitalSign = new OtherVitalSign().tap {
            entryDateTime = Timestamp.valueOf(fields.get("entryDateTime") as String).toInstant()
            crt = fields.get("crt")
            cbs = fields.get("cbs")
            cgs = fields.get("cgs")
            cbc = fields.get("cbc")
            employee = objectMapper.convertValue(fields.get("employee"), Employee)
            parentCase = objectMapper.convertValue(fields.get("parentCase"), Case)
        }

        otherVitalSignRepository.save(otherVitalSign)
    }

    @GraphQLMutation
    OtherVitalSign addOtherVitalSignsForFlutter(
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    ) {
        return otherVitalSignRepository.save(
                new OtherVitalSign().tap {
                    entryDateTime = Timestamp.valueOf(fields.get("entryDateTime") as String).toInstant()
                    crt = fields.get("crt")
                    cbs = fields.get("cbs")
                    cgs = fields.get("cgs")
                    cbc = fields.get("cbc")
                    employee = objectMapper.convertValue(fields.get("employee"), Employee)
                    parentCase = objectMapper.convertValue(fields.get("parentCase"), Case)
                }
        )
    }
}
