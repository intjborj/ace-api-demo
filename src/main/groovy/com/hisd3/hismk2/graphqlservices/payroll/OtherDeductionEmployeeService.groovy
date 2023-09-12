package com.hisd3.hismk2.graphqlservices.payroll

import com.hisd3.hismk2.domain.payroll.OtherDeductionEmployee
import com.hisd3.hismk2.domain.payroll.enums.PayrollEmployeeStatus
import com.hisd3.hismk2.graphqlservices.payroll.common.AbstractPayrollEmployeeStatusService
import com.hisd3.hismk2.graphqlservices.payroll.enums.PayrollModule
import com.hisd3.hismk2.graphqlservices.types.GraphQLResVal
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.payroll.OtherDeductionEmployeeRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class OtherDeductionEmployeeService extends AbstractPayrollEmployeeStatusService<OtherDeductionEmployee> {

    final PayrollModule payrollModule = PayrollModule.OTHER_DEDUCTION
    private final EmployeeRepository employeeRepository
    private final OtherDeductionEmployeeRepository otherDeductionEmployeeRepository

    @Autowired
    OtherDeductionEmployeeService(EmployeeRepository employeeRepository, OtherDeductionEmployeeRepository otherDeductionEmployeeRepository) {
        super(OtherDeductionEmployee.class ,employeeRepository)
        this.employeeRepository = employeeRepository
        this.otherDeductionEmployeeRepository = otherDeductionEmployeeRepository
    }

    @Override
    @GraphQLMutation(name = "updateOtherDeductionEmployeeStatus")
    GraphQLResVal<OtherDeductionEmployee> updateEmployeeStatus(
            @GraphQLArgument(name = "id", description = "ID of the module employee.")UUID id,
            @GraphQLArgument(name = "status", description = "Status of the module employee you want to set.") PayrollEmployeeStatus status
    ){
        OtherDeductionEmployee employee = null
        otherDeductionEmployeeRepository.findById(id).ifPresent {employee = it}
        if(!employee) return new GraphQLResVal<OtherDeductionEmployee>(null, false, "Failed to update employee other deduction status. Please try again later!")
        else{
            employee = this.updateStatus(id, status)
            return new GraphQLResVal<OtherDeductionEmployee>(employee, true, "Successfully updated employee other deduction status!")
        }
    }
}
