package com.hisd3.hismk2.repository.doh

import com.hisd3.hismk2.domain.doh.OperationMajorOpt
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface OperationMajorOptRepository extends JpaRepository<OperationMajorOpt, UUID> {
    @Query(value = "select c from OperationMajorOpt c")
    List<OperationMajorOpt> findAllOperationMajor()
}