package com.hisd3.hismk2.graphqlservices.hrm

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hrm.SalaryRateMultiplier
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.hrm.SalaryRateMultiplierRepository
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
class SalaryRateMultiplierService {

    @Autowired
    SalaryRateMultiplierRepository salaryRateMultiplierRepository

    @Autowired
    ObjectMapper objectMapper

    //============================Query============================\\

    @GraphQLQuery(name = "getSalaryRateMultiplier", description = "Get the values of salary rate multiplier.")
    SalaryRateMultiplier getSalaryRateMultiplier(){
        SalaryRateMultiplier salaryRateMultiplier = salaryRateMultiplierRepository.findAll().first()
        return salaryRateMultiplier
    }

    //============================Query============================\\

    //===========================Mutation==========================\\

    @GraphQLMutation(name = "updateSalaryRateMultiplier", description = "Update the salary rate multiplier")
    GraphQLRetVal<SalaryRateMultiplier> updateSalaryRateMultiplier(
            @GraphQLArgument(name = "fields")Map<String, Object> fields
    ){
        SalaryRateMultiplier salaryRateMultiplier = salaryRateMultiplierRepository.findAll().first()
        if(!salaryRateMultiplier) return new GraphQLRetVal<SalaryRateMultiplier>(null, false, "Failed to update salary rate multiplier.", null)
        salaryRateMultiplier = objectMapper.updateValue(salaryRateMultiplier, fields)
        salaryRateMultiplierRepository.save(salaryRateMultiplier)
        return new GraphQLRetVal<SalaryRateMultiplier>(salaryRateMultiplier, true, "Successfully updated salary rate multiplier.", null)

    }


    //===========================Mutation==========================\\



}
