package com.hisd3.hismk2.repository.dietary

import com.hisd3.hismk2.domain.dietary.DietCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param


interface DietCategoryRepository extends JpaRepository<DietCategory, UUID> {
	@Query(value = "Select c from DietCategory c")
	List<DietCategory>findAllDietCategory()

	@Query(value = "SELECT c FROM DietCategory c WHERE lower(c.dietCategoryCode) like lower(concat('%',:code,'%')) ")
	List<DietCategory>findAllLikeCategory(@Param("code") String code)
}
