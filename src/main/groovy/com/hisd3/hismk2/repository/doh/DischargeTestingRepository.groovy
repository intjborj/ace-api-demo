package com.hisd3.hismk2.repository.doh

import com.hisd3.hismk2.domain.doh.DischargesTesting
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface DischargeTestingRepository extends JpaRepository<DischargesTesting, UUID> {
    @Query(value = "select c from DischargesTesting c")
    List<DischargesTesting> findAllTesting()
}