package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.FlowRate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FlowRateRepository extends JpaRepository<FlowRate, UUID> {
	
	@Query(value = "Select flowRate from FlowRate flowRate where lower(flowRate.description) like lower(concat('%',:filter,'%'))")
	List<FlowRate> filterAllO2FlowRates(@Param("filter") String filter)
}
