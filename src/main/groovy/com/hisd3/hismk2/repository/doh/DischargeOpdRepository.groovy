package com.hisd3.hismk2.repository.doh

import com.hisd3.hismk2.domain.doh.DischargeOpd
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface DischargeOpdRepository extends JpaRepository<DischargeOpd, UUID> {
    @Query(value = "select c from DischargeOpd c")
    List<DischargeOpd> findAllDischargeOpd()
}