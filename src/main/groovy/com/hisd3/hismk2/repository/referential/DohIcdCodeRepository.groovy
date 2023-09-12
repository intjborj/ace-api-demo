package com.hisd3.hismk2.repository.referential

import com.hisd3.hismk2.domain.referential.DohIcdCode
import groovy.transform.TypeChecked
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

@TypeChecked
interface DohIcdCodeRepository extends JpaRepository<DohIcdCode, UUID> {
	@Query(value = "SELECT c FROM DohIcdCode c WHERE lower(c.icdCode) like lower(concat('%',:filter,'%')) or lower(c.icdDesc) like lower(concat('%',:filter,'%'))")
	List<DohIcdCode> searchDOHICDCodes(@Param("filter") String filter)
	
	@Query(value = '''SELECT c FROM DohIcdCode c WHERE  lower(c.icdCode) like lower(concat('%',:filter,'%')) or lower(c.icdDesc) like lower(concat('%',:filter,'%'))''',
			countQuery = '''SELECT count(c) FROM DohIcdCode c WHERE  lower(c.icdCode) like lower(concat('%',:filter,'%')) or lower(c.icdDesc) like lower(concat('%',:filter,'%'))''')
	Page<DohIcdCode> searchDOHICDCodesPageable(@Param("filter") String filter, Pageable pageable)
}
