package com.hisd3.hismk2.repository.ancillary

import com.hisd3.hismk2.domain.ancillary.OrderSlipViewNew
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import java.time.Instant

interface OrderSlipViewNewRepositiry extends JpaRepository<OrderSlipViewNew, UUID> {
	
	@Query(value = ''' Select  osv from OrderSlipViewNew osv where  osv.parentCase.registryType like concat('%',:ptype,'%')
					   and lower(osv.patient.fullName) like lower(concat('%',:filter,'%'))
					   and osv.item.createdDate >= :startDate and osv.item.createdDate <= :endDate''',
			countQuery = ''' Select  osv from OrderSlipViewNew osv where  osv.parentCase.registryType like concat('%',:ptype,'%')
					   and lower(osv.patient.fullName) like lower(concat('%',:filter,'%'))
					   and osv.item.createdDate >= :startDate and osv.item.createdDate <= :endDate''')
	Page<OrderSlipViewNew> orderslipsViewByPatientType(
			@Param("ptype") String type,
			@Param("filter") String filter,
			@Param("startDate") Instant startDate,
			@Param("endDate") Instant endDate,
			Pageable pageable)
}
