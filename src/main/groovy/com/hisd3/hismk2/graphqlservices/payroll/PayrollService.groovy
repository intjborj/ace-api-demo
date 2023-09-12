package com.hisd3.hismk2.graphqlservices.payroll

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.payroll.Payroll
import com.hisd3.hismk2.domain.payroll.PayrollEmployee
import com.hisd3.hismk2.domain.payroll.enums.PayrollEmployeeStatus
import com.hisd3.hismk2.domain.payroll.enums.PayrollStatus
import com.hisd3.hismk2.graphqlservices.payroll.common.AbstractPayrollStatusService
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.payroll.PayrollEmployeeRepository
import com.hisd3.hismk2.repository.payroll.PayrollRepository
import groovy.transform.TypeChecked
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

@TypeChecked
@Component
@GraphQLApi
class PayrollService extends AbstractPayrollStatusService<Payroll> {

    private final EmployeeRepository employeeRepository

    @Autowired
    PayrollRepository payrollRepository

    @Autowired
    PayrollEmployeeRepository payrollEmployeeRepository

    @Autowired
    List<IPayrollOperation> payrollOperations

    @Autowired
    TimekeepingService timekeepingService

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    PayrollService(EmployeeRepository employeeRepository) {
        super(Payroll.class, employeeRepository)
        this.employeeRepository = employeeRepository
    }

    //=================================QUERY=================================\\
    @GraphQLQuery(name = "payrolls", description = "Get All payroll")
    List<Payroll> findAll() {
        payrollRepository.findAll().sort { it.createdDate }
    }

    @GraphQLQuery(name = "getPayrollById", description = "Get payroll by ID")
    Payroll findById(@GraphQLArgument(name = "id") UUID id) {
        if (id) {
            return payrollRepository.findById(id).get()
        } else {
            return null
        }

    }

    @GraphQLQuery(name = 'getPayrollByPagination', description = 'list of all allowances with pagination')
    Page<Payroll> getPayrollByPagination(
            @GraphQLArgument(name = "pageSize") Integer pageSize,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "filter") String filter
    ) {
        return payrollRepository.getPayrollByFilterPageable(filter, PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, 'createdDate')))
    }


    //=================================QUERY=================================\\


    //================================MUTATION================================\\

    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation
    GraphQLRetVal<Payroll> upsertPayroll(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields,
            @GraphQLArgument(name = "employeeList") List<UUID> employeeList
    ) {
        if (id) {
            Payroll payroll = payrollRepository.findById(id).get()
            if (fields) {
                payroll = objectMapper.updateValue(payroll, fields)

            }

            List<PayrollEmployee> employeesToRemove = []


            payroll.payrollEmployees.each { PayrollEmployee pe ->
                int index = employeeList.indexOf(pe.employee.id)
                if (index < 0) {
                    employeesToRemove.add(pe)
                } else {
                    employeeList.remove(index)
                }
            }
            List<PayrollEmployee> employeesToAdd = []

            if (employeeList.size() > 0) {
                List<Employee> employees = employeeRepository.getEmployees(employeeList)
                employeesToAdd = createPayrollEmployees(employees, payroll)
                payrollEmployeeRepository.saveAll(employeesToAdd)
//                payroll.payrollEmployees.addAll(employeesToAdd)
            }

            payroll.payrollEmployees.removeAll(employeesToRemove)
            payrollRepository.save(payroll)

            if (payroll.status == PayrollStatus.ACTIVE && employeesToAdd.size() > 0) {
                timekeepingService.addEmployees(employeesToAdd, payroll) //TODO: Temporary only, use the code below in the future
//                payrollOperations.each {
//                    it.addEmployees(employeesToAdd, payroll)
//                }
            }

            return new GraphQLRetVal<Payroll>(payroll, true, "Successfully updated Payroll")
        } else {
            Payroll payroll = objectMapper.convertValue(fields, Payroll)
            payroll.status = PayrollStatus.DRAFT

            List<Employee> employees = employeeRepository.getEmployees(employeeList)
            payroll.payrollEmployees.addAll(createPayrollEmployees(employees, payroll))

            payroll = payrollRepository.save(payroll)

            return new GraphQLRetVal<Payroll>(payroll, true, "Successfully created new Payroll")
        }
    }

    static List<PayrollEmployee> createPayrollEmployees(List<Employee> employees, Payroll payroll) {
        List<PayrollEmployee> payrollEmployees = new ArrayList<PayrollEmployee>()
        employees.each {
            PayrollEmployee payrollEmployee = new PayrollEmployee()
            payrollEmployee.status = PayrollEmployeeStatus.DRAFT
            payrollEmployee.employee = it
            payrollEmployee.payroll = payroll
            payrollEmployees.add(payrollEmployee)
        }
        return payrollEmployees
    }


    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation
    GraphQLRetVal<Payroll> updatePayrollStatus(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "status") String status

    ) {
        Payroll payroll = updateStatus(id, PayrollStatus.valueOf(status))

        if (status == 'ACTIVE') {
            //TODO: actions for creating timekeeping, timekeeping employee, accumulated logs summary and accumulated logs.


            //TODO: actions for creating allowance, payroll employee allowance, payroll employee allowance item.
            //TODO: actions for creating contributions and payroll employee contributions

            timekeepingService.startPayroll(payroll) //TODO: Temporary only, use the code below in the future
//            payrollOperations.each {
//                it.startPayroll(payroll)
//            }

        }
//        else if (status == 'CANCELLED')
//            payroll.status = PayrollApprovalStatus.CANCELLED
//        else if (status == 'FINALIZED') {
//            payroll.status = PayrollApprovalStatus.FINALIZED
//        }
//
        payrollRepository.save(payroll)

        return new GraphQLRetVal<Payroll>(payroll, true, "Successfully updated payroll")
    }

    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation
    GraphQLRetVal<String> updatePayrollDetails(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "fields") Map<String, Object> fields
    ) {

        Payroll payroll = payrollRepository.findById(id).get()

        payroll = objectMapper.updateValue(payroll, fields)
        payrollRepository.save(payroll)

        return new GraphQLRetVal<String>("OK", true, "Successfully updated payroll details")

    }

    @Transactional(rollbackFor = Exception.class)
    @GraphQLMutation
    GraphQLRetVal<String> deletePayroll(
            @GraphQLArgument(name = "id") UUID id
    ) {
        if (!id) return new GraphQLRetVal<String>("ERROR", false, "Failed to delete payroll")
        Payroll timekeeping = payrollRepository.findById(id).get()
        payrollRepository.delete(timekeeping)

        return new GraphQLRetVal<String>("OK", true, "Successfully deleted payroll")
    }


}






