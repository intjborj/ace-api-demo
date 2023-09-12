package com.hisd3.hismk2.repository.hrm

import com.hisd3.hismk2.domain.hrm.EmployeeLoan
import com.hisd3.hismk2.domain.hrm.Employee
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface EmployeeLoanRepository extends JpaRepository<EmployeeLoan, UUID> {
	
	@Query(value = "Select e from EmployeeLoan e where e.employee.id = :id")
	List<EmployeeLoan> findByEmployeeID(@Param("id") UUID id)

	@Query( value = "select c from EmployeeLoan c join c.employee e where e.id = :id ")
	List<EmployeeLoan>getEmployeeLoanById(@Param("id")UUID id)

	@Query(value = """
					Select c from EmployeeLoan c where (lower(c.employee.id || c.employee.fullName) like lower(concat('%',:search, '%')))
			""")
	List<EmployeeLoan>getEmployeeLoanByIdWithFilter(@Param("search") String search)

}
