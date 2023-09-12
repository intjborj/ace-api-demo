package com.hisd3.hismk2.domain.doh

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "hosp_quality_management", schema = "doh")
class GenInfoQualityManagements extends AbstractAuditingEntity {
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "quality_mgmttype", columnDefinition = "numeric")
    Integer qualityMgmttype

    @GraphQLQuery
    @Column(name = "description", columnDefinition = "varchar")
    String description

    @GraphQLQuery
    @Column(name = "certifying_body", columnDefinition = "varchar")
    String certifyingBody

    @GraphQLQuery
    @Column(name = "phil_health_accredition", columnDefinition = "numeric")
    Integer philHealthAccredition

    @GraphQLQuery
    @Column(name = "validity_from", columnDefinition = "timestamp")
    Instant validityFrom

    @GraphQLQuery
    @Column(name = "validity_to", columnDefinition = "timestamp")
    Instant validityTo

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
