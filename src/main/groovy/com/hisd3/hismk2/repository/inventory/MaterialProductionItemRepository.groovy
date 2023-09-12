package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.MaterialProductionItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MaterialProductionItemRepository extends JpaRepository<MaterialProductionItem, UUID> {
	@Query(value = "select m from MaterialProductionItem m where m.materialProduction.id = :id",
			countQuery = "Select count(m) from MaterialProductionItem m where m.materialProduction.id = :id")
	Page<MaterialProductionItem> getByMaterialProduction(@Param("id") UUID id, Pageable pageable)
	
	@Query(value = "select m from MaterialProductionItem m where m.materialProduction.id = :id")
	List<MaterialProductionItem> getAllMaterialProductionItems(@Param("id") UUID id)

	@Query(value = "select m from MaterialProductionItem m where m.materialProduction.id = :id and m.isPosted = true")
	List<MaterialProductionItem> getAllMaterialProductionItemsPosted(@Param("id") UUID id)

	@Query(value = "select m from MaterialProductionItem m where m.materialProduction.id = :id and m.type = :type")
	List<MaterialProductionItem> getAllMaterialProductionItemsByType(@Param("id") UUID id,@Param("type") String type)
}
