package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.Item
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ItemRepository extends JpaRepository<Item, UUID> {
	
	@Query(value = "Select item from Item item where item.isMedicine = true and item.active = true")
	List<Item> findAllMedicines()
	
	@Query(value = "Select item from Item item where lower(item.descLong) like lower(concat('%',:filter,'%')) and (item.isMedicine = true and item.active = true)")
	List<Item> filterAllMedicines(@Param("filter") String filter)
	
	@Query(value = "Select item from Item item where item.isMedicine = false and item.active = true")
	List<Item> findAllSupplies()
	
	@Query(value = "Select item from Item item where lower(item.descLong) like lower(concat('%',:filter,'%')) and (item.isMedicine = true and item.active = true)")
	List<Item> findAllMedicines(@Param("filter") String filter)
	
	@Query(value = "Select item from Item item where lower(item.descLong) like lower(concat('%',:filter,'%')) and (item.isMedicine = false and item.active = true)")
	List<Item> findAllSupplies(@Param("filter") String filter)
	
	@Query(value = "Select item from Item item where item.isMedicine = true and (item.fluid = false or item.fluid is null) and item.active = true")
	List<Item> findAllNonFluidMedicines()
	
	@Query(value = "Select item from Item item where lower(item.descLong) like lower(concat('%',:filter,'%')) and (item.isMedicine = true and (item.fluid = false or item.fluid is null) and item.active = true)")
	List<Item> filterAllNonFluidMedicines(@Param("filter") String filter)
	
	@Query(value = "Select item from Item item where item.isMedicine = true and item.fluid = true and item.active = true")
	List<Item> findAllFluidMedicines()
	
	@Query(value = "Select item from Item item where lower(item.descLong) like lower(concat('%',:filter,'%')) and (item.isMedicine = true and item.fluid = true and item.active = true)")
	List<Item> filterAllFluidMedicines(@Param("filter") String filter)
	
	@Query(value = "Select item from Item item where lower(item.descLong) like lower(concat('%',:filter,'%')) and (item.isMedicine = false and item.gas = true and item.active = true)")
	List<Item> filterAllGasItems(@Param("filter") String filter)
	
	@Query(value = "Select item from Item item where item.isMedicine = false and item.gas = true and item.active = true")
	List<Item> findAllGasItems()
	
	@Query(value = '''Select item from Item item where
					  lower(item.descLong) like lower(concat('%',:filter,'%')) or
					  lower(item.sku) like lower(concat('%',:filter,'%'))
			''')
	List<Item> itemsByFilter(@Param("filter") String filter)
	
	@Query(value = '''Select item from Item item where
					  item.active = true AND
					  (lower(item.descLong) like lower(concat('%',:filter,'%')) or
					  lower(item.sku) like lower(concat('%',:filter,'%')))
			''')
	List<Item> activeItems(@Param("filter") String filter)
	
	@Query(value = '''Select item from Item  item  where
					  lower(item.descLong) like lower(concat('%',:filter,'%')) or
					  lower(item.sku) like lower(concat('%',:filter,'%'))
			''')
	List<Item> itemsByFilterLimited(@Param("filter") String filter)
	
	@Query(value = '''Select item from Item item where
					  lower(item.descLong) like lower(concat('%',:filter,'%')) or
					  lower(item.sku) like lower(concat('%',:filter,'%'))
			''')
	Page<Item> itemsByFilterPageable(@Param("filter") String filter, Pageable pageable)
	
	@Query(value = "Select item from Item item where item.item_group.id = :itemGroup")
	Set<Item> findByItemGroup(@Param("itemGroup") UUID itemGroup)
	
	@Query(value = "Select item from Item item where item.item_category.id = :itemCategory")
	Set<Item> findByItemCategory(@Param("itemCategory") UUID itemCategory)
	
	@Query(value = "Select item from Item item where item.item_generics.id = :itemGeneric")
	Set<Item> findByItemGeneric(@Param("itemGeneric") UUID itemGeneric)
	
	@Query(value = "Select item from Item item where item.unit_of_purchase.id = :unitMeasurement")
	Set<Item> findByUnitOfPurchase(@Param("unitMeasurement") UUID unitMeasurement)
	
	@Query(value = "Select item from Item item where item.unit_of_usage.id = :unitMeasurement")
	Set<Item> findByUnitOfUsage(@Param("unitMeasurement") UUID unitMeasurement)
	
	@Query(value = "Select item from Item item where item.production = true")
	Set<Item> findAllProductionItem()
	
	@Query(value = '''Select item from Item item where
					  (lower(item.descLong) like lower(concat('%',:filter,'%')) or
					  lower(item.sku) like lower(concat('%',:filter,'%'))) and (item.production = false or item.production is null) order by item.descLong asc
			''')
	Page<Item> itemsByFilterAndIsNotProductionPageable(@Param("filter") String filter, Pageable pageable)
	
	@Query(value = '''Select item from Item item where item.item_group.id = :group ''')
	List<Item> itemsFilterByGroup(@Param("group") UUID group)
	
	@Query(value = '''Select item from Item item where
					  item.item_group.id = :group
					  and item.item_category.id IN (:category)
			''')
	List<Item> itemsFilterByCategory(@Param("group") UUID group, @Param("category") List<UUID> category)
	
	//pagealbe
	@Query(value = '''Select item from Item item where
					  (lower(item.descLong) like lower(concat('%',:filter,'%')) or
					  lower(item.sku) like lower(concat('%',:filter,'%')))
					  and  item.item_group.id = :group
			''',
			countQuery = '''
    Select count(item) from Item item where
					  (lower(item.descLong) like lower(concat('%',:filter,'%')) or
					  lower(item.sku) like lower(concat('%',:filter,'%')))
					  and  item.item_group.id = :group
   ''')
	Page<Item> itemsByFilterPagedGroup(@Param("filter") String filter, @Param("group") UUID group, Pageable pageable)
	//------------//
	@Query(value = '''Select item from Item item where
					  (lower(item.descLong) like lower(concat('%',:filter,'%')) or
					  lower(item.sku) like lower(concat('%',:filter,'%')))
					  and  item.item_group.id = :group
					  and item.item_category.id IN (:category)
			''',
			countQuery = '''
    Select count(item) from Item item where
					  (lower(item.descLong) like lower(concat('%',:filter,'%')) or
					  lower(item.sku) like lower(concat('%',:filter,'%')))
					  and  item.item_group.id = :group
					  and item.item_category.id IN (:category)
   ''')
	Page<Item> itemsByFilterPagedCategory(@Param("filter") String filter, @Param("group") UUID group, @Param("category") List<UUID> category, Pageable pageable)
	//--------//
	@Query(value = '''Select item from Item item where
					  (lower(item.descLong) like lower(concat('%',:filter,'%')) or
					  lower(item.sku) like lower(concat('%',:filter,'%')))
			''',
			countQuery = '''
    Select count(item) from Item item where
					  (lower(item.descLong) like lower(concat('%',:filter,'%')) or
					  lower(item.sku) like lower(concat('%',:filter,'%')))
   ''')
	Page<Item> itemsByFilterPaged(@Param("filter") String filter, Pageable pageable)
	
	@Query(value = '''Select item from Item item where
					  (lower(item.descLong) like lower(concat('%',:filter,'%')) or
					  lower(item.sku) like lower(concat('%',:filter,'%'))) and item.isMedicine = true
			''',
			countQuery = '''
    Select count(item) from Item item where
					  (lower(item.descLong) like lower(concat('%',:filter,'%')) or
					  lower(item.sku) like lower(concat('%',:filter,'%'))) and item.isMedicine = true
   ''')
	Page<Item> itemsByFilterPagedMedicines(@Param("filter") String filter, Pageable pageable)
	
	@Query(value = '''Select item from Item item where
					  (lower(item.descLong) like lower(concat('%',:filter,'%')) or
					  lower(item.sku) like lower(concat('%',:filter,'%'))) and item.isMedicine = false
			''',
			countQuery = '''
    Select count(item) from Item item where
					  (lower(item.descLong) like lower(concat('%',:filter,'%')) or
					  lower(item.sku) like lower(concat('%',:filter,'%'))) and item.isMedicine = false
   ''')
	Page<Item> itemsByFilterPagedNonMedicines(@Param("filter") String filter, Pageable pageable)


	@Query(value = '''Select item from Item item where
					  (lower(item.descLong) like lower(concat('%',:filter,'%')) or
					  lower(item.sku) like lower(concat('%',:filter,'%'))) and item.isMedicine = false
					   and (item.item_category.categoryDescription = 'MEDICAL SUPPLY')
			''',
			countQuery = '''
    Select count(item) from Item item where
					  (lower(item.descLong) like lower(concat('%',:filter,'%')) or
					  lower(item.sku) like lower(concat('%',:filter,'%'))) and item.isMedicine = false 
					  and (item.item_category.categoryDescription = 'MEDICAL SUPPLY')
   ''')
	Page<Item> itemsByFilterPagedMedicalSupplies(@Param("filter") String filter, Pageable pageable)
	
	//-------- BY COST RANGE QUERIES ------------- //
	//-------- BY COST RANGE QUERIES ------------- //
	
	@Query(value = "Select item from Item item where (item.isMedicine = true) and (item.actualUnitCost BETWEEN :from AND :to)")
	List<Item> findAllMedicinesByCostRange(@Param("from") BigDecimal from, @Param("to") BigDecimal to)
	
	@Query(value = "Select item from Item item where (item.isMedicine = false) and (item.actualUnitCost BETWEEN :from AND :to)")
	List<Item> findAllSuppliesByCostRange(@Param("from") BigDecimal from, @Param("to") BigDecimal to)
	
	@Query(value = '''Select item from Item item where
					  (lower(item.descLong) like lower(concat('%',:filter,'%'))
					  or lower(item.sku) like lower(concat('%',:filter,'%')))
					  and (item.actualUnitCost BETWEEN :from AND :to)
			''')
	List<Item> itemsByFilterByCostRange(@Param("filter") String filter, @Param("from") BigDecimal from, @Param("to") BigDecimal to)
	
	@Query(value = '''Select item from Item item where
					  (lower(item.descLong) like lower(concat('%',:filter,'%')) or lower(item.sku) like lower(concat('%',:filter,'%')))
					  and (item.actualUnitCost BETWEEN :from AND :to)''',
			countQuery = '''Select count(item) from Item item where
					  (lower(item.descLong) like lower(concat('%',:filter,'%')) or lower(item.sku) like lower(concat('%',:filter,'%')))
					  and (item.actualUnitCost BETWEEN :from AND :to)''')
	Page<Item> itemsByFilterAndCostRangePaged(@Param("filter") String filter, Pageable pageable, @Param("from") BigDecimal from, @Param("to") BigDecimal to)
	
	@Query(value = '''Select item from Item item where
					  (lower(item.descLong) like lower(concat('%',:filter,'%'))
					  or lower(item.sku) like lower(concat('%',:filter,'%')))
					  and (item.isMedicine = true) and (item.actualUnitCost BETWEEN :from AND :to)''',
			countQuery = '''Select count(item) from Item item where
					  (lower(item.descLong) like lower(concat('%',:filter,'%'))
					  or lower(item.sku) like lower(concat('%',:filter,'%')))
					  and (item.isMedicine = true) and (item.actualUnitCost BETWEEN :from AND :to)''')
	Page<Item> itemsByFilterAndCostRangePagedMedicines(@Param("filter") String filter, Pageable pageable, @Param("from") BigDecimal from, @Param("to") BigDecimal to)
	
	@Query(value = '''Select item from Item item where
					  (lower(item.descLong) like lower(concat('%',:filter,'%'))
					  or lower(item.sku) like lower(concat('%',:filter,'%')))
					  and (item.isMedicine = false)
					  and (item.actualUnitCost BETWEEN :from AND :to)''',
			countQuery = '''Select count(item) from Item item where
					  (lower(item.descLong) like lower(concat('%',:filter,'%'))
					  or lower(item.sku) like lower(concat('%',:filter,'%')))
					  and (item.isMedicine = false)
					  and (item.actualUnitCost BETWEEN :from AND :to)''')
	Page<Item> itemsByFilterAndCostRangePagedNonMedicines(@Param("filter") String filter, Pageable pageable, @Param("from") BigDecimal from, @Param("to") BigDecimal to)


	@Query(value = '''Select item from Item item where
					  (lower(item.descLong) like lower(concat('%',:filter,'%'))
					  or lower(item.sku) like lower(concat('%',:filter,'%')))
					  and (item.isMedicine = false)
					  and (item.item_category.categoryDescription = 'MEDICAL SUPPLY')
					  and (item.actualUnitCost BETWEEN :from AND :to)''',
			countQuery = '''Select count(item) from Item item where
						  (lower(item.descLong) like lower(concat('%',:filter,'%'))
						  or lower(item.sku) like lower(concat('%',:filter,'%')))
						  and (item.isMedicine = false)
						 and (item.item_category.categoryDescription = 'MEDICAL SUPPLY')
						  and (item.actualUnitCost BETWEEN :from AND :to)''')
	Page<Item> itemsByFilterAndCostRangePagedMedicalSupplies(@Param("filter") String filter, Pageable pageable, @Param("from") BigDecimal from, @Param("to") BigDecimal to)
	//-------- END. BY COST RANGE QUERIES ------------- //
	//-------- END. BY COST RANGE QUERIES ------------- //

	@Query(
			value = '''
					Select item from Item item where
					( 
						lower(item.descLong) like lower(concat('%',:search,'%')) or
						lower(item.sku) like lower(concat('%',:search,'%')) or 
						lower(item.brand) like lower(concat('%',:search,'%'))
					)
				  	and item.item_category.id IN (:category)
					and item.fixAsset is true
			''',
			countQuery = '''
					Select count(item) from Item item where
					( 
						lower(item.descLong) like lower(concat('%',:search,'%')) or
						lower(item.sku) like lower(concat('%',:search,'%')) or 
						lower(item.brand) like lower(concat('%',:search,'%'))
					)
				  	and item.item_category.id IN (:category)
					and item.fixAsset is true
   ''')
	Page<Item> fixedAssetsItemsWithCategoryPageable(@Param("search") String search, @Param("category") List<UUID> category, Pageable pageable)

	@Query(
			value = '''
					Select item from Item item where
					( 
						lower(item.descLong) like lower(concat('%',:search,'%')) or
						lower(item.sku) like lower(concat('%',:search,'%')) or 
						lower(item.brand) like lower(concat('%',:search,'%'))
					)
					and item.fixAsset is true
			''',
			countQuery = '''
					Select count(item) from Item item where
					( 
						lower(item.descLong) like lower(concat('%',:search,'%')) or
						lower(item.sku) like lower(concat('%',:search,'%')) or 
						lower(item.brand) like lower(concat('%',:search,'%'))
					)
					and item.fixAsset is true
   ''')
	Page<Item> fixedAssetsItemsPageable(@Param("search") String search, Pageable pageable)
}
