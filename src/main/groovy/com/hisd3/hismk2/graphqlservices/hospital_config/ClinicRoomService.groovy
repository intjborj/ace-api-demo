package com.hisd3.hismk2.graphqlservices.hospital_config

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hospital_config.ClinicRoom
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.domain.pms.CentralLineRecord
import com.hisd3.hismk2.repository.hospital_config.ClinicRoomRepository
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@GraphQLApi
class ClinicRoomService {

	@Autowired
	ClinicRoomRepository clinicRoomRepository

    @Autowired
    ObjectMapper objectMapper

	@GraphQLQuery(name = "get_rooms", description = "Get all rooms")
	List<ClinicRoom> getRooms() {
		return clinicRoomRepository.findAll().sort{a, b -> b.createdDate <=> a.createdDate}
	}

    @GraphQLMutation
    ClinicRoom upsertClinicRoom(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    )
    {

        if (id) {
            ClinicRoom clinicRoomObj = clinicRoomRepository.findById(id).get()
            objectMapper.updateValue(clinicRoomObj, fields)
            return clinicRoomRepository.save(clinicRoomObj)
        } else {
            ClinicRoom clinicRoomObj = objectMapper.convertValue(fields, ClinicRoom)
            def result =  clinicRoomRepository.save(clinicRoomObj)
            return result
        }
    }



}
