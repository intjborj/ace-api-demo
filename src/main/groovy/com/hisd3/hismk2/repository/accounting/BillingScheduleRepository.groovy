package com.hisd3.hismk2.repository.accounting

import com.hisd3.hismk2.domain.accounting.BillingSchedule
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BillingScheduleRepository extends JpaRepository<BillingSchedule, UUID> {
	

	
}
