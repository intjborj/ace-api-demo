package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.Markup
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MarkupRepository extends JpaRepository<Markup, UUID> {
	
	@Query(value = '''Select m from Markup m where
					  (lower(m.descLong) like lower(concat('%',:filter,'%')) or
					  lower(m.sku) like lower(concat('%',:filter,'%'))) AND
					  m.active = true
					  order by m.descLong ASC
			''')
	List<Markup> activeMarkup(@Param("filter") String filter)
	
	@Query(value = '''select m from Markup m where m.active = true and lower(m.descLong) like lower(concat('%', :filter, '%'))''',
			countQuery = '''select count(m) from Markup m where m.active = true and lower(m.descLong) like lower(concat('%', :filter, '%'))'''
	)
	Page<Markup> activeMarkupPageable(@Param("filter") String filter, Pageable pageable)
	
	@Query(value = "Select m from Markup m where m.active = true AND (lower(m.descLong) like lower(concat('%',:filter,'%')) or lower(m.sku) like lower(concat('%',:filter,'%'))) AND m.item_group.id = :group ")
	List<Markup> activeMarkupAndGroup(@Param("filter") String filter, @Param("group") UUID group)
	
	@Query(value = '''Select m from Markup m where
					  m.active = true AND
					  (lower(m.descLong) like lower(concat('%',:filter,'%')) or
					  lower(m.sku) like lower(concat('%',:filter,'%')))
					  and m.item_group.id = :group
					  and m.item_category.id IN (:category)
			''')
	List<Markup> activeMarkupAndCat(@Param("filter") String filter, @Param("group") UUID group, @Param("category") List<UUID> category)
}
