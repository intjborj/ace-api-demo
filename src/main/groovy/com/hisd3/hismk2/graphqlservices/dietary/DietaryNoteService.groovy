package com.hisd3.hismk2.graphqlservices.dietary

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.dietary.DietNotes
import com.hisd3.hismk2.domain.fixed_assets.FixedAssetTransfer
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.pms.Case
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.dietary.DietNoteRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.pms.CaseRepository
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component


@Component
@GraphQLApi
class DietaryNoteService extends AbstractDaoService<DietNotes>  {

    DietaryNoteService() {
        super(DietNotes.class)
    }

    @Autowired
    DietNoteRepository dietNoteRepository

    @Autowired
    CaseRepository caseRepository

    @Autowired
    EmployeeRepository employeeRepository

    @Autowired
    ObjectMapper objectMapper

    //===================================== Query && Mutation ====================================\\

    @GraphQLQuery(name = "getDietNotesByCasePageable")
    Page<DietNotes> getDietNotesByCasePageable(
            @GraphQLArgument(name = "id") UUID caseId,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "pageSize") Integer pageSize
    ) {
        return dietNoteRepository.getDietNotesByCasePageable(caseId, PageRequest.of(page, pageSize, Sort.Direction.DESC, 'dateTime'))
    }

    @GraphQLMutation(name = "upsertDietNotes", description = "Insert Diet Notes")
    GraphQLRetVal<DietNotes> upsertDietNotes(
            @GraphQLArgument(name = "id") UUID  id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields,
            @GraphQLArgument(name = "caseId") UUID caseId,
            @GraphQLArgument(name = "employeeId") UUID employeeId
    ){
        def patientCaseId = caseRepository.findById(caseId).get()
        def empId = employeeRepository.findById(employeeId).get()

            if(id){
                DietNotes  dietNotes = findOne(id)
                dietNotes = objectMapper.updateValue(dietNotes, fields)
                dietNotes.caseId = patientCaseId
                dietNotes.employee = empId
                dietNotes = save(dietNotes)
                return  new GraphQLRetVal<DietNotes>(dietNotes, true, "Successfully Updated")
            }
            DietNotes dietNotes = objectMapper.convertValue(fields, DietNotes)
            dietNotes.caseId = patientCaseId
            dietNotes.employee = empId
            dietNotes = save(dietNotes)
            return new GraphQLRetVal<DietNotes>(dietNotes, true, 'Successfully Save')
    }


    @GraphQLMutation(name = "deleteDietNote", description = "Delete DietNote")
    GraphQLRetVal<Boolean> deleteDietNote(
            @GraphQLArgument(name ="id") UUID id
    ) {
            if (id) {
                DietNotes obj = findOne(id)
                def deleteDietNote = delete(obj)

                return new GraphQLRetVal<Boolean>(deleteDietNote, true, 'Successfully Deleted')
            }
            return new GraphQLRetVal<Boolean>(false, false, "No ID Found")

    }



}
