package com.hisd3.hismk2.repository.hrm

import org.springframework.data.jpa.repository.JpaRepository
import com.hisd3.hismk2.domain.hrm.AddOn
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AddOnRepository extends JpaRepository<AddOn, UUID> {

    @Query( value = "select c from AddOn c ")
    List<AddOn>getAllAddOn()

    @Query( value = "select c from AddOn c join c.employee e where e.id = :id ")
    List<AddOn>getEmployeeAddOns(@Param("id")UUID id)

}