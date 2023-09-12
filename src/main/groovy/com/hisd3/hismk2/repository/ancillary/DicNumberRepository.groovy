package com.hisd3.hismk2.repository.ancillary

import com.hisd3.hismk2.domain.ancillary.DicNumber
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DicNumberRepository extends JpaRepository<DicNumber, UUID> {
	
	@Query(value = '''Select d from DicNumber d where d.department.id= :department
			and d.patient.id= :patient ''')
	
	List<DicNumber> getAltenaneNumber(@Param("department") UUID department, @Param("patient") UUID patient)
	
}
