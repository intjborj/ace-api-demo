package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.ItemCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ItemCategoryRepository extends JpaRepository<ItemCategory, UUID> {
	
	@Query(value = '''Select s from ItemCategory s where s.isActive=true''')
	List<ItemCategory> itemCategoryActive()
	
	@Query(value = '''Select s from ItemCategory s where lower(s.categoryDescription) like lower(concat('%',:filter,'%'))''')
	List<ItemCategory> itemCategoryFilter(@Param("filter") String filter)
	
	@Query(value = '''Select s from ItemCategory s where s.isActive=true AND s.itemGroup.id = :id''')
	List<ItemCategory> findAllByIdGroup(@Param("id") UUID id)
	
	//validation query
	@Query(value = "Select s from ItemCategory s where upper(s.categoryCode) = upper(:categoryCode)")
	ItemCategory findOneByItemCategoryCode(@Param("categoryCode") String categoryCode)
	
	@Query(value = "Select s from ItemCategory s where upper(s.categoryDescription) = upper(:categoryDescription)")
	ItemCategory findOneByItemCategoryName(@Param("categoryDescription") String categoryDescription)
	//end validation query
	
}
