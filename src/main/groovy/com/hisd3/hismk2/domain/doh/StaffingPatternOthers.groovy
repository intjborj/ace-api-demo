package com.hisd3.hismk2.domain.doh

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant
import java.time.LocalDate


@Entity
@Table(name = "staffing_pattern_others", schema = "doh")
class StaffingPatternOthers extends AbstractAuditingEntity{
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "parent", columnDefinition = "numeric")
    Integer parent

    @GraphQLQuery
    @Column(name = "professiondesignation", columnDefinition = "varchar")
    String professionDesignation

    @GraphQLQuery
    @Column(name = "specialtyboardcertified", columnDefinition = "numeric")
    Integer specialtyBoardCertified

    @GraphQLQuery
    @Column(name = "fulltime40permanent", columnDefinition = "numeric")
    Integer fulltime40Permanent

    @GraphQLQuery
    @Column(name = "fulltime40contractual", columnDefinition = "numeric")
    Integer fulltime40Contractual

    @GraphQLQuery
    @Column(name = "parttimepermanent", columnDefinition = "numeric")
    Integer parttimePermanent

    @GraphQLQuery
    @Column(name = "parttimecontractual", columnDefinition = "numeric")
    Integer parttimeContractual

    @GraphQLQuery
    @Column(name = "activerotatingaffiliate", columnDefinition = "numeric")
    Integer activeRotatingAffiliate

    @GraphQLQuery
    @Column(name = "outsoured", columnDefinition = "numeric")
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
