package com.hisd3.hismk2.repository.payroll

import com.hisd3.hismk2.domain.payroll.OtherDeductionEmployee
import com.hisd3.hismk2.domain.payroll.Payroll
import com.hisd3.hismk2.domain.payroll.PayrollOtherDeduction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PayrollOtherDeductionRepository extends JpaRepository<PayrollOtherDeduction, UUID> {

    Optional<PayrollOtherDeduction> findByPayroll(Payroll payroll)
}
