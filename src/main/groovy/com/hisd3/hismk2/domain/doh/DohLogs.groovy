package com.hisd3.hismk2.domain.doh

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant

enum DOH_REPORT_TYPE{
    SUMMARY_PATIENT,
    DELIVERIES,
    BED_CAPACITY,
    DISCHARGES_OPD,
    DISCHARGES_ER,
    DISCHARGE_OPV,
    EXPENSES,
    REVENUES,
    DISCHARGES_MORBIDITY,
    DISCHARGES_MORTALITY,
    TESTING,
    DEATHS,
    DISCHARGES_EV,
    DISCHARGES_SPECIALTY,
    DISCHARGES_SPECIALTY_OTHERS,
    STAFFING_PATTERN,
    STAFFING_PATTERN_OTHERS,
    MAJOR_OPERATIONS,
    MINOR_OPERATIONS,
    INFECTIONS,
    GEN_INFO_CLASSIFICATION,
    GEN_INFO_QUALITY_MANAGEMENT
}


@Entity
@Table(name = "doh_logs", schema = "doh")
class DohLogs extends AbstractAuditingEntity implements Serializable{
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "type", columnDefinition = "varchar")
    String type

    @GraphQLQuery
    @Column(name = "submitted_report", columnDefinition = "text")
    String submittedReport

    @GraphQLQuery
    @Column(name = "report_response", columnDefinition = "text")
    String reportResponse

    @GraphQLQuery
    @Column(name = "reporting_year", columnDefinition = "int")
    Integer reportingYear

    @GraphQLQuery
    @Column(name = "status", columnDefinition = "varchar")
    String status

}
