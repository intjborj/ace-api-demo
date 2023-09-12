package com.hisd3.hismk2.repository.hrm

import com.hisd3.hismk2.domain.hrm.EmployeeOvetimeRequest
import org.springframework.data.jpa.repository.JpaRepository

interface EmployeeOvertimeRequestRepository extends JpaRepository<EmployeeOvetimeRequest, UUID> {

}
