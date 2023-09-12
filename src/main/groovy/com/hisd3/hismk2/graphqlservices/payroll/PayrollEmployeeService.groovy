package com.hisd3.hismk2.graphqlservices.payroll

import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.payroll.common.PayrollEmployeeAuditingEntity
import com.hisd3.hismk2.domain.payroll.enums.PayrollEmployeeStatus
import com.hisd3.hismk2.graphqlservices.payroll.common.AbstractPayrollEmployeeStatusService
import com.hisd3.hismk2.graphqlservices.payroll.enums.PayrollModule
import com.hisd3.hismk2.graphqlservices.types.GraphQLResVal
import com.hisd3.hismk2.repository.payroll.PayrollEmployeeRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@TypeChecked
@Component
@GraphQLApi
class PayrollEmployeeService {

    @Autowired
    PayrollEmployeeRepository payrollEmployeeRepository

    @Autowired
    OtherDeductionEmployeeService otherDeductionEmployeeService

    @Autowired
    PayrollEmployeeAllowanceService payrollEmployeeAllowanceService

    @Autowired
    PayrollEmployeeContributionService payrollEmployeeContributionService

    @Autowired
    TimekeepingEmployeeService timekeepingEmployeeService

    @Autowired
    Map<PayrollModule, AbstractPayrollEmployeeStatusService> payrollEmployeeStatusServiceMap

    @PersistenceContext
    EntityManager entityManager

    //=================================QUERY=================================\\

    @GraphQLQuery(name = "getPayrollEmployeeIds", description = "Gets all the ids of the employees of the Payroll")
    List<UUID> getPayrollEmployeeIds(@GraphQLArgument(name = "PayrollId") UUID payrollId) {
        List<UUID> ids = entityManager.createQuery("""
                Select e.employee.id from PayrollEmployee e where e.Payroll.id = :payrollId
            """, UUID.class).setParameter("payrollId", payrollId)
                .getResultList()
        return ids
    }


    @GraphQLQuery(name = "getPayrollEmployee", description = "Gets all the employees by payroll id")
    List<Employee> getPayrollEmployee(@GraphQLArgument(name = "id") UUID id) {
        return payrollEmployeeRepository.findByPayrollEmployees(id)
    }
//
//    @GraphQLQuery(name = "getPayrollEmployeesV2", description = "Gets all the ids of the employees of the Payroll")
//    List<PayrollEmployee> getPayrollEmployeesV2(@GraphQLArgument(name="id") UUID id) {
//        return PayrollEmployeeRepository.findByPayrollId(id)
//    }

    //=================================MUTATIONS=================================\\

    @GraphQLMutation(description = "A mutation for updating the status of module employee status.")
    GraphQLResVal<String> updatePayrollModuleEmployeeStatus(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "status") PayrollEmployeeStatus status,
            @GraphQLArgument(name = "module") PayrollModule module
    ) {
        AbstractPayrollEmployeeStatusService service = payrollEmployeeStatusServiceMap.get(module)

        if (service) {
            def response = service.updateEmployeeStatus(id, status) as GraphQLResVal<PayrollEmployeeAuditingEntity>
            return new GraphQLResVal<String>(response.response.payrollEmployee.employee.fullName, response.success, response.message)
        }

        return new GraphQLResVal<String>(null, false, "Failed to update employee status. Please try again later.")
    }
}
