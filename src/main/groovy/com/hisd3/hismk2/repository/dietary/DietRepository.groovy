package com.hisd3.hismk2.repository.dietary

import com.hisd3.hismk2.domain.dietary.Diet
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DietRepository extends JpaRepository<Diet, UUID> {
	@Query(value = "SELECT d from Diet d where lower(d.dietName) like concat('%',:filter,'%') or lower(d.dietDescription) like concat('%',:filter,'%')",
			countQuery = "SELECT count(d) from Diet d where lower(d.dietName) like concat('%',:filter,'%') or lower(d.dietDescription) like concat('%',:filter,'%')"
	)
	Page<Diet> findAllByFilter(@Param("filter") String filter, Pageable pageable)
	
	@Query(value = "Select diet from Diet diet where lower(diet.dietName) like lower(concat('%',:filter,'%')) or lower(diet.dietDescription) like lower(concat('%',:filter,'%'))")
	List<Diet> filterAllDiets(@Param("filter") String filter)
}
