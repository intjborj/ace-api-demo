package com.hisd3.hismk2.graphqlservices.payroll

import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.payroll.OtherDeductionEmployee
import com.hisd3.hismk2.domain.payroll.PayrollEmployee
import com.hisd3.hismk2.domain.payroll.PayrollEmployeeAllowance
import com.hisd3.hismk2.domain.payroll.PayrollOtherDeduction
import com.hisd3.hismk2.domain.payroll.Timekeeping
import com.hisd3.hismk2.domain.payroll.enums.PayrollEmployeeStatus
import com.hisd3.hismk2.domain.payroll.enums.TimekeepingEmployeeStatus
import com.hisd3.hismk2.domain.payroll.TimekeepingEmployee
import com.hisd3.hismk2.graphqlservices.payroll.common.AbstractPayrollEmployeeStatusService
import com.hisd3.hismk2.graphqlservices.payroll.enums.PayrollModule
import com.hisd3.hismk2.graphqlservices.types.GraphQLResVal
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.payroll.TimekeepingEmployeeRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@TypeChecked
@Component
@GraphQLApi
class TimekeepingEmployeeService extends AbstractPayrollEmployeeStatusService<TimekeepingEmployee> {

    final PayrollModule payrollModule = PayrollModule.TIMEKEEPING

    @Autowired
    TimekeepingEmployeeRepository timekeepingEmployeeRepository

    @PersistenceContext
    EntityManager entityManager

    private final EmployeeRepository employeeRepository

    @Autowired
    TimekeepingEmployeeService(EmployeeRepository employeeRepository) {
        super(TimekeepingEmployee.class, employeeRepository)
        this.employeeRepository = employeeRepository
    }
//=================================QUERY=================================\\

    @GraphQLQuery(name = "getTimekeepingEmployee", description = "Gets all the ids of the employees of the timekeeping")
    List<Employee> getTimekeepingEmployee(@GraphQLArgument(name="id") UUID id) {
        return timekeepingEmployeeRepository.findByTimekeepingEmployee(id)
    }
//
    @GraphQLQuery(name = "getTimekeepingEmployeesV2", description = "Gets all the ids of the employees of the timekeeping")
    List<TimekeepingEmployee> getTimekeepingEmployeesV2(@GraphQLArgument(name="id") UUID id) {
        return timekeepingEmployeeRepository.findByTimekeepingId(id)
    }
//
//    List<TimekeepingEmployee> getByIds(@GraphQLArgument(name="getByIds") UUID id) {
//        return timekeepingEmployeeRepository.findByTimekeepingId(id)
//    }

    //=================================MUTATIONS=================================\\
    @Override
    @GraphQLMutation(name="updateTimekeepingEmployeeStatus")
    GraphQLResVal<TimekeepingEmployee> updateEmployeeStatus(
            @GraphQLArgument(name = "id", description = "ID of the module employee.")UUID id,
            @GraphQLArgument(name = "status", description = "Status of the module employee you want to set.") PayrollEmployeeStatus status
    ){
        TimekeepingEmployee employee = null
        timekeepingEmployeeRepository.findById(id).ifPresent {employee = it}
        if(!employee) return new GraphQLResVal<TimekeepingEmployee>(null, false, "Failed to update employee timekeeping status. Please try again later!")
        else{
            employee = this.updateStatus(id, status)
            return new GraphQLResVal<TimekeepingEmployee>(employee, true, "Successfully updated employee timekeeping status!")
        }
    }

}
