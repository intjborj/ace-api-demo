package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.DepartmentItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DepartmentItemRepository extends JpaRepository<DepartmentItem, UUID> {
	
	@Query(value = '''Select s from DepartmentItem s where s.item.id=:item AND s.department.id=:dep''')
	DepartmentItem findByItemDep(@Param("item") UUID item, @Param("dep") UUID dep)

	@Query(value = '''Select s from DepartmentItem s where s.item.id=:item''')
	List<DepartmentItem> findListByItem(@Param("item") UUID item)

	@Query(value = '''Select s from DepartmentItem s where s.item.sku = :sku and s.department.id = :id and s.is_assign = true''')
	DepartmentItem findListByItemSKU(@Param("sku") String sku, @Param("id") UUID id)
	
}
