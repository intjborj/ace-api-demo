package com.hisd3.hismk2.domain.doh

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant
import java.time.LocalDate


@Entity
@Table(name = "staffing_pattern", schema = "doh")
class StaffingPattern extends AbstractAuditingEntity{
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "profession_designation", columnDefinition = "numeric")
    Integer professionDesignation

    @GraphQLQuery
    @Column(name = "specialty_board_certified", columnDefinition = "numeric")
    Integer specialtyBoardCertified

    @GraphQLQuery
    @Column(name = "fulltime_40permanent", columnDefinition = "numeric")
    Integer fullTime40Permanent

    @GraphQLQuery
    @Column(name = "fulltime_40contractual", columnDefinition = "numeric")
    Integer fulltime40Contractual

    @GraphQLQuery
    @Column(name = "parttime_permanent", columnDefinition = "numeric")
    Integer partTimePermanent

    @GraphQLQuery
    @Column(name = "parttime_contractual", columnDefinition = "numeric")
    Integer partTimeContractual

    @GraphQLQuery
    @Column(name = "active_rotating_affiliate", columnDefinition = "numeric")
    Integer activeRotatingAffiliate

    @GraphQLQuery
    @Column(name = "out_soured", columnDefinition = "numeric")
    Integer outSourced

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
