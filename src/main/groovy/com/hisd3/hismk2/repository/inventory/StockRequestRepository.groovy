package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.StockRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface StockRequestRepository extends JpaRepository<StockRequest, UUID> {
	
	@Query(value = "Select sr from StockRequest sr where sr.status = :status and sr.patient.id = :patientId")
	List<StockRequest> getStockRequestsByPatientAndStatus(@Param("status") String status, @Param("patientId") UUID patientId)
	
	@Query(value = "Select sr from StockRequest sr where sr.status = :status")
	List<StockRequest> getStockRequestsByStatus(@Param("status") String status)
	
	@Query(value = "Select sr from StockRequest sr where sr.patient.id = :patientId")
	List<StockRequest> getStockRequestsByPatient(@Param("patientId") UUID patientId)
	
	@Query(value = "Select sr from StockRequest sr where sr.stockRequestNo = :srNo")
	StockRequest stockRequestBySRNo(@Param("srNo") String srNo)
	
	@Query(value = "Select sr from StockRequest sr where sr.id = :id")
	StockRequest getById(@Param("id") UUID id)
	
	@Query(value = '''Select sr from StockRequest sr where
						(lower(sr.patient.lastName) like lower(concat('%',:search,'%')) or
						lower(sr.patient.firstName) like lower(concat('%',:search,'%')) or
						lower(sr.patient.middleName) like lower(concat('%',:search,'%'))) and
						sr.status = :status and 
						sr.patientCase.status <> 'DISCHARGED'  and 
						sr.patientCase.status <> 'CLOSED' 
						 order by sr.requestedByDatetime DESC''')
	List<StockRequest> searchStockRequest(@Param("search") String search, @Param("status") String status)
	
	@Query(value = "Select count(sr) from StockRequest sr where sr.status = :status and sr.patientCase.status <> 'DISCHARGED'  and  sr.patientCase.status <> 'CLOSED'")
	Integer getStockRequestsCountByStatus(@Param("status") String status)
	
}
