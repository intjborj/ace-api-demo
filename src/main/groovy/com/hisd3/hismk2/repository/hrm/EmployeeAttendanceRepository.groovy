package com.hisd3.hismk2.repository.hrm


import com.hisd3.hismk2.domain.hrm.EmployeeAttendance
import com.hisd3.hismk2.domain.hrm.dto.EmployeeAttendanceDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import java.time.Instant

interface EmployeeAttendanceRepository extends JpaRepository<EmployeeAttendance, UUID> {

    @Query(
            value = "Select e from EmployeeAttendance e where e.employee.id = :id"
    )
    List<EmployeeAttendance> findByEmployeeID(
            @Param("id") UUID id)

    @Query(
            value = """Select e from EmployeeAttendance e 
				join fetch e.employee emp
				where emp.id = :id
				and (e.attendance_time >= :startDate and e.attendance_time <= :endDate)""",
            countQuery = """Select count(e) from EmployeeAttendance e 
				where e.employee.id = :id
				and (e.attendance_time >= :startDate and e.attendance_time <= :endDate)"""
    )
    Page<EmployeeAttendance> getEmployeeAttendance(
            @Param("id") UUID id,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            Pageable pageable)

    @Query(
            value = """Select e from EmployeeAttendance e 
				join fetch e.employee emp
				where emp.id = :id
				and (e.isIgnored is null or e.isIgnored is false)
				and (e.attendance_time >= :startDate and e.attendance_time <= :endDate)""",
            countQuery = """Select count(e) from EmployeeAttendance e 
				where e.employee.id = :id
				and (e.isIgnored is null or e.isIgnored is false)
				and (e.attendance_time >= :startDate and e.attendance_time <= :endDate)"""
    )
    Page<EmployeeAttendance> getEmployeeAttendanceExIgnored(
            @Param("id") UUID id,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            Pageable pageable)


//    @Query(value = """
// 			Select
//                    e.original_attendance_time as originalAttendanceTime,
//                    e.original_type as originalType,
//                    e.source,
//                    e.is_manual as isManual,
//                    emp.id as employeeId from hrm.employee_attendance e
//            left join hrm.employees emp on e.employee = emp.id
//            where
//                e.original_attendance_time >= :startDate and e.original_attendance_time <= :endDate
//                and e.is_manual is not true
//            order by e.original_attendance_time""", nativeQuery = true)
//    List<EmployeeAttendanceDto> getEmployeeAttendance(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate)

        @Query(value = """
 			Select
                    e.original_attendance_time as originalAttendanceTime,
                    e.original_type as originalType,
                    e.source,
                    e.is_manual as isManual,
                    cast(emp.id as varchar) as employeeId
                    from hrm.employee_attendance e
            left join hrm.employees emp on e.employee = emp.id
            where
                e.original_attendance_time >= :startDate and e.original_attendance_time <= :endDate
                and e.is_manual is not true
            order by e.original_attendance_time""", nativeQuery = true)
        List<EmployeeAttendanceDto> getEmployeeAttendance(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate)

}
