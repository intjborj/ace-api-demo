package com.hisd3.hismk2.repository.doh

import com.hisd3.hismk2.domain.doh.Revenues
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface RevenuesRepository extends JpaRepository<Revenues, UUID> {
    @Query(value = "select c from Revenues c")
    List<Revenues> findAllRevenues()
}