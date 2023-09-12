package com.hisd3.hismk2.repository.referential

import com.hisd3.hismk2.domain.referential.QualityManagementType
import groovy.transform.TypeChecked
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

@TypeChecked
interface QualityManagementTypeRepository extends JpaRepository<QualityManagementType, UUID> {
	
	@Query(value = "Select c from QualityManagementType c where c.title = :title")
	List<QualityManagementType> getQualityManagementType(@Param("title") String title)
	
	@Query(value = " SELECT c FROM QualityManagementType c WHERE c.title IS NOT NULL")
	List<QualityManagementType> getQualityManagement()
	
	@Query(value = "SELECT c FROM QualityManagementType c WHERE c.description = : description ")
	List<QualityManagementType> getQualityManagementDesc(@Param("description") String desc)
	
	@Query(value = "Select c from QualityManagementType c where lower(c.title) like lower(concat('%',:title,'%'))")
	List<QualityManagementType> getQualityManagementTypes(@Param("title") String title)
	
}
