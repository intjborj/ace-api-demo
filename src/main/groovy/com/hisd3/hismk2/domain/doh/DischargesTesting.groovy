package com.hisd3.hismk2.domain.doh

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant
import java.time.LocalDate


@Entity
@Table(name = "discharge_testing", schema = "doh")
class DischargesTesting extends AbstractAuditingEntity{
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "testing_group", columnDefinition = "varchar")
    String testingGroup

    @GraphQLQuery
    @Column(name = "testing", columnDefinition = "varchar")
    String testing

    @GraphQLQuery
    @Column(name = "number", columnDefinition = "numeric")
    Integer number

    @GraphQLQuery
    @Column(name = "reporting_year", columnDefinition = "numeric")
    Integer reportingYear

    @GraphQLQuery
    @Column(name = "submitted_date_time", columnDefinition = "timestamp")
    Instant submittedDateTime

}
