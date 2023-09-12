package com.hisd3.hismk2.repository.payroll

import com.hisd3.hismk2.domain.payroll.OtherDeductionEmployee
import com.hisd3.hismk2.domain.payroll.PayrollEmployee
import com.hisd3.hismk2.domain.payroll.PayrollOtherDeduction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface OtherDeductionEmployeeRepository extends JpaRepository<OtherDeductionEmployee, UUID> {

    @Query("Select oe from OtherDeductionEmployee oe left join fetch oe.deductionItems left join oe.payrollEmployee e where e = :employee")
    Optional<OtherDeductionEmployee> findByPayrollEmployee(@Param("employee")PayrollEmployee employee)

    @Query("Select oe from OtherDeductionEmployee oe left join fetch oe.deductionItems left join oe.payrollEmployee e where e IN (:employees)")
    List<OtherDeductionEmployee> findByPayrollEmployees(@Param("employees")List<PayrollEmployee> employees)

    @Query("""Select oe from OtherDeductionEmployee oe 
                  left join fetch oe.otherDeduction  d
              where d = :otherDeduction""")
    List<OtherDeductionEmployee> findAllByOtherDeduction(@Param("otherDeduction")PayrollOtherDeduction otherDeduction)
}
