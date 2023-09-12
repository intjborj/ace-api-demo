package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.StockRequestItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface StockRequestItemRepository extends JpaRepository<StockRequestItem, UUID> {
	
	@Query(value = "Select sri from StockRequestItem sri where sri.stockRequest.id = :srId")
	List<StockRequestItem> getSRItemsBySRId(@Param("srId") UUID srId)
	
	@Query(value = "Select sri from StockRequestItem sri where sri.stockRequest.patientCase.id = :caseId")
	List<StockRequestItem> getSRItemsByCaseId(@Param("caseId") UUID caseId)
	
	@Query(value = "Select sri from StockRequestItem sri where sri.stockRequest.patientCase.id = :caseId and sri.stockRequest.status = 'REQUESTED'")
	List<StockRequestItem> getSRItemsByCaseIdAndPending(@Param("caseId") UUID caseId)
	
	@Query(value = "Select sri from StockRequestItem sri where sri.stockRequest.patientCase.id = :caseId and sri.stockRequest.status = 'REQUESTED' and sri.item.id = :itemId")
	List<StockRequestItem> getSRItemsByCaseIdAndPendingAndItemId(@Param("caseId") UUID caseId, @Param("itemId") UUID itemId)
	
	@Query(value = "Select sri from StockRequestItem sri where sri.stockRequest.patientCase.id = :caseId and (sri.stockRequest.status = 'REQUESTED' or sri.stockRequest.status = 'CLAIMABLE' or sri.stockRequest.status = 'SENT') and sri.item.id = :itemId")
	List<StockRequestItem> getSRItemsByCaseIdAndPendingAndUnclaimedItemId(@Param("caseId") UUID caseId, @Param("itemId") UUID itemId)

	@Query(value = "Select sum(sri.preparedQty) from StockRequestItem sri where sri.stockRequest.patientCase.id = :caseId and (sri.stockRequest.status = 'CLAIMABLE' or sri.stockRequest.status = 'SENT' ) and sri.item.id = :itemId")
	Integer getSRItemsByCaseIdAndClaimableAndItemId(@Param("caseId") UUID caseId, @Param("itemId") UUID itemId)

	@Query(value = "Select sum(sri.requestedQty) from StockRequestItem sri where sri.stockRequest.patientCase.id = :caseId and sri.stockRequest.status = 'REQUESTED' and sri.item.id = :itemId")
	Integer getSRItemsByCaseIdAndPendingAndItemISum(@Param("caseId") UUID caseId, @Param("itemId") UUID itemId)

	@Query(value = """ Select sum(sri.requestedQty) from StockRequestItem sri 
					   where 
					   		sri.stockRequest.patientCase.id = :caseId 
					   and 
					   		(sri.stockRequest.status = 'CLAIMABLE' or  sri.stockRequest.status = 'SENT' or sri.stockRequest.status = 'CLAIMED')
					   and 
					   		(sri.noStock = true or sri.cancelled = true) 
					   and 
					   		sri.item.id = :itemId			   		
					   """)
	BigDecimal getSRIItemsByCaseIdAndNoStock(@Param("caseId") UUID caseId, @Param("itemId") UUID itemId)

	@Query(value = """ Select sum(sri.requestedQty) from StockRequestItem sri 
					   where 
					   		sri.stockRequest.patientCase.id = :caseId 
					   and 
					   		sri.stockRequest.status = 'CANCELLED'
					   and 
					   		(sri.noStock = false or sri.noStock is null) 
					   and 
					   		(sri.cancelled = false or sri.cancelled is null) 
					   and 
					   		sri.item.id = :itemId			   		
					   """)
	BigDecimal getSRIItemsByCaseIdAndCancelled(@Param("caseId") UUID caseId, @Param("itemId") UUID itemId)

	@Query(value = """ Select sum(sri.requestedQty) from StockRequestItem sri 
					   where 
					   		sri.stockRequest.patientCase.id = :caseId 
					   and 
					   		sri.stockRequest.status = 'CLAIMED'
					   and 
					   		(sri.noStock = false or sri.noStock = false) 
					   	and 
					   		(sri.cancelled = false or sri.cancelled is null) 
					   and 
					   		sri.item.id = :itemId			   		
					   """)
	BigDecimal getSRIItemsByCaseIdAndClaimedOnHand(@Param("caseId") UUID caseId, @Param("itemId") UUID itemId)


}
