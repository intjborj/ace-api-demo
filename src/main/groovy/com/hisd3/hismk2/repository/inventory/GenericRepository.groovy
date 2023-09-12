package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.Generic
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface GenericRepository extends JpaRepository<Generic, UUID> {
	
	@Query(value = '''Select s from Generic s where s.isActive=true''')
	List<Generic> genericActive()
	
	@Query(value = '''Select s from Generic s where lower(s.genericDescription) like lower(concat('%',:filter,'%')) or lower(s.genericCode) like lower(concat('%',:filter,'%'))''')
	List<Generic> genericFilter(@Param("filter") String filter)
	
	@Query(value = '''Select s from Generic s where s.isActive=true and
					  lower(s.genericDescription) like lower(concat('%',:filter,'%'))
			''',
			countQuery = '''
    Select count(s) from Generic s where s.isActive=true and
					  lower(s.genericDescription) like lower(concat('%',:filter,'%'))
   ''')
	Page<Generic> genericActiveFilter(@Param("filter") String filter, Pageable pageable)

	@Query(value = '''Select s from Generic s where
					  lower(s.genericDescription) like lower(concat('%',:filter,'%'))
					  or lower(s.genericCode) like lower(concat('%',:filter,'%'))
			''',
			countQuery = '''
    Select count(s) from Generic s where lower(s.genericDescription) like lower(concat('%',:filter,'%'))
					  or lower(s.genericCode) like lower(concat('%',:filter,'%'))
   ''')
	Page<Generic> genericPageFilter(@Param("filter") String filter, Pageable pageable)
	
	//validation query
	@Query(value = "Select s from Generic s where upper(s.genericCode) = upper(:genericCode)")
	Generic findOneByGenericCode(@Param("genericCode") String genericCode)
	
	@Query(value = "Select s from Generic s where upper(s.genericDescription) = upper(:genericDescription)")
	Generic findOneByGenericName(@Param("genericDescription") String genericDescription)
	//end validation query
}
