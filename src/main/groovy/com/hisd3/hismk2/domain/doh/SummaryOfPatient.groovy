package com.hisd3.hismk2.domain.doh

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "summary_of_patient", schema = "doh")
class SummaryOfPatient extends AbstractAuditingEntity {
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "total_inpatients", columnDefinition = "numeric")
    Integer totalInpatients

    @GraphQLQuery
    @Column(name = "total_newborn", columnDefinition = "numeric")
    Integer totalNewborn

    @GraphQLQuery
    @Column(name = "total_discharges", columnDefinition = "numeric")
    Integer totalDischarges

    @GraphQLQuery
    @Column(name = "total_pad", columnDefinition = "numeric")
    Integer totalPad

    @GraphQLQuery
    @Column(name = "total_ibd", columnDefinition = "numeric")
    Integer totalIbd

    @GraphQLQuery
    @Column(name = "total_inpatient_transto", columnDefinition = "numeric")
    Integer totalInpatientTransto

    @GraphQLQuery
    @Column(name = "total_inpatient_transfrom", columnDefinition = "numeric")
    Integer totalInpatientTransFrom

    @GraphQLQuery
    @Column(name = "total_patient_remaining", columnDefinition = "numeric")
    Integer totalPatientRemaining

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
