package com.hisd3.hismk2.graphqlservices.doh

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.doh.DohConfiguration
import com.hisd3.hismk2.domain.doh.DohLogs
import com.hisd3.hismk2.domain.doh.TotalDeathsConfig
import com.hisd3.hismk2.domain.hospital_config.HospitalInfo
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.doh.DohConfigurationRepository
import com.hisd3.hismk2.repository.doh.DohLogsRepository
import com.hisd3.hismk2.services.EntityObjectMapperService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
@GraphQLApi
class DohConfigurationService  {


    @Autowired
    DohConfigurationRepository dohConfigurationsRepository

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    EntityObjectMapperService entityObjectMapperService

    //================================== Query ====================================


    @GraphQLQuery(name = "getDohConfig", description = "Find all doh logs")
    DohConfiguration getDohConfig() {
        return dohConfigurationsRepository.findAll().find()
    }


    //================================== Mutation =================================

    @GraphQLMutation(name = "upsertDohConfig")
    GraphQLRetVal<DohConfiguration> upsertDohConfig(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "totalDeathsConfig") Map<String, Object> totalDeathsConfig) {
        try{
            DohConfiguration config = new DohConfiguration()
            if(id){
                config = dohConfigurationsRepository.findById(id).get()
            }
            config.totalDeathsConfig = objectMapper.convertValue(totalDeathsConfig, TotalDeathsConfig)
            config = dohConfigurationsRepository.save(config)
            return new GraphQLRetVal<DohConfiguration>(config,true,'Success')

        }catch(e) {
            return  new GraphQLRetVal<DohConfiguration>(null,false,e.message)
        }



    }


}
