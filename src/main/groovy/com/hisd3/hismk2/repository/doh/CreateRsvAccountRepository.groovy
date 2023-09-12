package com.hisd3.hismk2.repository.doh

import com.hisd3.hismk2.domain.doh.CreateRsvAccount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CreateRsvAccountRepository extends JpaRepository<CreateRsvAccount, UUID> {
    @Query(value = "select c from CreateRsvAccount c")
    List<CreateRsvAccount> findAllCreateRsvAccount()

}