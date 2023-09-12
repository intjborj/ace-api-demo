package com.hisd3.hismk2.repository.doh

import com.hisd3.hismk2.domain.doh.BedCapacity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface BedCapacityRepository extends JpaRepository<BedCapacity, UUID> {
    @Query(value = "select c from BedCapacity c")
    List<BedCapacity> findAllBedCapacity()
}