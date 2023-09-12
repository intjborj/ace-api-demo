package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.Output
import org.javers.spring.annotation.JaversSpringDataAuditable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import java.time.Instant

@JaversSpringDataAuditable
interface OutputRepository extends JpaRepository<Output, UUID> {
	
	@Query(nativeQuery = true, value = 'Select * from pms.outputs output where output."case" = :parentCase and (deleted <> true or deleted is null) order by output.entry_datetime desc')
	List<Output> getOutputsByCase(@Param("parentCase") UUID parentCase)
	
	@Query(
			value = "Select o from Output o where o.parentCase.id = :parentCase order by o.entryDateTime desc",
			countQuery = "Select count(o) from Output o where o.parentCase.id = :parentCase"
	)
	Page<Output> getOutputsByCasePageable(
			@Param("parentCase") UUID parentCase, Pageable pageable
	)
	
	@Query(
			value = "Select o from Output o where o.parentCase.id = :parentCase and o.entryDateTime between :start and :end",
			countQuery = "Select count(o) from Output o where o.parentCase.id = :parentCase and o.entryDateTime between :start and :end"
	)
	Page<Output> getOutputsByCaseAndDatePageable(
			@Param("parentCase") UUID parentCase,
			@Param('start') Instant start,
			@Param('end') Instant end,
			Pageable pageable
	)
	
	@Query(
			value = "Select o from Output o where o.parentCase.id = :parentCase and o.entryDateTime between :start and :end",
			countQuery = "Select count(o) from Output o where o.parentCase.id = :parentCase and o.entryDateTime between :start and :end"
	)
	List<Output> getOutputsByCaseAndDate(
			@Param("parentCase") UUID parentCase,
			@Param('start') Instant start,
			@Param('end') Instant end
	)
	
	@Query(value = "Select output from Output output where output.parentCase.id = :parentCase and output.entryDateTime > :dateToday")
	List<Output> getOutputsToday(@Param("parentCase") UUID parentCase, @Param("dateToday") Instant dateToday)
	
	@Query(value = "Select output from Output output where output.parentCase.id = :parentCase and output.entryDateTime between :start and :end")
	List<Output> outputsWithin24hrs(@Param("parentCase") UUID parentCase, @Param("start") Instant start, @Param("end") Instant end)
	
	@Query(value = "Select output from Output output where output.parentCase.id = :parentCase and output.entryDateTime between :start and :end")
	List<Output> outputsByShifts(@Param("parentCase") UUID parentCase, @Param("start") Instant start, @Param("end") Instant end)
}
