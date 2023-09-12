package com.hisd3.hismk2.graphqlservices.payroll

import com.hisd3.hismk2.domain.payroll.Payroll
import com.hisd3.hismk2.domain.payroll.PayrollEmployee

interface IPayrollEmployeeBaseOperation<T> {


    /**
     * This method is used when we are adding payrollEmployee
     * from payroll when payroll has already started or
     * has the status of {@code ACTIVE}
     * @param payrollEmployee payrollEmployee that we want to add
     * @param payroll payroll entity.
     */
    T addEmployee(PayrollEmployee payrollEmployee, Payroll payroll)

    /**
     * This method is used when we are removing payrollEmployee
     * from payroll when payroll has already started or
     * has the status of {@code ACTIVE}
     * @param payrollEmployee payrollEmployee that we want to remove
     * @param payroll payroll entity.
     */
    void removeEmployee(PayrollEmployee payrollEmployee, Payroll payroll)

    /**
     * The same operation as {@link #addEmployee addEmployee}
     * but accepts {@code List<Employee>} as argument.
     * @param payrollEmployees List of payrollEmployees that you want to add.
     * @param payroll payroll entity.
     */
    List<T> addEmployees(List<PayrollEmployee> payrollEmployees, Payroll payroll)

    /**
     * The same operation as {@link #removeEmployee removeEmployee}
     * but accepts {@code List<Employee>} as argument.
     * @param payrollEmployees List of payrollEmployees that you want to add.
     * @param payroll payroll entity.
     */
    void removeEmployees(List<PayrollEmployee> payrollEmployees, Payroll payroll)

    /**
     * Used for recalculating payrollEmployee from the module's calculation when
     * payroll module was first created
     * @param payrollEmployee payrollEmployee that you want to recalculate
     * @param payroll payroll entity.
     */
    T recalculateEmployee(PayrollEmployee payrollEmployee, Payroll payroll)

    /**
     * The same operation as {@link #recalculateEmployee recalculateEmployee}
     * but recalculates all of employees in the payroll module.
     * @param payroll payroll entity.
     */
    void recalculateAllEmployee(Payroll payroll)

}