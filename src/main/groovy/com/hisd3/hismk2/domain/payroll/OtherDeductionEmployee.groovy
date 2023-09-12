package com.hisd3.hismk2.domain.payroll

import com.fasterxml.jackson.annotation.JsonIgnore
import com.hisd3.hismk2.domain.payroll.common.PayrollEmployeeAuditingEntity
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(schema = "payroll", name = "other_deduction_employees")
class OtherDeductionEmployee extends PayrollEmployeeAuditingEntity implements Serializable {


    @Id
    @Column(name = "employee", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @OneToMany(mappedBy = "otherDeductionEmployee", orphanRemoval = true, cascade = CascadeType.ALL)
    List<OtherDeductionEmployeeItem> deductionItems = []

    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "other_deduction", referencedColumnName = "payroll")
    PayrollOtherDeduction otherDeduction

}
