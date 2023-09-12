package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.ReturnSupplier
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ReturnSupplierRepository extends JpaRepository<ReturnSupplier, UUID> {
	
	@Query(value = '''Select r from ReturnSupplier r where r.department.id = :id  and lower(r.rtsNo) like lower(concat('%',:filter,'%'))''')
	List<ReturnSupplier> findReturnSupplierByDep(@Param("id") UUID id, @Param("filter") String filter)

	@Query('''select p from ReturnSupplier p where 
			p.department.id = :id and
			(p.rtsNo like concat('%',:filter,'%') or 
			lower(p.refSrr) like lower(concat('%',:filter,'%')) or 
			lower(p.receivedRefNo) like lower(concat('%',:filter,'%')) or 
			lower(p.supplier.supplierFullname) like lower(concat('%',:filter,'%'))) and
			to_date(to_char(p.returnDate, 'YYYY-MM-DD'),'YYYY-MM-DD')
             	between to_date(:startDate,'YYYY-MM-DD') and  to_date(:endDate,'YYYY-MM-DD')''')
	Page<ReturnSupplier> returnSupplierByDepPage(@Param('filter') String filter,
															@Param('id') UUID id,
															@Param('startDate') String startDate,
															@Param('endDate') String endDate,
															Pageable pageable)
}
