package com.hisd3.hismk2.domain.pms

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant

@Entity
@Table(schema = "pms", name = "central_line_records")
class CentralLineRecord extends AbstractAuditingEntity {

    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "`case`", referencedColumnName = "id")
    Case aCase

    @GraphQLQuery
    @Column(name = "start_date", columnDefinition = "timestamp")
    Instant startDate

    @GraphQLQuery
    @Column(name = "end_date", columnDefinition = "timestamp")
    Instant endDate

    @GraphQLQuery
    @Column(name = "sensitivity", columnDefinition = "varchar")
    String sensitivity

    @GraphQLQuery
    @Column(name = "remarks", columnDefinition = "varchar")
    String remarks
}
