package com.hisd3.hismk2.domain.payroll

import com.hisd3.hismk2.domain.payroll.common.PayrollEmployeeAuditingEntity
import org.hibernate.annotations.Type

import javax.persistence.*

@Entity
@Table(schema = "payroll", name = "payroll_employee_contributions")
class PayrollEmployeeContribution extends PayrollEmployeeAuditingEntity{


    @Id
    @Column(name = "employee", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payrollContribution", referencedColumnName = "payroll")
    PayrollContribution payrollContribution

    @Column(name = "sss_ee", columnDefinition = "numeric")
    BigDecimal sssEE

    @Column(name = "sss_er", columnDefinition = "numeric")
    BigDecimal sssEr

    @Column(name = "phic_ee", columnDefinition = "numeric")
    BigDecimal phicEE

    @Column(name = "phic_er", columnDefinition = "numeric")
    BigDecimal phicER

    @Column(name = "hdmf_er", columnDefinition = "numeric")
    BigDecimal hdmfER

    @Column(name = "hdmf_ee", columnDefinition = "numeric")
    BigDecimal hdmfEE


}
