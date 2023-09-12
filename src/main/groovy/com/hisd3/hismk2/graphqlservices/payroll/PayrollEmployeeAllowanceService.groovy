package com.hisd3.hismk2.graphqlservices.payroll

import com.hisd3.hismk2.domain.payroll.PayrollEmployeeAllowance
import com.hisd3.hismk2.domain.payroll.enums.PayrollEmployeeStatus
import com.hisd3.hismk2.graphqlservices.payroll.common.AbstractPayrollEmployeeStatusService
import com.hisd3.hismk2.graphqlservices.payroll.enums.PayrollModule
import com.hisd3.hismk2.graphqlservices.types.GraphQLResVal
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.payroll.PayrollEmployeeAllowanceRepository
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@GraphQLApi
@Service
class PayrollEmployeeAllowanceService extends AbstractPayrollEmployeeStatusService<PayrollEmployeeAllowance> {

    final PayrollModule payrollModule = PayrollModule.ALLOWANCE
    private final EmployeeRepository employeeRepository
    private final PayrollEmployeeAllowanceRepository payrollEmployeeAllowanceRepository

    @Autowired
    PayrollEmployeeAllowanceService(EmployeeRepository employeeRepository, PayrollEmployeeAllowanceRepository payrollEmployeeAllowanceRepository) {
        super(PayrollEmployeeAllowance.class, employeeRepository)
        this.employeeRepository = employeeRepository
        this.payrollEmployeeAllowanceRepository = payrollEmployeeAllowanceRepository
    }

    @Override
    @GraphQLMutation(name = "updatePayrollEmployeeAllowanceStatus")
    GraphQLResVal<PayrollEmployeeAllowance> updateEmployeeStatus(
            @GraphQLArgument(name = "id", description = "ID of the module employee.") UUID id,
            @GraphQLArgument(name = "status", description = "Status of the module employee you want to set.") PayrollEmployeeStatus status
    ) {
        PayrollEmployeeAllowance employee = null
        employee = this.updateStatus(id, status)

        if (!employee) return new GraphQLResVal<PayrollEmployeeAllowance>(null, false, "Failed to update employee allowance status. Please try again later!")
        return new GraphQLResVal<PayrollEmployeeAllowance>(employee, true, "Successfully updated employee allowance status!")
    }
}
