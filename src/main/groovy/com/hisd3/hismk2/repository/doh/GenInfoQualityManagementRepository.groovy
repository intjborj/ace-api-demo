package com.hisd3.hismk2.repository.doh

import com.hisd3.hismk2.domain.doh.GenInfoQualityManagements
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface GenInfoQualityManagementRepository  extends JpaRepository<GenInfoQualityManagements, UUID> {
    @Query(value = "select c from GenInfoQualityManagements c")
    List<GenInfoQualityManagements> findAllQualityManagement()
}