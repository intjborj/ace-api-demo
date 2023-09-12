package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.Inventory
import com.hisd3.hismk2.domain.inventory.Supplier
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SupplierRepository extends JpaRepository<Supplier, UUID> {
	
	@Query(value = '''Select s from Supplier s where
            lower(s.supplierFullname) like lower (concat('%',:filter,'%')) or 
            lower(s.supplierCode) like lower (concat('%',:filter,'%'))''')
	List<Supplier> findAllByFilter(@Param("filter") String filter)



	@Query(value = '''Select s from Supplier s where
            lower(s.supplierFullname) like lower (concat('%',:filter,'%')) or 
            lower(s.supplierCode) like lower (concat('%',:filter,'%'))''',
			countQuery = '''Select count(s) from Supplier s where
            lower(s.supplierFullname) like lower (concat('%',:filter,'%')) or 
            lower(s.supplierCode) like lower (concat('%',:filter,'%'))''')
	Page<Supplier> findAllByFilterPageable(@Param("filter") String filter, Pageable page)

	@Query(value = '''Select s from Supplier s where
            (lower(s.supplierFullname) like lower (concat('%',:filter,'%')) or 
            lower(s.supplierCode) like lower (concat('%',:filter,'%'))) and s.isActive = true''',
			countQuery = '''Select count(s) from Supplier s where
            (lower(s.supplierFullname) like lower (concat('%',:filter,'%')) or 
            lower(s.supplierCode) like lower (concat('%',:filter,'%'))) and s.isActive = true''')
	Page<Supplier> findAllByFilterActivePageable(@Param("filter") String filter, Pageable page)
	
	@Query(value = '''Select s from Supplier s where s.isActive=true''')
	List<Supplier> SupplierActive()
	
	//validation query
	@Query(value = "Select s from Supplier s where upper(s.supplierCode) = upper(:supplierCode)")
	Supplier findOneBySupplierCode(@Param("supplierCode") String supplierCode)
	
	@Query(value = "Select s from Supplier s where upper(s.supplierFullname) = upper(:supplierFullname)")
	Supplier findOneBySupplierName(@Param("supplierFullname") String supplierFullname)
	//end validation query

	@Query(value = '''Select s from Supplier s where
			s.employeeId is not null AND
            (lower(s.supplierFullname) like lower (concat('%',:filter,'%')) or 
            lower(s.supplierCode) like lower (concat('%',:filter,'%')))''',
			countQuery = '''Select count(s) from Supplier s where
			s.employeeId is not null AND
            (lower(s.supplierFullname) like lower (concat('%',:filter,'%')) or 
            lower(s.supplierCode) like lower (concat('%',:filter,'%')))''')
	Page<Supplier> findDoctorPersonalAccountByFilterPageable(@Param("filter") String filter, Pageable page)

	//validation query
	@Query(value = "Select s from Supplier s where s.investorId = :investorId")
	Supplier findByInvestorId(@Param("investorId") UUID investorId)

	@Query(value = '''Select s from Supplier s where
			s.supplierTypes.supplierTypeDesc = :type and
            (lower(s.supplierFullname) like lower (concat('%',:filter,'%')) or 
            lower(s.supplierCode) like lower (concat('%',:filter,'%'))) and s.isActive = true''',
			countQuery = '''Select count(s) from Supplier s where
			s.supplierTypes.supplierTypeDesc = :type and
            (lower(s.supplierFullname) like lower (concat('%',:filter,'%')) or 
            lower(s.supplierCode) like lower (concat('%',:filter,'%'))) and s.isActive = true''')
	Page<Supplier> findAllByFilterActiveByTypePageable(@Param("filter") String filter,@Param("type") String type, Pageable page)
}
