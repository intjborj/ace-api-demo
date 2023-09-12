package com.hisd3.hismk2.repository.hrm

import com.hisd3.hismk2.domain.hrm.Allowance
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.hrm.EmployeeAllowance
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface EmployeeAllowanceRepository extends JpaRepository<EmployeeAllowance, UUID> {

    @Query(value= "Select c from EmployeeAllowance c")
    List<EmployeeAllowance> findAllEmployeeAllowance()

}