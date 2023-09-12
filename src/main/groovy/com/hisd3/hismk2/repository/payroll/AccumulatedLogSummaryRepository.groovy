package com.hisd3.hismk2.repository.payroll


import com.hisd3.hismk2.domain.payroll.AccumulatedLog
import com.hisd3.hismk2.domain.payroll.AccumulatedLogSummary
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AccumulatedLogSummaryRepository extends JpaRepository<AccumulatedLogSummary, UUID> {

    @Query(
            value = """Select summary from AccumulatedLogSummary summary where summary.timekeepingEmployee.id = :id 
                       """
    )
    List<AccumulatedLogSummary> findByTimekeepingEmployee(@Param("id") UUID id)


}
