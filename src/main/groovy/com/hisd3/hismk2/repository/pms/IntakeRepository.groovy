package com.hisd3.hismk2.repository.pms

import com.hisd3.hismk2.domain.pms.Intake
import org.javers.spring.annotation.JaversSpringDataAuditable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import java.time.Instant

@JaversSpringDataAuditable
interface IntakeRepository extends JpaRepository<Intake, UUID> {
	
	@Query(nativeQuery = true, value = 'Select * from pms.intakes intake where intake."case" = :parentCase and (deleted <> true or deleted is null) order by intake.entry_datetime desc')
	List<Intake> getIntakesByCase(@Param("parentCase") UUID parentCase)
	
	@Query(
			value = "Select i from Intake i where i.parentCase.id = :parentCase order by i.entryDateTime desc",
			countQuery = "Select count(i) from Intake i where i.parentCase.id = :parentCase"
	)
	Page<Intake> getIntakesByCasePageable(@Param("parentCase") UUID parentCase, Pageable pageable)
	
	@Query(
			value = "Select i from Intake i where i.parentCase.id = :parentCase and i.entryDateTime between :start and :end",
			countQuery = "Select count(i) from Intake i where i.parentCase.id = :parentCase and i.entryDateTime between :start and :end"
	)
	Page<Intake> getIntakesByCaseAndDatePageable(@Param("parentCase") UUID parentCase, @Param('start') Instant start, @Param('end') Instant end, Pageable pageable)
	
	@Query(
			value = "Select i from Intake i where i.parentCase.id = :parentCase and i.entryDateTime between :start and :end",
			countQuery = "Select count(i) from Intake i where i.parentCase.id = :parentCase and i.entryDateTime between :start and :end"
	)
	List<Intake> getIntakesByCaseAndDate(@Param("parentCase") UUID parentCase, @Param('start') Instant start, @Param('end') Instant end)
	
	@Query(value = "Select intake from Intake intake where intake.parentCase.id = :parentCase and intake.entryDateTime > :dateToday")
	List<Intake> getIntakesToday(@Param("parentCase") UUID parentCase, @Param("dateToday") Instant dateToday)
	
	@Query(value = "Select intake from Intake intake where intake.parentCase.id = :parentCase and intake.entryDateTime between :start and :end")
	List<Intake> intakesWithin24hrs(@Param("parentCase") UUID parentCase, @Param("start") Instant start, @Param("end") Instant end)
	
	@Query(value = "Select intake from Intake intake where intake.parentCase.id = :parentCase and intake.entryDateTime between :start and :end")
	List<Intake> intakesByShifts(@Param("parentCase") UUID parentCase, @Param("start") Instant start, @Param("end") Instant end)
}
