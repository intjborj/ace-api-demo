package com.hisd3.hismk2.graphqlservices.pms

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.pms.PatientVaccination
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLResVal
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.pms.PatientVaccinationRepository
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@GraphQLApi
class PatientVaccinationService extends AbstractDaoService<PatientVaccination>{

    PatientVaccinationService(){
        super(PatientVaccination.class)
    }

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    PatientVaccinationRepository patientVaccinationRepository


    @GraphQLMutation(name = "upsertVaccinationRecord")
    GraphQLResVal<PatientVaccination> upsertVaccinationRecord(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String,Object> fields
    ){
        try{
            PatientVaccination newSave = new PatientVaccination()
            upsertFromMap(id,fields, { it,hasInserted -> newSave = it })
            return new GraphQLResVal<PatientVaccination>(newSave,true,'Success',newSave.id)
        }catch(e){
            return new GraphQLResVal<PatientVaccination>(new PatientVaccination(),false,e.message,)
        }
    }

    @GraphQLQuery(name="getVaccinationRecordsByPatient")
    List<PatientVaccination> getVaccinationRecordsByPatient(
            @GraphQLArgument(name="id" , description = "Patient Id") UUID id
    ){
        try{
            List<PatientVaccination> result = patientVaccinationRepository.getVaccinationRecordsByPatient(id)
            if(result) {
                return result
            }
            else
                return  []
        }
        catch (e){
            return  []
        }
    }
}
