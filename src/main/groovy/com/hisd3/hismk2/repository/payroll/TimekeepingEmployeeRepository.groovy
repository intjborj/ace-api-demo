package com.hisd3.hismk2.repository.payroll

import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.payroll.TimekeepingEmployee
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface TimekeepingEmployeeRepository extends JpaRepository<TimekeepingEmployee, UUID> {


    @Query(
            value = """Select te.payrollEmployee.employee from TimekeepingEmployee te where te.timekeeping.id = :id"""
    )
    List<Employee> findByTimekeepingEmployee(@Param("id") UUID id)

//
    @Query(
            value = """Select te from TimekeepingEmployee te where te.timekeeping.id in :id"""
    )
    List<TimekeepingEmployee> findByTimekeepingId(@Param("id") UUID id)
//
//    @Query(
//            value = """Select te from TimekeepingEmployee te where te.employee.id in :ids"""
//    )
//    List<TimekeepingEmployee> findByEmployeeIds(@Param("ids") UUID ids)

}
