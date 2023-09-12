package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.CashBasis
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CashBasisRepository extends JpaRepository<CashBasis, UUID> {

    @Query(value = "Select s from CashBasis s where s.department.id = :departmentId and  upper(s.patient.fullName) like  upper(concat('%',:filter,'%')) order by s.patient.fullName asc",
            countQuery = "Select s from CashBasis s where s.department.id = :departmentId and upper(s.patient.fullName) like  upper(concat('%',:filter,'%')) order by s.patient.fullName asc")
    Page<CashBasis> getCashBasisPatientListPageable(@Param("departmentId") UUID departmentId, @Param("filter") String filter, Pageable pageable)

    @Query(value = "Select s from CashBasis s where s.patientCase.id = :caseId and  upper(s.status) like  upper('Pending') ")
    List<CashBasis> getPendingCashBasisByCase(@Param("caseId") UUID caseId)

}
