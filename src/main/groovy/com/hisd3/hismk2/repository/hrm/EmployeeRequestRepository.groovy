package com.hisd3.hismk2.repository.hrm

import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.hrm.EmployeeRequest
import com.hisd3.hismk2.domain.hrm.enums.EmployeeRequestDateType
import com.hisd3.hismk2.domain.hrm.enums.EmployeeRequestStatus
import com.hisd3.hismk2.domain.hrm.enums.EmployeeRequestType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import java.time.Instant

interface EmployeeRequestRepository extends JpaRepository<EmployeeRequest, UUID> {


    @Query(
            value = """
                Select r from EmployeeRequest r
                    left join fetch r.requestedBy rb
                    left join fetch r.hrApprovedBy hab
                    left join fetch r.department d
                where r.type = :type and r.status in :status
            """,
            countQuery = "Select count(r) from EmployeeRequest r where r.type = :type and r.status in :status"
    )
    Page<EmployeeRequest> findEmployeeRequests(
            @Param("type") EmployeeRequestType type,
            @Param("status") List<EmployeeRequestStatus> status,
            Pageable pageable
    )

    @Query(
            value = """
                Select r from EmployeeRequest r
                    left join fetch r.requestedBy rb
                    left join fetch r.hrApprovedBy hab
                    left join fetch r.department d
                where r.type = :type and rb.id = :id and r.status in :status
            """,
            countQuery = "Select count(r) from EmployeeRequest r where r.type = :type and r.requestedBy.id = id and r.status in :status"
    )
    Page<EmployeeRequest> findEmployeeRequestsByRequestedEmployee(
            @Param("id") UUID id,
            @Param("status") List<EmployeeRequestStatus> status,
            @Param("type") EmployeeRequestType type,
            Pageable pageable
    )

    @Query(
            value = """
                Select r from EmployeeRequest r
                    left join fetch r.requestedBy rb
                    left join fetch r.hrApprovedBy hab
                    left join fetch r.department d
                where r.type = :type and hab.id = :id and r.status in :status
            """,
            countQuery = "Select count(r) from EmployeeRequest r where r.type = :type and r.hrApprovedBy.id = id  and r.status in :status"
    )
    Page<EmployeeRequest> findEmployeeRequestsByHrApprovedEmployee(
            @Param("id") UUID id,
            @Param("status") List<EmployeeRequestStatus> status,
            @Param("type") EmployeeRequestType type,
            Pageable pageable
    )

}
