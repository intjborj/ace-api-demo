package com.hisd3.hismk2.repository.billing

import com.hisd3.hismk2.domain.billing.InvestorPaymentLedger
import com.hisd3.hismk2.domain.billing.ServicePriceControl
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface InvestorPaymentLedgerRepository extends JpaRepository<InvestorPaymentLedger, UUID> {

}
