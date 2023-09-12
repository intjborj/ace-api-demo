package com.hisd3.hismk2.repository.accounting


import com.hisd3.hismk2.domain.accounting.ArTransactionType
import com.hisd3.hismk2.domain.accounting.BsPhilClaims
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BsPhilClaimsRepository extends JpaRepository<BsPhilClaims, UUID> {

//    @Query(value = '''select b from BsPhilClaims b where b.billingItem.billing.patientCase.caseNo = :caseNo and b.billingSchedule.id = :billingScheduleId''')
//    List<BsPhilClaims> getClaimsPerSchedule(@Param("billingScheduleId") UUID billingScheduleId,@Param("caseNo") String caseNo)
}
