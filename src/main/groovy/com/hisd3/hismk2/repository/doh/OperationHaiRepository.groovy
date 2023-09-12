package com.hisd3.hismk2.repository.doh

import com.hisd3.hismk2.domain.doh.OperationHai
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface OperationHaiRepository extends JpaRepository<OperationHai, UUID> {
    @Query(value = "select c from OperationHai c")
    List<OperationHai> findAllOperationHai()
}