package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.MaterialProduction
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MaterialProductionRepository extends JpaRepository<MaterialProduction, UUID> {
	@Query(value = "select m from MaterialProduction m where m.item.id = :itemId",
			countQuery = "Select count(m) from MaterialProduction m where m.item.id = :itemId")
	Page<MaterialProduction> getAllMaterialProduction(@Param("itemId") UUID itemId, Pageable pageable)

	@Query(value = "select m from MaterialProduction m where m.department.id = :deptID",
			countQuery = "Select count(m) from MaterialProduction m where m.department.id = :deptID")
	Page<MaterialProduction> getAllMaterialProductionByDept(@Param("deptID") UUID deptID, Pageable pageable)

	@Query(value = "select m from MaterialProduction m where m.department.id = :deptID and (m.mpNo like concat('%',:filter,'%') or m.description like concat('%',:filter,'%'))",
			countQuery = "Select count(m) from MaterialProduction m where m.department.id = :deptID and (m.mpNo like concat('%',:filter,'%') or m.description like concat('%',:filter,'%'))")
	Page<MaterialProduction> filterableMaterialProductionByDept(@Param("deptID") UUID deptID,@Param("filter") String filter, Pageable pageable)

	@Query(value = '''select m from MaterialProduction m where 
			m.department.id = :id 
			and (m.mpNo like concat('%',:filter,'%') or m.description like concat('%',:filter,'%'))
			and to_date(to_char(m.dateTransaction, 'YYYY-MM-DD'),'YYYY-MM-DD')
             	between to_date(:startDate,'YYYY-MM-DD') and  to_date(:endDate,'YYYY-MM-DD')''',
			countQuery = '''Select count(m) from MaterialProduction m where 
			m.department.id = :id 
			and (m.mpNo like concat('%',:filter,'%') or m.description like concat('%',:filter,'%'))
			and to_date(to_char(m.dateTransaction, 'YYYY-MM-DD'),'YYYY-MM-DD')
             	between to_date(:startDate,'YYYY-MM-DD') and  to_date(:endDate,'YYYY-MM-DD')''')
	Page<MaterialProduction> filterableMaterialProductionByDeptPage(@Param('filter') String filter,
																@Param('id') UUID id,
																@Param('startDate') String startDate,
																@Param('endDate') String endDate,
																Pageable pageable)
}
