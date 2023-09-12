package com.hisd3.hismk2.domain.payroll.common

import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.payroll.enums.PayrollEmployeeStatus
import com.hisd3.hismk2.domain.payroll.enums.PayrollStatus
import io.leangen.graphql.annotations.GraphQLQuery

import javax.persistence.*
import java.time.Instant

@MappedSuperclass
class PayrollAuditingEntity extends PayrollStatusEntity<PayrollStatus> implements Serializable {

    @GraphQLQuery
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "finalized_by", referencedColumnName = "id")
    Employee finalizedBy

    @GraphQLQuery
    @Column(name = "finalized_date", columnDefinition = "timestamp")
    Instant finalizedDate

}