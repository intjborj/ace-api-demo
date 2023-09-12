package com.hisd3.hismk2.repository.hrm

import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.hrm.OtherDeduction
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface OtherDeductionRepository extends JpaRepository<OtherDeduction, UUID> {


    @Query(value = "select od from OtherDeduction od")
    List<OtherDeduction> findAllOtherDeduction()

    @Query(value = "select od from OtherDeduction od left join fetch od.employees e where od.id = :id and coalesce(e.isActive, TRUE) is TRUE and coalesce(e.excludePayroll,FALSE) is FALSE")
    Optional<OtherDeduction> findOneOtherDeductionWithEmployees(@Param("id") UUID id)

    @Query(value = "select od from OtherDeduction od left join fetch od.employees e where e.id = :employeeId and coalesce(e.isActive, TRUE) is TRUE and coalesce(e.excludePayroll,FALSE) is FALSE")
    List<OtherDeduction> findOtherDeductionByEmployees(@Param("employeeId") UUID employeeId)

    @Query(value = "select od from OtherDeduction od WHERE lower(od.title) like lower(concat('%',:search,'%')) ")
    List<OtherDeduction> findAllLikeOtherDeduction(@Param("search") String search)

    @Query(value = "select e.id from OtherDeduction od left join od.employees e WHERE od.id = :id")
    List<UUID> findOtherDeductionEmployeeId(@Param("id") UUID id)

    @Query(value = "select e from OtherDeduction od inner join od.employees e WHERE od.id = :id and lower(e.fullName) like lower(concat('%',:filter,'%')) and coalesce(e.isActive, TRUE) is TRUE and coalesce(e.excludePayroll,FALSE) is FALSE")
    Page<Employee> findOneOtherDeductionEmployees(@Param("id") UUID id, @Param("filter") String filter, Pageable pageable)

    @Query(value = "select e from OtherDeduction od inner join od.employees e WHERE od.id = :id and coalesce(e.isActive, TRUE) is TRUE and coalesce(e.excludePayroll,FALSE) is FALSE")
    List<Employee> findOneOtherDeductionEmployees(@Param("id") UUID id)

    @Query(value = "select od from OtherDeduction od where (lower(od.title) like lower(concat('%',:filter,'%')))",
            countQuery = "Select count(od) from OtherDeduction od where (lower(od.title) like lower(concat('%',:filter,'%')))")
    Page<OtherDeduction> getOtherDeductions(@Param("filter") String filter, Pageable pageable)

}
