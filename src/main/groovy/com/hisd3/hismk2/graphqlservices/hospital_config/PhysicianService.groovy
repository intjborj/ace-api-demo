package com.hisd3.hismk2.graphqlservices.hospital_config

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hospital_config.ClinicRoom
import com.hisd3.hismk2.domain.hospital_config.Physician
import com.hisd3.hismk2.domain.pms.VentilatorRecordItem
import com.hisd3.hismk2.repository.hospital_config.ClinicRoomRepository
import com.hisd3.hismk2.repository.hospital_config.PhysicianRepository
import com.hisd3.hismk2.services.EntityObjectMapperService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@GraphQLApi
class PhysicianService {

	@Autowired
    PhysicianRepository physicianRepository

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    EntityObjectMapperService entityObjectMapperService

	@GraphQLQuery(name = "get_physician", description = "Get all physician")
	List<Physician> getPhysician() {
		return physicianRepository.findAll()
	}

    @GraphQLMutation
    Physician upsertPhysician(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    )
    {
        if (id) {
            Physician physicianObj = physicianRepository.findById(id).get()
            entityObjectMapperService.updateFromMap(physicianObj,fields)
            return physicianRepository.save(physicianObj)
        } else {
             Physician physicianObj = new Physician()
             entityObjectMapperService.updateFromMap(physicianObj,fields)
            def result =  physicianRepository.save(physicianObj)
            return result
        }
    }



}
