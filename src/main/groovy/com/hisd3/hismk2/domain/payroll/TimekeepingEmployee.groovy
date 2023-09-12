package com.hisd3.hismk2.domain.payroll

import com.fasterxml.jackson.annotation.JsonIgnore
import com.hisd3.hismk2.domain.payroll.common.PayrollEmployeeAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
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
@Table(schema = "payroll", name = "timekeeping_employees")
class TimekeepingEmployee extends PayrollEmployeeAuditingEntity implements Serializable {

    @GraphQLQuery
    @Id
    @Column(name = "employee", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @OneToMany(mappedBy = "timekeepingEmployee", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<AccumulatedLogSummary> accumulatedLogSummaryList = []

    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "timekeeping", referencedColumnName = "payroll")
    Timekeeping timekeeping

}
