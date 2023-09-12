package com.hisd3.hismk2.repository.inventory

import com.hisd3.hismk2.domain.inventory.AccountingCategory
import com.hisd3.hismk2.domain.inventory.PaymentTerm
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PaymentTermRepository extends JpaRepository<PaymentTerm, UUID> {
	
	@Query(value = '''Select s from PaymentTerm s where s.isActive=true''')
	List<PaymentTerm> paymentTermActive()
	
	@Query(value = '''Select s from PaymentTerm s where lower(s.paymentDesc) like lower(concat('%',:filter,'%'))''')
	List<PaymentTerm> paymentTermFilter(@Param("filter") String filter)

	@Query(value = '''Select s from PaymentTerm s where
					  lower(s.paymentDesc) like lower(concat('%',:filter,'%')) or  
					lower(s.paymentCode) like lower(concat('%',:filter,'%'))
			''',
			countQuery = '''
    Select count(s) from PaymentTerm s where
					  lower(s.paymentDesc) like lower(concat('%',:filter,'%')) or  
					lower(s.paymentCode) like lower(concat('%',:filter,'%'))
   ''')
	Page<PaymentTerm> paymentTermFilterPage(@Param("filter") String filter, Pageable pageable)
	
	//validation query
	@Query(value = "Select s from PaymentTerm s where upper(s.paymentCode) = upper(:paymentCode)")
	PaymentTerm findOneByPaymentTermCode(@Param("paymentCode") String paymentCode)
	
	@Query(value = "Select s from PaymentTerm s where upper(s.paymentDesc) = upper(:paymentDesc)")
	PaymentTerm findOneByPaymentTermName(@Param("paymentDesc") String paymentDesc)
	//end validation query
}
