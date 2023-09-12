package com.hisd3.hismk2.repository.doh

import com.hisd3.hismk2.domain.doh.DischargesEr
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface DischargeErRepository extends JpaRepository<DischargesEr, UUID> {
    @Query(value = "select c from DischargesEr c")
    List<DischargesEr> findAllEr()
}