package com.hisd3.hismk2.repository.hrm

import com.hisd3.hismk2.domain.hrm.AllowanceTemplate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AllowanceTemplatesRepository extends JpaRepository<AllowanceTemplate, UUID> {

    @Query(value = "select c from AllowanceTemplate c where (c.name like concat('%',:filter,'%'))",
            countQuery = "Select count(c) from AllowanceTemplate c where (c.name like concat('%',:filter,'%'))")
    Page<AllowanceTemplate> getAllowanceTemplates(@Param("filter") String filter, Pageable pageable)

    @Query(value = "select c from AllowanceTemplate c where Coalesce(c.active, TRUE) = TRUE and c.total > 0",
            countQuery = "Select count(c) from AllowanceTemplate c where Coalesce(c.active, TRUE) = TRUE and c.total > 0")
    List<AllowanceTemplate> findActiveWithTotal()
}