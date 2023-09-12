package com.hisd3.hismk2.repository.doh

import com.hisd3.hismk2.domain.doh.DischargeNumberDeliveries
import com.hisd3.hismk2.domain.doh.DischargeNumberDeliveries
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface DischargeNumberDeliveriesRepository extends JpaRepository<DischargeNumberDeliveries, UUID> {
    @Query(value = "select c from DischargeNumberDeliveries c")
    List<DischargeNumberDeliveries> findAllDischargeNumberDeliveries()
}