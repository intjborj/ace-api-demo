package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.PaymentTerm
import com.hisd3.hismk2.domain.inventory.SupplierType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SupplierTypeRepository extends JpaRepository<SupplierType, UUID> {
	
	@Query(value = '''Select s from SupplierType s where s.isActive=true''')
	List<SupplierType> supplierTypeActive()

	@Query(value = '''Select s from SupplierType s where s.isActive=true and lower(s.supplierTypeDesc) like lower(concat('%',:filter,'%'))''')
	List<SupplierType> supplierTypeActiveFilter(@Param("filter") String filter)
	
	@Query(value = '''Select s from SupplierType s where lower(s.supplierTypeDesc) like lower(concat('%',:filter,'%'))''')
	List<SupplierType> supplierTypeFilter(@Param("filter") String filter)

	@Query(value = '''Select s from SupplierType s where
					  lower(s.supplierTypeDesc) like lower(concat('%',:filter,'%')) or  
					lower(s.supplierTypeCode) like lower(concat('%',:filter,'%'))
			''',
			countQuery = '''
    Select count(s) from SupplierType s where
					  lower(s.supplierTypeDesc) like lower(concat('%',:filter,'%')) or  
					lower(s.supplierTypeCode) like lower(concat('%',:filter,'%'))
   ''')
	Page<SupplierType> supplierTypeFilterPage(@Param("filter") String filter, Pageable pageable)
	
	//validation query
	@Query(value = "Select s from SupplierType s where upper(s.supplierTypeCode) = upper(:supplierTypeCode)")
	SupplierType findOneBySupplierTypeCode(@Param("supplierTypeCode") String supplierTypeCode)
	
	@Query(value = "Select s from SupplierType s where upper(s.supplierTypeDesc) = upper(:supplierTypeDesc)")
	SupplierType findOneBySupplierTypeName(@Param("supplierTypeDesc") String supplierTypeDesc)
	//end validation query
}
