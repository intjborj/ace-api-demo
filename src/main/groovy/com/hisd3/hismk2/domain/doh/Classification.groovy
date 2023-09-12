package com.hisd3.hismk2.domain.doh

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant
import java.time.LocalDate


@Entity
@Table(name = "classifications", schema = "doh")
class Classification extends AbstractAuditingEntity {
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "service_capability", columnDefinition = "numeric")
    Integer serviceCapability

    @GraphQLQuery
    @Column(name = "general", columnDefinition = "numeric")
    Integer general

    @GraphQLQuery
    @Column(name = "specialty", columnDefinition = "numeric")
    Integer specialty

    @GraphQLQuery
    @Column(name = "specialty_specify", columnDefinition = "varchar")
    String specialtySpecify

    @GraphQLQuery
    @Column(name = "trauma_capability", columnDefinition = "numeric")
    Integer traumaCapability

    @GraphQLQuery
    @Column(name = "nature_of_ownership", columnDefinition = "numeric")
    Integer natureOfOwnership

    @GraphQLQuery
    @Column(name = "government", columnDefinition = "numeric")
    Integer government

    @GraphQLQuery
    @Column(name = "nationals", columnDefinition = "numeric")
    Integer nationals

    @GraphQLQuery
    @Column(name = "locals", columnDefinition = "numeric")
    Integer locals

    @GraphQLQuery
    @Column(name = "privates", columnDefinition = "numeric")
    Integer privates

    @GraphQLQuery
    @Column(name = "reporting_year", columnDefinition = "numeric")
    Integer reportingYear

    @GraphQLQuery
    @Column(name = "ownership_others", columnDefinition = "varchar")
    String ownershipOthers

    @GraphQLQuery
    @Column(name = "submitted_date_time", columnDefinition = "timestamp")
    Instant submittedDateTime
}


