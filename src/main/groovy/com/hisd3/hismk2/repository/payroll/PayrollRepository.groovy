package com.hisd3.hismk2.repository.payroll

import com.hisd3.hismk2.domain.payroll.Payroll
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PayrollRepository extends JpaRepository<Payroll, UUID> {


    @Query(value = "select p from Payroll p where (lower(p.title) like lower(concat('%',:filter,'%')))",
            countQuery = "select count(p) from Payroll p where (lower(p.title) like lower(concat('%',:filter,'%')))")
    Page<Payroll> getPayrollByFilterPageable(@Param("filter") String filter, Pageable pageable)

    @Query(value = "select p from Payroll p where (lower(p.title) like lower(concat('%',:filter,'%')))",
            countQuery = "select count(p) from Payroll p where (lower(p.title) like lower(concat('%',:filter,'%')))")
    Page<Payroll> getPayroll(@Param("filter") String filter, Pageable pageable)

}
