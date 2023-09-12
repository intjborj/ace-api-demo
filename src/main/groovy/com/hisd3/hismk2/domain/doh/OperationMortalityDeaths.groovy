package com.hisd3.hismk2.domain.doh

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant
import java.time.LocalDate


@Entity
@Table(name = "mortality_death", schema = "doh")
class OperationMortalityDeaths extends AbstractAuditingEntity{
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "icd10desc", columnDefinition = "varchar")
    String icd10desc

    @GraphQLQuery
    @Column(name = "munder1", columnDefinition = "numeric")
    Integer munder1

    @GraphQLQuery
    @Column(name = "funder1", columnDefinition = "numeric")
    Integer funder1

    @GraphQLQuery
    @Column(name = "m1to4", columnDefinition = "numeric")
    Integer m1to4

    @GraphQLQuery
    @Column(name = "f1to4", columnDefinition = "numeric")
    Integer f1to4

    @GraphQLQuery
    @Column(name = "m5to9", columnDefinition = "numeric")
    Integer m5to9

    @GraphQLQuery
    @Column(name = "f5to9", columnDefinition = "numeric")
    Integer f5to9

    @GraphQLQuery
    @Column(name = "m10to14", columnDefinition = "numeric")
    Integer m10to14

    @GraphQLQuery
    @Column(name = "f10to14", columnDefinition = "numeric")
    Integer f10to14

    @GraphQLQuery
    @Column(name = "m15to19", columnDefinition = "numeric")
    Integer m15to19

    @GraphQLQuery
    @Column(name = "f15to19", columnDefinition = "numeric")
    Integer f15to19

    @GraphQLQuery
    @Column(name = "m20to24", columnDefinition = "numeric")
    Integer m20to24

    @GraphQLQuery
    @Column(name = "f20to24", columnDefinition = "numeric")
    Integer f20to24

    @GraphQLQuery
    @Column(name = "m25to29", columnDefinition = "numeric")
    Integer m25to29

    @GraphQLQuery
    @Column(name = "f25to29", columnDefinition = "numeric")
    Integer f25to29

    @GraphQLQuery
    @Column(name = "m30to34", columnDefinition = "numeric")
    Integer m30to34

    @GraphQLQuery
    @Column(name = "f30to34", columnDefinition = "numeric")
    Integer f30to34

    @GraphQLQuery
    @Column(name = "m35to39", columnDefinition = "numeric")
    Integer m35to39

    @GraphQLQuery
    @Column(name = "f35to39", columnDefinition = "numeric")
    Integer f35to39

    @GraphQLQuery
    @Column(name = "m40to44", columnDefinition = "numeric")
    Integer m40to44

    @GraphQLQuery
    @Column(name = "f40to44", columnDefinition = "numeric")
    Integer f40to44

    @GraphQLQuery
    @Column(name = "m45to49", columnDefinition = "numeric")
    Integer m45to49

    @GraphQLQuery
    @Column(name = "f45to49", columnDefinition = "numeric")
    Integer f45to49

    @GraphQLQuery
    @Column(name = "m50to54", columnDefinition = "numeric")
    Integer m50to54

    @GraphQLQuery
    @Column(name = "f50to54", columnDefinition = "numeric")
    Integer f50to54

    @GraphQLQuery
    @Column(name = "m55to59", columnDefinition = "numeric")
    Integer m55to59

    @GraphQLQuery
    @Column(name = "f55to59", columnDefinition = "numeric")
    Integer f55to59

    @GraphQLQuery
    @Column(name = "m60to64", columnDefinition = "numeric")
    Integer m60to64

    @GraphQLQuery
    @Column(name = "f60to64", columnDefinition = "numeric")
    Integer f60to64

    @GraphQLQuery
    @Column(name = "m65to69", columnDefinition = "numeric")
    Integer m65to69

    @GraphQLQuery
    @Column(name = "f65to69", columnDefinition = "numeric")
    Integer f65to69

    @GraphQLQuery
    @Column(name = "m70over", columnDefinition = "numeric")
    Integer m70over

    @GraphQLQuery
    @Column(name = "f70over", columnDefinition = "numeric")
    Integer f70over

    @GraphQLQuery
    @Column(name = "msubtotal", columnDefinition = "numeric")
    Integer msubTotal

    @GraphQLQuery
    @Column(name = "fsubtotal", columnDefinition = "numeric")
    Integer fsubTotal

    @GraphQLQuery
    @Column(name = "grandtotal", columnDefinition = "numeric")
    Integer grandTotal

    @GraphQLQuery
    @Column(name = "icd10code", columnDefinition = "varchar")
    String icd10code

    @GraphQLQuery
    @Column(name = "icd10category", columnDefinition = "varchar")
    String icd10category

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
