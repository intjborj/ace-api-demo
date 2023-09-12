package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.BeginningBalance
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BeginningBalanceRepository extends JpaRepository<BeginningBalance, UUID> {
	
	@Query(value = "select q from BeginningBalance q where q.item.id = :id")
	List<BeginningBalance> getBeginningById(@Param('id') UUID id)
	
}
