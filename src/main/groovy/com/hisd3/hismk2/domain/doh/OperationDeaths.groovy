package com.hisd3.hismk2.domain.doh

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant
import java.time.LocalDate


@Entity
@Table(name = "operation_deaths", schema = "doh")
class OperationDeaths extends AbstractAuditingEntity{
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "total_death", columnDefinition = "numeric")
    Integer totalDeath

    @GraphQLQuery
    @Column(name = "total_death_48_down", columnDefinition = "numeric")
    Integer totalDeaths48Down

    @GraphQLQuery
    @Column(name = "total_death_48_up", columnDefinition = "numeric")
    Integer totalDeath48Up

    @GraphQLQuery
    @Column(name = "total_er_deaths", columnDefinition = "numeric")
    Integer totalErDeaths

    @GraphQLQuery
    @Column(name = "total_doa", columnDefinition = "numeric")
    Integer totalDoa

    @GraphQLQuery
    @Column(name = "total_still_birth", columnDefinition = "numeric")
    Integer totalStillBirth

    @GraphQLQuery
    @Column(name = "total_neonatal_death", columnDefinition = "numeric")
    Integer totalNeonatalDeath

    @GraphQLQuery
    @Column(name = "total_maternal_death", columnDefinition = "numeric")
    Integer totalMaterialDeath

    @GraphQLQuery
    @Column(name = "total_deaths_new_born", columnDefinition = "numeric")
    Integer totalDeathsNewBorn

    @GraphQLQuery
    @Column(name = "total_newborn_death", columnDefinition = "numeric")
    Integer totalNewBornDeath

    @GraphQLQuery
    @Column(name = "total_discharge_death", columnDefinition = "numeric")
    Integer totalDischargeDeath

    @GraphQLQuery
    @Column(name = "gross_death_rate", columnDefinition = "numeric")
    Integer grossDeathRate

    @GraphQLQuery
    @Column(name = "ndr_numerator", columnDefinition = "numeric")
    Integer ndrNumerator

    @GraphQLQuery
    @Column(name = "ndr_denominator", columnDefinition = "numeric")
    Integer ndrDenominator

    @GraphQLQuery
    @Column(name = "net_death_rate", columnDefinition = "decimal")
    Integer netDeathRate

    @GraphQLQuery
    @Column(name = "reporting_year", columnDefinition = "numeric")
    Integer reportingYear

    @GraphQLQuery
    @Column(name = "submitted_date_time", columnDefinition = "timestamp")
    Instant submittedDateTime


    @GraphQLQuery
    @Column(name = "doh_response", columnDefinition = "varchar")
    String dohResponse

}
