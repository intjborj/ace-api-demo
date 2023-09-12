package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.SupplierItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SupplierItemRepository extends JpaRepository<SupplierItem, UUID> {
	@Query(
			value = '''Select items from SupplierItem items where items.supplier.id =:id and
            (lower(items.item.descLong) like concat('%',:filter,'%') or 
            lower(items.item.genericName) like concat('%',:filter,'%'))'''
	)
	List<SupplierItem> findBySupplier(@Param("id") UUID id, @Param("filter") String filter)
	
	@Query(
			value = '''Select items from SupplierItem items where items.supplier.id =:id and
            (lower(items.item.descLong) like concat('%',:filter,'%') or 
            lower(items.item.genericName) like concat('%',:filter,'%') or 
            lower(items.item.sku) like concat('%',:filter,'%'))'''
	)
	Page<SupplierItem> findBySupplierPageable(@Param("id") UUID id, @Param("filter") String filter, Pageable pageable)
	
	@Query(value = "select s from SupplierItem s where s.item.id = :item_id")
	List<SupplierItem> findSupplierByItem(@Param("item_id") UUID itemId)

	@Query(value = "select s from SupplierItem s where s.supplier.id = :supplier and s.item.sku = :barcode")
	List<SupplierItem> getSupItemByBarcode(@Param("supplier") UUID supplier, @Param("barcode") String barcode)
	
	@Query(value = "select s from SupplierItem s where s.item.id = :item_id")
	List<SupplierItem> findByItem(@Param("item_id") UUID item_id)
	
	@Query(value = "select s from SupplierItem s where s.supplier.id = :supplier_id")
	List<SupplierItem> findBySupplierId(@Param("supplier_id") UUID supplierId)

	@Query(value = "select s from SupplierItem s where s.supplier.id = :supplier_id and s.item.id = :item_id")
	SupplierItem findIfExist(@Param("supplier_id") UUID supplierId, @Param("item_id") UUID item_id)
}
