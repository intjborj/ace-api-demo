package com.hisd3.hismk2.repository.billing

import com.hisd3.hismk2.domain.billing.ItemPriceControl
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ItemPriceControlRepository extends JpaRepository<ItemPriceControl, UUID> {
	
	@Query(value = "Select pc From ItemPriceControl pc where pc.item.id = :itemId and pc.priceTierDetail.id = :tierId")
	ItemPriceControl getItemPriceControl(@Param("tierId") UUID tierId, @Param("itemId") UUID itemId)
	
	@Query(value = "select pc from ItemPriceControl pc where pc.priceTierDetail.id = :tierId and pc.item.id = :itemId")
	ItemPriceControl getItemByIdAndTier(@Param("tierId") UUID tierId, @Param("itemId") UUID itemId)
	
	@Query(value = "Select pc from ItemPriceControl pc where pc.priceTierDetail.id = :tierId")
	List<ItemPriceControl> getItemControlItemsByTier(@Param("tierId") UUID tierId)
}
