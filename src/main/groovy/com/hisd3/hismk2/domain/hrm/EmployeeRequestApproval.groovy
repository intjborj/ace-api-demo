package com.hisd3.hismk2.domain.hrm

import com.fasterxml.jackson.annotation.JsonIgnore
import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.billing.PriceTierDetail
import com.hisd3.hismk2.domain.hrm.enums.EmployeeRequestApprovalStatus
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.ResultCheckStyle
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.*
import java.time.Instant

@Entity
@Table(schema = "hrm", name = "employee_request_approval")
@SQLDelete(sql = "UPDATE hrm.employee_request_approval SET deleted = true WHERE id = ? ", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted <> true or deleted is null")
class EmployeeRequestApproval extends AbstractAuditingEntity {


    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee", referencedColumnName = "id")
    Employee employee

    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request", referencedColumnName = "id")
    EmployeeRequest request

    @GraphQLQuery
    @Column(name = "remarks", columnDefinition = "varchar")
    String remarks

    @GraphQLQuery
    @Column(name = "approved_date", columnDefinition = "timestamp")
    Instant approvedDate

    @GraphQLQuery
    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "varchar")
    EmployeeRequestApprovalStatus status

}
