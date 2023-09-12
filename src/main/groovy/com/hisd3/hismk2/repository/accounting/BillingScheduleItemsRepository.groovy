package com.hisd3.hismk2.repository.accounting

import com.hisd3.hismk2.domain.accounting.BillingSchedule
import com.hisd3.hismk2.domain.accounting.BillingScheduleItems
import com.hisd3.hismk2.domain.ancillary.AncillaryConfig
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BillingScheduleItemsRepository extends JpaRepository<BillingScheduleItems, UUID> {

    @Query(value = '''Select sum(a.amount) as amountTotal from BillingScheduleItems a where a.billingSchedule.id = :billingSchedule and (a.isVoided = FALSE OR a.isVoided IS NULL)''')
    BigDecimal getSumBillingSchedule(@Param("billingSchedule") UUID billingSchedule)
	
}
