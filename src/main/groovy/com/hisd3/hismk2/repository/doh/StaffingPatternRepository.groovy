package com.hisd3.hismk2.repository.doh

import com.hisd3.hismk2.domain.doh.StaffingPattern
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface StaffingPatternRepository extends JpaRepository<StaffingPattern, UUID> {
    @Query(value = "select c from StaffingPattern c")
    List<StaffingPattern> findAllStaffingPattern()
}