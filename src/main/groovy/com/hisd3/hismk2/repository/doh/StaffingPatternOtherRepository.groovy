package com.hisd3.hismk2.repository.doh

import com.hisd3.hismk2.domain.doh.StaffingPatternOthers
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface StaffingPatternOtherRepository extends JpaRepository<StaffingPatternOthers, UUID> {
    @Query(value = "select c from StaffingPatternOthers c")
    List<StaffingPatternOthers> findAllStaffingPatternOthers()
}