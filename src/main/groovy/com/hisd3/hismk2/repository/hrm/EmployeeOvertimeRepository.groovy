package com.hisd3.hismk2.repository.hrm

import com.hisd3.hismk2.domain.hrm.EmployeeOvertime
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface EmployeeOvertimeRepository extends JpaRepository<EmployeeOvertime, UUID> {
	
	@Query(
			value = "Select e from EmployeeOvertime e where e.employee.id = :id"
	)
	List<EmployeeOvertime> findByEmployeeID(@Param("id") UUID id)
	
	@Query(
			value = "Select e from EmployeeOvertime e where e.payslip = :id"
	)
	List<EmployeeOvertime> findByPayslip(@Param("id") UUID id)
	
}
