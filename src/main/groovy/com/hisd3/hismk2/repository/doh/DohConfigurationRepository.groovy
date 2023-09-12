package com.hisd3.hismk2.repository.doh

import com.hisd3.hismk2.domain.doh.BedCapacity
import com.hisd3.hismk2.domain.doh.DohConfiguration
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface DohConfigurationRepository extends JpaRepository<DohConfiguration, UUID> {

}