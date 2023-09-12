package com.hisd3.hismk2.repository.billing

import com.hisd3.hismk2.domain.billing.PriceTierModifier
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PriceTierModifierRepository extends JpaRepository<PriceTierModifier, UUID> {
	@Query(
			value = '''Select tm from PriceTierModifier tm where 
				(
					upper(tm.priceTierDetail.tierCode) like upper(concat('%',:filter,'%')) or 
					upper(tm.priceTierDetail.description) like upper(concat('%',:filter,'%'))
				) and 
				upper(tm.categoryType) like upper(concat('%',:type,'%'))''',
			countQuery = '''Select count(tm) from PriceTierModifier tm where 
				(
					upper(tm.priceTierDetail.tierCode) like upper(concat('%',:filter,'%')) or 
					upper(tm.priceTierDetail.description) like upper(concat('%',:filter,'%'))
				) and 
				upper(tm.categoryType) like upper(concat('%',:type,'%'))'''
	)
	Page<PriceTierModifier> getPriceTierModifiers(@Param("filter") String filter, @Param("type") String type, Pageable pageable)
	
	@Query(
			value = '''Select tm from PriceTierModifier tm where (:cost between tm.fromCost and tm.toCost) and (tm.categoryType = :type and tm.priceTierDetail.id = :tier)'''
	)
	List<PriceTierModifier> getPriceTierModifierByCost(@Param("cost") BigDecimal cost, @Param("type") String type, @Param("tier") UUID tierId)

	@Query(value = '''Select tm from PriceTierModifier tm where tm.priceTierDetail.id = :tier''')
	List<PriceTierModifier> getPriceTierModifierByTier(@Param("tier") UUID tierId)
}
