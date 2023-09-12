package com.hisd3.hismk2.repository.payroll


import com.hisd3.hismk2.domain.payroll.PayrollEmployeeContribution
import org.springframework.data.jpa.repository.JpaRepository

interface PayrollEmployeeContributionRepository extends JpaRepository<PayrollEmployeeContribution, UUID> {


}
