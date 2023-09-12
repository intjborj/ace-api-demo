package com.hisd3.hismk2.repository.hrm

import com.hisd3.hismk2.domain.hrm.EmployeeRequestApproval
import com.hisd3.hismk2.domain.hrm.enums.EmployeeRequestApprovalStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface EmployeeRequestApprovalRepository extends JpaRepository<EmployeeRequestApproval, UUID> {

    @Query(value = """
          Select a from EmployeeRequestApproval a
          left join fetch a.request r
          where r.id = :request and a.status in :status
    """)
    List<EmployeeRequestApproval> findEmployeeRequestApproval(@Param("request")UUID request, @Param("status")List<EmployeeRequestApprovalStatus> status)

}