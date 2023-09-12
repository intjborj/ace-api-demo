package com.hisd3.hismk2.repository.accounting

import com.hisd3.hismk2.domain.accounting.AccountReceivable
import com.hisd3.hismk2.graphqlservices.accounting.AccountReceivableServices
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AccountReceivableRepository extends JpaRepository<AccountReceivable, UUID> {

    @Query(value = '''select b from AccountReceivable b where b.groups['BILLING_SCHEDULE_ID'] = :billingScheduleId and b.status = 'active' ''')
    AccountReceivable getARByBillingId(@Param("billingScheduleId") UUID billingScheduleId)

    @Query(value = '''select extract (year from b.transactionDate) as transDate from AccountReceivable b where b.groups['COMPANY_ACCOUNT_ID'] = :company_id group by extract (year from b.transactionDate)''')
    List<String> getARYearListByCompany(@Param("company_id") UUID company_id)
//
//    @Query(value = '''Select sum(a.payment) as balance from AccountReceivable a where a.companyAccount.id = :companyId and (a.isVoided = FALSE OR a.isVoided IS NULL) ''')
//    BigDecimal getARSumPaymentByCompany(@Param("companyId") UUID companyId)
//
//    @Query(value = '''Select sum(a.memo) as balance from AccountReceivable a where a.companyAccount.id = :companyId and (a.isVoided = FALSE OR a.isVoided IS NULL)''')
//    BigDecimal getARSumMemoByCompany(@Param("companyId") UUID companyId)
}
