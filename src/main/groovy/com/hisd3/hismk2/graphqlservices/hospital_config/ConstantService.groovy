package com.hisd3.hismk2.graphqlservices.hospital_config

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hospital_config.Constant
import com.hisd3.hismk2.domain.hospital_config.ConstantType
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.hospital_config.ConstantRepository
import com.hisd3.hismk2.repository.hospital_config.ConstantTypeRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import javax.persistence.EntityManager

@TypeChecked
@Component
@GraphQLApi
class ConstantService {

    class ConstantTypes {
        static UUID leaveScheduleType = UUID.fromString("eee7917a-82b7-46a8-8dd4-34de9bbb8438")
    }

    @Autowired
    ConstantRepository constantRepository

    @Autowired
    ConstantTypeRepository constantTypeRepository

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    EntityManager entityManager


    //========================QUERIES==============================

    @GraphQLQuery(name = "constants", description = "list of all constants")
    List<Constant> getAllConstants(@GraphQLArgument(name = 'filter') String filter) {

        if (StringUtils.equalsIgnoreCase(filter, "All") || filter == null) {
            return constantRepository.findAll().sort { it.name }
        } else {
            return constantRepository.findByFilter(UUID.fromString(filter)).sort { it.name }
        }
    }

    @GraphQLQuery(name = "constantsByName", description = "list of all constants")
    List<Constant> getConstantByName(@GraphQLArgument(name = 'name') String name) {
        return constantRepository.findByName(name).sort { it.name }

    }

    @GraphQLQuery(name = "constantsById", description = "list of all constants")
    List<Constant> getConstantById(@GraphQLArgument(name = 'id') UUID id) {
        return constantRepository.findByFilter(id).sort { it.name }

    }

    @GraphQLQuery(name = "constants_types", description = "list of all constants types")
    List<ConstantType> getAllConstantTypes() {
        constantTypeRepository.findAll().sort { it.name }
    }

    @GraphQLQuery(name = "constantsByType", description = "Get Constants by type")
    List<Constant> constantsByType(@GraphQLArgument(name = 'type') String type,
                                   @GraphQLArgument(name = 'activeOnly') Boolean activeOnly
    ) {
        if (activeOnly)
            return constantRepository.findActiveByType(ConstantTypes[type] as UUID).sort { it.name }
        else
            return constantRepository.findByFilter(ConstantTypes[type] as UUID).sort { it.name }
    }

    //m===============================MUTATIONS======================================

    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = 'upsertConstantsByType', description = "Upsert Constants")
    GraphQLRetVal<String> upsertConstantsByType(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = 'type') String type,
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    ) {

        Constant constant = new Constant()
        if (id) {
            constant = constantRepository.findById(id).get()
        }
        Constant duplicate = null
        try {
            duplicate = entityManager.createQuery("""
               Select c from Constant c  where c.type.id = '${ConstantTypes[type] as UUID}' and c.name = '${fields.get('name')}'
            """, Constant.class)
                    .getSingleResult()

            duplicate

            if (duplicate && constant.name != fields.get('name') as String) {
                return new GraphQLRetVal<String>("OK", false, "Duplicate already exists.")
            }
        } catch (ignored) {
        }

        constant = objectMapper.updateValue(constant, fields)
        constant.type = constantTypeRepository.findById(ConstantTypes[type] as UUID).get()
        constantRepository.save(constant)

        return new GraphQLRetVal<String>("OK", true, "Successfully ${id ? "updated" : "created new"} Leave Schedule Type")
    }

}
