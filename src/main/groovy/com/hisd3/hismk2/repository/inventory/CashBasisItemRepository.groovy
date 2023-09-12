package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.CashBasisItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CashBasisItemRepository extends JpaRepository<CashBasisItem, UUID> {

}
