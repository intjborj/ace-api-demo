package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.Inventory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface InventoryRepository extends JpaRepository<Inventory, UUID> {
	
	@Query(value = "Select inv from Inventory inv where inv.depId=:departmentid and inv.active = true and upper(inv.descLong) like  upper(concat('%',:filter,'%')) order by  inv.descLong")
	List<Inventory> inventoryByDepartmentAndFilter(@Param("departmentid") UUID departmentid, @Param("filter") String filter)
	
	@Query(value = "Select inv from Inventory inv where inv.depId=:departmentid and inv.item.isMedicine = false and inv.active = true and upper(inv.descLong) like  upper(concat('%',:filter,'%')) order by  inv.descLong")
	List<Inventory> inventoryByDepartmentAndFilterSupplies(@Param("departmentid") UUID departmentid, @Param("filter") String filter)
	
	@Query(value = "Select inv from Inventory inv where inv.depId=:departmentid and inv.item.isMedicine = false and inv.active = true and upper(inv.descLong) like  upper(concat('%',:filter,'%')) ",
			countQuery = "Select count (inv) from Inventory inv where inv.depId=:departmentid and inv.item.isMedicine = false and inv.active = true and upper(inv.descLong) like  upper(concat('%',:filter,'%'))")
	Page<Inventory> inventoryByDepartmentAndFilterSuppliesPageable(@Param("departmentid") UUID departmentid, @Param("filter") String filter,
	                                                               Pageable page)
	
	@Query(value = "Select inv from Inventory inv where inv.depId=:departmentid and inv.item.isMedicine = true and inv.active = true and upper(inv.descLong) like  upper(concat('%',:filter,'%')) order by  inv.descLong")
	List<Inventory> inventoryByDepartmentAndFilterMedicine(@Param("departmentid") UUID departmentid, @Param("filter") String filter)
	
	@Query(value = "Select inv from Inventory inv where inv.depId=:departmentid and inv.item.isMedicine = true and inv.active = true and upper(inv.descLong) like  upper(concat('%',:filter,'%'))",
			countQuery = "Select count(inv) from Inventory inv where inv.depId=:departmentid and inv.item.isMedicine = true and inv.active = true and upper(inv.descLong) like  upper(concat('%',:filter,'%'))")
	Page<Inventory> inventoryByDepartmentAndFilterMedicinePageable(@Param("departmentid") UUID departmentid, @Param("filter") String filter,
	                                                               Pageable page)
	
	@Query(value = "Select inv from Inventory inv where inv.depId=:departmentid and inv.active = true and upper(inv.descLong) like  upper(concat('%',:filter,'%')) and inv.item.production = true",
			countQuery = "Select count(inv) from Inventory inv where inv.depId=:departmentid and inv.active = true and upper(inv.descLong) like  upper(concat('%',:filter,'%')) and inv.item.production = true")
	Page<Inventory> inventoryByDepartmentAndFilterPageable(@Param("departmentid") UUID departmentid, @Param("filter") String filter, Pageable pageable)
	
	@Query(value = "Select inv from Inventory inv where upper(inv.descLong) like  upper(concat('%',:filter,'%')) and inv.active = true and  inv.depId = :department and inv.item.production = true order by  inv.descLong")
	Page<Inventory> inventoryFilterPageable(@Param("filter") String filter, @Param('department') UUID department, Pageable pageable)
	
	@Query(value = "Select inv from Inventory inv where inv.depId=:departmentid and inv.active = true and upper(inv.descLong) like  upper(concat('%',:filter,'%')) and (inv.item.production = false or inv.item.production is null ) order by  inv.descLong")
	Page<Inventory> inventoryByDepartmentAndFilterPageableNonProduction(@Param("departmentid") UUID departmentid, @Param("filter") String filter, Pageable pageable)
	
	@Query(value = '''Select inv from Inventory inv where
					  lower(inv.sku) = lower(:filter) or
					  lower(inv.itemCode) = lower(:filter) or
					  lower(inv.descLong) like lower(concat('%',:filter,'%'))
					  and inv.active = true
			''')
	List<Inventory> itemsByFilter(@Param("filter") String filter)
	
	@Query(value = "Select inv from Inventory inv where inv.depId=:departmentid and inv.active = true and upper(inv.descLong) like  upper(concat('%',:filter,'%')) and inv.item.item_group.id = :group order by  inv.descLong")
	List<Inventory> inventoryByDepartmentAndFilterAndGroup(@Param("departmentid") UUID departmentid, @Param("filter") String filter, @Param("group") UUID group)
	
	@Query(value = "Select inv from Inventory inv where inv.depId=:departmentid and inv.active = true and upper(inv.descLong) like  upper(concat('%',:filter,'%')) and inv.item.item_group.id = :group and inv.item.item_category.id IN (:category) order by  inv.descLong")
	List<Inventory> inventoryByDepartmentAndFilterAndGroupAndCat(@Param("departmentid") UUID departmentid, @Param("filter") String filter, @Param("group") UUID group, @Param("category") List<UUID> category)
	
	//pageable
	@Query(value = "Select inv from Inventory inv where inv.depId=:departmentid and inv.active = true and upper(inv.descLong) like  upper(concat('%',:filter,'%'))",
			countQuery = "Select count (inv) from Inventory inv where inv.depId=:departmentid and inv.active = true and upper(inv.descLong) like  upper(concat('%',:filter,'%'))")
	Page<Inventory> inventoryByDepartmentAndFilterPaged(@Param("departmentid") UUID departmentid, @Param("filter") String filter, Pageable page)
	
	@Query(value = "Select inv from Inventory inv where inv.depId=:departmentid and inv.active = true and upper(inv.descLong) like  upper(concat('%',:filter,'%')) and inv.item.item_group.id = :group",
			countQuery = "Select count (inv) from Inventory inv where inv.depId=:departmentid and inv.active = true and upper(inv.descLong) like  upper(concat('%',:filter,'%')) and inv.item.item_group.id = :group")
	Page<Inventory> inventoryByDepartmentAndFilterAndGroupPaged(@Param("departmentid") UUID departmentid, @Param("filter") String filter, @Param("group") UUID group, Pageable page)
	
	@Query(value = "Select inv from Inventory inv where inv.depId=:departmentid and inv.active = true and upper(inv.descLong) like  upper(concat('%',:filter,'%')) and inv.item.item_group.id = :group and inv.item.item_category.id IN (:category)",
			countQuery = "Select count (inv) from Inventory inv where inv.depId=:departmentid and inv.active = true and upper(inv.descLong) like  upper(concat('%',:filter,'%')) and inv.item.item_group.id = :group and inv.item.item_category.id IN (:category)")
	Page<Inventory> inventoryByDepartmentAndFilterAndGroupAndCatPaged(@Param("departmentid") UUID departmentid, @Param("filter") String filter, @Param("group") UUID group, @Param("category") List<UUID> category, Pageable page)
	
}
