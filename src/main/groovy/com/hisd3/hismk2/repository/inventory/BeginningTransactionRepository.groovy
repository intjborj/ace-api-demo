package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.BeginningDetails
import com.hisd3.hismk2.domain.inventory.BeginningTransaction
import com.hisd3.hismk2.domain.inventory.ReturnSupplier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BeginningTransactionRepository extends JpaRepository<BeginningTransaction, UUID> {

	@Query(value = "select q from BeginningTransaction q where q.id = :id")
	List<BeginningTransaction> getBeginningTransById(@Param('id') UUID id)

}
