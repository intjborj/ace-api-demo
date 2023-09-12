package com.hisd3.hismk2.repository.doh

import com.hisd3.hismk2.domain.doh.DischargeEv
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface DischargeEvRepository extends JpaRepository<DischargeEv, UUID> {
    @Query(value = "select c from DischargeEv c")
    List<DischargeEv> findAllDischargeEv()
}