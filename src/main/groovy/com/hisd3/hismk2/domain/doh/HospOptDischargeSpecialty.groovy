package com.hisd3.hismk2.domain.doh

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "discharge_specialty", schema = "doh")
class HospOptDischargeSpecialty extends AbstractAuditingEntity {
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "type_of_service", columnDefinition = "numeric")
    Integer typeOfService

    @GraphQLQuery
    @Column(name = "no_patient", columnDefinition = "numeric")
    Integer noPatient

    @GraphQLQuery
    @Column(name = "total_length_stay", columnDefinition = "numeric")
    Integer totalLengthStay

    @GraphQLQuery
    @Column(name = "non_philhealth_stay", columnDefinition = "numeric")
    Integer nonPhilHealthStay

    @GraphQLQuery
    @Column(name = "nhp_service_charity", columnDefinition = "numeric")
    Integer nhpServiceCharity

    @GraphQLQuery
    @Column(name = "total_non_philhealth", columnDefinition = "numeric")
    Integer totalNonPhilHealth

    @GraphQLQuery
    @Column(name = "philhealth_pay", columnDefinition = "numeric")
    Integer philHealthPay

    @GraphQLQuery
    @Column(name = "philhealth_service", columnDefinition = "numeric")
    Integer philHealthService

    @GraphQLQuery
    @Column(name = "total_philhealth", columnDefinition = "numeric")
    Integer totalPhilHealth

    @GraphQLQuery
    @Column(name = "hmo", columnDefinition = "numeric")
    Integer hmo

    @GraphQLQuery
    @Column(name = "owwa", columnDefinition = "numeric")
    Integer owwa

    @GraphQLQuery
    @Column(name = "recovered_improved", columnDefinition = "numeric")
    Integer recoveredImproved

    @GraphQLQuery
    @Column(name = "transferred", columnDefinition = "numeric")
    Integer transferred

    @GraphQLQuery
    @Column(name = "hama", columnDefinition = "numeric")
    Integer hama

    @GraphQLQuery
    @Column(name = "absconded", columnDefinition = "numeric")
    Integer absconded

    @GraphQLQuery
    @Column(name = "unimproved", columnDefinition = "numeric")
    Integer unImproved

    @GraphQLQuery
    @Column(name = "deaths_below_48_hours", columnDefinition = "numeric")
    Integer deathsBelow48Hours

    @GraphQLQuery
    @Column(name = "deaths_over_48", columnDefinition = "numeric")
    Integer deathsOver48

    @GraphQLQuery
    @Column(name = "total_deaths", columnDefinition = "numeric")
    Integer totalDeaths

    @GraphQLQuery
    @Column(name = "total_discharge", columnDefinition = "numeric")
    Integer totalDischarge

    @GraphQLQuery
    @Column(name = "remarks", columnDefinition = "varchar")
    String remarks

    @GraphQLQuery
    @Column(name = "reporting_year", columnDefinition = "numeric")
    Integer reportingYear

    @GraphQLQuery
    @Column(name = "submitted_date_time", columnDefinition = "timestamp")
    Instant submittedDateTime
}
