package com.hisd3.hismk2.graphqlservices.payroll

import com.hisd3.hismk2.domain.payroll.OtherDeductionEmployee
import com.hisd3.hismk2.domain.payroll.PayrollEmployeeContribution
import com.hisd3.hismk2.domain.payroll.enums.PayrollEmployeeStatus
import com.hisd3.hismk2.graphqlservices.payroll.common.AbstractPayrollEmployeeStatusService
import com.hisd3.hismk2.graphqlservices.payroll.enums.PayrollModule
import com.hisd3.hismk2.graphqlservices.types.GraphQLResVal
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.payroll.PayrollEmployeeAllowanceRepository
import com.hisd3.hismk2.repository.payroll.PayrollEmployeeContributionRepository
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@GraphQLApi
@Service
class PayrollEmployeeContributionService extends AbstractPayrollEmployeeStatusService<PayrollEmployeeContribution> {

    final PayrollModule payrollModule = PayrollModule.CONTRIBUTION
    private final EmployeeRepository employeeRepository
    private final PayrollEmployeeContributionRepository payrollEmployeeContributionRepository

    @Autowired
    PayrollEmployeeContributionService(EmployeeRepository employeeRepository, PayrollEmployeeContributionRepository payrollEmployeeContributionRepository) {
        super(PayrollEmployeeContribution.class, employeeRepository)
        this.employeeRepository = employeeRepository
        this.payrollEmployeeContributionRepository = payrollEmployeeContributionRepository
    }

    @Override
    @GraphQLMutation(name = "updatePayrollEmployeeContributionStatus")
    GraphQLResVal<PayrollEmployeeContribution> updateEmployeeStatus(
            @GraphQLArgument(name = "id", description = "ID of the module employee.") UUID id,
            @GraphQLArgument(name = "status", description = "Status of the module employee you want to set.") PayrollEmployeeStatus status
    ) {
        PayrollEmployeeContribution employee = null
        employee = this.updateStatus(id, status)

        if (!employee) return new GraphQLResVal<PayrollEmployeeContribution>(null, false, "Failed to update employee contribution status. Please try again later!")
        return new GraphQLResVal<PayrollEmployeeContribution>(employee, true, "Successfully updated employee contribution status!")
    }
}
