package com.hisd3.hismk2.domain.doh

import com.hisd3.hismk2.domain.AbstractAuditingEntity
import io.leangen.graphql.annotations.GraphQLQuery
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type

import javax.persistence.*
import java.time.Instant
import java.time.LocalDate


@Entity
@Table(name = "discharges_morbidity", schema = "doh")
class DischargeMobidity extends AbstractAuditingEntity {
    @GraphQLQuery
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid")
    @Type(type = "pg-uuid")
    UUID id

    @GraphQLQuery
    @Column(name = "icd_10_desc", columnDefinition = "varchar")
    String icd10Desc

    @GraphQLQuery
    @Column(name = "male_under_1", columnDefinition = "numeric")
    Integer maleUnder1

    @GraphQLQuery
    @Column(name = "female_under_1", columnDefinition = "numeric")
    Integer femaleUnder1

    @GraphQLQuery
    @Column(name = " Male_1_4", columnDefinition = "numeric")
    Integer male14

    @GraphQLQuery
    @Column(name = "female_1_4", columnDefinition = "numeric")
    Integer female14

    @GraphQLQuery
    @Column(name = "male_5_9", columnDefinition = "numeric")
    Integer male59

    @GraphQLQuery
    @Column(name = "female_5_9", columnDefinition = "numeric")
    Integer female59

    @GraphQLQuery
    @Column(name = "male_10_14", columnDefinition = "numeric")
    Integer male1014

    @GraphQLQuery
    @Column(name = "female_10_14", columnDefinition = "numeric")
    Integer female1014

    @GraphQLQuery
    @Column(name = "male_15_19", columnDefinition = "numeric")
    Integer male1519

    @GraphQLQuery
    @Column(name = "female_15_19", columnDefinition = "numeric")
    Integer female1519

    @GraphQLQuery
    @Column(name = "male_20_24", columnDefinition = "numeric")
    Integer male2024

    @GraphQLQuery
    @Column(name = "female_20_24", columnDefinition = "numeric")
    Integer female2024

    @GraphQLQuery
    @Column(name = "male_25_29", columnDefinition = "numeric")
    Integer male2529

    @GraphQLQuery
    @Column(name = "female_25_29", columnDefinition = "numeric")
    Integer female2529

    @GraphQLQuery
    @Column(name = "male_30_34", columnDefinition = "numeric")
    Integer male3034

    @GraphQLQuery
    @Column(name = "female_30_34", columnDefinition = "numeric")
    Integer female3034

    @GraphQLQuery
    @Column(name = "male_35_39", columnDefinition = "numeric")
    Integer male3539

    @GraphQLQuery
    @Column(name = "female_35_39", columnDefinition = "numeric")
    Integer female3539


    @GraphQLQuery
    @Column(name = "male_40_44", columnDefinition = "numeric")
    Integer male4044


    @GraphQLQuery
    @Column(name = "female_40_44", columnDefinition = "numeric")
    Integer female4044

    @GraphQLQuery
    @Column(name = "male_45_49", columnDefinition = "numeric")
    Integer male4549

    @GraphQLQuery
    @Column(name = "female_45_49", columnDefinition = "numeric")
    Integer female4549

    @GraphQLQuery
    @Column(name = "male_50_54", columnDefinition = "numeric")
    Integer male5054

    @GraphQLQuery
    @Column(name = "female_50_54", columnDefinition = "numeric")
    Integer female5054

    @GraphQLQuery
    @Column(name = "male_55_59", columnDefinition = "numeric")
    Integer male5559

    @GraphQLQuery
    @Column(name = "female_55_59", columnDefinition = "numeric")
    Integer female5559

    @GraphQLQuery
    @Column(name = "male_60_64", columnDefinition = "numeric")
    Integer male6064

    @GraphQLQuery
    @Column(name = "female_60_64", columnDefinition = "numeric")
    Integer female6064

    @GraphQLQuery
    @Column(name = "male_65_69", columnDefinition = "numeric")
    Integer male5669

    @GraphQLQuery
    @Column(name = "female_65_69", columnDefinition = "numeric")
    Integer female6569

    @GraphQLQuery
    @Column(name = "male_70_over", columnDefinition = "numeric")
    Integer male70Over

    @GraphQLQuery
    @Column(name = "female_70_over", columnDefinition = "numeric")
    Integer female70Over

    @GraphQLQuery
    @Column(name = "male_subtotal", columnDefinition = "numeric")
    Integer maleSubtotal

    @GraphQLQuery
    @Column(name = "female_subtotal", columnDefinition = "numeric")
    Integer femaleSubtotal

    @GraphQLQuery
    @Column(name = "grand_total", columnDefinition = "numeric")
    Integer grandTotal

    @GraphQLQuery
    @Column(name = "icd_10_code", columnDefinition = "varchar")
    String icd10Code

    @GraphQLQuery
    @Column(name = "diagnosis_category", columnDefinition = "varchar")
    String diagnosisCategory

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
