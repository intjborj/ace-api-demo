package com.hisd3.hismk2.repository.doh

import com.hisd3.hismk2.domain.doh.DischargeMobidity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface DischargeMobidityRepository extends JpaRepository<DischargeMobidity, UUID> {
    @Query(value = "select c from DischargeMobidity c")
    List<DischargeMobidity> findAllDischargeMobidity()
}