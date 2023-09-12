package com.hisd3.hismk2.repository.pms


import com.hisd3.hismk2.domain.pms.PatientCaseView
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PatientCaseViewRepository extends JpaRepository<PatientCaseView, UUID> {

    @Query(value = """ Select p from PatientCaseView p where p.primaryphysician.id = :physician or p.comanagingPhysician.id = :physician """,
            countQuery = """Select count(p) from PatientCaseView p  where p.primaryphysician.id = :physician or p.comanagingPhysician.id = :physician"""
    )
    Page<PatientCaseView> getAllPatientPageable(@Param("physician") UUID physician, Pageable pageable)

    @Query(
            value = """Select d from PatientCaseView d where d.status = 'ACTIVE'""",
            countQuery = """Select count(d) from PatientCaseView d where d.status = 'ACTIVE'"""
    )
    Page<PatientCaseView> getAllPatientByDepartment(@Param("department") UUID department, Pageable pageable)

}
