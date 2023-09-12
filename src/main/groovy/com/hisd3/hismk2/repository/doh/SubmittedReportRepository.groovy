package com.hisd3.hismk2.repository.doh

import com.hisd3.hismk2.domain.doh.SubmittedReports
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SubmittedReportRepository extends JpaRepository<SubmittedReports, UUID> {
    @Query(value = "select c from SubmittedReports c")
    List<SubmittedReports> findAllSubmittedReports()
}