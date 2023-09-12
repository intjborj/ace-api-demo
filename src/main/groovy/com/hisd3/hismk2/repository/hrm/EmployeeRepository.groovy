package com.hisd3.hismk2.repository.hrm

import com.hisd3.hismk2.domain.User
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.hrm.dto.EmployeeDepartmentSalaryDto
import com.hisd3.hismk2.rest.dto.EmployeeDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    Optional<Employee> findByIsActiveTrueAndExcludePayrollFalseAndId(UUID id)

    List<Employee> findByIsActiveTrueAndExcludePayrollFalse()

    @Query(value = "Select e from Employee e left join e.user where e.id = :id")
    Optional<Employee> findEmployeeById(@Param("id") UUID id)

    @Query(
            value = "Select e from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%'))",
            countQuery = "Select count(e) from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%'))"
    )
    Page<Employee> getEmployees(@Param("filter") String filter, Pageable pageable)

    @Query(value = "Select e from Employee e where e.id in :id")
    List<Employee> getEmployees(@Param("id") List<UUID> id)

    @Query(
            value = "Select e from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%'))"
    )
    List<Employee> searchEmployees(@Param("filter") String filter)

    @Query(
            value = "Select e from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.isAllowedCoManage = true",
            countQuery = "Select count(e) from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.isAllowedCoManage = true"
    )
    List<Employee> searchEmployeesByAllowedCoManage(@Param("filter") String filter)

    @Query(
            value = "Select e from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.supplierId is null",
            countQuery = "Select count(e) from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.supplierId is null"
    )
    List<Employee> searchEmployeesBySup(@Param("filter") String filter)

    @Query(
            value = "Select e from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) ",
            countQuery = "Select count(e) from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%'))"
    )
    Page<Employee> searchEmployees(@Param("filter") String filter, Pageable pageable)

    @Query(
            value = "Select e from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.department.id = :department ",
            countQuery = "Select count(e) from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.department.id = :department"
    )
    Page<Employee> searchEmployeesAndDepartment(@Param("filter") String filter, @Param('department') UUID department, Pageable pageable)

    @Query(
            value = "Select e from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.department.id = :department order by e.fullName "
    )
    List<Employee> searchEmployeesAndDepartment(@Param("filter") String filter, @Param('department') UUID department)

    @Query(
            value = "Select e from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.isActive IS NOT NULL and e.isActive = true ",
            countQuery = "Select count(e) from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.isActive IS NOT NULL and e.isActive = true"
    )
    Page<Employee> searchActiveEmployeesPageable(@Param("filter") String filter, Pageable pageable)

    @Query(
            value = "Select e from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.isActive IS NOT NULL and e.isActive = true order by e.fullName "
    )
    List<Employee> searchActiveEmployees(@Param("filter") String filter)

    @Query(
            value = "Select e from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.department.id = :department and e.isActive IS NOT NULL and e.isActive = true",
            countQuery = "Select count(e) from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.department.id = :department and e.isActive IS NOT NULL and e.isActive = true"
    )
    Page<Employee> searchActiveEmployeesAndDepartmentPageable(@Param("filter") String filter, @Param('department') UUID department, Pageable pageable)

    @Query(
            value = "Select e from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.department.id = :department and e.isActive IS NOT NULL and e.isActive = true order by e.fullName "
    )
    List<Employee> searchActiveEmployeesAndDepartment(@Param("filter") String filter, @Param('department') UUID department)

    @Query(
            value = "Select e from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.department.id = :department and e.isActive = false",
            countQuery = "Select count(e) from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.department.id = :department and e.isActive = false"
    )
    Page<Employee> searchInActiveEmployeesAndDepartment(@Param("filter") String filter, @Param('department') UUID department, Pageable pageable)

    @Query(value = "Select e from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.department.id = :department and e.isActive = false")
    List<Employee> searchInActiveEmployeesAndDepartment(@Param("filter") String filter, @Param('department') UUID department)

    @Query(
            value = "Select e from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.isActive IS NOT NULL and e.isActive = true and e.excludePayroll = FALSE order by e.fullName "
    )
    List<Employee> searchActiveAndIncldInPayrollEmps(@Param("filter") String filter)

    @Query(
            value = "Select e from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.isActive = true and (e.excludePayroll = FALSE or e.excludePayroll is NULL)",
            countQuery = "Select count(e) from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.isActive = true and (e.excludePayroll = FALSE or e.excludePayroll is NULL)"
    )
    Page<Employee> searchActiveAndIncldInPayrollEmps(@Param("filter") String filter, Pageable pageable)

    @Query(
            value = "Select e from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.isActive IS NOT NULL and e.isActive = true and e.excludePayroll = FALSE",
            countQuery = "Select count(e.id) from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.isActive IS NOT NULL and e.isActive = true and e.excludePayroll = FALSE"
    )
    Page<Employee> searchInActiveEmployees(@Param("filter") String filter, Pageable pageable)

    @Query(value = "Select e from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.isActive IS NOT NULL and e.isActive = true and e.excludePayroll = FALSE")
    List<Employee> searchInActiveEmployees(@Param("filter") String filter)

    @Query(
            value = "Select e from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.department.id = :department and e.isActive IS NOT NULL and e.isActive = true and e.excludePayroll = FALSE order by e.fullName "
    )
    List<Employee> searchActiveAndIncldInPayrollEmpsAndDept(@Param("filter") String filter, @Param('department') UUID department)

    @Query(
            value = "Select e from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.department.id = :department and e.isActive = true and (e.excludePayroll = FALSE or e.excludePayroll is NULL)",
            countQuery = "Select count(e) from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.department.id = :department and e.isActive = true and (e.excludePayroll = FALSE or e.excludePayroll is NULL)"
    )
    Page<Employee> searchActiveAndIncldInPayrollEmpsAndDept(@Param("filter") String filter, @Param('department') UUID department, Pageable pageable)

    @Query(
            value = "Select e from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.excludePayroll = FALSE and (e.isActive IS NULL or e.isActive = TRUE or e.isActive = FALSE) order by e.fullName "
    )
    List<Employee> searchIncludedPayrollOrAnyActiveEmployees(@Param("filter") String filter)

    @Query(
            value = "Select e from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.department.id = :department and e.excludePayroll = TRUE",
            countQuery = "Select count(e) from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.department.id = :department and e.excludePayroll = TRUE"
    )
    Page<Employee> searchExcldInPayrollEmpAndDept(@Param("filter") String filter, @Param('department') UUID department, Pageable pageable)


    @Query(value = "Select e from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.department.id = :department and e.excludePayroll = TRUE")
    List<Employee> searchExcldInPayrollEmpAndDept(@Param("filter") String filter, @Param('department') UUID department)

    @Query(
            value = "Select e from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and (e.excludePayroll = TRUE or e.isActive = FALSE)",
            countQuery = "Select count(e) from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and (e.excludePayroll = TRUE or e.isActive = FALSE)"
    )
    Page<Employee> searchExcludedInPayrollEmployees(@Param("filter") String filter, Pageable pageable)

    @Query(value = "Select e from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and (e.excludePayroll = TRUE or e.isActive = FALSE)")
    List<Employee> searchExcludedInPayrollEmployees(@Param("filter") String filter)

    @Query(
            value = "Select e from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.department.id = :department and e.excludePayroll = FALSE and (e.isActive IS NULL or e.isActive = TRUE or e.isActive = FALSE) order by e.fullName "
    )
    List<Employee> searchIncludedInPayrollOrAnyActiveEmployeesAndDepartment(@Param("filter") String filter, @Param('department') UUID department)

    @Query(
            value = "Select e from Employee e where e.user.login = :username"
    )
    List<Employee> findByUsername(@Param("username") String username)

    @Query(
            value = "Select e from Employee e where e.user.login = :username"
    )
    Optional<Employee> findOneByUsername(@Param("username") String username)

    @Query(
            value = """
                Select new com.hisd3.hismk2.rest.dto.EmployeeDto(e.id, e.fullName, d.id, d.departmentName) 
                from Employee e 
                left join e.department d 
                where e.employeeId = :id"""
    )
    EmployeeDto findByEmployeeId(@Param("id") String id)

    @Query(
            value = "Select e from Employee e where e.departmentOfDuty.id = :id or e.department.id = :id"
    )
    List<Employee> findEmployeesByDepartment(@Param("id") UUID id)

    @Query(
            value = "Select e from Employee e where e.fullName = :fullName"
    )
    List<Employee> getEmployeeByFullName(@Param("fullName") String fullName)

    Employee findOneByUser(@Param("user") User user)

    @Query(
            value = """
				Select new com.hisd3.hismk2.domain.hrm.dto.EmployeeDepartmentSalaryDto(
					e.id, e.fullName,d.departmentName, e.payFreq, e.basicSalary, e.scheduleType, e.contributionPagIbig, e.employeeNo) 
				from Employee e 
					join e.department d 
				where lower(e.fullName) like lower(concat('%',:filter,'%')) 
			""",
            countQuery = "Select count(e) from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%'))"
    )
    Page<EmployeeDepartmentSalaryDto> searchEmployeesSalaryPageable(@Param("filter") String filter, Pageable pageable)

    @Query(
            value = """
				Select new com.hisd3.hismk2.domain.hrm.dto.EmployeeDepartmentSalaryDto(
					e.id, e.fullName,d.departmentName, e.payFreq, e.basicSalary, e.scheduleType, e.contributionPagIbig, e.employeeNo) 
				from Employee e 
					join e.department d 
				where 
					lower(e.fullName) like lower(concat('%',:filter,'%')) 
					and d.id = :department
			""",
            countQuery = "Select count(e) from Employee e where lower(e.fullName) like lower(concat('%',:filter,'%')) and e.department.id = :department"
    )
    Page<EmployeeDepartmentSalaryDto> searchEmployeesSalaryAndDepartmentPageable(@Param("filter") String filter, @Param('department') UUID department, Pageable pageable)

    @Query(
            value = "Select e from Employee e where e.id in (:ids) and lower(e.fullName) like lower(concat('%',:fullName,'%')) and e.isActive = true",
            countQuery = "Select count(e) from Employee e where e.id in (:ids) and lower(e.fullName) like lower(concat('%',:fullName,'%')) and e.isActive = true"
    )
    Page<Employee> findInIdsByFullName(@Param("fullName") String fullName, @Param("ids") ArrayList<UUID> ids, Pageable pageable)

    @Query(
            value = "Select e from Employee e where e.id in (:ids) and lower(e.fullName) like lower(concat('%',:fullName,'%')) and e.isActive = true and e.department.id = :department",
            countQuery = "Select count(e) from Employee e where e.id in (:ids) and lower(e.fullName) like lower(concat('%',:fullName,'%')) and e.isActive = true and e.department.id = :department"
    )
    Page<Employee> findInIdsByByFullNameAndDepartment(@Param("fullName") String fullName, @Param('department') UUID department, @Param("ids") ArrayList<UUID> ids, Pageable pageable)

    @Query("Select DISTINCT e from Employee e left join fetch e.otherDeductions where e in (:employees)")
    List<Employee> getEmployeesWithOtherDeduction(@Param("employees")List<Employee> employees)
}