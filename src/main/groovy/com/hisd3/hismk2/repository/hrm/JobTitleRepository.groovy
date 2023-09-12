package com.hisd3.hismk2.repository.hrm

import com.hisd3.hismk2.domain.hrm.EmployeeAttendance
import com.hisd3.hismk2.domain.hrm.JobTitle
import com.hisd3.hismk2.domain.payroll.Timekeeping
import com.hisd3.hismk2.domain.hrm.enums.JobTitleStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface JobTitleRepository extends JpaRepository<JobTitle, UUID> {

    @Query(
            value = "Select j from JobTitle j where j.status = 'ACTIVE'"
    )
    List<JobTitle> findByActive()


    @Query(
            value = "Select j.value from JobTitle j where j.value = :val"
    )
    String findByValue(@Param("val") String val)

    @Query(
            value = "Select j.status from JobTitle j where j.value = :jobTitle"
    )
    JobTitleStatus getStatusByValue(@Param("jobTitle") String jobTitle)
}
