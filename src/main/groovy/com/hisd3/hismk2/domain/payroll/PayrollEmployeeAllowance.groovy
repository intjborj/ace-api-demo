package com.hisd3.hismk2.domain.payroll

import com.hisd3.hismk2.domain.payroll.common.PayrollEmployeeAuditingEntity
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(schema = "payroll", name = "payroll_employee_allowances")
class PayrollEmployeeAllowance extends PayrollEmployeeAuditingEntity implements Serializable {

    @Id
    @Column(name = "employee", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @OneToMany(mappedBy = "payrollEmployeeAllowance", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<PayrollEmployeeAllowanceItem> allowanceItems = []

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payrollAllowance", referencedColumnName = "payroll")
    PayrollAllowance payrollAllowance
}
