package com.hisd3.hismk2.graphqlservices.hospital_config

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hospital_config.ClinicDoctor
import com.hisd3.hismk2.domain.hospital_config.ClinicRoom
import com.hisd3.hismk2.domain.hospital_config.Physician
import com.hisd3.hismk2.domain.pms.VentilatorRecordItem
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.hospital_config.ClinicDoctorRepository
import com.hisd3.hismk2.repository.hospital_config.ClinicRoomRepository
import com.hisd3.hismk2.repository.hospital_config.PhysicianRepository
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@GraphQLApi
class ClinicDoctorService extends AbstractDaoService<ClinicDoctor> {

    ClinicDoctorService() {
        super(ClinicDoctor.class)
    }

    @Autowired
    ClinicRoomRepository clinicRoomRepository

	@Autowired
    ClinicRoomService clinicRoomService

    @Autowired
    PhysicianService physicianService

    @Autowired
    ClinicDoctorRepository clinicDoctorRepository

    @Autowired
    ObjectMapper objectMapper

//	@GraphQLQuery(name = "get_rooms", description = "Get all rooms")
//	List<ClinicRoom> getRooms() {
//		return clinicRoomRepository.findAll()
//	}

    @GraphQLQuery(name = "getAllClinicDoctor", description = 'Get all Clinic Doctor')
    List<ClinicDoctor>getAllClinicDoctor(){
        return clinicDoctorRepository.findAll()
    }

    @GraphQLMutation
    GraphQLRetVal<Boolean> upsertRoomPhysician(
            @GraphQLArgument(name = "roomFields") Map<String, Object> roomFields,
            @GraphQLArgument(name = "physicianFields") List<Map<String, Object>> physicianFields
    ) {
        try {

            def clinicRoom = clinicRoomService.upsertClinicRoom(null, roomFields)

            if (clinicRoom && physicianFields) {
                physicianFields.each {

                    addPhysicianToClinic(it, clinicRoom.id)

                }
                return new GraphQLRetVal<Boolean>(true, true, 'Success')
            }
            return new GraphQLRetVal<Boolean>(true, true, 'Success')
        }catch(e){
            return new GraphQLRetVal<Boolean>(false, false, e.message)
        }
    }

    @GraphQLMutation
    GraphQLRetVal<Boolean> addPhysicianToClinic(
            @GraphQLArgument(name = "physicianField") Map<String, Object> physicianField,
            @GraphQLArgument(name = "roomId") UUID roomId
    ){
        try {
            def physician = physicianService.upsertPhysician(null, physicianField)
            def room = clinicRoomRepository.findById(roomId).get()
            if (physician) {
                ClinicDoctor clinicDoctor = new ClinicDoctor()
                clinicDoctor.room = room
                clinicDoctor.physician = physician
                save(clinicDoctor)
            }
            return new GraphQLRetVal<Boolean>(true, true, 'Success')
        }catch(e){
            return new GraphQLRetVal<Boolean>(false, false, e.message)
        }
    }


    @GraphQLMutation
    Physician deletePhysician(
            @GraphQLArgument(name = "id") UUID id
    )
    {
        if (id) {
            return deleteById(id)
        }
    }

}
