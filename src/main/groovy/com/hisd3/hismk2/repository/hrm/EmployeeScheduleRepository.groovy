package com.hisd3.hismk2.repository.hrm

import com.hisd3.hismk2.domain.hrm.EmployeeSchedule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import java.time.Instant

interface EmployeeScheduleRepository extends JpaRepository<EmployeeSchedule, UUID> {
	
	@Query(
			value = "Select e from EmployeeSchedule e where e.employee.id = :id"
	)
	List<EmployeeSchedule> findByEmployeeID(@Param("id") UUID id)


	@Query(value = """
		Select es from EmployeeSchedule es 
		left join fetch es.employee e
		where e.id = :id
		and es.dateTimeStartRaw <= :date 
		and es.dateTimeEndRaw >= :date
	""")
	List<EmployeeSchedule> findBetweenAndEqualSchedule(@Param("id")UUID id, @Param("date") Instant date)

	@Query(value = """
		Select es from EmployeeSchedule es 
		left join fetch es.employee e
		where e.id = :id
		and es.dateTimeStartRaw < :date 
		and es.dateTimeEndRaw > :date
	""")
	List<EmployeeSchedule> findBetweenSchedule(@Param("id")UUID id, @Param("date") Instant date)

}
