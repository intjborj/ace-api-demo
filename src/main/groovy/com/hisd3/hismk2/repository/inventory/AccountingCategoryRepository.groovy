package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.AccountingCategory
import com.hisd3.hismk2.domain.inventory.Generic
import com.hisd3.hismk2.domain.inventory.Item
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AccountingCategoryRepository extends JpaRepository<AccountingCategory, UUID> {

	@Query(value = "Select ac from AccountingCategory ac where ac.isActive = true")
	List<AccountingCategory> activeAccountingCategories()

	@Query(value = '''Select ac from AccountingCategory ac where 
					lower(ac.categoryCode) like lower(concat('%',:filter,'%')) or  
					lower(ac.categoryDescription) like lower(concat('%',:filter,'%'))''')
	List<AccountingCategory> filterAccountingCategories(@Param("filter") String filter)

	@Query(value = '''Select ac from AccountingCategory ac where
					  lower(ac.categoryCode) like lower(concat('%',:filter,'%')) or  
					lower(ac.categoryDescription) like lower(concat('%',:filter,'%'))
			''',
			countQuery = '''
    Select count(ac) from AccountingCategory ac where
					  lower(ac.categoryCode) like lower(concat('%',:filter,'%')) or  
					lower(ac.categoryDescription) like lower(concat('%',:filter,'%'))
   ''')
	Page<AccountingCategory> accountingCategoryPage(@Param("filter") String filter, Pageable pageable)


	//validation query
	@Query(value = "Select s from AccountingCategory s where upper(s.categoryCode) = upper(:code)")
	AccountingCategory findOneByAcctCategoryCode(@Param("code") String code)

	@Query(value = "Select s from AccountingCategory s where upper(s.categoryDescription) = upper(:description)")
	AccountingCategory findOneByAcctCategoryName(@Param("description") String description)
	//end validation query


}
