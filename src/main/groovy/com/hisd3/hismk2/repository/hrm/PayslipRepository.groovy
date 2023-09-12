package com.hisd3.hismk2.repository.hrm

import com.hisd3.hismk2.domain.hrm.Payroll
import com.hisd3.hismk2.domain.hrm.Payslip
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PayslipRepository extends JpaRepository<Payslip, UUID> {

    @Query(value = """
         Select ps from Payslip ps 
            join fetch ps.payroll
            where ps.id = :id
    """)
    Payslip findPayslipWithPayroll(@Param("id")UUID id)

    @Query(value = """
         Select ps from Payslip ps 
            join fetch ps.employee
            where ps.id = :id
    """)
    Payslip findPayslipWithEmployee(@Param("id")UUID id)
}
