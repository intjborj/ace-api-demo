package com.hisd3.hismk2.repository.referential

import com.hisd3.hismk2.domain.referential.DohSurgeryCode
import groovy.transform.TypeChecked
import org.apache.lucene.search.Sort
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

@TypeChecked
interface DohSurgeryCodeRepository extends JpaRepository<DohSurgeryCode, UUID> {
	@Query(value = '''SELECT c FROM DohSurgeryCode c WHERE lower(c.procdesc) like lower(concat('%',:filter,'%')) or lower(c.proccode) like lower(concat('%',:filter,'%'))''')
	List<DohSurgeryCode> searchDOHSurgeryCodes(@Param("filter") String filter)

	@Query(value = '''SELECT c FROM DohSurgeryCode c WHERE lower(c.procdesc) like lower(concat('%',:filter,'%')) or lower(c.proccode) like lower(concat('%',:filter,'%'))''',
			countQuery  = '''SELECT count(c) FROM DohSurgeryCode c WHERE lower(c.procdesc) like lower(concat('%',:filter,'%')) or lower(c.proccode) like lower(concat('%',:filter,'%'))''')
	Page<DohSurgeryCode> searchDOHSurgeryCodesPageable(@Param("filter") String filter, Pageable pageable)
}
