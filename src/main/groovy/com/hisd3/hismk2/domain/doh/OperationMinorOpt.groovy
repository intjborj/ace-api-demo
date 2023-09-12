package com.hisd3.hismk2.domain.doh

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant
import java.time.LocalDate


@Entity
@Table(name = "operation_minor_opt", schema = "doh")
class OperationMinorOpt extends AbstractAuditingEntity{
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "operation_code", columnDefinition = "varchar")
    String operationCode

    @GraphQLQuery
    @Column(name = "surgical_operation", columnDefinition = "varchar")
    String surgicalOperation

    @GraphQLQuery
    @Column(name = "number", columnDefinition = "numeric")
    Integer number

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
