package com.hisd3.hismk2.domain.hrm

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.hrm.enums.EmployeeRequestDateType
import com.hisd3.hismk2.domain.hrm.enums.EmployeeRequestScheduleType
import com.hisd3.hismk2.domain.hrm.enums.EmployeeRequestStatus
import com.hisd3.hismk2.domain.hrm.enums.EmployeeRequestType
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Type
import org.hibernate.annotations.Where

import javax.persistence.*
import javax.validation.constraints.NotBlank
import java.time.Instant

@Canonical
class SelectedDate{
    Instant startDatetime
    Instant endDatetime
    Double hours
    String scheduleType
}

@Entity
@Table(schema = "hrm", name = "employee_request")
@SQLDelete(sql = "UPDATE hrm.employee_request SET deleted = true WHERE id = ?")
@Where(clause = "deleted <> true or deleted is  null ")
class EmployeeRequest extends AbstractAuditingEntity {

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "`requested_by`", referencedColumnName = "id")
    Employee requestedBy

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "`hr_approved_by`", referencedColumnName = "id")
    Employee hrApprovedBy

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "`reverted_by`", referencedColumnName = "id")
    Employee revertedBy

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "request", orphanRemoval = true, cascade = CascadeType.ALL)
    List<EmployeeSchedule> schedules

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "request", orphanRemoval = true, cascade = CascadeType.ALL)
    List<EmployeeRequestApproval> approvals

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department", referencedColumnName = "id")
    Department department

    @GraphQLQuery
    @Column(name = "status", columnDefinition = "varchar", nullable = false)
    @Enumerated(value = EnumType.STRING)
    EmployeeRequestStatus status = EmployeeRequestStatus.PENDING

    @GraphQLQuery
    @Column(name = "reason", columnDefinition = "varchar", nullable = false)
    @NotBlank(message = "Reason is required")
    String reason

    @GraphQLQuery
    @Column(name = "remarks", columnDefinition = "varchar", nullable = false)
    String remarks

    @GraphQLQuery
    @Column(name = "approved_date", columnDefinition = "timestamp")
    Instant approvedDate

    @GraphQLQuery
    @Column(name = "hr_approved_date", columnDefinition = "timestamp")
    Instant hrApprovedDate

    @GraphQLQuery
    @Column(name = "requested_date", columnDefinition = "timestamp")
    Instant requestedDate

    @GraphQLQuery
    @Column(name = "reverted_date", columnDefinition = "timestamp")
    Instant revertedDate

    @GraphQLQuery
    @Column(name = "with_pay", columnDefinition = "boolean default false", nullable = false)
    Boolean withPay

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type", columnDefinition = "varchar(50)", nullable = false)
    EmployeeRequestType type

    @GraphQLQuery
    @Type(type = "jsonb")
    @Column(name="dates",columnDefinition = "jsonb")
    List<SelectedDate> dates

    @GraphQLQuery
    @Column(name="dates_type",columnDefinition = "varchar")
//    @Enumerated(EnumType.STRING)
    String datesType


}
