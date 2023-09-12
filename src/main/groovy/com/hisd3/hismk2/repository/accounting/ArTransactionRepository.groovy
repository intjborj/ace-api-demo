package com.hisd3.hismk2.repository.accounting

import com.hisd3.hismk2.domain.accounting.AccountReceivable
import com.hisd3.hismk2.domain.accounting.ArTransaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ArTransactionRepository extends JpaRepository<ArTransaction, UUID> {


}
