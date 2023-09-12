package com.hisd3.hismk2.repository.doh

import com.hisd3.hismk2.domain.doh.OperationMortalityDeaths
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface OperationMortalityDeathRepository extends JpaRepository<OperationMortalityDeaths, UUID> {
    @Query(value = "select c from OperationMortalityDeaths c")
    List<OperationMortalityDeaths> findAllMortalityDeathsRepository()
}