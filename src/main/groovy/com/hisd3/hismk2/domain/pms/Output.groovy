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
@javax.persistence.Table(schema = "pms", name = "outputs")
@SQLDelete(sql = "UPDATE pms.outputs SET deleted = true WHERE id = ? ")
@Where(clause = "deleted <> true or deleted is null ")
class Output extends AbstractAuditingEntity {

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
    @Column(name = "voided_output", columnDefinition = "varchar")
    Float voidedOutput

    @GraphQLQuery
    @Column(name = "catheter_output", columnDefinition = "varchar")
    Float catheterOutput

    @GraphQLQuery
    @Column(name = "ng_output", columnDefinition = "varchar")
    Float ngOutput

    @GraphQLQuery
    @Column(name = "insensible_loss_output", columnDefinition = "varchar")
    Float insensibleLossOutput

    @GraphQLQuery
    @Column(name = "stool_output", columnDefinition = "varchar")
    Integer stoolOutput

    @GraphQLQuery
    @Column(name = "emesis_output", columnDefinition = "varchar")
    Float emesisOutput

    @GraphQLQuery
    @Column(name = "blood_loss", columnDefinition = "varchar")
    Float bloodLoss

    @GraphQLQuery
    @Column(name = "remarks", columnDefinition = "text")
    String remarks

    @GraphQLQuery
    @Column(name = "drainage", columnDefinition = "text")
    String drainage

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
