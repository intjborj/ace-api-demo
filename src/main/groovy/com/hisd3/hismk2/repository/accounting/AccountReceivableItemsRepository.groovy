package com.hisd3.hismk2.repository.accounting

import com.hisd3.hismk2.domain.accounting.AccountReceivable
import com.hisd3.hismk2.domain.accounting.AccountReceivableItems
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AccountReceivableItemsRepository extends JpaRepository<AccountReceivableItems, UUID> {

//    @Query(value = '''Select sum(a.amount) as amountTotal from AccountReceivableItems a where a.accountReceivable.companyAccount.id = :companyID and a.type = :type and (a.isVoided = FALSE OR a.isVoided IS NULL)''')
//    BigDecimal getARSumByType(@Param("companyID") UUID companyID, @Param("type") String type)
//
//    @Query(value = '''select b from AccountReceivableItems b where  b.billingItem.id = :billingItemId and (b.isVoided = FALSE OR b.isVoided IS NULL)''')
//    AccountReceivableItems checkBillingItemPosted(@Param("billingItemId") UUID billingItemId)
}
