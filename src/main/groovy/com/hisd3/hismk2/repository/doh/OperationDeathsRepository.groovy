package com.hisd3.hismk2.repository.doh

import com.hisd3.hismk2.domain.doh.OperationDeaths
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface OperationDeathsRepository extends JpaRepository<OperationDeaths, UUID> {
    @Query(value = "select c from OperationDeaths c")
    List<OperationDeaths> findAllOperationDeaths()
}