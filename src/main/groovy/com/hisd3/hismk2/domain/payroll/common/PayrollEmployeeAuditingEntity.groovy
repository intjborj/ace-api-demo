package com.hisd3.hismk2.domain.payroll.common

import com.fasterxml.jackson.annotation.JsonIgnore
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.payroll.PayrollEmployee
import com.hisd3.hismk2.domain.payroll.enums.PayrollEmployeeStatus
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction

import javax.persistence.Column
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MappedSuperclass
import javax.persistence.MapsId
import javax.persistence.OneToOne
import java.time.Instant

@MappedSuperclass
class PayrollEmployeeAuditingEntity extends PayrollStatusEntity<PayrollEmployeeStatus> implements Serializable {

    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee", referencedColumnName = "id")
    @MapsId
    PayrollEmployee payrollEmployee

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "approved_by", referencedColumnName = "id")
    Employee approvedBy

    @Column(name = "approved_date", columnDefinition = "timestamp")
    Instant approvedDate

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "finalized_by", referencedColumnName = "id")
    Employee finalizedBy

    @Column(name = "finalized_date", columnDefinition = "timestamp")
    Instant finalizedDate

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rejected_by", referencedColumnName = "id")
    Employee rejectedBy

    @Column(name = "rejected_date", columnDefinition = "timestamp")
    Instant rejectedDate

}