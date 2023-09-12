package com.hisd3.hismk2.domain.doh

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant
import java.time.LocalDate


@Entity
@Table(name = "discharge_ev", schema = "doh")
class DischargeEv extends AbstractAuditingEntity{
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "emergency_visits", columnDefinition = "numeric")
    Integer emergencyVisits

    @GraphQLQuery
    @Column(name = "emergency_visits_adult", columnDefinition = "numeric")
    Integer emergencyVisitsAdult

    @GraphQLQuery
    @Column(name = "emergency_visits_pediatric", columnDefinition = "numeric")
    Integer emergencyVisitsPediatric

    @GraphQLQuery
    @Column(name = "ev_from_facility_to_another", columnDefinition = "numeric")
    Integer evFromFacilityToAnother

    @GraphQLQuery
    @Column(name = "reporting_year", columnDefinition = "numeric")
    Integer reportingYear

    @GraphQLQuery
    @Column(name = "submitted_date_time", columnDefinition = "timestamp")
    Instant submittedDateTime

}
