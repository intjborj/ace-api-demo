package com.hisd3.hismk2.domain.payroll.common

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.hrm.Employee
import com.hisd3.hismk2.domain.payroll.enums.PayrollApprovalStatus
import io.leangen.graphql.annotations.GraphQLQuery

import javax.persistence.*
import java.time.Instant

@MappedSuperclass
class PayrollStatusEntity<T> extends AbstractAuditingEntity implements Serializable {
    @GraphQLQuery
    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "varchar")
    T status

}