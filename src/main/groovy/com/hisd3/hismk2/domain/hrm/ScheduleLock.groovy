package com.hisd3.hismk2.domain.hrm

import com.fasterxml.jackson.annotation.JsonIgnore
import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant

@Entity
@javax.persistence.Table(schema = "hrm", name = "schedule_locks")
class ScheduleLock extends AbstractAuditingEntity {


    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "date", columnDefinition = "timestamp")
    Instant date

    @GraphQLQuery
    @Column(name = "is_locked", columnDefinition = "bool")
    Boolean isLocked

}
