package com.hisd3.hismk2.repository.accounting

import com.hisd3.hismk2.domain.accounting.AccountReceivable
import com.hisd3.hismk2.domain.accounting.AccountReceivableCompany
import com.hisd3.hismk2.domain.billing.CompanyAccount
import com.hisd3.hismk2.domain.inventory.Inventory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AccountReceivableCompanyRepository extends JpaRepository<AccountReceivableCompany, UUID> {
	
	@Query(value = "select distinct(arc.companyAccount) from AccountReceivableCompany arc where arc.accountReceivable.balance > 0")
	List<CompanyAccount> companyWithARList()

}
