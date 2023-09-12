package com.hisd3.hismk2.graphqlservices.hrm

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.hrm.Allowance
import com.hisd3.hismk2.domain.hrm.AllowanceTemplate
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.hrm.EmployeeAllowance
import com.hisd3.hismk2.graphqlservices.types.GraphQLResVal
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.hrm.AllowanceRepository
import com.hisd3.hismk2.repository.hrm.AllowanceTemplatesRepository
import com.hisd3.hismk2.repository.hrm.EmployeeAllowanceRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional


@Component
@GraphQLApi
class EmployeeAllowanceService {

    @Autowired
    EmployeeAllowanceRepository employeeAllowanceRepository

    @Autowired
    EmployeeRepository employeeRepository

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    AllowanceRepository allowanceRepository

    @Autowired
    AllowanceTemplatesRepository allowanceTemplatesRepository


    @GraphQLQuery(name ="findAllEmployeeAllowance", description = "get all employee Allowance")
    List<EmployeeAllowance>findAllEmployeeAllowance(){
        return employeeAllowanceRepository.findAllEmployeeAllowance()
    }





    //==================================Mutation ==========================================\\
    @GraphQLMutation
    GraphQLRetVal<String>postEmployeeAllowance(
            @GraphQLArgument(name ="id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields,
            @GraphQLArgument(name = "employee_id") UUID employee_id,
            @GraphQLArgument(name = "allowance_id") UUID allowance_id
    ){
        Allowance allowance = allowanceRepository.findById(allowance_id).get()
        Employee employee = employeeRepository.findById(employee_id).get()
        if (id){

            EmployeeAllowance employeeAllowance = employeeAllowanceRepository.findById(id).get()
            objectMapper.updateValue(fields, employeeAllowance)
            employeeAllowance.employee = employee
            employeeAllowanceRepository.save(employeeAllowance)

            return new GraphQLRetVal<String>( "ok", true, "Successfully Update")
        }else{

            EmployeeAllowance employeeAllowance = objectMapper.convertValue(fields, EmployeeAllowance)
            employeeAllowance.employee= employee
            employeeAllowanceRepository.save(employeeAllowance)
            return new GraphQLRetVal<String>("ok", true, "SuccessFully  Saved")
        }
    }

    @GraphQLMutation(name="upsertEmployeeAllowance")
    @Transactional(rollbackFor = Exception.class)
    GraphQLResVal<EmployeeAllowance> upsertEmployeeAllowance(
            @GraphQLArgument(name="template_id") UUID template_id,
            @GraphQLArgument(name="employee_id") ArrayList<UUID> employee_id
    ){
        AllowanceTemplate template = allowanceTemplatesRepository.findById(template_id).get()
        if (template)
        {
            if (employee_id) {
                ArrayList<EmployeeAllowance> empAllowanceList = new ArrayList<EmployeeAllowance>()
                List<Employee> employeeList = employeeRepository.getEmployees(employee_id)
                employeeList.each {
                    Employee employee = it
                    employee.employeeAllowance.clear()
                    template.templates.each {
                        if(it.active) {
                            EmployeeAllowance employeeAllowance = new EmployeeAllowance()
                            employeeAllowance.employee = employee
                            employeeAllowance.name = it.allowance.name
                            employeeAllowance.amount = it.allowance.amount
                            employeeAllowance.taxable = it.allowance.taxable
                            employeeAllowance.notes = it.allowance.notes
                            empAllowanceList.add(employeeAllowance)
                        }
                    }
                }
                employeeAllowanceRepository.saveAll(empAllowanceList)
                return new GraphQLResVal<EmployeeAllowance>(new EmployeeAllowance(), true, "Successfully assigned allowance template to ${employee_id.size()} employee(s).")
            }
                else{
                return new GraphQLResVal<EmployeeAllowance>(new EmployeeAllowance(), false, 'Failed to assign allowance Template, Employees not found.')
            }
        }
        else{
            return new GraphQLResVal<EmployeeAllowance>(new EmployeeAllowance(), false, "Failed to assign allowance template to ${employee_id.size()} employee(s).")
        }
    }

    @GraphQLMutation(name="deleteEmployeeAllowance")
    GraphQLResVal<Boolean> deleteEmployeeAllowance(
            @GraphQLArgument(name="id") UUID id
    ){
        try{
            if(id){
                employeeAllowanceRepository.deleteById(id)
                return new GraphQLResVal<Boolean>(true,true,'Success')
            }
            return new GraphQLResVal<Boolean>(false,false,'No parameter')
        }catch(e){
            return new GraphQLResVal<Boolean>(false,false,e.message)
        }
    }
}
