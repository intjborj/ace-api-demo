package com.hisd3.hismk2.graphqlservices.hrm

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hrm.AddOn
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.hrm.EmployeeAttendance
import com.hisd3.hismk2.domain.hrm.EmployeeLoan
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.hrm.EmployeeLoanRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import graphql.schema.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import io.leangen.graphql.annotations.GraphQLArgument

@Component
@GraphQLApi
class EmployeeLoanService {

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    EmployeeRepository employeeRepository

    @Autowired
    EmployeeLoanRepository employeeLoanRepository


        @GraphQLQuery(name = "getEmployeeLoanById", description = "Search employees")
    List<EmployeeLoan> getEmployeeLoanById(@GraphQLArgument(name = "id") UUID id) {
        employeeLoanRepository.findByEmployeeID(id)
    }

    @GraphQLQuery(name = "getEmployeeLoanByIdWithFilter", description = "Search employees")
    List<EmployeeLoan> getEmployeeLoanByIdWithFilter(@GraphQLArgument(name = "search") String search) {
        employeeLoanRepository.getEmployeeLoanByIdWithFilter(search).sort { it.createdDate }
    }


//    @GraphQLQuery(name = "getEmployeeLoanById", description = "Get Employee Loan ")
//    List<EmployeeLoan> getEmployeeLoanById(@GraphQLArgument(name = "id")UUID id){
//        if(!id) throw new RuntimeException("ID is required")
//        return employeeLoanRepository.getEmployeeLoanById(id)
//    }

    //==================================Mutation ============
    @GraphQLMutation(name = 'postEmployeeLoan', description = 'Add Allowance')
    GraphQLRetVal<String>postEmployeeLoan(
           @GraphQLArgument(name='id') UUID id,
           @GraphQLArgument(name ="fields") Map<String, Object> fields,
            @GraphQLArgument(name ="employee") UUID employee
    ){
        Employee employee1 = employeeRepository.findById(employee).get()
        if(id){

        EmployeeLoan employeeLoan = employeeRepository.findById().get()
            objectMapper.updateValue(fields, employeeLoan)
            employeeLoan.employee = employee1
            employeeLoanRepository.save(employeeLoan)
        return new GraphQLRetVal<String>("OK", true, "Successfully Updated")
        }else{

        EmployeeLoan employeeLoan = objectMapper.convertValue(fields, EmployeeLoan)
            employeeLoan.employee = employeeLoan
            employeeLoanRepository.save(employeeLoan)
        return new GraphQLRetVal<String>("OK", true, "Successfully Saved")
        }
    }

}
