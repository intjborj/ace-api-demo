package com.hisd3.hismk2.repository.accounting

import com.hisd3.hismk2.domain.accounting.Bank
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BankRepository extends JpaRepository<Bank, UUID> {
	
	@Query(value = """
    Select c from Bank c  where lower(c.bankname) like lower(concat('%',:filter,'%'))
    or
    lower(c.accountNumber) like lower(concat('%',:filter,'%'))
""",
			countQuery = """
     Select count(c) from Bank c  where lower(c.bankname) like lower(concat('%',:filter,'%'))
     or
    lower(c.accountNumber) like lower(concat('%',:filter,'%'))
""")
	Page<Bank> getBanks(@Param("filter") String filter,
	                    Pageable page)
	
}
