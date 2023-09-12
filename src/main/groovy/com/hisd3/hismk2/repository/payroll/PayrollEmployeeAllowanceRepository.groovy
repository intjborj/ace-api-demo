package com.hisd3.hismk2.repository.payroll

import com.hisd3.hismk2.domain.payroll.OtherDeductionEmployee
import com.hisd3.hismk2.domain.payroll.OtherDeductionEmployeeItem
import com.hisd3.hismk2.domain.payroll.PayrollEmployeeAllowance
import org.springframework.data.jpa.repository.JpaRepository

interface PayrollEmployeeAllowanceRepository extends JpaRepository<PayrollEmployeeAllowance, UUID> {


}
