package com.hisd3.hismk2.repository.hospital_config

import com.hisd3.hismk2.domain.hospital_config.CostEstimationItem
import org.springframework.data.jpa.repository.JpaRepository

interface CostEstimationItemRepository extends JpaRepository<CostEstimationItem, UUID> {

}
