package com.hisd3.hismk2.repository.philhealth

import com.hisd3.hismk2.domain.philhealth.RVSCode
import groovy.transform.TypeChecked
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

@TypeChecked
interface RVSCodesRepository extends JpaRepository<RVSCode, UUID> {
	
	@Query(value = "Select r from RVSCode r order by rvsCode",
			countQuery = "Select count(r) from RVSCode r order by rvsCode"
	)
	Set<RVSCode> getRVSCodes()
	
	@Query(value = '''Select r from RVSCode r where
            lower(r.rvsCode) like concat('%',lower(:filter),'%') or
            lower(r.longName) like concat('%',lower(:filter),'%')''')
	Set<RVSCode> searchRVSCodes(@Param("filter") String filter)
	
	@Query(value = '''Select r from RVSCode r where
            lower(r.rvsCode) like concat('%',lower(:filter),'%')
            and CURRENT_DATE BETWEEN TO_DATE(r.effDate, 'MM/DD/YYYY') and TO_DATE(r.effEndDate, 'MM/DD/YYYY')
            
             ''')
	Set<RVSCode> searchRVSCodesOnly(@Param("filter") String filter)
	
	@Query(value = '''Select distinct r from RVSCode r where
            lower(r.rvsCode) like concat('%',lower(:filter),'%') or
            lower(r.longName) like concat('%',lower(:filter),'%')''',
			countQuery = '''Select count(distinct r) from RVSCode r where
            lower(r.rvsCode) like concat('%',lower(:filter),'%') or
            lower(r.longName) like concat('%',lower(:filter),'%')'''
	)
	Page<RVSCode> searchRVSCodesPageable(@Param("filter") String filter, Pageable pageable)
}
