package com.hisd3.hismk2.repository.billing

import com.hisd3.hismk2.domain.billing.PriceTierDetail
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PriceTierDetailRepository extends JpaRepository<PriceTierDetail, UUID> {
	
	@Query(value = '''Select tier from PriceTierDetail tier
						where upper(tier.registryType) like upper(concat('%',:filter,'%'))''')
	List<PriceTierDetail> getPriceTierDetailsByFilter(@Param("filter") String filter)
	
	@Query(value = '''Select tier from PriceTierDetail tier where upper(tier.registryType) like upper(concat('%',:filter,'%')) and upper(tier.accommodationType) like upper(concat('%',:type,'%')) order by tier.tierCode''',
			countQuery = '''Select count(tier) from PriceTierDetail tier where upper(tier.registryType) like upper(concat('%',:filter,'%')) and upper(tier.accommodationType) like upper(concat('%',:type,'%')) order by tier.tierCode'''
	)
	Page<PriceTierDetail> getPriceTierDetailsByFilterPageable(@Param("filter") String filter, @Param("type") String type, Pageable pageable)
	
	@Query(value = "Select tier from PriceTierDetail tier where upper(tier.registryType) = :registryType and upper(tier.accommodationType) = :accommodationType")
	PriceTierDetail getTier(
			@Param("registryType") String registryType,
			@Param("accommodationType") String accommodationType
	)
	
	//findById
	@Query(value = "Select tier from PriceTierDetail tier where tier.id = :id")
	PriceTierDetail getPriceTierDetailsById(@Param("id") UUID id)
	
	@Query(value = '''Select tier from PriceTierDetail tier
						where (upper(tier.registryType) = :registryType and upper(tier.accommodationType) = :accommodationType)
						and (upper(tier.roomTypes) like upper(concat('%',:roomType,'%')) and tier.department.id = :department)
						and tier.forSenior = :forSenior
						''')
	List<PriceTierDetail> getTiers(
			@Param("registryType") String registryType,
			@Param("accommodationType") String accommodationType,
			@Param("department") UUID department,
			@Param("roomType") String roomType,
			@Param("forSenior") Boolean forSenior
	)
	
	@Query(value = '''Select tier from PriceTierDetail tier
						where (upper(tier.registryType) = :registryType and upper(tier.accommodationType) = :accommodationType)
						and (upper(tier.roomTypes) like upper(concat('%',:roomType,'%')) and tier.forSenior = :forSenior)
						''')
	List<PriceTierDetail> getTiers(
			@Param("registryType") String registryType,
			@Param("accommodationType") String accommodationType,
			@Param("roomType") String roomType,
			@Param("forSenior") Boolean forSenior
	)
	
	@Query(value = '''Select tier from PriceTierDetail tier
						where (upper(tier.registryType) = :registryType and upper(tier.accommodationType) = :accommodationType)
						and upper(tier.roomTypes) like upper(concat('%',:roomType,'%'))
						''')
	List<PriceTierDetail> getTiersDisregardSenior(
			@Param("registryType") String registryType,
			@Param("accommodationType") String accommodationType,
			@Param("roomType") String roomType
	)
	
	@Query(value = '''Select tier from PriceTierDetail tier
						where (upper(tier.registryType) = :registryType and upper(tier.accommodationType) = :accommodationType)
						''')
	List<PriceTierDetail> getTiersDisregardSenior(
			@Param("registryType") String registryType,
			@Param("accommodationType") String accommodationType
	)
	
	@Query(value = '''Select tier from PriceTierDetail tier
						where (upper(tier.registryType) = :registryType and upper(tier.accommodationType) = :accommodationType)
						and tier.forSenior = :forSenior
						''')
	List<PriceTierDetail> getTiers(
			@Param("registryType") String registryType,
			@Param("accommodationType") String accommodationType,
			@Param("forSenior") Boolean forSenior
	)
	
	@Query(value = '''Select tier from PriceTierDetail tier
						where (upper(tier.registryType) = :registryType and upper(tier.accommodationType) = :accommodationType)
						and (tier.department.id = :department and tier.forSenior = :forSenior)
						''')
	List<PriceTierDetail> getTiers(
			@Param("registryType") String registryType,
			@Param("accommodationType") String accommodationType,
			@Param("department") UUID department,
			@Param("forSenior") Boolean forSenior
	)
}
