package com.hisd3.hismk2.graphqlservices.bundy

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hrm.BiometricServiceConfig
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
@GraphQLApi
class BiometricDeviceConfigService extends AbstractDaoService<BiometricServiceConfig> {

    BiometricDeviceConfigService(){
        super(BiometricServiceConfig.class)
    }

    @Autowired
    ObjectMapper objectMapper

    @GraphQLMutation(name = "upsert_biometric_service_config")
    GraphQLRetVal<BiometricServiceConfig> upsertBiometricServiceConfig(
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    ){
        def biometricServiceConfig = new BiometricServiceConfig()

        try {
            if(fields.id){

                biometricServiceConfig = findOne(UUID.fromString(fields.id as String))
                objectMapper.updateValue(biometricServiceConfig, fields)
                save(biometricServiceConfig)

            }else {

                objectMapper.updateValue(biometricServiceConfig, fields)
                save(biometricServiceConfig)

            }

            return new GraphQLRetVal<BiometricServiceConfig>(biometricServiceConfig, true)

        } catch (Exception e) {

            return  new GraphQLRetVal<BiometricServiceConfig>(biometricServiceConfig, false, e.message)

        }
    }

}
