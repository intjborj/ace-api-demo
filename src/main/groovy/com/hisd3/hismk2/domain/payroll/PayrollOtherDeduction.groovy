package com.hisd3.hismk2.domain.payroll


import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.payroll.enums.TimekeepingStatus
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.*

@Entity
@Table(schema = "payroll", name = "other_deductions")
@SQLDelete(sql = "UPDATE payroll.other_deductions SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class PayrollOtherDeduction extends AbstractAuditingEntity implements Serializable {


    @Id
    @Column(name = "payroll", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @OneToMany(mappedBy = "otherDeduction", orphanRemoval = true, cascade = CascadeType.ALL)
    List<OtherDeductionEmployee> employees = []

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "varchar")
    TimekeepingStatus status

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payroll", referencedColumnName = "id")
    @MapsId
    Payroll payroll


}
