package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.inventory.UnitMeasurement
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UnitMeasurementRepository extends JpaRepository<UnitMeasurement, UUID> {
	
	@Query(value = '''Select s from UnitMeasurement s where s.isActive=true''')
	List<UnitMeasurement> unitMeasurementActive()
	
	@Query(value = '''Select s from UnitMeasurement s where lower(s.unitDescription) like lower(concat('%',:filter,'%'))''')
	List<UnitMeasurement> unitMeasurementFilter(@Param("filter") String filter)
	
	@Query(value = '''Select s from UnitMeasurement s where s.isActive=true AND s.isBig=true''')
	List<UnitMeasurement> unitOfPurchase()
	
	@Query(value = '''Select s from UnitMeasurement s where s.isActive=true AND s.isSmall=true''')
	List<UnitMeasurement> unitOfUsage()
	
	//validation query
	@Query(value = "Select s from UnitMeasurement s where upper(s.unitCode) = upper(:unitCode)")
	UnitMeasurement findOneByUnitMeasurementCode(@Param("unitCode") String unitCode)
	
	@Query(value = "Select s from UnitMeasurement s where upper(s.unitDescription) = upper(:unitDescription)")
	UnitMeasurement findOneByUnitMeasurementName(@Param("unitDescription") String unitDescription)
	//end validation query

	//page
	@Query(value = '''Select s from UnitMeasurement s 
where lower(s.unitDescription) like lower(concat('%',:filter,'%')) or 
lower(s.unitCode) like lower(concat('%',:filter,'%'))
			''',
			countQuery = '''Select count(s) from UnitMeasurement s 
where lower(s.unitDescription) like lower(concat('%',:filter,'%')) or 
lower(s.unitCode) like lower(concat('%',:filter,'%'))
   ''')
	Page<UnitMeasurement> measureFilterPage(@Param("filter") String filter, Pageable pageable)
}
