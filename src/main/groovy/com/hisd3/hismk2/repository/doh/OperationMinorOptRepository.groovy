package com.hisd3.hismk2.repository.doh

import com.hisd3.hismk2.domain.doh.OperationMinorOpt
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface OperationMinorOptRepository extends JpaRepository<OperationMinorOpt, UUID> {
    @Query(value = "select c from OperationMinorOpt c")
    List<OperationMinorOpt> findAllOperationMinor()
}