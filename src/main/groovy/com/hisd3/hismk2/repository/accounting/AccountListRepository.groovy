package com.hisd3.hismk2.repository.accounting

import com.hisd3.hismk2.domain.accounting.AccountList
import org.springframework.data.jpa.repository.JpaRepository

interface AccountListRepository extends JpaRepository<AccountList, UUID> {

}
