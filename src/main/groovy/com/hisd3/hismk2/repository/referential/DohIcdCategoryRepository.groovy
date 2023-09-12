package com.hisd3.hismk2.repository.referential

import com.hisd3.hismk2.domain.referential.DohIcdCategory
import groovy.transform.TypeChecked
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

@TypeChecked
interface DohIcdCategoryRepository extends JpaRepository<DohIcdCategory, UUID> {
	@Query(value = "SELECT c FROM DohIcdCategory c WHERE lower(c.icdCategoryCode) like lower(concat('%',:filter,'%'))")
	List<DohIcdCategory> allDohIcdCodeCategory(@Param("filter") String filter)
	
	@Query(
			value = "SELECT c FROM DohIcdCategory c WHERE lower(c.icdCategoryCode) like lower(concat('%',:filter,'%'))",
			countQuery = "SELECT count(c) FROM DohIcdCategory c WHERE lower(c.icdCategoryCode) like lower(concat('%',:filter,'%'))"
	)
	Page<DohIcdCategory> allDohIcdCodeCategoryPageable(@Param("filter") String filter, Pageable pageable)
}
