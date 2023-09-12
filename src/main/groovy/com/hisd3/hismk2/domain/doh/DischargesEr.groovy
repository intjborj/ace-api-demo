package com.hisd3.hismk2.domain.doh

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant
import java.time.LocalDate


@Entity
@Table(name = "discharge_er", schema = "doh")
class DischargesEr extends AbstractAuditingEntity{
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "er_consultations", columnDefinition = "varchar")
    String erConsultations

    @GraphQLQuery
    @Column(name = "number", columnDefinition = "numeric")
    Integer number

    @GraphQLQuery
    @Column(name = "icd_10_code", columnDefinition = "varchar")
    String icd10Code

    @GraphQLQuery
    @Column(name = "icd_10_category", columnDefinition = "varchar")
    String icd10Category

    @GraphQLQuery
    @Column(name = "reporting_year", columnDefinition = "numeric")
    Integer reportingYear

    @GraphQLQuery
    @Column(name = "submitted_date_time", columnDefinition = "timestamp")
    Instant submittedDateTime

}
