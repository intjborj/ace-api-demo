package com.hisd3.hismk2.domain.pms

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import com.hisd3.hismk2.domain.hrm.Employee
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.*
import org.javers.core.metamodel.annotation.DiffIgnore
import org.javers.core.metamodel.annotation.ShallowReference

import javax.persistence.*
import java.time.Instant

@javax.persistence.Entity
@javax.persistence.Table(schema = "pms", name = "intakes")
@SQLDelete(sql = "UPDATE pms.intakes SET deleted = true WHERE id=? ")
@Where(clause = "deleted <> true or deleted is null ")
class Intake extends AbstractAuditingEntity {

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @DiffIgnore
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "`case`", referencedColumnName = "id")
    Case parentCase

    @GraphQLQuery
    @Column(name = "po_intake", columnDefinition = "varchar")
    Float poIntake

    @GraphQLQuery
    @Column(name = "tube_intake", columnDefinition = "varchar")
    Float tubeIntake

    @GraphQLQuery
    @Column(name = "ivf_intake", columnDefinition = "varchar")
    Float ivfIntake

    @GraphQLQuery
    @Column(name = "blood_intake", columnDefinition = "varchar")
    Float bloodIntake

    @GraphQLQuery
    @Column(name = "tpn_intake", columnDefinition = "varchar")
    Float tpnIntake

    @GraphQLQuery
    @Column(name = "pb_intake", columnDefinition = "varchar")
    Float pbIntake

    @GraphQLQuery
    @Column(name = "medication_intake", columnDefinition = "varchar")
    Float medicationIntake

    @GraphQLQuery
    @Column(name = "remarks", columnDefinition = "text")
    String remarks

    @GraphQLQuery
    @Column(name = "others", columnDefinition = "text")
    String others

    @GraphQLQuery
    @Column(name = "multiple_medication", columnDefinition = "varchar")
    String multiMedication

    @GraphQLQuery
    @Column(name = "multiple_ivf", columnDefinition = "varchar")
    String multiIVF

    @GraphQLQuery
    @Transient
    Float total = 0

    @GraphQLQuery
    @Column(name = "entry_datetime", columnDefinition = "timestamp")
    Instant entryDateTime

    @ShallowReference
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee", referencedColumnName = "id")
    Employee employee
}
