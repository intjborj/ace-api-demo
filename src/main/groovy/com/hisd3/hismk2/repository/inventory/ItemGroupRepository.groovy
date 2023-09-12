package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.ItemGroup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ItemGroupRepository extends JpaRepository<ItemGroup, UUID> {
	
	@Query(value = '''Select s from ItemGroup s where s.isActive=true''')
	List<ItemGroup> itemGroupActive()
	
	@Query(value = '''Select s from ItemGroup s where lower(s.itemDescription) like lower(concat('%',:filter,'%'))
	or lower(s.itemCode) like lower(concat('%',:filter,'%'))''')
	List<ItemGroup> itemGroupFilter(@Param("filter") String filter)
	
	//validation query
	@Query(value = "Select s from ItemGroup s where upper(s.itemCode) = upper(:itemCode)")
	ItemGroup findOneByItemGroupCode(@Param("itemCode") String itemCode)
	
	@Query(value = "Select s from ItemGroup s where upper(s.itemDescription) = upper(:itemDescription)")
	ItemGroup findOneByItemGroupName(@Param("itemDescription") String itemDescription)
	//end validation query
}
