package com.hisd3.hismk2.repository.payroll

import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.payroll.PayrollEmployee
import com.hisd3.hismk2.domain.payroll.PayrollOtherDeduction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PayrollEmployeeRepository extends JpaRepository<PayrollEmployee, UUID> {



    @Query(
            value = """Select te.employee from PayrollEmployee te where te.payroll.id = :id"""
    )
    List<Employee> findByPayrollEmployees(@Param("id") UUID id)

    @Query(
            value = """Select te from PayrollEmployee te where te.payroll.id = :id"""
    )
    List<PayrollEmployee> findByPayrollId(@Param("id") UUID id)

    @Query("""Select te from PayrollEmployee te left join te.payroll p left join p.otherDeduction od where od = otherDeduction""")
    List<PayrollEmployee> findByPayrollOtherDeduction(@Param("otherDeduction") PayrollOtherDeduction otherDeduction)





}
