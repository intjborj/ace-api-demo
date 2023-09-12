package com.hisd3.hismk2.repository.accounting

import com.hisd3.hismk2.domain.accounting.ArLedger
import com.hisd3.hismk2.domain.billing.CompanyAccount
import org.springframework.data.jpa.repository.JpaRepository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ArLedgerRepository extends JpaRepository<ArLedger, UUID> {


}
