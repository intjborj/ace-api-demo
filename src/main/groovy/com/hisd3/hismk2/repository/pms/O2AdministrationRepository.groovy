package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.O2Administration
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface O2AdministrationRepository extends JpaRepository<O2Administration, UUID> {
	
	@Query(value = '''Select s from O2Administration s where s.patientCase.id=:caseid''')
	List<O2Administration> findByCase(@Param("caseid") UUID caseid)
}
