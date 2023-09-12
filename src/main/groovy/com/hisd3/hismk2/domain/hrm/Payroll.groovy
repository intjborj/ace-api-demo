package com.hisd3.hismk2.domain.hrm

import com.fasterxml.jackson.annotation.JsonIgnore
import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.hrm.enums.PayrollStatus
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant

@Entity
@javax.persistence.Table(schema = "hrm", name = "payrolls")
class Payroll extends AbstractAuditingEntity implements Serializable {


    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "name", columnDefinition = "varchar")
    String name

    @GraphQLQuery
    @Column(name = "generated", columnDefinition = "bool")
    Boolean generated

    @GraphQLQuery
    @Column(name = "date_start", columnDefinition = "timestamp")
    Instant dateStart

    @GraphQLQuery
    @Column(name = "date_end", columnDefinition = "timestamp")
    Instant dateEnd

    @GraphQLQuery
    @Column(name = "locked_date", columnDefinition = "timestamp")
    Instant lockedDate

    @GraphQLQuery
    @Column(name = "note", columnDefinition = "varchar")
    String note

    @GraphQLQuery
    @Column(name = "status", columnDefinition = "varchar")
    @Enumerated(EnumType.STRING)
    PayrollStatus status

//    @GraphQLQuery
//    @Column(name = "salary_rate_multiplier", columnDefinition = "varchar")
//    SalaryRateMultiplier salaryRateMultiplier

    @GraphQLQuery
    @OneToMany(mappedBy = "payroll", fetch = FetchType.LAZY)
    List<Payslip> payslip = []

    @GraphQLQuery
    @OneToMany(mappedBy = "payroll", fetch = FetchType.LAZY)
    List<LogFlag> logFlags = []

}
