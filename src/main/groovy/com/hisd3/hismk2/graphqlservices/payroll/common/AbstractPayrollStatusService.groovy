package com.hisd3.hismk2.graphqlservices.payroll.common


import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.payroll.common.PayrollAuditingEntity
import com.hisd3.hismk2.domain.payroll.enums.PayrollStatus
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.security.SecurityUtils

import java.time.Instant

abstract class AbstractPayrollStatusService<T extends PayrollAuditingEntity> extends AbstractDaoService<T> {

    EmployeeRepository employeeRepository

    AbstractPayrollStatusService(Class<T> classType, EmployeeRepository employeeRepository) {
        super(classType)
        this.employeeRepository = employeeRepository
    }

    T updateStatus(UUID id, PayrollStatus status) {
        T entity = findOne(id)
        entity.status = status
        if (entity.status == PayrollStatus.FINALIZED) {
            Employee employee = null

            employeeRepository.findOneByUsername(SecurityUtils.currentLogin()).ifPresent { employee = it }
            if (employee == null) throw new RuntimeException("No approver found.")

            entity.finalizedBy = employee
            entity.finalizedDate = Instant.now()
        } else {
            entity.finalizedBy = null
            entity.finalizedDate = null
        }

        return save(entity)
    }

}
