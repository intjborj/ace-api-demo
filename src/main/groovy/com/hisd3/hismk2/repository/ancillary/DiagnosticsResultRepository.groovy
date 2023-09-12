package com.hisd3.hismk2.repository.ancillary

import com.hisd3.hismk2.domain.ancillary.DiagnosticResult
import groovy.transform.TypeChecked
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

@TypeChecked
interface DiagnosticsResultRepository extends JpaRepository<DiagnosticResult, UUID> {
	
	@Query(
			value = "Select d from DiagnosticResult d where d.orderSlipItem.service.id =:id"
	)
	List<DiagnosticResult> findByService(@Param("id") UUID id)
	
	@Query(
			value = "Select d from DiagnosticResult d where d.orderSlipItem.id =:id"
	)
	List<DiagnosticResult> findByOrderSlipItem(@Param("id") UUID id)

	@Query(
			value = "Select d from DiagnosticResult d where d.patient.id =:id"
	)
	List<DiagnosticResult> resultsByPatienId(@Param("id") UUID id)

	@Query(
			value = "Select d from DiagnosticResult d where d.orderSlipItem.orderslip.parentCase.id = :caseId"
	)
	List<DiagnosticResult> resultsByCaseId(@Param("caseId") UUID caseId)

	@Query(
			value = '''Select d from DiagnosticResult d where 
						d.orderSlipItem.orderslip.parentCase.id = :caseId and 
						d.service.id = :serviceId order by d.createdDate DESC'''
	)
	List<DiagnosticResult> resultsDataByService(@Param("caseId") UUID caseId, @Param("serviceId") UUID serviceId)
	
}
