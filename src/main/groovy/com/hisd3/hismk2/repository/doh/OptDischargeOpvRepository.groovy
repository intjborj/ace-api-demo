package com.hisd3.hismk2.repository.doh

import com.hisd3.hismk2.domain.doh.OptDischargeOpv
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface OptDischargeOpvRepository extends JpaRepository<OptDischargeOpv, UUID> {
    @Query(value = "select c from OptDischargeOpv c")
    List<OptDischargeOpv> findAllDischargeOpv()
}