package com.hisd3.hismk2.graphqlservices.hrm

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hrm.BiometricDevice
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.hibernate.jpa.QueryHints
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.persistence.EntityManager

@Component
@GraphQLApi
class BiometricDeviceService extends AbstractDaoService<BiometricDevice> {

    BiometricDeviceService() {
        super(BiometricDevice.class)
    }

    @Autowired
    EntityManager entityManager

    @Autowired
    ObjectMapper objectMapper

    @GraphQLQuery(name = "get_all_biometric_device")
    List<BiometricDevice> getAllBiometricDevice() {

        return createQuery("select b from BiometricDevice b")
        .setHint(QueryHints.HINT_READONLY, true)
        .resultList

    }

    @GraphQLMutation(name = "add_biometric_device")
    GraphQLRetVal<BiometricDevice> addBiometricDevice(@GraphQLArgument(name = 'fields') Map<String, Object> fields){

        BiometricDevice afterSave = new BiometricDevice()

        try {
            if(fields.id){
                afterSave = findOne(UUID.fromString(fields.id as String))
                objectMapper.updateValue(afterSave, fields)
                save(afterSave)
            }else {
                objectMapper.updateValue(afterSave, fields)
                save(afterSave)
            }


            return new GraphQLRetVal<BiometricDevice>(afterSave, true, "Successfully saved!")
        } catch (Exception e) {
            return new GraphQLRetVal<BiometricDevice>(afterSave, false, e.message)

        }
    }

    @GraphQLMutation(name = "delete_biometric_device")
    GraphQLRetVal<BiometricDevice> deleteBiometricDevice(@GraphQLArgument(name = "id") UUID id){

        BiometricDevice afterDelete = new BiometricDevice()

        try {
            afterDelete = findOne(id)
            delete(afterDelete)

            return new GraphQLRetVal<BiometricDevice>(afterDelete, true, """${afterDelete.deviceName} Successfully Delete!""")
        } catch(Exception e){
            return new GraphQLRetVal<BiometricDevice>(afterDelete, false, e.message)
        }
    }

}
