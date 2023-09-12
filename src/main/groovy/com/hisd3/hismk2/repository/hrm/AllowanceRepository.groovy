package com.hisd3.hismk2.repository.hrm

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import com.hisd3.hismk2.domain.hrm.Allowance
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AllowanceRepository extends  JpaRepository<Allowance, UUID> {

    @Query(value = "select c from Allowance c")
    List<Allowance> findAllAllowance()

    @Query(value = "select c from Allowance c WHERE (lower(c.name || c.payFrequency) like lower(concat('%',:search,'%'))) ")
    List<Allowance> findAllLikeAllowance(@Param("search") String search)

    @Query(value = "select c from Allowance c where (lower(c.name) like lower(concat('%',:filter,'%')))",
            countQuery = "Select count(c) from Allowance c where (lower(c.name) like lower(concat('%',:filter,'%')))")
    Page<Allowance> getAllowances(@Param("filter") String filter, Pageable pageable)



}