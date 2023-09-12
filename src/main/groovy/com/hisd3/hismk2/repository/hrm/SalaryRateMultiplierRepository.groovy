package com.hisd3.hismk2.repository.hrm

import com.hisd3.hismk2.domain.hrm.SalaryRateMultiplier
import org.springframework.data.jpa.repository.JpaRepository

interface SalaryRateMultiplierRepository extends JpaRepository<SalaryRateMultiplier, UUID> {

}
