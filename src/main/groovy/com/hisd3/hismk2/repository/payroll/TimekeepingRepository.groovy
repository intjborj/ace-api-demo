package com.hisd3.hismk2.repository.payroll

import com.hisd3.hismk2.domain.Authority
import com.hisd3.hismk2.domain.User
import com.hisd3.hismk2.domain.payroll.Payroll
import com.hisd3.hismk2.domain.payroll.Timekeeping
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.xmlsoap.schemas.soap.encoding.Time

interface TimekeepingRepository extends JpaRepository<Timekeeping, UUID> {
    @Query(value = "select t from Timekeeping t where t.payroll.id = :payrollId")
   Optional <Timekeeping> findByPayrollId(@Param("payrollId") UUID payrollId)

}
