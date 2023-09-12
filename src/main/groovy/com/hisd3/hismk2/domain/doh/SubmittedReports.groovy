package com.hisd3.hismk2.domain.doh

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant
import java.time.LocalDate


@Entity
@Table(name = "submitted_reports", schema = "doh")
class SubmittedReports extends AbstractAuditingEntity{
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "reporting_year", columnDefinition = "numeric")
    Integer reportingYear

    @GraphQLQuery
    @Column(name = "reporting_status", columnDefinition = "varchar")
    String reportingStatus

    @GraphQLQuery
    @Column(name = "reported_by", columnDefinition = "varchar")
    String reportedBy

    @GraphQLQuery
    @Column(name = "designation", columnDefinition = "varchar")
    String designation

    @GraphQLQuery
    @Column(name = "sections", columnDefinition = "varchar")
    String sections

    @GraphQLQuery
    @Column(name = "department", columnDefinition = "varchar")
    String department

    @GraphQLQuery
    @Column(name = "submitted_date_time", columnDefinition = "timestamp")
    Instant submittedDateTime

    @GraphQLQuery
    @Column(name = "doh_response", columnDefinition = "varchar")
    String dohResponse

}
