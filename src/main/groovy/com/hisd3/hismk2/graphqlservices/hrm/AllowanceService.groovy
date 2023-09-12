package com.hisd3.hismk2.graphqlservices.hrm

import com.hisd3.hismk2.domain.hrm.Allowance
import com.hisd3.hismk2.domain.hrm.AllowanceTemplate
import com.hisd3.hismk2.graphqlservices.types.GraphQLResVal
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import io.leangen.graphql.annotations.GraphQLQuery

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.repository.hrm.AllowanceRepository



@Component
@GraphQLApi
class AllowanceService {

    @Autowired
    AllowanceRepository allowanceRepository

    @Autowired
    ObjectMapper objectMapper



    @GraphQLQuery(name = "findAllLikeAllowance", description = "find All Allowance")
    List<Allowance>findAllLikeAllowance(@GraphQLArgument(name ="search") String search){
        allowanceRepository.findAllLikeAllowance(search).sort {it.createdDate}
    }


    @GraphQLQuery(name = "findAllAllowance", description = "find All Allowance")
    List<Allowance>findAllAllowance(){
        return  allowanceRepository.findAllAllowance()
    }

    //==================================Mutation ============
    @GraphQLMutation(name ="postAllowance", description = "Add Allowance")
    GraphQLRetVal<String>postAllowance(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields

    ) {
        if (id) {
            Allowance allowance = allowanceRepository.findById(id).get()
            objectMapper.updateValue(allowance, fields)
            allowanceRepository.save(allowance)
            return new GraphQLRetVal<String>("ok", true, "Successfully Updated")
        } else {
            Allowance allowance = objectMapper.convertValue(fields, Allowance)
            allowanceRepository.save(allowance)
            return new GraphQLRetVal<String>("ok", true, "Successfully Saved")
        }
    }

    @GraphQLQuery(name = 'getAllowanceByPagination', description = 'list of all allowances with pagination')
    Page<Allowance> getAllowanceByPagination(
            @GraphQLArgument(name = "pageSize") Integer pageSize,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "filter") String filter
    ) {
        return allowanceRepository.getAllowances(filter, PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, 'createdDate')))
    }

    @GraphQLQuery(name = 'getOneAllowance', description = 'get one allowance')
    GraphQLResVal<Allowance> getOneAllowance(
            @GraphQLArgument(name = "id") UUID id
    ) {
        try {
            if (id) {
                Allowance allowance = allowanceRepository.findById(id).get()
                return new GraphQLResVal<Allowance>(allowance, true, 'Successfully Fetched Allowance.')
            } else
                return new GraphQLResVal<Allowance>(new Allowance(), true, 'Failed to Fetch Allowance.')
        }
        catch (e) {
            return new GraphQLResVal<Allowance>(new Allowance(), false, e.message)
        }
    }

    @GraphQLMutation(name = "upsertAllowance")
    GraphQLResVal<Allowance> upsertAllowance(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String,Object> fields
    ){
        try{
            if(id){
                Allowance allowance = allowanceRepository.findById(id).get()
                if(allowance){
                    objectMapper.updateValue(allowance,fields)
                    def newSave = allowanceRepository.save(allowance)
                    return new GraphQLResVal<Allowance>(newSave,true,'Successfully Updated Allowance.')
                }
                return new GraphQLResVal<Allowance>(new Allowance(),false,'Failed to Update Allowance.')
            }
            else {
                Allowance allowance = new Allowance()
                objectMapper.updateValue(allowance,fields)
                def newSave = allowanceRepository.save(allowance)
                return new GraphQLResVal<Allowance>(newSave,true,'Successfully Created Allowance.')
            }
        }catch(e){
            return new GraphQLResVal<Allowance>(new Allowance(),false,e.message)
        }
    }

    @GraphQLMutation(name="deleteAllowance")
    GraphQLResVal<Boolean> deleteAllowance(
            @GraphQLArgument(name="id") UUID id
    ){
        try{
            if(id){
                allowanceRepository.deleteById(id)
                return new GraphQLResVal<Boolean>(true,true,'Successfully deleted Allowance.')
            }
            return new GraphQLResVal<Boolean>(false,false,'Failed to delete Allowance.')
        }catch(e){
            return new GraphQLResVal<Boolean>(false,false,e.message)
        }

    }
}
