package com.hisd3.hismk2.graphqlservices.payroll

import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.hrm.OtherDeduction
import com.hisd3.hismk2.domain.payroll.*
import com.hisd3.hismk2.domain.payroll.enums.PayrollEmployeeStatus
import com.hisd3.hismk2.domain.payroll.enums.TimekeepingStatus
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.hrm.OtherDeductionRepository
import com.hisd3.hismk2.repository.payroll.*
import groovy.transform.TypeChecked
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@TypeChecked
@Component
@GraphQLApi
class PayrollOtherDeductionService implements IPayrollOperation<PayrollOtherDeduction, OtherDeductionEmployee> {

    @Autowired
    PayrollOtherDeductionRepository payrollOtherDeductionRepository

    @Autowired
    PayrollEmployeeRepository payrollEmployeeRepository

    @Autowired
    EmployeeRepository employeeRepository

    @Autowired
    OtherDeductionEmployeeRepository otherDeductionEmployeeRepository

    @Autowired
    OtherDeductionEmployeeItemRepository otherDeductionEmployeeItemRepository

    @Autowired
    OtherDeductionRepository otherDeductionRepository

    /**
     * Method that will be used to run when the payroll starts and
     * creates all the modules.
     * @param payroll the payroll that has started
     */
    @Override
    PayrollOtherDeduction startPayroll(Payroll payroll) {
        PayrollOtherDeduction otherDeduction = new PayrollOtherDeduction()
        otherDeduction.payroll = payroll
        otherDeduction.status = TimekeepingStatus.ACTIVE
        otherDeduction = payrollOtherDeductionRepository.save(otherDeduction)

        payroll.otherDeduction = otherDeduction
        addEmployees(payroll.payrollEmployees, payroll)
        return otherDeduction
    }

    /**
     * Method that will be used to run when the payroll is finalized
     * @param payroll the payroll that was finalized
     */
    @Override
    void finalizePayroll(Payroll payroll) {

    }

    /**
     * This method is used when we are adding employee
     * from payroll when payroll has already started or
     * has the status of {@code ACTIVE}
     * @param payrollEmployee employee that we want to add
     * @param payroll payroll entity.
     */
    @Override
    OtherDeductionEmployee addEmployee(PayrollEmployee payrollEmployee, Payroll payroll) {
        List<OtherDeduction> otherDeductions = otherDeductionRepository.findOtherDeductionByEmployees(payrollEmployee.employee.id)

        if (!payroll) {
            throw new RuntimeException("Failed to create employee other deduction: No OtherDeduction Found")
        }

        OtherDeductionEmployee otherDeductionEmployee = populateOtherDeductionEmployee(payrollEmployee, payroll.otherDeduction)
        otherDeductionEmployee = otherDeductionEmployeeRepository.save(otherDeductionEmployee)

        List<OtherDeductionEmployeeItem> otherDeductionItems = []
        otherDeductions.each { OtherDeduction deduction ->
            otherDeductionItems << populateOtherDeductionEmployeeItem(deduction, otherDeductionEmployee)
        }

        otherDeductionItems = otherDeductionEmployeeItemRepository.saveAll(otherDeductionItems)

        otherDeductionEmployee.deductionItems.addAll(otherDeductionItems)

        return otherDeductionEmployee
    }

    /**
     * This method is used when we are removing employee
     * from payroll when payroll has already started or
     * has the status of {@code ACTIVE}
     * @param payrollEmployee employee that we want to remove
     * @param payroll payroll entity.
     */
    @Override
    void removeEmployee(PayrollEmployee payrollEmployee, Payroll payroll) {
        // TODO: try deleting OtherDeductionEmployee and check if it is also deleting the items.
        OtherDeductionEmployee otherDeductionEmployee = null
        otherDeductionEmployeeRepository.findByPayrollEmployee(payrollEmployee).ifPresent { otherDeductionEmployee = it }

        otherDeductionEmployeeRepository.delete(otherDeductionEmployee)
    }

    /**
     * The same operation as {@link com.hisd3.hismk2.graphqlservices.payroll.IPayrollEmployeeBaseOperation#addEmployee(PayrollEmployee, Payroll) addEmployee}
     * but accepts {@code List<Employee>} as argument.
     * @param payrollEmployees List of employees that you want to add.
     * @param payrollModule payroll entity.
     */
    @Override
    List<OtherDeductionEmployee> addEmployees(List<PayrollEmployee> payrollEmployees, Payroll payroll) {

        List<OtherDeductionEmployee> otherDeductionEmployees = []

        payrollEmployees.each { otherDeductionEmployees << populateOtherDeductionEmployee(it, payroll.otherDeduction) }
        otherDeductionEmployees = otherDeductionEmployeeRepository.saveAll(otherDeductionEmployees)

        this.addOtherDeductionEmployeeItems(payroll, otherDeductionEmployees)

        return otherDeductionEmployees
    }

    /**
     * The same operation as {@link com.hisd3.hismk2.graphqlservices.payroll.IPayrollEmployeeBaseOperation#removeEmployee(PayrollEmployee, Payroll) removeEmployee}
     * but accepts {@code List<Employee>} as argument.
     * @param payrollEmployees List of employees that you want to add.
     * @param payroll payroll entity.
     */
    @Override
    void removeEmployees(List<PayrollEmployee> payrollEmployees, Payroll payroll) {
        // TODO: should be the same logic as removeEmployee just has some looping logic.
        // TODO: try deleting OtherDeductionEmployee and check if it is also deleting the items.
        List<OtherDeductionEmployee> otherDeductionEmployees = otherDeductionEmployeeRepository.findByPayrollEmployees(payrollEmployees)
        otherDeductionEmployeeRepository.deleteInBatch(otherDeductionEmployees)
    }

    /**
     * Used for recalculating employee from the module's calculation when
     * payroll module was first created
     * @param payrollEmployee employee that you want to recalculate
     * @param payrollModule payroll entity.
     */
    @Override
    OtherDeductionEmployee recalculateEmployee(PayrollEmployee payrollEmployee, Payroll payroll) {
        List<OtherDeduction> otherDeductions = otherDeductionRepository.findOtherDeductionByEmployees(payrollEmployee.employee.id)

        OtherDeductionEmployee otherDeductionEmployee = null
        otherDeductionEmployeeRepository.findByPayrollEmployee(payrollEmployee).ifPresent { otherDeductionEmployee = it }
        otherDeductionEmployee.deductionItems.clear()

        List<OtherDeductionEmployeeItem> otherDeductionItems = []
        otherDeductions.each { OtherDeduction deduction ->
            otherDeductionItems << populateOtherDeductionEmployeeItem(deduction, otherDeductionEmployee)
        }

        otherDeductionEmployeeItemRepository.saveAll(otherDeductionItems)

        return otherDeductionEmployee
    }

    /**
     * The same operation as {@link com.hisd3.hismk2.graphqlservices.payroll.IPayrollEmployeeBaseOperation#recalculateEmployee(PayrollEmployee, Payroll) recalculateEmployee}
     * but recalculates all of employees in the payroll module.
     * @param payroll payroll entity.
     */
    @Override
    void recalculateAllEmployee(Payroll payroll) {
        List<OtherDeductionEmployee> otherDeductionEmployees = otherDeductionEmployeeRepository.findAllByOtherDeduction(payroll.otherDeduction)

        //We just need to delete the items to recalculate, no need to delete the OtherDeductionEmployee entities.
        otherDeductionEmployeeItemRepository.deleteInBatch(otherDeductionEmployeeItemRepository.findByOtherDeductionEmployeeIn(otherDeductionEmployees))

        this.addOtherDeductionEmployeeItems(payroll, otherDeductionEmployees)
    }

    //==========================UTILITY METHODS==========================\\
    private static OtherDeductionEmployeeItem populateOtherDeductionEmployeeItem(OtherDeduction deduction, OtherDeductionEmployee employee) {
        OtherDeductionEmployeeItem otherDeductionItem = new OtherDeductionEmployeeItem()
        otherDeductionItem.amount = deduction.amount
        otherDeductionItem.title = deduction.title
        otherDeductionItem.identifier = deduction.identifier
        otherDeductionItem.otherDeductionEmployee = employee

        return otherDeductionItem
    }

    private static OtherDeductionEmployee populateOtherDeductionEmployee(PayrollEmployee employee, PayrollOtherDeduction payrollModule) {
        OtherDeductionEmployee otherDeductionEmployee = new OtherDeductionEmployee()
        otherDeductionEmployee.payrollEmployee = employee
        //TODO: Possibly change this to a whole different enum
        otherDeductionEmployee.status = PayrollEmployeeStatus.DRAFT
        otherDeductionEmployee.otherDeduction = payrollModule

        return otherDeductionEmployee
    }

    private List<OtherDeductionEmployeeItem> addOtherDeductionEmployeeItems(Payroll payroll, List<OtherDeductionEmployee> otherDeductionEmployees) {
        List<Employee> employees = payrollEmployeeRepository.findByPayrollEmployees(payroll.id)
        Map<UUID, List<OtherDeduction>> employeeOtherDeductions = employeeRepository
                .getEmployeesWithOtherDeduction(employees)
                .collectEntries { [it.id, it.otherDeductions] }
        List<OtherDeductionEmployeeItem> otherDeductionItems = []
        otherDeductionEmployees.each { employee ->
            employeeOtherDeductions.get(employee.payrollEmployee.employee.id).each { deduction ->
                OtherDeductionEmployeeItem otherDeductionItem = populateOtherDeductionEmployeeItem(deduction, employee)
                employee.deductionItems.add(otherDeductionItem)
                otherDeductionItems << otherDeductionItem
            }
        }
        return otherDeductionEmployeeItemRepository.saveAll(otherDeductionItems)
    }



}
