package com.hisd3.hismk2.repository.doh

import com.hisd3.hismk2.domain.doh.DohExpenses
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ExpensesRepository extends JpaRepository<DohExpenses, UUID> {
    @Query(value = "select c from DohExpenses c")
    List<DohExpenses> findAllExpenses()
}