package com.hisd3.hismk2.repository.accounting

import com.hisd3.hismk2.domain.accounting.AccountReceivable
import com.hisd3.hismk2.domain.accounting.Ledger
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface LedgerRepository extends JpaRepository<Ledger, UUID> {

    @Modifying
    @Query("delete from Ledger b where b.id=:id")
    void deleteLedger(@Param("id") UUID id);
}
