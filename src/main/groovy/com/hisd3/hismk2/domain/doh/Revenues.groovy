package com.hisd3.hismk2.domain.doh

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant
import java.time.LocalDate


@Entity
@Table(name = "revenues", schema = "doh")
class Revenues extends AbstractAuditingEntity{
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "amountfromdoh", columnDefinition = "numeric")
    Double amountFromDoh

    @GraphQLQuery
    @Column(name = "amountfromlgu", columnDefinition = "numeric")
    Double amountFromLgu

    @GraphQLQuery
    @Column(name = "amountfromdonor", columnDefinition = "numeric")
    Double amountFromDonor

    @GraphQLQuery
    @Column(name = "amountfromprivateorg", columnDefinition = "numeric")
    Double amountFromPrivateOrg

    @GraphQLQuery
    @Column(name = "amountfromphilhealth", columnDefinition = "numeric")
    Double amountFromPhilHealth

    @GraphQLQuery
    @Column(name = "amountfrompatient", columnDefinition = "numeric")
    Double amountFromPatient

    @GraphQLQuery
    @Column(name = "amountfromreimbursement", columnDefinition = "numeric")
    Double amountFromReimbursement

    @GraphQLQuery
    @Column(name = "amountfromothersources", columnDefinition = "numeric")
    Double amountFromOtherSources

    @GraphQLQuery
    @Column(name = "grandtotal", columnDefinition = "numeric")
    Double grandTotal

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
