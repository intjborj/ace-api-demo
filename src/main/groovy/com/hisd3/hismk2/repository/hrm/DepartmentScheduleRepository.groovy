package com.hisd3.hismk2.repository.hrm

import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.hrm.DepartmentSchedule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DepartmentScheduleRepository extends JpaRepository<DepartmentSchedule, UUID> {

    @Query(value = "Select s from DepartmentSchedule s join fetch s.department d where d.id = :id order by s.dateTimeStartRaw")
    List<DepartmentSchedule> getOneDepartmentSchedule(@Param("id")UUID id)

    @Query(value = "Select s from DepartmentSchedule s join fetch s.department order by s.dateTimeStartRaw")
    List<DepartmentSchedule> getAllDepartmentSchedules()

}
