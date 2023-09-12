package com.hisd3.hismk2.graphqlservices.payroll.common

import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.payroll.PayrollEmployeeContribution
import com.hisd3.hismk2.domain.payroll.common.PayrollEmployeeAuditingEntity
import com.hisd3.hismk2.domain.payroll.enums.PayrollEmployeeStatus
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.payroll.enums.PayrollModule
import com.hisd3.hismk2.graphqlservices.types.GraphQLResVal
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.security.SecurityUtils

import java.time.Instant

abstract class AbstractPayrollEmployeeStatusService<T extends PayrollEmployeeAuditingEntity> extends AbstractDaoService<T> {

    EmployeeRepository employeeRepository

    AbstractPayrollEmployeeStatusService(Class<T> classType, EmployeeRepository employeeRepository) {
        super(classType)
        this.employeeRepository = employeeRepository
    }

    abstract PayrollModule getPayrollModule()
    abstract GraphQLResVal<T> updateEmployeeStatus(UUID id, PayrollEmployeeStatus status)

    T updateStatus(UUID id, PayrollEmployeeStatus status) {
        T entity = findOne(id)
        if(entity == null) return null

        entity.status = status
        if (entity.status == PayrollEmployeeStatus.APPROVED || entity.status == PayrollEmployeeStatus.REJECTED || entity.status == PayrollEmployeeStatus.FINALIZED) {
            Employee employee = null

            employeeRepository.findOneByUsername(SecurityUtils.currentLogin()).ifPresent { employee = it }
            if (employee == null) throw new RuntimeException("No approver found.")
            entity.status = status

            if (status == PayrollEmployeeStatus.APPROVED) {
                entity.approvedBy = employee
                entity.approvedDate = Instant.now()
            } else if (status == PayrollEmployeeStatus.REJECTED) {
                entity.rejectedBy = employee
                entity.rejectedDate = Instant.now()
            }else if (status == PayrollEmployeeStatus.FINALIZED ){
                entity.finalizedBy = employee
                entity.finalizedDate = Instant.now()
            }

            // clearing "xxxxBy" and "xxxxDate" to null depending on the new status
            if(status == PayrollEmployeeStatus.REJECTED || status == PayrollEmployeeStatus.FINALIZED|| status == PayrollEmployeeStatus.DRAFT ){
                entity.approvedBy = null
                entity.approvedDate = null
            }
            if(status == PayrollEmployeeStatus.APPROVED || status == PayrollEmployeeStatus.FINALIZED|| status == PayrollEmployeeStatus.DRAFT ) {
                entity.rejectedBy = null
                entity.rejectedDate = null
            }
            if(status == PayrollEmployeeStatus.DRAFT){
                entity.finalizedBy = null
                entity.finalizedDate = null
            }
        }

        return save(entity)
    }

}
