package com.hisd3.hismk2.repository.referential

import com.hisd3.hismk2.domain.referential.DohServiceType
import groovy.transform.TypeChecked
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

@TypeChecked
interface DohServiceTypeRepository extends JpaRepository<DohServiceType, UUID> {
	
	@Query(value = "SELECT s FROM DohServiceType s")
	List<DohServiceType> getDOHServiceTypes()
	
	@Query(value = "SELECT s FROM DohServiceType s WHERE upper(s.tsdesc) = upper(:tsdesc)")
	DohServiceType getDOHServiceTypesByDesc(@Param("tsdesc") String tsdesc)
	
}
