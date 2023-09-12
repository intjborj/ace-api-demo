package com.hisd3.hismk2.graphqlservices.hrm

import com.hisd3.hismk2.domain.hrm.AddOn
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.hrm.AddOnRepository
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.transaction.annotation.Transactional

@Component
@GraphQLApi
class AddOnService {

    @Autowired
    AddOnRepository addOnRepository

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    EmployeeRepository employeeRepository


    @GraphQLQuery(name = "getAllAddOn", description = "find All Add on")
    List<AddOn> getAllAddOn() {
     return addOnRepository.getAllAddOn()
    }

    //==================================Mutation ============


    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation(name = "postAddOn", description = "Add on")
    GraphQLRetVal<String> postAddOn(
//            @GraphQLArgument(name = "fields") Map<String, Object> fields,
            @GraphQLArgument(name = "add_on") List<Map<String, Object>> fields,
            @GraphQLArgument(name = "employee_id") UUID employee_id  //id of employee
    ) {

        if (!employee_id) return new GraphQLRetVal<String>("ERROR", false, "Failed to add allowance to employee.")
        Employee employee = employeeRepository.findById(employee_id).get()

        fields.forEach({
            AddOn addOn = objectMapper.convertValue(it, AddOn)
            addOn.employee = employee
            addOnRepository.save(addOn)
        })

        return new GraphQLRetVal<String>("OK", true, "Successfully Updated")
    }

//    @GraphQLMutation
//    def deleteAddOn(
//            @GraphQLArgument(name = "id") UUID id
//    ){
//        if (id) {
//            employeeRepository.deleteById(id)
//        }
//    }
    @GraphQLMutation
    GraphQLRetVal<String>deleteAddOn(
            @GraphQLArgument(name = "id") UUID id
    ){
        if(!id) return new GraphQLRetVal<String>("Error", false, "Failed to Delete" )

        AddOn addOn = addOnRepository.findById(id).get()
        addOnRepository.delete(addOn)
        return  new GraphQLRetVal<String>("Ok", true, "Successfully deleted")
    }
}
