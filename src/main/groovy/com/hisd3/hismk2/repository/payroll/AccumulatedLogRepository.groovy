package com.hisd3.hismk2.repository.payroll

import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.payroll.AccumulatedLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AccumulatedLogRepository extends JpaRepository<AccumulatedLog, UUID> {

    @Query(
            value = """Select al from AccumulatedLog al 
                       left join AccumulatedLogSummary summary on al.summary = summary.id
                       where summary.timekeepingEmployee.id = :id"""
    )
    List<AccumulatedLog> findByTimekeepingEmployee(@Param("id") UUID id)


}
