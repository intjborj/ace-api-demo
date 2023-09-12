package com.hisd3.hismk2.domain.doh

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "number_deliveries", schema = "doh")
class DischargeNumberDeliveries  extends AbstractAuditingEntity{
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "total_if_delivery", columnDefinition = "numeric")
    Integer totalIfDelivery

    @GraphQLQuery
    @Column(name = "total_bv_delivery", columnDefinition = "numeric")
    Integer totalBvDelivery

    @GraphQLQuery
    @Column(name = "total_lbc_delivery", columnDefinition = "numeric")
    Integer totalLbcDelivery

    @GraphQLQuery
    @Column(name = "total_other_delivery", columnDefinition = "numeric")
    Integer totalOtherDelivery

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